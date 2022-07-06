package com.example.capstone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstone.R;
import com.example.capstone.fragments.OtherDatesFragment;
import com.example.capstone.models.Event;
import com.example.capstone.models.GroupChat;
import com.example.capstone.models.Message;
import com.example.capstone.models.User;
import com.example.capstone.models.VideoContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DetailEventActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageButton upArrowDetail;
    private TextView tvTypeOfContentDetail;
    private TextView tvTitleDetail;
    private TextView tvDateDetail;
    private TextView tvOverviewDetail;
    private RatingBar rbDetails;
    private ImageView ivPosterDetail;
    private TextView tvScreenNameDetail;
    private TextView tvNumInterestedDetail;
    private ImageButton iBtnOtherDates;
    private ImageButton iBtnGroupChatDetail;
    FirebaseDatabase database;

    private ImageButton iBtnInterested;
    private TextView tvInterested;
    private ImageButton iBtnDelete;
    private TextView tvDelete;
    private Event event;
    private AtomicBoolean isInterested;
    private List<List<ParseUser>> interestedUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);

        upArrowDetail = findViewById(R.id.upArrowDetail);
        upArrowDetail.setOnClickListener(v -> {
            Intent i = new Intent(DetailEventActivity.this, FeedActivity.class);
            startActivity(i);
            finish();
        });

        event = getIntent().getExtras().getParcelable("event");

        interestedUsers = event.getInterestedUsers();
        isInterested = new AtomicBoolean(false);
        interestedUsers.get(event.getEarliestUserIndex()).forEach(parseUser -> {
            if (Objects.equals(parseUser.getObjectId(), ParseUser.getCurrentUser().getObjectId())) {
                isInterested.set(true);
            }
        });

        assignDataToViews();

        iBtnInterested.setOnClickListener(v -> onInterestedClick());

        iBtnDelete.setOnClickListener(v -> {
            // should check if there are any other times, then delete whole event if there arent
            // if there are other times, delete that index
            event.deleteInBackground(e -> {
                Intent i = new Intent(DetailEventActivity.this, FeedActivity.class);
                startActivity(i);
                finish();
            });
        });

        iBtnGroupChatDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                String groupChatID = (event.getDates().get(event.getEarliestUserIndex()) + event.getTitle() + event.getTypeOfContent()).replace(".", "");

                // Checks if the user has already been added to the group chat
                List<String> userGroupChatIDs = user.getList("groupChatID");
                AtomicBoolean inGroupChat = new AtomicBoolean(false);
                if (userGroupChatIDs != null) {
                    userGroupChatIDs.forEach(s -> {
                        if (Objects.equals(s, groupChatID)) { inGroupChat.set(true); }
                    });
                }

                if (isInterested.get() && !inGroupChat.get()) {
                    // Creates a unique id for the event currently displayed and stores it with the authenticated user

                    List<String> groupChatIDs = user.getList("groupChatID");
                    if (groupChatIDs != null) {
                        groupChatIDs = user.getList("groupChatID");
                    } else {
                        groupChatIDs = new ArrayList<>();
                    }
                    groupChatIDs.add(groupChatID);
                    user.put("groupChatID", groupChatIDs);
                    user.saveInBackground();


                    DatabaseReference ref = database.getReference("group_chats");
                    // todo: will be useful for finding gc to update messages with
                    ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            final boolean[] groupChatExist = {false};

                            task.getResult().getChildren().forEach(dataSnapshot -> {
                                GroupChat groupChat = dataSnapshot.getValue(GroupChat.class);
                                if (groupChatID.equals(groupChat.getId())) {
                                    //it already exist in the data base
                                    groupChatExist[0] = true;
                                }
                            });

                            if (groupChatExist[0]) {
                                Log.i("DetailEventActivity", "onComplete: add user to existing group chat" );

                            } else {
                                List<User> users = new ArrayList<>();
                                User firstUser = new User(user.getObjectId(), user.getUsername(), user.getString("screenName"));
                                users.add(firstUser);

                                List<Message> messages = new ArrayList<>();
                                messages.add(new Message("Hi", firstUser));
                                ref.push().setValue(new GroupChat(event.getTitle() + " @ " + event.getDates().get(event.getEarliestUserIndex()), groupChatID, users, messages));
                            }
                        }
                    });
                } else if(isInterested.get() && inGroupChat.get()) {
                    Toast.makeText(DetailEventActivity.this, "Already in the groupchat", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(DetailEventActivity.this, "Indicate that you are interested first! ", Toast.LENGTH_SHORT).show();
                }





                //check if the group chat exist already by querying the "groupchat" path for an id that == the group chat id

                    //use this instance of the group chat
                    //check if current user has been added to the group chat
                // if not, create the group chat



//                DatabaseReference myRef = database.getReference("hello"); //think you can put your own path in that
//                Conversation conversation = new Conversation();
//                conversation.isGroupChat = true;
//                conversation.preview = "deez Nutz";
//                myRef.setValue(conversation);
            }
        });

        iBtnOtherDates.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            OtherDatesFragment otherDatesFragment = OtherDatesFragment.newInstance(event);
            otherDatesFragment.show(fm, "other_dates");
        });

    }

    private void onInterestedClick() {
        if (!isInterested.get()) {
            isInterested.set(true);
            event.setNumInterested(event.getNumInterested() + 1);
            interestedUsers.get(event.getEarliestUserIndex()).add(ParseUser.getCurrentUser());
            event.setInterestedUsers(interestedUsers);
            event.saveInBackground();

            tvNumInterestedDetail.setText(event.getInterestedUsers().get(event.getEarliestUserIndex()).size()+ " attending");
            iBtnInterested.setColorFilter(Color.argb(150, 0, 255, 0));
        } else {
            isInterested.set(false);
            event.setNumInterested(event.getNumInterested() - 1);
            List<ParseUser> user = interestedUsers.get(event.getEarliestUserIndex());

            for (int i = 0; i < user.size(); i++){
                if (Objects.equals(user.get(i).getObjectId(), ParseUser.getCurrentUser().getObjectId())) {
                    user.remove(i);
                }
            }
            event.setInterestedUsers(interestedUsers);
            event.saveInBackground();

            tvNumInterestedDetail.setText(event.getInterestedUsers().get(event.getEarliestUserIndex()).size() + " attending");
            iBtnInterested.setColorFilter(Color.argb(0, 0, 255, 0));
        }
    }

    private void assignDataToViews() {
        tvTypeOfContentDetail = findViewById(R.id.tvTypeOfContentDetail);
        tvTitleDetail = findViewById(R.id.tvTitleDetail);
        tvDateDetail = findViewById(R.id.tvDateDetail);
        tvOverviewDetail = findViewById(R.id.tvOverviewDetail);
        rbDetails = findViewById(R.id.rbDetails);
        ivPosterDetail = findViewById(R.id.ivPosterDetail);
        tvScreenNameDetail = findViewById(R.id.tvScreenNameDetail);
        iBtnInterested = findViewById(R.id.iBtnInterested);
        tvInterested = findViewById(R.id.tvInterested);
        iBtnDelete = findViewById(R.id.iBtnDelete);
        tvDelete = findViewById(R.id.tvDelete);
        tvNumInterestedDetail = findViewById(R.id.tvNumInterestedDetail);
        iBtnOtherDates = findViewById(R.id.iBtnOtherDates);
        iBtnGroupChatDetail = findViewById(R.id.iBtnGroupChatDetail);
        database = FirebaseDatabase.getInstance();

        tvDateDetail.setText(event.getDates().get(event.getEarliestUserIndex()));
        tvTitleDetail.setText(event.getTitle());

        // todo: put error/placeholder image
        Glide.with(this).load(event.getPosterUrl()).into(ivPosterDetail);
        ivPosterDetail.setColorFilter(Color.argb(60, 0, 0, 0));

        try {
            VideoContent videoContent = event.getVideContent().fetch();
            tvOverviewDetail.setText(videoContent.getOverview());
            float voterAverage = videoContent.getVoteAverage().floatValue() / 2.0f;
            rbDetails.setRating(voterAverage);

        } catch (ParseException e) {
            Log.e("DetailEventActivity", "onCreate: ", e);
        }

        try {
            ParseUser user = event.getUsers().get(event.getEarliestUserIndex()).fetch();
            tvScreenNameDetail.setText(user.getString("screenName") + "'s Event");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        tvNumInterestedDetail.setText(event.getInterestedUsers().get(event.getEarliestUserIndex()).size() + " attending");


        //if current user is the author && NumInterestedUsers = 1
        String currUserObjectID = ParseUser.getCurrentUser().getObjectId();
        String eventUserObjectID = event.getUsers().get(event.getEarliestUserIndex()).getObjectId();
        if (Objects.equals(currUserObjectID, eventUserObjectID) && event.getNumInterested() <= 1) {
            tvDelete.setVisibility(View.VISIBLE);
            iBtnDelete.setVisibility(View.VISIBLE);
            iBtnDelete.setColorFilter(Color.argb(60, 255, 0, 0));
        } else if (!Objects.equals(currUserObjectID, eventUserObjectID)) {
            tvInterested.setVisibility(View.VISIBLE);
            iBtnInterested.setVisibility(View.VISIBLE);
        }


        if (isInterested.get()) {
            iBtnInterested.setColorFilter(Color.argb(150, 0, 255, 0));
        } else {
            iBtnInterested.setColorFilter(Color.argb(0, 0, 255, 0));
        }
    }
}