package com.github.skyfall;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.github.skyfall.ui.receive.ReceiveActivity;
import com.github.skyfall.ui.send.SendActivity;
import com.github.skyfall.ui.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton sendButton = findViewById(R.id.sendButton);
        ImageButton receiveButton = findViewById(R.id.receiveButton);
        ImageButton settingsButton = findViewById(R.id.hamburgerButton);

        sendButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SendActivity.class));
        });

        receiveButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), ReceiveActivity.class));
        });

        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
        });
    }
}