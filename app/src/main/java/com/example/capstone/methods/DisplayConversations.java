package com.example.capstone.methods;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.capstone.adapters.ConversationsAdapter;
import com.example.capstone.models.GroupDetail;
import com.example.capstone.models.Message;
import com.example.capstone.models.TypingDetail;
import com.example.capstone.models.UserPublicColumns;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DisplayConversations {
    public static void fetchGroupDetails(ParseUser user, DatabaseReference groupDetailsRef, List<GroupDetail> allGroupDetails, ConversationsAdapter adapter, TextView tvJoinAGroupChat) {
        ParseQuery<UserPublicColumns> publicColumnsQuery = ParseQuery.getQuery(UserPublicColumns.class);
        publicColumnsQuery.whereEqualTo(UserPublicColumns.KEY_USER_ID, user.getObjectId());

        publicColumnsQuery.findInBackground((userPublicColumns, e) -> {
            UserPublicColumns userPublicColumn = userPublicColumns.get(0);
            List<String> groupChatIDs = userPublicColumn.getGroupChatIds();

            // If the user is in any group chats
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
                                TypingDetail typingDetail = child.child("typing_detail").getValue(TypingDetail.class);
                                groupDetail.setTyping_detail(typingDetail);
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
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            } else {
                tvJoinAGroupChat.setVisibility(View.VISIBLE);
            }

        });
    }
}
