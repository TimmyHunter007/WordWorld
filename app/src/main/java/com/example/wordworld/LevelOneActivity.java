package com.example.wordworld;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.wordworld.WordManagement;

public class LevelOneActivity extends AppCompatActivity {
    private EditText letter1, letter2, letter3, letter4;
    private TextView tvFeedBack, tvFeedBack1, tvFeedBack2, tvFeedBack3, tvFeedBack4;
    private TextView tvAttempts;
    private WordGame wordGame;
    private Button submitButton;
    //private WordGame wordGames;
    private WordManagement wordManagement;
    private int feedbackIndex = 0;


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
        tvFeedBack1 = findViewById(R.id.tv_feedback1);
        tvFeedBack2 = findViewById(R.id.tv_feedback2);
        tvFeedBack3 = findViewById(R.id.tv_feedback3);
        tvFeedBack4 = findViewById(R.id.tv_feedback4);
        tvAttempts = findViewById(R.id.tv_attempts);
        submitButton = findViewById(R.id.submit_level_one);

        wordManagement = new WordManagement(this);
        wordGame = new WordGame(wordManagement);
        int level = 1;
        wordGame.startGame(level);

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

        // Add TextWatcher to move to the next EditText
        letter1.addTextChangedListener(new LetterTextWatcher(letter1, letter2));
        letter2.addTextChangedListener(new LetterTextWatcher(letter2, letter3));
        letter3.addTextChangedListener(new LetterTextWatcher(letter3, letter4));
        letter4.addTextChangedListener(new LetterTextWatcher(letter4, null));
    }

    private void handleGuess() {
        String userGuess = getUserInput();

        // Get feedback from the WordGame class
        WordGame.Feedback feedback = wordGame.handleGuess(userGuess);
        tvFeedBack.setText(feedback.message);

        //display previous user guesses in tvfeedback boxes
        if (feedbackIndex == 0) {
            tvFeedBack1.setText(userGuess);
        }else if(feedbackIndex == 1) {
            tvFeedBack2.setText(userGuess);
        }else if(feedbackIndex == 2) {
            tvFeedBack3.setText(userGuess);
        }else if(feedbackIndex == 3) {
            tvFeedBack4.setText(userGuess);
        }
        feedbackIndex++;

        //update attempts
        tvAttempts.setText("Attempts Left: " + feedback.attemptsLeft);

        // Check if the game is over and disable inputs if necessary
        if (feedback.message.contains("Congratulations") || feedback.message.contains("Sorry")) {
            enableLetters(false); // Disable further input
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

    // TextWatcher class to move focus to the next EditText
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
            if (s.length() == 1) {
                if (nextEditText != null) {
                    nextEditText.requestFocus();
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
