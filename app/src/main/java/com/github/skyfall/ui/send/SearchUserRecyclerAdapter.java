package com.github.skyfall.ui.send;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.skyfall.R;
import com.github.skyfall.data.model.User;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<User, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_rows, parent, false);
        return new UserModelViewHolder(view);

    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull User model) {
        holder.userNameText.setText(model.getDisplayName());
        holder.profilePicture.setImageURI(model.getPhotoURL());
        holder.userID.setText(model.getUid());
    }

    class UserModelViewHolder extends RecyclerView.ViewHolder {

        TextView userNameText;
        TextView userID;
        ImageView profilePicture;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = itemView.findViewById(R.id.username_text);
            userID = itemView.findViewById(R.id.userID);
            profilePicture = itemView.findViewById(R.id.profile_picture_view);

        }
    }

}
