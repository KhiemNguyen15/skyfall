package com.github.skyfall.data.model;

import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private static FirebaseManager instance;
    private final FirebaseFunctions mFunctions;
    private final FirebaseMessaging mMessaging;

    private FirebaseManager() {
        mFunctions = FirebaseFunctions.getInstance();
        mMessaging = FirebaseMessaging.getInstance();
    }

    public static FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }

        return instance;
    }

    public Task<List<ShareRequest>> getIncomingRequests() {
        return mFunctions.getHttpsCallable("getShareRequests")
                .call()
                .continueWith(task -> {
                    Gson gson = new Gson();
                    String json = gson.toJson(task.getResult().getData());
                    Type listType = new TypeToken<List<ShareRequest>>() {
                    }.getType();

                    return gson.fromJson(json, listType);
                });
    }

    public Task<Void> uploadFcmToken() {
        String token = mMessaging.getToken().getResult();

        Map<String, Object> data = new HashMap<>();
        data.put("fcmToken", token);

        return mFunctions.getHttpsCallable("setFcmToken")
                .call(data)
                .continueWith(task -> null);
    }

    public Task<User> getUser(String name) {
        Map<String, Object> data = new HashMap<>();
        data.put("displayName", name);

        return mFunctions.getHttpsCallable("getUserByName")
                .call(data)
                .continueWith(task -> {
                    Gson gson = new Gson();
                    String json = gson.toJson(task.getResult().getData());

                    return gson.fromJson(json, User.class);
                });
    }

    public Task<Void> updateDisplayName(String name) {
        Map<String, Object> data = new HashMap<>();
        data.put("displayName", name);

        return mFunctions.getHttpsCallable("updateDisplayName")
                .call(data)
                .continueWith(task -> null);
    }
}
