package com.example.tradeal;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MyAreaFragment extends Fragment {
    private Listing[] favourites;
    private User user;
    private ArrayList<Listing> userListings;

    public static MyAreaFragment newInstance(Listing[] favourites, ArrayList<Listing> userListings, User user) {
        Bundle args = new Bundle();
        args.putSerializable("favourites", favourites);
        args.putSerializable("userListings", userListings);
        args.putSerializable("user", user);
        MyAreaFragment fragment = new MyAreaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_area, container, false);

        user = (User) getArguments().getSerializable("user");
        favourites = (Listing[]) getArguments().getSerializable("favourites");
        userListings = (ArrayList<Listing>) getArguments().getSerializable("userListings");

        TabLayout tabLayout = view.findViewById(R.id.myAreaTabLayout);
        ViewPager pager = view.findViewById(R.id.myAreaViewPager);
        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);


        return view;
    }

    private class PagerAdapter extends FragmentStatePagerAdapter{

        public PagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return ListingListFragment.newInstance(favourites == null ? new Listing[0] : favourites, user);
                case 1:
                    Listing[] listings = new Listing[0];
                    return ListingListFragment.newInstance(userListings == null ? listings : userListings.toArray(listings), user);
                default:
                    return ListingListFragment.newInstance(favourites == null ? new Listing[0] : favourites, user);
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.favourites);
                case 1:
                    return "My Listings";
                default:
                    return getString(R.string.favourites);
            }

        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
