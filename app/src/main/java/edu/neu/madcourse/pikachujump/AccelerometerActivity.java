package edu.neu.madcourse.pikachujump;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class AccelerometerActivity extends Activity {

    private SimulationView mSimulationView;
    private SensorManager mSensorManager;
    private PowerManager mPowerManager;
    private WindowManager mWindowManager;
    private Display mDisplay;
    private WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());
        mSimulationView = new SimulationView(this);
        //mSimulationView.setBackgroundResource(R.drawable.background_0);
        setContentView(mSimulationView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // When the activity is resumed, we acquire a wake-lock so that the screen stays on.
        mWakeLock.acquire();
        mSimulationView.startSimulation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // When the activity is paused, it stops the simulation and release sensor resources.
        mSimulationView.stopSimulation();
        // Release our wake-lock
        mWakeLock.release();
    }

    class SimulationView extends FrameLayout implements SensorEventListener {
        private static final float sBallDiameter = 0.015f;
        private int mDstWidth;
        private int mDstHeight;

        private Sensor mAccelerometer;
        private final Pikachu mPikachu;
        private long mLastT;

        private float mXDpi;// dpi / feet in x
        private float mYDpi;// dpi / feet in y
        private float mMetersToPixelsX;// dpi / m in x
        private float mMetersToPixelsY;// dpi / m in y
        private float mXOrigin;
        private float mYOrigin;
        private float mSensorX;// a in x
        private float mSensorY;// a in y
        private float mHorizontalBound;
        private float mVerticalBound;

        public SimulationView(Context context) {
            super(context);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // Screen size
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mXDpi = metrics.xdpi;
            mYDpi = metrics.ydpi;
            // 1.0 feet == 0.0254 m
            mMetersToPixelsX = mXDpi / 0.0254f;
            mMetersToPixelsY = mYDpi / 0.0254f;

            // Rescale the ball so it's about 0.5 cm on screen
            mDstWidth = (int) (sBallDiameter * mMetersToPixelsX + 0.5f);
            mDstHeight = (int) (sBallDiameter * mMetersToPixelsY + 0.5f);

            // Initialize a Pikachu
            mPikachu = new Pikachu(getContext());
            mPikachu.setBackgroundResource(R.drawable.pikachu_icon);
            mPikachu.setLayerType(LAYER_TYPE_HARDWARE, null);
            addView(mPikachu, new ViewGroup.LayoutParams(mDstWidth, mDstHeight));

            Options opts = new Options();
            opts.inDither = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
        }

        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            // compute the origin of the screen relative to the origin of the bitmap
            mXOrigin = (w - mDstWidth) * 0.5f;
            mYOrigin = (h - mDstHeight) * 0.5f;
            mHorizontalBound = (w / mMetersToPixelsX - sBallDiameter) * 0.5f;
            mVerticalBound = (h / mMetersToPixelsY - sBallDiameter) * 0.5f;
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
                return;
            }
            switch(mDisplay.getRotation()) {
                case Surface.ROTATION_0:
                    mSensorX = event.values[0];
                    mSensorY = event.values[1];
                    break;
                case Surface.ROTATION_90:
                    mSensorX = -event.values[1];
                    mSensorY = event.values[0];
                    break;
                case Surface.ROTATION_180:
                    mSensorX = -event.values[0];
                    mSensorY = -event.values[1];
                    break;
                case Surface.ROTATION_270:
                    mSensorX = event.values[1];
                    mSensorY = -event.values[0];
                    break;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        @Override
        protected void onDraw(Canvas canvas) {
            /* Compute the new position of our object, based on accelerometer
             * data and present time. */
            final Pikachu pikachu = mPikachu;
            final long now = System.currentTimeMillis();
            final float sx = mSensorX;
            final float sy = mSensorY;

            pikachu.update(sx, sy, now);
            final float xc = mXOrigin;
            final float yc = mYOrigin;
            final float xs = mMetersToPixelsX;
            final float ys = mMetersToPixelsY;

            /* We transform the canvas so that the coordinate system matches
             * the sensors coordinate system with the origin in the center
             * of the screen and the unit is the meter. */
            final float x = xc + pikachu.mPosX * xs;
            final float y = yc - pikachu.mPosY * ys;
            pikachu.setTranslationX(x);
            pikachu.setTranslationY(y);

            // and make sure to redraw asap
            invalidate();
        }

        public void startSimulation() {
            /* Using a slower rate (SENSOR_DELAY_UI), we get an automatic low-pass filter,
             * which "extracts" the gravity component of the acceleration. */
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        public void stopSimulation() {
            mSensorManager.unregisterListener(this);
        }

        class Pikachu extends View {
            private float mPosX = (float) Math.random();
            private float mPosY = (float) Math.random();
            private float mVelX;
            private float mVelY;

            public Pikachu(Context context) { super(context); }

            public Pikachu(Context context, AttributeSet attrs) { super(context, attrs); }

            public Pikachu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
                super(context, attrs, defStyleAttr, defStyleRes);
            }

            public void computePhysics(float sx, float sy, float dT) {
                final float ax = -sx/5;
                final float ay = -sy/5;
                mPosX += mVelX * dT + ax * dT * dT / 2;
                mPosY += mVelY * dT + ay * dT * dT / 2;
                mVelX += ax * dT;
                mVelY += ay * dT;
            }

            public void resolveCollisionWithBounds() {
                final float xmax = mHorizontalBound;
                final float ymax = mVerticalBound;
                final float x = mPosX;
                final float y = mPosY;
                if (x > xmax) {
                    mPosX = xmax;
                    mVelX = 0;
                } else if (x < -xmax) {
                    mPosX = - xmax;
                    mVelX = 0;
                }
                if (y > ymax) {
                    mPosY = ymax;
                    mVelY = 0;
                } else if (y < -ymax) {
                    mPosY = - ymax;
                    mVelY = 0;
                }
            }

            public void update(float sx, float sy, long timestamp) {
                final long t = timestamp;
                if (mLastT != 0) {
                    final float dT = (float) (t - mLastT) / 1000.f /** (1.0f / 1000000000.0f)*/;
                    computePhysics(sx, sy, dT);
                }
                mLastT = t;
                mPikachu.resolveCollisionWithBounds();
            }
        }
    }
}
