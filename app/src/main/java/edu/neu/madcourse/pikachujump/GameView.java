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
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Random;

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

    // The size of the screen in pixels
    private int mWidth;
    private int mHeight;

    // Pikachu
    public Pikachu pikachu;
    private float maxVelX = 30;

    // Fruit array
    private Fruit[] fruits = new Fruit[300];
    private int numFruits = 0;
    private Random random;

    // Game is paused at the start
    public boolean paused;
    public int score;
    public CountDownTimer timer;
    private long totalSec;
    private String timerText = "";
    private long timeThisFrame;

    private int frameWidth = 384;
    private int frameHeight = 384;
    private int frameCount = 8;

    // Time that last frame has changed
    private long lastFrameChangeTime = 0;
    private int frameLengthInMilliseconds = 200;

    // A rectangle to define an area of the sprite sheet that represents 1 frame
    private Rect frameToDraw = new Rect(0, 0, frameWidth, frameHeight);
    // A rect that defines an area of the screen on which to draw
    private RectF whereToDraw;

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        paint = new Paint();
        random = new Random();

        // Get a Display object to access screen details
        Display display = ((GameActivity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mWidth = size.x;
        mHeight = size.y;
        pikachu = new Pikachu(mWidth / 2, mHeight / 2);
        whereToDraw = new RectF(pikachu.getPosX() - frameWidth/2, (int)pikachu.getPosY() - frameHeight/2,
                pikachu.getPosX() + frameWidth/2, (int)pikachu.getPosY() + frameHeight/2);

        bitmapPika = BitmapFactory.decodeResource(getResources(), R.drawable.pika_sprite_8_384);
        bitmapPika = Bitmap.createScaledBitmap(bitmapPika, frameWidth * frameCount, frameHeight, false);
        apple = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        banana = BitmapFactory.decodeResource(getResources(), R.drawable.banana);
        coke = BitmapFactory.decodeResource(getResources(), R.drawable.coke);
        createFruitsAndRestart();

        initTimer(60000);
    }

    public void createFruitsAndRestart() {
        numFruits = 0;
        for (int column = 0; column < 13; column++) {
            for (int row = 0; row < 4; row++) {
                int type = random.nextInt(5);
                if (type == 0 || type == 1) {// apple
                    fruits[numFruits] = new Fruit(row, column, mWidth, mHeight, FruitType.APPLE);
                } else if (type == 2) {// banana
                    fruits[numFruits] = new Fruit(row, column, mWidth, mHeight, FruitType.BANANA);
                } else if (type == 3) {// coke
                    fruits[numFruits] = new Fruit(row, column, mWidth, mHeight, FruitType.COKE);
                } else {// invisible
                    fruits[numFruits] = new Fruit(row, column, mWidth, mHeight, FruitType.INVISIBLE);
                }
                numFruits++;
            }
        }
        Log.i(TAG, String.valueOf(fruits.length));
    }

    public void initTimer(long leftTime) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(leftTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                totalSec = millisUntilFinished / 1000;
                Log.i(TAG, "@" + totalSec);
                long mins = totalSec / 60;
                long secs = totalSec % 60;
                timerText = (mins < 10? "0" + mins : mins) + ":" + (secs < 10? "0" + secs : secs);
            }

            @Override
            public void onFinish() {
                timerText = "00:00";
                ((GameActivity)getContext()).win();
            }
        }.start();
    }

    @Override
    public void run() {
        while (playGame) {
            if (!paused) {
                // Curr Time
                long startFrameTime = System.currentTimeMillis();
                // Update the frame
                update();
                // Draw the frame
                draw();
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    // Current frame per second
                    pikachu.setFps(1000 / timeThisFrame);
                }
            }
        }
    }

    public void update() {
        pikachu.update();
        // Check if pikachu eats a fruit
        for (int i = 0; i  < numFruits; ++i) {
            Fruit fruit = fruits[i];
            RectF pikaRect = new RectF(pikachu.getPosX() - 30, pikachu.getPosY() - 50,
                    pikachu.getPosX() + 30, pikachu.getPosY() + 50);
            if(RectF.intersects(pikaRect, fruit.getFruit())) {
                if(fruit.getVisibility()){
                    fruit.setInvisible();
                    score = score + fruit.getFruitScore();
                }
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

            // Draw the Pikachu
            whereToDraw.set(pikachu.getPosX() - frameWidth/2, (int)pikachu.getPosY() - frameHeight/2,
                    pikachu.getPosX() + frameWidth/2, (int)pikachu.getPosY() + frameHeight/2);

            // Draw the visible fruits
            for (int i = 0; i < numFruits; i++) {
                if (fruits[i].getVisibility()) {
                    FruitType type = fruits[i].getFruitType();
                    switch (type) {
                        case APPLE:
                            canvas.drawBitmap(apple, null, fruits[i].getFruit(), paint);
                            break;
                        case BANANA:
                            canvas.drawBitmap(banana, null, fruits[i].getFruit(), paint);
                            break;
                        case COKE:
                            canvas.drawBitmap(coke, null, fruits[i].getFruit(), paint);
                            break;
                    }
                }
            }

            // Draw the score
            paint.setTextSize(30);
            canvas.drawText("Score: " + score, 3, mHeight - 3, paint);
            // Draw the timer
            canvas.drawText(timerText, mWidth - 75, mHeight - 3, paint);

            // Update frameToDraw
            getCurrentFrame();
            canvas.drawBitmap(bitmapPika, frameToDraw, whereToDraw, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void getCurrentFrame() {
        long time = System.currentTimeMillis();
        int currentFrame = pikachu.getCurrentFrame();
        if (pikachu.isJumping()) {
            if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
                Log.i(TAG, "currentFrame: " + currentFrame);
                lastFrameChangeTime = time;
                currentFrame++;
                pikachu.setCurrentFrame(currentFrame);
                if (currentFrame >= frameCount) {
                    currentFrame = 0;
                    pikachu.setCurrentFrame(currentFrame);
                    pikachu.setJumping(false);
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
                if (!paused) {
                    paused = true;
                    pause();
                } else {
                    paused = false;
                    resume();
                }
                break;
        }
        return true;
    }

    public void pause() {
        Log.i(TAG, "Pause Game.");
        playGame = false;
        timer.cancel();
        try {
            gameThread.join();
        } catch(InterruptedException e) {
            Log.e(TAG, "Joining thread.");
        }
    }

    public void resume() {
        Log.i(TAG, "Resume." + totalSec);
        playGame = true;
        gameThread = new Thread(this);
        gameThread.start();
        //initTimer(totalSec * 1000);
    }

    public void setJumpTrue() {
        Log.i(TAG,"Pikachu Jump Up.");
        pikachu.setJumping(true);
    }

    public void moveLeft() {
        Log.i(TAG,"Pikachu left." + pikachu.getVelX());
        float mPosX = pikachu.getPosX() - Math.max(pikachu.getVelX(), maxVelX);
        mPosX = Math.max(30, mPosX);
        pikachu.setPosX(mPosX);
    }

    public void moveRight() {
        Log.i(TAG,"Pikachu right." + pikachu.getVelX());
        float mPosX = pikachu.getPosX() + Math.max(pikachu.getVelX(), maxVelX);
        mPosX = Math.min(mWidth - 30, mPosX);
        pikachu.setPosX(mPosX);
    }
}
