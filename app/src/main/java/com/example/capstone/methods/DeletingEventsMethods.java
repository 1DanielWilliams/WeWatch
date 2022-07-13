package com.example.capstone.methods;

import com.example.capstone.models.Event;
import com.example.capstone.models.UserPublicColumns;
import com.example.capstone.models.VideoContent;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class DeletingEventsMethods {

    public static void removeChatId(String groupChatID, Event event) {
        ParseQuery<UserPublicColumns> groupChatIDQuery = ParseQuery.getQuery(UserPublicColumns.class);
        List<String> groupChatIDs = new ArrayList<>();
        groupChatIDs.add(groupChatID);
        groupChatIDQuery.whereContainedIn(UserPublicColumns.KEY_GROUPCHAT_IDS, groupChatIDs);

        groupChatIDQuery.findInBackground((userPublicColumns, e12) -> userPublicColumns.forEach(userPublicColumn -> {
            List<String> IDs = userPublicColumn.getGroupChatIds();
            IDs.remove(groupChatID);
            userPublicColumn.setGroupChatIds(IDs);

            List<VideoContent> watchedContent = userPublicColumn.getWatchedContent();
            VideoContent content = null;
            try {
                content = event.getVideContent().fetch();
                if (!watchedContent.contains(content)) {
                    watchedContent.add(content);
                    userPublicColumn.setWatchedContent(watchedContent);
                }
            } catch (com.parse.ParseException ex) {
                ex.printStackTrace();
            }

            userPublicColumn.saveInBackground();
        }));
    }

}
