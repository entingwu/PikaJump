package edu.neu.madcourse.pikachujump;

import android.app.Activity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Window;
import android.view.WindowManager;
import android.util.Log;

public class GameActivity extends Activity implements SensorEventListener {

    public static final String TAG = "GameActivity";
    public static final String KEY_RESTORE = "key_restore";
    private static final float threshold = 5;
    public GameView gameView;
    private SensorManager mSensorManager;
    private GameFragment mGameFragment;

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
            Log.i(TAG, String.valueOf(accelerationSquareRoot));
            if (accelerationSquareRoot >= threshold && Math.abs(y) >= threshold) {
                gameView.setJumpTrue();
                if (y >= 10) {
                    gameView.moveLeft();
                }
                if (y <= -10) {
                    gameView.moveRight();
                }
                return;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
        mSensorManager.unregisterListener(this);
    }
}