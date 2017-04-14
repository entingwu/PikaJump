package edu.neu.madcourse.pikachujump;

import android.graphics.RectF;

public class Fruit {

    private RectF fruit;
    private FruitType fruitType;
    private boolean isVisible;

    private int fruitScore;
    private int paddingRow = 4;
    private int paddingCol = 12;

    // fruit size
    private int width;
    private int height;

    public Fruit(int row, int column, int mWidth, int mHeight, FruitType fruitType) {
        this.fruitType = fruitType;
        this.isVisible = true;

        switch (fruitType) {
            case APPLE:
                this.width = mWidth / 13;
                this.height = mHeight / 10;
                this.fruitScore = 10;
                break;
            case BANANA:
                this.width = mWidth / 11;
                this.height = mHeight / 10;
                this.fruitScore = 30;
                break;
            case COKE:
                this.width = mWidth / 16;
                this.height = mHeight / 10;
                this.fruitScore = -10;
                break;
            default:
                this.isVisible = false;
                break;
        }
        float maxWidth = mWidth / 11;
        float maxHeight = mHeight / 10;
        fruit = new RectF(column * maxWidth + paddingCol,
                row * maxHeight + paddingRow,
                column * maxWidth + this.width - paddingCol,
                row * maxHeight + this.height - paddingRow);
    }

    public RectF getFruit() {
        return this.fruit;
    }

    public void setFruit(RectF fruit) {
        this.fruit = fruit;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setInvisible() {
        isVisible = false;
    }

    public boolean getVisibility() {
        return isVisible;
    }

    public FruitType getFruitType() {
        return fruitType;
    }

    public int getFruitScore() {
        return fruitScore;
    }
}
