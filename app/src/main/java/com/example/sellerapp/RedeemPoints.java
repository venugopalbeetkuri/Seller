package com.example.sellerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.R;
import com.example.db.AcknowledgePoints;
import com.example.db.PointsBO;
import com.example.util.Utility;

import com.example.wifidirect.Service.DataTransferService;
import com.example.wifidirect.WifiDirectReceive;
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
    //SimpleDateFormat acceptdate = new SimpleDateFormat("dd/MM/yyy");
    final static String log = "Seller app";
    PointsBO points = null;
    String redeemString = null;

    String jsonACK = null;
    String remoteMacAddress = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redeem);

        Intent intent = getIntent();
        redeemString = intent.getStringExtra("earnRedeemString");

        remoteMacAddress = intent.getStringExtra("remoteAddress");

        Gson gson = new Gson();
        points = gson.fromJson(redeemString, PointsBO.class);

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
                sendAcknowledgement(false);
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

                // Send acknowledgement to customer.
                sendAcknowledgement(true);

                // Save to database.
                saveToDataBase();
                finish();
            }

        });
    }

    private void sendAcknowledgement(boolean success) {

        AcknowledgePoints ack = null;

        if(success) {

            ack = new AcknowledgePoints("success", redeemString);
        } else {

            ack = new AcknowledgePoints("failure", redeemString);
        }

        Gson gson = Utility.getGsonObject();
        jsonACK = gson.toJson(ack);
        sendMessage();

        // After sending acknowledgement move to first screen.
        Intent itt = new Intent(this, WifiDirectReceive.class);
        startActivity(itt);
    }

    Intent serviceIntent = null;

    private void sendMessage() {

        try {

            boolean instance = DataTransferService.isInstanceCreated();
            if(!instance) {
                serviceIntent = new Intent(this, DataTransferService.class);
            }

            // Send msg to seller.
            serviceIntent.setAction(DataTransferService.ACTION_SEND_DATA);
            serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_ADDRESS, remoteMacAddress);

            serviceIntent.putExtra(DataTransferService.MESSAGE, jsonACK);

            Log.i("bizzmark", "Customer Address: " + remoteMacAddress);
            serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_PORT, 9999);

            // Start service.
            startService(serviceIntent);

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void saveToDataBase() {

        try {

            String formattedDate = df.format(c.getTime());
            String storeName = points.getStoreName();

            DatabaseReference earnDatabase = clientDatabase.child(storeName);
            DatabaseReference time = earnDatabase.child(formattedDate);
            time.setValue(points);
            Utility.calculateTotal(storeName);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

}
