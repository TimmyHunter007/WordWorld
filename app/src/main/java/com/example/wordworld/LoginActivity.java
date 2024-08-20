package com.example.wordworld;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    // Declare UI components and Firebase authentication instance
    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hide the action bar for a full-screen experience
        setContentView(R.layout.activity_login); // Set the layout for this activity

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Check if a user is already signed in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, navigate to MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // Close the login activity
        }

        // Initialize UI components by finding them in the layout
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnSignIn = findViewById(R.id.sign_in_button);
        btnSignUp = findViewById(R.id.sign_up_button);
        progressBar = findViewById(R.id.progressBar);

        // Set the Sign In button click listener
        btnSignIn.setOnClickListener(new View.OnClickListener() {
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

                progressBar.setVisibility(View.VISIBLE); // Show progress bar during authentication

                // Sign in the user with email and password
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            progressBar.setVisibility(View.GONE); // Hide the progress bar after authentication
                            if (task.isSuccessful()) {
                                // Sign in successful, navigate to MainActivity
                                Toast.makeText(LoginActivity.this, "Sign In Successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish(); // Close the login activity
                            } else {
                                // Sign in failed, display error message
                                Toast.makeText(LoginActivity.this, "Authentication Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Initialize the sign-up button and set the click listener to navigate to the SignUpActivity
        Button profileButton = findViewById(R.id.sign_up_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the SignUpActivity
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the back button and set the click listener to navigate back to the previous screen
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
