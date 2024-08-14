package com.example.wordworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private EditText etGuess;
    private TextView tvFeedBack;
    private TextView tvAttempts;

    private String chosenWord;
    private int attempts = 5;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        // Get current date
        String currentDate = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(new Date());

        // Fine the TextView by its ID
        TextView dateTextView = findViewById(R.id.date);
        dateTextView.setText(currentDate);

        // Initialize the back button and set the click listener
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button action
                onBackPressed();
            }
        });

        // Initialize the navigation button and set the click listener
        ImageButton navButton = findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle navigation button action
                // Open navigation drawer or other action
            }
        });

        // Initialize level one button and set the click listener
        Button levelOneButton = findViewById(R.id.level_one);
        levelOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LevelOneActivity.class);
                startActivity(intent);
                chosenWord = WordGame.getRandomWord(WordGame.diffOneWords);
                startGame();
            }

        });

        // Initialize level two button and set the click listener
        Button levelTwoButton = findViewById(R.id.level_two);
        levelTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LevelTwoActivity.class);
                startActivity(intent);
                chosenWord = WordGame.getRandomWord(WordGame.diffTwoWords);
                startGame();
            }
        });

        // Initialize level three button and set the click listener
        Button levelThreeButton = findViewById(R.id.level_three);
        levelThreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LevelThreeActivity.class);
                startActivity(intent);
                chosenWord = WordGame.getRandomWord(WordGame.diffThreeWords);
                startGame();
            }
        });

        // Initialize the submit button
        submitButton = findViewById(R.id.submit_level_one);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add submit button logic here
                handleGuess();
            }
        });
    }

    private void startGame(){
        attempts = 5;
        tvFeedBack.setText("");
        tvAttempts.setText("Attempts Left: " + attempts);
        etGuess.setText("");
        etGuess.setEnabled(true);
    }

    private void handleGuess() {
        String userGuess = etGuess.getText().toString().trim();

        if (userGuess.length() != chosenWord.length()) {
            tvFeedBack.setText("Your guess must be " + chosenWord.length() + " letters long.");
            return;
        }

        String feedback = WordGame.gamePlay(chosenWord, userGuess);
        tvFeedBack.setText(feedback);

        if (feedback.equalsIgnoreCase(chosenWord)) {
            tvFeedBack.setText("Congratulations! You've guessed the word: " + chosenWord);
            etGuess.setEnabled(false); // Disable further input
        } else {
            attempts--;
            tvAttempts.setText("Attempts left: " + attempts);

            if (attempts <= 0) {
                tvFeedBack.setText("Sorry, you've run out of attempts. The word was: " + chosenWord);
                etGuess.setEnabled(false); // Disable further input
            }
        }

        etGuess.setText(""); // Clear the input field after each guess
    }


}