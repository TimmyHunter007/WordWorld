package com.example.wordworld;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LevelOneActivity extends AppCompatActivity {
    // Declare UI components and game logic variables
    private EditText letter1, letter2, letter3, letter4;
    private TextView tvFeedBack;
    private TextView tvAttempts;
    private WordGame wordGame;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hide the action bar for a full-screen experience
        setContentView(R.layout.activity_level_one); // Set the layout for this activity

        // Initialize UI components by finding them in the layout
        letter1 = findViewById(R.id.letter1);
        letter2 = findViewById(R.id.letter2);
        letter3 = findViewById(R.id.letter3);
        letter4 = findViewById(R.id.letter4);
        tvFeedBack = findViewById(R.id.tv_feedback);
        tvAttempts = findViewById(R.id.tv_attempts);
        submitButton = findViewById(R.id.submit_level_one);

        // Set up the game with a predefined word list
        wordGame = new WordGame(WordGame.diffOneWords);
        wordGame.startGame(); // Start the game

        // Set up the submit button listener to handle user guesses
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleGuess(); // Handle the user's guess when the button is clicked
            }
        });

        // Initialize the back button and set the click listener to go back to the previous activity
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Handle back button action
            }
        });

        // Add TextWatchers to EditTexts to move focus automatically to the next EditText
        letter1.addTextChangedListener(new LetterTextWatcher(letter1, letter2));
        letter2.addTextChangedListener(new LetterTextWatcher(letter2, letter3));
        letter3.addTextChangedListener(new LetterTextWatcher(letter3, letter4));
        letter4.addTextChangedListener(new LetterTextWatcher(letter4, null)); // No next EditText for the last one
    }

    // Handle the user's guess and provide feedback
    private void handleGuess() {
        String userGuess = getUserInput(); // Get the user's input from the EditTexts

        // Get feedback from the WordGame class based on the user's guess
        WordGame.Feedback feedback = wordGame.handleGuess(userGuess);
        tvFeedBack.setText(feedback.message); // Display feedback message

        tvAttempts.setText("Attempts Left: " + feedback.attemptsLeft); // Update the attempts left

        // Check if the game is over based on the feedback and disable inputs if necessary
        if (feedback.message.contains("Congratulations") || feedback.message.contains("Sorry")) {
            enableLetters(false); // Disable further input if the game is over
        }

        clearLetters(); // Clear the input fields after each guess
    }

    // Get the user's input by concatenating the text from all EditTexts
    private String getUserInput() {
        return letter1.getText().toString().trim() +
                letter2.getText().toString().trim() +
                letter3.getText().toString().trim() +
                letter4.getText().toString().trim();
    }

    // Clear the input fields
    private void clearLetters() {
        letter1.setText("");
        letter2.setText("");
        letter3.setText("");
        letter4.setText("");
    }

    // Enable or disable the input fields
    private void enableLetters(boolean enabled) {
        letter1.setEnabled(enabled);
        letter2.setEnabled(enabled);
        letter3.setEnabled(enabled);
        letter4.setEnabled(enabled);
    }

    // Custom TextWatcher class to move focus to the next EditText when a letter is entered
    private class LetterTextWatcher implements TextWatcher {
        private final EditText currentEditText;
        private final EditText nextEditText;

        public LetterTextWatcher(EditText currentEditText, EditText nextEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // No action needed here
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Move focus to the next EditText if a letter is entered
            if (s.length() == 1) {
                if (nextEditText != null) {
                    nextEditText.requestFocus(); // Move to the next EditText
                } else {
                    currentEditText.clearFocus(); // Clear focus on the last EditText
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // No action needed here
        }
    }
}
