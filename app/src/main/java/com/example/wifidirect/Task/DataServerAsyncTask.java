package com.example.wifidirect.Task;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import com.example.db.PointsBO;
import com.example.sellerapp.EarnPoints;
import com.example.sellerapp.MainActivity;
import com.example.sellerapp.RedeemPoints;
import com.example.wifidirect.WifiDirectReceive;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.R.id.textView;

public class DataServerAsyncTask extends AsyncTask<Void, Void, String> {

    private TextView statusText;
    private WifiDirectReceive activity;

    public DataServerAsyncTask(WifiDirectReceive activity, TextView statusText) {

        this.statusText = statusText;
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

             Log.i("bizzmark", "Client connected.");
             InputStream inputstream = client.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             int i;
             while ((i = inputstream.read()) != -1) {
                 baos.write(i);
             }

             String str = baos.toString();

            try {

                OutputStream outputStream = client.getOutputStream();
                outputStream.write("Success\r\n".getBytes());
            }
            catch (Throwable th){
                th.printStackTrace();
            }

            serverSocket.close();

            return str;

        } catch (Throwable e) {
            Log.e("bizzmark", e.toString());
            return null;
        }
    }


    @Override
    protected void onPostExecute(String result) {

        Log.i("bizzmark", "data on post execute.Result: " + result);

        //Toast.makeText(activity, "From customer: " + result, Toast.LENGTH_SHORT).show();

        if (result != null) {
            //statusText.setText(result);
            //statusText.setVisibility(View.INVISIBLE);
            try {
                Gson gson = new Gson();
                PointsBO points = gson.fromJson(result, PointsBO.class);

                Log.i("bizzmark", "data on post execute.Result: " + points.getPoints());
                String type = points.getType().toString();
                //saveToFireBase(points);
                if(type.equalsIgnoreCase("Earn")){
                    Intent intent = new Intent(activity,EarnPoints.class);
                    intent.putExtra("earnRedeemString", result);
                    activity.startActivity(intent);
                }else if (type.equalsIgnoreCase("Redeem")){
                    Intent intent = new Intent(activity,RedeemPoints.class);
                    intent.putExtra("earnRedeemString", result);
                    activity.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    /*private void saveToFireBase(PointsBO points) {
        points.getDeviceId();
        String type = points.getType().toString();
        String billAmount = points.getBillAmount().toString();
        String storeName = points.getStoreName().toString();
        String checkpoints = points.getPoints().toString();

        if (type.equalsIgnoreCase("earn")) {

            earnDatabase.setValue(points);
            //clientDatabase.child("Earn").setValue(points);
            Intent intent = new Intent(activity,EarnPoints.class);

            intent.putExtra("earnPoints",checkpoints);
            intent.putExtra("billAmount",billAmount);

            activity.startActivity(intent);
        }else{

            redeemDatabase.setValue(points);
            Intent intent = new Intent(activity,RedeemPoints.class);

            intent.putExtra("redeemPoints",checkpoints);
            intent.putExtra("billAmount",billAmount);
            intent.putExtra("discountAmount",billAmount);

            activity.startActivity(intent);
        }
    }*/
/*
    private boolean saveToFireBase(PointsBO points){

        boolean success = false;

        try{



            String type = points.getType();
            String storeName = points.getStoreName();
            if("Earn".equalsIgnoreCase(type)){

                clientDatabase.child("points").setValue(points);
            }else {

                List<PointsBO> storeTransactions = getPointsOfMyStore(storeName);

                // Totalearned - totalredeemed (Use for loop)
                int availablePoints = getAvailablePointsForRedeem(storeTransactions);

                String wantsToRedeem = points.getPoints();
                int toRedeem = Integer.getInteger(wantsToRedeem);

                int pointsAvailableAfterRedeem = availablePoints-toRedeem;

                if(pointsAvailableAfterRedeem > 0){
                    // Success and save to fire base.
                    clientDatabase.child("points").setValue(points);
                } else {
                    // Toast error. Insufficient funds.
                }

            }




            success = true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return  success;
    }*/

    @Override
    protected void onPreExecute() {

    }

}
