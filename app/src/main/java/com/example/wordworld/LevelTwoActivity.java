package com.example.wordworld;

import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.graphics.Color;

public class LevelTwoActivity extends AppCompatActivity {
    private EditText[][] letterBoxes;
    private WordGame wordGame;
    private Button submitButton;
    private WordManagement wordManagement;
    private int currentRow = 0;
    private FirebaseUser user;
    private RewardManager rewardManager;
    private DatabaseReference userDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_level_two);

        // Initialize UI components
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

        //initialize letter boxes
        initializeLetterBoxes();

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

    private void initializeLetterBoxes() {
        // Initialize the 5 rows and 5 columns for level two
        letterBoxes = new EditText[5][5];

        // Loop through rows and columns to find the EditText IDs dynamically
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                String editTextId = "letter" + (col + 1) + "_row" + (row + 1);  // IDs like letter1_row1, letter2_row1, etc.
                int resID = getResources().getIdentifier(editTextId, "id", getPackageName());
                letterBoxes[row][col] = findViewById(resID);
            }
        }
    }

    private void handleGuess() {
        String userGuess = getUserInput();

        // message notifying the user that the submission was too short
        if (userGuess.length() != wordGame.chosenWord.length()) {
            // Display a message to the user
            Toast.makeText(this, "Your guess must be " + wordGame.chosenWord.length() +
                    " letters long.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get feedback from the WordGame class
        WordGame.Feedback feedback = wordGame.handleGuess(userGuess);

        // Set colored feedback for the current row
        setColoredFeedback(currentRow, feedback.feedbackChars, feedback.feedbackStatus);

        // move to the next row
        currentRow++;

        // Check if the game is over (either win or out of attempts)
        if (feedback.message.contains("Congratulations") || feedback.attemptsLeft <= 0) {
            endGame(feedback);
        }
    }

    private String getUserInput() {
        StringBuilder userGuess = new StringBuilder();
        for (int col = 0; col < 5; col++) {
            userGuess.append(letterBoxes[currentRow][col].getText().toString().trim());
        }
        return userGuess.toString();
    }

    private void setColoredFeedback(int row, char[] feedbackChars, int[] feedbackStatus) {
        for (int col = 0; col < 5; col++) {
            String color;
            if (feedbackStatus[col] == 2) {
                // Green for correct position
                color = "#3CB371";
            } else if (feedbackStatus[col] == 1) {
                // Yellow for wrong position
                color = "#FFBF00";
            } else {
                // Default color for incorrect letters
                color = "#000000";
            }
            letterBoxes[row][col].setTextColor(Color.parseColor(color));
            letterBoxes[row][col].setText(String.valueOf(feedbackChars[col]));
        }
    }

    private void endGame(WordGame.Feedback feedback) {
        // Disable further input
        submitButton.setEnabled(false);

        // Show a message to the user
        RelativeLayout messageContainer = findViewById(R.id.message_container);
        TextView tvMessage = findViewById(R.id.tv_message);

        // Check if the user has won
        if (feedback.message.contains("Congratulations")) {
            // Award the user for completing Level 2
            int coinsEarned = rewardManager.awardLevelCompletionReward(2); // Level 2
            int pointsEarned = rewardManager.getPointsEarned(2);
            rewardManager.getWordCount(2);

            // Display the success message with rewards earned
            tvMessage.setText(feedback.message + "\n\nYou earned:\n" + coinsEarned + " Silver Coins\n" + pointsEarned + " Points");
        } else {
            // Display failure message (if no rewards are earned)
            tvMessage.setText(feedback.message);
        }

        // Show the message container with the result
        messageContainer.setVisibility(View.VISIBLE);
    }
}