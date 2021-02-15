package com.example.redbus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class BookTicketActivity extends AppCompatActivity {


    //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
    private DatabaseReference mDatabase;
    private String TAG = "BookTicketAcitivity";
    static ArrayList<Integer> bookedSeats = new ArrayList<Integer>();
    String uid;
    ArrayList<Integer> currentSelection = new ArrayList<Integer>();
// ...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ticket);
        mDatabase = FirebaseDatabase.getInstance().getReference("MDU-SAL");
        Intent intent = getIntent();

         uid = intent.getExtras().getString("userId");
        Button[] seats = new Button[40];
        LinearLayout l1 = findViewById(R.id.linear);
        TableLayout tableLayout = new TableLayout(this);
        tableLayout.setStretchAllColumns(true);
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT));

        for(int i = 0; i < 10; i++){
            TableRow tableRow = new TableRow(this);
            tableRow.setGravity(Gravity.CENTER);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT, 1.0f));
            for(int j = 0; j < 4; j++){
                seats[4 * i + j] = new Button(this);
                seats[4 * i + j].setText(Integer.toString(4 * i + j + 1));
                int finalI = 4 * i + j;
                seats[4 * i + j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        seats[finalI].setBackgroundColor(Color.parseColor("#FF0000"));
                        currentSelection.add(finalI);
                    }
                });
                seats[4 * i + j].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT));

                tableRow.addView(seats[4 * i + j]);
            }
            tableLayout.addView(tableRow);
        }
        l1.addView(tableLayout);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot postSnapshot:dataSnapshot.getChildren()) {
                        booked seats = postSnapshot.getValue(booked.class);
                        if(postSnapshot.getKey().equals(uid)){
                            for(Integer i:seats.pos){
                                currentSelection.add(i);
                            }
                        }
                        if(seats.pos != null){
                            for(Integer i:seats.pos) {
                                bookedSeats.add(i);
                            }
                        }
                    }
                        for(Integer i : bookedSeats){
                            Log.d(TAG, "onDataChange" + Integer.toString(i));
                            seats[i].setBackgroundColor(Color.parseColor("#FF0000"));
                            seats[i].setEnabled(false);
                            Log.d(TAG, "onDataChange" + Integer.toString(i));
                        }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "Failed to read booked tickets", databaseError.toException());
            }
            });
    }

    public void SuccessActivity(View view){
        mDatabase.child(uid).setValue(new booked(currentSelection));
        Intent intent2 = new Intent(this, SuccessActivity.class);
        startActivity(intent2);
    }
}