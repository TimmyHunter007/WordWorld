package com.example.wordworld;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class RewardManager {
    private static final int COINS_PER_LEVEL_1 = 10;
    private static final int COINS_PER_LEVEL_2 = 15;
    private static final int COINS_PER_LEVEL_3 = 20;
    private static final int POINTS_PER_LEVEL_1 = 25;
    private static final int POINTS_PER_LEVEL_2 = 50;
    private static final int POINTS_PER_LEVEL_3 = 100;
    // Reference to the Firebase Realtime Database for the current user's data
    private final DatabaseReference databaseReference;

    public RewardManager(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    // Method for rewarding coins based on level
    public int awardLevelCompletionReward(int level) {
        int coinsEarned;

        switch (level) {
            case 1:
                coinsEarned = COINS_PER_LEVEL_1;
                break;
            case 2:
                coinsEarned = COINS_PER_LEVEL_2;
                break;
            case 3:
                coinsEarned = COINS_PER_LEVEL_3;
                break;
            default:
                coinsEarned = 0;
                break;
        }
        Log.d("RewardManager", "Awarded " + coinsEarned + " coins for level " + level);

        // Store new coin amount into the database
        databaseReference.child("silverCoins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentCoins = dataSnapshot.getValue(Integer.class);

                if (currentCoins != null) {
                    int newCoinValue = currentCoins + coinsEarned;

                    // Using to handle any errors
                    databaseReference.child("silverCoins").setValue(newCoinValue)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("RewardManager", "Coin balance updated successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("RewardManager", "Error updating coin balance: " + e.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RewardManager", "Error fetching coin balance: " + databaseError.getMessage());
            }
        });

        return coinsEarned;
    }

    // Method to get the points earned based on level
    public int getPointsEarned(int level) {
        int pointsEarned;

        switch (level) {
            case 1:
                pointsEarned = POINTS_PER_LEVEL_1;
                break;
            case 2:
                pointsEarned = POINTS_PER_LEVEL_2;
                break;
            case 3:
                pointsEarned = POINTS_PER_LEVEL_3;
                break;
            default:
                pointsEarned = 0;
                break;
        }

        // Store new points total into the database
        databaseReference.child("points").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentPoints = dataSnapshot.getValue(Integer.class);

                if (currentPoints != null) {
                    int newPointValue = currentPoints + pointsEarned;

                    // Using this to handle any errors
                    databaseReference.child("points").setValue(newPointValue)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("RewardManager", "Points updated successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("RewardManager", "Error updating points: " + e.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RewardManager", "Error fetching point balance: " + databaseError.getMessage());
            }
        });

        return pointsEarned;
    }

    // Method to increase word count when word is correct
    public int getWordCount(int word) {
        int wordCount;

        switch (word) {
            case 1:
                wordCount = 1;
                break;
            case 2:
                wordCount = 1;
                break;
            case 3:
                wordCount = 1;
                break;
            default:
                wordCount = 0;
                break;
        }

        // Store new word count
        databaseReference.child("wordsCorrect").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentWordCount = dataSnapshot.getValue(Integer.class);

                if (currentWordCount != null) {
                    int newWordCount = currentWordCount + wordCount;

                    databaseReference.child("wordsCorrect").setValue(newWordCount)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("RewardManager", "Words updated successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("RewardManager", "Error updating words: " + e.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RewardManager", "Error fetching word count: " + databaseError.getMessage());
            }
        });
        return wordCount;
    }
}
