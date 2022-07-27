package com.example.capstone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstone.R;
import com.example.capstone.fragments.ProfileOrDmFragment;
import com.parse.ParseUser;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private Context context;
    private List<ParseUser> users;


    public UsersAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvScreenNameUsers;
        private TextView tvUsernameUsers;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvScreenNameUsers = itemView.findViewById(R.id.tvScreenNameUsers);
            tvUsernameUsers = itemView.findViewById(R.id.tvUsernameUsers);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                String toUserId = users.get(position).getObjectId();
                String currId = ParseUser.getCurrentUser().getObjectId();
                FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                ProfileOrDmFragment profileOrDmFragment = ProfileOrDmFragment.newInstance(toUserId, currId);
                profileOrDmFragment.show(fm, "dm_or_profile");

            });
        }

        public void bind(ParseUser user) {
            tvScreenNameUsers.setText(user.getString("screenName"));
            tvUsernameUsers.setText("@" + user.getUsername());

        }
    }


}
