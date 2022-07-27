package com.example.capstone.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.capstone.R;
import com.example.capstone.activities.ProfileActivity;
import com.example.capstone.methods.GroupChatMethods;
import com.example.capstone.models.GroupDetail;
import com.example.capstone.models.Message;
import com.example.capstone.models.TypingDetail;
import com.example.capstone.models.UserPublicColumns;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileOrDmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileOrDmFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TO_USER_ID = "toUserId";
    private static final String CURR_ID = "currId";

    // TODO: Rename and change types of parameters
    private String toUserId;
    private String currId;

    public ProfileOrDmFragment() {
        // Required empty public constructor
    }

    public static ProfileOrDmFragment newInstance(String toUserId, String currId) {
        ProfileOrDmFragment fragment = new ProfileOrDmFragment();
        Bundle args = new Bundle();
        args.putString(TO_USER_ID, toUserId);
        args.putString(CURR_ID, currId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            toUserId = getArguments().getString(TO_USER_ID);
            currId = getArguments().getString(CURR_ID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_message_long_click, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference groupDetailsRef = database.getReference("group_details");

        Button btnDmUser = view.findViewById(R.id.btnDmUser);
        Button btnVisitProfile = view.findViewById(R.id.btnVisitProfile);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", toUserId);
        query.findInBackground((users, e) -> {
            btnDmUser.setText("DM " + users.get(0).getString("screenName"));
        });

        btnDmUser.setOnClickListener(v -> {
            String dmID =  currId + " " + toUserId;
            char[] dmIDArray = dmID.toCharArray();
            Arrays.sort(dmIDArray);
            dmID = String.valueOf(dmIDArray);
            //check if the dm already exist
            final String finalDmID = dmID;
            groupDetailsRef.get().addOnCompleteListener(task -> {
                boolean chatExist = false;
                for (DataSnapshot child : task.getResult().getChildren()) {
                    if (finalDmID.equals(child.child("id").getValue())) {
                        chatExist = true;
                    }
                }

                if (!chatExist) {
                    Message firstMessage = new Message("", currId);
                    DatabaseReference push = groupDetailsRef.push();
                    push.setValue(new GroupDetail(toUserId, finalDmID, firstMessage)).addOnCompleteListener(task1 -> {
                        DatabaseReference detailMembers = database.getReference("group_details/" + push.getKey() + "/members");
                        detailMembers.push().setValue(currId);
                        detailMembers.push().setValue(toUserId);

                        DatabaseReference groupDetailRef = database.getReference("group_details/" + push.getKey() + "/typing_detail");
                        groupDetailRef.setValue(new TypingDetail(false));
                    });

                    ParseQuery<UserPublicColumns> publicColumnsQuery = ParseQuery.getQuery(UserPublicColumns.class);
                    List<String> participantsIds = new ArrayList<>();
                    participantsIds.add(toUserId);
                    participantsIds.add(currId);
                    publicColumnsQuery.whereContainedIn(UserPublicColumns.KEY_USER_ID, participantsIds);
                    publicColumnsQuery.findInBackground((userPublicColumns, e) -> userPublicColumns.forEach(userPublicColumn -> {
                        List<String> groupChatIDs = userPublicColumn.getGroupChatIds();
                        groupChatIDs.add(finalDmID);
                        userPublicColumn.setGroupChatIds(groupChatIDs);
                        userPublicColumn.saveInBackground();
                    }));
                }
                GroupChatMethods.toConversationDetail(getContext(), finalDmID, false, toUserId);
            });

        });

        btnVisitProfile.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), ProfileActivity.class);
            i.putExtra("id", toUserId);
            startActivity(i);
            this.dismiss();
        });

    }
}