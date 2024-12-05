package com.github.skyfall.ui.send;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.skyfall.R;
import com.github.skyfall.data.model.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserModelViewHolder> {
    private final ArrayList<User> users;
    private final SendActivity sendActivity;

    public UserRecyclerViewAdapter(ArrayList<User> data, SendActivity sendActivity) {
        this.users = data;
        this.sendActivity = sendActivity;
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_user_recycler_rows, parent, false);

        return new UserModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserModelViewHolder holder, int position) {
        User user = users.get(position);
        holder.user = user;
        holder.usernameText.setText(user.getDisplayName());

        if (user.getPhotoURL() == null) {
            user.setPhotoURL("profile_pictures/default_pfp.jpg");
        }

        StorageReference profilePictureRef = FirebaseStorage.getInstance()
                .getReference()
                .child(user.getPhotoURL());

        Glide.with(sendActivity.getApplicationContext())
                .load(profilePictureRef)
                .into(holder.profilePicture);
    }

    @Override
    public int getItemCount() {
        return this.users.size();
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        ImageView profilePicture;
        Button sendFileButton;
        User user;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username_text);
            profilePicture = itemView.findViewById(R.id.profile_picture_view);
            sendFileButton = itemView.findViewById(R.id.recyclerRowButton);

            sendFileButton.setOnClickListener(v -> sendActivity.sendUserFile());
        }
    }
}