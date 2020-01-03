package com.example.tradeal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class AddListingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_listing, container, false);

        EditText TitleEt = view.findViewById(R.id.listingTitleEt);
        EditText DescriptionEt = view.findViewById(R.id.ListingDescriptionEt);


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private class pagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<String> imageUrls;

        public pagerAdapter(@NonNull FragmentManager fm, int behavior, ArrayList<String> imageUrls) {
            super(fm, behavior);
            this.imageUrls = imageUrls;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return ListingImageCellFragment.newInstance(imageUrls.get(position));
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }
    }
}
