package com.example.wordworld;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private TableLayout leaderboardTable;
    private TableLayout userTable;
    private List<UserData> leaderboardData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hide the action bar for a full-screen experience
        setContentView(R.layout.activity_leader_board);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Link XML elements to Java code
        leaderboardTable = findViewById(R.id.leaderboard_table);
        userTable = findViewById(R.id.user_table);

        // Fetch leaderboard data
        fetchLeaderboardData();

        // Initialize the back button and set its click listener to navigate back to the previous screen
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Navigate back to the previous activity
            }
        });

        // Initialize the navigation button and set its click listener
        ImageButton navButton = findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle navigation button action
                // Open navigation drawer or perform another action (placeholder)
            }
        });
    }

    private void fetchLeaderboardData() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                leaderboardData.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String fName = snapshot.child("fName").getValue(String.class);
                    int points = snapshot.child("points").getValue(Integer.class);
                    int wordsCorrect = snapshot.child("wordsCorrect").getValue(Integer.class);

                    // Calculate the badge count by checking each "hasBadge_" field
                    int badgeCount = 0;
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        if (childSnapshot.getKey().startsWith("hasBadge_")) {
                            Integer badgeValue = childSnapshot.getValue(Integer.class);
                            if (badgeValue != null && badgeValue == 1) {
                                badgeCount++;
                            }
                        }
                    }

                    String uid = snapshot.getKey();

                    UserData userData = new UserData(fName, points, wordsCorrect, badgeCount, uid);
                    leaderboardData.add(userData);
                }

                // Sort the data by points in descending order, then by word count if points are equal
                Collections.sort(leaderboardData, new Comparator<UserData>() {
                    @Override
                    public int compare(UserData u1, UserData u2) {
                        int pointComparison = Integer.compare(u2.getPoints(), u1.getPoints());
                        if (pointComparison != 0) {
                            return pointComparison;
                        } else {
                            return Integer.compare(u2.getWordCount(), u1.getWordCount());
                        }
                    }
                });

                // Display data
                displayTop100Users();
                displayCurrentUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    private void displayTop100Users() {
        leaderboardTable.removeViews(1, Math.max(0, leaderboardTable.getChildCount() - 1)); // Clear previous rows

        int rank = 1;
        for (UserData userData : leaderboardData) {
            if (rank <= 100) {
                TableRow row = new TableRow(this);
                row.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_background));

                TextView nameView = new TextView(this);
                nameView.setText(userData.getFirstName());
                nameView.setPadding(8, 8, 8, 8);
                nameView.setGravity(Gravity.CENTER);
                nameView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                nameView.setTextSize(18); // Set text size

                TextView pointsView = new TextView(this);
                pointsView.setText(String.valueOf(userData.getPoints()));
                pointsView.setPadding(8, 8, 8, 8);
                pointsView.setGravity(Gravity.CENTER);
                pointsView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                pointsView.setTextSize(18); // Set text size

                TextView wordCountView = new TextView(this);
                wordCountView.setText(String.valueOf(userData.getWordCount()));
                wordCountView.setPadding(8, 8, 8, 8);
                wordCountView.setGravity(Gravity.CENTER);
                wordCountView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                wordCountView.setTextSize(18); // Set text size

                TextView badgeCountView = new TextView(this);
                badgeCountView.setText(String.valueOf(userData.getBadgeCount()));
                badgeCountView.setPadding(8, 8, 8, 8);
                badgeCountView.setGravity(Gravity.CENTER);
                badgeCountView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                badgeCountView.setTextSize(18); // Set text size

                row.addView(nameView);
                row.addView(pointsView);
                row.addView(wordCountView);
                row.addView(badgeCountView);

                leaderboardTable.addView(row);
            }
            rank++;
        }
    }

    private void displayCurrentUser() {
        userTable.removeAllViews(); // Clear previous user data rows
        String currentUserUid = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : null;

        if (currentUserUid != null) {
            for (int i = 0; i < leaderboardData.size(); i++) {
                UserData userData = leaderboardData.get(i);
                if (userData.getUid().equals(currentUserUid)) {
                    TableRow userRow = new TableRow(this);
                    userRow.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_background));
                    userRow.setGravity(Gravity.CENTER); // Center the content of the TableRow

                    int rank = i + 1;
                    int rankColor;
                    if (rank >= 1 && rank <= 3) {
                        rankColor = ContextCompat.getColor(this, R.color.gold); // Special color for top 3
                    } else if (rank >= 4 && rank <= 10) {
                        rankColor = ContextCompat.getColor(this, R.color.silver); // Another color for rank 4-10
                    } else {
                        rankColor = ContextCompat.getColor(this, android.R.color.white); // Default color
                    }

                    TextView rankView = new TextView(this);
                    rankView.setText(String.valueOf(rank));
                    rankView.setPadding(8, 8, 8, 8);
                    rankView.setGravity(Gravity.CENTER); // Center text within the TextView
                    rankView.setTextColor(rankColor);
                    rankView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    rankView.setTypeface(null, Typeface.BOLD);
                    rankView.setTextSize(18); // Set text size

                    userRow.addView(rankView);

                    userTable.addView(userRow);
                    break; // Break the loop after finding the current user
                }
            }
        } else {
            // If the user is not logged in, show a message prompting them to log in
            TextView userInfoTextView = new TextView(this);
            userInfoTextView.setText("Log in to see your rank and stats on the leaderboard.");
            userInfoTextView.setPadding(16, 16, 16, 16);
            userInfoTextView.setTextSize(18); // Set text size
            userInfoTextView.setGravity(Gravity.CENTER);
            userInfoTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            userTable.addView(userInfoTextView);
        }
    }

    private static class UserData {
        private String fName;
        private int points;
        private int wordsCorrect;
        private int badgeCount;
        private String uid;

        public UserData(String fName, int points, int wordsCorrect, int badgeCount, String uid) {
            this.fName = fName;
            this.points = points;
            this.wordsCorrect = wordsCorrect;
            this.badgeCount = badgeCount;
            this.uid = uid;
        }

        public String getFirstName() {
            return fName;
        }

        public int getPoints() {
            return points;
        }

        public int getWordCount() {
            return wordsCorrect;
        }

        public int getBadgeCount() {
            return badgeCount;
        }

        public String getUid() {
            return uid;
        }
    }
}
