package com.example.wordworld;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
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
        getSupportActionBar().hide();
        setContentView(R.layout.activity_leader_board);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        leaderboardTable = findViewById(R.id.leaderboard_table);
        userTable = findViewById(R.id.user_table);

        fetchLeaderboardData();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageButton navButton = findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle navigation button action (e.g., open navigation drawer or another action)
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
                    String uid = snapshot.getKey();
                    UserData userData = new UserData(fName, points, wordsCorrect, uid);
                    leaderboardData.add(userData);
                }

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

                displayTop100Users();
                displayCurrentUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LeaderBoardActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayTop100Users() {
        leaderboardTable.removeViews(1, Math.max(0, leaderboardTable.getChildCount() - 1));

        int rank = 1;
        for (UserData userData : leaderboardData) {
            if (rank <= 100) {
                TableRow row = new TableRow(this);

                TextView nameView = new TextView(this);
                nameView.setText(userData.getFirstName());
                nameView.setPadding(8, 8, 8, 8);
                nameView.setTypeface(null, Typeface.BOLD);
                nameView.setTextSize(18);

                TextView pointsView = new TextView(this);
                pointsView.setText(String.valueOf(userData.getPoints()));
                pointsView.setPadding(8, 8, 8, 8);
                pointsView.setTextSize(18);

                TextView wordCountView = new TextView(this);
                wordCountView.setText(String.valueOf(userData.getWordCount()));
                wordCountView.setPadding(8, 8, 8, 8);
                wordCountView.setTextSize(18);

                row.addView(nameView);
                row.addView(pointsView);
                row.addView(wordCountView);

                if (rank == 1) {
                    row.setBackgroundResource(R.drawable.rounded_background_gold);
                    row.setPadding(8, 65, 8, 65);
                    setRowTextColor(row, ContextCompat.getColor(this, android.R.color.black));
                } else if (rank == 2) {
                    row.setBackgroundResource(R.drawable.rounded_background_silver);
                    row.setPadding(8, 55, 8, 55);
                    setRowTextColor(row, ContextCompat.getColor(this, android.R.color.black));
                } else if (rank == 3) {
                    row.setBackgroundResource(R.drawable.rounded_background_bronze);
                    row.setPadding(8, 45, 8, 45);
                    setRowTextColor(row, ContextCompat.getColor(this, android.R.color.black));
                } else {
                    row.setBackgroundResource(R.drawable.rounded_background);
                    setRowTextColor(row, ContextCompat.getColor(this, android.R.color.white));
                }

                leaderboardTable.addView(row);
            }
            rank++;
        }
    }

    private void setRowTextColor(TableRow row, int color) {
        for (int j = 0; j < row.getChildCount(); j++) {
            View view = row.getChildAt(j);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(color);
            }
        }
    }

    private void displayCurrentUser() {
        userTable.removeAllViews();

        String currentUserUid = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : null;

        if (currentUserUid != null) {
            for (int i = 0; i < leaderboardData.size(); i++) {
                UserData userData = leaderboardData.get(i);
                if (userData.getUid().equals(currentUserUid)) {
                    TableRow userRow = new TableRow(this);
                    userRow.setGravity(Gravity.CENTER);

                    int rank = i + 1;

                    if (rank == 1) {
                        FrameLayout frameLayout = new FrameLayout(this);
                        ImageView trophyIcon = new ImageView(this);
                        trophyIcon.setImageResource(R.drawable.trophy);

                        FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(150, 150);
                        iconParams.gravity = Gravity.CENTER;
                        trophyIcon.setLayoutParams(iconParams);

                        TextView rankView = new TextView(this);
                        rankView.setText(String.valueOf(rank));
                        rankView.setGravity(Gravity.CENTER);
                        rankView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                        rankView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        rankView.setTypeface(null, Typeface.BOLD);
                        rankView.setTextSize(20);

                        frameLayout.addView(trophyIcon);
                        frameLayout.addView(rankView);

                        userRow.addView(frameLayout);
                    } else {
                        TextView rankView = new TextView(this);
                        rankView.setText(String.valueOf(rank));
                        rankView.setPadding(8, 8, 8, 8);
                        rankView.setGravity(Gravity.CENTER);
                        rankView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        rankView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        rankView.setTypeface(null, Typeface.BOLD);
                        rankView.setTextSize(25);

                        userRow.addView(rankView);
                    }

                    if (rank == 1) {
                        userRow.setBackgroundResource(R.drawable.rounded_background_gold);
                        setRowTextColor(userRow, ContextCompat.getColor(this, android.R.color.black));
                    } else if (rank == 2) {
                        userRow.setBackgroundResource(R.drawable.rounded_background_silver);
                        setRowTextColor(userRow, ContextCompat.getColor(this, android.R.color.black));
                    } else if (rank == 3) {
                        userRow.setBackgroundResource(R.drawable.rounded_background_bronze);
                        setRowTextColor(userRow, ContextCompat.getColor(this, android.R.color.black));
                    } else {
                        userRow.setBackgroundResource(R.drawable.rounded_background);
                    }

                    userTable.addView(userRow);
                    break;
                }
            }
        } else {
            TextView userInfoTextView = new TextView(this);
            userInfoTextView.setText("Log in to see your rank and stats on the leaderboard.");
            userInfoTextView.setPadding(16, 16, 16, 16);
            userInfoTextView.setTextSize(18);
            userInfoTextView.setGravity(Gravity.CENTER);
            userInfoTextView.setBackgroundResource(R.drawable.rounded_background);
            userInfoTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            userTable.addView(userInfoTextView);
        }
    }

    private static class UserData {
        private String fName;
        private int points;
        private int wordsCorrect;
        private String uid;

        public UserData(String fName, int points, int wordsCorrect, String uid) {
            this.fName = fName;
            this.points = points;
            this.wordsCorrect = wordsCorrect;
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

        public String getUid() {
            return uid;
        }
    }
}
