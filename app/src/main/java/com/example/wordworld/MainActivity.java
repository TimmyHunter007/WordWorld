package com.example.wordworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth; // Firebase Authentication instance
    private FirebaseUser user; // Current authenticated user
    private Button profileButton; // Button for navigating to the profile or login screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hide the action bar for a full-screen experience
        setContentView(R.layout.activity_main); // Set the layout for this activity

        // Initialize Firebase Auth and get the current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Get the current date formatted as "Month Day, Year"
        String currentDate = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(new Date());

        // Find the TextView by its ID and set the current date
        TextView dateTextView = findViewById(R.id.date);
        dateTextView.setText(currentDate);

        // Find the user email TextView and set its text for testing purposes
        TextView userEmailTextView = findViewById(R.id.user_email);
        if (user != null) {
            // If the user is signed in, display their email
            userEmailTextView.setText("Logged in as: " + user.getEmail());
        } else {
            // If no user is signed in, display "Not logged in"
            userEmailTextView.setText("Not logged in");
        }

        // Initialize the back button and set its click listener to navigate back to the previous screen
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button action
                onBackPressed(); // Navigate back to the previous activity
            }
        });

        // Initialize the navigation button and set its click listener
        ImageButton navButton = findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle navigation button action
                // Open navigation drawer or perform another action (placeholder)
            }
        });

        // Initialize the profile button and set its click listener
        profileButton = findViewById(R.id.profile);
        if (user != null) {
            // If the user is signed in, set the button text to "PROFILE"
            profileButton.setText("PROFILE");
            profileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the ProfileActivity when the profile button is clicked
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            // If no user is signed in, set the button text to "SIGN IN"
            profileButton.setText("SIGN IN");
            profileButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start the LoginActivity when the sign-in button is clicked
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }

        // Initialize the level one button and set its click listener to start LevelOneActivity
        Button levelOneButton = findViewById(R.id.level_one);
        levelOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LevelOneActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the level two button and set its click listener to start LevelTwoActivity
        Button levelTwoButton = findViewById(R.id.level_two);
        levelTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LevelTwoActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the level three button and set its click listener to start LevelThreeActivity
        Button levelThreeButton = findViewById(R.id.level_three);
        levelThreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LevelThreeActivity.class);
                startActivity(intent);
            }
        });
    }
}
