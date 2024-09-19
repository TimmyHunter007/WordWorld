package com.example.wordworld;

// Import statements for Android components and Firebase libraries
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
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

    // Firebase Authentication instance for handling user authentication
    private FirebaseAuth auth;
    // Reference to the Firebase Realtime Database for reading user data
    private DatabaseReference databaseReference;
    // TableLayout for displaying the leaderboard data
    private TableLayout leaderboardTable;
    // TableLayout for displaying the current user's rank and stats
    private TableLayout userTable;
    // List to store UserData objects for sorting and displaying leaderboard entries
    private List<UserData> leaderboardData = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); // Hide the action bar to create a full-screen experience
        setContentView(R.layout.activity_leader_board); // Set the layout for the activity

        // Initialize Firebase authentication and database reference
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Link the TableLayout views from the XML layout to the Java code
        leaderboardTable = findViewById(R.id.leaderboard_table);
        userTable = findViewById(R.id.user_table);

        // Fetch the leaderboard data from Firebase and populate the leaderboard
        fetchLeaderboardData();

        // Set up the back button to navigate to the previous activity when clicked
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Navigate back to the previous activity
            }
        });

        // Set up the navigation button (currently a placeholder for future actions)
        ImageButton navButton = findViewById(R.id.nav_button);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle navigation button action (e.g., open navigation drawer or another action)
            }
        });
    }

    // Method to fetch leaderboard data from the Firebase Realtime Database
    private void fetchLeaderboardData() {
        // Add a listener to retrieve data from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear any existing leaderboard data before fetching new data
                leaderboardData.clear();

                // Iterate through each user in the database and retrieve their data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Extract user's first name, points, and word count from the database
                    String fName = snapshot.child("fName").getValue(String.class);
                    int points = snapshot.child("points").getValue(Integer.class);
                    int wordsCorrect = snapshot.child("wordsCorrect").getValue(Integer.class);

                    // Get the user's unique ID from the database
                    String uid = snapshot.getKey();

                    // Create a UserData object with the retrieved data and add it to the leaderboardData list
                    UserData userData = new UserData(fName, points, wordsCorrect, uid);
                    leaderboardData.add(userData);
                }

                // Sort the leaderboard data: first by points (descending), then by word count if points are equal
                Collections.sort(leaderboardData, new Comparator<UserData>() {
                    @Override
                    public int compare(UserData u1, UserData u2) {
                        int pointComparison = Integer.compare(u2.getPoints(), u1.getPoints());
                        if (pointComparison != 0) {
                            return pointComparison; // Compare by points
                        } else {
                            return Integer.compare(u2.getWordCount(), u1.getWordCount()); // Compare by word count
                        }
                    }
                });

                // Display the top 100 users on the leaderboard regardless of login status
                displayTop100Users();

                // Display the current user's rank if they are logged in, otherwise show a login prompt
                displayCurrentUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LeaderBoardActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to display the top 100 users on the leaderboard
    private void displayTop100Users() {
        // Remove any existing rows in the leaderboard table, keeping the header row intact
        leaderboardTable.removeViews(1, Math.max(0, leaderboardTable.getChildCount() - 1));

        int rank = 1;
        // Iterate through the sorted leaderboard data and display the top 100 users
        for (UserData userData : leaderboardData) {
            if (rank <= 100) {
                TableRow row = new TableRow(this); // Create a new table row

                // Create and configure a TextView for the user's name
                TextView nameView = new TextView(this);
                nameView.setText(userData.getFirstName());
                nameView.setPadding(8, 8, 8, 8);
                nameView.setTypeface(null, Typeface.BOLD);
                nameView.setTextSize(18); // Set text size

                // Create and configure a TextView for the user's points
                TextView pointsView = new TextView(this);
                pointsView.setText(String.valueOf(userData.getPoints()));
                pointsView.setPadding(8, 8, 8, 8);
                pointsView.setTextSize(18); // Set text size

                // Create and configure a TextView for the user's word count
                TextView wordCountView = new TextView(this);
                wordCountView.setText(String.valueOf(userData.getWordCount()));
                wordCountView.setPadding(8, 8, 8, 8);
                wordCountView.setTextSize(18); // Set text size

                // Add the TextViews to the TableRow
                row.addView(nameView);
                row.addView(pointsView);
                row.addView(wordCountView);

                // Set unique background colors and text color for the top 3 ranks
                if (rank == 1) {
                    row.setBackgroundResource(R.drawable.rounded_background_gold); // Gold for 1st place
                    row.setPadding(8, 65, 8, 65); // Add padding to make it stand out more
                    setRowTextColor(row, ContextCompat.getColor(this, android.R.color.black));
                } else if (rank == 2) {
                    row.setBackgroundResource(R.drawable.rounded_background_silver); // Silver for 2nd place
                    row.setPadding(8, 55, 8, 55); // Add padding to make it stand out more
                    setRowTextColor(row, ContextCompat.getColor(this, android.R.color.black));
                } else if (rank == 3) {
                    row.setBackgroundResource(R.drawable.rounded_background_bronze); // Bronze for 3rd place
                    row.setPadding(8, 45, 8, 45); // Add padding to make it stand out more
                    setRowTextColor(row, ContextCompat.getColor(this, android.R.color.black));
                } else {
                    row.setBackgroundResource(R.drawable.rounded_background); // Default background for other ranks
                    setRowTextColor(row, ContextCompat.getColor(this, android.R.color.white));
                }

                // Add the TableRow to the leaderboardTable
                leaderboardTable.addView(row);
            }
            rank++; // Increment the rank for the next user
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


    // Method to display the current user's rank and stats
    private void displayCurrentUser() {
        // Clear any existing user data rows in the user table
        userTable.removeAllViews();

        // Get the current user's unique ID from Firebase authentication
        String currentUserUid = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : null;

        if (currentUserUid != null) {
            // Iterate through the leaderboard data to find the current user's data
            for (int i = 0; i < leaderboardData.size(); i++) {
                UserData userData = leaderboardData.get(i);
                if (userData.getUid().equals(currentUserUid)) {
                    // Create a new table row to display the current user's data
                    TableRow userRow = new TableRow(this);
                    userRow.setGravity(Gravity.CENTER); // Center the content of the TableRow

                    int rank = i + 1; // Calculate the rank of the current user

                    // If the user is ranked 1st, show a trophy icon behind their rank
                    if (rank == 1) {
                        // Create a FrameLayout to overlay the trophy icon behind the rank
                        FrameLayout frameLayout = new FrameLayout(this);

                        // Create an ImageView for the trophy icon
                        ImageView trophyIcon = new ImageView(this);
                        trophyIcon.setImageResource(R.drawable.trophy); // Set the trophy icon image

                        // Set the size of the trophy icon
                        FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(150, 150);
                        iconParams.gravity = Gravity.CENTER; // Center the icon
                        trophyIcon.setLayoutParams(iconParams);

                        // Create and configure a TextView for the user's rank
                        TextView rankView = new TextView(this);
                        rankView.setText(String.valueOf(rank));
                        rankView.setGravity(Gravity.CENTER); // Center the text within the FrameLayout
                        rankView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                        rankView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        rankView.setTypeface(null, Typeface.BOLD);
                        rankView.setTextSize(22); // Set text size

                        // Add the trophy icon and rank view to the FrameLayout
                        frameLayout.addView(trophyIcon);
                        frameLayout.addView(rankView);

                        // Add the FrameLayout to the TableRow
                        userRow.addView(frameLayout);
                    } else {
                        // Create and configure a TextView for the user's rank without the trophy icon
                        TextView rankView = new TextView(this);
                        rankView.setText(String.valueOf(rank));
                        rankView.setPadding(8, 8, 8, 8);
                        rankView.setGravity(Gravity.CENTER); // Center the text within the TextView
                        rankView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        rankView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        rankView.setTypeface(null, Typeface.BOLD);
                        rankView.setTextSize(22); // Set text size

                        // Add the rank TextView to the TableRow
                        userRow.addView(rankView);
                    }

                    // Set special background colors for the top ranks
                    if (rank == 1) {
                        userRow.setBackgroundResource(R.drawable.rounded_background_gold); // Gold for 1st place
                        setRowTextColor(userRow, ContextCompat.getColor(this, android.R.color.black));
                        playSoundEffect(R.raw.rank_1);
                    } else if (rank == 2) {
                        userRow.setBackgroundResource(R.drawable.rounded_background_silver); // Silver for 2nd place
                        setRowTextColor(userRow, ContextCompat.getColor(this, android.R.color.black));
                    } else if (rank == 3) {
                        userRow.setBackgroundResource(R.drawable.rounded_background_bronze); // Bronze for 3rd place
                        setRowTextColor(userRow, ContextCompat.getColor(this, android.R.color.black));
                    } else {
                        userRow.setBackgroundResource(R.drawable.rounded_background); // Default background for other ranks
                    }

                    // Add the TableRow to the userTable
                    userTable.addView(userRow);
                    break; // Exit the loop after finding and displaying the current user's data
                }
            }
        } else {
            // If the user is not logged in, show a message prompting them to log in
            TextView userInfoTextView = new TextView(this);
            userInfoTextView.setText("Log in to see your rank and stats on the leaderboard.");
            userInfoTextView.setPadding(16, 16, 16, 16);
            userInfoTextView.setTextSize(18); // Set text size
            userInfoTextView.setGravity(Gravity.CENTER);
            userInfoTextView.setBackgroundResource(R.drawable.rounded_background);
            userInfoTextView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            userTable.addView(userInfoTextView);
        }
    }

    // Inner class to represent user data for the leaderboard
    private static class UserData {
        private String fName; // User's first name
        private int points; // User's points
        private int wordsCorrect; // User's word count
        private String uid; // User's unique ID

        // Constructor for UserData
        public UserData(String fName, int points, int wordsCorrect, String uid) {
            this.fName = fName;
            this.points = points;
            this.wordsCorrect = wordsCorrect;
            this.uid = uid;
        }

        // Getter for the user's first name
        public String getFirstName() {
            return fName;
        }

        // Getter for the user's points
        public int getPoints() {
            return points;
        }

        // Getter for the user's word count
        public int getWordCount() {
            return wordsCorrect;
        }

        // Getter for the user's unique ID
        public String getUid() {
            return uid;
        }
    }

    private void playSoundEffect(int soundResourceId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, soundResourceId);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
