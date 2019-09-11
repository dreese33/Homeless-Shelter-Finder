package com.example.jake.cs_2340_team_48;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by ericreese on 3/27/18.
 */

/**
 * Allows users to reserve beds
 */
public class ReserveBeds extends AppCompatActivity {

    private Button back;
    private Button reserve;
    private EditText currentNumberOfBeds;
    private EditText bedsToReserve;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.reserve_beds);

        this.currentNumberOfBeds = (EditText) findViewById(R.id.current_beds);
        this.currentNumberOfBeds.setText("Current Number of Beds: " + FindShelters.beds);

        this.back = (Button) findViewById(R.id.back);
        this.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindShelters.beds = 0;
                Context context = v.getContext();
                Intent goToPopScreen = new Intent(context, Pop.class);
                startActivity(goToPopScreen);
                finish();
            }
        });

        this.reserve = (Button) findViewById(R.id.reserve);
        this.reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Decrement database by new number of beds
                //Throw error if user has already registered a bed (or beds), or registers too many beds
                reserveBeds();
            }
        });

        this.bedsToReserve = (EditText) findViewById(R.id.editText);
    }

    /**
     * Reserves beds for those who register
     */
    private void reserveBeds() {
        final View[] focusView = {null};
        boolean cancel = false;

        String numberReserve = bedsToReserve.getText().toString();
        if (numberReserve.isEmpty() || numberReserve.equals("")) {
            bedsToReserve.setError("Field Required");
            focusView[0] = bedsToReserve;
            cancel = true;
        }

        for (int i = 0; i < 10; i++) {
            numberReserve = numberReserve.replace(Integer.toString(i), "");
        }
        if (!numberReserve.isEmpty()) {
            bedsToReserve.setError("Must be an integer value");
            focusView[0] = bedsToReserve;
            cancel = true;
        }

        if (cancel) {
            focusView[0].requestFocus();
        } else {
            final int bedsToGet = Integer.parseInt(bedsToReserve.getText().toString());
            if (FindShelters.beds < bedsToGet) {
                bedsToReserve.setError("Not enough beds available");
                focusView[0] = bedsToReserve;
                cancel = true;
            }
            if (!cancel) {
                WelcomeActivity.mReference.child("Shelters").child(FindShelters.name).child("Beds").setValue(Integer.toString(FindShelters.beds - bedsToGet));
                WelcomeActivity.mReference.child("users").child(WelcomeActivity.username).child("Reserved").setValue(bedsToGet);
                WelcomeActivity.mReference.child("users").child(WelcomeActivity.username).child("Shelter Name").setValue(MainActivity.name);
                MainActivity.numberOfBeds = bedsToGet;
                FindShelters.beds = 0;
                FindShelters.name = "";
                Context context = bedsToReserve.getContext();
                Intent goToMainScreen = new Intent(context, MainActivity.class);
                startActivity(goToMainScreen);
                Toast toast = Toast.makeText(context, "Room(s) Reserved", Toast.LENGTH_LONG);
                toast.show();
                finish();
            } else {
                focusView[0].requestFocus();
            }
        }
    }
}
