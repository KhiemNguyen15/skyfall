package com.github.skyfall.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;

import com.github.skyfall.R;
import com.github.skyfall.data.model.FirebaseManager;
import com.github.skyfall.ui.login.FirebaseAuthActivity;

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
        Preference pfpButton = findPreference("changePfp");
        Preference deleteButton = findPreference("deleteAccount");

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
        if (pfpButton == null) {
            Toast.makeText(getActivity(), "Profile Picture Button is null", Toast.LENGTH_LONG).show();
            return;
        }
        if (deleteButton == null) {
            Toast.makeText(getActivity(), "Password Button is null", Toast.LENGTH_LONG).show();
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

        deleteButton.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = getBuilder();

            AlertDialog dialog = builder.create();
            dialog.show();

            return false;
        });
    }

    private AlertDialog.Builder getBuilder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(true)
                .setTitle("Confirm account deletion")
                .setMessage("Are you sure you want to delete your account?");

        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
                    firebaseManager.deleteUser()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Your account has been deleted", Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getActivity(), FirebaseAuthActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    requireActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), "Unable to delete account", Toast.LENGTH_LONG).show();
                                }
                            });
                });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
        });

        return builder;
    }
}