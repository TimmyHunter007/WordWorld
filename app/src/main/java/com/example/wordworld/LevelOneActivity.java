package com.example.wordworld;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class LevelOneActivity extends AppCompatActivity {

    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_level_one);

        // Initialize the back button and set the click listener
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button action
                onBackPressed();
            }
        });

        // Get references to the EditText fields
        final EditText letter1 = findViewById(R.id.letter1);
        final EditText letter2 = findViewById(R.id.letter2);
        final EditText letter3 = findViewById(R.id.letter3);
        final EditText letter4 = findViewById(R.id.letter4);

        // Set focus change listeners to highlight the currently selected box
        setFocusChangeListener(letter1);
        setFocusChangeListener(letter2);
        setFocusChangeListener(letter3);
        setFocusChangeListener(letter4);

        // Add TextWatchers to automatically move to the next box
        addTextWatcher(letter1, letter2);
        addTextWatcher(letter2, letter3);
        addTextWatcher(letter3, letter4);

        // Initialize the submit button
        submitButton = findViewById(R.id.submit_level_one);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add submit button logic here
            }
        });
    }

    private void addTextWatcher(final EditText currentEditText, final EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    nextEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setFocusChangeListener(final EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editText.setBackgroundResource(R.drawable.edittext_highlighted_level1);  // Highlighted state
                } else {
                    editText.setBackgroundResource(R.drawable.edittext_normal_level1);  // Normal state
                }
            }
        });
    }
}
