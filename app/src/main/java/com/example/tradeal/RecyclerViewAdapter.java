package com.example.tradeal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ListingViewHolder> {

    Listing[] listings;
    ListingListListener listener;
    User user;

    interface ListingListListener {
        void onListingClicked(Listing listing);

        void onDeleteListingClicked(Listing listing);

        void onFavouriteListingClicked(Listing listing);
    }

    public void setListener(ListingListListener listener) {
        this.listener = listener;
    }

    public RecyclerViewAdapter(Listing[] listings, User user) {
        this.listings = listings;
        this.user = user;
    }

    public class ListingViewHolder extends RecyclerView.ViewHolder {

        ImageView listingImageView;
        ImageButton favouriteImageButton;
        ImageButton deleteImageBtn;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);

            this.listingImageView = itemView.findViewById(R.id.listingImage);
            this.favouriteImageButton = itemView.findViewById(R.id.recyclerViewFavouriteBtn);
            this.deleteImageBtn = itemView.findViewById(R.id.recyclerViewDeleteBtn);

            favouriteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFavouriteListingClicked(listings[getAdapterPosition()]);
                    notifyItemChanged(getAdapterPosition());
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onListingClicked(listings[getAdapterPosition()]);
                }
            });

            deleteImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDeleteListingClicked(listings[getAdapterPosition()]);
                    ArrayList<Listing> list = Lists.newArrayList(listings);
                    list.remove(getAdapterPosition());
                    Listing[] a = new Listing[0];
                    listings = list.toArray(a);
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_listing, parent, false);
        ListingViewHolder listingViewHolder = new ListingViewHolder(view);
        return listingViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {

        holder.favouriteImageButton.setVisibility(View.VISIBLE);
        holder.deleteImageBtn.setVisibility(View.GONE);

        Listing listing = listings[position];
        if (listing.getImageUrls().get(0) != null) {
            Glide.with(holder.itemView.getContext()).load(listing.getImageUrls().get(0)).into(holder.listingImageView);
        }
        if (user != null) {
            if (listing.getUserEmail().equals(user.getEmail())) {
                holder.favouriteImageButton.setVisibility(View.GONE);
                holder.deleteImageBtn.setVisibility(View.VISIBLE);
            } else {
                if (user != null) {
                    if (user.getFavouriteIds().contains(listing.getId()))
                        holder.favouriteImageButton.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_favorite_black_24dp));
                    else
                        holder.favouriteImageButton.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_favorite_border_black_24dp));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return listings.length;
    }
}
