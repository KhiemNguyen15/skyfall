package com.github.skyfall.ui.send;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.skyfall.R;
import com.github.skyfall.data.model.User;

import java.util.ArrayList;

public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.UserModelViewHolder>{

    ArrayList<User> users;
    static SendActivity sendActivity;

    public UserRecyclerViewAdapter(ArrayList<User> data, SendActivity sendActivity) {
        this.users = data;
        this.sendActivity = sendActivity;
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_user_recycler_rows, parent, false);
        return new UserModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserModelViewHolder holder, int position) {
        User user = users.get(position);
        holder.userNameText.setText(user.getDisplayName());
        holder.profilePicture.setImageURI(user.getPhotoURL());
        holder.userID.setText(user.getUid());
        holder.user = user;
    }

    @Override
    public int getItemCount(){
        return this.users.size();
    }

    static class UserModelViewHolder extends RecyclerView.ViewHolder {

        TextView userNameText;
        TextView userID;
        ImageView profilePicture;
        Button sendFileButton;
        User user;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = itemView.findViewById(R.id.username_text);
            userID = itemView.findViewById(R.id.userID);
            profilePicture = itemView.findViewById(R.id.profile_picture_view);
            sendFileButton = itemView.findViewById(R.id.recyclerRowButton);

            sendFileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendActivity.sendUserFile();
                }
            });
        }
    }
}