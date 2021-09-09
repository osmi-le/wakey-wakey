/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.ike.ikev2.message;

import android.annotation.Nullable;

import com.android.ike.ikev2.IkeDhParams;
import com.android.ike.ikev2.SaProposal;
import com.android.ike.ikev2.exceptions.IkeException;
import com.android.ike.ikev2.exceptions.InvalidSyntaxException;
import com.android.ike.ikev2.utils.BigIntegerUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.SecureRandom;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
import javax.crypto.spec.DHPublicKeySpec;

/**
 * IkeKePayload represents a Key Exchange payload
 *
 * <p>This class provides methods for generating Diffie-Hellman value and doing Diffie-Hellman
 * exhchange. Upper layer should ignore IkeKePayload with unsupported DH group type.
 *
 * @see <a href="https://tools.ietf.org/html/rfc7296#page-89">RFC 7296, Internet Key Exchange
 *     Protocol Version 2 (IKEv2)</a>
 */
public final class IkeKePayload extends IkePayload {
    private static final int KE_HEADER_LEN = 4;
    private static final int KE_HEADER_RESERVED = 0;

    // Key exchange data length in octets
    private static final int DH_GROUP_1024_BIT_MODP_DATA_LEN = 128;
    private static final int DH_GROUP_2048_BIT_MODP_DATA_LEN = 256;

    // Algorithm name of Diffie-Hellman
    private static final String KEY_EXCHANGE_ALGORITHM = "DH";

    // TODO: Create a library initializer that checks if Provider supports DH algorithm.

    /** Supported dhGroup falls into {@link DhGroup} */
    public final int dhGroup;

    /** Public DH key for the recipient to calculate shared key. */
    public final byte[] keyExchangeData;

    /** Flag indicates if this is an outbound payload. */
    public final boolean isOutbound;

    /**
     * localPrivateKey caches the locally generated private key when building an outbound KE
     * payload. It will not be sent out. It is only used to calculate DH shared
     * key when IKE library receives a public key from the remote server.
     *
     * <p>localPrivateKey of a inbound payload will be set to null. Caller MUST ensure its an
     * outbound payload before using localPrivateKey.
     */
    @Nullable public final DHPrivateKeySpec localPrivateKey;

    /**
     * Construct an instance of IkeKePayload in the context of IkePayloadFactory
     *
     * @param critical indicates if this payload is critical. Ignored in supported payload as
     *     instructed by the RFC 7296.
     * @param payloadBody payload body in byte array
     * @throws IkeException if there is any error
     * @see <a href="https://tools.ietf.org/html/rfc7296#page-76">RFC 7296, Internet Key Exchange
     *     Protocol Version 2 (IKEv2), Critical.
     */
    IkeKePayload(boolean critical, byte[] payloadBody) throws IkeException {
        super(PAYLOAD_TYPE_KE, critical);

        isOutbound = false;
        localPrivateKey = null;

        ByteBuffer inputBuffer = ByteBuffer.wrap(payloadBody);

        dhGroup = Short.toUnsignedInt(inputBuffer.getShort());
        // Skip reserved field
        inputBuffer.getShort();

        int dataSize = payloadBody.length - KE_HEADER_LEN;
        // Check if dataSize matches the DH group type
        boolean isValidSyntax = true;
        switch (dhGroup) {
            case SaProposal.DH_GROUP_1024_BIT_MODP:
                isValidSyntax = DH_GROUP_1024_BIT_MODP_DATA_LEN == dataSize;
                break;
            case SaProposal.DH_GROUP_2048_BIT_MODP:
                isValidSyntax = DH_GROUP_2048_BIT_MODP_DATA_LEN == dataSize;
                break;
            default:
                // For unsupported DH group, we cannot check its syntax. Upper layer will ingore
                // this payload.
        }
        if (!isValidSyntax) {
            throw new InvalidSyntaxException("Invalid KE payload length for provided DH group.");
        }

        keyExchangeData = new byte[dataSize];
        inputBuffer.get(keyExchangeData);
    }

    /**
     * Construct an instance of IkeKePayload for building an outbound packet.
     *
     * <p>Generate a DH key pair. Cache the private key and and send out the public key as
     * keyExchangeData.
     *
     * <p>Critical bit in this payload must not be set as instructed in RFC 72