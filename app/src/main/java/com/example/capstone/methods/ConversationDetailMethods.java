package com.example.capstone.methods;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.activities.ConversationDetailActivity;
import com.example.capstone.adapters.MessagesAdapter;
import com.example.capstone.models.Message;
import com.example.capstone.models.TypingDetail;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ConversationDetailMethods {
    public static void setChatName(String groupChatId, TextView gcNameConversationDetail, Intent intent) {
        if (intent.getBooleanExtra("is_group_chat", false)) {
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
            String toUserId = intent.getStringExtra("to_user_id");
            parseQuery.whereEqualTo("objectId", toUserId);
            parseQuery.findInBackground((users, e) -> gcNameConversationDetail.setText(users.get(0).getUsername()) );
        }
    }

    public static void onMessageSend(Context context, EditText etMessageContent, FirebaseDatabase database, ParseUser currUser, String groupChatId, DatabaseReference groupDetailsRef) {
        String messageContent = etMessageContent.getText().toString();
        if (messageContent.equals("")) {
            Toast.makeText(context, "A message cannot be empty", Toast.LENGTH_SHORT).show();
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
                    break;
                }
            }
            //updates the group chat with the newest message
            database.getReference("group_details/" + groupChatKey + "/message").setValue(message);

        });
    }

    public static void populateMessages(DatabaseReference groupChatRef, List<Message> allMessages, MessagesAdapter adapter) {
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
    }

    public static void isTypingLogic(DatabaseReference groupDetailsRef, String groupChatId, FirebaseDatabase database, ParseUser currUser, List<Message> allMessages, MessagesAdapter adapter, RecyclerView rvMessages, EditText etMessageContent) {
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

            OnETChange.typingIndicator(database, groupDetailKey.get(), etMessageContent, currUser);

        });
    }
}
