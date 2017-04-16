package edu.neu.madcourse.pikachujump;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;

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
        mGameView = new GameView(this);

        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        Log.d("UT3", "restore = " + restore);
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
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float [] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            float accelerationSquareRoot =
                    (x * x + y * y) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
            if (Math.abs(y) > Math.abs(x)) {
                float deltaX = Math.min(Math.abs(y), GameUtils.maxVel);
                mGameView.pikachu.setVelX(mGameView.pikachu.getVelX() + deltaX);
                mGameView.y = y;
                Log.i(TAG, "Accelerometer: x=" + x + ", y=" + y + ", z=" + z + ", acc=" + accelerationSquareRoot);
            }
            if (Math.abs(x - SensorManager.GRAVITY_EARTH) > GameUtils.threshold
                    && Math.abs(x) > GameUtils.threshold && Math.abs(x) > Math.abs(y)) {
                float deltaY = Math.min(Math.abs(x), GameUtils.maxVel);
                mGameView.pikachu.setVelY(mGameView.pikachu.getVelY() + deltaY);
                mGameView.setJumpTrue();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void win() {
        /** 1. Display Dialog */
        mBuilder.setMessage(String.format("Good job! Your score is: %s", GameUtils.score));
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.main_menu_label,
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.i(TAG, "Pika Jump Winner");
                    finish();
                    onBackPressed();
                }
            });
        mBuilder.show();
        GameUtils.score = 0;
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
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
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