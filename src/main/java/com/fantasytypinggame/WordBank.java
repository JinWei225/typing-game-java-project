package com.fantasytypinggame;

public class WordBank {

    private static String[] easyWords = {
        "fire",
        "rock",
        "paid",
        "red",
        "blue",
        "sad",
        "shift",
        "cat",
        "dog",
        "sun",
        "moon",
        "tree",
        "book",
        "desk",
        "lamp",
        "shoe",
        "fish",
        "bird",
        "frog",
        "leaf",
        "star",
        "wind",
        "rain",
        "snow",
        "sand",
        "rock",
        "wave",
    };
    private static String[] mediumWords = {
        "dragon",
        "castle",
        "medium",
        "weight",
        "height",
        "knight",
        "jungle",
        "planet",
        "forest",
        "bridge",
        "window",
        "camera",
        "rocket",
        "island",
        "shadow",
        "garden",
        "castle",
        "mirror",
        "turtle",
        "rabbit",
        "stream",
        "valley",
        "canyon",
        "desert",
        "meadow",
        "glacier",
    };
    private static String[] bossWords = {
        "necromancer",
        "thunderstrike",
        "firefighter",
        "encyclopedia",
        "adventure",
        "butterfly",
        "challenge",
        "discovery",
        "education",
        "fantastic",
        "generator",
        "happiness",
        "important",
        "knowledge",
        "landscape",
        "microscope",
        "navigation",
        "orchestra",
        "philosophy",
        "restaurant",
        "technology",
        "university",
        "volcanoes",
        "waterfalls",
    };

    public static String[] getEasyWords() {
        return easyWords;
    }

    public static String[] getMediumWords() {
        return mediumWords;
    }

    public static String[] getBossWords() {
        return bossWords;
    }

    public static String getRandomWord(String[] wordArray) {
        int index = (int) (Math.random() * wordArray.length);
        return wordArray[index];
    }
}
