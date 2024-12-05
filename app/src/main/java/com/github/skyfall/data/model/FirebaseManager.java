package com.github.skyfall.data.model;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

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

    public Task<File> downloadFile(ShareRequest shareRequest, Context context) throws IOException {
        StorageReference storageRef = mStorage.getReference();
        StorageReference fileRef = storageRef.child(shareRequest.getFileUri());

        File downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (downloadsDir == null) {
            throw new IOException("Unable to access downloads directory");
        }

        String originalFileName = shareRequest.getFileUri()
                .substring(shareRequest.getFileUri().lastIndexOf('/') + 1);

        File finalFile = new File(downloadsDir, originalFileName);

        // Ensure the file does not exist
        if (finalFile.exists()) {
            finalFile.delete();
        }

        // Start the file download
        return fileRef.getFile(finalFile)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("DownloadFile", "File downloaded successfully: " + finalFile.getAbsolutePath());
                })
                .addOnFailureListener(e -> {
                    Log.e("DownloadFile", "File download failed: ", e);
                })
                .addOnProgressListener(snapshot -> {
                    long bytesTransferred = snapshot.getBytesTransferred();
                    long totalBytes = snapshot.getTotalByteCount();
                    Log.d("DownloadFile", "Progress: " + bytesTransferred + "/" + totalBytes);
                })
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        // Verify file size to avoid 0-byte files
                        if (finalFile.length() == 0) {
                            throw new IOException("Downloaded file is empty");
                        }
                        return Tasks.forResult(finalFile);
                    } else {
                        throw Objects.requireNonNull(task.getException());
                    }
                });
    }

    public Task<Void> deleteFile(String fileUri) {
        StorageReference storageRef = mStorage.getReference().child(fileUri);
        return storageRef.delete();
    }
}
