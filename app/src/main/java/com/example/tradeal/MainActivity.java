package com.example.tradeal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ParametersAreNonnullByDefault;


public class MainActivity extends AppCompatActivity implements SignEventListener, UserDialogFragment.userDialogEventListener, AddListingFragment.AddListingEventListener, ListingListFragment.ListingListListener, ViewListingFragment.viewListingEventListener, EditListingFragment.EditListingEventListener, ConversationListFragment.ConversationListListener, MessageListFragment.MessageListEventListener {

    //<editor-fold desc="variables">
    final static int CAMERA_REQUEST_CODE = 1, WRITE_EXT_REQUEST_CODE = 0, MESSAGE_NOTIF_ID = 1;
    final String USERS_DB_COLLECTION_NAME = "users", LISTINGS_DB_COLLECTION_NAME = "listings";
    CoordinatorLayout coordinatorLayout;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseInstanceId firebaseInstanceId = FirebaseInstanceId.getInstance();
    StorageReference storageReference;
    User user;
    FloatingActionButton addListingBtn;
    List<Listing> listings;
    BottomNavigationView bottomNavigationView;
    boolean finished;
    Bitmap circularUserImage;
    boolean isComplete;
    MessageListFragment messageListFragment;
    final String apiTokenKey = "AAAAGOilxxw:APA91bEuM-WjZBbl9-QiHD3eC0s0yPc9e6THVUuIWyfGUsGs7MxQGLeQxsCG4aCFIbc8XezlTJUd5IKCrOkdLLMTKoPIgxBBDHK9fnnkQOz1UBDH0nPW3gSZG_ZTqY1R9LIzyMn7d83U";
    BroadcastReceiver receiver;
    mainActivityListener listener;
    NotificationManager manager;
    //</editor-fold>

    @Override
    public void onSignInClick(final String email, String password) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait, fetching your account");
        dialog.setCancelable(false);
        dialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(coordinatorLayout, "Sign in successful", Snackbar.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("signDialog")).commit();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(coordinatorLayout, "Wrong password", Snackbar.LENGTH_LONG);
                        Toast.makeText(MainActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Snackbar.make(coordinatorLayout, "Can't find a user with this email", Snackbar.LENGTH_LONG);
                        Toast.makeText(MainActivity.this, "Can't find a user with this email", Toast.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(coordinatorLayout, "Sign in failed", Snackbar.LENGTH_SHORT);
                        Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onSignUpClick(final String Email, final String username, final String password, Uri image) {

        final StorageReference filePath = storageReference.child("users").child(Email + ".jpg");

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait, creating your account");
        dialog.setCancelable(false);
        dialog.show();


        if (image != null) {
            UploadTask uploadTask = filePath.putFile(image);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            String imageUrl = uri.toString();
                            user = new User(username, Email.toLowerCase(), imageUrl);
                            createUser(password, dialog);

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("uploadImage", "onFailure: " + e.toString());
                }
            });
        } else {
            user = new User(username, Email.toLowerCase(), "");
            createUser(password, dialog);
        }


    }

    public void createUser(String password, final ProgressDialog dialog) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Snackbar.make(coordinatorLayout, "Sign up successful", Snackbar.LENGTH_SHORT).show();
                db.collection(USERS_DB_COLLECTION_NAME).document(user.getEmail()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(coordinatorLayout, getString(R.string.Welcome) + user.getUsername(), Snackbar.LENGTH_SHORT);
                        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("signDialog")).commit();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("SignUpUser", "exception:" + e.getMessage());
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
            }
        });
    }

    @Override
    public void onManageProfileClick() {
        getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("userDialog")).commit();
    }

    @Override
    public void onSignOutClicked() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Signing you out");
        dialog.setTitle("Just a sec");
        dialog.setCancelable(false);
        dialog.show();
        firebaseInstanceId.getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                dialog.dismiss();
                user.getDeviceTokens().remove(instanceIdResult.getToken());
                db.collection(USERS_DB_COLLECTION_NAME).document(user.getEmail()).update("deviceTokens", user.getDeviceTokens());
                firebaseAuth.signOut();
                getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("userDialog")).commit();
            }
        });

    }

    @Override
    public void onListingClicked(Listing listing) {
        addListingBtn.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityLL, ViewListingFragment.newInstance(listing, user), "viewListing").addToBackStack(null).commit();
        bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    public void sendMessageClicked(final Message message, ArrayList<Message> messages, final User oUser) {
        if (user.getUsersConversedEmails() == null) {
            user.setUsersConversedEmails(new ArrayList<String>());
        }
        if (!user.getUsersConversedEmails().contains(message.getoUserEmail())) {
            user.addUserConversed(message.getoUserEmail());
            db.collection(USERS_DB_COLLECTION_NAME).document(user.getEmail()).update("usersConversedEmails", user.getUsersConversedEmails());
        }
        messages.add(message);
        Intent intent = new Intent(MainActivity.this, SendMessageService.class);
        intent.putExtra("message", message);
        intent.putExtra("oUser", oUser);
        intent.putExtra("user", user);
        startService(intent);

    }

    @Override
    public void backBtnClicked() {
        getSupportFragmentManager().popBackStack();
        bottomNavigationView.setVisibility(View.VISIBLE);
        addListingBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void deleteConversation(User oUser) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("messageList");
        if (fragment != null && fragment.isVisible())
            getSupportFragmentManager().beginTransaction().remove(fragment);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setVisibility(View.VISIBLE);
        addListingBtn.setVisibility(View.VISIBLE);
        user.getUsersConversedEmails().remove(oUser.getEmail());
        db.collection(USERS_DB_COLLECTION_NAME).document(user.getEmail()).update("usersConversedEmails", user.getUsersConversedEmails());
    }

    @Override
    public void conversationClicked(User oUser, ArrayList<Message> messages) {
        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityLL, MessageListFragment.newInstance(messages, user, oUser), "messageList").addToBackStack(null).commit();
        bottomNavigationView.setVisibility(View.GONE);
        addListingBtn.setVisibility(View.GONE);
    }

    @Override
    public void messageSellerClicked(final User oUser) {
        if (user != null) {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("getting you messages");
            progressDialog.setTitle("Just a sec");
            progressDialog.setCancelable(false);
            progressDialog.show();
            db.collection("messages").whereIn("oUserEmail", Arrays.asList(oUser.getEmail(), user.getEmail())).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    ArrayList<Message> messages;
                    if (queryDocumentSnapshots != null)
                        messages = new ArrayList<>(queryDocumentSnapshots.toObjects(Message.class));
                    else
                        messages = new ArrayList<>();
                    ArrayList<Message> toRemove = new ArrayList<>();
                    for (Message message : messages) {
                        if (!message.getUserEmail().equals(user.getEmail()) && !message.getUserEmail().equals(oUser.getEmail())) {
                            toRemove.add(message);
                        }
                    }
                    for (Message m : toRemove) {
                        messages.remove(m);
                    }
                    getSupportFragmentManager().popBackStackImmediate();
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityLL, MessageListFragment.newInstance(messages, user, oUser), "messageList").addToBackStack(null).commit();
                    progressDialog.dismiss();
                    bottomNavigationView.setVisibility(View.GONE);
                    addListingBtn.setVisibility(View.GONE);
                }
            });
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("You must be signed in to message").setPositiveButton(getString(R.string.SignIn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        SignDialogFragment signDialogFragment = new SignDialogFragment();
                        signDialogFragment.show(getSupportFragmentManager().beginTransaction(), "signDialog");
                        getSupportFragmentManager().popBackStack();
                        bottomNavigationView.setSelectedItemId(R.id.home);
                    }
                }
            }).show();
            /*.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    bottomNavigationView.setSelectedItemId(R.id.home);
                }
            })*/
        }

    }


    @Override
    public void onDeleteListingClicked(Listing listing) {
        listings.remove(listing);
        db.collection(LISTINGS_DB_COLLECTION_NAME).document(listing.getId()).delete();
        user.getListingIds().remove(listing.getId());
        db.collection(USERS_DB_COLLECTION_NAME).document(user.getEmail()).update("listingIds", user.getListingIds());
        bottomNavigationView.setSelectedItemId(R.id.myArea);
    }

    @Override
    public void editListingClicked(Listing listing) {
        getSupportFragmentManager().popBackStackImmediate();
        getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityLL, EditListingFragment.newInstance(user, listing)).addToBackStack(null).commit();
    }

    @Override
    public void onFavouriteListingClicked(Listing listing) {
        favourite(listing);
    }

    void favourite(Listing listing) {
        if (firebaseAuth.getCurrentUser() != null) {
            if (user.getFavouriteIds().contains(listing.getId())) {
                user.getFavouriteIds().remove(listing.getId());
            } else {
                user.addFavouriteId(listing.getId());
            }
            db.collection(USERS_DB_COLLECTION_NAME).document(user.getEmail()).update("favouriteIds", user.getFavouriteIds());
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton(R.string.SignIn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SignDialogFragment fragment = new SignDialogFragment();
                    fragment.show(getSupportFragmentManager(), "signDialog");
                }
            }).setMessage("You have to be signed in to have favourites");
        }
    }

    @Override
    public void favouriteClicked(Listing listing) {
        favourite(listing);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    @Override
    public void onEditListingClicked(final Listing newListing, final Listing oldListing, final ProgressDialog dialog) {
        db.collection(LISTINGS_DB_COLLECTION_NAME).document(oldListing.getId()).delete();
        user.getListingIds().remove(oldListing.getId());
        db.collection(LISTINGS_DB_COLLECTION_NAME).add(newListing).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                user.addLisitngId(documentReference.getId());
                newListing.setId(documentReference.getId());
                documentReference.update("id", documentReference.getId());
                db.collection(USERS_DB_COLLECTION_NAME).document(user.getEmail()).update("listingIds", user.getListingIds());
                getSupportFragmentManager().popBackStackImmediate();
                getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityLL, ViewListingFragment.newInstance(newListing, user), "viewListing").addToBackStack(null).commit();
                bottomNavigationView.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void addListingClicked(final Listing listing, final ProgressDialog dialog) {
        //listings.add(listing);
        //Listing[] listings1 = new Listing[listings.size()];
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ListingListFragment.newInstance(listings.toArray(listings1), user)).commit();
        db.collection(LISTINGS_DB_COLLECTION_NAME).add(listing).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                user.addLisitngId(documentReference.getId());
                listing.setId(documentReference.getId());
                documentReference.update("id", documentReference.getId());
                db.collection(USERS_DB_COLLECTION_NAME).document(user.getEmail()).update("listingIds", user.getListingIds());
                getSupportFragmentManager().popBackStackImmediate();
                getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityLL, ViewListingFragment.newInstance(listing, user), "viewListing").addToBackStack(null).commit();
                bottomNavigationView.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        addListingBtn.setVisibility(addListingBtn.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        bottomNavigationView.setVisibility(bottomNavigationView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        super.onBackPressed();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait, just getting some things ready for you");
        dialog.setCancelable(false);
        dialog.show();

        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        coordinatorLayout = findViewById(R.id.coordinatorMainLayout);

        bottomNavigationView = findViewById(R.id.bottomNavigationMenu);
        final User oUser = (User) getIntent().getSerializableExtra("oUser");
        if (oUser != null) {
            user = (User) getIntent().getSerializableExtra("user");
            notificationClicked(null, oUser);
        } else
            bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Listing[] listings1 = new Listing[listings.size()];
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ListingListFragment.newInstance(listings.toArray(listings1), user)).commit();
                        break;

                    case R.id.myArea:
                        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Just a sec, getting your info");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        if (user != null) {
                            finished = false;
                            final ArrayList<Listing> favourites = new ArrayList<>();
                            final ArrayList<Listing> userListings = new ArrayList<>();
                            if (user.getFavouriteIds().size() > 0) {
                                for (String s : user.getFavouriteIds()) {
                                    db.collection(LISTINGS_DB_COLLECTION_NAME).document(s).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            favourites.add(documentSnapshot.toObject(Listing.class));
                                            if (favourites.size() == user.getFavouriteIds().size()) {
                                                if (finished) {
                                                    Listing[] listings2 = new Listing[favourites.size()];
                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, MyAreaFragment.newInstance(favourites.toArray(listings2), userListings, user)).commit();
                                                    progressDialog.dismiss();
                                                } else
                                                    finished = true;
                                            }
                                        }
                                    });
                                }
                            } else {
                                if (finished) {
                                    Listing[] listings2 = new Listing[favourites.size()];
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, MyAreaFragment.newInstance(favourites.toArray(listings2), userListings, user)).commit();
                                    progressDialog.dismiss();
                                } else
                                    finished = true;
                            }
                            if (user.getListingIds().size() > 0) {
                                for (String s : user.getListingIds()) {
                                    db.collection(LISTINGS_DB_COLLECTION_NAME).document(s).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            userListings.add(documentSnapshot.toObject(Listing.class));
                                            if (userListings.size() == user.getListingIds().size()) {
                                                if (finished) {
                                                    Listing[] listings2 = new Listing[favourites.size()];
                                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, MyAreaFragment.newInstance(favourites.toArray(listings2), userListings, user)).commit();
                                                    progressDialog.dismiss();
                                                } else
                                                    finished = true;
                                            }
                                        }
                                    });
                                }
                            } else {
                                if (finished) {
                                    Listing[] listings2 = new Listing[favourites.size()];
                                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, MyAreaFragment.newInstance(favourites.toArray(listings2), userListings, user)).commit();
                                    progressDialog.dismiss();
                                } else
                                    finished = true;
                            }
                        } else {

                            progressDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("You must be signed in to access your area").setPositiveButton(getString(R.string.SignIn), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == DialogInterface.BUTTON_POSITIVE) {
                                        SignDialogFragment signDialogFragment = new SignDialogFragment();
                                        signDialogFragment.show(getSupportFragmentManager().beginTransaction(), "signDialog");
                                        bottomNavigationView.setSelectedItemId(R.id.home);
                                    }
                                }
                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    bottomNavigationView.setSelectedItemId(R.id.home);
                                }
                            }).show();

                        }
                        break;
                    case R.id.messages:
                        if (user != null) {
                            final ProgressDialog progressDialog1 = new ProgressDialog(MainActivity.this);
                            progressDialog1.setCancelable(false);
                            progressDialog1.setTitle("Just a sec");
                            progressDialog1.setMessage("Loading messages");
                            progressDialog1.show();

                            final ArrayList<User> usersConversed = new ArrayList<>();
                            if (user.getUsersConversedEmails() != null && !user.getUsersConversedEmails().isEmpty()) {
                                for (String email : user.getUsersConversedEmails()) {
                                    db.collection(USERS_DB_COLLECTION_NAME).document(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            usersConversed.add(documentSnapshot.toObject(User.class));
                                            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ConversationListFragment.newInstance(user, usersConversed)).commit();
                                            progressDialog1.dismiss();
                                        }
                                    });
                                }
                            } else {
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ConversationListFragment.newInstance(user, usersConversed)).commit();
                                progressDialog1.dismiss();
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("You must be signed in to access your messages").setPositiveButton(getString(R.string.SignIn), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == DialogInterface.BUTTON_POSITIVE) {
                                        SignDialogFragment signDialogFragment = new SignDialogFragment();
                                        signDialogFragment.show(getSupportFragmentManager().beginTransaction(), "signDialog");
                                        bottomNavigationView.setSelectedItemId(R.id.home);
                                    }
                                }
                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    bottomNavigationView.setSelectedItemId(R.id.home);
                                }
                            }).show();
                        }


                }
                return true;
            }
        });

        Toolbar toolbar = findViewById(R.id.mainActivityToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXT_REQUEST_CODE);
            }
        }


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    isComplete = false;
                    db.collection(USERS_DB_COLLECTION_NAME).document(firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            user = documentSnapshot.toObject(User.class);
                            getSupportActionBar().setTitle(user.getUsername());
                            firebaseInstanceId.getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    if (user.getDeviceTokens() == null) {
                                        user.setDeviceTokens(new ArrayList<String>());
                                    }
                                    if (!user.getDeviceTokens().contains(instanceIdResult.getToken())) {
                                        user.addDeviceToken(instanceIdResult.getToken());
                                        db.collection(USERS_DB_COLLECTION_NAME).document(user.getEmail()).update("deviceTokens", user.getDeviceTokens());
                                    }

                                }
                            });

                            if (user.getImageUrl() != null && !user.getImageUrl().equals("")) {
                                Glide.with(MainActivity.this).asBitmap().load(user.getImageUrl()).into(new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        circularUserImage = Utils.getCroppedBitmap(resource);
                                        invalidateOptionsMenu();
                                        if (isComplete) {
                                            dialog.dismiss();
                                        } else
                                            isComplete = true;
                                        /*dialog.dismiss();*/
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
                            } else {
                                if (isComplete) {
                                    dialog.dismiss();
                                } else
                                    isComplete = true;
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Log.d("getUser", "onFailure: " + e.getMessage());
                        }
                    });

                    Tasks.whenAllSuccess(db.collection(LISTINGS_DB_COLLECTION_NAME).whereGreaterThan("userEmail", firebaseAuth.getCurrentUser().getEmail()).get(), db.collection(LISTINGS_DB_COLLECTION_NAME).whereLessThan("userEmail", firebaseAuth.getCurrentUser().getEmail()).get()).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            listings = new ArrayList<>();
                            for (int i = 0; i < objects.size(); i++) {
                                QuerySnapshot querySnapshot = (QuerySnapshot) objects.get(i);
                                if (querySnapshot.size() > 0) {
                                    listings.addAll(querySnapshot.toObjects(Listing.class));
                                }
                            }
                            if (oUser == null)
                                bottomNavigationView.setSelectedItemId(R.id.home);
                            if (isComplete) {
                                dialog.dismiss();
                            } else isComplete = true;
                        }
                    });


                } else {
                    ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    if (!dialog.isShowing()) {
                        progressDialog.setMessage("Please wait, just getting some things ready for you");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                    db.collection(LISTINGS_DB_COLLECTION_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            listings = queryDocumentSnapshots.toObjects(Listing.class);
                            //  Listing[] listings1 = new Listing[listings.size()];
                            bottomNavigationView.setSelectedItemId(R.id.home);
                            //   getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, ListingListFragment.newInstance(listings.toArray(listings1), user)).commit();
                        }
                    });
                    if (user != null) {
                        firebaseInstanceId.getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                user.getDeviceTokens().remove(instanceIdResult.getToken());
                                db.collection(USERS_DB_COLLECTION_NAME).document(user.getEmail()).update("deviceTokens", user.getDeviceTokens());
                                user = null;
                            }
                        });
                    }
                    getSupportActionBar().setTitle(R.string.app_name);
                    circularUserImage = null;
                    invalidateOptionsMenu();
                    if (dialog.isShowing())
                        dialog.dismiss();
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }


            }
        };

        addListingBtn = findViewById(R.id.addListingFloatingBtn);
        addListingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("You have to be signed in to add a listing").setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                SignDialogFragment signDialogFragment = new SignDialogFragment();
                                signDialogFragment.show(getSupportFragmentManager().beginTransaction(), "signDialog");
                            }
                        }
                    }).show();
                }
            }
        });

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("messageList");
                final Message message = (Message) intent.getSerializableExtra("message");
                if (fragment != null && fragment.isVisible()) {
                    if (((User) (fragment.getArguments().getSerializable("oUser"))).getEmail().equals(message.getUserEmail())) {
                        listener.messageAdded((Message) intent.getSerializableExtra("message"));
                    }
                } else {
                    db.collection(USERS_DB_COLLECTION_NAME).document(message.getUserEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User oUser = documentSnapshot.toObject(User.class);
                            String channelId = null;
                            if (Build.VERSION.SDK_INT >= 26) {
                                channelId = "new_message_channel";
                                CharSequence channelName = "New Message";
                                int importance = NotificationManager.IMPORTANCE_HIGH;
                                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
                                notificationChannel.enableLights(true);
                                notificationChannel.enableVibration(true);
                                notificationChannel.setDescription("New messages received by traDeal");
                                notificationChannel.setLightColor(Color.CYAN);

                                manager.createNotificationChannel(notificationChannel);
                            }
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, channelId);
                            IconCompat icon;
                            if (oUser.getImageUrl() == null) {
                                icon = IconCompat.createWithResource(MainActivity.this, R.drawable.ic_person_black_24dp);
                            } else {
                                icon = IconCompat.createWithContentUri(oUser.getImageUrl());
                            }

                            Person person = new Person.Builder().setName(oUser.getUsername()).setIcon(icon).build();
                            NotificationCompat.MessagingStyle.Message message1 = new NotificationCompat.MessagingStyle.Message(message.getContent(), message.getDate().getTime(), person);
                            IconCompat icon1;
                            if (oUser.getImageUrl() == null) {
                                icon1 = IconCompat.createWithResource(MainActivity.this, R.drawable.ic_person_black_24dp);
                            } else {
                                icon1 = IconCompat.createWithContentUri(oUser.getImageUrl());
                            }
                            Person me = new Person.Builder().setName("you").setIcon(icon1).build();
                            builder.setSmallIcon(android.R.drawable.sym_def_app_icon).setStyle(new NotificationCompat.MessagingStyle(me).addMessage(message1));


                                /*builder.setSmallIcon(android.R.drawable.sym_def_app_icon).setGroup("messages")
                                        .setContentTitle(oUser.getUsername()).setContentText(message.getContent())
                                        .setPriority(NotificationCompat.PRIORITY_MAX);*/

                            Intent intent1 = new Intent("com.example.MESSAGE_CLICKED");
                            intent1.putExtra("oUser", oUser);
                            intent1.putExtra("user", user);
                            ArrayList<Message> lastMessages = new ArrayList<>();
                            lastMessages.add(message);
                            intent1.putExtra("lastMessages", lastMessages);
                            //intent1.setComponent(new ComponentName(getPackageName(), "com.example.tradeal.NotificationClickReceiver"));
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, oUser.getEmail().hashCode(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                            RemoteInput remoteInput = new RemoteInput.Builder(MessagingService.KEY_TEXT_REPLY).setLabel("Reply").build();
                            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_send_black_24dp, "Reply", pendingIntent).addRemoteInput(remoteInput).setAllowGeneratedReplies(true).build();
                            builder.addAction(action);
                            Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
                            intent2.putExtra("oUser", oUser);
                            intent2.putExtra("user", user);
                            PendingIntent pendingIntent1 = PendingIntent.getActivity(MainActivity.this.getApplicationContext(), oUser.getEmail().hashCode() / 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                            builder.setContentIntent(pendingIntent1);
                            manager.notify(oUser.getEmail() + user.getEmail(), oUser.getEmail().hashCode(), builder.build());
                        }
                    });
                }
            }
        };


        IntentFilter intentFilter = new IntentFilter("messageReceived");
        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(receiver, intentFilter);

    }

    interface mainActivityListener {
        void messageAdded(Message message);
    }

    public void setListener(mainActivityListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        User oUser = (User) intent.getSerializableExtra("oUser");
        if (oUser != null) {
            user = (User) intent.getSerializableExtra("user");
            notificationClicked(null, oUser);
        }

        setIntent(intent);
    }

    public void notificationClicked(final ProgressDialog dialog, final User oUser) {
        db.collection("messages").whereIn("oUserEmail", Arrays.asList(oUser.getEmail(), user.getEmail())).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final ArrayList<Message> messages = new ArrayList<>(queryDocumentSnapshots.toObjects(Message.class));
                ArrayList<Message> toRemove = new ArrayList<>();
                for (Message message : messages) {
                    if (!message.getUserEmail().equals(user.getEmail()) && !message.getUserEmail().equals(oUser.getEmail())) {
                        toRemove.add(message);
                    }
                }
                for (Message m : toRemove) {
                    messages.remove(m);
                }
                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });
                getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityLL, MessageListFragment.newInstance(messages, user, oUser)).addToBackStack(null).commit();
                bottomNavigationView.setVisibility(View.GONE);
                addListingBtn.setVisibility(View.VISIBLE);
                if (dialog != null)
                    dialog.dismiss();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_EXT_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("The application must have this permission to work").setPositiveButton("Give Permission", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXT_REQUEST_CODE);
                        }
                    }
                }).setNegativeButton("Close Tradeal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            Fragment addListingFragment = getSupportFragmentManager().findFragmentByTag("addListing");
            if (addListingFragment == null || !addListingFragment.isVisible()) {
                ArrayList<Bitmap> images = new ArrayList<>();
                images.add(image);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainActivityLL, AddListingFragment.newInstance(user, images), "addListing").addToBackStack(null).commit();
                bottomNavigationView.setVisibility(View.GONE);
                addListingBtn.setVisibility(View.GONE);
            }

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        if (circularUserImage != null) {
            menu.getItem(0).setIcon(new BitmapDrawable(getResources(), circularUserImage));
        }


        return true;
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sp = getSharedPreferences("mainActivity", MODE_PRIVATE);
        sp.edit().putBoolean("isActive", false).apply();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
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
