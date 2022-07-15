package com.example.capstone.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.capstone.R;
import com.example.capstone.adapters.DatesAdapter;
import com.example.capstone.models.Event;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


public class OtherDatesFragment extends DialogFragment {

    private static final String ARG_PARAM1 = "event";
    private Event event;
    private RecyclerView rvDates;
    private DatesAdapter adapter;
    private List<String> allDates;
    private List<ParseUser> allAuthors;
    private List<List<ParseUser>> allInterestedUsers;
    private FloatingActionButton fab;


    public OtherDatesFragment() {
        // Required empty public constructor
    }


    public static OtherDatesFragment newInstance(Event event) {
        OtherDatesFragment fragment = new OtherDatesFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_dates, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvDates = view.findViewById(R.id.rvDates);
        fab = view.findViewById(R.id.fabDates);

        ImageButton iBtnUpFromDates = view.findViewById(R.id.iBtnUpFromDates);
        iBtnUpFromDates.setOnClickListener(v -> this.dismiss());


        allDates = event.getDates();
        allAuthors = event.getUsers();
        allInterestedUsers = event.getInterestedUsers();
        adapter = new DatesAdapter(getContext(), allDates, allAuthors, allInterestedUsers);

        rvDates.setAdapter(adapter);
        rvDates.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();

        fab.show();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("OtherDatesFragment", "onClick: ");
                //date selection
                //append date + time to the event
                //save event in background
                //notify adapter changed
            }
        });


    }
}