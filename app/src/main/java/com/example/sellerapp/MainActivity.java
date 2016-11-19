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


}
