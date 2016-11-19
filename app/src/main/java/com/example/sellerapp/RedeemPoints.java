package com.example.sellerapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.R;
import com.example.db.EarnBO;
import com.example.db.MongoDB;
import com.example.db.PointsBO;
import com.example.db.RedeemBO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;


public class RedeemPoints extends AppCompatActivity {

    String storeName="BizzMark";
    TextView redeemPoints,billAmount,discountAmount;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference clientDatabase = database.child("client");
    DatabaseReference earnDatabase =clientDatabase.child(storeName);

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    String formattedDate = df.format(c.getTime());

    final static String log = "Seller app";
    PointsBO points = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        Intent intent = getIntent();
        String earnString = intent.getStringExtra("earnRedeemString");

        Gson gson = new Gson();
        points = gson.fromJson(earnString, PointsBO.class);


        billAmount = (TextView) findViewById(R.id.billAmount);
        redeemPoints = (TextView) findViewById(R.id.redeemPoints);
        discountAmount = (TextView) findViewById(R.id.discountAmount);

        billAmount.setText(points.getBillAmount());
        redeemPoints.setText(points.getPoints());
        discountAmount.setText(points.getPoints());

        addListenerOnAcceptButton();
        addListenerOnCancelButton();

    }
    public void addListenerOnCancelButton() {

        Button redeemCancelButton = (Button) findViewById(R.id.redeemCancelButton);

        redeemCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation));

                Intent itt = new Intent(RedeemPoints.this, ReportPoints.class);
                startActivity(itt);
            }

        });

    }

    public void addListenerOnAcceptButton() {

        Button earnButton = (Button) findViewById(R.id.redeemButton);

        earnButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation));

                DatabaseReference time = earnDatabase.child(formattedDate);
                time.setValue(points);
                Intent itt = new Intent(RedeemPoints.this, ReportPoints.class);
                startActivity(itt);
            }

        });
    }

}
