package com.example.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.example.R;
import com.example.db.PointsBO;
import com.example.db.StoreBO;
import com.example.login.LoginActivity;
import com.example.sellerapp.EarnPoints;
import com.example.sellerapp.RedeemPoints;
import com.example.sellerapp.ReportActivity;
import com.example.wifidirect.Adapter.WifiAdapter;
import com.example.wifidirect.BroadcastReceiver.WifiDirectBroadcastReceiver;
import com.example.wifidirect.Service.DataTransferService;
import com.example.wifidirect.Task.DataServerAsyncTask;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class WifiDirectReceive extends AppCompatActivity implements View.OnClickListener {

    private TextView txtView;
    private RecyclerView mRecyclerView;
    private WifiAdapter mAdapter;
    private Button btnRefresh;
    private FirebaseAuth firebaseAuth;
    private Button report;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mFilter;
    private WifiP2pInfo info;
    private MenuInflater menuInflater;
    static boolean calledAlready = false;
    private DataServerAsyncTask mDataTask;
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private TextView pointsGiven, totalsale = null, totaldiscount = null;
    String storeName = "storename";
    String percentage = "10";
    int point;
    static String store = "teststore";
    StoreBO storeBO;
    int bil;
    int b;
    int p;
    int d;

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference clientDatabase = database.child("client");
    DatabaseReference storeDatabase = database.child("store");
    String EmailidDB;
    boolean check= false;
   // ReportActivity reportActivity;
    // For peers information.
    private List<HashMap<String, String>> peersshow = new ArrayList();

    // All the peers.
    private List peers = new ArrayList();

    boolean sendAck = false;
    String jsonACK = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_direct_client);
        final Calendar c = Calendar.getInstance();

        String formattedDate = df.format(c.getTime());
        final DatabaseReference time = storeDatabase.child(formattedDate);

        info = null;
        final String storeName = "StoreName";
        final String percentage = "10";
        //storeBO = new StoreBO(storeEmail, storeName, percentage);
        if (!calledAlready) {
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        Intent intent = getIntent();
        if(null != intent.getExtras()) {

            jsonACK = intent.getExtras().getString("jsonACK");
            sendAck = true;
            info = null;
        } else {
            sendAck = false;
        }

        firebaseAuth = FirebaseAuth.getInstance();
        final String email=firebaseAuth.getCurrentUser().getEmail();
        final StoreBO storeBO = new StoreBO(email, storeName, percentage);

        //************************************************************************************************
        try {
            //String storeName = pointsBO.getStoreName();
            // Getting firebase auth object.
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            final String storeemail = firebaseAuth.getCurrentUser().getEmail();

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

                            EmailidDB = timeStampKey.get("emailId");
                            Log.i("Email Id from DB", EmailidDB);
                            Log.i("Local Email Id ",email);
                            if (EmailidDB.equalsIgnoreCase(email)) {
                                Log.i("Email ID Equals :", EmailidDB + " ~ " + email);
                                String Per = timeStampKey.get("percentage");
                                store = timeStampKey.get("storeName");
                                storeBO.setStoreName(store);
                                Log.i("Found Percentage : ", Per);
                                storeBO.setPercentage(Per);
                                found=true;
                                Toast.makeText(getApplicationContext(), " Retrived Percentage : " + Per, Toast.LENGTH_LONG).show();
                                Log.i("Retrived Percentage : ", Per);
                                Log.i("Percentage from Bo ",storeBO.getPercentage());
                                //print(Per);
                            }
                        }
                        if (!found)
                        {
                            if (!EmailidDB.equalsIgnoreCase(email)) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(WifiDirectReceive.this);
                                builder.setMessage("Please Contact Bizzmark for percentage changes.")
                                        .setCancelable(false)
                                        .setTitle("Warning...!")
                                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                Log.i("Retrive Status : ", "Not Found");
                                                //Log.i("Store Email ",storeBO.getEmailId());
                                                finish();
                                                firebaseAuth.signOut();
                                                Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent1);
                                                //Toast.makeText(getApplicationContext(),"Default Percentage is 10%",Toast.LENGTH_LONG).show();

                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();

                            }
                        }

                        //calculate(percentage);
                    }

                    /*private void calculate(int percent) {
                        Log.i("Data outside : ", " onDataChange : "+percent);
                        temp = percent;
                    }*/
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //************************************************************************************************

       //String storeName = getStoreName();


        //Utility.calculateTotal("xyz");
        report = (Button) findViewById(R.id.report);

        //updatePoints();

        initView();
        initIntentFilter();
        initReceiver();
        initEvents();
        getTxtView();
        discoverPeers();

        if(null == mDataTask) {
            mDataTask = new DataServerAsyncTask(WifiDirectReceive.this);
            mDataTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
//OVER ALL POINTS DISPLAY IN MAIN SCREEN
        Query query = clientDatabase.child(store);
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

                    try {
                        point = Integer.parseInt(pointsStr);
                        p += point;

                        bil = Integer.parseInt(billAmountStr);
                        b += bil;
                    }catch(NumberFormatException ex) {
                        ex.printStackTrace();
                    }
//                    int disco = Integer.parseInt(discountAmountStr);
//                    d=d+disco;
                }
                totalprint();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void totalprint()
    {
        String point = Integer.toString(p);
        pointsGiven.setText(point);
        String bill = Integer.toString(b);
        totalsale.setText(bill);
        totaldiscount.setText(point);
        //pointsGiven.setText(p);
        //totaldiscount.setText(b);
    }

    private void initView() {

        pointsGiven = (TextView) findViewById(R.id.pointsgiven);
        totalsale = (TextView) findViewById(R.id.totalsale);
        totaldiscount = (TextView)findViewById(R.id.discountgiven);
        txtView = (TextView) findViewById(R.id.tv1);
        btnRefresh = (Button)findViewById(R.id.btnRefresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mAdapter = new WifiAdapter(peersshow);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));

    }

    private void initIntentFilter() {

        mFilter = new IntentFilter();
        mFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void initReceiver() {

        mManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, Looper.myLooper(), null);

        WifiP2pManager.PeerListListener mPeerListListerner = new WifiP2pManager.PeerListListener() {

            @Override
            public void onPeersAvailable(WifiP2pDeviceList peersList) {

                peers.clear();
                peersshow.clear();

                Collection<WifiP2pDevice> aList = peersList.getDeviceList();
                peers.addAll(aList);

                for (int i = 0; i < aList.size(); i++) {
                    WifiP2pDevice a = (WifiP2pDevice) peers.get(i);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", a.deviceName);
                    map.put("address", a.deviceAddress);
                    peersshow.add(map);
                }

                mAdapter = new WifiAdapter(peersshow);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(WifiDirectReceive.this));

                if (null == info) {
                    return;
                }

                if(sendAck) {

                    mAdapter.SetOnItemClickListener(new WifiAdapter.OnItemClickListener() {

                        @Override
                        public void OnItemClick(View view, int position) {
                            // createConnect(peersshow.get(position).get("address"), peersshow.get(position).get("name"));
                            createConnect(peersshow.get(position).get("address"));
                        }

                        @Override
                        public void OnItemLongClick(View view, int position) {

                        }
                    });
                }

            }
        };


        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),ReportActivity.class);

                startActivity(intent);

            }
        });

        WifiP2pManager.ConnectionInfoListener mInfoListener = new WifiP2pManager.ConnectionInfoListener() {

            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo minfo) {

                Log.i("bizzmark", "InfoAvailable is on");
                // Toast.makeText(getApplicationContext(),"ConnectionInfoListener onConnectionInfoAvailable.",Toast.LENGTH_SHORT).show();

                info = minfo;
            }
        };

        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this, mPeerListListerner, mInfoListener);
    }

    public void getTxtView() {

        //Toast.makeText(getApplicationContext(),txtView.getText().toString(),Toast.LENGTH_LONG).show();
    }

    /*A demo base on API which you can connect android device by wifidirect,
    and you can send file or data by socket,what is the most important is that you can set
    which device is the client or service.*/

    private void createConnect(String address) {

        WifiP2pConfig config = initWifiP2pConfig(address);

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

                sendAck();
            }

            @Override
            public void onFailure(int reason) {

                Toast.makeText(getApplicationContext(),"WifiP2pManager connect failure. Reason: " + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Initialize P2PConfiguration.
     */
    private WifiP2pConfig initWifiP2pConfig(String address) {

        WifiP2pConfig config = null;

        try {
            // WifiP2pDevice device;
            config = new WifiP2pConfig();
            Log.i("bizzmark", address);

            config.deviceAddress = address;

            config.wps.setup = WpsInfo.PBC;

            // Acknowledgement so not group owner.
            config.groupOwnerIntent = 0;
        } catch (Throwable th) {
            th.printStackTrace();
        }

        return config;
    }

    private void sendAck() {

        try {

            if(null == info) {
               return;
            }

            // Send msg to seller.
            Intent serviceIntent = new Intent(this, DataTransferService.class);
            serviceIntent.setAction(DataTransferService.ACTION_SEND_DATA);
            serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_ADDRESS, info.groupOwnerAddress.getHostAddress());

            serviceIntent.putExtra(DataTransferService.MESSAGE, jsonACK);

            Log.i("bizzmark", "owenerip is " + info.groupOwnerAddress.getHostAddress());
            serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_PORT, 9999);
            // Start service.
            startService(serviceIntent);

        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void initEvents() {

        btnRefresh.setOnClickListener(this);

        discoverPeers();
    }

    private void discoverPeers() {

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                 Toast.makeText(getApplicationContext(),"WifiP2pManager.discoverPeers success.",Toast.LENGTH_SHORT).show();
                Log.i("Connection  :  ","Sucesssss");
            }

            @Override
            public void onFailure(int reason) {

                // Wifi disabled, Enable.
                if(2 == reason) {

                    // Toast.makeText(getApplicationContext(),"Enabling wifi.", Toast.LENGTH_SHORT).show();
                    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                }
                Toast.makeText(getApplicationContext(),"WifiP2pManager discoverPeers failure. Reason: " + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void StopConnect() {

        // SetButtonGone();
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();
        registerReceiver(mReceiver, mFilter);

        ResetReceiver();
        discoverPeers();
    }

    @Override
    public void onPause() {

        super.onPause();
        Log.i("bizzmark", "on pause.");
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StopConnect();
    }

    public void ResetReceiver() {

        unregisterReceiver(mReceiver);

        // deletePersistentGroups();
        registerReceiver(mReceiver, mFilter);
    }

    private void deletePersistentGroups(){
        //WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            Method[] methods = WifiP2pManager.class.getMethods();
            for (int i = 0; i < methods.length; i++) {
                if (methods[i].getName().equals("deletePersistentGroup")) {
                    // Delete any persistent group
                    for (int netid = 0; netid < 32; netid++) {
                        methods[i].invoke(mManager, mChannel, netid, null);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View refresh) {

        Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.buttonrotate);
        rotation.start();
        refresh.startAnimation(rotation);

            /*ResetReceiver();
            discoverPeers();*/
        finish();
        Intent intent = new Intent(getApplicationContext(),WifiDirectReceive.class);
        startActivity(intent);

        // New code Start.
/*        */

    }

    private void saveDataToFireBase() {
        try {

            Gson gson = new Gson();
            PointsBO points = new PointsBO("Earn", "2000", "storeexample", "200", "DeviceId", "0", "08/12/2016");
            String result = gson.toJson(points);

            Log.i("bizzmark", "data on post execute.Result: " + points.getPoints());
            String type = points.getType().toString();

            if(type.equalsIgnoreCase("Earn")){
                Intent intent = new Intent(this,EarnPoints.class);
                intent.putExtra("earnRedeemString", result);
                startActivity(intent);
            }else if (type.equalsIgnoreCase("Redeem")){
                Intent intent = new Intent(this,RedeemPoints.class);
                intent.putExtra("earnRedeemString", result);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //StopConnect();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.share) {

               /*deletePersistentGroups();
            StopConnect();
            peers.clear();
            peersshow.clear();
            mAdapter = new WifiAdapter(peersshow);
            mRecyclerView.setAdapter(mAdapter);

            discoverPeers();
            info = null;
*/
            WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            for (WifiConfiguration currentConfiguration : wifiManager.getConfiguredNetworks()) {
                wifiManager.removeNetwork(currentConfiguration.networkId);
            }

            Toast.makeText(getApplicationContext(), "Connection Removed !", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        } else {

            firebaseAuth.signOut();
            finish();
            Intent in = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(in);
            return super.onOptionsItemSelected(item);
        }
    }


    private void shareButtonFunctionality(){
        try {

            ApplicationInfo app = getApplicationContext().getApplicationInfo();
            String filePath = app.sourceDir;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
            startActivity(Intent.createChooser(intent, "Share app"));
            Toast.makeText(getApplicationContext(),"Share the Seller App...",Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
