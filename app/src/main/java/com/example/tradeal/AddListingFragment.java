package com.example.tradeal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static android.app.Activity.RESULT_OK;

public class AddListingFragment extends Fragment {

    final int CAMERA_REQUEST_CODE = 2;
    private User user;
    private int imageId = 0;
    pagerAdapter pagerAdapter;
    AddListingEventListener listener;
    ArrayList<Bitmap> images;
    ViewPager pager;
    ImageView[] dots;
    LinearLayout dotsLL;

    public static AddListingFragment newInstance(User user, ArrayList<Bitmap> images){
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        bundle.putSerializable("images", images);
        AddListingFragment fragment = new AddListingFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    interface AddListingEventListener{
        void addListingClicked(Listing listing, ProgressDialog dialog);
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

        images = (ArrayList<Bitmap>)getArguments().getSerializable("images");
        user = (User)getArguments().getSerializable("user");

        final EditText TitleEt = view.findViewById(R.id.listingTitleEt);
        final EditText DescriptionEt = view.findViewById(R.id.ListingDescriptionEt);

        final Category category = new Category();

        final LinearLayout categoryLinearLayout = view.findViewById(R.id.addListingCategoryLinearLayout);
        final ImageView categoryImage = view.findViewById(R.id.addListingCategoryImageView);
        final TextView categoryNameTv = view.findViewById(R.id.addListingCategoryNameTv);

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
                                        listener.addListingClicked(new Listing(imageUrls, TitleEt.getText().toString(), DescriptionEt.getText().toString(), user.getEmail(), category), progressDialog);
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

        pager = view.findViewById(R.id.addListingImageViewPager);

        pagerAdapter = new pagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,images);
        pager.setAdapter(pagerAdapter);

        dotsLL = view.findViewById(R.id.addListingDotsLL);
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
