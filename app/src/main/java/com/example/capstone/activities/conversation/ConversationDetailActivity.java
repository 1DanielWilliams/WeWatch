package com.example.capstone.activities.conversation;

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
import com.example.capstone.methods.ConversationDetailMethods;
import com.example.capstone.methods.OnETChange;
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
        ConversationDetailMethods.setChatName(groupChatId, gcNameConversationDetail, getIntent());

        ConversationDetailMethods.populateMessages(groupChatRef, allMessages, adapter);

        ConversationDetailMethods.isTypingLogic(groupDetailsRef, groupChatId, database, currUser, allMessages, adapter, rvMessages, etMessageContent);

        btnSend.setOnClickListener(v -> ConversationDetailMethods.onMessageSend(this, etMessageContent, database, currUser, groupChatId, groupDetailsRef));

    }
}