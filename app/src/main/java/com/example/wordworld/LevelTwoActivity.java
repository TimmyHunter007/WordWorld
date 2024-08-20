package com.example.wordworld;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class LevelTwoActivity extends AppCompatActivity {

    private Button submitButton; // Declare the submit button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hide the action bar for a full-screen experience
        setContentView(R.layout.activity_level_two); // Set the layout for this activity

        // Initialize the back button and set its click listener
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Navigate back to the previous screen when the back button is pressed
            }
        });

        // Get references to the EditText fields
        final EditText letter1 = findViewById(R.id.letter1);
        final EditText letter2 = findViewById(R.id.letter2);
        final EditText letter3 = findViewById(R.id.letter3);
        final EditText letter4 = findViewById(R.id.letter4);
        final EditText letter5 = findViewById(R.id.letter5);

        // Set focus change listeners to highlight the currently selected EditText
        setFocusChangeListener(letter1);
        setFocusChangeListener(letter2);
        setFocusChangeListener(letter3);
        setFocusChangeListener(letter4);
        setFocusChangeListener(letter5);

        // Add TextWatchers to automatically move focus to the next EditText when a character is entered
        addTextWatcher(letter1, letter2);
        addTextWatcher(letter2, letter3);
        addTextWatcher(letter3, letter4);
        addTextWatcher(letter4, letter5);

        // Initialize the submit button and set its click listener
        submitButton = findViewById(R.id.submit_level_two);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add submit button logic here (e.g., handling the user's input)
            }
        });
    }

    // Method to add a TextWatcher to an EditText that moves focus to the next EditText when a character is entered
    private void addTextWatcher(final EditText currentEditText, final EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before the text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    nextEditText.requestFocus(); // Move focus to the next EditText when a character is entered
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after the text changes
            }
        });
    }

    // Method to set focus change listeners on an EditText to change its background when it gains or loses focus
    private void setFocusChangeListener(final EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Change the background to highlighted when the EditText gains focus
                    editText.setBackgroundResource(R.drawable.edittext_highlighted_level2);
                } else {
                    // Change the background to normal when the EditText loses focus
                    editText.setBackgroundResource(R.drawable.edittext_normal_level2);
                }
            }
        });
    }
}
