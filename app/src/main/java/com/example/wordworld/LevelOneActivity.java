package com.example.wordworld;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
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

public class LevelOneActivity extends AppCompatActivity {
    private EditText[][] letterBoxes;
    private EditText activeEditText;
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
        setContentView(R.layout.activity_level_one);

        // Initialize UI components
        submitButton = findViewById(R.id.submit_level_one);

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

        //initialize letter boxes
        initializeLetterBoxes();
        initializeCustomKeyboard();

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

    @SuppressLint("ClickableViewAccessibility")
    private void initializeLetterBoxes() {
        letterBoxes = new EditText[5][4];

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 4; col++) {
                String editTextId = "letter" + (col + 1) + "_row" + (row + 1);
                @SuppressLint("DiscouragedApi") int resID = getResources().getIdentifier(editTextId, "id", getPackageName());
                letterBoxes[row][col] = findViewById(resID);

                // Ensure focusable and custom keyboard works
                letterBoxes[row][col].setFocusable(true);
                letterBoxes[row][col].setFocusableInTouchMode(true);
                letterBoxes[row][col].setCursorVisible(true);
                // Prevent system keyboard
                letterBoxes[row][col].setShowSoftInputOnFocus(false);

                // Add TextWatcher to automatically move to the next box after typing a character
                final int currentRow = row;
                final int currentCol = col;

                letterBoxes[row][col].addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.length() == 1) {
                            moveToNextBox(currentRow, currentCol);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        }
    }

    private void moveToNextBox(int row, int col) {
        if (col < 3) {
            // Move to the next column in the same row
            letterBoxes[row][col + 1].requestFocus();
        } else if (row < 4) {
            // Move to the first column in the next row
            letterBoxes[row + 1][0].requestFocus();
        }
    }

    // Initialize the custom keyboard buttons
    private void initializeCustomKeyboard() {
        findViewById(R.id.key_q).setOnClickListener(v -> onKeyClick("Q"));
        findViewById(R.id.key_w).setOnClickListener(v -> onKeyClick("W"));
        findViewById(R.id.key_e).setOnClickListener(v -> onKeyClick("E"));
        findViewById(R.id.key_r).setOnClickListener(v -> onKeyClick("R"));
        findViewById(R.id.key_t).setOnClickListener(v -> onKeyClick("T"));
        findViewById(R.id.key_y).setOnClickListener(v -> onKeyClick("Y"));
        findViewById(R.id.key_u).setOnClickListener(v -> onKeyClick("U"));
        findViewById(R.id.key_i).setOnClickListener(v -> onKeyClick("I"));
        findViewById(R.id.key_o).setOnClickListener(v -> onKeyClick("O"));
        findViewById(R.id.key_p).setOnClickListener(v -> onKeyClick("P"));

        findViewById(R.id.key_a).setOnClickListener(v -> onKeyClick("A"));
        findViewById(R.id.key_s).setOnClickListener(v -> onKeyClick("S"));
        findViewById(R.id.key_d).setOnClickListener(v -> onKeyClick("D"));
        findViewById(R.id.key_f).setOnClickListener(v -> onKeyClick("F"));
        findViewById(R.id.key_g).setOnClickListener(v -> onKeyClick("G"));
        findViewById(R.id.key_h).setOnClickListener(v -> onKeyClick("H"));
        findViewById(R.id.key_j).setOnClickListener(v -> onKeyClick("J"));
        findViewById(R.id.key_k).setOnClickListener(v -> onKeyClick("K"));
        findViewById(R.id.key_l).setOnClickListener(v -> onKeyClick("L"));

        findViewById(R.id.key_z).setOnClickListener(v -> onKeyClick("Z"));
        findViewById(R.id.key_x).setOnClickListener(v -> onKeyClick("X"));
        findViewById(R.id.key_c).setOnClickListener(v -> onKeyClick("C"));
        findViewById(R.id.key_v).setOnClickListener(v -> onKeyClick("V"));
        findViewById(R.id.key_b).setOnClickListener(v -> onKeyClick("B"));
        findViewById(R.id.key_n).setOnClickListener(v -> onKeyClick("N"));
        findViewById(R.id.key_m).setOnClickListener(v -> onKeyClick("M"));

        findViewById(R.id.key_back_space).setOnClickListener(v -> onBackspaceClick());
    }

    //insert letter into the active EditText
    private void onKeyClick(String letter) {
        if (activeEditText != null) {
            activeEditText.setText(letter); // Set the letter in the active EditText
            moveToNextBox(getRowOf(activeEditText), getColOf(activeEditText)); // Move to next box
        }
    }

    private void onBackspaceClick() {
        if (activeEditText != null) {
            int row = getRowOf(activeEditText);
            int col = getColOf(activeEditText);

            if (activeEditText.getText().length() > 0) {
                activeEditText.setText(""); // Clear the current box
            } else if (col > 0) {
                //letterBoxes[row][col - 1].setText(""); // Move to the previous box and clear
                letterBoxes[row][col - 1].requestFocus();
            } else if (row > 0) {
                //letterBoxes[row - 1][3].setText(""); // Move to the last box in the previous row and clear
                letterBoxes[row - 1][3].requestFocus();
            }
        }
    }

    private int getRowOf(EditText editText){
        for(int row = 0; row < letterBoxes.length; row++) {
            for(int col = 0; col < letterBoxes[row].length; col++) {
                if(letterBoxes[row][col] == editText){
                    return row;
                }
            }
        }
        return -1;
    }

    private int getColOf(EditText editText){
        for(int row = 0; row < letterBoxes.length; row++) {
            for(int col = 0; col < letterBoxes[row].length; col++) {
                if(letterBoxes[row][col] == editText){
                    return col;
                }
            }
        }
        return -1;
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

        //move to the next row
        currentRow++;

        // Check if the game is over (either win or out of attempts)
        if (feedback.message.contains("Congratulations") || feedback.attemptsLeft <= 0) {
            endGame(feedback);
        }
    }

    private String getUserInput() {
        StringBuilder userGuess = new StringBuilder();
        for (int col = 0; col < 4; col++) {
            userGuess.append(letterBoxes[currentRow][col].getText().toString().trim());
        }
        return userGuess.toString();
    }

    private void setColoredFeedback(int row, char[] feedbackChars, int[] feedbackStatus) {
        for (int col = 0; col < 4; col++) {
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
            // Award the user for completing Level 1
            int coinsEarned = rewardManager.awardLevelCompletionReward(1); // Level 1
            int pointsEarned = rewardManager.getPointsEarned(1);
            rewardManager.getWordCount(1);

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