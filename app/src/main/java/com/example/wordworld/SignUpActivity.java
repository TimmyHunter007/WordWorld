package com.example.wordworld;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword; // UI components for user input
    private Button btnSignUp; // Sign Up button
    private ProgressBar progressBar; // Progress bar for showing loading status
    private FirebaseAuth auth; // Firebase Authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hide the action bar for a full-screen experience
        setContentView(R.layout.activity_sign_up); // Set the layout for this activity

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI components by finding them in the layout
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.sign_up_button);
        progressBar = findViewById(R.id.progressBar);

        // Set the Sign Up button click listener
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get email and password from input fields
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Validate email input
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }

                // Validate password input
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }

                // Ensure password length is at least 6 characters
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }

                progressBar.setVisibility(View.VISIBLE); // Show progress bar during account creation

                // Create a new user with the provided email and password
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, task -> {
                            progressBar.setVisibility(View.GONE); // Hide progress bar after account creation
                            if (task.isSuccessful()) {
                                // Account creation successful, navigate to MainActivity
                                Toast.makeText(SignUpActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                finish(); // Close the SignUpActivity
                            } else {
                                // Account creation failed, display error message
                                Toast.makeText(SignUpActivity.this, "Authentication Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Initialize the back button and set its click listener to navigate back
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button action
                onBackPressed(); // Navigate back to the previous activity
            }
        });

    }
}
