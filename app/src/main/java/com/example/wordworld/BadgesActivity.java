package com.example.wordworld;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;


public class BadgesActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ProgressBar progressBar;
    //
    private ProgressBar progressBar1;
    private int progress1 = 0;
    private final int MaxProgressBar1 = 1;
    //
    private ProgressBar progressBar2;
    private int progress2 = 0;
    private final int MaxProgressBar2 = 1;
    //
    private ProgressBar progressBar3;
    private int progress3 = 0;
    private final int MaxProgressBar3 = 1;
    //
    private ProgressBar progressBar4;
    private int progress4 = 0;
    private final int MaxProgressBar4 = 1;
    //
    private ProgressBar progressBar5;
    private int progress5 = 0;
    private final int MaxProgressBar5 = 10;
    //
    private ProgressBar progressBar6;
    private int progress6 = 0;
    private final int MaxProgressBar6 = 20;
    //
    private ProgressBar progressBar7;
    private int progress7 = 0;
    private final int MaxProgressBar7 = 50;
    //
    private ProgressBar progressBar8;
    private int progress8 = 0;
    private final int MaxProgressBar8 = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        setContentView(R.layout.activity_badges);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        //initalized Each progress bar and max and progress
        progressBar1 = findViewById(R.id.progressBar);
        progressBar2 = findViewById(R.id.ProgressBar2);
        progressBar3 = findViewById(R.id.ProgressBar3);
        progressBar4 = findViewById(R.id.ProgressBar4);
        progressBar5 = findViewById(R.id.ProgressBar5);
        progressBar6 = findViewById(R.id.ProgressBar6);
        progressBar7 = findViewById(R.id.ProgressBar7);
        progressBar8 = findViewById(R.id.ProgressBar8);

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

    private void initializeProgressBars()
    {
        if(progressBar1 != null)
        {
            progressBar1.setMax(MaxProgressBar1);
            progressBar1.setProgress(progress1);
        }
        if(progressBar2 != null)
        {
            progressBar2.setMax(MaxProgressBar2);
            progressBar2.setProgress(progress2);
        }
        if(progressBar3 != null)
        {
            progressBar3.setMax(MaxProgressBar3);
            progressBar3.setProgress(progress3);

        }
        if(progressBar4 != null)
        {
            progressBar4.setMax(MaxProgressBar4);
            progressBar4.setProgress(progress4);
        }
        if(progressBar5 != null)
        {
            progressBar5.setMax(MaxProgressBar5);
            progressBar5.setProgress(progress5);
        }
        if(progressBar6 != null)
        {
            progressBar6.setMax(MaxProgressBar6);
            progressBar6.setProgress(progress6);
        }
        if(progressBar7 != null)
        {
            progressBar7.setMax(MaxProgressBar7);
            progressBar7.setProgress(progress7);
        }
        if(progressBar8 != null)
        {
            progressBar8.setMax(MaxProgressBar8);
            progressBar8.setProgress(progress8);
        }
    }

    private void CompleteWord()
    {
        if(progress1 < MaxProgressBar1)
        {
            progress1++;
            progressBar1.setProgress(progress1);
            showBadgeEarned("First word Level 1!!");
        }
        if(progress2 < MaxProgressBar2 && progress1 >= MaxProgressBar1)
        {
            progress2++;
            progressBar2.setProgress(progress2);
            showBadgeEarned("First word Level 2!!");
        }
        if(progress3 < MaxProgressBar3 && progress2 >= MaxProgressBar2)
        {
            progress3++;
            progressBar3.setProgress(progress3);
            showBadgeEarned("First word Level 3!!");
        }
        if(progress4 < MaxProgressBar4)
        {
            progress4++;
            progressBar4.setProgress(progress4);
            showBadgeEarned("Daily Streak!!");
        }
        if(progress5 < MaxProgressBar5)
        {
            progress5++;
            progressBar5.setProgress(progress5);
            if(progress5 == MaxProgressBar5)
            {
                showBadgeEarned("10 Word Streak");
            }
        }
        if(progress6 < MaxProgressBar6)
        {
            progress6++;
            progressBar6.setProgress(progress6);
            if(progress6 == MaxProgressBar6)
            {
                showBadgeEarned("20 Word Streak");
            }
        }
        if(progress7 < MaxProgressBar7)
        {
            progress7++;
            progressBar7.setProgress(progress7);
            if(progress7 == MaxProgressBar7)
            {
                showBadgeEarned("50 Word Streak");
            }
        }
        if(progress8 < MaxProgressBar8)
        {
            progress8++;
            progressBar8.setProgress(progress8);
            if(progress8 == MaxProgressBar8)
            {
                showBadgeEarned("100 Word Streak");
            }
        }
    }
    // Come back too double check work
    private void showBadgeEarned(String s)
    {
        Toast.makeText(this,"you Have earned "+s,Toast.LENGTH_SHORT).show();
    }
    //This wil show the Badge Once Earned On the screen and for a Duration of time
    private void Badge(ImageView BadgeView)
    {
        Animation Animate = new AlphaAnimation(0.0f , 1.0f);
        Animate.setDuration(500);
        Animate.setRepeatCount(1);
        Animate.setRepeatMode(Animation.REVERSE);
        BadgeView.startAnimation(Animate);
    }



}
