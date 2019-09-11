package com.example.jake.cs_2340_team_48;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by ericreese on 3/13/18.
 */

/**
 * Displays a textview describing how to use this app
 */
public class Explaination extends AppCompatActivity {

    private Button done;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.explaination);

        this.done = (Button) findViewById(R.id.done);
        this.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Context context = v.getContext();
                    Intent goBack = new Intent(context, FindShelters.class);
                    startActivity(goBack);
                    finish();
            }
        });
    }
}
