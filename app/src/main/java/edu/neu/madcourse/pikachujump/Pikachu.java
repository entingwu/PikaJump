package edu.neu.madcourse.pikachujump;

import android.graphics.RectF;

public class Pikachu {

    private RectF pikachu;
    private float mPosX;
    private float mPosY;

    // Jump Speed Per Second
    private float mVelY = 350;
    private int frameWidth = 384;
    private int frameHeight = 384;

    public Pikachu() {
        pikachu = new RectF();
    }

    public RectF getPikachu() {
        return pikachu;
    }

    public float getmPosX() {
        return mPosX;
    }

    public void setmPosX(float mPosX) {
        this.mPosX = mPosX;
    }

    public float getmPosY() {
        return mPosY;
    }

    public void setmPosY(float mPosY) {
        this.mPosY = mPosY;
    }
}
