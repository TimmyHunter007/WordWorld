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

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private TextView emailDisplay, silverCoinsTextView, testDisplay;
    private EditText oldPassword, newPassword;
    private Button updatePasswordButton, signOutButton, addCoinsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Set up database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        emailDisplay = findViewById(R.id.email_display);
        silverCoinsTextView = findViewById(R.id.silver_coins);
        oldPassword = findViewById(R.id.old_password);
        newPassword = findViewById(R.id.new_password);
        updatePasswordButton = findViewById(R.id.update_password_button);
        signOutButton = findViewById(R.id.sign_out_button);
        addCoinsButton = findViewById(R.id.add_coins_button);
        testDisplay = findViewById(R.id.test_display);

        // Display user's email
        if (user != null) {
            emailDisplay.setText(user.getUid());
        }

        databaseReference.child("fName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the fName value from the database
                    String fName = dataSnapshot.getValue(String.class);
                    Log.d("ProfileActivity", "fName: " + fName); // Log the fName value

                    // Set the fName value to the TextView
                    testDisplay.setText(fName);
                } else {
                    Log.d("ProfileActivity", "fName does not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(ProfileActivity.this, "Failed to load first name.", Toast.LENGTH_SHORT).show();
            }
        });

        // Retrieve and display Silver Coins
        databaseReference.child("silverCoins").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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

        // Add coins button click listener
        addCoinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("silverCoins").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Integer currentCoins = dataSnapshot.getValue(Integer.class);
                        if (currentCoins != null) {
                            int newCoinValue = currentCoins + 10; // Add 10 coins for testing
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

        // Update password button click listener
        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = oldPassword.getText().toString().trim();
                String newPass = newPassword.getText().toString().trim();

                if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass)) {
                    Toast.makeText(ProfileActivity.this, "Please enter both passwords", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Re-authenticate user before updating password
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
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

        // Sign out button click listener
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
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
}
