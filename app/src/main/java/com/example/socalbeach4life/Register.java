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

public class Register extends AppCompatActivity {
    // create DatabaseReference object to access realtime database
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

                // check if user filled all fields
                if(nameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || conPasswordText.isEmpty()) {
                    Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } // check if passwords match
                else if(!passwordText.equals(conPasswordText)) {
                    Toast.makeText(Register.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // get next user id
                            final int nextid = snapshot.child("userlastid").getValue(Integer.class) + 1;
                            final String stringnextid = String.valueOf(nextid);

                            boolean accountAlreadyExists = false;

                            DataSnapshot userSnapshot = snapshot.child("users");
                            // loop through ids and check if email already associated with an account
                            for (DataSnapshot idSnapshot: userSnapshot.getChildren()) {
                                if(idSnapshot.child("email").getValue(String.class).equals(emailText)) {
                                    accountAlreadyExists = true;
                                    Toast.makeText(Register.this, "Email is already associated with an account", Toast.LENGTH_SHORT).show();
                                }
                            }

                            if(!accountAlreadyExists) {
                                databaseReference.child("users").child(stringnextid).child("id").setValue(nextid);
                                databaseReference.child("users").child(stringnextid).child("name").setValue(nameText);
                                databaseReference.child("users").child(stringnextid).child("email").setValue(emailText);
                                databaseReference.child("users").child(stringnextid).child("password").setValue(passwordText);
                                databaseReference.child("userlastid").setValue(nextid);
                                Toast.makeText(Register.this, "Account registration success", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

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
}