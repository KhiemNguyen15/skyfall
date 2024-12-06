package com.github.skyfall.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;

import com.github.skyfall.MainActivity;
import com.github.skyfall.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class FirebaseAuthActivity extends AppCompatActivity {
    private static final int RC_GOOGLE_SIGN_IN = 123;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_sign_in_layout);

        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();

        // PRODUCTION APP CHECK PROVIDER
//        firebaseAppCheck.installAppCheckProviderFactory(
//                PlayIntegrityAppCheckProviderFactory.getInstance());

        // DEVELOPMENT APP CHECK PROVIDER
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.email_button).setOnClickListener(v -> {
            // Navigate to the email/password Login screen
            startActivity(new Intent(this, LoginActivity.class));
        });
        findViewById(R.id.google_button).setOnClickListener(v -> {
            launchGoogleSignIn();
        });

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && (user.getEmail() == null || user.isEmailVerified())) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            mAuth.signOut(); // Ensure user is logged out
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            if (mAuth.getCurrentUser() != null) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        }
    }

    private void launchGoogleSignIn() {
        List<AuthUI.IdpConfig> providers = List.of(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.Theme_Skyfall_NoActionBar)
                .build();

        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }
}