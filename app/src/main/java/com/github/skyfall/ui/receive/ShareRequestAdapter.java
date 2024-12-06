package com.github.skyfall.ui.receive;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.skyfall.data.model.FirebaseManager;
import com.github.skyfall.data.model.User;
import com.github.skyfall.data.model.ShareRequest;
import com.github.skyfall.databinding.ItemShareRequestBinding;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ShareRequestAdapter extends RecyclerView.Adapter<ShareRequestAdapter.ViewHolder> {
    private final List<ShareRequest> shareRequests;
    private final OnDownloadListener onDownloadListener;

    public interface OnDownloadListener {
        void onDownloadRequest(ShareRequest request);
    }

    public ShareRequestAdapter(List<ShareRequest> shareRequests, OnDownloadListener listener) {
        this.shareRequests = shareRequests;
        this.onDownloadListener = listener;
    }

    public void updateData(List<ShareRequest> newRequests) {
        this.shareRequests.clear(); // Clear old data
        this.shareRequests.addAll(newRequests); // Add new data
        notifyDataSetChanged(); // Notify RecyclerView to re-render
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemShareRequestBinding binding = ItemShareRequestBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShareRequest request = shareRequests.get(position);

        // Get displayName using senderUid
        FirebaseManager firebaseManager = FirebaseManager.getInstance();
        firebaseManager.getUserByUid(request.getSenderUid()).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                User user = task.getResult();
                String displayName = user.getDisplayName();

                // Update UI with the User's displayName
                holder.deviceTextView.setText(String.format("%s's %s", displayName, request.getDeviceType()));
            } else {
                // Fallback if user fetching fails
                holder.deviceTextView.setText(String.format("Unknown User's %s", request.getDeviceType()));
            }
        });

        // Bind file name
        String fileNameWithExtension = getFileName(request);

        // Set the file name in the TextView
        holder.fileTypeTextView.setText(fileNameWithExtension);

        // Bind timestamp
        if (request.getTimestamp() != null) {
            String formattedTimestamp = request.getTimestamp().getFormattedTimestamp();
            holder.timestampTextView.setText(formattedTimestamp);
        } else {
            holder.timestampTextView.setText("Unknown Timestamp");
        }

        // Fetch and bind file size
        firebaseManager.getStorageReference()
                .child(request.getFileUri())
                .getMetadata()
                .addOnSuccessListener(storageMetadata -> {
                    long fileSize = storageMetadata.getSizeBytes();
                    String fileSizeText = android.text.format.Formatter
                            .formatFileSize(holder.itemView.getContext(), fileSize);
                    holder.fileSizeTextView.setText(String.format("%s", fileSizeText));
                }).addOnFailureListener(e -> {
                    holder.fileSizeTextView.setText("Size: Unknown");
                });

        holder.deleteButton.setOnClickListener(v -> {
            firebaseManager.deleteFile(request.getFileUri()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    shareRequests.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(holder.itemView.getContext(), "Denied Request", Toast.LENGTH_SHORT).show();

                    if (shareRequests.isEmpty()) {
                        ((ReceiveActivity) holder.itemView.getContext()).updateNoRequestsView();
                    }
                } else {
                    Toast.makeText(holder.itemView.getContext(), "Failed to Deny Request", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Set up download button action
        holder.downloadIcon.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();

            try {
                firebaseManager.downloadFile(request, context).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        File downloadedFile = task.getResult();
                        Log.d("Adapter", "File downloaded: " + downloadedFile.getAbsolutePath());

                        // Proceed with deletion of the file from cloud storage
                        firebaseManager.deleteFile(request.getFileUri()).addOnCompleteListener(deleteTask -> {
                            if (deleteTask.isSuccessful()) {
                                // Remove item from the list and notify RecyclerView
                                shareRequests.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "File downloaded successfully.", Toast.LENGTH_SHORT).show();

                                // If the list is empty, update the view
                                if (shareRequests.isEmpty()) {
                                    ((ReceiveActivity) context).updateNoRequestsView();
                                }
                            } else {
                                Log.e("Adapter", "Error deleting file: ", deleteTask.getException());
                                Toast.makeText(context, "Error deleting file from storage.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Throwable exception = task.getException();
                        if (exception instanceof IOException && "File already exists".equals(exception.getMessage())) {
                            Log.d("Adapter", "Duplicate file detected.");
                            Toast.makeText(context, "File already exists.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Adapter", "Error downloading file: ", exception);
                            Toast.makeText(context, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (IOException e) {
                Log.e("Adapter", "Error: ", e);
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    private static String getFileName(ShareRequest request) {
        String fileUri = request.getFileUri();
        int lastSlashIndex = fileUri.lastIndexOf('/');
        String fileName;

        // Extract the name from fileUri
        if (lastSlashIndex != -1) {
            fileName = fileUri.substring(lastSlashIndex + 1); // Get the file name without the path
        } else {
            fileName = "Unknown File"; // Fallback if structure is not as expected
        }

        return fileName;
    }

    @Override
    public int getItemCount() {
        return shareRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceTextView;
        private final TextView fileTypeTextView;
        private final TextView fileSizeTextView; // Added file size TextView
        private final ImageButton downloadIcon;
        private final TextView timestampTextView;
        private final ImageButton deleteButton;

        public ViewHolder(ItemShareRequestBinding binding) {
            super(binding.getRoot());
            deviceTextView = binding.deviceTextView;
            fileTypeTextView = binding.fileTypeTextView;
            fileSizeTextView = binding.fileSizeTextView; // Initialize file size TextView
            downloadIcon = binding.downloadIcon;
            timestampTextView = binding.timestampTextView;
            deleteButton = binding.deleteButton;
        }
    }
}
