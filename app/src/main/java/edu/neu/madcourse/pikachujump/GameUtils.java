package edu.neu.madcourse.pikachujump;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.Log;

public class GameUtils {

    private static final String TAG = GameUtils.class.getSimpleName();
    public static final String DIV = ",";
    public static final String MODE_EASY = "EASY";
    public static final String MODE_HARD = "HARD";
    public static final String PAUSED = "Paused";
    public static final float thresholdX = 2;
    public static final float thresholdY = 2;

    public static boolean hasRestore;
    public static boolean isLogedIn;
    public static String username;
    public static String currentDataTime;

    // Music
    public static MediaPlayer mMediaPlayer;
    public static boolean isMusicPlaying = true;
    public static final float mVolume = 2f;
    public static final float mRate = 1f;
    public static int previousMusic;
    public static Context previousContext;
    private static long pauseLen;

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
    public static final long totalTime = 10;
    public static volatile boolean playGame;
    public static String mode = MODE_EASY;
    public static boolean WIN;
    public static long totalSec = 30;
    public static int apples;
    public static int bananas;
    public static int cokes;
    public static int score;
    public static int jumps;

    // Bitmap
    public static Bitmap bitmapPause;
    public static Bitmap bitmapRestart;
    public static Bitmap apple;
    public static Bitmap banana;
    public static Bitmap coke;

    public static void setHasRestore(boolean restore) {
        hasRestore = restore;
    }

    public static boolean getHasRestore() {
        return hasRestore;
    }

    public static boolean getIsLogedIn() {
        return isLogedIn;
    }
    public static void setIsLogedIn(boolean value) {
        isLogedIn = value;
    }
    public static void setUsername(String name) {
        username = name;
    }
    public static String getUsername() {
        return username;
    }
    public static void setCurrentDataTime(String dataTime) {
        currentDataTime = dataTime;
    }
    public static String getCurrentDataTime() {
        return currentDataTime;
    }

    public static void playMusic(Context context, int raw_id) {
        previousMusic = raw_id;
        previousContext = context;
        Log.i(TAG, "PlayMusic " + isMusicPlaying);

        if (isMusicPlaying) {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
            }
            mMediaPlayer = MediaPlayer.create(context, raw_id);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setVolume(mVolume, mVolume);
            mMediaPlayer.start();
        }
    }

    public static void pauseMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            pauseLen = mMediaPlayer.getCurrentPosition();
        }
    }

    public static void stopMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
    }
}