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
    public float y;// velX

    // Fruit array
    private Fruit[] fruits = new Fruit[GameUtils.totalFruits];
    private int numFruits = 0;
    private Random random;

    // Game is paused at the start
    public boolean paused;
    public CountDownTimer timer;
    private String timerText = "";
    private long timeThisFrame;

    // Time that last frame has changed
    private long lastFrameChangeTime = 0;

    // A rectangle to define an area of the sprite sheet that represents 1 frame
    private Rect frameToDraw = new Rect(0, 0, GameUtils.frameWidth, GameUtils.frameHeight);
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
        whereToDraw = new RectF(pikachu.getPosX() - GameUtils.frameWidth/2,
                      (int)pikachu.getPosY() - GameUtils.frameHeight/2,
                      pikachu.getPosX() + GameUtils.frameWidth/2,
                      (int)pikachu.getPosY() + GameUtils.frameHeight/2);

        bitmapPika = BitmapFactory.decodeResource(getResources(), R.drawable.pika_sprite_8_384);
        bitmapPika = Bitmap.createScaledBitmap(bitmapPika,
                GameUtils.frameWidth * GameUtils.frameCount, GameUtils.frameHeight, false);
        apple = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        banana = BitmapFactory.decodeResource(getResources(), R.drawable.banana);
        coke = BitmapFactory.decodeResource(getResources(), R.drawable.coke);
        createFruitsAndRestart();

        initTimer(60000);
    }

    public void createFruitsAndRestart() {
        Log.i(TAG, "Create Fruits: " + fruits.length);
        numFruits = 0;
        for (int column = 0; column < 13; column++) {
            for (int row = 0; row < 4; row++) {
                int type = random.nextInt(6);
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
    }

    public void initTimer(long leftTime) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(leftTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                GameUtils.totalSec = millisUntilFinished / 1000;
                long mins = GameUtils.totalSec / 60;
                long secs = GameUtils.totalSec % 60;
                timerText = (mins < 10? "0" + mins : mins) + ":" + (secs < 10? "0" + secs : secs);
            }

            @Override
            public void onFinish() {
                timerText = "00:00";
                pause();
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
            if (RectF.intersects(pikaRect, fruit.getFruit()) && fruit.getVisibility()) {
                FruitType type = fruit.getFruitType();
                if(type == FruitType.APPLE) {
                    GameUtils.apples++;
                } else if (type == FruitType.BANANA) {
                    GameUtils.bananas++;
                } else if (type == FruitType.COKE) {
                    GameUtils.cokes++;
                }
                GameUtils.score += fruit.getFruitScore();
                fruit.setInvisible();
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
            whereToDraw.set(pikachu.getPosX() - GameUtils.frameWidth/2,
                           (int)pikachu.getPosY() - GameUtils.frameHeight/2,
                            pikachu.getPosX() + GameUtils.frameWidth/2,
                           (int)pikachu.getPosY() + GameUtils.frameHeight/2);

            // Draw dynamic visible fruits
            drawFruits();
            // Draw the score
            paint.setTextSize(30);
            canvas.drawText("Score: " + GameUtils.score, 3, mHeight - 3, paint);
            // Draw the timer
            canvas.drawText(timerText, mWidth - 75, mHeight - 3, paint);

            // Update frameToDraw
            getCurrentFrame();
            canvas.drawBitmap(bitmapPika, frameToDraw, whereToDraw, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void drawFruits() {
        for (int i = 0; i < numFruits; i++) {
            Fruit fruit = fruits[i];
            if (fruit.getVisibility()) {
                FruitType type = fruits[i].getFruitType();
                RectF rectF = fruit.getFruit();
                float nextPos = rectF.centerX() + GameUtils.dx;
                if (nextPos > mWidth) {
                    nextPos = nextPos - mWidth;
                }
                float rounded = nextPos;
                float left = rounded - rectF.width() * 0.5f ;
                float right = rounded + rectF.width()  * 0.5f;
                float top = rectF.centerY() - rectF.height()  * 0.5f;
                float bottom = rectF.centerY() + rectF.height() * 0.5f;

                RectF newRectF = new RectF(left, top, right, bottom);
                switch (type) {
                    case APPLE:
                        canvas.drawBitmap(apple, null, newRectF, paint);
                        break;
                    case BANANA:
                        canvas.drawBitmap(banana, null, newRectF, paint);
                        break;
                    case COKE:
                        canvas.drawBitmap(coke, null, newRectF, paint);
                        break;
                }
                fruit.setFruit(newRectF);
            }
        }
    }

    public void getCurrentFrame() {
        long time = System.currentTimeMillis();
        int currentFrame = pikachu.getCurrentFrame();
        if (pikachu.isJumping()) {
            if (time > lastFrameChangeTime + GameUtils.frameLengthInMilliseconds) {
                Log.i(TAG, "currentFrame: " + currentFrame);
                lastFrameChangeTime = time;
                currentFrame++;
                pikachu.setCurrentFrame(currentFrame);
                if (currentFrame >= GameUtils.frameCount) {
                    currentFrame = 0;
                    pikachu.setCurrentFrame(currentFrame);
                    pikachu.setJumping(false);
                }
            }
            if (y < -GameUtils.threshold) {
                pikachu.moveLeft();
            }
            if (y > GameUtils.threshold) {
                pikachu.moveRight();
            }
        }
        //update the left and right values of the source of the next frame on the sprite sheet
        frameToDraw.left = currentFrame * GameUtils.frameWidth;
        frameToDraw.right = frameToDraw.left + GameUtils.frameWidth;
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
        GameUtils.totalSec = GameUtils.totalSec <= 1 ? 60 : GameUtils.totalSec;
        Log.i(TAG, "Resume." + GameUtils.totalSec);
        playGame = true;
        gameThread = new Thread(this);
        gameThread.start();
        initTimer(GameUtils.totalSec * 1000);
    }

    /** totalSec, score, apples, bananas, cokes */
    public String getState() {
        StringBuilder sb = new StringBuilder();
        sb.append(GameUtils.totalSec).append(GameUtils.DIV);
        sb.append(GameUtils.score).append(GameUtils.DIV);
        sb.append(GameUtils.apples).append(GameUtils.DIV);
        sb.append(GameUtils.bananas).append(GameUtils.DIV);
        sb.append(GameUtils.cokes).append(GameUtils.DIV);
        return sb.toString();
    }

    public void putState(String gameData) {
        String[] data = gameData.split(GameUtils.DIV);
        GameUtils.totalSec = Integer.parseInt(data[0]);
        GameUtils.score = Integer.parseInt(data[1]);
        GameUtils.apples = Integer.parseInt(data[2]);
        GameUtils.bananas = Integer.parseInt(data[3]);
        GameUtils.cokes = Integer.parseInt(data[4]);
    }

    public void setJumpTrue() {
        Log.i(TAG,"Pikachu Jump Up.");
        pikachu.setJumping(true);
    }
}