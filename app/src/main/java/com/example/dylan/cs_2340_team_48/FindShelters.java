package com.example.jake.cs_2340_team_48;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import java.util.ArrayList;
import android.util.Log;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.app.Dialog;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Created by ericreese on 3/5/18.
 */

/**
 * Displays a list with a search bar to find shelters
 */
public class FindShelters extends AppCompatActivity {

    private ListView listView;
    private Button back;
    private EditText search;
    private Button explaination;
    private ArrayAdapter<String> adapter;
    public static int beds = 0;
    public static String name = "";
    public static String value = "";
    private static ArrayList<String> currentKeys = new ArrayList<>();
    private static ArrayList<String> currentValues = new ArrayList<>();

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.find_shelters);

        beds = 0;

        for (int i = 0; i < WelcomeActivity.keys.size(); i++) {
            if (currentKeys.contains(WelcomeActivity.keys.get(i))) {
                continue;
            }
            currentKeys.add(WelcomeActivity.keys.get(i));
            currentValues.add(WelcomeActivity.values.get(i));
        }

        //Originally was WelcomeActivity.keys if not done correctly, revert to this
        this.adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                currentKeys);

        this.listView = findViewById(R.id.list_view);
        this.listView.setAdapter(adapter);
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                //Get values string
                name = currentKeys.get(position);
                MainActivity.name = name;
                value = currentValues.get(position);

                currentKeys.clear();
                currentValues.clear();
                Context context = view.getContext();
                Intent goToPopUpScreen = new Intent(context, Pop.class);
                startActivity(goToPopUpScreen);
                finish();
            }
        });
        adapter.notifyDataSetChanged();
        this.back = (Button) findViewById(R.id.button2);
        this.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WelcomeActivity.loggedIn) {
                    Context context = v.getContext();
                    Intent goToMainScreen = new Intent(context, MainActivity.class);
                    startActivity(goToMainScreen);
                    finish();
                } else {
                    Context context = v.getContext();
                    Intent goToWelcomeScreen = new Intent(context, WelcomeActivity.class);
                    startActivity(goToWelcomeScreen);
                    finish();
                }
            }
        });

        this.search = (EditText) findViewById(R.id.search);
        this.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                currentKeys.clear();
                currentValues.clear();

                for (int index = 0; index < WelcomeActivity.keys.size(); index++) {
                    String sequence = charSequence.toString().toLowerCase();
                    if (sequence.equals("men") || sequence.equals("women")) {
                        if (WelcomeActivity.values.get(index).toLowerCase().contains("/" + sequence)) {
                            currentKeys.add(WelcomeActivity.keys.get(index));
                            currentValues.add(WelcomeActivity.values.get(index));
                        } else if (WelcomeActivity.values.get(index).toLowerCase().contains("=" + sequence)) {
                            currentKeys.add(WelcomeActivity.keys.get(index));
                            currentValues.add(WelcomeActivity.values.get(index));
                        }
                        continue;
                    }
                    if (WelcomeActivity.values.get(index).toLowerCase().contains(sequence)) {
                        currentKeys.add(WelcomeActivity.keys.get(index));
                        currentValues.add(WelcomeActivity.values.get(index));
                    } else if (WelcomeActivity.keys.get(index).toLowerCase().contains(sequence)) {
                        currentKeys.add(WelcomeActivity.keys.get(index));
                        currentValues.add(WelcomeActivity.values.get(index));
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        this.explaination = (Button) findViewById(R.id.search_explained);
        this.explaination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentKeys.clear();
                currentValues.clear();
                Context context = v.getContext();
                Intent goToExplaination = new Intent(context, Explaination.class);
                startActivity(goToExplaination);
                finish();
            }
        });
    }
}
