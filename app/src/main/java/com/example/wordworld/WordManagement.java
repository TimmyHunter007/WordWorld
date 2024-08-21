package com.example.wordworld;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;


public class WordManagement {
    private String[] fourLetterWords;
    private String[] fiveLetterWords;
    private String[] sixLetterWords;
    private Context context;

    public WordManagement(Context context) {
        this.context = context;
        loadFourLetterWords();
        loadFiveLetterWords();
        loadSixLetterWords();
    }


    private void loadFourLetterWords() {
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.four_letter_words);  // Check for resource existence
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                List<String> words = new ArrayList<>();
                String line;
                while ((line = reader.readLine())
                        != null) {
                    words.add(line);
                }
                fourLetterWords = words.toArray(new String[0]);
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadFiveLetterWords() {
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.five_letter_words);  // Check for resource existence
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                List<String> words = new ArrayList<>();
                String line;
                while ((line = reader.readLine())
                        != null) {
                    words.add(line);
                }
                fiveLetterWords = words.toArray(new String[0]);
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadSixLetterWords() {
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.six_letter_words);  // Check for resource existence
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                List<String> words = new ArrayList<>();
                String line;
                while ((line = reader.readLine())
                        != null) {
                    words.add(line);
                }
                sixLetterWords = words.toArray(new String[0]);
                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public String[] getRandomWord(int level) {
        // Get the current date (without time)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long seed = calendar.getTimeInMillis();
        Random random = new Random(seed);
        switch(level) {
            case 1:
                if (fourLetterWords != null && fourLetterWords.length > 0) {
                    Log.d("WordManagement", "Returning random four-letter word");
                    return new String[]{fourLetterWords[random.nextInt(fourLetterWords.length)]};
                } else {
                    Log.e("WordManagement", "fourLetterWords is null or empty");
                    return new String[]{"Error"};
                }
            case 2:
                if (fiveLetterWords != null && fiveLetterWords.length > 0) {
                    Log.d("WordManagement", "Returning random five-letter word");
                    return new String[]{fiveLetterWords[random.nextInt(fiveLetterWords.length)]};
                } else {
                    Log.e("WordManagement", "fiveLetterWords is null or empty");
                    return new String[]{"Error"};
                }
            case 3:
                if (sixLetterWords != null && sixLetterWords.length > 0) {
                    Log.d("WordManagement", "Returning random six-letter word");
                    return new String[]{sixLetterWords[random.nextInt(sixLetterWords.length)]};
                } else {
                    Log.e("WordManagement", "sixLetterWords is null or empty");
                    return new String[]{"Error"};
                }
            default:
                return new String[]{"No"};

        }
    }


}


