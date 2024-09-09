package com.example.wordworld;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LevelThreeActivity extends AppCompatActivity {
    private EditText letter1, letter2, letter3, letter4, letter5, letter6;
    private TextView tvFeedBack, tvFeedBack1, tvFeedBack2, tvFeedBack3, tvFeedBack4;
    private TextView tvAttempts;
    private WordGame wordGame;
    private Button submitButton;
    private WordManagement wordManagement;
    private int feedbackIndex = 0;
    private FirebaseUser user;
    private RewardManager rewardManager;
    private DatabaseReference userDatabaseReference;
    private MediaPlayer mediaPlayer; // For playing sound effects

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

        // Initialize Firebase components
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        } else {
            Log.e("LevelThreeActivity", "User not authenticated");
        }

        // Initialize RewardManager with the correct DatabaseReference
        if (userDatabaseReference != null) {
            rewardManager = new RewardManager(userDatabaseReference);
        }

        if (userDatabaseReference != null) {
            checkIfUserCanGuess();
        } else {
            logInRequired();
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
                onBackPressed();
            }
        });

        // Add TextWatcher to move to the next EditText and handle backspace navigation
        letter1.addTextChangedListener(new LetterTextWatcher(letter1, letter2, null));
        letter2.addTextChangedListener(new LetterTextWatcher(letter2, letter3, letter1));
        letter3.addTextChangedListener(new LetterTextWatcher(letter3, letter4, letter2));
        letter4.addTextChangedListener(new LetterTextWatcher(letter4, letter5, letter3));
        letter5.addTextChangedListener(new LetterTextWatcher(letter5, letter6, letter4));
        letter6.addTextChangedListener(new LetterTextWatcher(letter6, null, letter5));
    }

    private void handleGuess() {
        String userGuess = getUserInput();

        if (userGuess.length() != wordGame.chosenWord.length()) {
            Toast.makeText(this, "Your guess must be " + wordGame.chosenWord.length() + " letters long.", Toast.LENGTH_SHORT).show();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(submitButton.getWindowToken(), 0);
            playSoundEffect(R.raw.error_sound);
            return;
        }

        WordGame.Feedback feedback = wordGame.handleGuess(userGuess);

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

        feedbackIndex++;
        tvAttempts.setText("Attempts Left: " + feedback.attemptsLeft);
        updateAttemptsInDatabase(feedback.attemptsLeft);

        if (feedback.message.contains("Congratulations") || feedback.attemptsLeft <= 0) {
            enableLetters(false);
            updateUserMetaData();

            RelativeLayout messageContainer = findViewById(R.id.message_container);
            TextView tvMessage = findViewById(R.id.tv_message);

            if (feedback.message.contains("Congratulations")) {
                int coinsEarned = rewardManager.awardLevelCompletionReward(3);
                int pointsEarned = rewardManager.getPointsEarned(3);

                // Fetch the current total points from Firebase
                userDatabaseReference.child("points").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentPoints = 0;  // Default value if points do not exist yet

                        if (snapshot.exists()) {
                            currentPoints = snapshot.getValue(Integer.class);  // Get current points from database
                        }

                        int newTotalPoints = currentPoints + pointsEarned;  // Update the total points

                        // Save the updated total points back to the database
                        userDatabaseReference.child("points").setValue(newTotalPoints);

                        // Display the message to the user with the new total points
                        tvMessage.setText(feedback.message + "\n\nYou earned:\n" + coinsEarned + " Silver Coins\n" + pointsEarned + " Points" +
                                "\n\nTotal Points: " + newTotalPoints);

                        playSoundEffect(R.raw.victory_sound); // Play victory sound
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("LevelThreeActivity", "Failed to fetch total points");
                    }
                });

            } else {
                tvMessage.setText(feedback.message);
                playSoundEffect(R.raw.sad_sound); // Play sad sound
            }

            messageContainer.setVisibility(View.VISIBLE);
            hideGameComponents();
        }

        clearLetters();
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

    private void hideGameComponents() {
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

    private void updateAttemptsInDatabase(int attemptsLeft) {
        if (userDatabaseReference != null) {
            userDatabaseReference.child("metaData").child("l3AttemptsLeft").setValue(attemptsLeft);
        }
    }

    private void logInRequired() {
        RelativeLayout messageContainer = findViewById(R.id.message_container);
        TextView tvMessage = findViewById(R.id.tv_message);
        messageContainer.setVisibility(View.VISIBLE);
        hideGameComponents();
        tvMessage.setText("You need to be logged in to play this level.");
        playSoundEffect(R.raw.error_sound);
    }

    private void updateUserMetaData() {
        if (userDatabaseReference != null) {
            long currentTime = System.currentTimeMillis();
            userDatabaseReference.child("metaData").child("l3WordGuess").setValue(1);
            userDatabaseReference.child("metaData").child("l3DateTried").setValue(currentTime);
        }
    }

    private void checkIfUserCanGuess() {
        userDatabaseReference.child("metaData").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String storedDate = getStoredDate(snapshot);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
                    String currentDate = sdf.format(Calendar.getInstance().getTime());

                    Integer wordGuess = snapshot.child("l3WordGuess").exists() ?
                            snapshot.child("l3WordGuess").getValue(Integer.class) : 0;

                    if (storedDate != null && storedDate.equals(currentDate)) {
                        Integer savedAttempts = snapshot.child("l3AttemptsLeft").exists() ?
                                snapshot.child("l3AttemptsLeft").getValue(Integer.class) : 5;
                        wordGame.attempts = savedAttempts;
                        tvAttempts.setText("Attempts Left: " + savedAttempts);

                        if (wordGuess == 1) {
                            preventFurtherGuessing();
                        }
                    } else {
                        int attempts = 5;

                        userDatabaseReference.child("metaData").child("l3AttemptsLeft").setValue(attempts);
                        wordGame.attempts = attempts;
                        tvAttempts.setText("Attempts Left: " + attempts);

                        userDatabaseReference.child("metaData").child("l3WordGuess").setValue(0);
                        userDatabaseReference.child("metaData").child("l3DateTried").setValue(currentDate);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LevelThreeActivity", "Failed to fetch user metadata");
            }
        });
    }

    private String getStoredDate(DataSnapshot snapshot) {
        if (snapshot.child("l3DateTried").getValue() instanceof Long) {
            Long dateAsLong = snapshot.child("l3DateTried").getValue(Long.class);
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
            return sdf.format(dateAsLong);
        } else {
            return snapshot.child("l3DateTried").getValue(String.class);
        }
    }

    private void preventFurtherGuessing() {
        Toast.makeText(LevelThreeActivity.this, "You have already guessed today.", Toast.LENGTH_SHORT).show();
        RelativeLayout messageContainer = findViewById(R.id.message_container);
        TextView tvMessage = findViewById(R.id.tv_message);
        tvMessage.setText("You have already guessed today.");
        playSoundEffect(R.raw.error_sound);
        messageContainer.setVisibility(View.VISIBLE);
        hideGameComponents();
        enableLetters(false);
        submitButton.setEnabled(false);
    }

    private void setColoredFeedback(TextView textView, char[] feedbackChars, int[] feedbackStatus) {
        StringBuilder coloredText = new StringBuilder();
        for (int i = 0; i < feedbackChars.length; i++) {
            if (feedbackStatus[i] == 2) {
                coloredText.append("<font color='#3CB371'>").append(feedbackChars[i]).append("</font>");
            } else if (feedbackStatus[i] == 1) {
                coloredText.append("<font color='#FFBF00'>").append(feedbackChars[i]).append("</font>");
            } else {
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

    private class LetterTextWatcher implements TextWatcher {
        private final EditText currentEditText;
        private final EditText nextEditText;
        private final EditText prevEditText;

        public LetterTextWatcher(EditText currentEditText, EditText nextEditText, EditText prevEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
            this.prevEditText = prevEditText;

            this.currentEditText.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                        keyCode == android.view.KeyEvent.KEYCODE_DEL) {
                    if (currentEditText.getText().toString().isEmpty() && prevEditText != null) {
                        prevEditText.requestFocus();
                        prevEditText.setText("");
                        prevEditText.setSelection(prevEditText.getText().length());
                        return true;
                    }
                }
                return false;
            });
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1) {
                if (nextEditText != null) {
                    nextEditText.requestFocus();
                } else {
                    currentEditText.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(currentEditText.getWindowToken(), 0);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
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
