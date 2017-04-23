package edu.neu.madcourse.pikachujump;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class LeaderBoardActivity extends AppCompatActivity {

    private static final String TAG = LeaderBoardActivity.class.getSimpleName();
    private ListView listView;
    private Button buttonSortByScore;
    private Button buttonSortByJump;
    private ArrayAdapter adapter;
    private ArrayList<String> contents = new ArrayList<>();
    private int num1 = 0;
    private int num2 = 0;
    private ArrayList<User> users = new ArrayList<>();
    public ArrayList<User> displayedUsers = new ArrayList<>();

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUsersRef = mRootRef.child("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        listView = (ListView) findViewById(R.id.leaderBoard_list);
        adapter = new ArrayAdapter<>(LeaderBoardActivity.this,
                R.layout.leaderboard_listitem, contents);
        listView.setAdapter(adapter);

        buttonSortByScore = (Button) findViewById(R.id.button_leader_board_sort_score);
        buttonSortByJump = (Button) findViewById(R.id.button_leader_board_sort_jump);


        buttonSortByScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByScore(num1);
                num1++;
                adapter.notifyDataSetChanged();
            }
        });

        buttonSortByJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByJump(num2);
                num2++;
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mUsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    users.add(user);
                }
                displayedUsers = getFirst10Users(users);
                GameUtils.clearDisplayedUser();
                GameUtils.setDisplayedUser(displayedUsers);

                for (User u : displayedUsers) {
                    String string = convertUser(u);
                    contents.add(string);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<User> getFirst10Users(ArrayList<User> users) {
        ArrayList<User> result = new ArrayList<>();

        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o2.score - o1.score;
            }
        });

        if (users.size() <= 10) {
            result = users;
        } else {
            for (int i = 0; i < 10; i++) {
                result.add(users.get(i));
            }
        }
        return result;
    }

    private void sortByScore(final int num) {
        Collections.sort(displayedUsers, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                if ((num & 1) == 0) {
                    return o1.score - o2.score;
                } else {
                    return o2.score - o1.score;
                }
            }
        });
        contents.clear();

        for (User u : displayedUsers) {
            String string = convertUser(u);
            contents.add(string);
        }
        adapter.notifyDataSetChanged();
    }

    private void sortByJump(final int num) {
        Collections.sort(displayedUsers, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                if ((num & 1) == 0) {
                    return o1.totalJumps - o2.totalJumps;
                } else {
                    return o2.totalJumps - o1.totalJumps;
                }
            }
        });
        contents.clear();

        for (User u : displayedUsers) {
            String string = convertUser(u);
            contents.add(string);
        }
        adapter.notifyDataSetChanged();
    }



    private String convertUser (User user) {
        String result;
        result = "Username: " + user.username + "\n"
                + "TotalScore: " + user.score + "\n"
                + "Number of Jumps: " + user.totalJumps + "\n"
                + "DataPlayed: " + user.datePlayed + "\n";

        return result;
    }
}
