package com.github.skyfall;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (findViewById(R.id.idFrameLayout) != null) {
            if (savedInstanceState != null) {
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.idFrameLayout, new SettingsFragment())
                    .commit();
        }

    }
    public Intent launchPasswordActivity() {
        return new Intent(this, ChangePasswordActivity.class);
    }
}