package com.example.wordworld;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.TextAppearanceInfo;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

public class LevelTwoActivity extends AppCompatActivity {
    private EditText[][] letterBoxes;
    private EditText activeEditText;
    private WordGame wordGame;
    private Button submitButton;
    private WordManagement wordManagement;
    private int currentRow = 0;
    private FirebaseUser user;
    private RewardManager rewardManager;
    private DatabaseReference userDatabaseReference;
    private int currentAttemptsLeft = 5;
    private int currentWordGuess = 0;
    private MediaPlayer mediaPlayer;
    private Map<Character, Button> keyButtons;

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

        // Initialize keyButtons map
        keyButtons = new HashMap<>();
        keyButtons.put('Q', (Button) findViewById(R.id.key_q));
        keyButtons.put('W', (Button) findViewById(R.id.key_w));
        keyButtons.put('E', (Button) findViewById(R.id.key_e));
        keyButtons.put('R', (Button) findViewById(R.id.key_r));
        keyButtons.put('T', (Button) findViewById(R.id.key_t));
        keyButtons.put('Y', (Button) findViewById(R.id.key_y));
        keyButtons.put('U', (Button) findViewById(R.id.key_u));
        keyButtons.put('I', (Button) findViewById(R.id.key_i));
        keyButtons.put('O', (Button) findViewById(R.id.key_o));
        keyButtons.put('P', (Button) findViewById(R.id.key_p));

        keyButtons.put('A', (Button) findViewById(R.id.key_a));
        keyButtons.put('S', (Button) findViewById(R.id.key_s));
        keyButtons.put('D', (Button) findViewById(R.id.key_d));
        keyButtons.put('F', (Button) findViewById(R.id.key_f));
        keyButtons.put('G', (Button) findViewById(R.id.key_g));
        keyButtons.put('H', (Button) findViewById(R.id.key_h));
        keyButtons.put('J', (Button) findViewById(R.id.key_j));
        keyButtons.put('K', (Button) findViewById(R.id.key_k));
        keyButtons.put('L', (Button) findViewById(R.id.key_l));

        keyButtons.put('Z', (Button) findViewById(R.id.key_z));
        keyButtons.put('X', (Button) findViewById(R.id.key_x));
        keyButtons.put('C', (Button) findViewById(R.id.key_c));
        keyButtons.put('V', (Button) findViewById(R.id.key_v));
        keyButtons.put('B', (Button) findViewById(R.id.key_b));
        keyButtons.put('N', (Button) findViewById(R.id.key_n));
        keyButtons.put('M', (Button) findViewById(R.id.key_m));

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
            initializeUserData();  // Fetch and initialize user data for attempts and guesses
        } else {
            // User is not logged in
            ImageButton backButton = findViewById(R.id.back_button);
            backButton.setOnClickListener(v -> onBackPressed());
            showLoginRequiredMessage();
            return;
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
        submitButton.setOnClickListener(v -> handleGuess());

        // Initialize the back button and set the click listener to navigate back to the previous activity
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());
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
        }
    }

    private void deleteLastLetter() {
        if (activeEditText != null) {
            int currentRowIndex = getActiveEditTextRowIndex();
            int currentColIndex = getActiveEditTextColIndex();

            if (currentRowIndex == currentRow) {
                if (activeEditText.getText().length() > 0) {
                    // Erase the current letter
                    activeEditText.setText("");
                } else if (currentColIndex > 0) {
                    letterBoxes[currentRow][currentColIndex - 1].requestFocus();
                    activeEditText = letterBoxes[currentRow][currentColIndex - 1];
                    activeEditText.setText("");
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

        if (currentAttemptsLeft <= 0) {
            Toast.makeText(this, "No more attempts left for today.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userGuess.length() != wordGame.chosenWord.length()) {
            Toast.makeText(this, "Your guess must be " + wordGame.chosenWord.length() + " letters long.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get feedback from the WordGame class
        WordGame.Feedback feedback = wordGame.handleGuess(userGuess);

        //text box color feedback
        displayFeedback(feedback);

        currentAttemptsLeft--;

        if (feedback.message.contains("Congratulations") || currentAttemptsLeft == 0) {
            userDatabaseReference.child("metaData").child("l2WordGuess").setValue(1);
            endGame(feedback);
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

        saveAttemptDate();
        userDatabaseReference.child("metaData").child("l2AttemptsLeft").setValue(currentAttemptsLeft);
    }

    private void saveAttemptDate() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());

        // Save the current date in the 'l1DateTried' field
        userDatabaseReference.child("metaData").child("l2DateTried").setValue(currentDate);
    }


    private String getUserInput() {
        StringBuilder guess = new StringBuilder();
        for (EditText box : letterBoxes[currentRow]) {
            guess.append(box.getText().toString());
        }
    }

    private void displayFeedback(WordGame.Feedback feedback) {
        setColoredFeedback(currentRow, feedback.feedbackChars, feedback.feedbackStatus);
    }

    private void updateKeyColors(char[] feedbackChars, int[] feedbackStatus) {
        for (int i = 0; i < feedbackChars.length; i++) {
            char letter = feedbackChars[i];
            Button keyButton = keyButtons.get(Character.toUpperCase(letter));

            if (keyButton != null) {
                // Green: Correct letter and correct position
                if (feedbackStatus[i] == 2) {
                    keyButton.setBackgroundColor(Color.parseColor("#556B2F"));
                }
                // Yellow: Correct letter, wrong position
                else if (feedbackStatus[i] == 1) {
                    keyButton.setBackgroundColor(Color.parseColor("#DAA520"));
                }
                // Gray: Incorrect letter
                else {
                    keyButton.setBackgroundColor(Color.parseColor("#696969"));
                }
                keyButton.setTextColor(Color.WHITE);
                keyButton.setShadowLayer(15f, 0f, 0f, Color.BLACK);
            }
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
                letterBox.setBackground(createColoredBackground(Color.parseColor("#80000000")));
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
            int coinsEarned = rewardManager.awardLevelCompletionReward(1);
            int pointsEarned = rewardManager.getPointsEarned(1);

            // Update the total score in Firebase
            updateScore(pointsEarned, newScore -> {
                // Update the word count when the word is guessed correctly
                updateWordCount(wordCount -> {
                    tvMessage.setText(feedback.message + "\n\nYou earned:\n" + coinsEarned + " Silver Coins\n" + pointsEarned + " Points\n\n New Total Points: " + newScore + "\nWords Correct: " + wordCount);
                    playSoundEffect(R.raw.victory_sound);
                });
            });

        } else {
            tvMessage.setText(feedback.message);
            playSoundEffect(R.raw.sad_sound);
        }

        //allow end message to display
        messageContainer.setVisibility(View.VISIBLE);

        // Hide game UI when displaying the end message
        findViewById(R.id.letterBoxesContainer).setVisibility(View.GONE);
        findViewById(R.id.keyboard).setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }

    // Method to update the word count in Firebase
    private void updateWordCount(final LevelTwoActivity.OnWordCountUpdatedListener listener) {
        DatabaseReference wordCountRef = userDatabaseReference.child("wordsCorrect");

        wordCountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentWordCount = dataSnapshot.getValue(Integer.class);

                if (currentWordCount != null) {
                    int newWordCount = currentWordCount + 1;

                    // Store the updated word count back into Firebase
                    wordCountRef.setValue(newWordCount).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Notify the caller with the updated word count
                            listener.onWordCountUpdated(newWordCount);
                        } else {
                            Log.e("RewardManager", "Error updating word count");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RewardManager", "Error fetching word count: " + databaseError.getMessage());
            }
        });
    }

    // Callback interface for word count update
    interface OnWordCountUpdatedListener {
        void onWordCountUpdated(int newWordCount);
    }

    private void updateScore(int pointsEarned, final OnScoreUpdatedListener listener) {
        // Get the current score from Firebase
        DatabaseReference scoreRef = userDatabaseReference.child("points");

        scoreRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int currentScore = task.getResult().getValue(Integer.class);

                // Calculate new score
                int newScore = currentScore + pointsEarned;

                // Update the score in Firebase
                scoreRef.setValue(newScore).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        // Notify listener that the score has been updated
                        listener.onScoreUpdated(newScore);
                    } else {
                        Log.e("updateScore", "Failed to update score in Firebase", updateTask.getException());
                    }
                });
            } else {
                Log.e("updateScore", "Failed to get current score from Firebase", task.getException());
            }
        });
    }

    // Create an interface to handle the callback after score is updated
    interface OnScoreUpdatedListener {
        void onScoreUpdated(int newScore);
    }

    private void initializeUserData() {
        userDatabaseReference.child("metaData").child("l2WordGuess").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentWordGuess = task.getResult().getValue(Integer.class);
                if (currentWordGuess == 1) {
                    checkDateAndRestrict();
                }
            }
        });

        userDatabaseReference.child("metaData").child("l2AttemptsLeft").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentAttemptsLeft = task.getResult().getValue(Integer.class);
            }
        });

        userDatabaseReference.child("metaData").child("l2DateTried").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String savedDate = task.getResult().getValue(String.class);  // Expect the date as a String

                if (isNewDay(savedDate)) {  // Pass the savedDate as a string
                    resetAttempts();
                }
            }
        });

    }

    private void checkDateAndRestrict() {
        userDatabaseReference.child("metaData").child("l2DateTried").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String savedDate = task.getResult().getValue(String.class);

                if (!isNewDay(savedDate)) {
                    // If it's the same day, check if the word has already been guessed
                    userDatabaseReference.child("metaData").child("l2WordGuess").get().addOnCompleteListener(guessTask -> {
                        if (guessTask.isSuccessful()) {
                            int wordGuess = guessTask.getResult().getValue(Integer.class);

                            // If the word is guessed (l1WordGuess == 1), block the game
                            if (wordGuess == 1) {
                                blockUserFromGame();
                            }
                        }
                    });
                }
            }
        });
    }


    private void blockUserFromGame() {
        enableLetters(false);
        RelativeLayout messageContainer = findViewById(R.id.message_container);
        TextView tvMessage = findViewById(R.id.tv_message);
        tvMessage.setText("You have already attempted the word for today. Try again tomorrow.");
        playSoundEffect(R.raw.error_sound);
        messageContainer.setVisibility(View.VISIBLE);
        findViewById(R.id.letterBoxesContainer).setVisibility(View.GONE);
        findViewById(R.id.keyboard).setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }

    private boolean isNewDay(String savedDate) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());

        // Compare the saved date with the current date
        return !savedDate.equals(currentDate);
    }

    private void resetAttempts() {
        currentAttemptsLeft = 5;
        currentWordGuess = 0;
        userDatabaseReference.child("metaData").child("l2AttemptsLeft").setValue(currentAttemptsLeft);
        userDatabaseReference.child("metaData").child("l2WordGuess").setValue(currentWordGuess);
    }

    private void showLoginRequiredMessage() {
        enableLetters(false);
        RelativeLayout messageContainer = findViewById(R.id.message_container);
        TextView tvMessage = findViewById(R.id.tv_message);
        tvMessage.setText("You need to log in to play this level.");
        playSoundEffect(R.raw.error_sound);
        messageContainer.setVisibility(View.VISIBLE);
        findViewById(R.id.letterBoxesContainer).setVisibility(View.GONE);
        findViewById(R.id.keyboard).setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }

    private void playSoundEffect(int soundResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, soundResourceId);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
