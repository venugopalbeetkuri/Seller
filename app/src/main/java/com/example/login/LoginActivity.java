package com.example.login;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.R;
import com.example.db.StoreBO;
import com.example.wifidirect.WifiDirectReceive;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String LOGIN_CRADINTIALS = "LOGIN_CRADINTIALS";
    private static final String MY_PREFS_NAME = "";
    // defining views
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignup;
    StoreBO storeBO;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private TextView policy,terms;
    //firebase auth object
    private FirebaseAuth firebaseAuth;
    private SignInButton mGoogleLoginButton;
    //progress dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Getting firebase auth object.
        firebaseAuth = FirebaseAuth.getInstance();

        // if the objects getcurrentuser method is not null
        //means user is already logged in
        if (firebaseAuth.getCurrentUser() != null) {
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), WifiDirectReceive.class));
        }

        // initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        textViewSignup = (TextView) findViewById(R.id.textViewSignUp);
        policy =(TextView)findViewById(R.id.policy);
        terms = (TextView) findViewById(R.id.terms);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog custom = new Dialog(LoginActivity.this);
                custom.setTitle("CUSTOM DIALOG");
                custom.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //custom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                custom.setContentView(R.layout.terms);
                //custom.setCancelable(false);
                custom.setCanceledOnTouchOutside(false);
                custom.show();
            }
        });
        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog custom = new Dialog(LoginActivity.this);
                custom.setTitle("CUSTOM DIALOG");
                custom.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //custom.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                custom.setContentView(R.layout.policy);
                //custom.setCancelable(false);
                custom.setCanceledOnTouchOutside(false);
                custom.show();
            }
        });
        progressDialog = new ProgressDialog(this);

        //attaching click listener
        buttonSignIn.setOnClickListener(this);
        textViewSignup.setOnClickListener(this);
    }



    //method for user login
    private void userLogin() {
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(300);
            Toast.makeText(this, "Please enter Login ID", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(300);
            Toast.makeText(this, "Please enter Login Password", Toast.LENGTH_LONG).show();
            return;
        }

        // If the email and password are not empty displaying a progress dialog
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        // Logging in the user.
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();
                        //storeBO = new StoreBO(storeEmail, storeName, percentage);
                        // If the task is successful.
                        if (task.isSuccessful()) {
                            // start the profile activity finish();
                            editTextEmail.setText("");
                            editTextPassword.setText("");
                            finish();
//************************************************************************
                            /*try {
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

                                                String EmailidDB = timeStampKey.get("emailId");
                                                Log.i("Store Name from DB", EmailidDB);
                                                Log.i("Local Email Id ",email);
                                                if (EmailidDB.equalsIgnoreCase(email)) {
                                                    Log.i("Email ID Equals :", EmailidDB + " ~ " + email);
                                                    String Per = timeStampKey.get("percentage");
                                                    Log.i("Found Percentage : ", Per);
                                                    storeBO.setPercentage(Per);
                                                    found=true;
                                                    Toast.makeText(getApplicationContext(), " Retrived Percentage : " + Per, Toast.LENGTH_LONG).show();
                                                    Log.i("Retrived Percentage : ", Per);
                                                    Log.i("Percentage from Bo ",storeBO.getPercentage());
                                                    print(Per);
                                                }
                                            }
                                            if (!found) {
                                                Log.i("Retrive Status : ", "Not Found");
                                    *//*StoreBO store = new StoreBO(storeEmail, storeName, percentage);
                                    String formattedDate = df.format(c.getTime());
                                    DatabaseReference time = storeDatabase.child(formattedDate);
                                    time.setValue(store);*//*
                                            }
                                            //calculate(percentage);
                                        }

                                *//*private void calculate(int percent) {
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
                            }*/
//************************************************************************
                            startActivity(new Intent(getApplicationContext(), WifiDirectReceive.class));
                        } else {

                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            v.vibrate(500);
                            Toast.makeText(getApplicationContext(),"Please enter your valid Email and Password",Toast.LENGTH_LONG).show();
                        }
                    }
        });

    }

    @Override
    public void onClick(View view) {

        if (view == buttonSignIn) {
            userLogin();
        }

        if (view == textViewSignup) {
            // finish();
            //startActivity(new Intent(this, MainActivity_login.class));
        }
    }

}

