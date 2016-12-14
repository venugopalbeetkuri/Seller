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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.example.R;
import com.example.db.PointsBO;
import com.example.sellerapp.EarnPoints;
import com.example.sellerapp.RedeemPoints;
import com.example.sellerapp.ReportActivity;
import com.example.util.Utility;
import com.example.wifidirect.Adapter.WifiAdapter;
import com.example.wifidirect.BroadcastReceiver.WifiDirectBroadcastReceiver;
import com.example.wifidirect.Service.DataTransferService;
import com.example.wifidirect.Task.DataServerAsyncTask;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
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

    // For peers information.
    private List<HashMap<String, String>> peersshow = new ArrayList();

    // All the peers.
    private List peers = new ArrayList();
    private TextView pointsGiven, totalsale, totaldiscount;

    boolean sendAck = false;
    String jsonACK = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_direct_client);

        info = null;

        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
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

       //String storeName = getStoreName();


        Utility.calculateTotal("xyz");
        report = (Button) findViewById(R.id.report);

        updatePoints();

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



    }

    private void updatePoints() {
        try {

            Integer totalPoints = Utility.totalEarnPoints - Utility.totalRedeemPoints;
            Integer totalBillAmount = Utility.totalBillAmount;
            Integer totalDiscountmount = Utility.totalDiscount;

            pointsGiven = (TextView) findViewById(R.id.pointsgiven);
            totalsale = (TextView) findViewById(R.id.totalsale);
            totaldiscount=(TextView) findViewById(R.id.discountgiven);

            pointsGiven.setText(totalPoints.toString());
            totalsale.setText(totalBillAmount.toString());
            totaldiscount.setText(totalDiscountmount.toString());

            Utility.updateReference(pointsGiven, totalsale, totaldiscount);


        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initView() {

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
                // Toast.makeText(getApplicationContext(),"WifiP2pManager.discoverPeers success.",Toast.LENGTH_SHORT).show();
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

        /*if(Utility.isTesting()) {

            saveDataToFireBase();
        } else {*/

        // New code Start.
        peers.clear();
        peersshow.clear();
        mAdapter = new WifiAdapter(peersshow);
        mRecyclerView.setAdapter(mAdapter);

        // ResetReceiver();
        discoverPeers();
        info = null;

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        //}
    }

    private void saveDataToFireBase() {
        try {

            Gson gson = new Gson();
            PointsBO points = new PointsBO("Earn", "2000", "venu-xyz", "200", "xyz", "0", "macId");
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

            if(Utility.isTesting()) {
                deletePersistentGroups();
            } else {
                shareButtonFunctionality();
            }
            return super.onOptionsItemSelected(item);
        } else {

            firebaseAuth.signOut();
            finish();
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
