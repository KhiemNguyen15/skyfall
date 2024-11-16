package com.github.skyfall;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference passwordButton = findPreference("changePassword");
        passwordButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                //Toast.makeText(getActivity(), "IT WORKS!", Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }
}