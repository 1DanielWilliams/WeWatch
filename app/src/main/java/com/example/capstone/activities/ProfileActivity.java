package com.example.capstone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.capstone.R;
import com.example.capstone.activities.login.logOrSignActivity;
import com.example.capstone.adapters.WatchedContentAdapter;
import com.example.capstone.models.UserPublicColumns;
import com.example.capstone.models.VideoContent;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton upArrowProfile;
    private TextView tvScreenNameProfile;
    private TextView tvUsernameProfile;
    private TextView tvNumWatchedProfile;
    private ImageButton iBtnLogoutMenu;
    private RecyclerView rvWatchedContent;
    private WatchedContentAdapter adapter;
    private List<VideoContent> allWatchedContent;
    private Button btnWishToSee;
    private Button btnWatchedContent;
    private View vWatchedLine;
    private View vWishToSeeLine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_profile);

        vWishToSeeLine = findViewById(R.id.vWishToSeeLine);
        vWatchedLine = findViewById(R.id.vWatchedLine);
        btnWatchedContent = findViewById(R.id.btnWatchedContent);
        btnWishToSee = findViewById(R.id.btnWishToSee);
        rvWatchedContent = findViewById(R.id.rvWatchedContent);
        iBtnLogoutMenu = findViewById(R.id.iBtnLogoutMenu);
        tvScreenNameProfile = findViewById(R.id.tvScreenNameProfile);
        tvUsernameProfile = findViewById(R.id.tvUsernameProfile);
        tvNumWatchedProfile = findViewById(R.id.tvNumWatchedProfile);
        upArrowProfile = findViewById(R.id.upArrowProfile);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);

        upArrowProfile.setOnClickListener(v -> finish());


        allWatchedContent = new ArrayList<>();
        adapter = new WatchedContentAdapter(this, allWatchedContent);
        rvWatchedContent.setAdapter(adapter);
        rvWatchedContent.setLayoutManager(new LinearLayoutManager(this));



        String userID = getIntent().getStringExtra("id");

        if (Objects.equals(userID, ParseUser.getCurrentUser().getObjectId())) {
            iBtnLogoutMenu.setVisibility(View.VISIBLE);
        }
        iBtnLogoutMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(ProfileActivity.this, iBtnLogoutMenu);
            popupMenu.getMenuInflater().inflate(R.menu.profile_popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.option_logout) {
                    ParseUser.logOut();
                    Intent i = new Intent(ProfileActivity.this, logOrSignActivity.class);
                    startActivity(i);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });



        AtomicReference<List<VideoContent>> wishToWatch = new AtomicReference<>();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userID);
        query.findInBackground((users, e) -> {
            if (e != null) {
                Log.e("ProfileActivity", "done: ", e);
                return;
            }

            ParseUser user = users.get(0);
            tvScreenNameProfile.setText(user.getString("screenName"));
            tvUsernameProfile.setText("@" + user.getUsername());
            List<ParseUser> friends = user.getList("friends");
            if (friends == null) {
                friends = new ArrayList<>();
                user.saveInBackground();
            }
//            tvNumFriendsProfile.setText(String.valueOf(friends.size()));




            wishToWatch.set(user.getList("wishToWatch"));
            if (wishToWatch.get() == null) {
                wishToWatch.set(new ArrayList<>());
                user.saveInBackground();
            }

        });
        AtomicReference<List<VideoContent>> watchedContent = new AtomicReference<>();

        ParseQuery<UserPublicColumns> userPublicColumnsParseQuery = ParseQuery.getQuery(UserPublicColumns.class);
        userPublicColumnsParseQuery.whereEqualTo(UserPublicColumns.KEY_USER_ID, userID);
        userPublicColumnsParseQuery.findInBackground((userPublicColumns, e) -> {
            UserPublicColumns userPublicColumn = userPublicColumns.get(0);
            watchedContent.set(userPublicColumn.getWatchedContent());
            if (watchedContent.get() == null) {
                watchedContent.set(new ArrayList<>());
                userPublicColumn.saveInBackground();
            }
            tvNumWatchedProfile.setText(String.valueOf(watchedContent.get().size()));
            allWatchedContent.addAll(watchedContent.get());
            adapter.notifyDataSetChanged();
        });



        // make other rv gone, set this one to visible
        // do all the uptopstuff for this rv
        btnWishToSee.setOnClickListener(v -> {
            btnWatchedContent.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            vWatchedLine.setBackgroundColor(Color.rgb(170, 170, 170));
            vWishToSeeLine.setBackgroundColor(Color.rgb(0, 0, 0));
            btnWishToSee.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            allWatchedContent.clear();
            allWatchedContent.addAll(wishToWatch.get());
            adapter.notifyDataSetChanged();

        });

        btnWatchedContent.setOnClickListener(v -> {
            btnWishToSee.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            vWishToSeeLine.setBackgroundColor(Color.rgb(170, 170, 170));
            vWatchedLine.setBackgroundColor(Color.rgb(0, 0, 0));
            btnWatchedContent.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

            allWatchedContent.clear();
            allWatchedContent.addAll(watchedContent.get());
            adapter.notifyDataSetChanged();

        });

    }
}