package com.github.skyfall.ui.receive;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.skyfall.data.model.FirebaseManager;
import com.github.skyfall.data.model.ShareRequest;
import com.github.skyfall.databinding.ActivityReceiveBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        firebaseManager.getIncomingRequests().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ShareRequest> requests = task.getResult();
                if (requests != null && !requests.isEmpty()) {
                    Log.d("ShareRequests", "Fetched " + requests.size() + " requests");

                    // Update RecyclerView with the fetched requests
                    adapter.updateData(requests);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.noRequestsTextView.setVisibility(View.GONE);

                    for (ShareRequest request : requests) {
                        Log.d("ShareRequests", "File: " + request.getFileUri());
                    }
                } else {
                    Log.d("ShareRequests", "No requests found.");

                    // No requests, show the "No requests found" text
                    binding.noRequestsTextView.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                }
            } else {
                Log.e("ShareRequests", "Error fetching requests", task.getException());

                binding.noRequestsTextView.setText("Error fetching requests. Please try again.");
                binding.noRequestsTextView.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            }
        });
    }

    private void onDownloadRequest(ShareRequest request) {
        try {
            firebaseManager.downloadFile(request,this).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    // Handle failure
                    Toast.makeText(
                            ReceiveActivity.this,
                            String.format("Failed to download file: %s",
                                    Objects.requireNonNull(task.getException()).getMessage()),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // File downloaded successfully
                File downloadedFile = task.getResult(); // Get the downloaded file
                String fileName = request.getFileUri()
                        .substring(request.getFileUri().lastIndexOf('/') + 1);

                // Save the file to the public storage
                saveFileToPublicStorage(downloadedFile, fileName);
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error downloading file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFileToPublicStorage(File sourceFile, String fileName) {
        File publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File destinationFile = new File(publicDir, fileName);

        try (InputStream in = new FileInputStream(sourceFile);
             OutputStream out = new FileOutputStream(destinationFile)) {

            // Copy data from source to destination
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateNoRequestsView() {
        if (adapter.getItemCount() == 0) {
            binding.noRequestsTextView.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            binding.noRequestsTextView.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
