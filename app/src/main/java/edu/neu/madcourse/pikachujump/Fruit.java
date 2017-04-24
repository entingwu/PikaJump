package edu.neu.madcourse.pikachujump;

import android.graphics.RectF;

public class Fruit {

    private RectF fruit;
    private FruitType fruitType;
    private boolean isVisible;
    private int fruitScore;
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
                this.fruitScore = GameUtils.appleScore;
                break;
            case BANANA:
                this.width = mWidth / 11;
                this.height = mHeight / 10;
                this.fruitScore = GameUtils.bananaScore;
                break;
            case COKE:
                this.width = mWidth / 16;
                this.height = mHeight / 10;
                this.fruitScore = GameUtils.cokeScore;
                break;
            default:
                this.isVisible = false;
                break;
        }
        float maxWidth = mWidth / 11;
        float maxHeight = mHeight / 10;
        fruit = new RectF(column * maxWidth + GameUtils.paddingCol,
                row * maxHeight + GameUtils.paddingRow,
                column * maxWidth + this.width - GameUtils.paddingCol,
                row * maxHeight + this.height - GameUtils.paddingRow);
    }

    public RectF getFruit() {
        return this.fruit;
    }

    public void setFruit(RectF fruit) {
        this.fruit = fruit;
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

    public int getHeight() {
        return height;
    }
}
