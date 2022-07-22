package com.example.capstone.methods;

import android.content.Context;
import android.content.Intent;

import com.example.capstone.activities.conversation.ConversationDetailActivity;

public class GroupChatMethods {

    public static void toConversationDetail(Context context, String chatId, boolean isGroupChat, String toId) {

        Intent i = new Intent(context, ConversationDetailActivity.class);
       i.putExtra("chat_id", chatId);
       i.putExtra("is_group_chat", isGroupChat);
       i.putExtra("to_user_id", toId);
        context.startActivity(i);
    }
}
