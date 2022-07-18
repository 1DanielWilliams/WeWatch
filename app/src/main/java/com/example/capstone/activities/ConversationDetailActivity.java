package com.example.capstone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.capstone.models.TypingDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
    private DatabaseReference groupChatRef;
    private DatabaseReference groupDetailsRef;
    private ParseUser currUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.capstone.R.layout.activity_detail_conversation);

        upArrowProfile = findViewById(R.id.upArrowProfile);
        gcNameConversationDetail = findViewById(R.id.gcNameConversationDetail);
        rvMessages = findViewById(R.id.rvMessages);
        btnSend = findViewById(R.id.btnSend);
        etMessageContent = findViewById(R.id.etMessageContent);

        currUser = ParseUser.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        allMessages = new ArrayList<>();
        adapter = new MessagesAdapter(this, allMessages);
        String groupChatId = getIntent().getStringExtra("chat_id");
        groupChatRef = database.getReference("group_messages/" + groupChatId);
        groupDetailsRef = database.getReference("group_details");

        rvMessages.setAdapter(adapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));

        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);

        upArrowProfile.setOnClickListener(v -> NavUtils.navigateUpFromSameTask(this) );

        // Formats the group chats name
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
            parseQuery.findInBackground((users, e) -> gcNameConversationDetail.setText(users.get(0).getUsername()) );
        }


        groupChatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                allMessages.add(0, snapshot.getValue(Message.class));
                adapter.notifyItemInserted(0);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });


        AtomicReference<String> groupDetailKey = new AtomicReference<>();
        groupDetailsRef.get().addOnCompleteListener(task -> {
            for (DataSnapshot child : task.getResult().getChildren()) {
                if (groupChatId.equals(child.child("id").getValue())) {
                    groupDetailKey.set(child.getKey());
                    break;
                }
            }

            DatabaseReference groupDetailRef = database.getReference("group_details/" + groupDetailKey.get());

            AtomicReference<Message> isTypingMessage = new AtomicReference<>();
            AtomicBoolean wasTyping = new AtomicBoolean(false);
            groupDetailRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    TypingDetail typingDetail = snapshot.getValue(TypingDetail.class);
                    //if a user is typing and it isnt yourself
                    if (typingDetail.isTyping() && !Objects.equals(currUser.getObjectId(), typingDetail.getTypingUserID())) {
                        //add a message that means something is typing
                        Message typingMessage = new Message(typingDetail.getTypingUserID(), typingDetail.getTypingUserID(), System.currentTimeMillis());
                        isTypingMessage.set(typingMessage);
                        allMessages.add(0, typingMessage);
                        adapter.notifyItemInserted(0);
                        rvMessages.smoothScrollToPosition(0);
                        wasTyping.set(true);
                    } else if (!typingDetail.isTyping() && !Objects.equals(currUser.getObjectId(), typingDetail.getTypingUserID()) && wasTyping.get()){
                        Log.i("ConversationDetialActivity", "onChildChanged: " + typingDetail.getTypingUsername() + " is no longer typing");
                        int index = allMessages.indexOf(isTypingMessage.get());
                        allMessages.remove(index);
                        adapter.notifyItemRemoved(index);
                        wasTyping.set(false);
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            DatabaseReference typingDetailRef = database.getReference("group_details/" + groupDetailKey.get() + "/typing_detail");

            etMessageContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TypingDetail typingDetail = new TypingDetail(true, currUser.getObjectId(), currUser.getUsername(), currUser.getString("screenName"));
                    typingDetailRef.setValue(typingDetail);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TypingDetail typingDetail = new TypingDetail(false);
                        typingDetailRef.setValue(typingDetail);
                    }
                }
            });

        });


        btnSend.setOnClickListener(v -> {
            String messageContent = etMessageContent.getText().toString();
            if (messageContent.equals("")) {
                Toast.makeText(ConversationDetailActivity.this, "A message cannot be empty", Toast.LENGTH_SHORT).show();
            }
            etMessageContent.setText("");

            //create a message thing and copy
            Message message = new Message(messageContent, currUser.getObjectId());
            database.getReference("group_messages").child(groupChatId).push().setValue(message); //saves message to data base

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