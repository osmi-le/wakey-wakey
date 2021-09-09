package com.example.androidmain;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class UserData {

    private static GoogleAccountCredential credential;
    private static String userName;
    private static String userEmail;
    private static UserData userData;


    public static void setCredential(GoogleAccountCredential credential) {
        UserData.credential = credential;
    }

    public static void setUserName(String userName) {
        UserData.userName = userName;
    }

    public static void setUserEmail(String userEmail) {
        UserData.userEmail = userEmail;
    }

    public static void setUserData(UserData userData) {
        UserData.userData = userData;
    }

    public static GoogleAccountCredential getCredential() {
        return credential;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getUserEmail() {
        return userEmail;
    }
}
