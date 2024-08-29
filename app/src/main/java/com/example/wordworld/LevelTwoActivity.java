package com.example.wordworld;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LevelTwoActivity extends AppCompatActivity {
    private EditText letter1, letter2, letter3, letter4, letter5;
    private TextView tvFeedBack, tvFeedBack1, tvFeedBack2, tvFeedBack3, tvFeedBack4;
    private TextView tvAttempts;
    private WordGame wordGame;
    private Button submitButton;
    private WordManagement wordManagement;
    private int feedbackIndex = 0;
    private FirebaseUser user;
    private RewardManager rewardManager;
    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_level_two);

        // Initialize UI components
        letter1 = findViewById(R.id.letter1);
        letter2 = findViewById(R.id.letter2);
        letter3 = findViewById(R.id.letter3);
        letter4 = findViewById(R.id.letter4);
        letter5 = findViewById(R.id.letter5);
        tvFeedBack = findViewById(R.id.tv_feedback);
        tvFeedBack1 = findViewById(R.id.tv_feedback1);
        tvFeedBack2 = findViewById(R.id.tv_feedback2);
        tvFeedBack3 = findViewById(R.id.tv_feedback3);
        tvFeedBack4 = findViewById(R.id.tv_feedback4);
        tvAttempts = findViewById(R.id.tv_attempts);
        submitButton = findViewById(R.id.submit_level_two);

        wordManagement = new WordManagement(this);
        wordGame = new WordGame(wordManagement);
        int level = 2;
        wordGame.startGame(level);

        // Initialize Firebase components
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        } else {
            // Handle the case where the user is not authenticated
            Log.e("LevelTwoActivity", "User not authenticated");
        }

        // Initialize RewardManager with the correct DatabaseReference
        if (userDatabaseReference != null) {
            rewardManager = new RewardManager(userDatabaseReference);
        }

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

        // Add TextWatcher to move to the next EditText and handle backspace navigation
        letter1.addTextChangedListener(new LetterTextWatcher(letter1, letter2, null));
        letter2.addTextChangedListener(new LetterTextWatcher(letter2, letter3, letter1));
        letter3.addTextChangedListener(new LetterTextWatcher(letter3, letter4, letter2));
        letter4.addTextChangedListener(new LetterTextWatcher(letter4, letter5, letter3));
        letter5.addTextChangedListener(new LetterTextWatcher(letter5, null, letter4));
    }

    private void handleGuess() {
        String userGuess = getUserInput();

        // message notifying the user that the submission was too short
        if(userGuess.length() != wordGame.chosenWord.length()){
            // Display a message to the user
            Toast.makeText(this, "Your guess must be " + wordGame.chosenWord.length() +
                    " letters long.", Toast.LENGTH_SHORT).show();
            // Hide the keyboard when user hits submit
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(submitButton.getWindowToken(), 0);
            return;
        }

        // Get feedback from the WordGame class
        WordGame.Feedback feedback = wordGame.handleGuess(userGuess);

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

        // Update attempts
        tvAttempts.setText("Attempts Left: " + feedback.attemptsLeft);

        // Check if the game is over and disable inputs if necessary
        if (feedback.message.contains("Congratulations") || feedback.attemptsLeft <= 0) {
            // Disable further input
            enableLetters(false);

            // Show the message container and message
            RelativeLayout messageContainer = findViewById(R.id.message_container);
            TextView tvMessage = findViewById(R.id.tv_message);
            if (feedback.message.contains("Congratulations")) {
                int coinsEarned = rewardManager.awardLevelCompletionReward(2); // Use correct level
                int pointsEarned = rewardManager.getPointsEarned(2); // Use correct level
                tvMessage.setText(feedback.message + "\n\nYou earned:\n" + coinsEarned + " Silver Coins\n" + pointsEarned + " Points");
            } else {
                tvMessage.setText(feedback.message);
            }
            messageContainer.setVisibility(View.VISIBLE);

            // Hide all boxes so the win/loss message is the only thing that shows
            letter1.setVisibility(View.GONE);
            letter2.setVisibility(View.GONE);
            letter3.setVisibility(View.GONE);
            letter4.setVisibility(View.GONE);
            letter5.setVisibility(View.GONE);
            tvFeedBack.setVisibility(View.GONE);
            tvFeedBack1.setVisibility(View.GONE);
            tvFeedBack2.setVisibility(View.GONE);
            tvFeedBack3.setVisibility(View.GONE);
            tvFeedBack4.setVisibility(View.GONE);
            tvAttempts.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
        }

        // Clear the input fields after each guess
        clearLetters();
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
                letter5.getText().toString().trim();
    }

    private void clearLetters() {
        letter1.setText("");
        letter2.setText("");
        letter3.setText("");
        letter4.setText("");
        letter5.setText("");
    }

    private void enableLetters(boolean enabled) {
        letter1.setEnabled(enabled);
        letter2.setEnabled(enabled);
        letter3.setEnabled(enabled);
        letter4.setEnabled(enabled);
        letter5.setEnabled(enabled);
    }

    // TextWatcher class to move focus to the next EditText
    private class LetterTextWatcher implements TextWatcher {
        private final EditText currentEditText;
        private final EditText nextEditText;
        private final EditText prevEditText;

        public LetterTextWatcher(EditText currentEditText, EditText nextEditText, EditText prevEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
            this.prevEditText = prevEditText;

            // Add a listener for detecting backspace
            this.currentEditText.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                        keyCode == android.view.KeyEvent.KEYCODE_DEL) {

                    // Check if currentEditText is empty and move to the previous EditText
                    if (currentEditText.getText().toString().isEmpty() && prevEditText != null) {
                        prevEditText.requestFocus();
                        prevEditText.setText("");  // Clear the previous EditText
                        prevEditText.setSelection(prevEditText.getText().length());  // Place cursor at the end
                        return true;
                    }
                }
                return false;
            });
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // No action needed here
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1 && nextEditText != null) {
                nextEditText.requestFocus();
            } else if (s.length() == 1 && nextEditText == null) {
                currentEditText.clearFocus(); // Clear focus on the last EditText

                // Hide the keyboard when the last letter is entered
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(currentEditText.getWindowToken(), 0);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // No action needed here
        }
    }
}