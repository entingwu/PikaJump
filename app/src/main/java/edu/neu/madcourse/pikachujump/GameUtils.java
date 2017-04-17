package edu.neu.madcourse.pikachujump;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

public class GameUtils {

    public static final String DIV = ",";
    public static final String MODE_EASY = "EASY";
    public static final String MODE_HARD = "HARD";

    public static final float thresholdX = 3;
    public static final float thresholdY = 3;
    public static final float mVolume = 2f;
    public static final float mRate = 1f;

    // The size of the screen in pixels
    public static int mWidth;
    public static int mHeight;

    // Pikachu max mVelX
    public static float maxVelX = 8;
    public static float maxVelY = 20;
    public static int dx = 3;
    public static float dy = 0.5f;

    // Pikachu frame
    public static Bitmap bitmapPika;
    public static int frameWidth;
    public static int frameHeight;
    public static final int frameCount = 8;
    public static final int frameLengthInMilliseconds = 200;
    // A rectangle to define an area of the sprite sheet that represents 1 frame
    public static Rect frameToDraw;
    // A rect that defines an area of the screen on which to draw
    public static RectF whereToDraw;

    // Fruits
    public static final int paddingRow = 4;
    public static final int paddingCol = 12;

    public static final int totalFruits = 13 * 4;
    public static final int appleScore = 10;
    public static final int bananaScore = 30;
    public static final int cokeScore = -10;
    public static int visibleFruit;

    // Game Data
    public static String mode = MODE_EASY;
    public static boolean WIN;
    public static int brokenFruits;
    public static long totalSec = 60;
    public static int apples;
    public static int bananas;
    public static int cokes;
    public static int score;
    public static int jumps;
}
