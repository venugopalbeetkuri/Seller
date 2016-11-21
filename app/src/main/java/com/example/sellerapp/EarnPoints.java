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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EarnPoints extends AppCompatActivity {

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference clientDatabase = database.child("client");

    TextView earnPoints,billAmount;
    final static String log = "Seller app";

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    PointsBO points = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn);

        Intent intent = getIntent();
        String earnString = intent.getStringExtra("earnRedeemString");

        Gson gson = new Gson();
        points = gson.fromJson(earnString, PointsBO.class);

        earnPoints = (TextView) findViewById(R.id.earnPoints);
        billAmount = (TextView) findViewById(R.id.billamount);

        earnPoints.setText(points.getPoints());
        billAmount.setText(points.getBillAmount());

        addListenerOnAcceptButton();
        addListenerOnCancelButton();
    }

    public void addListenerOnAcceptButton() {

        Button earnAcceptButton = (Button) findViewById(R.id.earnAcceptButton);
        earnAcceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation));

                String formattedDate = df.format(c.getTime());

                String storeName = points.getStoreName();
                DatabaseReference earnDatabase = clientDatabase.child(storeName);
                DatabaseReference time = earnDatabase.child(formattedDate);
                time.setValue(points);

                Intent intent = new Intent(EarnPoints.this, ReportPoints.class);
                startActivity(intent);
            }

        });
    }
    public void addListenerOnCancelButton() {

        Button earnCancelButton = (Button) findViewById(R.id.earnCancelButton);
        earnCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation));
                Intent itt = new Intent(EarnPoints.this, ReportPoints.class);
                startActivity(itt);
            }

        });
    }

}
