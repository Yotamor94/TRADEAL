package com.example.tradeal;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListingListFragment extends Fragment {

    RecyclerView recyclerView;
    Listing[] listings;
    ListingListListener listener;
    User user;

    interface ListingListListener{
        void onListingClicked(Listing listing);
        void onFavouriteListingClicked(Listing listing);
        void onDeleteListingClicked(Listing listing);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ListingListListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException("the activity must implement ListingListListener");
        }
    }

    public static ListingListFragment newInstance(Listing[] listings, User user){
        ListingListFragment fragment = new ListingListFragment();
        Bundle args = new Bundle();
        args.putSerializable("listings", listings);
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listings_list, container, false);

        recyclerView = view.findViewById(R.id.listingsFragmentRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        user = (User)getArguments().getSerializable("user");
        listings = (Listing[])getArguments().getSerializable("listings");

        com.example.tradeal.RecyclerViewAdapter.ListingListListener adapterListener = new com.example.tradeal.RecyclerViewAdapter.ListingListListener() {
            @Override
            public void onListingClicked(Listing listing) {
                listener.onListingClicked(listing);
            }

            @Override
            public void onFavouriteListingClicked(Listing listing) {
                listener.onFavouriteListingClicked(listing);
            }

            @Override
            public void onDeleteListingClicked(Listing listing) {
                listener.onDeleteListingClicked(listing);
                ArrayList<Listing> list = Lists.newArrayList(listings);
                list.remove(listing);
                Listing[] a = new Listing[0];
                listings = list.toArray(a);
            }
        };

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(listings, user);
        adapter.setListener(adapterListener);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
