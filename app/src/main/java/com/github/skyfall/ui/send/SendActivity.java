package com.github.skyfall.ui.send;  

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.skyfall.R;
import com.github.skyfall.data.model.FirebaseManager;
import com.github.skyfall.data.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class SendActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    RecyclerView recyclerView;
    UserRecyclerViewAdapter adapter;
    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    Log.d("demo", "Path: " + uri.getPath());

                    Task<User> userTask = FirebaseManager.getInstance().getUser(searchInput.getText().toString());

                    userTask.addOnCompleteListener(result -> {
                        User user = result.getResult();

                        UploadTask uploadTask = FirebaseManager.getInstance().sendFile(uri, user.getUid(), getApplicationContext());

                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(), "File was unable to be sent.", Toast.LENGTH_LONG).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(getApplicationContext(), "File has been sent.", Toast.LENGTH_LONG).show();
                            }
                        });
                    });
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        searchButton = findViewById(R.id.searchicon_button);
        searchInput = findViewById(R.id.username_input);
        recyclerView = findViewById(R.id.users_RecyclerView);

        searchInput.requestFocus();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = searchInput.getText().toString();

                if (searchTerm.isEmpty() || searchTerm.length() < 2) {
                    searchInput.setError("Invalid Username");
                    return;
                }

                setupSearchRecyclerView(searchTerm);
            }
        });
    }
    void setupSearchRecyclerView(String searchTerm){

        ArrayList<User> users = new ArrayList<>();
        Task<User> u = FirebaseManager.getInstance().getUser(searchTerm);

        u.addOnCompleteListener(result -> {
            try {
                users.add(u.getResult());
                adapter = new UserRecyclerViewAdapter(users, this);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
            }catch(Exception e){
                Toast.makeText(this, "User does not exist", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void sendUserFile() {
        mGetContent.launch("image/*");
    }
}