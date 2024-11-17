package com.github.skyfall;


import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.skyfall.data.model.FirebaseManager;
import com.github.skyfall.data.model.ShareRequest;
import com.github.skyfall.databinding.ActivityReceiveBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;

public class ReceiveActivity extends AppCompatActivity {

    private ActivityReceiveBinding binding;
    private FirebaseManager firebaseManager;
    private ShareRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityReceiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();
        adapter = new ShareRequestAdapter(new ArrayList<>(), this::onDownloadRequest);

        // Set up RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // Load incoming requests
        fetchRequests();
    }

    private void fetchRequests() {
        firebaseManager.getIncomingRequests()
                .addOnCompleteListener(new OnCompleteListener<List<ShareRequest>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<ShareRequest>> task) {
                        if (!task.isSuccessful()) {
                            // Handle fa`ilure
                            Toast.makeText(ReceiveActivity.this, "Failed to load requests", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List<ShareRequest> shareRequests = task.getResult();

                        if (shareRequests != null && !shareRequests.isEmpty()) {
                            adapter.updateData(shareRequests);
                        } else {
                            Toast.makeText(ReceiveActivity.this, "No incoming requests", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void onDownloadRequest(ShareRequest request) {
        try {
            firebaseManager.downloadFile(request)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            // Handle failure
                            Toast.makeText(ReceiveActivity.this, "Failed to download file", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // File downloaded successfully
                        Toast.makeText(ReceiveActivity.this, "File downloaded successfully", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Toast.makeText(this, "Error downloading file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
