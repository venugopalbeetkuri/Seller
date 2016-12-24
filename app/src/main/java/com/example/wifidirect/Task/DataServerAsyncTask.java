package com.example.wifidirect.Task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.example.db.PointsBO;
import com.example.db.StoreBO;
import com.example.login.LoginActivity;
import com.example.sellerapp.EarnPoints;
import com.example.sellerapp.RedeemPoints;
import com.example.wifidirect.WifiDirectReceive;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class DataServerAsyncTask extends AsyncTask<Void, Void, String> {

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    String storename = "teststore";
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference clientDatabase = database.child("client");

    Query query = clientDatabase.child(storename);
    private WifiDirectReceive activity;
    String earnpoints,redeempoints;
    String id = null;
    StoreBO storeBO;
    PointsBO pointsBO;
    int earnpoint,redeempoint;
    int inputvalue;
    static int percentage = 5;
    int temp = 1;
    int ep = 0;
    int rp = 0;
    String remoteAddress = null;
    int check = 0;
    String p = null;
    String b = null;


    public DataServerAsyncTask(WifiDirectReceive activity) {
        this.activity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {

             Log.i("bizzmark", "data doing back");
             ServerSocket serverSocket = new ServerSocket(8888);
             serverSocket.setReuseAddress(true);

             Log.i("bizzmark", "Opening socket on 8888.");
             Socket client = serverSocket.accept();

             remoteAddress =  ((InetSocketAddress)client.getRemoteSocketAddress()).getAddress().getHostName();
                     // client.getRemoteSocketAddress().toString();

             Log.i("bizzmark", "Client connected.");

             InputStream inputstream = client.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             int i;
             while ((i = inputstream.read()) != -1) {
                 baos.write(i);
             }

             String str = baos.toString();

            serverSocket.close();
            return str;

        } catch (Throwable e) {
            Log.e("bizzmark", e.toString());
            return null;
        }

    }
    @Override
    protected void onPostExecute(String result) {

        String time = df.format(c.getTime());

        Log.i("bizzmark", "data on post execute.Result: " + result);

        if (result != null) {
            try {
                final String discount = "0";
                Gson gson = new Gson();
                final PointsBO points = gson.fromJson(result, PointsBO.class);
                inputvalue = Integer.parseInt(points.getPoints());
                points.setTime(time);//setting time to PointsBo
                final String type = points.getType().toString();

                final String storeName = points.getStoreName();


                //updateStoreName(storeName);
                //*********************************************************************************
/*
                    try {
                        //String storeName = pointsBO.getStoreName();
                        // Getting firebase auth object.
                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                        final String storeEmail = firebaseAuth.getCurrentUser().getEmail();

                        if (firebaseAuth.getCurrentUser() != null) {

                            Log.i("Current User ", "Not Null");
                            Query query = database.child("store");

                            // Query query = storeDatabase.orderByChild("Earn");
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    boolean found = false;
                                    for (DataSnapshot timeStampSnapShot : dataSnapshot.getChildren()) {

                                        HashMap<String, String> timeStampKey = (HashMap) timeStampSnapShot.getValue();

                                        String storeNameDB = timeStampKey.get("storeName");
                                        Log.i("Store Name from DB", storeNameDB);
                                        if (storeNameDB.equalsIgnoreCase(storeName)) {
                                            Log.i("Store Name Equals :", storeNameDB + " ~ " + storeName);
                                            String Per = timeStampKey.get("percentage");
                                            Log.i("Found Percentage : ", Per);
                                            percentage = Integer.parseInt(Per);
                                            found = true;
                                            Toast.makeText(activity, " Retrived Percentage : " + Per, Toast.LENGTH_LONG).show();
                                            Log.i("Retrived Percentage : ", String.valueOf(percentage));
                                        }
                                    }
                                    if (!found) {
                                        Log.i("Retrive Status : ", "Not Found");
                                    */
/*StoreBO store = new StoreBO(storeEmail, storeName, percentage);
                                    String formattedDate = df.format(c.getTime());
                                    DatabaseReference time = storeDatabase.child(formattedDate);
                                    time.setValue(store);*//*

                                    }
                                    calculate(percentage);
                                }

                                */
                                /*private void calculate(int percent) {
                                    Log.i("Data outside : ", " onDataChange : "+percent);
                                    temp = percent;
                                }*//*


                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
*/

                //*********************************************************************************
                Log.i("Percentage from Bo ",storeBO.getPercentage());
                temp = Integer.parseInt(storeBO.getPercentage());

                        b = points.getBillAmount();
                        p = points.getPoints();
                        //Log.i("Get Points from Bo : ", p);
                        int i = Integer.parseInt(p);
                        i = ((Integer.parseInt(b) * temp)/100);
                        p = String.valueOf(i);
                        Log.i("Set Points : ", p);

                //*****************************
                if (type.equalsIgnoreCase("earn")) {
                    Log.i("Earn Percent ", String.valueOf(percentage));
                    points.setPoints(p);//Setting Points to PointsBo
                }
                result = gson.toJson(points);

//*************************************************************************** Get the percentage from DataBase..!!!
/*                try {

                    //String storeName = pointsBO.getStoreName();
                    // Getting firebase auth object.
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    final String storeEmail = firebaseAuth.getCurrentUser().getEmail();

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
                                        String Per = timeStampKey.get("percentage");
                                        percentage = Integer.parseInt(Per);
                                        found = true;
                                        Toast.makeText(activity, " Retrived Percentage : "+Per, Toast.LENGTH_LONG).show();
                                        Log.i("Retrived Percentage : ", String.valueOf(percentage));
                                    }
                                }
                                if(!found) {
                                    Log.i("Retrive Status : ","Not Found");
                            *//*StoreBO store = new StoreBO(storeEmail, storeName, percentage);
                            String formattedDate = df.format(c.getTime());
                            DatabaseReference time = storeDatabase.child(formattedDate);
                            time.setValue(store);*//*
                                }
                                calculate(percentage);
                            }

                            private void calculate(int percentage) {
                                Log.i("Data outside : "," onDataChange");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }*/
//********************************************************************************


                id = points.getDeviceId().toString();

                if(type.equalsIgnoreCase("earn")) {

                    Intent intent = new Intent(activity,EarnPoints.class);
                    intent.putExtra("earnRedeemString", result);
                    intent.putExtra("remoteAddress", remoteAddress);
                    activity.startActivity(intent);
                }
                else if (type.equalsIgnoreCase("Redeem")){

                  final String finalResult = result;
                    query.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ep=0;rp=0;
                            for (DataSnapshot timeStampSnapShot : dataSnapshot.getChildren()) {

                                HashMap<String, String> timeStampKey = (HashMap)timeStampSnapShot.getValue();
                                String deviceid = timeStampKey.get("deviceId");
                                if(id.equalsIgnoreCase(deviceid)){
                                    String type = timeStampKey.get("type");
                                    if(type.equalsIgnoreCase("earn")) {
                                        earnpoints = timeStampKey.get("points");
                                        //Toast.makeText(activity.getApplicationContext(),"Points : "+earnpoints, Toast.LENGTH_SHORT).show();
                                        try {
                                            earnpoint = Integer.parseInt(earnpoints);
                                            ep += earnpoint;

                                            //Toast.makeText(activity.getApplicationContext(), "Earn Points : "+ep, Toast.LENGTH_SHORT).show();
                                        }
                                        catch(NumberFormatException ex) {
                                            ex.printStackTrace();
                                        }
                                        //earnvalues=Integer.parseInt(pointsStr);
                                    }
                                    if(type.equalsIgnoreCase("Redeem")){
                                        redeempoints = timeStampKey.get("points");
                                        try {
                                            redeempoint = Integer.parseInt(redeempoints);
                                            rp += redeempoint;
                                            //Toast.makeText(activity.getApplicationContext(), "Redeem Points : "+rp, Toast.LENGTH_SHORT).show();
                                        }
                                        catch(NumberFormatException ex) {
                                            ex.printStackTrace();
                                        }
                                        //earnvalues=Integer.parseInt(pointsStr);
                                    }
                                }
                            }
                            //Log.i("Retrive all : ", String.valueOf(pp));
                            Log.i("Input Value : ", String.valueOf(inputvalue));
                            Log.i("Earn Values : ", String.valueOf(ep));
                            Log.i("Redeem Values : ", String.valueOf(rp));
                            int cp = ep-rp;

                            if(inputvalue<(ep-rp)){
                                Log.i("Condition True : ", String.valueOf(cp));
                                check=1;
                                print();
                                }else{
                                Toast.makeText(activity, "No Enough Points to Redeem  ", Toast.LENGTH_SHORT).show();
                                Toast.makeText(activity, "Current Points : "+cp, Toast.LENGTH_SHORT).show();
                                Log.i("Condition Status : "," Failure ");
                                check=0;
                            }
                            //print(pp);
                        }

                        private void print() {if(check==1){
                            points.setDisCountAmount(discount);
                            // NewBillAmount = billamount - redeem points;
                            String bill = points.getBillAmount();
                            String po = String.valueOf(inputvalue);
                            int newbill = Integer.parseInt(bill)-Integer.parseInt(po);
                            String newbillA = String.valueOf(newbill);
                            Intent intent = new Intent(activity, RedeemPoints.class);
                            intent.putExtra("earnRedeemString", finalResult);
                            intent.putExtra("remoteAddress", remoteAddress);
                            intent.putExtra("newBillAmount",newbillA);
                            activity.startActivity(intent);}
                        else{
                            Toast.makeText(activity, "Sorry...!", Toast.LENGTH_SHORT).show();
                            Log.i("Status : ","Can't redeem");
                        }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                points.setPoints(p);
                points.setTime(time);
                result = gson.toJson(points);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void calculate(int percentage) {

    }

    private void updateStoreName(final String storeName) {

    }

    @Override
    protected void onPreExecute() {

    }

    public void showToast(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper())
                .post(
                        new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }
                        });
    }

}
