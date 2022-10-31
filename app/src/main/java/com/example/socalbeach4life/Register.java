package com.example.socalbeach4life;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import android.util.Log;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

public class Register extends AppCompatActivity {
    // create DatabaseReference object to access realtime database
//    FirebaseDatabase database = FirebaseDatabase.getInstance("https://socalbeach4life-2bd0d-default-rtdb.firebaseio.com/");
//    DatabaseReference usersRef = database.getReference("users");
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://socalbeach4life-2bd0d-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText name = findViewById(R.id.register_name);
        final EditText email = findViewById(R.id.register_email);
        final EditText password = findViewById(R.id.register_password);
        final EditText conPassword = findViewById(R.id.register_con_password);

        final Button registerButton = findViewById(R.id.register_button);
        final TextView loginNow = findViewById(R.id.login_now);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get data from text edit fields
                final String nameText = name.getText().toString();
                final String emailText = email.getText().toString();
                final String passwordText = password.getText().toString();
                final String conPasswordText = conPassword.getText().toString();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("message");

                myRef.setValue("Hello, World!!!");
                System.out.println("value settt");

                // check if user filled all fields
                if(nameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || conPasswordText.isEmpty()) {
                    Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } // check if passwords match
                else if(!passwordText.equals(conPasswordText)) {
                    Toast.makeText(Register.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                } else {

                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            System.out.println("here");
                            // check if email is not already registered
                            if(dataSnapshot.hasChild(emailText)) {
                                System.out.println("sending data to firebase realtime database");
                                Toast.makeText(Register.this, "Email is already associated with an account", Toast.LENGTH_SHORT).show();
                            } else { // send data to firebase realtime database
                                System.out.println("sending data to firebase realtime database");
                                 //email is unique identifier for each user
                                    databaseReference.child("message").setValue("bruh");
                                databaseReference.child("users").child(emailText).child("name").setValue(nameText);
                                databaseReference.child("users").child(emailText).child("password").setValue(passwordText);
                                databaseReference.child("users").child(emailText).child("trips").setValue(null);
                                databaseReference.child("users").child(emailText).child("review").setValue(null);

                                 //show success message for account registration
                                Toast.makeText(Register.this, "Account registration success", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
}