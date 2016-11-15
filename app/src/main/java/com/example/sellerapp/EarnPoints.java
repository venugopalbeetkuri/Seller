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
import android.widget.Toast;

import com.example.R;
import com.example.db.EarnBO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.net.InetAddress;
import java.util.Map;

public class EarnPoints extends AppCompatActivity {


    String userId,storeId,points, bill;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference clientDatabase = database.child("client");
    TextView redeemPoints,billAmount;
    final static String log = "Seller app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earn);

        Intent intent = getIntent();
        redeemPoints = (TextView) findViewById(R.id.earnPoints);
        billAmount = (TextView) findViewById(R.id.billamount);

        points = intent.getStringExtra("points");
        bill = intent.getStringExtra("billAmount");
        userId = intent.getStringExtra("userId");
        storeId = intent.getStringExtra("storeId");


        redeemPoints.setText("40");
        billAmount.setText("30");

        addListenerOnAcceptButton();
        addListenerOnCancelButton();

    }

    /**
     * Earn Button click listener.
     *
     */
    public void addListenerOnCancelButton() {

        Button earnButton = (Button) findViewById(R.id.redeemCancelButton);

        earnButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation));
                Intent itt = new Intent(EarnPoints.this, ReportPoints.class);
                itt.putExtra("storeId", storeId);
                startActivity(itt);
            }

        });

    }

    /**
     * Earn Button click listener.
     *
     */
    public void addListenerOnAcceptButton() {

        Button earnButton = (Button) findViewById(R.id.redeemButton);

        earnButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                EarnBO earnBO = new EarnBO("Ravi", "Ravi Store", "50", "50");
                clientDatabase.setValue(earnBO);

                String msg = clientDatabase.getKey().toString();

                //Toast.makeText(getApplication(),msg,Toast.LENGTH_LONG).show();
                arg0.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation));

                Intent itt = new Intent(EarnPoints.this, ReportPoints.class);
                itt.putExtra("storeId", storeId);
                startActivity(itt);
                // EarnBO earn = new EarnBO(userId, storeId, bill, points);
                // MongoDB db = new MongoDB();
                //insertRecordEarn(earn);


            }

        });

    }

    public void insertRecordEarn(EarnBO earn) {

        //
        Log.i(log, "Earn points.");
        Document document = new Document();
        document.put("userId", earn.getUserId());
        document.put("storeId", earn.getStoreId());
        document.put("billAmount", earn.getBillAmount());
        document.put("earned", earn.getEarned());

        // collection.insertOne(document);
        insertDocument(document);
    }

    private void insertDocument(Document document) {

        try {
            // MongoCollection collection = getMongoCollection();
            AsyncTaskRunner task = new AsyncTaskRunner(document);
            task.execute();
            // String str_result = task.execute().get();
        } catch (Throwable th) {
            th.printStackTrace();
        }

        // String str_result= new RunInBackGround().execute().get();
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        Document document = null;

        public AsyncTaskRunner(Document document) {
            this.document = document;
        }

        @Override
        protected String doInBackground(String... params) {

            // Calls onProgressUpdate()
            publishProgress("Sleeping...");
            try {

                if (isInternetAvailable()) {

                    Log.i(log, "Internet available.");
                }


                Log.i(log, "doInBackground");
                //MongoDB.getInstance().insertRecordEarn(d);

                MongoClientURI mongoURI = new MongoClientURI("mongodb://venugopalbeetkuri:shreshta143@ds015770.mlab.com:15770/pointshub");
                MongoClient mClient = new MongoClient(mongoURI);

                MongoDatabase db = mClient.getDatabase(mongoURI.getDatabase());

                MongoCollection collection = db.getCollection("client");

                //MongoCollection collection = getMongoCollection();

                collection.insertOne(document);

            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return "";
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            Log.i("Seller app", "onPostExecute");
            // execution of result of Long time consuming operation
            // finalResult.setText(result);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            Log.i("Seller app", "onPreExecute");
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog

            Intent itt = new Intent(EarnPoints.this, ReportPoints.class);
            itt.putExtra("storeId", storeId);
            startActivity(itt);
        }
    }

    public boolean isInternetAvailable() {
        try {

            // You can replace it with your name.
            InetAddress ipAddr = InetAddress.getByName("google.com");

            if (ipAddr.equals("")) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }

    }

}
