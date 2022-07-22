package com.example.capstone.activities.conversation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.capstone.R;
import com.example.capstone.adapters.ConversationsAdapter;
import com.example.capstone.methods.DisplayConversations;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.models.GroupDetail;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {

    private ImageButton imBtnMenuFeed;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawerFeed;
    private Toolbar toolbar;
    private RecyclerView rvMessages;
    private ConversationsAdapter adapter;
    private List<GroupDetail> allGroupDetails;
    private FirebaseDatabase database;
    private DatabaseReference groupDetailsRef;
    private ParseUser user;
    private TextView tvJoinAGroupChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_conversation);

        imBtnMenuFeed = findViewById(R.id.imBtnMenuFeed);
        drawerLayout = findViewById(R.id.drawerLayout);
        navDrawerFeed = findViewById(R.id.navDrawerFeed);
        toolbar = findViewById(R.id.toolbar);
        rvMessages = findViewById(R.id.rvMessages);
        tvJoinAGroupChat = findViewById(R.id.tvJoinAGroupChat);

        try {
            user = ParseUser.getCurrentUser().fetch();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        database = FirebaseDatabase.getInstance();
        groupDetailsRef = database.getReference("group_details");
        allGroupDetails = new ArrayList<>();
        adapter = new ConversationsAdapter(this, allGroupDetails);


        toolbar.setContentInsetsAbsolute(0, 0);

        NavigationMethods.setUpNavDrawer(ConversationActivity.this, navDrawerFeed, imBtnMenuFeed, drawerLayout);

        rvMessages.setAdapter(adapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        DisplayConversations.fetchGroupDetails(user, groupDetailsRef, allGroupDetails, adapter, tvJoinAGroupChat);

    }



}