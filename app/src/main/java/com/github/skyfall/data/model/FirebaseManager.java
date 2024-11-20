package com.github.skyfall.data.model;

import android.net.Uri;
import android.os.Build;

import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.apache.tika.Tika;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseManager {
    private static FirebaseManager instance;

    private final FirebaseAuth mAuth;
    private final FirebaseFunctions mFunctions;
    private final FirebaseMessaging mMessaging;
    private final FirebaseStorage mStorage;

    private FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        mMessaging = FirebaseMessaging.getInstance();
        mStorage = FirebaseStorage.getInstance();
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

    public UploadTask sendFile(File file, String recipientUid) {
        StorageReference storageRef = mStorage.getReference();

        Uri fileUri = Uri.fromFile(file);
        StorageReference fileRef = storageRef.child(
                String.format(
                        "users/%s/%s",
                        Objects.requireNonNull(mAuth.getCurrentUser()).getUid(),
                        fileUri.getLastPathSegment()));

        Tika tika = new Tika();
        String fileType = "";
        try {
            fileType = tika.detect(file);
        } catch (Exception ignored) {
            fileType = "unknown";
        }

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(fileType)
                .setCustomMetadata("senderUid", mAuth.getCurrentUser().getUid())
                .setCustomMetadata("recipientUid", recipientUid)
                .setCustomMetadata("deviceType", Build.MODEL)
                .build();

        return fileRef.putFile(fileUri, metadata);
    }

    public FileDownloadTask downloadFile(ShareRequest shareRequest) throws IOException {
        StorageReference storageRef = mStorage.getReference();
        StorageReference fileRef = storageRef.child(shareRequest.getFileUri());

        String fileType = shareRequest.getFileType()
                .substring(shareRequest.getFileType().lastIndexOf("/") + 1);

        File localFile = File.createTempFile(fileRef.getName(), fileType);

        return fileRef.getFile(localFile);
    }

    public Task<Void> deleteFile(String fileUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileUri);
        return storageRef.delete();
    }
}
