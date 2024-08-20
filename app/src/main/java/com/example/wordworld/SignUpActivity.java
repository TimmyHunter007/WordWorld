package com.example.wordworld;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputFname, inputLname, inputEmail, inputPassword; // UI components for user input
    private Button btnSignUp; // Sign Up button
    private ProgressBar progressBar; // Progress bar for showing loading status
    private FirebaseAuth auth; // Firebase Authentication instance
    private DatabaseReference databaseReference; // Firebase Realtime Database reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hide the action bar for a full-screen experience
        setContentView(R.layout.activity_sign_up); // Set the layout for this activity

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI components by finding them in the layout
        inputFname = findViewById(R.id.fname); // First name input field
        inputLname = findViewById(R.id.lname); // Last name input field
        inputEmail = findViewById(R.id.email); // Email input field
        inputPassword = findViewById(R.id.password); // Password input field
        btnSignUp = findViewById(R.id.sign_up_button); // Sign Up button
        progressBar = findViewById(R.id.progressBar); // Progress bar

        // Set the Sign Up button click listener
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user inputs from the fields
                String fname = inputFname.getText().toString().trim();
                String lname = inputLname.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Validate first name input
                if (TextUtils.isEmpty(fname)) {
                    Toast.makeText(getApplicationContext(), "Enter first name!", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }

                // Validate last name input
                if (TextUtils.isEmpty(lname)) {
                    Toast.makeText(getApplicationContext(), "Enter last name!", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }

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
                                // Account creation successful
                                Toast.makeText(SignUpActivity.this, "Account Created!", Toast.LENGTH_SHORT).show();

                                // Get the created user
                                FirebaseUser user = auth.getCurrentUser();

                                // Initialize database reference to the "Users" node in Firebase Realtime Database
                                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

                                // Create a new User object to store in the database
                                User newUser = new User(fname, lname, email, 0, 0, 0, 0, 0, 0); // Initializing silver coins to 0

                                // Save the user details in the database
                                databaseReference.setValue(newUser)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                // User data saved successfully, navigate to MainActivity
                                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                finish(); // Close the SignUpActivity
                                            } else {
                                                // Failed to save user data, display error message
                                                Toast.makeText(SignUpActivity.this, "Failed to save user information.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
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

    // User class for holding user data in the Realtime Database
    public static class User {
        public String fName;
        public String lName;
        public String email;
        public int silverCoins, points;
        public int hasBadge_FirstWord, hasBadge_SevenDayStreak, hasBadge_LevelThreeGuesser, wordsCorrect;

        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        public User() {
        }

        // Constructor to initialize user data
        public User(String fName, String lName, String email, int silverCoins, int points, int hasBadge_FirstWord, int hasBadge_SevenDayStreak, int hasBadge_LevelThreeGuesser, int wordsCorrect) {
            this.fName = fName;
            this.lName = lName;
            this.email = email;
            this.silverCoins = silverCoins;
            this.points = points;
            this.hasBadge_FirstWord = hasBadge_FirstWord;
            this.hasBadge_SevenDayStreak = hasBadge_SevenDayStreak;
            this.hasBadge_LevelThreeGuesser = hasBadge_LevelThreeGuesser;
            this.wordsCorrect = wordsCorrect;
        }
    }
}
