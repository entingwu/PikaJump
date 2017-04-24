package edu.neu.madcourse.pikachujump;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String token;
    public String username;
    public Integer score;
    public String datePlayed;
    public int totalJumps;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String token, String username, Integer score, String datePlayed,
                Integer totalJumps){
        this.token = token;
        this.username = username;
        this.score = score;
        this.datePlayed = datePlayed;
        this.totalJumps = totalJumps;
    }

    @Override
    public String toString() {
        return "User{" +
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", score=" + score +
                ", datePlayed='" + datePlayed + '\'' +
                ", totalJumps=" + totalJumps +
                '}';
    }
}
