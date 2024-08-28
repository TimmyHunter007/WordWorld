package com.example.wordworld;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;

public class LevelThreeActivity extends AppCompatActivity {
    private EditText letter1, letter2, letter3, letter4, letter5, letter6;
    private TextView tvFeedBack, tvFeedBack1, tvFeedBack2, tvFeedBack3, tvFeedBack4;
    private TextView tvAttempts;
    private WordGame wordGame;
    private Button submitButton;
    private WordManagement wordManagement;
    private int feedbackIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_level_three);

        // Initialize UI components
        letter1 = findViewById(R.id.letter1);
        letter2 = findViewById(R.id.letter2);
        letter3 = findViewById(R.id.letter3);
        letter4 = findViewById(R.id.letter4);
        letter5 = findViewById(R.id.letter5);
        letter6 = findViewById(R.id.letter6);
        tvFeedBack = findViewById(R.id.tv_feedback);
        tvFeedBack1 = findViewById(R.id.tv_feedback1);
        tvFeedBack2 = findViewById(R.id.tv_feedback2);
        tvFeedBack3 = findViewById(R.id.tv_feedback3);
        tvFeedBack4 = findViewById(R.id.tv_feedback4);
        tvAttempts = findViewById(R.id.tv_attempts);
        submitButton = findViewById(R.id.submit_level_three);

        wordManagement = new WordManagement(this);
        wordGame = new WordGame(wordManagement);
        int level = 3;
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
        letter1.addTextChangedListener(new LevelThreeActivity.LetterTextWatcher(letter1, letter2));
        letter2.addTextChangedListener(new LevelThreeActivity.LetterTextWatcher(letter2, letter3));
        letter3.addTextChangedListener(new LevelThreeActivity.LetterTextWatcher(letter3, letter4));
        letter4.addTextChangedListener(new LevelThreeActivity.LetterTextWatcher(letter4, letter5));
        letter5.addTextChangedListener(new LevelThreeActivity.LetterTextWatcher(letter5, letter6));
        letter6.addTextChangedListener(new LevelThreeActivity.LetterTextWatcher(letter6, null));
    }

    private void handleGuess() {
        String userGuess = getUserInput();

        // Check if the guess has the correct number of characters
        if (userGuess.length() != wordGame.chosenWord.length()) {
            // Display a message to the user
            Toast.makeText(this, "Your guess must be " + wordGame.chosenWord.length() + " letters long.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get feedback from the WordGame class if length check is good
        WordGame.Feedback feedback = wordGame.handleGuess(userGuess);
        //tvFeedBack.setText(feedback.message);

        // Handle feedback display based on feedbackIndex
        switch (feedbackIndex) {
            case 0:
                setColoredFeedback(tvFeedBack, feedback.feedbackChars, feedback.feedbackStatus);
                break;
            case 1:
                setColoredFeedback(tvFeedBack1, feedback.feedbackChars, feedback.feedbackStatus);
                break;
            case 2:
                setColoredFeedback(tvFeedBack2, feedback.feedbackChars, feedback.feedbackStatus);
                break;
            case 3:
                setColoredFeedback(tvFeedBack3, feedback.feedbackChars, feedback.feedbackStatus);
                break;
            case 4:
                setColoredFeedback(tvFeedBack4, feedback.feedbackChars, feedback.feedbackStatus);
                break;
        }

        // Increment feedbackIndex and check if the game should end
        feedbackIndex++;

        // Hide the keyboard when user hits submit
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(submitButton.getWindowToken(), 0);

        //update attempts
        tvAttempts.setText("Attempts Left: " + feedback.attemptsLeft);

        // Check if the game is over and disable inputs if necessary
        if (feedback.message.contains("Congratulations") || feedback.message.contains("Sorry")) {
            // Disable further input
            enableLetters(false);

            // Show the message container and message
            RelativeLayout messageContainer = findViewById(R.id.message_container);
            TextView tvMessage = findViewById(R.id.tv_message);
            tvMessage.setText(feedback.message);
            messageContainer.setVisibility(View.VISIBLE);

            //hide all boxes so the win/loss message is the only thing that shows
            letter1.setVisibility(View.GONE);
            letter2.setVisibility(View.GONE);
            letter3.setVisibility(View.GONE);
            letter4.setVisibility(View.GONE);
            letter5.setVisibility(View.GONE);
            letter6.setVisibility(View.GONE);
            tvFeedBack.setVisibility(View.GONE);
            tvFeedBack1.setVisibility(View.GONE);
            tvFeedBack2.setVisibility(View.GONE);
            tvFeedBack3.setVisibility(View.GONE);
            tvFeedBack4.setVisibility(View.GONE);
            tvAttempts.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
        }

        clearLetters(); // Clear the input fields after each guess
    }

    private void setColoredFeedback(TextView textView, char[] feedbackChars, int[] feedbackStatus) {
        StringBuilder coloredText = new StringBuilder();
        for (int i = 0; i < feedbackChars.length; i++) {
            if (feedbackStatus[i] == 2) {
                // Green for correct position
                coloredText.append("<font color='#3CB371'>").append(feedbackChars[i]).append("</font>");
            } else if (feedbackStatus[i] == 1) {
                // Yellow for wrong position
                coloredText.append("<font color='#FFBF00'>").append(feedbackChars[i]).append("</font>");
            } else {
                // Default color for incorrect letters
                coloredText.append(feedbackChars[i]);
            }
        }
        textView.setText(android.text.Html.fromHtml(coloredText.toString()));
    }

    private String getUserInput() {
        return letter1.getText().toString().trim() +
                letter2.getText().toString().trim() +
                letter3.getText().toString().trim() +
                letter4.getText().toString().trim() +
                letter5.getText().toString().trim() +
                letter6.getText().toString().trim();
    }

    private void clearLetters() {
        letter1.setText("");
        letter2.setText("");
        letter3.setText("");
        letter4.setText("");
        letter5.setText("");
        letter6.setText("");
    }

    private void enableLetters(boolean enabled) {
        letter1.setEnabled(enabled);
        letter2.setEnabled(enabled);
        letter3.setEnabled(enabled);
        letter4.setEnabled(enabled);
        letter5.setEnabled(enabled);
        letter6.setEnabled(enabled);
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