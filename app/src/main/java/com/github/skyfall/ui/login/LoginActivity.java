package com.github.skyfall.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.skyfall.MainActivity;
import com.github.skyfall.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);
        Button resendVerificationButton = findViewById(R.id.resendVerificationButton);
        TextView forgotPasswordButton = findViewById(R.id.forgotPasswordButton);

        // Initially hide the Resend Verification Email button
        resendVerificationButton.setVisibility(View.GONE);

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();

                                // Show the resend verification button
                                resendVerificationButton.setVisibility(View.VISIBLE);
                                resendVerificationButton.setEnabled(true); // Ensure it's enabled initially
                                resendVerificationButton.setOnClickListener(view -> {
                                    resendVerificationEmailWithCooldown(resendVerificationButton, user);
                                });
                            }
                        } else {
                            Toast.makeText(this, "Unknown email or password.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Sign Up button logic
        signupButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Send verification email
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(emailTask -> {
                                            if (emailTask.isSuccessful()) {
                                                Toast.makeText(this, "Registration successful. Verify your email before logging in.", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // Handle exceptions
                            if (task.getException() != null) {
                                String errorCode = ((com.google.firebase.auth.FirebaseAuthException) task.getException()).getErrorCode();

                                switch (errorCode) {
                                    case "ERROR_EMAIL_ALREADY_IN_USE":
                                        Toast.makeText(this, "An account is already associated with this email.", Toast.LENGTH_SHORT).show();
                                        break;

                                    case "ERROR_WEAK_PASSWORD":
                                        Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }
                    });
        });

        forgotPasswordButton.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");

        final EditText emailInput = new EditText(this);
        emailInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInput.setHint("Enter your email");
        builder.setView(emailInput);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(email);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "No account associated with this email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void resendVerificationEmailWithCooldown(Button resendVerificationButton, FirebaseUser user) {
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Verification email sent. Check your inbox.", Toast.LENGTH_SHORT).show();

                            // Disable the button and start the cooldown timer
                            resendVerificationButton.setEnabled(false);
                            resendVerificationButton.setText("Please wait...");

                            // Re-enable after a delay (60 seconds)
                            resendVerificationButton.postDelayed(() -> {
                                resendVerificationButton.setEnabled(true);
                                resendVerificationButton.setText("Resend Verification Email");
                            }, 60000); // 60,000 ms = 60 seconds
                        } else {
                            Toast.makeText(this, "Failed to send verification email. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Unable to resend verification email. Please log in again.", Toast.LENGTH_SHORT).show();
        }
    }
}
