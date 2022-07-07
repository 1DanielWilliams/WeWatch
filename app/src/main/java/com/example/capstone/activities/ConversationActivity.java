package com.example.capstone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import com.example.capstone.R;
import com.example.capstone.adapters.ConversationsAdapter;
import com.example.capstone.methods.NavigationMethods;
import com.example.capstone.models.GroupDetail;
import com.example.capstone.models.Message;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_conversation);

        imBtnMenuFeed = findViewById(R.id.imBtnMenuFeed);
        drawerLayout = findViewById(R.id.drawerLayout);
        navDrawerFeed = findViewById(R.id.navDrawerFeed);
        toolbar = findViewById(R.id.toolbar);
        rvMessages = findViewById(R.id.rvMessages);

        user = ParseUser.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        groupDetailsRef = database.getReference("group_details");
        allGroupDetails = new ArrayList<>();
        adapter = new ConversationsAdapter(this, allGroupDetails);


        toolbar.setContentInsetsAbsolute(0, 0);

        NavigationMethods.setUpNavDrawer(ConversationActivity.this, navDrawerFeed, imBtnMenuFeed, drawerLayout);

        rvMessages.setAdapter(adapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this));

        List<String> groupChatIDs = user.getList("groupChatID");
        if (groupChatIDs != null) {
            groupDetailsRef.get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("ConversationActivity", "Error getting data ", task.getException());
                    return;
                }
                // Finds the groupChats relevant to the user and adds it to the adapter
                for (DataSnapshot child : task.getResult().getChildren()) {
                    // the authenticated user is in this group
                    String id = child.child("id").getValue().toString();
                    if (groupChatIDs.contains(id)) {
                        Message message = child.child("message").getValue(Message.class);
                        GroupDetail groupDetail = new GroupDetail(child.child("name").getValue().toString(), id, message);
                        allGroupDetails.add(groupDetail);
                    }
                }
                // Sorts the list by the message date
                allGroupDetails.sort((firstGd, secondGd) -> {
                    if (firstGd == null || secondGd == null) {
                        return 0;
                    }
                    Date firstDate = new Date(firstGd.getMessage().getDate_time());
                    Date secondDate = new Date(secondGd.getMessage().getDate_time());
                    return secondDate.compareTo(firstDate);
                });
                adapter.notifyDataSetChanged();
            });

        } else {
            //display something to show that you have no groupchats
        }



    }



}