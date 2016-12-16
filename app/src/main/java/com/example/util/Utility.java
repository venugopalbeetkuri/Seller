package com.example.util;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Created by Venu gopal on 17-11-2016.
 */

public class Utility {


    static Gson gson = null;

    static boolean testing = true;

    public static int totalEarnPoints;

    public static int totalRedeemPoints;

    public static int totalBillAmount;

    public static int totalDiscount;

    static TextView pointsGiven, totalsale, totaldiscount;

    public static synchronized Gson getGsonObject() {

        if(null == gson){
            gson = new Gson();
        }

        return gson;
    }

    public static synchronized boolean isTesting () {
        return testing;
    }


    public static void updateReference(TextView pointsGiven, TextView totalsale, TextView totaldiscount){
        try {

            Utility.pointsGiven = pointsGiven;
            Utility.totaldiscount = totaldiscount;
            Utility.totalsale = totalsale;
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static void calculateTotal(String storeName) {

        try {

            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            DatabaseReference clientDatabase = database.child("client");
            Query query  = clientDatabase.child(storeName);

            // Query query = storeDatabase.orderByChild("Earn");
            query.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Utility.totalBillAmount = 0;
                    Utility.totalDiscount = 0;
                    Utility.totalBillAmount = 0;


                    for (DataSnapshot timeStampSnapShot : dataSnapshot.getChildren()) {

                        HashMap<String, String> timeStampKey = (HashMap)timeStampSnapShot.getValue();
                        String type = timeStampKey.get("type");
                        String pointsStr = timeStampKey.get("points");
                        String billAmountStr = timeStampKey.get("billAmount");
                        String discountAmountStr = timeStampKey.get("disCountAmount");


                        int billAmount = Integer.parseInt(billAmountStr);
                        Utility.totalBillAmount = Utility.totalBillAmount + billAmount;

                        /*if(null == discountAmountStr){
                            discountAmountStr = "0";
                        }*/
                       /* int discountAmount = Integer.parseInt(discountAmountStr);
                        Utility.totalDiscount = Utility.totalDiscount + discountAmount;
*/
                        /*if("Earn".equalsIgnoreCase(type)) {

                            int points = Integer.parseInt(pointsStr);
                            Utility.totalEarnPoints = Utility.totalEarnPoints + points;
                        } else if("Redeem".equalsIgnoreCase(type)) {

                            int points = Integer.parseInt(pointsStr);
                            Utility.totalRedeemPoints = Utility.totalRedeemPoints + points;
                        }*/
                    }

                    Integer totalPoints = Utility.totalEarnPoints - Utility.totalRedeemPoints;
                    Integer totalBillAmount = Utility.totalBillAmount;
                    Integer totalDiscountmount = Utility.totalDiscount;

/*
                    Utility.pointsGiven.setText(totalPoints.toString());
                    Utility.totalsale.setText(totalBillAmount.toString());
                    Utility.totaldiscount.setText(totalDiscountmount.toString());
*/
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
