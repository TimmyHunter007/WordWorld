package com.example.wordworld;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private int currentAttemptsLeft = 5;
    private int currentWordGuess = 0;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_level_one);

        letterBoxes = new EditText[][]{
                {findViewById(R.id.letter1_row1), findViewById(R.id.letter2_row1), findViewById(R.id.letter3_row1), findViewById(R.id.letter4_row1)},
                {findViewById(R.id.letter1_row2), findViewById(R.id.letter2_row2), findViewById(R.id.letter3_row2), findViewById(R.id.letter4_row2)},
                {findViewById(R.id.letter1_row3), findViewById(R.id.letter2_row3), findViewById(R.id.letter3_row3), findViewById(R.id.letter4_row3)},
                {findViewById(R.id.letter1_row4), findViewById(R.id.letter2_row4), findViewById(R.id.letter3_row4), findViewById(R.id.letter4_row4)},
                {findViewById(R.id.letter1_row5), findViewById(R.id.letter2_row5), findViewById(R.id.letter3_row5), findViewById(R.id.letter4_row5)}
        };

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
            initializeUserData();  // Fetch and initialize user data for attempts and guesses
        } else {
            // User is not logged in
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
        for (int i = 0; i < letterBoxes[row].length; i++) {
            final EditText currentBox = letterBoxes[row][i];
            final int index = i;

            currentBox.setOnTouchListener((v, event) -> {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            });

            currentBox.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    activeEditText = currentBox;
                }
            });

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
                    activeEditText.setText("");
                } else if (currentColIndex > 0) {
                    letterBoxes[currentRow][currentColIndex - 1].requestFocus();
                    activeEditText = letterBoxes[currentRow][currentColIndex - 1];
                    activeEditText.setText("");
                }
            }
        }
    }

    private int getActiveEditTextRowIndex() {
        for (int row = 0; row < letterBoxes.length; row++) {
            for (int col = 0; col < letterBoxes[row].length; col++) {
                if (letterBoxes[row][col].equals(activeEditText)) {
                    return row;
                }
            }
        }
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
        return -1;
    }

    private void handleGuess() {
        String userGuess = getUserInput();

        // Check if the user has no attempts left
        if (currentAttemptsLeft <= 0) {
            Toast.makeText(this, "No more attempts left for today.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate if the guess length matches the expected word length
        if (userGuess.length() != wordGame.chosenWord.length()) {
            Toast.makeText(this, "Your guess must be " + wordGame.chosenWord.length() + " letters long.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get feedback from the WordGame class
        WordGame.Feedback feedback = wordGame.handleGuess(userGuess);
        displayFeedback(feedback);

        // Decrease attempts left
        currentAttemptsLeft--;

        // If the user guesses correctly or if attempts are exhausted, mark the word as guessed
        if (feedback.message.contains("Congratulations") || currentAttemptsLeft == 0) {
            userDatabaseReference.child("metaData").child("l1WordGuess").setValue(1);
            endGame(feedback);
        } else {
            // Move to the next row
            if (currentRow < letterBoxes.length - 1) {
                currentRow++;
                setUpLetterBoxListeners(currentRow);
                letterBoxes[currentRow][0].requestFocus();
            } else {
                // End the game if no more rows are left
                endGame(feedback);
            }
        }

        // Update attempts left and save the current date of the attempt
        saveAttemptDate();
        userDatabaseReference.child("metaData").child("l1AttemptsLeft").setValue(currentAttemptsLeft);
    }

    private void saveAttemptDate() {
        long currentDate = System.currentTimeMillis() / 1000;
        userDatabaseReference.child("metaData").child("l1DateTried").setValue(currentDate);
    }

    private String getUserInput() {
        StringBuilder guess = new StringBuilder();
        for (EditText box : letterBoxes[currentRow]) {
            guess.append(box.getText().toString());
        }
        return guess.toString();
    }

    private void displayFeedback(WordGame.Feedback feedback) {
        setColoredFeedback(currentRow, feedback.feedbackChars, feedback.feedbackStatus);
    }

    private void setColoredFeedback(int row, char[] feedbackChars, int[] feedbackStatus) {
        for (int i = 0; i < feedbackChars.length; i++) {
            EditText letterBox = letterBoxes[row][i];
            if (feedbackStatus[i] == 2) {
                letterBox.setBackground(createColoredBackground(Color.parseColor("#556B2F")));
            } else if (feedbackStatus[i] == 1) {
                letterBox.setBackground(createColoredBackground(Color.parseColor("#DAA520")));
            } else {
                letterBox.setBackground(createColoredBackground(Color.parseColor("#4D000000")));
            }
            letterBox.setText(String.valueOf(feedbackChars[i]));
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

    private void enableLetters(boolean enabled) {
        for (EditText[] row : letterBoxes) {
            for (EditText letterBox : row) {
                letterBox.setEnabled(enabled);
            }
        }
    }

    private void endGame(WordGame.Feedback feedback) {
        enableLetters(false);
        RelativeLayout messageContainer = findViewById(R.id.message_container);
        TextView tvMessage = findViewById(R.id.tv_message);

        if (feedback.message.contains("Congratulations")) {
            int coinsEarned = rewardManager.awardLevelCompletionReward(1);
            int pointsEarned = rewardManager.getPointsEarned(1);
            tvMessage.setText(feedback.message + "\n\nYou earned:\n" + coinsEarned + " Silver Coins\n" + pointsEarned + " Points");
            playSoundEffect(R.raw.victory_sound);
        } else {
            tvMessage.setText(feedback.message);
            playSoundEffect(R.raw.sad_sound);
        }

        messageContainer.setVisibility(View.VISIBLE);
        findViewById(R.id.letterBoxesContainer).setVisibility(View.GONE);
        findViewById(R.id.keyboard).setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }

    private void initializeUserData() {
        userDatabaseReference.child("metaData").child("l1WordGuess").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentWordGuess = task.getResult().getValue(Integer.class);

                // If the word has already been guessed, block the game
                if (currentWordGuess == 1) {
                    checkDateAndRestrict();
                }
            }
        });

        userDatabaseReference.child("metaData").child("l1AttemptsLeft").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentAttemptsLeft = task.getResult().getValue(Integer.class);
            }
        });

        userDatabaseReference.child("metaData").child("l1DateTried").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                long savedDate = task.getResult().getValue(Long.class);
                if (isNewDay(savedDate)) {
                    resetAttempts();
                }
            }
        });
    }

    private void checkDateAndRestrict() {
        userDatabaseReference.child("metaData").child("l1DateTried").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                long savedDate = task.getResult().getValue(Long.class);
                if (!isNewDay(savedDate)) {
                    // If it's the same day, block the user from attempting again
                    blockUserFromGame();
                }
            }
        });
    }

    private void blockUserFromGame() {
        // Disable all input fields
        enableLetters(false);

        // Show the restriction message
        RelativeLayout messageContainer = findViewById(R.id.message_container);
        TextView tvMessage = findViewById(R.id.tv_message);
        tvMessage.setText("You have already attempted the word for today. Try again tomorrow.");

        // Display the message and hide the game UI
        playSoundEffect(R.raw.error_sound);
        messageContainer.setVisibility(View.VISIBLE);
        findViewById(R.id.letterBoxesContainer).setVisibility(View.GONE);
        findViewById(R.id.keyboard).setVisibility(View.GONE);
        submitButton.setVisibility(View.GONE);
    }

    private boolean isNewDay(long savedDate) {
        long currentTime = System.currentTimeMillis() / 1000;
        long oneDayInSeconds = 24 * 60 * 60;
        return (currentTime - savedDate) >= oneDayInSeconds;
    }

    private void resetAttempts() {
        currentAttemptsLeft = 5;
        currentWordGuess = 0;

        userDatabaseReference.child("metaData").child("l1AttemptsLeft").setValue(currentAttemptsLeft);
        userDatabaseReference.child("metaData").child("l1WordGuess").setValue(currentWordGuess);
    }

    private void showLoginRequiredMessage() {
        // Disable all input fields
        enableLetters(false);

        // Show the login required message
        RelativeLayout messageContainer = findViewById(R.id.message_container);
        TextView tvMessage = findViewById(R.id.tv_message);
        tvMessage.setText("You need to log in to play this level.");

        // Display the message and hide the game UI
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
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
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
