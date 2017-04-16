package edu.neu.madcourse.pikachujump;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    MediaPlayer mMediaPlayer;
    boolean mute = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        Button muteButton = (Button) findViewById(R.id.mute_button);
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mute) {
                    mute = true;
                    view.setBackgroundResource(R.drawable.onmusic);
                    pause();
                } else {
                    mute = false;
                    view.setBackgroundResource(R.drawable.mute);
                    play();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        play();
    }

    public void pause() {
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }

    public void play() {
        mMediaPlayer = MediaPlayer.create(this, R.raw.bgm);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }
}
