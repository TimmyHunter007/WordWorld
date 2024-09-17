package com.example.wordworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private FirebaseAuth auth; // Firebase Authentication instance
    private FirebaseUser user; // Current authenticated user
    private DatabaseReference databaseReference; // Database reference for user's data
    private Button profileButton; // Button for navigating to the profile or login screen
    private TextView silverCoins, points; // UI Element

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

        // Set up the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent;

                if (id == R.id.level_one) {
                    intent = new Intent(MainActivity.this, LevelOneActivity.class);
                    startActivity(intent);
                } else if (id == R.id.level_two) {
                    intent = new Intent(MainActivity.this, LevelTwoActivity.class);
                    startActivity(intent);
                } else if (id == R.id.level_three) {
                    intent = new Intent(MainActivity.this, LevelThreeActivity.class);
                    startActivity(intent);
                } else if (id == R.id.badges) {
                    intent = new Intent(MainActivity.this, BadgesActivity.class);
                    startActivity(intent);

                } else if (id == R.id.profile) {
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else if (id == R.id.leader_board) {
                    intent = new Intent(MainActivity.this, LeaderBoardActivity.class);
                    startActivity(intent);
                }

                drawerLayout.closeDrawers(); // Close the drawer after an item is clicked
                return true;
            }
        });

        // Get current date
            databaseReference.child("points").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    points = findViewById(R.id.points);
                    Integer point = dataSnapshot.getValue(Integer.class);
                    points.setText("Points: " + point);
                }

                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If no user is signed in, handle it accordingly (e.g., show a message or redirect to log in)
            Toast.makeText(this, "No user is signed in.", Toast.LENGTH_SHORT).show();
        }

        // Get the current date formatted as "Month Day, Year"
        String currentDate = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(new Date());

        // Find the TextView by its ID and set the current date
        TextView dateTextView = findViewById(R.id.date);
        dateTextView.setText(currentDate);

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
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
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

        Button badgesButton = findViewById(R.id.badges);
        badgesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BadgesActivity.class);
                startActivity(intent);
            }
        });
    }

}



