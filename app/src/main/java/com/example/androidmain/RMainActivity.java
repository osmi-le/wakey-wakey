package com.example.androidmain;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class RMainActivity extends AppCompatActivity {
    static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    UserData userData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isGooglePlayServicesAvailable()) {
            if( UserData.getCredential() == null) {
                //intent subrogin
                Intent it = new Intent(getApplicationContext(), SubLogin.class);
                startActivity(it);

            }
            else{
                Intent it = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(it);
            }
        } else {
            Intent it = new Intent(getApplicationContext(), SubLogin.class);
            startActivity(it);
        }
        finish();
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                googleAPI.isGooglePlayServicesAvailable(this);
        if(connectionStatusCode != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(connectionStatusCode)) {
                googleAPI.getErrorDialog(this, connectionStatusCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }
}
