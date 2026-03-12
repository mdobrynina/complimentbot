package com.example.tgcompliment_bot.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class UserMoodTracker {

    private static class MoodData {
        int score = 0;
        LocalDate lastUpdate = LocalDate.now();
    }

    private final Map<Long, MoodData> userMood = new HashMap<>();

    private MoodData getData(long chatId) {
        MoodData data = userMood.get(chatId);
        if (data == null) {
            data = new MoodData();
            userMood.put(chatId, data);
        }
        // --- ежедневный сброс ---
        if (!data.lastUpdate.equals(LocalDate.now())) {
            data.score = 0;
            data.lastUpdate = LocalDate.now();
        }
        return data;
    }

    public void addPositive(long chatId) {
        MoodData data = getData(chatId);
        data.score++;
        data.lastUpdate = LocalDate.now();
    }

    public void addNegative(long chatId) {
        MoodData data = getData(chatId);
        data.score--;
        data.lastUpdate = LocalDate.now();
    }

    // --- новый метод для принудительной установки рейтинга ---
    public void setScore(long chatId, int score) {
        MoodData data = getData(chatId);
        data.score = score;
        data.lastUpdate = LocalDate.now();
    }

    public int getScore(long chatId) {
        return getData(chatId).score;
    }
}