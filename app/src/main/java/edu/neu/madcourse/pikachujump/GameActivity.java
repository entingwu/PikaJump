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
    private static final float threshold = 5;
    private float maxVel = 5;
    public GameView gameView;
    private SensorManager mSensorManager;
    private AlertDialog.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        gameView = new GameView(this);
        setContentView(gameView);

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
            Log.i(TAG, "Accelerometer: x=" + x + ", y=" + y + ", z=" + z);

            float accelerationSquareRoot =
                    (x * x + y * y + z * z) / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
            if (Math.abs(y) > Math.abs(x)) {
                float deltaX = Math.min(Math.abs(y), maxVel);
                gameView.pikachu.setVelX(gameView.pikachu.getVelX() + deltaX);
                if (y < -threshold) {
                    gameView.moveLeft();
                }
                if (y > threshold) {
                    gameView.moveRight();
                }
            }
            if (accelerationSquareRoot >= threshold &&
                    Math.abs(x) > Math.abs(y) && Math.abs(x) > threshold) {
                float deltaY = Math.min(Math.abs(x), maxVel);
                gameView.pikachu.setVelY(gameView.pikachu.getVelY() + deltaY);
                gameView.setJumpTrue();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void win() {
        /** 1. Display Dialog */
        mBuilder.setMessage(String.format("Good job! Your score is: %s", gameView.score));
        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton(R.string.main_menu_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i(TAG, "Pika jump winner");
                        finish();
                        onBackPressed();
                    }
                });
        mBuilder.show();
    }

    public void finish() {
        super.finish();
        gameView.timer.cancel();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        gameView.resume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
        gameView.pause();
        mSensorManager.unregisterListener(this);
    }
}