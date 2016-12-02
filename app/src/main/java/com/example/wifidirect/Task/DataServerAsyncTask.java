package com.example.wifidirect.Task;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import com.example.db.PointsBO;
import com.example.sellerapp.EarnPoints;
import com.example.sellerapp.RedeemPoints;
import com.example.wifidirect.WifiDirectReceive;
import com.google.gson.Gson;

public class DataServerAsyncTask extends AsyncTask<Void, Void, String> {

    private WifiDirectReceive activity;

    String remoteAddress = null;

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

            /*try {

                OutputStream outputStream = client.getOutputStream();
                outputStream.write("Success\r\n".getBytes());
            }
            catch (Throwable th){
                th.printStackTrace();
            }*/

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

        if (result != null) {

            try {

                Gson gson = new Gson();
                PointsBO points = gson.fromJson(result, PointsBO.class);

                Log.i("bizzmark", "data on post execute.Result: " + points.getPoints());
                String type = points.getType().toString();

                // remoteAddress = points.getMacAddress();

                //saveToFireBase(points);
                if(type.equalsIgnoreCase("Earn")){

                    Intent intent = new Intent(activity,EarnPoints.class);
                    intent.putExtra("earnRedeemString", result);
                    intent.putExtra("remoteAddress", remoteAddress);
                    activity.startActivity(intent);
                }else if (type.equalsIgnoreCase("Redeem")){

                    Intent intent = new Intent(activity,RedeemPoints.class);
                    intent.putExtra("earnRedeemString", result);
                    intent.putExtra("remoteAddress", remoteAddress);
                    activity.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    protected void onPreExecute() {

    }

}
