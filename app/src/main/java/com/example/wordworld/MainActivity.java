package com.example.wordworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // Set up the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

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
                    // Navigate to Profile activity
                } else if (id == R.id.leader_board) {
                    // Navigate to Leaderboard activity
                }

                drawerLayout.closeDrawers(); // Close the drawer after an item is clicked
                return true;
            }
        });

        // Get current date
        String currentDate = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(new Date());

        // Find the TextView by its ID and set the date
        TextView dateTextView = findViewById(R.id.date);
        dateTextView.setText(currentDate);

        // Initialize the back button and set the click listener
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Initialize the navigation button and set the click listener
        ImageButton navButton = findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });

        // Initialize level one button and set the click listener
        Button levelOneButton = findViewById(R.id.level_one);
        levelOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LevelOneActivity.class);
                startActivity(intent);
            }
        });

        // Initialize level two button and set the click listener
        Button levelTwoButton = findViewById(R.id.level_two);
        levelTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LevelTwoActivity.class);
                startActivity(intent);
            }
        });

        // Initialize level three button and set the click listener
        Button levelThreeButton = findViewById(R.id.level_three);
        levelThreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LevelThreeActivity.class);
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
