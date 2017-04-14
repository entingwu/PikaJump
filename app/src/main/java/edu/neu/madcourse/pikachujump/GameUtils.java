package edu.neu.madcourse.pikachujump;

public class GameUtils {

    public static final String DIV = ",";
    public static final float threshold = 5;
    // Pikachu max mVelX
    public static final float maxVel = 15;
    public static final int dx = 3;

    // Pikachu frame
    public static final int frameWidth = 384;
    public static final int frameHeight = 384;
    public static final int frameCount = 8;
    public static final int frameLengthInMilliseconds = 200;

    // Fruits
    public static final int paddingRow = 4;
    public static final int paddingCol = 12;

    public static final int totalFruits = 3000;
    public static final int appleScore = 10;
    public static final int bananaScore = 30;
    public static final int cokeScore = -10;

    // Game Data
    public static long totalSec = 60;
    public static int apples;
    public static int bananas;
    public static int cokes;
    public static int score;
}