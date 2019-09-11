package com.example.jake.cs_2340_team_48;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Main activity screen, links whole app together
 */
public class MainActivity extends AppCompatActivity {

    public static boolean activeMap = false;
    public static int numberOfBeds = 0;
    public static String name = "Name";
    private Button logoutButton1;
    private Button cancelButton1;
    private Button add_shelters;
    private Button find_shelters;
    private Button cancel;
    private TextView shelterName;
    private TextView numberOfOccupants;
    public static int obtainedBeds = -1;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private Button map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.activeMap = false;

        if (isServicesOkay()) {
            init();
        }

        this.logoutButton1 = (Button) this.findViewById(R.id.logoutButton);
        this.cancelButton1 = (Button) this.findViewById(R.id.cancelButton);
/*
        if (WelcomeActivity.keys.size() != WelcomeActivity.values.size() || WelcomeActivity.locations.size() != WelcomeActivity.keys.size()) {
            WelcomeActivity.username = "";
            Context context = cancelButton1.getContext();
            Intent goToWelcomeScreen = new Intent(context, WelcomeActivity.class);
            WelcomeActivity.loggedIn = false;
            startActivity(goToWelcomeScreen);
            finish();
        }*/

        this.add_shelters = (Button) this.findViewById(R.id.add_shelters);
        this.logoutButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // WelcomeActivity.mAuth.signOut();
                WelcomeActivity.username = "";
                Context context = v.getContext();
                Intent goToWelcomeScreen = new Intent(context, WelcomeActivity.class);
                WelcomeActivity.loggedIn = false;
                startActivity(goToWelcomeScreen);
                finish();
            }
        });
        this.cancelButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // WelcomeActivity.mAuth.signOut();
                WelcomeActivity.username = "";
                Context context = v.getContext();
                Intent goToWelcomeScreen = new Intent(context, WelcomeActivity.class);
                WelcomeActivity.loggedIn = false;
                startActivity(goToWelcomeScreen);
                finish();
            }
        });
        this.add_shelters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent goToAddShelters = new Intent(context, AddShelters.class);
                startActivity(goToAddShelters);
                finish();
            }
        });
        this.find_shelters = (Button) findViewById(R.id.find_shelters);
        this.find_shelters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent goToFindShelters = new Intent(context, FindShelters.class);
                startActivity(goToFindShelters);
                finish();
            }
        });
        this.cancel = (Button) findViewById(R.id.cancel_registration);
        this.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WelcomeActivity.mReference.child("Shelters").child(name) != null) {
                    if (obtainedBeds != -1) {
                        WelcomeActivity.mReference.child("Shelters").child(name).child("Beds").setValue(obtainedBeds + numberOfBeds);
                        numberOfBeds = 0;
                        shelterName.setText(name);
                        numberOfOccupants.setText("Number of Occupants");
                        WelcomeActivity.mReference.child("users").child(WelcomeActivity.username).child("Reserved").setValue("0");
                        WelcomeActivity.mReference.child("users").child(WelcomeActivity.username).child("Shelter Name").setValue("Name");
                    } else {
                        Log.d("TAG", "Bed count is -1");
                    }
                }
            }
        });

        this.shelterName = (TextView) findViewById(R.id.shelter_name);
        this.numberOfOccupants = (TextView) findViewById(R.id.number_of_occupants);
        if (numberOfBeds != 0) {
            shelterName.setText(name);
            numberOfOccupants.setText(Integer.toString(numberOfBeds));
        }

        WelcomeActivity.mReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Object beds = dataSnapshot.child(WelcomeActivity.username).child("Reserved").getValue();
                Object name = dataSnapshot.child(WelcomeActivity.username).child("Shelter Name").getValue();
                if (beds != null) {
                    MainActivity.numberOfBeds = Integer.parseInt(beds.toString());
                }
                if (name != null) {
                    MainActivity.name = name.toString();
                }
                if (numberOfBeds != 0) {
                    shelterName.setText(MainActivity.name);
                    numberOfOccupants.setText(Integer.toString(numberOfBeds));
                }
                Object bedThing = dataSnapshot.child(MainActivity.name).child("Beds").getValue();
                if (bedThing != null) {
                    obtainedBeds = Integer.parseInt(bedThing.toString());
                    Log.d("TAG", "Working");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Notifies user if services is not up to date
     * @return If services API works on current device
     */
    private boolean isServicesOkay() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You cannot make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Initializes the map
     */
    private void init() {
        this.map = (Button) findViewById(R.id.map_of_shelters);
        this.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }
}
