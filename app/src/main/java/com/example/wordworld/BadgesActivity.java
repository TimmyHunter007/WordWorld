package com.example.wordworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class BadgesActivity extends AppCompatActivity {
    private Button submitButton;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_badges);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        CompleteWord();

        // Initialize the back button and set the click listener
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button action
                onBackPressed();
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent;

                if (id == R.id.level_one) {
                    intent = new Intent(BadgesActivity.this, LevelOneActivity.class);
                    startActivity(intent);
                } else if (id == R.id.level_two) {
                    intent = new Intent(BadgesActivity.this, LevelTwoActivity.class);
                    startActivity(intent);
                } else if (id == R.id.level_three) {
                    intent = new Intent(BadgesActivity.this, LevelThreeActivity.class);
                    startActivity(intent);
                } else if (id == R.id.badges) {
                    intent = new Intent(BadgesActivity.this, BadgesActivity.class);
                    startActivity(intent);

                } else if (id == R.id.profile) {
                    intent = new Intent(BadgesActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else if (id == R.id.leader_board) {
                    intent = new Intent(BadgesActivity.this, LeaderBoardActivity.class);
                    startActivity(intent);
                }

                drawerLayout.closeDrawers(); // Close the drawer after an item is clicked
                return true;
            }
        });

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

    }

    private void CompleteWord()
    {

    }

    private void checkForbadge()
    {

    }

}
