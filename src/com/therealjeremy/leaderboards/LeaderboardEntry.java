package com.therealjeremy.leaderboards;

public class LeaderboardEntry {

    private final String id;
    private final int value;

    public LeaderboardEntry(String id, int value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

}
