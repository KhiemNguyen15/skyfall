package com.github.skyfall.ui.send;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.skyfall.R;
import com.github.skyfall.data.model.FirebaseManager;
import com.github.skyfall.data.model.User;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class SendActivity extends AppCompatActivity {
    private final long MAX_FILE_SIZE = 50_000_000;  // 50 MB

    private FirebaseManager firebaseManager;
    private EditText searchInput;
    private ImageButton searchButton;
    private RecyclerView recyclerView;
    private UserRecyclerViewAdapter adapter;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<>() {
                @Override
                public void onActivityResult(Uri uri) {
                    // Check if file is within correct file size limitations
                    try (AssetFileDescriptor fileDescriptor = getApplicationContext()
                            .getContentResolver()
                            .openAssetFileDescriptor(uri, "r")) {

                        assert fileDescriptor != null;
                        long fileSize = fileDescriptor.getLength();

                        if (fileSize > MAX_FILE_SIZE) {
                            Toast.makeText(getApplicationContext(),
                                    "File exceeds size limit",
                                    Toast.LENGTH_LONG).show();

                            return;
                        }
                    } catch (Exception e) {
                        Log.e("SendActivity", "Error checking file size: " + e.getMessage());
                    }

                    // Search for specific user using input from recycler view
                    firebaseManager.getUserByName(searchInput.getText().toString())
                            .addOnCompleteListener(result -> {
                                User user = result.getResult();

                                // Set listeners to show different messages if file was successfully sent or not
                                firebaseManager.sendFile(uri, user.getUid(), getApplicationContext())
                                        .addOnFailureListener(exception ->
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "File was unable to be sent.",
                                                        Toast.LENGTH_LONG).show())
                                        .addOnSuccessListener(taskSnapshot ->
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "File has been sent.",
                                                        Toast.LENGTH_LONG).show());
                            });
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        firebaseManager = FirebaseManager.getInstance();

        searchButton = findViewById(R.id.searchicon_button);
        searchInput = findViewById(R.id.username_input);
        recyclerView = findViewById(R.id.users_RecyclerView);

        searchInput.requestFocus();

        // onClickListener for searchButton
        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString();

            if (searchTerm.isEmpty() || searchTerm.length() < 2) {
                searchInput.setError("Invalid Username");
                return;
            }

            // Search recycler view for user using searchTerm (user input)
            setupSearchRecyclerView(searchTerm);
        });
    }

    void setupSearchRecyclerView(String searchTerm) {
        ArrayList<User> users = new ArrayList<>();
        Task<User> u = firebaseManager.getUserByName(searchTerm);

        // Get user from searching Firestore by username
        u.addOnCompleteListener(result -> {
            try {
                users.add(u.getResult());
                adapter = new UserRecyclerViewAdapter(users, this);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(adapter);
            } catch (Exception e) {
                // If search term results in user == null
                Toast.makeText(this, "User does not exist", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void sendUserFile() {
        mGetContent.launch("*/*");
    }
}