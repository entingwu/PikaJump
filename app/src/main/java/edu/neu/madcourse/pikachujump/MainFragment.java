package edu.neu.madcourse.pikachujump;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    private AlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        View newButton = rootView.findViewById(R.id.new_button);
        View continueButton = rootView.findViewById(R.id.continue_button);
        View scoreButton = rootView.findViewById(R.id.score_button);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // 1. Set game_mode.xml to alert dialog builder
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View modeView = layoutInflater.inflate(R.layout.game_mode, null);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setView(modeView);

            // 2. Mode Button
            Button easyButton = (Button) modeView.findViewById(R.id.easy_button);
            easyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GameUtils.mode = GameUtils.MODE_EASY;
                    Intent intent = new Intent(getActivity(), GameActivity.class);
                    startActivity(intent);
                }
            });
            Button hardButton = (Button) modeView.findViewById(R.id.hard_button);
            hardButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GameUtils.mode = GameUtils.MODE_HARD;
                    Log.i(TAG, GameUtils.MODE_HARD);
                    Intent intent = new Intent(getActivity(), GameActivity.class);
                    startActivity(intent);
                }
            });
            mDialog = dialogBuilder.create();
            mDialog.show();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                intent.putExtra(GameActivity.KEY_RESTORE, true);
                startActivity(intent);
            }
        });

        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LeaderBoardActivity.class);
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}
