package com.example.wordworld;
import java.util.Arrays;
import java.util.Random;
import com.example.wordworld.WordManagement;


public class WordGame {

    String chosenWord;
    private int attempts;
    private final WordManagement wordManagement; // Injected dependency
    private boolean[] revealedPositions; //To track which positions have been revealed
    //private String feedback;

    public WordGame(WordManagement wordManagement) {
        this.wordManagement = wordManagement;
        this.attempts = 5;
        //this.feedback = "";
    }


    //starting game logic
    public void startGame(int level) {
        String[] wordList = wordManagement.getRandomWord(level);
        Random random = new Random();
        int randomIndex = random.nextInt(wordList.length);
        this.attempts = 5;
        this.chosenWord = wordList[randomIndex];
        //this.feedback = "";

        // Initialize revealedPositions array for tracking revealed letters
        this.revealedPositions = new boolean[chosenWord.length()];
        Arrays.fill(this.revealedPositions, false); // All positions are initially unrevealed
    }

    // Method to handle the user's guess
    public Feedback handleGuess(String userInput) {
        if (userInput.length() != chosenWord.length()) {
            return new Feedback("Your guess must be " + chosenWord.length() + " letters long.",
                    attempts, userInput.toCharArray(), new int[chosenWord.length()]);
        }

        //feedback = gamePlay(chosenWord, userInput);

        int[] feedbackStatus = getFeedbackStatus(chosenWord, userInput);

        if (allCorrect(feedbackStatus)) {
            return new Feedback("Congratulations! You've guessed the word: \n" +
                    chosenWord, attempts, userInput.toCharArray(), feedbackStatus);
        } else {
            attempts--;

            if (attempts <= 0) {
                return new Feedback("Sorry, you've run out of attempts. The word was: " +
                        chosenWord, attempts, userInput.toCharArray(), feedbackStatus);
            } else {
                return new Feedback("", attempts, userInput.toCharArray(), feedbackStatus);
            }
        }
    }

    // Generate status for each letter: 0 = incorrect,
    // 1 = correct letter in wrong position, 2 = correct letter in correct position
    private static int[] getFeedbackStatus(String chosenWord, String userInput) {
        int[] status = new int[chosenWord.length()];
        boolean[] letterUsed = new boolean[chosenWord.length()];

        for (int i = 0; i < chosenWord.length(); i++) {
            if (userInput.charAt(i) == chosenWord.charAt(i)) {
                // Correct letter in correct position
                status[i] = 2;
                letterUsed[i] = true;
            } else {
                // Incorrect
                status[i] = 0;
            }
        }

        for (int i = 0; i < userInput.length(); i++) {
            // If not already correct
            if (status[i] == 0) {
                for (int j = 0; j < chosenWord.length(); j++) {
                    if (!letterUsed[j] && userInput.charAt(i) == chosenWord.charAt(j)) {
                        // Correct letter in wrong position
                        status[i] = 1;
                        letterUsed[j] = true;
                        break;
                    }
                }
            }
        }

        return status;
    }

    //method to help check if all letters are correct
    private static boolean allCorrect(int[] feedbackStatus) {
        for(int status: feedbackStatus) {
            //will return false if ANY letter is in the wrong position
            if (status != 2) {
                return false;
            }
        }
        //ALL letters are in correct position
        return true;
    }

    //New Hint Method
    //This method will reveal a correct letter in a random unrevealed position
    public Hint giveHint(){
        Random random = new Random();

        // Check if any unrevealed positions are left
        if (revealedPositions == null || revealedPositions.length == 0) {
            return new Hint("Hint system not initialized", null, -1);
        }

        int unrevealedCount = 0;
        for (boolean revealed : revealedPositions) {
            if (!revealed) unrevealedCount++;
        }

        if (unrevealedCount == 0) {
            // All positions have been revealed
            return new Hint("All letters are revealed", null, -1);
        }

        // Find an unrevealed position to provide a hint
        int hintPosition = -1;
        while (hintPosition == -1) {
            int randomIndex = random.nextInt(chosenWord.length());
            if (!revealedPositions[randomIndex]) {
                hintPosition = randomIndex;
                revealedPositions[randomIndex] = true; // Mark this position as revealed
            }
        }

        // Return a hint with the revealed letter and its position
        return new Hint("Here's a hint!", chosenWord.charAt(hintPosition), hintPosition);

    }


    // **Hint Class**
    // A class to encapsulate the hint result
    public static class Hint {
        public final String message;
        public final Character revealedLetter;
        public final int position;

        public Hint(String message, Character revealedLetter, int position) {
            this.message = message;
            this.revealedLetter = revealedLetter;
            this.position = position;
        }
    }

    // Feedback class to hold feedback message, attempts left, and status of each letter
    public static class Feedback {
        public final String message;
        public final int attemptsLeft;
        //guessed characters
        public final char[] feedbackChars;
        // 0 = Incorrect, 1 = Correct letter wrong position, 2 = Correct letter correct position
        public final int[] feedbackStatus;

        public Feedback(String message, int attemptsLeft, char[] feedbackChars, int[] feedbackStatus) {
            this.message = message;
            this.attemptsLeft = attemptsLeft;
            this.feedbackChars = feedbackChars;
            this.feedbackStatus = feedbackStatus;
        }
    }
}
