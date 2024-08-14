package com.example.wordworld;
import java.util.Random;

public class WordGame {
    //word strings per difficulty level
    public static String[] diffOneWords = {"CATS", "DOGS"};
    public static String[] diffTwoWords = {"fruit", "fishy"};
    public static String[] diffThreeWords = {"apples", "grapes"};

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
