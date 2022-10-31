package com.example.socalbeach4life;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class Login extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://socalbeach4life-2bd0d-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText email = findViewById(R.id.login_email);
        final EditText password = findViewById(R.id.login_password);
        final Button loginButton = findViewById(R.id.login_button);
        final TextView registerNow = findViewById(R.id.register_now);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailText = email.getText().toString();
                final String passwordText = password.getText().toString();

                if(emailText.isEmpty() || passwordText.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter your email and password", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean foundEmail = false;
                            // loop through ids and check if account exists
                            for (DataSnapshot idSnapshot: snapshot.getChildren()) {
                                if(idSnapshot.child("email").getValue(String.class).equals(emailText)) {
                                    // account with this email exists
                                    foundEmail = true;

                                    // get password and match it with user entered password
                                    final String getPassword = idSnapshot.child("password").getValue(String.class);
                                    if(getPassword.equals(passwordText)) { // login success
                                        Toast.makeText(Login.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Login.this, MainActivity.class));
                                        finish();
                                    } else { // wrong password entered
                                        Toast.makeText(Login.this, "Email or password is invalid", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                }
                            }

                            if(!foundEmail) {
                                Toast.makeText(Login.this, "Email or password is invalid", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }
}