package com.example.capstone.activities.feed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.TransitionInflater;
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
import com.example.capstone.methods.GroupChatMethods;
import com.example.capstone.methods.RemoveFromWishToWatch;
import com.example.capstone.models.Event;
import com.example.capstone.models.GroupDetail;
import com.example.capstone.models.Message;
import com.example.capstone.models.TypingDetail;
import com.example.capstone.models.UserPublicColumns;
import com.example.capstone.models.VideoContent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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
    private FirebaseDatabase database;
    private ImageButton ibShareDetail;

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
        upArrowDetail.setOnClickListener(v -> finish() );

        event = getIntent().getExtras().getParcelable("event");

        interestedUsers = event.getInterestedUsers();
        isInterested = new AtomicBoolean(false);
        interestedUsers.get(event.getEarliestUserIndex()).forEach(parseUser -> {
            if (Objects.equals(parseUser.getObjectId(), ParseUser.getCurrentUser().getObjectId())) {
                isInterested.set(true);
            }
        });

        assignDataToViews();

        iBtnInterested.setOnClickListener(v -> {
            try {
                onInterestedClick();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        iBtnDelete.setOnClickListener(v -> {
            event.deleteInBackground(e -> {
                Intent i = new Intent(DetailEventActivity.this, FeedActivity.class);
                startActivity(i);
                finish();
            });
        });

        iBtnGroupChatDetail.setOnClickListener(v -> {
            Date currentDate = new Date(System.currentTimeMillis());
            if (event.getEarliestDate().toInstant().isBefore(currentDate.toInstant().plus(Duration.ofHours(24)))) {
                ParseUser user = ParseUser.getCurrentUser();
                String groupChatID = (event.getDates().get(event.getEarliestUserIndex()) + event.getTitle() + event.getTypeOfContent()).replace(".", "");

                DatabaseReference groupDetailsRef = database.getReference("group_details");
                DatabaseReference groupMessagesRef= database.getReference("group_messages");

                //Checks if the user has already been added to the group chat
                ParseQuery<UserPublicColumns> publicColumnsQuery = ParseQuery.getQuery(UserPublicColumns.class);
                publicColumnsQuery.whereEqualTo(UserPublicColumns.KEY_USER_ID, user.getObjectId());
                AtomicBoolean inGroupChat = new AtomicBoolean(false);
                publicColumnsQuery.findInBackground((userPublicColumns, e) -> {
                    UserPublicColumns userPublicColumn = userPublicColumns.get(0);
                    List<String> userGroupChatIDs = userPublicColumn.getGroupChatIds();
                    userGroupChatIDs.forEach(s -> {
                        if (Objects.equals(s, groupChatID)) {
                            inGroupChat.set(true);
                        }
                    });


                    if (isInterested.get() && !inGroupChat.get()) {

                        userGroupChatIDs.add(groupChatID);
                        userPublicColumn.setGroupChatIds(userGroupChatIDs);
                        userPublicColumn.saveInBackground();

                        // Determines if a groupchat exist
                        AtomicBoolean groupChatExist = new AtomicBoolean(false);
                        AtomicReference<String> groupDetailKey = new AtomicReference<>();
                        groupDetailsRef.get().addOnCompleteListener(task -> {
                            for (DataSnapshot child : task.getResult().getChildren()) {
                                if (groupChatID.equals(child.child("id").getValue())) {
                                    groupChatExist.set(true);
                                    groupDetailKey.set(child.getKey());
                                    break;
                                }
                            }

                            if (groupChatExist.get()) {
                                DatabaseReference appendUser = database.getReference("group_details/" + groupDetailKey.get() + "/members");
                                appendUser.push().setValue(user.getObjectId());

                            } else {
                                Message firstMessage = new Message("Hi!", user.getObjectId());
                                DatabaseReference push = groupDetailsRef.push();
                                push.setValue(new GroupDetail(event.getTitle() + " @ " + event.getDates().get(event.getEarliestUserIndex()), groupChatID, firstMessage)).addOnCompleteListener(task1 -> {
                                    DatabaseReference detailMembers = database.getReference("group_details/" + push.getKey() + "/members");
                                    detailMembers.push().setValue(user.getObjectId());

                                    DatabaseReference groupDetailRef = database.getReference("group_details/" + push.getKey() + "/typing_detail");
                                    groupDetailRef.setValue(new TypingDetail(false));
                                });
                                groupMessagesRef.child(groupChatID).push().setValue(firstMessage);
                            }
                            GroupChatMethods.toConversationDetail(DetailEventActivity.this, groupChatID, true, "");
                        });

                    } else if (isInterested.get() && inGroupChat.get()) {
                        GroupChatMethods.toConversationDetail(DetailEventActivity.this, groupChatID, true, "");

                    } else {
                        Toast.makeText(DetailEventActivity.this, "Indicate that you are interested first! ", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Come back 24hrs before the event!", Toast.LENGTH_LONG).show();
            }

        });

        iBtnOtherDates.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            OtherDatesFragment otherDatesFragment = OtherDatesFragment.newInstance(event);
            otherDatesFragment.show(fm, "other_dates");
        });

        ibShareDetail.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey, you should log on to watch " + event.getTitle() + " on WeWatch @" + event.getDates().get(0) + " with " +  event.getInterestedUsers().get(0).size() + " other students.");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });


    }

    private void onInterestedClick() throws ParseException {
        if (!isInterested.get()) {
            isInterested.set(true);

            ParseUser user = ParseUser.getCurrentUser();
            VideoContent videoContent = event.getVideContent().fetchIfNeeded();
            List<VideoContent> wishToWatch = user.getList("wishToWatch");
            if (wishToWatch == null) {
                wishToWatch = new ArrayList<>();
            }
            RemoveFromWishToWatch.removeContent(wishToWatch, user, videoContent);

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
        ibShareDetail = findViewById(R.id.ibShareDetail);

        tvDateDetail.setText(event.getDates().get(0));
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