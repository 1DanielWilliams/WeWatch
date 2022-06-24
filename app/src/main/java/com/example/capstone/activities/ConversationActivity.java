package com.example.capstone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.capstone.R;
import com.example.capstone.adapters.ConversationsAdapter;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.models.Conversation;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {

    private ImageButton imBtnMenuFeed;
    private DrawerLayout drawerLayout;
    private NavigationView navDrawerFeed;
    private Toolbar toolbar;
    private RecyclerView rvMessages;
    private ConversationsAdapter adapter;
    private List<Conversation> allConversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_conversation);

        imBtnMenuFeed = findViewById(R.id.imBtnMenuFeed);
        drawerLayout = findViewById(R.id.drawerLayout);
        navDrawerFeed = findViewById(R.id.navDrawerFeed);
        toolbar = findViewById(R.id.toolbar);
        rvMessages = findViewById(R.id.rvMessages);

        allConversations = new ArrayList<>();
        adapter = new ConversationsAdapter(this, allConversations);

        rvMessages.setAdapter(adapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));

        toolbar.setContentInsetsAbsolute(0, 0);

        NavigationMethods.setUpNavDrawer(ConversationActivity.this, navDrawerFeed, imBtnMenuFeed, drawerLayout);

    }


}