package com.example.wordworld;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    // Firebase Authentication instance for handling user authentication
    private FirebaseAuth auth;
    // Represents the currently authenticated user
    private FirebaseUser user;
    // Reference to the Firebase Realtime Database for the current user's data
    private DatabaseReference databaseReference;
    // UI elements to display user's email, silver coins, name, badge count, points, and word correct count
    private TextView emailDisplay, silverCoinsTextView, nameDisplay, badgeCountDisplay, pointsDisplay, wordCorrectDisplay;
    // Buttons for updating password, signing out, and adding coins
    private Button updatePasswordButton, signOutButton, addCoinsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hide the action bar for a full-screen experience
        setContentView(R.layout.activity_profile); // Set the layout for this activity

        // Initialize Firebase Authentication instance
        auth = FirebaseAuth.getInstance();
        // Get the currently authenticated user
        user = auth.getCurrentUser();

        // Set up the database reference to the current user's data in Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        // Initialize UI components
        emailDisplay = findViewById(R.id.email_display);
        silverCoinsTextView = findViewById(R.id.silver_coins);
        nameDisplay = findViewById(R.id.name_display);
        badgeCountDisplay = findViewById(R.id.badge_count_display);
        pointsDisplay = findViewById(R.id.points_display);
        signOutButton = findViewById(R.id.sign_out_button);
        addCoinsButton = findViewById(R.id.add_coins_button);
        updatePasswordButton = findViewById(R.id.update_password_button);
        wordCorrectDisplay = findViewById(R.id.word_correct_display);

        // Display the user's email if the user is logged in
        if (user != null) {
            emailDisplay.setText(user.getEmail());
        }

        // Retrieve and display the user's points from the database
        databaseReference.child("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the points data exists in the database
                if (dataSnapshot.exists()) {
                    // Get the points value from the database and display it, or display 0 if it's null
                    Integer points = dataSnapshot.getValue(Integer.class);
                    pointsDisplay.setText("" + (points != null ? points : 0));
                } else {
                    // If points data does not exist, display 0
                    pointsDisplay.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors that occur when retrieving data from the database
            }
        });

        // Retrieve and display the user's word count from the database
        databaseReference.child("wordsCorrect").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the word count data exists in the database
                if(dataSnapshot.exists()){
                    // Get the word count value from the database and display it, or display 0 if it's null
                    Integer correctWords = dataSnapshot.getValue(Integer.class);
                    wordCorrectDisplay.setText("" + correctWords);
                } else {
                    // If word count data does not exist, display 0
                    wordCorrectDisplay.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors that occur when retrieving data from the database
            }
        });

        // Retrieve and display the user's first name, last name, and calculate badge count from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the user's data exists in the database
                if (dataSnapshot.exists()) {
                    // Get the user's first name and last name from the database and display them
                    String fName = dataSnapshot.child("fName").getValue(String.class);
                    String lName = dataSnapshot.child("lName").getValue(String.class);
                    nameDisplay.setText((fName != null ? fName : "") + " " + (lName != null ? lName : ""));

                    // Calculate the total number of badges the user has earned
                    int totalBadges = 0;
                    for (DataSnapshot badgeSnapshot : dataSnapshot.getChildren()) {
                        String key = badgeSnapshot.getKey();
                        if (key != null && key.startsWith("hasBadge_")) {
                            Integer badgeCount = badgeSnapshot.getValue(Integer.class);
                            totalBadges += (badgeCount != null && badgeCount > 0) ? badgeCount : 0;
                        }
                    }
                    // Display the total badge count
                    badgeCountDisplay.setText("" + totalBadges);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors that occur when retrieving data from the database
            }
        });

        // Retrieve and display the user's Silver Coins from the database
        databaseReference.child("silverCoins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Get the silver coins value from the database and display it, or display 0 if it's null
                Integer silverCoins = dataSnapshot.getValue(Integer.class);
                silverCoinsTextView.setText("" + (silverCoins != null ? silverCoins : 0));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors that occur when retrieving data from the database
            }
        });

        // Add coins button click listener to increase the user's Silver Coins by 10
        addCoinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("silverCoins").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Retrieve the current coins value from the database
                        Integer currentCoins = dataSnapshot.getValue(Integer.class);
                        if (currentCoins != null) {
                            // Add 10 coins to the current coins value and update it in the database
                            int newCoinValue = currentCoins + 10;
                            databaseReference.child("silverCoins").setValue(newCoinValue)
                                    .addOnCompleteListener(task -> {
                                        // Notify the user that 10 coins have been added successfully or if the operation failed
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "10 Coins Added!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Failed to add coins.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors that occur when retrieving data from the database
                    }
                });
            }
        });

        // Set up the "Update Password" button to show the password update dialog
        updatePasswordButton.setOnClickListener(v -> showUpdatePasswordDialog());

        // Sign out button click listener to log out the user and redirect to the MainActivity
        signOutButton.setOnClickListener(v -> {
            auth.signOut(); // Sign out the user
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent); // Redirect to MainActivity
            finish(); // Close the profile activity
        });

        // Initialize the back button and set the click listener to navigate back to the previous activity
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed()); // Navigate back to the previous activity
    }

    // Method to show the Update Password dialog
    private void showUpdatePasswordDialog() {
        // Inflate the custom dialog layout for updating the password
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_update_password, null);

        // Initialize the EditText fields and button within the dialog
        EditText oldPasswordDialog = view.findViewById(R.id.old_password_dialog);
        EditText newPasswordDialog = view.findViewById(R.id.new_password_dialog);
        Button updatePasswordButtonDialog = view.findViewById(R.id.update_password_button_dialog);

        // Create the AlertDialog using the custom layout
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();

        // Set the AlertDialog window background to transparent
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set the Update button click listener to handle password update logic
        updatePasswordButtonDialog.setOnClickListener(v -> {
            // Get the old and new passwords entered by the user
            String oldPass = oldPasswordDialog.getText().toString().trim();
            String newPass = newPasswordDialog.getText().toString().trim();

            // Validate the input fields to ensure both passwords are entered
            if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass)) {
                Toast.makeText(ProfileActivity.this, "Please enter both passwords", Toast.LENGTH_SHORT).show();
                return;
            }

            // Re-authenticate the user before updating the password
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Update the user's password in Firebase Authentication
                    user.updatePassword(newPass).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            // Notify the user that the password has been updated successfully
                            Toast.makeText(ProfileActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss(); // Close the dialog
                        } else {
                            // Notify the user if the password update failed
                            Toast.makeText(ProfileActivity.this, "Password Update Failed: " + task1.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Notify the user if re-authentication failed
                    Toast.makeText(ProfileActivity.this, "Authentication Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Show the dialog to the user
        alertDialog.show();
    }
}