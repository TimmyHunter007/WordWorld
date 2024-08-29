package com.example.wordworld;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

   /* So something like this:
    LevelOne - 1 coin per attempt left - 5 point when you complete the word - 50 coins per hint
    LevelTwo - 2 coins per attempt left - 10 points when you complete the word - 100 coins per hint
    LevelThree - 3 conins per attempt left - 15 points when you complete the word - 150 coins per hint*/
    //Method for rewarding coins based on level
public void awardLevelCompletionReward(int level) {
    int coinsEarned;
    int pointsEarned;

    switch (level) {
        //LevelOne reward
        case 1:
            coinsEarned = COINS_PER_LEVEL_1;
            pointsEarned = POINTS_PER_LEVEL_1;
            break;
            //Level 2 reward
            case 2:
                coinsEarned = COINS_PER_LEVEL_2;
                pointsEarned = POINTS_PER_LEVEL_2;
                break;
                //Level 3 reward
                case 3:
                    coinsEarned = COINS_PER_LEVEL_3;
                    pointsEarned = POINTS_PER_LEVEL_3;
                    break;
        default:
            coinsEarned = 0;
            pointsEarned = 0;
            break;
    }
    Log.d("RewardManager", "Awarded " + coinsEarned + " coins for level " + level);


    //Storing new point total into the database
    databaseReference.child("points").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer currentPoints = dataSnapshot.getValue(Integer.class);

            if(currentPoints != null) {
                int newPointValue = currentPoints + pointsEarned;

                //Using this to handle any errors
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
                        });}
            }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("RewardManager", "Error fetching point balance: " + databaseError.getMessage());
        }
    });

    //Storing new coin amount into the database
    databaseReference.child("silverCoins").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Integer currentCoins = dataSnapshot.getValue(Integer.class);

            if (currentCoins != null) {
                int newCoinValue = currentCoins + coinsEarned;

                //using to handle any errors
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
                        });}


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("RewardManager", "Error fetching coin balance: " + databaseError.getMessage());
        }
    });
}
}




