package com.example.wordworld;
import java.util.Random;

public class WordGame {

    private String chosenWord;
    private int attempts;
    private String[] wordList;
    private String feedback;

    public WordGame(String[] wordList) {
        this.wordList = wordList;
        this.chosenWord = getRandomWord(wordList);
        this.attempts = 5;
        this.feedback = "";
    }

    //word strings per difficulty level
    public static String[] diffOneWords = {"CATS", "DOGS"};
    public static String[] diffTwoWords = {"fruit", "fishy"};
    public static String[] diffThreeWords = {"apples", "grapes"};

    //starting game logic
    public void startGame() {
        this.attempts = 5;
        this.chosenWord = getRandomWord(wordList);
        this.feedback = "";
    }

    // Method to handle the user's guess
    public String handleGuess(String userInput) {
        if (userInput.length() != chosenWord.length()) {
            return "Your guess must be " + chosenWord.length() + " letters long.";
        }

        feedback = gamePlay(chosenWord, userInput);

        if (feedback.equalsIgnoreCase(chosenWord)) {
            return "Congratulations! You've guessed the word: " + chosenWord;
        } else {
            attempts--;

            if (attempts <= 0) {
                return "Sorry, you've run out of attempts. The word was: " + chosenWord;
            } else {
                return feedback + "\nAttempts left: " + attempts;
            }
        }
    }

    //method grabbing random words for selected difficulty
    public static String getRandomWord(String[] wordList){
        Random rand = new Random();
        return wordList[rand.nextInt(wordList.length)];
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
}
