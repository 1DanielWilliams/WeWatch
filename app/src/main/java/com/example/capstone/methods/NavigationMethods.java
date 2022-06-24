package com.example.capstone.methods;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.capstone.R;
import com.example.capstone.activities.FeedActivity;
import com.example.capstone.activities.ConversationActivity;
import com.example.capstone.activities.MovieSelectionActivity;
import com.example.capstone.activities.ProfileActivity;
import com.example.capstone.activities.TVShowSelectionActivity;
import com.example.capstone.activities.logOrSignActivity;
import com.google.android.material.navigation.NavigationView;

public class NavigationMethods {

    public static void setUpNavDrawer(Context context, NavigationView navDrawerFeed, ImageButton imBtnMenuFeed, DrawerLayout drawerLayout) {
        imBtnMenuFeed.setOnClickListener(v -> drawerLayout.open());

        // Hands logic of profile popup menu
        View headerView = navDrawerFeed.getHeaderView(0);
        headerView.findViewById(R.id.imBtnDropDown).setOnClickListener(v -> {
            showMenu(context, v, R.menu.popup_menu);
        });

        // onClickListener for navigation drawer items
        navDrawerFeed.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent i;
                Class currClass = context.getClass();
                switch (item.getItemId()) {
                    case R.id.optFeed:
                        if (currClass != FeedActivity.class) {
                            i = new Intent(context, FeedActivity.class);
                            context.startActivity(i);
                            item.setChecked(true);
                        }
                        drawerLayout.close();
                        return true;
                    case R.id.optMovies:
                        if (currClass != MovieSelectionActivity.class) {
                            i = new Intent(context, MovieSelectionActivity.class);
                            context.startActivity(i);
                            item.setChecked(true);
                        }
                        drawerLayout.close();
                        return true;
                    case R.id.optMessages:
                        if (currClass != ConversationActivity.class) {
                            i = new Intent(context, ConversationActivity.class);
                            context.startActivity(i);
                            item.setChecked(true);
                        }
                         drawerLayout.close();
                         return true;
                    case R.id.optTV:
                        if (currClass != TVShowSelectionActivity.class) {
                            i = new Intent(context, TVShowSelectionActivity.class);
                            context.startActivity(i);
                            item.setChecked(true);
                        }
                        drawerLayout.close();
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Determines which item to check in the menu
        Class<? extends Context> aClass = context.getClass();
        if (FeedActivity.class.equals(aClass)) {
            navDrawerFeed.setCheckedItem(R.id.optFeed);
        } else if (ConversationActivity.class.equals(aClass)) {
            navDrawerFeed.setCheckedItem(R.id.optMessages);
        }else if (MovieSelectionActivity.class.equals(aClass)) {
            navDrawerFeed.setCheckedItem(R.id.optMovies);
        } else if (TVShowSelectionActivity.class.equals(aClass)) {
            navDrawerFeed.setCheckedItem(R.id.optTV);
        }
    }

    public static void showMenu(Context context, View v, int popup_menu) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.getMenuInflater().inflate(popup_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            Intent i;
            switch (item.getItemId()) {
                case 2131231092: // TODO: not sure where these IDs come from
                    i = new Intent(context, ProfileActivity.class);
                    context.startActivity(i);
                    return true;
                case 2131231093: // TODO: not sure where these IDs come from
                    Toast.makeText(context, "Logged user out", Toast.LENGTH_SHORT).show();
                    i = new Intent(context, logOrSignActivity.class);
                    context.startActivity(i);
                    return true;
                default:
                    Log.i("FeedActivity", "onMenuItemClick: hit default " + item.getItemId());
                    return false;
            }
        });

        // TODO: do i need this?
        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });

        popup.show();
    }


}
