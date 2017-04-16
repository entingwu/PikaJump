package edu.neu.madcourse.pikachujump;

import android.graphics.RectF;
import android.util.Log;

public class Pikachu {

    public static final String TAG = "Pikachu";
    private RectF pikachu;
    private float mPosX;
    private float mPosY;
    private float mWidth;
    private float mHeight;

    // Frames per second
    private long fps;
    private int currentFrame = 0;

    // Jump Speed Per Second
    private float mVelY;
    private float mVelX;
    private boolean isJumping = false;

    public Pikachu(float x, float y) {
        this.pikachu = new RectF();
        this.mPosX = x;
        this.mPosY = y;
        this.mWidth = 2 * x;
        this.mHeight = 2 * y;
    }

    public void update() {
        // Move to the right place
        if (isJumping) {
            if (currentFrame >= 0 && currentFrame <= 3) {// up
                this.mPosY = Math.max(this.mPosY - mVelY / fps, 0);
            } else if (currentFrame < 7) {// down
                this.mPosY = Math.min(this.mPosY + mVelY / fps, mHeight / 2);
            } else if (currentFrame == 7) {
                this.mPosY = mHeight / 2;
            }
        }
    }

    public void moveLeft(float value) {
        Log.i(TAG,"Left Jump." + mVelX);
        mPosX += value * 2;
        mPosX = Math.max(30, mPosX);
    }

    public void moveRight(float value) {
        Log.i(TAG,"Right Jump." + mVelX);
        mPosX += value * 2;
        mPosX = Math.min(mWidth - 30, mPosX);
    }

    public float getPosX() {
        return mPosX;
    }

    public float getPosY() { return mPosY; }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    public void setFps(long fps) {
        this.fps = fps;
    }

    public float getVelX() {
        return mVelX;
    }

    public void setVelX(float mVelX) {
        this.mVelX = mVelX;
    }

    public float getVelY() {
        return mVelY;
    }

    public void setVelY(float mVelY) {
        this.mVelY = mVelY;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }
}