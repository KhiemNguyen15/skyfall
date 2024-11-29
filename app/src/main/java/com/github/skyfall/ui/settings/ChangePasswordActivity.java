package com.github.skyfall.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.skyfall.R;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check text views to see if current password is correct, new and confirm passwords are equal,
                //and new and confirm passwords are correct (right amount of characters, etc.)

                launchActivity();
            }
        });
    }
    private void launchActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
    }
}