package com.example.tradeal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import javax.annotation.ParametersAreNonnullByDefault;


public class MainActivity extends AppCompatActivity implements SignEventListener, UserDialogFragment.userDialogEventListener, AddListingFragment.AddListingEventListener {

    final String USERS_DB_COLLECTION_NAME = "users", LISTINGS_DB_COLLECTION_NAME = "listings";
    CoordinatorLayout coordinatorLayout;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;
    User user;
    FloatingActionButton addListingBtn;
    final int CAMERA_REQUEST_CODE = 1;


    @Override
    public void onSignInClick(final String email, String password) {
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("signDialog")).commit();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(coordinatorLayout, "Sign in successful", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(coordinatorLayout, "Wrong password", Snackbar.LENGTH_LONG);
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Snackbar.make(coordinatorLayout, "Can't find a user with this email", Snackbar.LENGTH_LONG);
                    } else {
                        Snackbar.make(coordinatorLayout, "Sign in failed", Snackbar.LENGTH_SHORT);
                    }
                }
            }
        });
    }

    @Override
    public void onSignUpClick(final String Email, final String username, final String password, Bitmap image) {
       // getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("signDialog")).commit();

        final StorageReference filePath = storageReference.child("users").child(username + ".jpg");

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait, creating your account");
        dialog.setCancelable(false);
        dialog.show();


        if (image != null) {
            byte[] data;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String imageUrl = filePath.getDownloadUrl().toString();
                    user = new User(username, Email, imageUrl);
                    createUser(password, dialog);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("uploadImage", "onFailure: " + e.toString());
                }
            });
        }else{
            user = new User(username, Email, "");
            createUser(password, dialog);
        }


    }

    public void createUser(String password, final ProgressDialog dialog){
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("signDialog")).commit();
                Snackbar.make(coordinatorLayout, "Sign up successful", Snackbar.LENGTH_SHORT).show();
                db.collection(USERS_DB_COLLECTION_NAME).document(user.getEmail()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(coordinatorLayout, getString(R.string.Welcome) + user.getUsername(), Snackbar.LENGTH_SHORT);
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("SignUpUser", "onFailure: exception:" + e.getMessage());
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                if (e.getClass() == FirebaseAuthWeakPasswordException.class) {
                    Toast.makeText(MainActivity.this, getString(R.string.weakPassword), Toast.LENGTH_LONG).show();
                } else if (e.getClass() == FirebaseAuthInvalidCredentialsException.class) {
                    Toast.makeText(MainActivity.this, getString(R.string.invalidEmail), Toast.LENGTH_LONG).show();
                } else if (e.getClass() == FirebaseAuthUserCollisionException.class) {
                    Toast.makeText(MainActivity.this, getString(R.string.emailUsed), Toast.LENGTH_LONG).show();
                }
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SignDialogFragment signDialogFragment = new SignDialogFragment();
                signDialogFragment.show(ft, "signDialog");
            }
        });
    }

    @Override
    public void onManageProfileClick() {
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("userDialog")).commit();
    }

    @Override
    public void onSignOutClicked() {
        firebaseAuth.signOut();
        user = null;
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("userDialog")).commit();
    }

    @Override
    public void addListingClicked(Listing listing, final ProgressDialog dialog) {
        db.collection(USERS_DB_COLLECTION_NAME).document(user.getEmail()).collection(LISTINGS_DB_COLLECTION_NAME).document(user.getNumOfListings() + "").set(listing).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                user.setNumOfListings(user.getNumOfListings() + 1);
                dialog.dismiss();
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("addListing"));
                addListingBtn.setVisibility(View.VISIBLE);
                //go to view Listing Page
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleIntent(getIntent());

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        coordinatorLayout = findViewById(R.id.coordinatorMainLayout);

        Toolbar toolbar = findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait, just getting some things ready for you");
        dialog.setCancelable(false);
        dialog.show();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                db.collection(USERS_DB_COLLECTION_NAME).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (firebaseAuth.getCurrentUser() == null) {
                                    user = null;
                                } else if (documentSnapshot.getData().get("email").toString().toLowerCase().equals(firebaseAuth.getCurrentUser().getEmail())) {
                                    user = documentSnapshot.toObject(User.class);
                                }
                            }
                        }
                        dialog.dismiss();
                    }
                });
                supportInvalidateOptionsMenu();
            }
        };

        addListingBtn = findViewById(R.id.addListingFloatingBtn);
        addListingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap image = (Bitmap)data.getExtras().get("data");
        ArrayList<Bitmap> images = new ArrayList<>();
        images.add(image);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            getSupportFragmentManager().beginTransaction().replace(R.id.coordinatorMainLayout, AddListingFragment.newInstance(user, images), "addListing").addToBackStack(null).commit();
            addListingBtn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH == intent.getAction()) {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            //perform search
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQueryRefinementEnabled(true);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        final MenuItem item = menu.findItem(R.id.menuUserBtn);

        if (user != null && user.getImageUrl() != null && !user.getImageUrl().equals("")) {
            Glide.with(MainActivity.this).asDrawable().load(user.getImageUrl()).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    item.setIcon(resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuUserBtn:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (firebaseAuth.getCurrentUser() != null) {
                    UserDialogFragment userDialogFragment = UserDialogFragment.newInstance(user);
                    userDialogFragment.show(ft, "userDialog");
                } else {
                    SignDialogFragment signDialogFragment = new SignDialogFragment();
                    signDialogFragment.show(ft, "signDialog");
                }

        }
        return super.onOptionsItemSelected(item);
    }
}
