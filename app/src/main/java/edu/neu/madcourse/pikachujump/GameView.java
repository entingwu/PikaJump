package edu.neu.madcourse.pikachujump;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
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

    // Frames per second
    private long fps;
    private long timeThisFrame;
    private boolean isJumping = false;
    private float jumpSpeedPerSecond = 200;
    private int mWidth = this.getResources().getDisplayMetrics().widthPixels;
    private int mHeight = this.getResources().getDisplayMetrics().heightPixels;
    private float xPosition = 10;
    private float yPosition = 10;


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
    RectF whereToDraw = new RectF(xPosition, yPosition, xPosition + frameWidth, yPosition + frameHeight);

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        paint = new Paint();
        bitmapPika = BitmapFactory.decodeResource(getResources(), R.drawable.pika_sprite_8_384);
        bitmapPika = Bitmap.createScaledBitmap(bitmapPika, frameWidth * frameCount, frameHeight, false);
    }

    @Override
    public void run() {
        while (playGame) {
            // Curr Time
            long startFrameTime = System.currentTimeMillis();
            // Update the frame
            update();
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
        // Move to the right place
        if (isJumping) {
            if (currentFrame <= 3) {
                yPosition = yPosition - (jumpSpeedPerSecond / fps);
            } else {
                yPosition = yPosition + (jumpSpeedPerSecond / fps);
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
            paint.setColor(Color.argb(255, 249, 129, 0));
            paint.setTextSize(50);
            canvas.drawText("FPS:" + fps, 100, 200, paint);
            whereToDraw.set(xPosition, (int)yPosition, xPosition + frameWidth, (int)yPosition + frameHeight);

            // Update frameToDraw
            Log.i(TAG, "@" + whereToDraw.toString());
            getCurrentFrame();
            canvas.drawBitmap(bitmapPika, frameToDraw, whereToDraw, paint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void getCurrentFrame() {
        long time = System.currentTimeMillis();
        if (isJumping) {
            if (time > lastFrameChangeTime + frameLengthInMilliseconds) {
                lastFrameChangeTime = time;
                currentFrame++;
                if (currentFrame >= frameCount) {
                    currentFrame = 0;
                }
            }
        }
        //update the left and right values of the source of the next frame on the sprite sheet
        frameToDraw.left = currentFrame * frameWidth;
        frameToDraw.right = frameToDraw.left + frameWidth;
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

    /** The SurfaceView class implements onTouchListener
     *  So we can override this method and detect screen touches. */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isJumping = true;
                break;
            case MotionEvent.ACTION_UP:
                isJumping = false;
                break;
        }
        return true;
    }
}
