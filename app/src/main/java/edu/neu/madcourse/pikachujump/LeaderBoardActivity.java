package edu.neu.madcourse.pikachujump;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    private static final String TAG = LeaderBoardActivity.class.getSimpleName();
    private List<String> gameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        gameList = new ArrayList<>();
        gameList.add("Hello");

        Log.i(TAG, "Add data...");
        ListView leaderListView = (ListView) findViewById(R.id.leaderBoard_list);
        LeaderBoardAdapter leaderBoardAdapter = new LeaderBoardAdapter(
                LeaderBoardActivity.this,
                R.layout.leaderboard_listitem,
                gameList);
        leaderListView.setAdapter(leaderBoardAdapter);
    }
}
