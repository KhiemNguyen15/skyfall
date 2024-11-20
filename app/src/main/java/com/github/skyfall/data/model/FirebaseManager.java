package com.github.skyfall.data.model;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.common.reflect.TypeToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

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

    public Task<User> getUserByName(String name) {
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

    public Task<User> getUserByUid(String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);

        return mFunctions.getHttpsCallable("getUserByUid")
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

    public UploadTask sendFile(Uri fileUri, String recipientUid, Context context) {
        StorageReference storageRef = mStorage.getReference();

        StorageReference fileRef = storageRef.child(
                String.format(
                        "users/%s/%s",
                        Objects.requireNonNull(mAuth.getCurrentUser()).getUid(),
                        fileUri.getLastPathSegment()));

        String fileType;
        try {
            ContentResolver cR = context.getContentResolver();
            fileType = cR.getType(fileUri);
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


    public Task<File> downloadFile(ShareRequest shareRequest) throws IOException {
        StorageReference storageRef = mStorage.getReference();
        StorageReference fileRef = storageRef.child(shareRequest.getFileUri());

        // Extract file extension
        String fileType = shareRequest.getFileType().substring(shareRequest.getFileType().lastIndexOf("/") + 1);
        String originalFileName = shareRequest.getFileUri().substring(shareRequest.getFileUri().lastIndexOf('/') + 1);

        // Build the final file name with the correct extension
        String baseFileName = originalFileName + "." + fileType;

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File finalFile = new File(downloadsDir, baseFileName);

        // Start the file download
        return fileRef.getFile(finalFile).continueWithTask(task -> {
            if (task.isSuccessful()) {
                return Tasks.forResult(finalFile); // Return the downloaded file
            } else {
                throw task.getException();
            }
        });
    }

    public Task<Void> deleteFile(String fileUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileUri);
        return storageRef.delete();
    }
}
