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
import static com.example.R.id.points;
import static com.example.R.id.time;

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

    TextView bill,points,discount;
    int point;
    int bil;
    int b;
    int p;
    int d;
    Boolean ischecked = false;
    TableLayout stk;
    Spinner spinner;
    Calendar c = Calendar.getInstance();

    final TableRow.LayoutParams tableRow = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT,1f);

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

        bill= (TextView) findViewById(R.id.bill);
        points = (TextView)findViewById(R.id.points);
        discount = (TextView) findViewById(R.id.discount);

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

        if(position==0) {
            if(ischecked == false)
            {
                init();
                Toast.makeText(getApplicationContext(), "Selected : Today", Toast.LENGTH_SHORT).show();

                //Toast.makeText(getApplicationContext(),"  Bill : "+b+"  Points : "+p+"  Discount : "+d,Toast.LENGTH_LONG).show();
                //totalprint();
            }
            else
            {
                clear();
                init();
            }
            //Toast.makeText(getApplicationContext(), "  Bill : " + b + "  Points : " + p, Toast.LENGTH_LONG).show();
            //totalprint();
        }
        if(position == 1){
            if(ischecked == false) {
                //init();
                Toast.makeText(getApplicationContext(), "Selected : Last Week", Toast.LENGTH_SHORT).show();
            }else
            {
                clear();
                //init();
            }
        }
        if(position == 2)
        {
            if(ischecked == false) {
                //init();
                Toast.makeText(getApplicationContext(), "Selected : Last Month", Toast.LENGTH_SHORT).show();
            }else
            {
                clear();
                //init();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
/*
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
            if(ischecked == false)
            {
                init();
                Toast.makeText(getApplicationContext(), "Today", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(),"  Bill : "+b+"  Points : "+p+"  Discount : "+d,Toast.LENGTH_LONG).show();
                totalprint();
            }
            else
            {
                clear();
                init();
                //totalprint();
            }
        }
        if(item.getItemId()==R.id.lastweek)
        {
            if(ischecked == false) {
                init();
                Toast.makeText(getApplicationContext(), "Last Week", Toast.LENGTH_SHORT).show();
            }else
            {
                clear();
                init();
            }
        }
        if(item.getItemId()==R.id.lastmonth)
        {
            if(ischecked == false) {
                init();
                Toast.makeText(getApplicationContext(), "Last Week", Toast.LENGTH_SHORT).show();

            }else
            {
                clear();
                init();
            }
        }
        //stk.removeAllViews();
        return super.onOptionsItemSelected(item);
    }*/

    public void clear()
    {
        stk.removeAllViewsInLayout();
        p=0;b=0;d=0;
        ischecked = false;
    }
    public void totalprint()
    {
        points.setText("Points : "+p);
        bill.setText("Bill Amount : "+b);
        discount.setText("Discount : "+d);
    }

    public void init() {
        ischecked = true;
        tableRow.setMargins(25,25,25,25);
        tableRow.weight=1;
        stk = (TableLayout) findViewById(R.id.table_main);
        TableRow tbrow0 = new TableRow(this);

        final TextView tv0 = new TextView(this);
        tv0.setLayoutParams(tableRow);
        tv0.setText(" Date ");
        tv0.setTextColor(Color.WHITE);
        tv0.setGravity(Gravity.CENTER);
        tbrow0.addView(tv0);

        final TextView tv1 = new TextView(this);
        tv1.setLayoutParams(tableRow);
        tv1.setText(" Points ");
        tv1.setTextColor(Color.WHITE);
        tv1.setGravity(Gravity.CENTER);
        tbrow0.addView(tv1);

        final TextView tv2 = new TextView(this);
        tv2.setLayoutParams(tableRow);
        tv2.setText(" Bill Amount ");
        tv2.setTextColor(Color.WHITE);
        tv2.setGravity(Gravity.CENTER);
        tbrow0.addView(tv2);

        final TextView tv3 = new TextView(this);
        tv3.setLayoutParams(tableRow);
        tv3.setText(" Discount ");
        tv3.setTextColor(Color.WHITE);
        tv3.setGravity(Gravity.CENTER);
        tbrow0.addView(tv3);

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
                    String date = timeStampKey.get("time");

                    point= Integer.parseInt(pointsStr);
                    p+=point;

                    bil = Integer.parseInt(billAmountStr) ;
                    b+=bil;

                    int disco = Integer.parseInt(discountAmountStr);
                    d=d+disco;

                    print(type,pointsStr,billAmountStr,discountAmountStr,date);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void print(String type, String pointsStr, String billAmountStr, String discountAmountStr,String date) {

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
        t0v.setText(date);
        t0v.setLayoutParams(tableRow);
        t0v.setTextColor(Color.WHITE);
        t0v.setGravity(Gravity.CENTER);
        tbrow.addView(t0v);

        TextView t1v = new TextView(getApplicationContext());
        t1v.setText(pointsStr);
        t1v.setLayoutParams(tableRow);
        t1v.setTextColor(Color.WHITE);
        t1v.setGravity(Gravity.CENTER);
        tbrow.addView(t1v);

        TextView t2v = new TextView(getApplicationContext());
        t2v.setText(billAmountStr);
        t2v.setLayoutParams(tableRow);
        t2v.setTextColor(Color.WHITE);
        t2v.setGravity(Gravity.CENTER);
        tbrow.addView(t2v);

        TextView t3v = new TextView(getApplicationContext());
        t3v.setText(discountAmountStr);
        t3v.setLayoutParams(tableRow);
        t3v.setTextColor(Color.WHITE);
        t3v.setGravity(Gravity.CENTER);
        tbrow.addView(t3v);

        stk.addView(tbrow);
        totalprint();
    }

}