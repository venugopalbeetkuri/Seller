package com.example.sellerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import com.example.R;
import com.example.db.PointsBO;
import com.example.db.StoreBO;
import com.example.login.LoginActivity;
import com.example.util.Utility;
import com.example.wifidirect.WifiDirectReceive;
import com.google.firebase.auth.FirebaseAuth;
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

public class EarnPoints extends Activity {

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference storeDatabase = database.child("store");
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

        String storeName = points.getStoreName();

        updateStoreName(storeName);

        earnPoints = (TextView) findViewById(R.id.earnPoints);
        billAmount = (TextView) findViewById(R.id.billamount);

        earnPoints.setText(points.getPoints());
        billAmount.setText(points.getBillAmount());

        addListenerOnAcceptButton();
        addListenerOnCancelButton();
    }

    private void updateStoreName(final String storeName){
        try {

            // Getting firebase auth object.
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            final String storeEmail = firebaseAuth.getCurrentUser().getEmail();

            // if the objects getcurrentuser method is not null
            //means user is already logged in
            if (firebaseAuth.getCurrentUser() != null) {

                Query query  = database.child("store");

                // Query query = storeDatabase.orderByChild("Earn");
                query.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        boolean found = false;
                        for (DataSnapshot timeStampSnapShot : dataSnapshot.getChildren()) {


                            HashMap<String, String> timeStampKey = (HashMap)timeStampSnapShot.getValue();

                            String storeNameDB = timeStampKey.get("storeName");
                            if (storeNameDB.equalsIgnoreCase(storeName)) {
                                found = true;
                            }

                        }

                        if(!found) {
                            StoreBO store = new StoreBO(storeEmail, storeName);
                            String formattedDate = df.format(c.getTime());
                            DatabaseReference time = storeDatabase.child(formattedDate);
                            time.setValue(store);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {

                //close this activity
                finish();
                //opening profile activity
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
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
                Utility.calculateTotal(storeName);
                finish();
            }

        });
    }
    public void addListenerOnCancelButton() {

        Button earnCancelButton = (Button) findViewById(R.id.earnCancelButton);
        earnCancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation));
                /*Intent itt = new Intent(EarnPoints.this, ReportPoints.class);
                startActivity(itt);*/
                finish();
            }

        });
    }

}
