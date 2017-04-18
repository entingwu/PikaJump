package edu.neu.madcourse.pikachujump;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        if(!GameUtils.getIsLogedIn()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        Button muteButton = (Button) findViewById(R.id.mute_button);
        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (GameUtils.isMusicPlaying) {
                GameUtils.isMusicPlaying = false;
                view.setBackgroundResource(R.drawable.onmusic);
                GameUtils.pauseMusic();
            } else {
                GameUtils.isMusicPlaying = true;
                view.setBackgroundResource(R.drawable.mute);
                GameUtils.playMusic(getApplicationContext(), R.raw.bgm);
            }
            }
        });
    }

    public void displayInstruction(View view) {
        Intent intent = new Intent(this, InstructionActivity.class);
        startActivity(intent);
    }

    public void displayLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GameUtils.pauseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GameUtils.playMusic(this, R.raw.bgm);
    }
}