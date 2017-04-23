package edu.neu.madcourse.pikachujump;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.iid.FirebaseInstanceId;

public class MainActivity extends AppCompatActivity {
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

//        if(!GameUtils.getIsLogedIn()) {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//        }

        String token = FirebaseInstanceId.getInstance().getToken();
        GameUtils.setToken(token);

        Button muteButton = (Button) findViewById(R.id.mute_button);
        Button acknowledgeButton = (Button) findViewById(R.id.ack_button);

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

        acknowledgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage(R.string.acknowledge_text);
                builder1.setCancelable(false);
                builder1.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // nothing
                            }
                        });
                mDialog = builder1.show();
            }
        });


    }

    public void displayInstruction(View view) {
        Intent intent = new Intent(this, InstructionActivity.class);
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