package com.example.tradeal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static android.app.Activity.RESULT_OK;

public class EditListingFragment extends Fragment {
    final int CAMERA_REQUEST_CODE = 2;
    private User user;
    private int imageId = 0;
    EditListingEventListener listener;
    pagerAdapter pagerAdapter;
    ArrayList<Bitmap> images;
    ViewPager pager;
    ImageView[] dots;
    LinearLayout dotsLL;
    Listing lastListing;

    public static EditListingFragment newInstance(User user, Listing listing){
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        bundle.putSerializable("listing", listing);
        EditListingFragment fragment = new EditListingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    interface EditListingEventListener{
        void onEditListingClicked(Listing newListing, Listing oldListing, ProgressDialog dialog);
    }

    @Override
    public void onStart() {
        super.onStart();

        try{
            listener = (EditListingEventListener)getContext();
        }catch (ClassCastException e){
            throw new ClassCastException("the activity must implement EditListingEventListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            images.add(image);
            dotsLL.removeAllViews();
            dots = new ImageView[images.size()];
            for (int i = 0; i < dots.length; i++){

                dots[i] = new ImageView(getContext());
                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_circle_not_selected_24dp));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(8, 0, 8, 0);

                dotsLL.addView(dots[i], params);
            }
            dots[pager.getCurrentItem()].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_circle_selected_black_24dp));
            pager.setAdapter(new pagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, images));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_listing, container, false);

        view.findViewById(R.id.addListingFragmentLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading listing info");
        progressDialog.setTitle("Just a sec");
        progressDialog.setCancelable(false);
        progressDialog.show();

        pager = view.findViewById(R.id.addListingImageViewPager);

        dotsLL = view.findViewById(R.id.addListingDotsLL);
        images = new ArrayList<>();

        lastListing = (Listing) getArguments().getSerializable("listing");
        user = (User) getArguments().getSerializable("user");
        for (int i = 0; i < lastListing.getImageUrls().size(); i++){
            Glide.with(getContext()).asBitmap().load(lastListing.getImageUrls().get(i)).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    images.add(resource);

                    pagerAdapter = new  pagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,images);
                    pager.setAdapter(pagerAdapter);

                    dots = new ImageView[images.size()];

                    for (int i = 0; i < dots.length; i++){

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
                            for (int i = 0; i < dots.length; i++){
                                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_circle_not_selected_24dp));
                            }
                            dots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_circle_selected_black_24dp));
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                    progressDialog.dismiss();
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }

        final EditText TitleEt = view.findViewById(R.id.listingTitleEt);
        final EditText DescriptionEt = view.findViewById(R.id.ListingDescriptionEt);

        final Category category = new Category();

        final LinearLayout categoryLinearLayout = view.findViewById(R.id.addListingCategoryLinearLayout);
        final ImageView categoryImage = view.findViewById(R.id.addListingCategoryImageView);
        final TextView categoryNameTv = view.findViewById(R.id.addListingCategoryNameTv);

        TitleEt.setText(lastListing.getTitle());
        DescriptionEt.setText(lastListing.getDescription());
        category.setName(lastListing.getCategory().getName());
        categoryImage.setImageDrawable(ContextCompat.getDrawable(getContext(), lastListing.getCategory().getImageId()));
        categoryNameTv.setText(category.getName());

        categoryLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), categoryLinearLayout);
                popupMenu.inflate(R.menu.category_pop_up_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        category.setName(item.getTitle().toString());
                        categoryImage.setImageDrawable(ContextCompat.getDrawable(getContext(), category.getImageId()));
                        categoryNameTv.setText(category.getName());
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        FloatingActionButton addImageBtn = view.findViewById(R.id.addPhotoToListingBtn);

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });


        Button addListingBtn = view.findViewById(R.id.addListingBtn);
        addListingBtn.setText(getString(R.string.ConfirmEdit));
        addListingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setMessage("Please Wait, creating listing");
                progressDialog.show();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                final ArrayList<String> imageUrls = new ArrayList<>();

                for (int i = 0; i < images.size(); i++){
                    final int pos = i;
                    final Bitmap image = images.get(i);
                    final StorageReference filePath = storageReference.child("listings").child(user.getEmail()).child(TitleEt.getText().toString()).child(imageId + ".jpg");
                    imageId++;
                    byte[] data;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    data = baos.toByteArray();

                    UploadTask uploadTask = filePath.putBytes(data);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrls.add(uri.toString());
                                    if (pos == images.size() - 1){
                                        listener.onEditListingClicked(new Listing(imageUrls, TitleEt.getText().toString(), DescriptionEt.getText().toString(), user.getEmail(), category), lastListing, progressDialog);
                                    }
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("uploadListingImageFail", "onFailure: " + e.getMessage());
                        }
                    });
                }
            }
        });





        return view;
    }

    private class pagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Bitmap> images;

        public pagerAdapter(@NonNull FragmentManager fm, int behavior, ArrayList<Bitmap> images) {
            super(fm, behavior);
            this.images = images;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            ListingImageCellFragment.ImageCellListener imageCellListener = new ListingImageCellFragment.ImageCellListener() {
                @Override
                public void deleteListingImage(Bitmap Image) {
                    if (images.size() == 1){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("A listing must have at least one image, please add more images to delete this one").setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                    }
                    else {
                        images.remove(Image);
                        dotsLL.removeAllViews();
                        dots = new ImageView[images.size()];
                        for (int i = 0; i < dots.length; i++) {

                            dots[i] = new ImageView(getContext());
                            dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_circle_not_selected_24dp));

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                            params.setMargins(8, 0, 8, 0);

                            dotsLL.addView(dots[i], params);
                        }
                        pager.setAdapter(new pagerAdapter(getChildFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, images));
                        dots[pager.getCurrentItem()].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_circle_selected_black_24dp));

                    }
                }
            };
            ListingImageCellFragment fragment = ListingImageCellFragment.newInstance(images.get(position));
            fragment.setListener(imageCellListener);
            return fragment;
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }
}
