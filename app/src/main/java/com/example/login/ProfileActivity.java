package com.example.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    // Firebase auth object.
    private FirebaseAuth firebaseAuth;

    // View objects.
    private TextView textViewUserEmail;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initializing firebase authentication object.
        firebaseAuth = FirebaseAuth.getInstance();

        // If the user is not logged in that means current user will return null.
        if (firebaseAuth.getCurrentUser() == null) {

            // Closing this activity.
            finish();

            // Starting login activity.
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Getting current user.
        FirebaseUser user = firebaseAuth.getCurrentUser();

        // Initializing views.
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        // Displaying logged in user name.
        textViewUserEmail.setText("Welcome " + user.getEmail());

        // Adding listener to button.
        buttonLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        // If logout is pressed.
        if (view == buttonLogout) {

            // Logging out the user.
            firebaseAuth.signOut();

            // Closing activity.
            finish();

            // Starting login activity.
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
