package edu.neu.madcourse.pikachujump;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    public static final String TAG = "GameView";
    public Thread gameThread = null;
    // Need surfaceHolder when use Paint and Canvas in a thread
    private SurfaceHolder surfaceHolder;
    private volatile boolean playGame;
    private Canvas canvas;
    private Paint paint;
    private Bitmap bitmapPika;
    private Bitmap apple;
    private Bitmap banana;
    private Bitmap coke;
    // Game is paused at the start
    private boolean paused;

    // Frames per second
    private long fps;
    private long timeThisFrame;
    private boolean isJumping = false;
    // Jump Speed Per Second
    private float mVelY = 350;

    // The size of the screen in pixels
    private int mWidth;
    private int mHeight;
    private float mPosX;
    private float mPosY;

    // Fruit array
    private Fruit[] fruits = new Fruit[300];
    private int numFruits = 0;
    private int score = 0;

    private int frameWidth = 384;
    private int frameHeight = 384;
    private int frameCount = 8;
    private int currentFrame = 0;
    // Time that last frame has changed
    private long lastFrameChangeTime = 0;
    private int frameLengthInMilliseconds = 200;

    // A rectangle to define an area of the sprite sheet that represents 1 frame
    private Rect frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
    // A rect that defines an area of the screen on which to draw
    RectF whereToDraw = new RectF(mPosX - frameWidth/2, (int)mPosY - frameHeight/2,
            mPosX + frameWidth/2, (int)mPosY + frameHeight/2);

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();

        // Get a Display object to access screen details
        Display display = ((GameActivity)context).getWindowManager().getDefaultDisplay();
        paint = new Paint();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x;
        mHeight = size.y;
        mPosX = mWidth / 2;
        mPosY = mHeight / 2;

        bitmapPika = BitmapFactory.decodeResource(getResources(), R.drawable.pika_sprite_8_384);
        bitmapPika = Bitmap.createScaledBitmap(bitmapPika, frameWidth * frameCount, frameHeight, false);
        apple = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        banana = BitmapFactory.decodeResource(getResources(), R.drawable.banana);
        coke = BitmapFactory.decodeResource(getResources(), R.drawable.coke);
        createFruitsAndRestart();
    }

    public void createFruitsAndRestart() {
        int fruitWidth = mWidth / 13;
        int fruitHeight = mHeight / 10;

        numFruits = 0;
        for (int column = 0; column < 13; column++) {
            for (int row = 0; row < 4; row++) {
                fruits[numFruits] = new Fruit(row, column, fruitWidth, fruitHeight);
                numFruits++;
            }
        }
        Log.i(TAG, String.valueOf(fruits.length));
    }

    @Override
    public void run() {
        while (playGame) {
            // Curr Time
            long startFrameTime = System.currentTimeMillis();
            // Update the frame
            if (!paused) {
                update();
            }

            // Draw the frame
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                // Current frame per second
                fps = 1000 / timeThisFrame;
            }
        }
    }

    public void update() {
        updatePikachu();
        // Check if pikachu eats a fruit
        for (int i = 0; i  < numFruits; ++i) {
            Fruit fruit = fruits[i];
            RectF pikachu = new RectF(mPosX - 30, mPosY - 50, mPosX + 30, mPosY + 50);
            if(RectF.intersects(pikachu, fruit.getFruit())) {
                if(fruit.getVisibility()){
                    fruit.setInvisible();
                    score = score + 10;
                }
            }
        }
    }

    public void updatePikachu() {
        // Move to the right place
        if (isJumping) {
            if (currentFrame >= 0 && currentFrame <= 3) {
                mPosY = mPosY - (mVelY / fps);
            } else if (currentFrame < 7) {
                mPosY = mPosY + (mVelY / fps);
            }
        }
    }

    public void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = surfaceHolder.lockCanvas();
            // Background
            Drawable d = getResources().getDrawable(R.drawable.background_1);
            d.setBounds(getLeft(), getTop(), getRight(), getBottom());
            d.draw(canvas);
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the pikachu
            whereToDraw.set(mPosX - frameWidth/2, (int)mPosY - frameHeight/2,
                    mPosX + frameWidth/2, (int)mPosY + frameHeight/2);

            // Draw the visible fruits
            for (int i = 0; i < numFruits; i++) {
                if (fruits[i].getVisibility()) {
                    canvas.drawBitmap(apple, null, fruits[i].getFruit(), paint);
                    //canvas.drawRect(fruits[i].getFruit(), paint);
                }
            }

            // Draw the score
            paint.setTextSize(30);
            canvas.drawText("Score: " + score, 3, mHeight - 3, paint);

            // Update frameToDraw
            getCurrentFrame();
            canvas.drawBitmap(bitmapPika, frameToDraw, whereToDraw, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void getCurrentFrame() {
        long time = System.currentTimeMillis();
        if (isJumping) {
            if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
                Log.i(TAG, "currentFrame: " + currentFrame);
                lastFrameChangeTime = time;
                currentFrame++;
                if (currentFrame >= frameCount) {
                    currentFrame = 0;
                    isJumping = false;
                }
            }
        }
        //update the left and right values of the source of the next frame on the sprite sheet
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                paused = true;
                break;
            case MotionEvent.ACTION_UP:
                paused = false;
                break;
        }
        return true;
    }

    public void pause() {
        playGame = false;
        try {
            gameThread.join();
        } catch(InterruptedException e) {
            Log.e(TAG, "Joining thread.");
        }
    }

    public void resume() {
        Log.i(TAG, "Resume.");
        playGame = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setJumpTrue() {
        Log.i(TAG,"Pikachu Jump Up.");
        isJumping = true;
    }

    public void moveLeft() {
        Log.i(TAG,"Pikachu left Up.");
        mPosX -= 100;
    }

    public void moveRight() {
        Log.i(TAG,"Pikachu right Up.");
        mPosX += 100;
    }
}
