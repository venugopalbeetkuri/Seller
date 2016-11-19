package com.example.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.R;
import com.example.sellerapp.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity_login extends AppCompatActivity implements View.OnClickListener {

    private static final String MY_PREFS_NAME = "my_data";

    // Defining view objects.
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup, google_signin;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);

        // Initializing firebase auth object.
        firebaseAuth = FirebaseAuth.getInstance();

        // If getCurrentUser does not returns null.
        if (firebaseAuth.getCurrentUser() != null) {


            // That means user is already logged in so close this activity and open profile activity.
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            //and open profile activity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        // Initializing views.
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignup = (Button) findViewById(R.id.buttonSignup);

        progressDialog = new ProgressDialog(this);

        // Attaching listener to button.
        buttonSignup.setOnClickListener(this);
    }

    private void registerUser() {

        // Getting email and password from edit texts.
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Checking if email and passwords are empty.
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        // If the email and password are not empty displaying a progress dialog
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        // Creating a new user.
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Checking if success.
                        if (task.isSuccessful()) {
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {

                            // Display some message here.
                            Toast.makeText(MainActivity_login.this, "Registration Error", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View view) {

        if (view == buttonSignup) {
            registerUser();

            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("mailid", editTextEmail.getText().toString().trim());
            editor.putString("password", editTextPassword.getText().toString().trim());
            editor.apply();
        }

        if (view == google_signin) {

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String restoredText = prefs.getString("text", null);
            if (restoredText != null) {

                String mail = prefs.getString("mailid", "default");
                String pw = prefs.getString("password", "default");

                System.out.println(mail);
                System.out.println(pw);
            }
        }

        if (view == textViewSignin) {

            // Open login activity when user taps on the already registered textview.
            startActivity(new Intent(this, LoginActivity.class));
        }

    }
}