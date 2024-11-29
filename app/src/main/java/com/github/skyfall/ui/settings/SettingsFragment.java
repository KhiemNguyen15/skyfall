package com.github.skyfall.ui.settings;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.github.skyfall.R;
import com.github.skyfall.data.model.FirebaseManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SettingsFragment extends PreferenceFragmentCompat{

    private FirebaseManager firebaseManager;

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<>() {
                @Override
                public void onActivityResult(Uri uri) {
                    //Call firebase manager changeUserPfp
                    Toast.makeText(getActivity(), "Image selection working", Toast.LENGTH_SHORT).show();
                }
            });
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        firebaseManager = FirebaseManager.getInstance();

        //Each preference represents a setting.
        SwitchPreferenceCompat notificationSwitch = findPreference("enableNotifs");
        Preference clearHistoryButton = findPreference("clearHistory");
        EditTextPreference usernameButton = findPreference("changeUsername");
        Preference passwordButton = findPreference("changePassword");
        Preference pfpButton = findPreference("changePfp");

        if(notificationSwitch == null) {
            Toast.makeText(getActivity(), "Notification Switch is null", Toast.LENGTH_LONG).show();
            return;
        }
        if(clearHistoryButton == null) {
            Toast.makeText(getActivity(), "Clear History Button is null", Toast.LENGTH_LONG).show();
            return;
        }
        if(usernameButton == null) {
            Toast.makeText(getActivity(), "Username Button is null", Toast.LENGTH_LONG).show();
            return;
        }
        if(passwordButton == null) {
            Toast.makeText(getActivity(), "Password Button is null", Toast.LENGTH_LONG).show();
            return;
        }
        if(pfpButton == null) {
            Toast.makeText(getActivity(), "Profile Picture Button is null", Toast.LENGTH_LONG).show();
            return;
        }

        usernameButton.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                String username = newValue.toString();
                firebaseManager.updateDisplayName(username).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Username has been changed to " + username, Toast.LENGTH_LONG).show();
                    }
                });
                return false;
            }
        });

        passwordButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                //startActivity(new Intent(getActivity(), ChangePasswordActivity.class));


                return false;
            }
        });

        pfpButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                mGetContent.launch("image/*");
                return false;
            }
        });
    }
}