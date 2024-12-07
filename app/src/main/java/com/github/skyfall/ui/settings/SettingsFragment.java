package com.github.skyfall.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;

import com.github.skyfall.R;
import com.github.skyfall.data.model.FirebaseManager;
import com.github.skyfall.ui.login.FirebaseAuthActivity;

public class SettingsFragment extends PreferenceFragmentCompat {
    private FirebaseManager firebaseManager;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        firebaseManager = FirebaseManager.getInstance();

        // Each preference represents a setting.
        EditTextPreference usernameButton = findPreference("changeUsername");
        Preference deleteButton = findPreference("deleteAccount");

        if (usernameButton == null) {
            Log.d("demo", "Username Button is null");
            return;
        }
        if (deleteButton == null) {
            Log.d("demo", "Delete Account Button is null");
            return;
        }

        // onClickListener for username
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

        // onClickListener for deleteButton
        deleteButton.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = getBuilder();
            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        });
    }

    private AlertDialog.Builder getBuilder() {
        // Create dialog box to ask user to confirm deleting their account
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(true)
                .setTitle("Confirm account deletion")
                .setMessage("Are you sure you want to delete your account?");

        // Set confirmation button
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
                    firebaseManager.deleteUser()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Your account has been deleted", Toast.LENGTH_LONG).show();

                                    // Clear the back stack and start the login activity
                                    Intent intent = new Intent(getActivity(), FirebaseAuthActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    requireActivity().finish();
                                } else {
                                    Toast.makeText(getActivity(), "Unable to delete account", Toast.LENGTH_LONG).show();
                                }
                            });
                });

        // Denial button closes dialog box, but does nothing else
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
        });

        return builder;
    }
}