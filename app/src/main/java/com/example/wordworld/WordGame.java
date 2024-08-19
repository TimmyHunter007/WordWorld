package com.example.wordworld;
import java.util.Arrays;
import java.util.Random;
import com.example.wordworld.WordManagement;


public class WordGame {

    private String chosenWord;
    private int attempts;
    private final WordManagement wordManagement; // Injected dependency
    private String feedback;

    public WordGame(WordManagement wordManagement) {
        this.wordManagement = wordManagement;
        this.attempts = 5;
        this.feedback = "";
    }


    //starting game logic
    public void startGame(int level) {
        String[] wordList = wordManagement.getRandomWord(level);
        Random random = new Random();
        int randomIndex = random.nextInt(wordList.length);
        this.attempts = 5;
        this.chosenWord = wordList[randomIndex];
        this.feedback = "";
    }

    // Method to handle the user's guess
    public Feedback handleGuess(String userInput) {
        if (userInput.length() != chosenWord.length()) {
            return new Feedback("Your guess must be " + chosenWord.length() + " letters long.", attempts);
        }

        feedback = gamePlay(chosenWord, userInput);

        if (feedback.equalsIgnoreCase(chosenWord)) {
            return new Feedback("Congratulations! You've guessed the word: " + chosenWord, attempts);
        } else {
            attempts--;

            if (attempts <= 0) {
                return new Feedback("Sorry, you've run out of attempts. The word was: " + chosenWord, attempts);
            } else {
                return new Feedback(feedback, attempts);
            }
        }
    }


    //game logic for game play
    public static String gamePlay(String chosenWord, String userInput){
        char[] feedback = new char[chosenWord.length()];
        boolean[] letterUsed = new boolean[chosenWord.length()];

        // show feedback to user (temporary for testing)
        for(int i = 0; i < chosenWord.length(); i++){
            feedback[i] = '_';
        }

        //first pass checking for correct letters in correct position of chosen word
        for(int i = 0; i < chosenWord.length(); i++){
            if(userInput.charAt(i) == chosenWord.charAt(i)){
                feedback[i] = userInput.charAt(i);
                //mark the letter as being used
                letterUsed[i] = true;
            }
        }
        //second pass checking for correct letters in wrong position in chosen word
        for(int i = 0; i < userInput.length(); i++){
            //skip letters match already
            if(feedback[i] == '_'){
                for(int j = 0; j < chosenWord.length(); j++){
                    if(!letterUsed[j] && userInput.charAt(i) == chosenWord.charAt(j)){
                        feedback[i] = userInput.charAt(i);
                        letterUsed[j] = true;
                        break;
                    }
                }
            }
        }
        return new String(feedback);
    }

    public static class Feedback{
        public final String message;
        public final int attemptsLeft;

        public Feedback(String message, int attemptsLeft) {
            this.message = message;
            this.attemptsLeft = attemptsLeft;
        }
    }
}
