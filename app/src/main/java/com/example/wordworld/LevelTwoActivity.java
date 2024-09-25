package com.example.wordworld;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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

public class LevelTwoActivity extends AppCompatActivity {
    private EditText[][] letterBoxes;
    private EditText activeEditText;
    private WordGame wordGame;
    private Button submitButton;
    private WordManagement wordManagement;
    private  int currentRow = 0;
    private FirebaseUser user;
    private RewardManager rewardManager;
    private DatabaseReference userDatabaseReference;
    private Button hintButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_level_two);

         letterBoxes = new EditText[][]{
                {findViewById(R.id.letter1_row1), findViewById(R.id.letter2_row1), findViewById(R.id.letter3_row1), findViewById(R.id.letter4_row1), findViewById(R.id.letter5_row1)},
                {findViewById(R.id.letter1_row2), findViewById(R.id.letter2_row2), findViewById(R.id.letter3_row2), findViewById(R.id.letter4_row2), findViewById(R.id.letter5_row2)},
                {findViewById(R.id.letter1_row3), findViewById(R.id.letter2_row3), findViewById(R.id.letter3_row3), findViewById(R.id.letter4_row3), findViewById(R.id.letter5_row3)},
                {findViewById(R.id.letter1_row4), findViewById(R.id.letter2_row4), findViewById(R.id.letter3_row4), findViewById(R.id.letter4_row4), findViewById(R.id.letter5_row4)},
                {findViewById(R.id.letter1_row5), findViewById(R.id.letter2_row5), findViewById(R.id.letter3_row5), findViewById(R.id.letter4_row5), findViewById(R.id.letter5_row5)}
        };

        // Initialize UI components
        submitButton = findViewById(R.id.submit_level_two);
        hintButton = findViewById(R.id.hint_level_two);

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

        // Set up listeners for all letterBoxes
        setUpLetterBoxListeners(currentRow);

        // Initialize the keyboard listeners
        initializeKeyboardListeners();

        // Automatically focus the first text box
        letterBoxes[0][0].requestFocus();

        // Set up the submit button listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleGuess();
            }
        });

        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {showHintDialog();}
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

    private void showHintDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LevelTwoActivity.this);
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


                        // Ensure valid hint data
                        if (hint.revealedLetter != null && hint.position >= 0 && hint.position < letterBoxes[currentRow].length) {
                            // Loop through the current row and all rows below it
                            for (int row = currentRow; row < letterBoxes.length; row++) {
                                EditText letterBox = letterBoxes[row][hint.position];

                                // Set the revealed letter in the correct position of each row
                                letterBox.setText(String.valueOf(hint.revealedLetter));

                                // Lock the letter by disabling the EditText
                                letterBox.setEnabled(false);  // Disable interaction
                                letterBox.setFocusable(false);  // Prevent further focus on it
                                letterBox.setFocusableInTouchMode(false);  // Prevent touch focus
                                letterBox.setClickable(false);  // Prevent clicking on it


                                // Skip to the next letter box (if it exists)
                                if (hint.position < letterBoxes[currentRow].length - 1) {
                                    letterBoxes[currentRow][hint.position + 1].requestFocus();
                                }
                            }
                            // Reset the current row to start from the beginning
                            currentRow = 0;
                            letterBoxes[currentRow][0].requestFocus();


                            // Provide feedback to the user
                            Toast.makeText(LevelTwoActivity.this, hint.message + " Letter: " + hint.revealedLetter, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LevelTwoActivity.this, "Error: Invalid hint data.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LevelTwoActivity.this, "Error: No hint available.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure() {
                    // Handle case when there was an error deducting coins
                    Toast.makeText(LevelTwoActivity.this, "Error deducting coins. Try again.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onInsufficientFunds() {
                    // Inform the user they don't have enough coins
                    Toast.makeText(LevelTwoActivity.this, "Insufficient funds for a hint!", Toast.LENGTH_SHORT).show();
                }

            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpLetterBoxListeners(int row) {
        // Only add listeners to the current row (no need for other rows)
        for (int i = 0; i < letterBoxes[row].length; i++) {
            final EditText currentBox = letterBoxes[row][i];
            final int index = i;

            // Prevent the system keyboard from appearing
            currentBox.setOnTouchListener((v, event) -> {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                // Consume the touch event so the system keyboard doesn't appear
                return true;
            });

            // Set focus change listener to track active EditText
            currentBox.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    // Track active EditText
                    activeEditText = currentBox;
                }
            });

            // TextWatcher to move focus to the next box in the same row
            currentBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < letterBoxes[row].length - 1) {
                        letterBoxes[row][index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void initializeKeyboardListeners() {
        findViewById(R.id.key_q).setOnClickListener(v -> insertLetter("Q"));
        findViewById(R.id.key_w).setOnClickListener(v -> insertLetter("W"));
        findViewById(R.id.key_e).setOnClickListener(v -> insertLetter("E"));
        findViewById(R.id.key_r).setOnClickListener(v -> insertLetter("R"));
        findViewById(R.id.key_t).setOnClickListener(v -> insertLetter("T"));
        findViewById(R.id.key_y).setOnClickListener(v -> insertLetter("Y"));
        findViewById(R.id.key_u).setOnClickListener(v -> insertLetter("U"));
        findViewById(R.id.key_i).setOnClickListener(v -> insertLetter("I"));
        findViewById(R.id.key_o).setOnClickListener(v -> insertLetter("O"));
        findViewById(R.id.key_p).setOnClickListener(v -> insertLetter("P"));

        findViewById(R.id.key_a).setOnClickListener(v -> insertLetter("A"));
        findViewById(R.id.key_s).setOnClickListener(v -> insertLetter("S"));
        findViewById(R.id.key_d).setOnClickListener(v -> insertLetter("D"));
        findViewById(R.id.key_f).setOnClickListener(v -> insertLetter("F"));
        findViewById(R.id.key_g).setOnClickListener(v -> insertLetter("G"));
        findViewById(R.id.key_h).setOnClickListener(v -> insertLetter("H"));
        findViewById(R.id.key_j).setOnClickListener(v -> insertLetter("J"));
        findViewById(R.id.key_k).setOnClickListener(v -> insertLetter("K"));
        findViewById(R.id.key_l).setOnClickListener(v -> insertLetter("L"));

        findViewById(R.id.key_z).setOnClickListener(v -> insertLetter("Z"));
        findViewById(R.id.key_x).setOnClickListener(v -> insertLetter("X"));
        findViewById(R.id.key_c).setOnClickListener(v -> insertLetter("C"));
        findViewById(R.id.key_v).setOnClickListener(v -> insertLetter("V"));
        findViewById(R.id.key_b).setOnClickListener(v -> insertLetter("B"));
        findViewById(R.id.key_n).setOnClickListener(v -> insertLetter("N"));
        findViewById(R.id.key_m).setOnClickListener(v -> insertLetter("M"));

        findViewById(R.id.key_back_space).setOnClickListener(v -> deleteLastLetter());
    }

    private void insertLetter(String letter) {
        if (activeEditText != null && activeEditText.isEnabled()) {
            activeEditText.setText(letter);

            // Check if the next letter box is disabled (locked)
            int currentRowIndex = getActiveEditTextRowIndex();
            int currentColIndex = getActiveEditTextColIndex();

            if (currentColIndex < letterBoxes[currentRowIndex].length - 1) {
                EditText nextLetterBox = letterBoxes[currentRowIndex][currentColIndex + 1];
                if (!nextLetterBox.isEnabled()) {
                    // If locked, keep moving forward until an enabled box is found
                    while (currentColIndex < letterBoxes[currentRowIndex].length - 1) {
                        currentColIndex++;
                        nextLetterBox = letterBoxes[currentRowIndex][currentColIndex + 1];
                        if (nextLetterBox.isEnabled()) {
                            break; // Exit the loop if an enabled box is found
                        }
                    }

                    // Set the active EditText to the first enabled box
                    if (currentColIndex < letterBoxes[currentRowIndex].length - 1) {
                        activeEditText = nextLetterBox;
                        activeEditText.requestFocus();
                    }
                }
            }
        }
    }

    private void deleteLastLetter() {
        if (activeEditText != null) {
            int currentRowIndex = getActiveEditTextRowIndex();
            int currentColIndex = getActiveEditTextColIndex();

            if (currentRowIndex == currentRow) {
                if (activeEditText.getText().length() > 0) {
                    // Check if the previous EditText is disabled (locked)
                    if (currentColIndex > 0 && !letterBoxes[currentRowIndex][currentColIndex - 1].isEnabled()) {
                        // If the previous EditText is locked, do nothing
                        return;
                    }

                    // Otherwise, delete the last letter of the current EditText
                    activeEditText.setText("");
                } else {
                    // If we are not at the first EditText in the row, move to the previous EditText
                    if (currentColIndex > 0) {
                        letterBoxes[currentRow][currentColIndex - 1].requestFocus();
                        activeEditText = letterBoxes[currentRow][currentColIndex - 1];
                        activeEditText.setText("");  // Clear the previous EditText
                    }
                }
            }
        }
    }

    // Helper method to get the row index of the active EditText
    private int getActiveEditTextRowIndex() {
        for (int row = 0; row < letterBoxes.length; row++) {
            for (int col = 0; col < letterBoxes[row].length; col++) {
                if (letterBoxes[row][col].equals(activeEditText)) {
                    return row;
                }
            }
        }
        // Return -1 if no active row is found (this shouldn't happen)
        return -1;
    }

    private int getActiveEditTextColIndex() {
        for (int row = 0; row < letterBoxes.length; row++) {
            for (int col = 0; col < letterBoxes[row].length; col++) {
                if (letterBoxes[row][col].equals(activeEditText)) {
                    return col;
                }
            }
        }
        // Return -1 if not found
        return -1;
    }

    private void moveToPreviousEditText() {
        for (int row = 0; row < letterBoxes.length; row++) {
            for (int col = 0; col < letterBoxes[row].length; col++) {
                if (letterBoxes[row][col].equals(activeEditText)) {
                    if (col > 0) {
                        // Move to the previous box
                        letterBoxes[row][col - 1].requestFocus();
                        activeEditText = letterBoxes[row][col - 1];
                    } else if (row > 0) {
                        // Move to the last box in the previous row
                        letterBoxes[row - 1][letterBoxes[row - 1].length - 1].requestFocus();
                        activeEditText = letterBoxes[row - 1][letterBoxes[row - 1].length - 1];
                    }
                    return;
                }
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

        //text box color feedback
        displayFeedback(feedback);

        //disable the previous row once submit button has been clicked
        disableRow(currentRow);

        // Now, handle the end of the game if the player wins or is out of attempts
        if (feedback.message.contains("Congratulations") || feedback.attemptsLeft <= 0) {
            endGame(feedback); // Call endGame to finalize the game
        } else {
            // Move to the next row for another attempt
            if (currentRow < letterBoxes.length - 1) {
                // Increment the row
                currentRow++;
                // Set up listeners for the new row
                setUpLetterBoxListeners(currentRow);
                // Move the cursor to the first box in the new row
                letterBoxes[currentRow][0].requestFocus();
            } else {
                // If there are no more rows, disable input and show message
                endGame(feedback);
            }
        }
    }

    //disables all EditText boxes in a given row
    private void disableRow(int row) {
        for (EditText editText : letterBoxes[row]) {
            editText.setEnabled(false);
        }
    }

    private void setColoredFeedback(int row, char[] feedbackChars, int[] feedbackStatus) {
        for (int i = 0; i < feedbackChars.length; i++) {
            EditText letterBox = letterBoxes[row][i];
            if (feedbackStatus[i] == 2) {
                // Green
                //letterBox.setBackground(createColoredBackground(Color.parseColor("#3CB371")));
                letterBox.setBackground(createColoredBackground(Color.parseColor("#556B2F")));
            } else if (feedbackStatus[i] == 1) {
                // Yellow
                //letterBox.setBackground(createColoredBackground(Color.parseColor("#FFBF00")));
                letterBox.setBackground(createColoredBackground(Color.parseColor("#DAA520")));
            } else {
                // Default
                letterBox.setBackground(createColoredBackground(Color.parseColor("#4D000000")));
            }
            letterBox.setText(String.valueOf(feedbackChars[i]));
            // Add a black outline around the text using setShadowLayer
            letterBox.setShadowLayer(15f, 0f, 0f, Color.BLACK);
        }
    }

    private Drawable createColoredBackground(int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(32f);

        return drawable;
    }

    private String getUserInput() {
        StringBuilder guess = new StringBuilder();
        for (EditText box : letterBoxes[currentRow]) {
            guess.append(box.getText().toString());
        }
        return guess.toString();
    }

    private void displayFeedback(WordGame.Feedback feedback) {
        // Color feedback for the current row
        setColoredFeedback(currentRow, feedback.feedbackChars, feedback.feedbackStatus);
    }

    private void enableLetters(boolean enabled) {
        for (EditText[] row : letterBoxes) {
            for (EditText letterBox : row) {
                letterBox.setEnabled(enabled);
            }
        }
    }

    private void endGame(WordGame.Feedback feedback) {
        // Disable further input
        enableLetters(false);

        // Show the message container and message
        RelativeLayout messageContainer = findViewById(R.id.message_container);
        TextView tvMessage = findViewById(R.id.tv_message);

        if (feedback.message.contains("Congratulations")) {
            int coinsEarned = rewardManager.awardLevelCompletionReward(2);
            int pointsEarned = rewardManager.getPointsEarned(2);
            rewardManager.getWordCount(1);
            tvMessage.setText(feedback.message + "\n\nYou earned:\n" + coinsEarned + " Silver Coins\n" + pointsEarned + " Points");
        } else {
            tvMessage.setText(feedback.message);
        }

        //allow end message to display
        messageContainer.setVisibility(View.VISIBLE);

        // Hide game UI when displaying the end message
        findViewById(R.id.letterBoxesContainer).setVisibility(View.GONE);
        findViewById(R.id.keyboard).setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
        hintButton.setVisibility(View.GONE);
    }
}