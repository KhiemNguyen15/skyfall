package com.github.skyfall;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        this.shareRequests = newRequests;
        notifyDataSetChanged();
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

        holder.senderTextView.setText(request.getSenderUid());
        holder.fileTypeTextView.setText(request.getFileType());
        holder.timestampTextView.setText(request.getTimestamp().toString());

        holder.downloadButton.setOnClickListener(v -> onDownloadListener.onDownload(request));
    }

    @Override
    public int getItemCount() {
        return shareRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView senderTextView;
        private final TextView fileTypeTextView;
        private final TextView timestampTextView;
        private final Button downloadButton;

        public ViewHolder(ItemShareRequestBinding binding) {
            super(binding.getRoot());
            senderTextView = binding.senderTextView;
            fileTypeTextView = binding.fileTypeTextView;
            timestampTextView = binding.timestampTextView;
            downloadButton = binding.downloadButton;
        }
    }
}
