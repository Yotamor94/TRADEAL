package com.example.tradeal;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewListingFragment extends Fragment {

    Listing listing;
    User user;
    User cUser;
    LinearLayout dotsLL;
    ImageView[] dots;
    viewListingEventListener listener;

    public static ViewListingFragment newInstance(Listing listing, User user) {

        Bundle args = new Bundle();
        args.putSerializable("listing", listing);
        args.putSerializable("user", user);
        ViewListingFragment fragment = new ViewListingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    interface viewListingEventListener {
        void favouriteClicked(Listing listing);
        void editListingClicked(Listing listing);
        void messageSellerClicked(User oUser);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (viewListingEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("the activity must implement viewListingEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_listing, container, false);

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("Just a sec");
        dialog.setMessage("Getting listing info");
        dialog.setCancelable(false);

        listing = (Listing) getArguments().getSerializable("listing");
        cUser = (User) getArguments().getSerializable("user");

        TextView listingTitle = view.findViewById(R.id.viewListingTitleTv);
        listingTitle.setText(listing.getTitle());

        final TextView listingDescription = view.findViewById(R.id.viewListingDescriptionTv);
        listingDescription.setText(listing.getDescription());

        final ImageButton favouriteIb = view.findViewById(R.id.viewListingFavouriteBtn);
        final ImageButton editBtn = view.findViewById(R.id.editListingBtn);

        final ImageView userIv = view.findViewById(R.id.viewListingUserImage);
        final TextView listingUsernameTv = view.findViewById(R.id.viewListingUserNameTv);
        FirebaseFirestore.getInstance().collection("users").document(listing.getUserEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (ViewListingFragment.this.isVisible()) {
                    user = documentSnapshot.toObject(User.class);
                    Glide.with(getContext()).load(user.getImageUrl()).into(userIv);
                    listingUsernameTv.setText(user.getUsername());

                    dialog.dismiss();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("getListingUser", "onFailure: " + e.getMessage());
            }
        });


        if (cUser != null) {
            if (listing.getUserEmail().equals(cUser.getEmail())) {
                favouriteIb.setVisibility(View.GONE);
                editBtn.setVisibility(View.VISIBLE);
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.editListingClicked(listing);
                    }
                });
            } else {
                favouriteIb.setImageDrawable(cUser.getFavouriteIds().contains(listing.getId()) ? ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_black_24dp) : ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_black_24dp));
                favouriteIb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.favouriteClicked(listing);
                        favouriteIb.setImageDrawable(cUser.getFavouriteIds().contains(listing.getId()) ? ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_black_24dp) : ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_black_24dp));
                    }
                });
            }
        }else{
            favouriteIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        Button sendMessageBtn = view.findViewById(R.id.messageSellerBtn);
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.messageSellerClicked(user);
            }
        });

        RelativeLayout userLayout = view.findViewById(R.id.userLayout);
        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dotsLL = view.findViewById(R.id.viewListingDotsLL);

        ViewPager pager = view.findViewById(R.id.viewListingImageViewPager);
        String[] a = new String[listing.getImageUrls().size()];
        listing.getImageUrls().toArray(a);
        ViewPagerAdapter adapter = new ViewPagerAdapter(a);
        pager.setAdapter(adapter);
        pager.setCurrentItem(0);
        dots = new ImageView[a.length];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_circle_not_selected_24dp));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            dotsLL.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_circle_selected_black_24dp));


        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dots.length; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_circle_not_selected_24dp));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_circle_selected_black_24dp));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ImageView categoryImageView = view.findViewById(R.id.viewListingCategoryImage);
        categoryImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), listing.getCategory().getImageId()));
        return view;
    }

    private class ViewPagerAdapter extends PagerAdapter {

        String[] imageUrls;

        public ViewPagerAdapter(String[] imageUrls) {
            this.imageUrls = imageUrls;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(getContext()).load(imageUrls[position]).into(imageView);
            (container).addView(imageView, position);
            return imageView;
        }

        @Override
        public int getCount() {
            return imageUrls.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Nullable
        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
