package com.github.skyfall.ui.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;

import com.github.skyfall.R;
import com.github.skyfall.data.model.FirebaseManager;
import com.github.skyfall.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle("Confirm account deletion");
        builder.setMessage("Are you sure you want to delete your account?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if(user != null) {
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Your account has been deleted", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getContext(), "Account could not be deleted", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "User could not be deleted", Toast.LENGTH_LONG).show();
                            return;
                        }
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder;
    }
}