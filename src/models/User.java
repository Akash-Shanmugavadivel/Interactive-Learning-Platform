package models;

public class User {
    private int id;
    private String username;
    private String password;
    private int streak;
    private int dailyGoalMinutes;
    private double progressPercentage;

    public User(int id, String username, String password, int streak, int dailyGoalMinutes, double progressPercentage) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.streak = streak;
        this.dailyGoalMinutes = dailyGoalMinutes;
        this.progressPercentage = progressPercentage;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getStreak() { return streak; }
    public int getDailyGoalMinutes() { return dailyGoalMinutes; }
    public double getProgressPercentage() { return progressPercentage; }
}

