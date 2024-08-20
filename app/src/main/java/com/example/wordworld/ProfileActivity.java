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

    private FirebaseAuth auth; // Firebase Authentication instance
    private FirebaseUser user; // Current authenticated user
    private DatabaseReference databaseReference; // Database reference for user's data
    private TextView emailDisplay, silverCoinsTextView, nameDisplay, badgeCountDisplay, pointsDisplay; // UI elements
    private Button updatePasswordButton, signOutButton, addCoinsButton; // Buttons for various actions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hide the action bar for a full-screen experience
        setContentView(R.layout.activity_profile); // Set the layout for this activity

        auth = FirebaseAuth.getInstance(); // Initialize Firebase Auth
        user = auth.getCurrentUser(); // Get the current authenticated user

        // Set up the database reference to the current user's data
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

        // Display the user's email
        if (user != null) {
            emailDisplay.setText(user.getEmail());
        }

        // Retrieve and display the user's points
        databaseReference.child("points").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer points = dataSnapshot.getValue(Integer.class);
                    pointsDisplay.setText("Points: " + (points != null ? points : 0));
                } else {
                    pointsDisplay.setText("Points: 0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load Points.", Toast.LENGTH_SHORT).show();
            }
        });

        // Retrieve and display the user's first name and last name, and calculate badge count
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fName = dataSnapshot.child("fName").getValue(String.class);
                    String lName = dataSnapshot.child("lName").getValue(String.class);
                    nameDisplay.setText((fName != null ? fName : "") + " " + (lName != null ? lName : ""));

                    int totalBadges = 0;
                    for (DataSnapshot badgeSnapshot : dataSnapshot.getChildren()) {
                        String key = badgeSnapshot.getKey();
                        if (key != null && key.startsWith("hasBadge_")) {
                            Integer badgeCount = badgeSnapshot.getValue(Integer.class);
                            totalBadges += (badgeCount != null && badgeCount > 0) ? badgeCount : 0;
                        }
                    }
                    badgeCountDisplay.setText("Total Badges: " + totalBadges);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load user information.", Toast.LENGTH_SHORT).show();
            }
        });

        // Retrieve and display the user's Silver Coins
        databaseReference.child("silverCoins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer silverCoins = dataSnapshot.getValue(Integer.class);
                silverCoinsTextView.setText("Silver Coins: " + (silverCoins != null ? silverCoins : 0));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load Silver Coins.", Toast.LENGTH_SHORT).show();
            }
        });

        // Add coins button click listener to increase the user's Silver Coins
        addCoinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("silverCoins").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Integer currentCoins = dataSnapshot.getValue(Integer.class);
                        if (currentCoins != null) {
                            int newCoinValue = currentCoins + 10;
                            databaseReference.child("silverCoins").setValue(newCoinValue)
                                    .addOnCompleteListener(task -> {
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
                        Toast.makeText(ProfileActivity.this, "Failed to retrieve current coins.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Set up the "Update Password" button to show the dialog
        updatePasswordButton.setOnClickListener(v -> showUpdatePasswordDialog());

        // Sign out button click listener to log out the user
        signOutButton.setOnClickListener(v -> {
            auth.signOut(); // Sign out the user
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent); // Redirect to MainActivity
            finish(); // Close the profile activity
        });

        // Initialize the back button and set the click listener to navigate back
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed()); // Navigate back to the previous activity
    }

    // Method to show the Update Password dialog
    private void showUpdatePasswordDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_update_password, null);

        // Initialize the input fields and button
        EditText oldPasswordDialog = view.findViewById(R.id.old_password_dialog);
        EditText newPasswordDialog = view.findViewById(R.id.new_password_dialog);
        Button updatePasswordButtonDialog = view.findViewById(R.id.update_password_button_dialog);

        // Create an AlertDialog to hold the custom layout
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setTitle("Update Password")
                .setNegativeButton("Cancel", null)
                .create();

        // Set the Update button click listener
        updatePasswordButtonDialog.setOnClickListener(v -> {
            String oldPass = oldPasswordDialog.getText().toString().trim();
            String newPass = newPasswordDialog.getText().toString().trim();

            // Validate the input fields
            if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass)) {
                Toast.makeText(ProfileActivity.this, "Please enter both passwords", Toast.LENGTH_SHORT).show();
                return;
            }

            // Re-authenticate the user before updating the password
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Update the user's password
                    user.updatePassword(newPass).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss(); // Close the dialog
                        } else {
                            Toast.makeText(ProfileActivity.this, "Password Update Failed: " + task1.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "Authentication Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Show the dialog
        alertDialog.show();
    }
}
