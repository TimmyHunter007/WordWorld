package com.example.wordworld;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LevelOneActivity extends AppCompatActivity {
    private EditText letter1, letter2, letter3, letter4;
    private TextView tvFeedBack, tvFeedBack1, tvFeedBack2, tvFeedBack3, tvFeedBack4;
    private TextView tvAttempts;
    private WordGame wordGame;
    private Button submitButton;
    private WordManagement wordManagement;
    private int feedbackIndex = 0;
    private FirebaseUser user;
    private RewardManager rewardManager;
    private DatabaseReference userDatabaseReference;
    private Button hintButton;

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
        hintButton = findViewById(R.id.hint_level_one);

        wordManagement = new WordManagement(this);
        wordGame = new WordGame(wordManagement);
        int level = 1;
        wordGame.startGame(level);

        // Initialize Firebase components
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        } else {
            // Handle the case where the user is not authenticated
            Log.e("LevelOneActivity", "User not authenticated");
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

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHintDialog();
//                final int hintCost = 5;  // Define the cost of a hint (e.g., 5 silver coins)
//
//                // Deduct coins for a hint
//                rewardManager.deductCoins(hintCost, new RewardManager.RewardCallback() {
//                    @Override
//                    public void onSuccess() {
//                        // If coins were successfully deducted, provide the hint
//                        WordGame.Hint hint = wordGame.giveHint();
//
//                        if (hint != null) {
//                            Log.d("HintDebug", "Hint message: " + hint.message);
//                            Log.d("HintDebug", "Revealed letter: " + hint.revealedLetter);
//                            Log.d("HintDebug", "Position: " + hint.position);
//
//                            if (hint.revealedLetter != null && hint.position >= 0 && hint.position < 4) {
//                                EditText[] letterBoxes = {letter1, letter2, letter3, letter4};
//                                letterBoxes[hint.position].setText(String.valueOf(hint.revealedLetter));
//
//                                // Provide feedback to the user
//                                Toast.makeText(LevelOneActivity.this, hint.message + " Letter: " + hint.revealedLetter, Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(LevelOneActivity.this, "Error: Invalid hint data.", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(LevelOneActivity.this, "Error: No hint available.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure() {
//                        // Handle case when there was an error deducting coins
//                        Toast.makeText(LevelOneActivity.this, "Error deducting coins. Try again.", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onInsufficientFunds() {
//                        // Inform the user they don't have enough coins
//                        Toast.makeText(LevelOneActivity.this, "Insufficient funds for a hint!", Toast.LENGTH_SHORT).show();
//                    }
//                });
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
        letter4.addTextChangedListener(new LetterTextWatcher(letter4, null, letter3));
    }

    private void showHintDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LevelOneActivity.this);
        builder.setTitle("Buy a Hint");

        builder.setMessage("Would you like to buy a hint?");


        builder.setPositiveButton("Buy Hint", (dialog, which) -> {
            final int hintCost = 5;  // Define the cost of a hint (e.g., 5 silver coins)

            // Deduct coins for a hint
            rewardManager.deductCoins(hintCost, new RewardManager.RewardCallback() {
                @Override
                public void onSuccess() {
                    // If coins were successfully deducted, provide the hint
                    WordGame.Hint hint = wordGame.giveHint();

                    if (hint != null) {
                        Log.d("HintDebug", "Hint message: " + hint.message);
                        Log.d("HintDebug", "Revealed letter: " + hint.revealedLetter);
                        Log.d("HintDebug", "Position: " + hint.position);

                        if (hint.revealedLetter != null && hint.position >= 0 && hint.position < 4) {
                            EditText[] letterBoxes = {letter1, letter2, letter3, letter4};
                            letterBoxes[hint.position].setText(String.valueOf(hint.revealedLetter));

                            // Provide feedback to the user
                            Toast.makeText(LevelOneActivity.this, hint.message + " Letter: " + hint.revealedLetter, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LevelOneActivity.this, "Error: Invalid hint data.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LevelOneActivity.this, "Error: No hint available.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure() {
                    // Handle case when there was an error deducting coins
                    Toast.makeText(LevelOneActivity.this, "Error deducting coins. Try again.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onInsufficientFunds() {
                    // Inform the user they don't have enough coins
                    Toast.makeText(LevelOneActivity.this, "Insufficient funds for a hint!", Toast.LENGTH_SHORT).show();
                }

            });


        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
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

        // Check if the game is over (either win or out of attempts)
        if (feedback.message.contains("Congratulations") || feedback.attemptsLeft <= 0) {
            // Disable further input
            enableLetters(false);

            // Show the message container and message
            RelativeLayout messageContainer = findViewById(R.id.message_container);
            TextView tvMessage = findViewById(R.id.tv_message);
            if (feedback.message.contains("Congratulations")) {
                int coinsEarned = rewardManager.awardLevelCompletionReward(1);
                int pointsEarned = rewardManager.getPointsEarned(1);
                rewardManager.getWordCount(1);
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
            tvFeedBack.setVisibility(View.GONE);
            tvFeedBack1.setVisibility(View.GONE);
            tvFeedBack2.setVisibility(View.GONE);
            tvFeedBack3.setVisibility(View.GONE);
            tvFeedBack4.setVisibility(View.GONE);
            tvAttempts.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
            hintButton.setVisibility(View.GONE);

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

    // TextWatcher class to move focus to the next EditText and handle backspace navigation
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
            if (s.length() == 1) {
                if (nextEditText != null) {
                    nextEditText.requestFocus();
                } else {
                    currentEditText.clearFocus(); // Clear focus on the last EditText

                    // Hide the keyboard when the last letter is entered
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentEditText.getWindowToken(), 0);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // No action needed here
        }


    }
}
