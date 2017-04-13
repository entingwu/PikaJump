package edu.neu.madcourse.pikachujump;

import android.graphics.RectF;

public class Fruit {

    private RectF fruit;
    private boolean isVisible;
    private int paddingRow = 4;
    private int paddingCol = 12;

    public Fruit(int row, int column, int width, int height) {
        isVisible = true;
        fruit = new RectF(column * width + paddingCol,
                         row * height + paddingRow,
                         column * width + width - paddingCol,
                         row * height + height - paddingRow);
    }

    public RectF getFruit() {
        return this.fruit;
    }

    public void setInvisible() {
        isVisible = false;
    }

    public boolean getVisibility() {
        return isVisible;
    }
}
