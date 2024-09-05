package com.example.wordworld;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class LevelTwoActivity extends AppCompatActivity {

    private Button submitButton;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_level_two);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent;

                if (id == R.id.level_one) {
                    intent = new Intent(LevelTwoActivity.this, LevelOneActivity.class);
                    startActivity(intent);
                } else if (id == R.id.level_two) {
                    intent = new Intent(LevelTwoActivity.this, LevelTwoActivity.class);
                    startActivity(intent);
                } else if (id == R.id.level_three) {
                    intent = new Intent(LevelTwoActivity.this, LevelThreeActivity.class);
                    startActivity(intent);
                } else if (id == R.id.badges) {
                    // Navigate to Badges activity
                } else if (id == R.id.profile) {
                    // Navigate to Profile activity
                } else if (id == R.id.leader_board) {
                    // Navigate to Leaderboard activity
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
        // Initialize the back button and set the click listener
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Get references to the EditText fields
        final EditText letter1 = findViewById(R.id.letter1);
        final EditText letter2 = findViewById(R.id.letter2);
        final EditText letter3 = findViewById(R.id.letter3);
        final EditText letter4 = findViewById(R.id.letter4);
        final EditText letter5 = findViewById(R.id.letter5);

        // Set focus change listeners to highlight the currently selected box
        setFocusChangeListener(letter1);
        setFocusChangeListener(letter2);
        setFocusChangeListener(letter3);
        setFocusChangeListener(letter4);
        setFocusChangeListener(letter5);

        // Add TextWatchers to automatically move to the next box
        addTextWatcher(letter1, letter2);
        addTextWatcher(letter2, letter3);
        addTextWatcher(letter3, letter4);
        addTextWatcher(letter4, letter5);

        // Initialize the submit button
        submitButton = findViewById(R.id.submit_level_two);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add submit button logic here
            }
        });
    }

    private void addTextWatcher(final EditText currentEditText, final EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    nextEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setFocusChangeListener(final EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editText.setBackgroundResource(R.drawable.edittext_highlighted_level2);
                } else {
                    editText.setBackgroundResource(R.drawable.edittext_normal_level2);
                }
            }
        });
    }
}
