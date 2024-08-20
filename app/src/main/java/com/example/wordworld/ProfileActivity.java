package com.example.wordworld;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
    private TextView emailDisplay, silverCoinsTextView, nameDisplay; // UI elements
    private EditText oldPassword, newPassword; // Input fields for password change
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
        oldPassword = findViewById(R.id.old_password);
        newPassword = findViewById(R.id.new_password);
        updatePasswordButton = findViewById(R.id.update_password_button);
        signOutButton = findViewById(R.id.sign_out_button);
        addCoinsButton = findViewById(R.id.add_coins_button);
        nameDisplay = findViewById(R.id.name_display);

        // Display the user's UID (as a placeholder for email display or other user info)
        if (user != null) {
            emailDisplay.setText(user.getEmail());
        }

        // Retrieve and display the user's first name and last name from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the fName and lName values from the database
                    String fName = dataSnapshot.child("fName").getValue(String.class);
                    String lName = dataSnapshot.child("lName").getValue(String.class);

                    // Combine the first and last name
                    String fullName = fName + " " + lName;

                    // Log the full name
                    Log.d("ProfileActivity", "Full Name: " + fullName);

                    // Set the full name to the TextView
                    nameDisplay.setText(fullName);
                } else {
                    Log.d("ProfileActivity", "User data does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(ProfileActivity.this, "Failed to load user information.", Toast.LENGTH_SHORT).show();
                Log.e("ProfileActivity", "Database error: " + databaseError.getMessage());
            }
        });

        // Retrieve and display the number of Silver Coins the user has
        databaseReference.child("silverCoins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the silverCoins value and display it
                    Integer silverCoins = dataSnapshot.getValue(Integer.class);
                    if (silverCoins != null) {
                        silverCoinsTextView.setText("Silver Coins: " + silverCoins);
                        Log.d("ProfileActivity", "Silver Coins retrieved: " + silverCoins);
                    } else {
                        silverCoinsTextView.setText("Silver Coins: 0");
                        Log.d("ProfileActivity", "Silver Coins value is null, setting to 0.");
                    }
                } else {
                    silverCoinsTextView.setText("Silver Coins: 0");
                    Log.d("ProfileActivity", "DataSnapshot does not exist, setting Silver Coins to 0.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProfileActivity.this, "Failed to load Silver Coins.", Toast.LENGTH_SHORT).show();
                Log.e("ProfileActivity", "Database error: " + databaseError.getMessage());
            }
        });

        // Add coins button click listener to increase the user's Silver Coins
        addCoinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve current Silver Coins value and add 10 coins
                databaseReference.child("silverCoins").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Integer currentCoins = dataSnapshot.getValue(Integer.class);
                        if (currentCoins != null) {
                            int newCoinValue = currentCoins + 10; // Add 10 coins
                            databaseReference.child("silverCoins").setValue(newCoinValue)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ProfileActivity.this, "10 Coins Added!", Toast.LENGTH_SHORT).show();
                                            Log.d("ProfileActivity", "10 Coins successfully added.");
                                        } else {
                                            Toast.makeText(ProfileActivity.this, "Failed to add coins.", Toast.LENGTH_SHORT).show();
                                            Log.e("ProfileActivity", "Failed to add coins: " + task.getException());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProfileActivity.this, "Failed to retrieve current coins.", Toast.LENGTH_SHORT).show();
                        Log.e("ProfileActivity", "Database error: " + databaseError.getMessage());
                    }
                });
            }
        });

        // Update password button click listener to change the user's password
        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = oldPassword.getText().toString().trim();
                String newPass = newPassword.getText().toString().trim();

                // Validate input fields
                if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass)) {
                    Toast.makeText(ProfileActivity.this, "Please enter both passwords", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Re-authenticate user before updating the password
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update the user's password
                        user.updatePassword(newPass).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(ProfileActivity.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Password Update Failed: " + task1.getException(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(ProfileActivity.this, "Authentication Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Sign out button click listener to log out the user
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut(); // Sign out the user
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent); // Redirect to MainActivity
                finish(); // Close the profile activity
            }
        });

        // Initialize the back button and set the click listener to navigate back
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button action
                onBackPressed(); // Navigate back to the previous activity
            }
        });
    }
}
