package com.example.sellerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.R;
import com.example.db.PointsBO;
import com.example.util.Utility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

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

                calculateTotal(storeName);


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

    private void calculateTotal(String storeName) {

        try {

            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference clientDatabase = database.child("client");
            Query query  = clientDatabase.child(storeName);

            // Query query = storeDatabase.orderByChild("Earn");
            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot timeStampSnapShot : dataSnapshot.getChildren()) {

                        HashMap<String, String> timeStampKey = (HashMap)timeStampSnapShot.getValue();
                        String type = timeStampKey.get("type");
                        String pointsStr = timeStampKey.get("points");

                        if("Earn".equalsIgnoreCase(type)) {

                            int points = Integer.parseInt(pointsStr);
                            Utility.totalEarnPoints = Utility.totalEarnPoints + points;
                        } else if("Redeem".equalsIgnoreCase(type)) {

                            int points = Integer.parseInt(pointsStr);
                            Utility.totalRedeemPoints = Utility.totalRedeemPoints + points;
                        }
                    }

                    Toast.makeText(getApplicationContext(), "Total Earn: " + Utility.totalEarnPoints + " Total Redeem: " + Utility.totalRedeemPoints, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(EarnPoints.this, ReportPoints.class);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

}
