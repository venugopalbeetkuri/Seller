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

                calculateTotal(storeName);
                finish();
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
                        String billAmountStr = timeStampKey.get("billAmount");
                        String discountAmountStr = timeStampKey.get("disCountAmount");

                        int billAmount = Integer.parseInt(billAmountStr);
                        Utility.totalBillAmount = Utility.totalBillAmount + billAmount;

                        if(null == discountAmountStr){
                            discountAmountStr = "0";
                        }
                        int discountAmount = Integer.parseInt(discountAmountStr);
                        Utility.totalDiscount = Utility.totalDiscount + discountAmount;

                        if("Earn".equalsIgnoreCase(type)) {

                            int points = Integer.parseInt(pointsStr);
                            Utility.totalEarnPoints = Utility.totalEarnPoints + points;
                        } else if("Redeem".equalsIgnoreCase(type)) {

                            int points = Integer.parseInt(pointsStr);
                            Utility.totalRedeemPoints = Utility.totalRedeemPoints + points;
                        }
                    }

                    Toast.makeText(getApplicationContext(), "Total Earn: " + Utility.totalEarnPoints + " Total Redeem: " + Utility.totalRedeemPoints, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RedeemPoints.this, ReportPoints.class);
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
