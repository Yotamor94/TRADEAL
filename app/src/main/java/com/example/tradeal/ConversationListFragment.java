package com.example.tradeal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ConversationListFragment extends Fragment {

    RecyclerView recyclerView;
    User cUser;
    ArrayList<User> oUsers;
    ConversationListListener listener;

    interface ConversationListListener{
        void conversationClicked(User oUser, ArrayList<Message> messages);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ConversationListListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException("the activity must implement ConversationListListener");
        }
    }

    public static ConversationListFragment newInstance(User cUser, ArrayList<User> oUsers){
        Bundle args = new Bundle();
        args.putSerializable("user", cUser);
        args.putSerializable("oUsers", oUsers);
        ConversationListFragment fragment = new ConversationListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversations_list, container, false);

        recyclerView = view.findViewById(R.id.conversationRecyclerView);

        cUser = (User) getArguments().getSerializable("user");
        oUsers = (ArrayList<User>) getArguments().getSerializable("oUsers");

        TextView noMessagesTv = view.findViewById(R.id.noMessagesTv);

        if (oUsers != null && oUsers.size() > 0){
            noMessagesTv.setVisibility(View.GONE);
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        conversationRecyclerViewAdapter.ConversationListListener recyclerViewListener = new conversationRecyclerViewAdapter.ConversationListListener() {
            @Override
            public void conversationClicked(User oUser, ArrayList<Message> messages) {
                listener.conversationClicked(oUser, messages);
            }
        };

        conversationRecyclerViewAdapter adapter = new conversationRecyclerViewAdapter(oUsers, cUser.getEmail());
        adapter.setListener(recyclerViewListener);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
