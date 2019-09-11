package com.example.jake.cs_2340_team_48;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by ericreese on 3/5/18.
 */

/**
 * Popup screen to be displayed on MapActivity and FindShelters
 */
public class Pop extends AppCompatActivity {

    private Button exit;
    private TextView textView;
    private EditText editText;
    private Button reserve;
    private String bedsString;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.pop_up);

        this.exit = (Button) findViewById(R.id.pop_up_button);
        this.exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if (!MainActivity.activeMap) {
                    MainActivity.name = "Name";
                    Intent goToFindScreen = new Intent(context, FindShelters.class);
                    startActivity(goToFindScreen);
                    finish();
                } else {
                    Intent goToMapScreen = new Intent(context, MapActivity.class);
                    startActivity(goToMapScreen);
                    finish();
                }
            }
        });

        this.textView = findViewById(R.id.pop_up_text_view);
        this.textView.setMovementMethod(new ScrollingMovementMethod());
        this.editText = (EditText) findViewById(R.id.editText);
        if (!MainActivity.activeMap) {
            textView.setText(FindShelters.value);
            editText.setText(FindShelters.name);
        } else {
            textView.setText(MapActivity.value);
            editText.setText(MapActivity.name);
        }
        this.reserve = (Button) findViewById(R.id.reserve);
        if (!WelcomeActivity.loggedIn || MainActivity.activeMap) {
            reserve.setVisibility(View.INVISIBLE);
        }
        this.reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.numberOfBeds == 0) {
                    Context context = v.getContext();
                    Intent goToReserveBedsScreen = new Intent(context, ReserveBeds.class);
                    startActivity(goToReserveBedsScreen);
                    finish();
                } else {
                    reserve.setError("Already registered for room, cancel current registration to register for others");
                    View focusView = reserve;
                    focusView.requestFocus();
                }
            }
        });

        WelcomeActivity.mReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Object bedThing = dataSnapshot.child(MainActivity.name).child("Beds").getValue();
                if (bedThing != null) {
                    bedsString = bedThing.toString();
                    FindShelters.beds = Integer.parseInt(bedsString);
                    MainActivity.obtainedBeds = FindShelters.beds;
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
    }
}
