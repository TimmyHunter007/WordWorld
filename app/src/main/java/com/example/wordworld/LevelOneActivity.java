package com.example.wordworld;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LevelOneActivity extends AppCompatActivity {
    private EditText letter1, letter2, letter3, letter4;
    private TextView tvFeedBack;
    private TextView tvAttempts;

    private String chosenWord;
    private int attempts = 5;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_level_one);

        // Initialize UI components
        letter1 = findViewById(R.id.letter1);
        letter2 = findViewById(R.id.letter2);
        letter3 = findViewById(R.id.letter3);
        letter4 = findViewById(R.id.letter4);
        tvFeedBack = findViewById(R.id.tv_feedback);
        tvAttempts = findViewById(R.id.tv_attempts);
        submitButton = findViewById(R.id.submit_level_one);

        // Set up the game
        chosenWord = WordGame.getRandomWord(WordGame.diffOneWords);
        startGame();

        // Set up the submit button listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleGuess();
            }
        });

        // Initialize the back button and set the click listener
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button action
                onBackPressed();
            }
        });
    }

    private void startGame() {
        attempts = 5;
        tvFeedBack.setText("");
        tvAttempts.setText("Attempts Left: " + attempts);
        clearLetters();
        enableLetters(true);
    }

    private void handleGuess() {
        String userGuess = getUserInput();

        if (userGuess.length() != chosenWord.length()) {
            tvFeedBack.setText("Your guess must be " + chosenWord.length() + " letters long.");
            return;
        }

        String feedback = WordGame.gamePlay(chosenWord, userGuess);
        tvFeedBack.setText(feedback);

        if (feedback.equalsIgnoreCase(chosenWord)) {
            tvFeedBack.setText("Congratulations! You've guessed the word: " + chosenWord);
            enableLetters(false); // Disable further input
        } else {
            attempts--;
            tvAttempts.setText("Attempts left: " + attempts);

            if (attempts <= 0) {
                tvFeedBack.setText("Sorry, you've run out of attempts. The word was: " + chosenWord);
                enableLetters(false); // Disable further input
            }
        }

        clearLetters(); // Clear the input fields after each guess
    }

    private String getUserInput() {
        return letter1.getText().toString().trim() +
                letter2.getText().toString().trim() +
                letter3.getText().toString().trim() +
                letter4.getText().toString().trim();
    }

    private void clearLetters() {
        letter1.setText("");
        letter2.setText("");
        letter3.setText("");
        letter4.setText("");
    }

    private void enableLetters(boolean enabled) {
        letter1.setEnabled(enabled);
        letter2.setEnabled(enabled);
        letter3.setEnabled(enabled);
        letter4.setEnabled(enabled);
    }
}
