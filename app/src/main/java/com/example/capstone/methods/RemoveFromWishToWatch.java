package com.example.capstone.methods;

import com.example.capstone.models.VideoContent;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;
import java.util.Objects;

public class RemoveFromWishToWatch {
    public static void removeContent(List<VideoContent> wishToWatch, ParseUser user, VideoContent videoContent) {
        //remove video content from wish to watch
        int wishToWatchSize = wishToWatch.size();
        for (int i = 0; i < wishToWatchSize; i++) {
            VideoContent wishToWatchContent = null;
            try {
                wishToWatchContent = wishToWatch.get(i).fetchIfNeeded();
                if (Objects.equals(wishToWatchContent.getTitle(), videoContent.getTitle())) {
                    wishToWatch.remove(i);
                    user.put("wishToWatch", wishToWatch);
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
