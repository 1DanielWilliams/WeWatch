package com.example.capstone.activities;

import androidx.annotation.NonNull;
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
import com.example.capstone.models.UserPublicColumns;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

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

//        List<String> groupChatIDs = user.getList("groupChatID");
        ParseQuery<UserPublicColumns> publicColumnsQuery = ParseQuery.getQuery(UserPublicColumns.class);
        publicColumnsQuery.whereEqualTo(UserPublicColumns.KEY_USER_ID, user.getObjectId());
        publicColumnsQuery.findInBackground(new FindCallback<UserPublicColumns>() {
            @Override
            public void done(List<UserPublicColumns> userPublicColumns, ParseException e) {
                UserPublicColumns userPublicColumn = userPublicColumns.get(0);
                List<String> groupChatIDs = userPublicColumn.getGroupChatIds();
                if (groupChatIDs.size() != 0) {
                    groupDetailsRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            allGroupDetails.clear();
                            adapter.notifyDataSetChanged();
                            // Finds the groupChats relevant to the user and adds it to the adapter
                            for (DataSnapshot child : snapshot.getChildren()) {
                                // the authenticated user is in this group
                                String id = child.child("id").getValue().toString();
                                if (groupChatIDs.contains(id)) {
                                    Message message = child.child("message").getValue(Message.class);
                                    List<String> userIds = new ArrayList<>();
                                    child.child("members").getChildren().forEach(dataSnapshot -> userIds.add(dataSnapshot.getValue().toString()));
                                    GroupDetail groupDetail = new GroupDetail(child.child("name").getValue().toString(), id, message, userIds);
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
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    //display something to show that you have no groupchats
                }

            }
        });

    }



}