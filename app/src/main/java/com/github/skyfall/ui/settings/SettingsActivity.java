package com.github.skyfall.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.skyfall.R;
import com.github.skyfall.ui.login.FirebaseAuthActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button logoutButton = findViewById(R.id.logoutButton);

        if (findViewById(R.id.idFrameLayout) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.idFrameLayout, new SettingsFragment())
                    .commit();
        }
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_LONG).show();

                // Clear the back stack and start the login activity
                Intent intent = new Intent(getApplicationContext(), FirebaseAuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Could not logout", Toast.LENGTH_LONG).show();
            }
        });
    }
}