package com.example.jake.cs_2340_team_48;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.database.Cursor;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.content.Context;
import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import android.widget.Toast;

/**
 * Created by ericreese on 3/5/18.
 */

/**
 * Adds shelters to the database
 */
public class AddShelters extends AppCompatActivity {

    private EditText mUniqueKey;
    private EditText mName;
    private EditText mCapacity;
    private EditText mLongitude;
    private EditText mLatitude;
    private EditText mRestrictions;
    private EditText mAddress;
    private EditText mNotes;
    private EditText mPhoneNumber;
    private EditText mBeds;
    private Button mAddShelter;
    private ScrollView mScrollView;
    private Context context;
    private Button cancel;
    private ProgressBar mProgressView;
    private View mLayoutView;
    final private String[] invalidCharacters = {"@", "#", "*", ".", "$", "~", "`", "<", ">"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_shelter_data);
        this.mUniqueKey = (EditText) findViewById(R.id.Shelter_Unique_Key);
        this.mUniqueKey.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        this.mCapacity = (EditText) findViewById(R.id.Shelter_Capacity);
        this.mCapacity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        this.mLongitude = (EditText) findViewById(R.id.Shelter_Longitude);
        this.mLongitude.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        this.mLatitude = (EditText) findViewById(R.id.Shelter_Latitude);
        this.mLatitude.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        this.mRestrictions = (EditText) findViewById(R.id.Shelter_Restrictions);
        this.mRestrictions.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        this.mAddress = (EditText) findViewById(R.id.Shelter_Address);
        this.mAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        this.mNotes = (EditText) findViewById(R.id.Shelter_Notes);
        this.mNotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        this.mPhoneNumber = (EditText) findViewById(R.id.Shelter_Phone_Number);
        this.mPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        this.mBeds = (EditText) findViewById(R.id.number_of_beds);
        this.mBeds.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        this.mName = (EditText) findViewById(R.id.Shelter_Name) ;
        this.mName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        this.mScrollView = (ScrollView) findViewById(R.id.scroll);
        this.cancel = (Button) findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent goToMainScreen = new Intent(context, MainActivity.class);
                startActivity(goToMainScreen);
                finish();
            }
        });
        this.mAddShelter = (Button) findViewById(R.id.add_shelters);
        mAddShelter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptAddItems();
            }
        });
        this.mProgressView = (ProgressBar) findViewById(R.id.login_progress);
        this.mLayoutView = findViewById(R.id.Scroll_Layout);
    }

    /**
     * Attempts to add items to firebase
     * Shows error message if name is empty, or name already exists
     */
    private void attemptAddItems() {
        final String name = mName.getText().toString();

        boolean cancel = false;
        final View[] focusView = {null};

        if (!isValid(name)) {
            mName.setError("This name is invalid");
            focusView[0] = mName;
            cancel = true;
        }

        String beds = mBeds.getText().toString();
        for (int i = 0; i < 10; i++) {
            beds = beds.replace(Integer.toString(i), "");
        }
        if (!beds.isEmpty()) {
            mBeds.setError("Must be an integer value");
            focusView[0] = mBeds;
            cancel = true;
        }

        if (cancel) {
            showProgress(false);
            focusView[0].requestFocus();
        } else {
            showProgress(true);
            focusView[0] = mLayoutView;

            //Begin firebase connection
            WelcomeActivity.mReference.child("Shelters").addValueEventListener(new ValueEventListener() {
                //THIS GETS CALLED WHEN ANYONE ENTERS VALUES!!!
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(name)) {

                        WelcomeActivity.locations.add(mAddress.getText().toString());
                        WelcomeActivity.mReference.child("Shelters").child(name);
                        WelcomeActivity.mReference.child("Shelters").child(name).child("Address").setValue(mAddress.getText().toString());
                        WelcomeActivity.mReference.child("Shelters").child(name).child("Capacity").setValue(mCapacity.getText().toString());
                        WelcomeActivity.mReference.child("Shelters").child(name).child("Latitude").setValue(mLatitude.getText().toString());
                        WelcomeActivity.mReference.child("Shelters").child(name).child("Longitude").setValue(mLongitude.getText().toString());
                        WelcomeActivity.mReference.child("Shelters").child(name).child("Phone Number").setValue(mPhoneNumber.getText().toString());
                        WelcomeActivity.mReference.child("Shelters").child(name).child("Restrictions").setValue(mRestrictions.getText().toString());
                        WelcomeActivity.mReference.child("Shelters").child(name).child("Special Note").setValue(mNotes.getText().toString());
                        WelcomeActivity.mReference.child("Shelters").child(name).child("Unique Key").setValue(mUniqueKey.getText().toString());
                        WelcomeActivity.mReference.child("Shelters").child(name).child("Beds").setValue(mBeds.getText().toString());

                        WelcomeActivity.keys.add(name);

                        //Account created and signing in
                        context = mName.getContext();
                        Intent goToMainScreen = new Intent(context, MainActivity.class);
                        startActivity(goToMainScreen);
                        Toast toast = Toast.makeText(context, "Shelter successfully saved to database, login again to view", Toast.LENGTH_LONG);
                        toast.show();
                        finish();
                    } else {
                        showProgress(false);
                        focusView[0].requestFocus();
                        mName.setError("Shelter already exists. Permission to edit " + name + " denied.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    showProgress(false);
                    focusView[0].requestFocus();
                }

            });
        }
    }

    /**
     * This will prevent firebase name errors
     * @param name The string to be determined valid
     * @return If it is valid
     */
    private boolean isValid(String name) {
        if (name.isEmpty()) {
            return false;
        } else if (name.length() < 2) {
            return false;
        }
        for (String s: invalidCharacters) {
            if (name.contains(s)) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLayoutView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLayoutView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLayoutView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLayoutView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Hides the keyboard when user clicks outside of edittext
     * @param view The current view
     */
    public void hideKeyboard(View view) {
        InputMethodManager inputManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
