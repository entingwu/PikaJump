package edu.neu.madcourse.pikachujump;

import android.graphics.RectF;

public class Pikachu {

    private RectF pikachu;
    private float mPosX;
    private float mPosY;

    // Frames per second
    private long fps;
    private int currentFrame = 0;

    // Jump Speed Per Second
    private float mVelY = 350;
    private boolean isJumping = false;

    public Pikachu(float x, float y) {
        this.pikachu = new RectF();
        this.mPosX = x;
        this.mPosY = y;
    }

    public void update() {
        // Move to the right place
        if (isJumping) {
            if (currentFrame >= 0 && currentFrame <= 3) {
                this.mPosY = this.mPosY - mVelY / fps;
            } else if (currentFrame < 7) {
                this.mPosY = this.mPosY + mVelY / fps;
            }
        }
    }

    public float getPosX() {
        return mPosX;
    }

    public void setPosX(float mPosX) {
        this.mPosX = mPosX;
    }

    public float getPosY() {
        return mPosY;
    }

    public void setPosY(float mPosY) {
        this.mPosY = mPosY;
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean jumping) {
        isJumping = jumping;
    }

    public void setFps(long fps) {
        this.fps = fps;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }
}