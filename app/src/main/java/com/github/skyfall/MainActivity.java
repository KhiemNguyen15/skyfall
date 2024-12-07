package com.github.skyfall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.skyfall.ui.receive.ReceiveActivity;
import com.github.skyfall.ui.send.SendActivity;
import com.github.skyfall.ui.settings.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        welcomeTextView = findViewById(R.id.welcomeTextView);
        ImageButton sendButton = findViewById(R.id.sendButton);
        ImageButton receiveButton = findViewById(R.id.receiveButton);
        ImageButton settingsButton = findViewById(R.id.settingsButton);

        sendButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SendActivity.class));
        });

        receiveButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ReceiveActivity.class));
        });

        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        });

        // Fetch and display username on initial load
        fetchAndUpdateUsername();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh username when coming back to MainActivity
        fetchAndUpdateUsername();
    }

    private void fetchAndUpdateUsername() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = firestore.collection("users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String username = task.getResult().getString("username");
                    if (username != null && !username.isEmpty()) {
                        welcomeTextView.setText(String.format("Welcome Back, %s!", username));
                    } else {
                        welcomeTextView.setText("Welcome Back!");
                    }
                } else {
                    Log.e(TAG, "Failed to fetch username", task.getException());
                    welcomeTextView.setText("Welcome Back!");
                }
            });
        } else {
            welcomeTextView.setText("Welcome Back!");
        }
    }
}