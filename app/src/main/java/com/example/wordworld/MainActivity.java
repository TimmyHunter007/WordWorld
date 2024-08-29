package com.example.wordworld;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth; // Firebase Authentication instance
    private FirebaseUser user; // Current authenticated user
    private DatabaseReference databaseReference; // Database reference for user's data
    private Button profileButton; // Button for navigating to the profile or login screen
    private TextView silverCoins; // UI Element

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hide the action bar for a full-screen experience
        setContentView(R.layout.activity_main); // Set the layout for this activity

        // Initialize Firebase Auth and get the current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Check if the user is signed in
        if (user != null) {
            // Set up the database reference to the current user's data
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

            // Gets user silver coin count and shows it on main screen
            databaseReference.child("silverCoins").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    silverCoins = findViewById(R.id.coin_count);
                    Integer coins = dataSnapshot.getValue(Integer.class);
                    silverCoins.setText("Silver Coins: " + coins);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible error
                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If no user is signed in, handle it accordingly (e.g., show a message or redirect to login)
            Toast.makeText(this, "No user is signed in.", Toast.LENGTH_SHORT).show();
        }

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

        // Initialize the LeaderBoard button and set its click listener to start LeaderBoard
        Button leaderBoard = findViewById(R.id.leader_board);
        leaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LeaderBoardActivity.class);
                startActivity(intent);
            }
        });
    }

}


