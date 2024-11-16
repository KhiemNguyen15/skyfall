package com.github.skyfall.data.model;

import com.google.firebase.auth.FirebaseUser;

public class User {

    // User variable contains profile picture and the user id
    private FirebaseUser user;
    private String displayName;
    private String fcmToken;

    public User(FirebaseUser user, String displayName, String fcmToken) {
        this.user = user;
        this.displayName = displayName;
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public FirebaseUser getUser() {
        return user;
    }
}