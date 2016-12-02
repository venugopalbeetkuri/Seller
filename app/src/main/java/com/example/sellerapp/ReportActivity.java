package com.example.sellerapp;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.R;
import com.example.db.StoreBO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.type;
import static com.example.R.id.billamount;

/**
 * Created by Chalam on 11/29/2016.
 */

public class ReportActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private MenuInflater menuInflater;
    int i = 0;
    //StoreBO storeBO;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference clientDatabase = database.child("client");
    Query query = clientDatabase.child("storeexample");


    TableLayout stk;
    Spinner spinner;
    Calendar c = Calendar.getInstance();

    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    Date yester = new Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24));
    Date last7 = new Date(System.currentTimeMillis() - (7000 * 60 * 60 * 24));
    Date last30 = new Date(System.currentTimeMillis() - (30000 * 60 * 60 * 24));

    String presentdate = df.format(c.getTime());
    String yesterday = df.format(yester);
    String last7days = df.format(last7);
    String last30days = df.format(last30);
//    String name = "storeexample";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportactivity);

        //name = storeBO.getStoreName().toString();
        //init();

        List<String> categories = new ArrayList<String>();
        categories.add("Today (" + presentdate + ")");
        categories.add("Last Week (" + last7days + ") - (" + yesterday + ")");
        categories.add("Last Month (" + last30days + ") - (" + yesterday + ")");


        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String item = adapterView.getItemAtPosition(position).toString();
        //init();
        Toast.makeText(getApplicationContext(), "Selected : " + item, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.report_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        stk.removeAllViewsInLayout();
        if(item.getItemId()==R.id.today)
        {
            init();
            Toast.makeText(getApplicationContext(),"Today",Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()==R.id.lastweek)
        {
            Toast.makeText(getApplicationContext(),"Last Week",Toast.LENGTH_SHORT).show();
        }
        if(item.getItemId()==R.id.lastmonth)
        {
            Toast.makeText(getApplicationContext(),"Last Month",Toast.LENGTH_SHORT).show();
        }
        //stk.removeAllViews();
        return super.onOptionsItemSelected(item);
    }

    public void init() {
        stk = (TableLayout) findViewById(R.id.table_main);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT,1f);
        //layoutParams.setMargins(25, 25, 25, 25);
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setLayoutParams(layoutParams);

        final TextView tv1 = new TextView(this);
        tv1.setText(" Date ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);

        final TextView tv2 = new TextView(this);
        tv2.setText(" Bill Amount ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);

        final TextView tv3 = new TextView(this);
        tv3.setText(" Points ");
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);

        final TextView tv4 = new TextView(this);
        tv4.setText(" Discount ");
        tv4.setTextColor(Color.WHITE);
        tbrow0.addView(tv4);

        stk.addView(tbrow0);

        final TextView t1v = new TextView(this);
        final TextView t2v = new TextView(this);
        final TextView t3v = new TextView(this);
        final TextView t4v = new TextView(this);
        query.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot timeStampSnapShot : dataSnapshot.getChildren()) {

                    HashMap<String, String> timeStampKey = (HashMap)timeStampSnapShot.getValue();
                    String type = timeStampKey.get("type");
                    String pointsStr = timeStampKey.get("points");
                    String billAmountStr = timeStampKey.get("billAmount");
                    String discountAmountStr = timeStampKey.get("disCountAmount");

                    print(type,pointsStr,billAmountStr,discountAmountStr);
                }
            }

            private void print(String type, String pointsStr, String billAmountStr, String discountAmountStr) {

                TableRow tbrow = new TableRow(getApplicationContext());

                if(type.equalsIgnoreCase("earn"))
                {
                    tbrow.setBackgroundColor(Color.parseColor("#FF0000"));
                }
                else if(type.equalsIgnoreCase("redeem"))
                {
                    tbrow.setBackgroundColor(Color.parseColor("#00FF00"));
                }

                TextView t0v = new TextView(getApplicationContext());
                t0v.setText("date");
                t0v.setTextSize(20);
                t0v.setTextColor(Color.WHITE);
                t0v.setGravity(Gravity.CENTER);
                tbrow.addView(t0v);

                TextView t1v = new TextView(getApplicationContext());
                t1v.setText(pointsStr);
                t1v.setTextColor(Color.WHITE);
                t1v.setGravity(Gravity.CENTER);
                tbrow.addView(t1v);

                TextView t2v = new TextView(getApplicationContext());
                t2v.setText(billAmountStr);
                t2v.setTextColor(Color.WHITE);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v);

                TextView t3v = new TextView(getApplicationContext());
                t3v.setText(discountAmountStr);
                t3v.setTextColor(Color.WHITE);
                t3v.setGravity(Gravity.CENTER);
                tbrow.addView(t3v);

                /*TextView t4v = new TextView(getApplicationContext());
                t4v.setText(discountAmountStr);
                t4v.setTextColor(Color.WHITE);
                t4v.setGravity(Gravity.CENTER);
                tbrow.addView(t4v);*/

                stk.addView(tbrow);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*public void DataRetrive() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference clientDatabase = database.child("client");
        Query query = clientDatabase.child("storeexample");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot timeStampSnapShot : dataSnapshot.getChildren()) {

                    HashMap<String, String> timeStampKey = (HashMap)timeStampSnapShot.getValue();
                    String type = timeStampKey.get("type");
                    Log.i("Received Type",type);
                    String pointsStr = timeStampKey.get("points");
                    Log.i("Received points",pointsStr);
                    String billAmountStr = timeStampKey.get("billAmount");
                    Log.i("Received Bill",billAmountStr);
                    String discountAmountStr = timeStampKey.get("disCountAmount");
                    Log.i("Received Discount",discountAmountStr);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/
}