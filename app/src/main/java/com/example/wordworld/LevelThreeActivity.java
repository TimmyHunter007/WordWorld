package com.example.wordworld;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class LevelThreeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_level_three);

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
        final EditText letter5 = findViewById(R.id.letter5);
        final EditText letter6 = findViewById(R.id.letter6);

        // Add TextWatchers to automatically move to the next box
        addTextWatcher(letter1, letter2);
        addTextWatcher(letter2, letter3);
        addTextWatcher(letter3, letter4);
        addTextWatcher(letter4, letter5);
        addTextWatcher(letter5, letter6);
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
}
