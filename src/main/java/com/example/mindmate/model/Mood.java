package com.example.mindmate.model;

public class Mood {
    private String mood;
    private String timestamp;

    public Mood() {}

    public Mood(String mood, String timestamp) {
        this.mood = mood;
        this.timestamp = timestamp;
    }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
