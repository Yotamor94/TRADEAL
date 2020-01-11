package com.example.tradeal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class AddListingFragment extends Fragment {

    private User user;
    ArrayList<String> imageUrls;
    pagerAdapter pagerAdapter;
    AddListingEventListener listener;

    public static AddListingFragment newInstance(User user, ArrayList<String> imageUrls){
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        bundle.putSerializable("imageUrls", imageUrls);
        AddListingFragment fragment = new AddListingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    interface AddListingEventListener{
        void addListingClicked(Listing listing);
    }

    @Override
    public void onStart() {
        super.onStart();

        try{
            listener = (AddListingEventListener)getContext();
        }catch (ClassCastException e){
            throw new ClassCastException("the activity must implement AddListingEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_listing, container, false);

        imageUrls = getArguments().getStringArrayList("imageUrls");
        user = (User)getArguments().getSerializable("user");

        final EditText TitleEt = view.findViewById(R.id.listingTitleEt);
        final EditText DescriptionEt = view.findViewById(R.id.ListingDescriptionEt);

        Button addListingBtn = view.findViewById(R.id.addListingBtn);

        addListingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.addListingClicked(new Listing(imageUrls, TitleEt.getText().toString(), DescriptionEt.getText().toString(), null));
            }
        });

        ViewPager pager = view.findViewById(R.id.addListingImageViewPager);

        pagerAdapter = new pagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,imageUrls);
        pager.setAdapter(pagerAdapter);

        return view;
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

    public ListingImageCellFragment.ImageCellListener imageCellListener = new ListingImageCellFragment.ImageCellListener() {
        @Override
        public void deleteListingImage(String ImageUrl) {
            imageUrls.remove(ImageUrl);
            pagerAdapter.notifyDataSetChanged();
        }
    };
}
