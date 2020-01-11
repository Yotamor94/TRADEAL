package com.example.tradeal;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
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
    private static int imageId = 0;
    ArrayList<Bitmap> images;
    pagerAdapter pagerAdapter;
    AddListingEventListener listener;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_listing, container, false);

        images = (ArrayList<Bitmap>)getArguments().getSerializable("images");
        user = (User)getArguments().getSerializable("user");

        final EditText TitleEt = view.findViewById(R.id.listingTitleEt);
        final EditText DescriptionEt = view.findViewById(R.id.ListingDescriptionEt);

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
                    Bitmap image = images.get(i);
                    final StorageReference filePath = storageReference.child("listings").child(imageId + ".jpg");
                    imageId++;
                    byte[] data;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    data = baos.toByteArray();

                    UploadTask uploadTask = filePath.putBytes(data);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageUrls.add(filePath.getDownloadUrl().toString());
                            if (pos == images.size() - 1){
                                listener.addListingClicked(new Listing(imageUrls, TitleEt.getText().toString(), DescriptionEt.getText().toString(), user), progressDialog);
                            }
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

        ViewPager pager = view.findViewById(R.id.addListingImageViewPager);

        pagerAdapter = new pagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,images);
        pager.setAdapter(pagerAdapter);

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
            return ListingImageCellFragment.newInstance(images.get(position));
        }

        @Override
        public int getCount() {
            return images.size();
        }
    }

    public ListingImageCellFragment.ImageCellListener imageCellListener = new ListingImageCellFragment.ImageCellListener() {
        @Override
        public void deleteListingImage(Bitmap Image) {
            images.remove(Image);
            pagerAdapter.notifyDataSetChanged();
        }
    };
}
