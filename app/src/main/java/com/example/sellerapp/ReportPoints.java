package com.example.sellerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.R;
import com.example.db.ReportBO;
import com.example.util.Utility;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class ReportPoints extends AppCompatActivity {

    TextView pointsGiven,totalsale,totaldiscount;
    String points,sale,disco;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference clientDatabase = database.child("client");

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Integer totalPoints = Utility.totalEarnPoints - Utility.totalRedeemPoints;

        pointsGiven = (TextView) findViewById(R.id.pointsgiven);
        totalsale = (TextView) findViewById(R.id.totalsale);
        totaldiscount=(TextView) findViewById(R.id.discountgiven);

        pointsGiven.setText(totalPoints.toString());

    }

}
