package com.usoroos.usorosyncprototype;

import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ExtractUrl {
    public static String extractUrl(String input) {
        List<String> result = new ArrayList<>();

        String[] words = input.split("\\s+");

        Pattern pattern = Patterns.WEB_URL;
        for (String word : words) {
            if (pattern.matcher(word).find()) {
                if (!word.toLowerCase().contains("http://") && !word.toLowerCase().contains("https://")) {
                    word = "http://" + word;
                }
                result.add(word);
            }
        }

        return result.get(0);
    }

}
