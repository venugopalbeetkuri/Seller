package com.example.wifidirect.Task;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.example.wifidirect.WifiDirectReceive;

public class DataServerAsyncTask extends AsyncTask<Void, Void, String> {

    private TextView statusText;
    private WifiDirectReceive activity;

    public DataServerAsyncTask(WifiDirectReceive activity, View statusText) {

        this.statusText = (TextView) statusText;
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

            serverSocket = null;

            return str;

        } catch (Throwable e) {
            Log.e("bizzmark", e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {

        Log.i("bizzmark", "data on post execute.Result: " + result);

        Toast.makeText(activity, "From customer: " + result, Toast.LENGTH_SHORT).show();

        if (result != null) {
            statusText.setText("From customer: " + result);
        }
    }

    @Override
    protected void onPreExecute() {

    }

}
