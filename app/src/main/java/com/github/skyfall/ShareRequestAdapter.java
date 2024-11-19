package com.github.skyfall;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.skyfall.data.model.FirebaseManager;
import com.github.skyfall.data.model.User;
import com.github.skyfall.data.model.ShareRequest;
import com.github.skyfall.databinding.ItemShareRequestBinding;

import java.util.List;
public class ShareRequestAdapter extends RecyclerView.Adapter<ShareRequestAdapter.ViewHolder> {
    private List<ShareRequest> shareRequests;
    private final OnDownloadListener onDownloadListener;

    public interface OnDownloadListener {
        void onDownload(ShareRequest request);
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
        firebaseManager.getUser(request.getSenderUid()).addOnCompleteListener(task -> {
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
        String fileName = request.getFileUri().replaceFirst("files/", "");
        holder.fileTypeTextView.setText(fileName);

        // Set up download button action
        holder.downloadIcon.setOnClickListener(v -> {
            // Call the onDownloadListener when the icon is clicked
            onDownloadListener.onDownload(request);

    });
    }

    @Override
    public int getItemCount() {
        return shareRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceTextView;
        private final TextView fileTypeTextView;
        private final ImageButton downloadIcon;


        public ViewHolder(ItemShareRequestBinding binding) {
            super(binding.getRoot());
            deviceTextView = binding.deviceTextView;
            fileTypeTextView = binding.fileTypeTextView;
            downloadIcon = binding.downloadIcon;
        }
    }
}
