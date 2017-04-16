package edu.neu.madcourse.pikachujump;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.util.Log;
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
    private Bitmap apple;
    private Bitmap banana;
    private Bitmap coke;

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
    private SoundPool mSoundPool;
    private int mSoundApple, mSoundBanana, mSoundCoke, mSoundMiss;

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        paint = new Paint();
        random = new Random();
        pikachu = new Pikachu(GameUtils.mWidth / 2, GameUtils.mHeight / 2);
        GameUtils.frameToDraw = new Rect(0, 0, GameUtils.frameWidth, GameUtils.frameHeight);
        GameUtils.whereToDraw = new RectF(pikachu.getPosX() - GameUtils.frameWidth / 2,
                                          pikachu.getPosY() - GameUtils.frameHeight / 3,
                                          pikachu.getPosX() + GameUtils.frameWidth / 2,
                                          pikachu.getPosY() + GameUtils.frameHeight / 3 * 2);

        GameUtils.bitmapPika = BitmapFactory.decodeResource(getResources(), R.drawable.pika_sprite_8_384);
        GameUtils.bitmapPika = Bitmap.createScaledBitmap(GameUtils.bitmapPika,
                GameUtils.frameWidth * GameUtils.frameCount, GameUtils.frameHeight, false);
        apple = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        banana = BitmapFactory.decodeResource(getResources(), R.drawable.banana);
        coke = BitmapFactory.decodeResource(getResources(), R.drawable.coke);
        createFruitsAndRestart();
        initTimer(60000);

        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundApple = mSoundPool.load(context, R.raw.sergenious_movex, 1);
        mSoundBanana = mSoundPool.load(context, R.raw.sergenious_moveo, 1);
        mSoundCoke = mSoundPool.load(context, R.raw.joanne_rewind, 1);
        mSoundMiss = mSoundPool.load(context, R.raw.erkanozan_miss, 1);
    }

    public void createFruitsAndRestart() {
        numFruits = 0;
        GameUtils.jumps = 0;
        for (int column = 0; column < 13; column++) {
            for (int row = 0; row < 4; row++) {
                int type = random.nextInt(6);
                if (type == 0 || type == 1) {// apple
                    fruits[numFruits] = new Fruit(row, column, GameUtils.mWidth, GameUtils.mHeight, FruitType.APPLE);
                    GameUtils.visibleFruit++;
                } else if (type == 2) {// banana
                    fruits[numFruits] = new Fruit(row, column, GameUtils.mWidth, GameUtils.mHeight, FruitType.BANANA);
                    GameUtils.visibleFruit++;
                } else if (type == 3) {// coke
                    fruits[numFruits] = new Fruit(row, column, GameUtils.mWidth, GameUtils.mHeight, FruitType.COKE);
                    GameUtils.visibleFruit++;
                } else {// invisible
                    fruits[numFruits] = new Fruit(row, column, GameUtils.mWidth, GameUtils.mHeight, FruitType.INVISIBLE);
                }
                numFruits++;
            }
        }
        Log.i(TAG, "Create visible Fruits: " + GameUtils.visibleFruit);
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
                if (secs < 10) {
                    mSoundPool.play(mSoundMiss, GameUtils.mVolume, GameUtils.mVolume,
                            1, 0, GameUtils.mRate);
                }
                if (GameUtils.visibleFruit == 0) {
                    pause();
                    ((GameActivity)getContext()).win();
                }
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
            RectF pikaRect = new RectF(pikachu.getPosX() - GameUtils.mWidth / 20,
                                       pikachu.getPosY() - GameUtils.mHeight / 20,
                                       pikachu.getPosX() + GameUtils.mWidth / 20,
                                       pikachu.getPosY() + GameUtils.mHeight / 20);
            if (RectF.intersects(pikaRect, fruit.getFruit()) && fruit.getVisibility()) {
                FruitType type = fruit.getFruitType();
                if(type == FruitType.APPLE) {
                    mSoundPool.play(mSoundApple, GameUtils.mVolume, GameUtils.mVolume,
                            1, 0, GameUtils.mRate);
                    GameUtils.apples++;
                    GameUtils.visibleFruit--;
                } else if (type == FruitType.BANANA) {
                    mSoundPool.play(mSoundBanana, GameUtils.mVolume, GameUtils.mVolume,
                            1, 0, GameUtils.mRate);
                    GameUtils.bananas++;
                    GameUtils.visibleFruit--;
                } else if (type == FruitType.COKE) {
                    mSoundPool.play(mSoundCoke, GameUtils.mVolume, GameUtils.mVolume,
                            1, 0, GameUtils.mRate);
                    GameUtils.cokes++;
                    GameUtils.visibleFruit--;
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
            GameUtils.whereToDraw.set(pikachu.getPosX() - GameUtils.frameWidth / 2,
                                      pikachu.getPosY() - GameUtils.frameHeight / 3,
                                      pikachu.getPosX() + GameUtils.frameWidth / 2,
                                      pikachu.getPosY() + GameUtils.frameHeight / 3 * 2);

            // Draw dynamic visible fruits
            drawFruits();
            // Draw the score
            paint.setTextSize(GameUtils.mWidth / 30);
            canvas.drawText("Score: " + GameUtils.score, 3, GameUtils.mHeight - 3, paint);
            // Draw the timer
            canvas.drawText(timerText, GameUtils.mWidth * 0.91f, GameUtils.mHeight - 3, paint);

            // Update frameToDraw
            getCurrentFrame();
            canvas.drawBitmap(GameUtils.bitmapPika, GameUtils.frameToDraw, GameUtils.whereToDraw, paint);
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
                if (nextPos > GameUtils.mWidth) {
                    nextPos = nextPos - GameUtils.mWidth;
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
                    GameUtils.jumps++;
                    pikachu.setCurrentFrame(currentFrame);
                    pikachu.setJumping(false);
                }
            }
            if (y < -GameUtils.thresholdX) {
                pikachu.moveLeft(y);
            }
            if (y > GameUtils.thresholdX) {
                pikachu.moveRight(y);
            }
        }
        //update the left and right values of the source of the next frame on the sprite sheet
        GameUtils.frameToDraw.left = currentFrame * GameUtils.frameWidth;
        GameUtils.frameToDraw.right = GameUtils.frameToDraw.left + GameUtils.frameWidth;
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

    /** totalSec, score, jumps, apples, bananas, cokes */
    public String getState() {
        StringBuilder sb = new StringBuilder();
        sb.append(GameUtils.totalSec).append(GameUtils.DIV);
        sb.append(GameUtils.score).append(GameUtils.DIV);
        sb.append(GameUtils.jumps).append(GameUtils.DIV);
        sb.append(GameUtils.apples).append(GameUtils.DIV);
        sb.append(GameUtils.bananas).append(GameUtils.DIV);
        sb.append(GameUtils.cokes).append(GameUtils.DIV);
        return sb.toString();
    }

    public void putState(String gameData) {
        String[] data = gameData.split(GameUtils.DIV);
        GameUtils.totalSec = Integer.parseInt(data[0]);
        GameUtils.score = Integer.parseInt(data[1]);
        GameUtils.jumps = Integer.parseInt(data[2]);
        GameUtils.apples = Integer.parseInt(data[3]);
        GameUtils.bananas = Integer.parseInt(data[4]);
        GameUtils.cokes = Integer.parseInt(data[5]);
    }
}