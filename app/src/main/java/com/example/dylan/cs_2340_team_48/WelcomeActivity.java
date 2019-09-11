package com.example.jake.cs_2340_team_48;

import android.content.Context;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * First screen displayed on app start
 */
public class WelcomeActivity extends AppCompatActivity {
    private Button loginButton1;
    private Button signUpButton1;
    private Button shelters;
    public static DatabaseReference mReference;
    public static ArrayList<String> keys = new ArrayList<>();
    public static ArrayList<String> values = new ArrayList<>();
    public static boolean loggedIn = false;
    public static String username;
    public static ArrayList<String> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        mReference = FirebaseDatabase.getInstance().getReference();
        //mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_welcome);
        this.loginButton1 = (Button) this.findViewById(R.id.loginButton);
        this.signUpButton1 = (Button) this.findViewById(R.id.signUpButton);
        this.loginButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent goToLogInScreen = new Intent(context, LoginActivity.class);
                startActivity(goToLogInScreen);
                finish();
            }
        });
        this.signUpButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent goToLogInScreen = new Intent(context, SignUpActivity.class);
                startActivity(goToLogInScreen);
                finish();
            }
        });

        this.mReference.addChildEventListener(new ChildEventListener() {

            String value = "";

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    if (child.getValue().equals("Terminate")) {
                        break;
                    } else if (keys.contains(child.getKey())) {
                        continue;
                    }

                    //Parse String here!
                    value = child.getValue().toString();
                    value = value.replace("{", "");
                    value = value.replace("}", "");
                    value = value.replaceAll("Address", "Address\n");
                    value = value.replaceAll(", Beds", "\n\nBeds\n");
                    value = value.replaceAll(", Capacity", "\n\nCapacity\n");
                    value = value.replaceAll(", Latitude", "\n\nLatitude\n");
                    value = value.replaceAll(", Longitude", "\n\nLongitude\n");
                    value = value.replaceAll(", Phone Number", "\n\nPhone Number\n");
                    value = value.replaceAll(", Restrictions", "\n\nRestrictions\n");
                    value = value.replaceAll(", Special Note", "\n\nSpecial Note\n");
                    value = value.replaceAll(", Unique Key", "\n\nUnique Key");

                    WelcomeActivity.keys.add(child.getKey());
                    WelcomeActivity.values.add(value);

                    Object location = child.child("Address").getValue();
                    if (location != null) {
                        WelcomeActivity.locations.add(location.toString());
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        this.shelters = (Button) findViewById(R.id.shelter_button);
        this.shelters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent goToSheltersScreen = new Intent(context, FindShelters.class);
                startActivity(goToSheltersScreen);
                finish();
            }
        });
    }
}
