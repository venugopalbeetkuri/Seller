package com.example.sellerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;


import com.example.R;
import com.example.db.PointsBO;
import com.example.util.Utility;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RedeemPoints extends AppCompatActivity {

    TextView redeemPoints,billAmount,discountAmount;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference clientDatabase = database.child("client");

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
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
                /*Intent itt = new Intent(RedeemPoints.this, ReportPoints.class);
                startActivity(itt);*/
                finish();
            }

        });

    }

    public void addListenerOnAcceptButton() {

        Button earnButton = (Button) findViewById(R.id.redeemButton);

        earnButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation));

                String formattedDate = df.format(c.getTime());
                String storeName = points.getStoreName();
                DatabaseReference pointsDB = clientDatabase.child(storeName);
                DatabaseReference time = pointsDB.child(formattedDate);
                time.setValue(points);

                Utility.calculateTotal(storeName);
                finish();
            }

        });
    }

}
