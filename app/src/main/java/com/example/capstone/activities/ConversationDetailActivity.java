package com.example.capstone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstone.R;
import com.example.capstone.adapters.MessagesAdapter;
import com.example.capstone.models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;
import java.util.Objects;

public class ConversationDetailActivity extends AppCompatActivity {
    private ImageButton upArrowProfile;
    private Toolbar toolbar;
    private Button btnSend;
    private EditText etMessageContent;

    private TextView gcNameConversationDetail;
    private FirebaseDatabase database;
    private RecyclerView rvMessages;
    private MessagesAdapter adapter;
    private List<Message> allMessages;
    private DatabaseReference groupMessagesRef;
    private DatabaseReference groupDetailsRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_detail_conversation);

        upArrowProfile = findViewById(R.id.upArrowProfile);
        gcNameConversationDetail = findViewById(R.id.gcNameConversationDetail);
        rvMessages = findViewById(R.id.rvMessages);
        btnSend = findViewById(R.id.btnSend);
        etMessageContent = findViewById(R.id.etMessageContent);

        database = FirebaseDatabase.getInstance();
        allMessages = new ArrayList<>();
        adapter = new MessagesAdapter(this, allMessages);
        groupMessagesRef = database.getReference("group_messages");
        groupDetailsRef = database.getReference("group_details");

        rvMessages.setAdapter(adapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));

        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);

        upArrowProfile.setOnClickListener(v -> NavUtils.navigateUpFromSameTask(this) );

        // Formats the group chats name
        String groupChatId = getIntent().getStringExtra("chat_id");
        if (getIntent().getBooleanExtra("is_group_chat", false)) {
            String[] groupName = groupChatId.split("PM");
            if (groupName.length == 1) {
                groupName = groupChatId.split("AM");
            }
            String name = groupName[1];
            if (name.length() > 25) {
                name = name.substring(0, 25) + "... @" + groupName[0];
            } else {
                name += "@" + groupName[0];
            }
            gcNameConversationDetail.setText(name);
        } else {
            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            String toUserId = getIntent().getStringExtra("to_user_id");
            parseQuery.whereEqualTo("objectId", toUserId);
            parseQuery.findInBackground((users, e) -> gcNameConversationDetail.setText(users.get(0).getUsername()) ); //todo problem here
        }


        // populates the adapter and listens for changes to the database
        groupMessagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMessages.clear();
                adapter.notifyDataSetChanged();
                for (DataSnapshot child : snapshot.getChildren()) {
                    //if the message is apart of the group
                    if (Objects.equals(groupChatId, child.getKey())) {
                        for (DataSnapshot messageChild : child.getChildren()) {

                            String messageContent = messageChild.child("message_content").getValue().toString();
                            String senderID = messageChild.child("senderID").getValue().toString();
                            long date = (long) messageChild.child("date_time").getValue();
                            Message message = new Message(messageContent, senderID, date);
                            allMessages.add(0, message);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ConversationDetailActivity", "onCancelled: ", error.toException());
            }
        });

        btnSend.setOnClickListener(v -> {
            String messageContent = etMessageContent.getText().toString();
            if (messageContent.equals("")) {
                Toast.makeText(ConversationDetailActivity.this, "A message cannot be empty", Toast.LENGTH_SHORT).show();
            }
            etMessageContent.setText("");

            //create a message thing and copy
            Message message = new Message(messageContent, ParseUser.getCurrentUser().getObjectId());
            groupMessagesRef.child(groupChatId).push().setValue(message); //saves message to data base

            //update current group chat with new message
            groupDetailsRef.get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.e("ConversationDetailActivity", "Error getting data ", task.getException());
                    return;
                }
                String groupChatKey = "";
                for (DataSnapshot child : task.getResult().getChildren()) {
                    if (groupChatId.equals(child.child("id").getValue().toString())) {
                        groupChatKey = child.getKey();
                    }
                }
                //updates the group chat with the newest message
                database.getReference("group_details/" + groupChatKey + "/message").setValue(message);

            });
        });

    }
}