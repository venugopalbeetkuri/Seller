package com.example.sellerapp;

import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;

import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.R;
import com.example.db.EarnBO;
import com.example.db.RedeemBO;
import com.example.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference clientDatabase = database.child("client");

    MenuInflater menuInflater;
    private FirebaseAuth firebaseAuth;

    private static final int REQUEST_ENABLE_BT = 3;
    public static final int MESSAGE_READ = 2;

    Button earnButton = null;
    Button redeemButton = null;

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    //private BluetoothAdapter mAdapter = null;

    //ConnectedThread connectedThread = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

           /* AcceptThread serverBluetoothAdapter = new AcceptThread();
            serverBluetoothAdapter.start();*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        earnButton = (Button) findViewById(R.id.buttonEarn);

        // String userId = "Khaizar";
        // String points = "500";
        // String billAmount = "1000";
        // String discount = "250";
        // String storeId = "xyz";

        // EarnBO earn = new EarnBO(userId, storeId, billAmount, points);
        //MongoDB.getInstance();

        earnButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation));
                Intent i = new Intent(getApplicationContext(),EarnPoints.class);
                startActivity(i);
                // sendNotification(storeId, "Earn", userId, points, billAmount, discount);
            }

        });

        redeemButton = (Button) findViewById(R.id.buttonRedeem);

        redeemButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(getApplicationContext(),RedeemPoints.class);
                startActivity(i);
                arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation));
                // sendNotification(storeId, "Redeem", userId, points, billAmount, discount);
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        firebaseAuth.signOut();
        finish();
        /*Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);*/
        return super.onOptionsItemSelected(item);

    }


    private void sendNotification(String storeId, String type, String userId, String points, String billAmount, String discount) {

        // Create Notification Builder.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        // Setting Notification Properties.
        mBuilder.setSmallIcon(R.drawable.bell);

        // mBuilder.setContentTitle("Notification Alert, Click Me!");
        // mBuilder.setContentText("Hi, This is Android Notification Detail!");

        if("Earn".equalsIgnoreCase(type)) {

            mBuilder.setContentTitle(userId + " : " + "would like to earn points from your store.");
            mBuilder.setContentText("Earn " + points + " request. For bill amount: " + billAmount);
        } else if("Redeem".equalsIgnoreCase(type)) {

            mBuilder.setContentTitle(userId + " : " + "would like to redeem points from your store.");
            mBuilder.setContentText("Redeem " + points + " request. For bill amount: " + billAmount);
        }

        // Cancel the notification after its selected
        mBuilder.setAutoCancel(true);

        // Attach Actions.
        if ("Redeem".equalsIgnoreCase(type)) {

            Intent redeemIntent =  new Intent(this, RedeemPoints.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(RedeemPoints.class);
            redeemIntent.putExtra("points", points);
            redeemIntent.putExtra("billAmount", billAmount);
            redeemIntent.putExtra("discount", discount);
            redeemIntent.putExtra("userId", userId);
            redeemIntent.putExtra("storeId", storeId);

            // Adds the Intent that starts the Activity to the top of the stack.
            stackBuilder.addNextIntent(redeemIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

        } else if ("Earn".equalsIgnoreCase(type)) {

            Intent earnIntent =  new Intent(this, EarnPoints.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(EarnPoints.class);
            earnIntent.putExtra("points", points);
            earnIntent.putExtra("billAmount", billAmount);
            earnIntent.putExtra("userId", userId);
            earnIntent.putExtra("storeId", storeId);

            // Adds the Intent that starts the Activity to the top of the stack.
            stackBuilder.addNextIntent(earnIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

        }

        // Issue the notification.
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // NotificationID allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());

    }


}
