package com.github.skyfall.ui.settings;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;

import com.github.skyfall.R;
import com.github.skyfall.data.model.FirebaseManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    private FirebaseManager firebaseManager;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                // Call firebase manager changeUserPfp
                Toast.makeText(getActivity(), "Image selection working", Toast.LENGTH_SHORT).show();
            });

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        firebaseManager = FirebaseManager.getInstance();

        // Each preference represents a setting.

        // SwitchPreferenceCompat notificationSwitch = findPreference("enableNotifs");
        // Preference clearHistoryButton = findPreference("clearHistory");
        EditTextPreference usernameButton = findPreference("changeUsername");
        Preference passwordButton = findPreference("changePassword");
        Preference pfpButton = findPreference("changePfp");

        /*if(notificationSwitch == null) {
            Toast.makeText(getActivity(), "Notification Switch is null", Toast.LENGTH_LONG).show();
            return;
        }
        if(clearHistoryButton == null) {
            Toast.makeText(getActivity(), "Clear History Button is null", Toast.LENGTH_LONG).show();
            return;
        }*/
        if (usernameButton == null) {
            Toast.makeText(getActivity(), "Username Button is null", Toast.LENGTH_LONG).show();
            return;
        }
        if (passwordButton == null) {
            Toast.makeText(getActivity(), "Password Button is null", Toast.LENGTH_LONG).show();
            return;
        }
        if (pfpButton == null) {
            Toast.makeText(getActivity(), "Profile Picture Button is null", Toast.LENGTH_LONG).show();
            return;
        }

        usernameButton.setOnPreferenceChangeListener((preference, newValue) -> {
            String username = newValue.toString();
            firebaseManager.updateDisplayName(username).addOnCompleteListener(task -> {
                Toast.makeText(
                        getActivity(),
                        "Username has been changed to " + username,
                        Toast.LENGTH_LONG).show();
            });

            return false;
        });

        pfpButton.setOnPreferenceClickListener(preference -> {
            mGetContent.launch("image/*");
            return false;
        });
    }
}