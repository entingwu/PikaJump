package edu.neu.madcourse.pikachujump;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;

import java.util.Calendar;

public class GameActivity extends Activity implements SensorEventListener {

    public static final String TAG = "GameActivity";
    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    public GameView mGameView;
    private SensorManager mSensorManager;
    private AlertDialog.Builder mBuilder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        initialGame();
        mGameView = new GameView(this);

        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        Log.d(TAG, "restore = " + restore);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE).getString(PREF_RESTORE, null);
            if (gameData != null) {
                mGameView.putState(gameData);
            }
        }
        setContentView(mGameView);

        Log.i(TAG, "Initialize Sensor Manager");
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mBuilder = new AlertDialog.Builder(this);
        GameUtils.playMusic(this, R.raw.pika_bgm);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float [] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            float accelerationSquareRoot =
                    (x * x + y * y + z * z) /
                            (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
            // Left, right: improve the jump experience for left and right movement
            // if (Math.abs(y) > Math.abs(x))
            if (accelerationSquareRoot > 1) {
                float deltaX = Math.min(Math.abs(y), GameUtils.maxVelX);
                mGameView.pikachu.setVelX(mGameView.pikachu.getVelX() + deltaX);
                mGameView.y = y;
                Log.i(TAG, "Accelerometer: x=" + x + ", y=" + y + ", z=" + z + ", a="
                        + accelerationSquareRoot);

            }
            // Up
            if (accelerationSquareRoot >= GameUtils.thresholdY  &&
                    Math.abs(x) > GameUtils.thresholdY && Math.abs(x) > Math.abs(y)) {
                float deltaY = Math.min(Math.abs(x), GameUtils.maxVelY);
                //increase velocity on y by increasing deltaY
                mGameView.pikachu.setVelY(mGameView.pikachu.getVelY() + deltaY * 2);
                mGameView.pikachu.setJumping(true);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void win() {
        /** 1. Display Dialog */
        if (GameUtils.WIN) {
            mBuilder.setTitle(R.string.reportTitile);
            mBuilder.setMessage(generateReport());

        } else {
            mBuilder.setMessage(String.format("Try again! Your score is: %s", GameUtils.score));
        }
        GameUtils.setHasRestore(false);
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.main_menu_label,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.i(TAG, "Pika Jump Close");
                    finish();
                    onBackPressed();
                }
            });
        mBuilder.show();
        GameUtils.score = 0;
    }

    public String generateReport() {
        String mydate = java.text.DateFormat.getDateTimeInstance().
                format(Calendar.getInstance().getTime());
        GameUtils.setCurrentDataTime(mydate);

        String result = getResources().getString(R.string.recoredDateTime)
                        + mydate + "\n"
                        + getResources().getString(R.string.totalJumps) + " "  + GameUtils.jumps + "\n"
                        + getResources().getString(R.string.appleEaten) + " " + GameUtils.apples + "\n"
                        + getResources().getString(R.string.bananaEaten) + " " + GameUtils.bananas + "\n"
                        + getResources().getString(R.string.cokeEaten) + " " + GameUtils.cokes + "\n"
                        + "\n" + getResources().getString(R.string.enjoyText);
        return result;
    }

    private void initialGame() {
        // Get a Display object to access screen details
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        GameUtils.mWidth = size.x;
        GameUtils.mHeight = size.y;
        GameUtils.frameWidth = (int)(GameUtils.mWidth / 2.5);
        GameUtils.frameHeight = GameUtils.frameWidth;
        GameUtils.dx = GameUtils.mWidth / 120;
        GameUtils.maxVelX = GameUtils.mHeight / 15;
        GameUtils.maxVelY = GameUtils.mHeight / 8;

        // Bitmap
        GameUtils.bitmapPika = BitmapFactory.decodeResource(getResources(),
                R.drawable.pika_sprite_8_384);
        GameUtils.bitmapPika = Bitmap.createScaledBitmap(GameUtils.bitmapPika,
                GameUtils.frameWidth * GameUtils.frameCount, GameUtils.frameHeight, false);
        GameUtils.apple = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
        GameUtils.banana = BitmapFactory.decodeResource(getResources(), R.drawable.banana);
        GameUtils.coke = BitmapFactory.decodeResource(getResources(), R.drawable.coke);

        GameUtils.bitmapRestart = BitmapFactory.decodeResource(getResources(), R.drawable.restart);
        GameUtils.bitmapRestart = Bitmap.createScaledBitmap(GameUtils.bitmapRestart,
                GameUtils.mWidth / 12, GameUtils.mWidth / 12, false);
        GameUtils.bitmapPause = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
        GameUtils.bitmapPause = Bitmap.createScaledBitmap(GameUtils.bitmapPause,
                GameUtils.mWidth / 15, GameUtils.mWidth / 15, false);

        // Game Data
        GameUtils.WIN = true;
        GameUtils.score = 0;
        GameUtils.apples = 0;
        GameUtils.bananas = 0;
        GameUtils.cokes = 0;
        GameUtils.jumps = 0;
        GameUtils.totalSec = GameUtils.totalTime;
        GameUtils.visibleFruit = 0;
    }

    public void finish() {
        super.finish();
        mGameView.timer.cancel();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        mGameView.resume();
        mSensorManager.registerListener(this, mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameView.pause();
        String gameData = mGameView.getState();
        getPreferences(MODE_PRIVATE).edit().putString(PREF_RESTORE, gameData).commit();
        Log.i(TAG, "onPause: " + gameData);
        mSensorManager.unregisterListener(this);
    }
}