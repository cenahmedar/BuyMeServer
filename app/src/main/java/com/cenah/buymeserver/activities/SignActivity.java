package com.cenah.buymeserver.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cenah.buymeserver.R;
import com.cenah.buymeserver.models.Common;
import com.cenah.buymeserver.models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignActivity extends AppCompatActivity {


    private EditText ed_phone, ed_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        FirebaseApp.initializeApp(this);
        ed_phone = findViewById(R.id.ed_phone);
        ed_password = findViewById(R.id.ed_password);

        Button btnLogin = findViewById(R.id.btnLogin);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 200);
        }

        // init firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed_password.getText().toString().isEmpty() || ed_phone.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "enter password and phone number..", Toast.LENGTH_LONG).show();
                    return;
                }

                final ProgressDialog progressDialog = new ProgressDialog(SignActivity.this);
                progressDialog.setMessage("Searching..");
                progressDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        //check if user exists
                        if (dataSnapshot.child(ed_phone.getText().toString()).exists()) {
                            progressDialog.dismiss();
                            //get user info
                            User user = dataSnapshot.child(ed_phone.getText().toString().trim()).getValue(User.class);
                            user.setPhone(ed_phone.getText().toString());

                            if (Boolean.parseBoolean(user.getIsStaff())) {
                                if (user.getPassword().equals(ed_password.getText().toString())) {
                                    Common.currnetUser = user;
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(getApplicationContext(), "Hatalı şifre", Toast.LENGTH_LONG).show();

                                }
                            } else
                                Toast.makeText(getApplicationContext(), "Not an administrator", Toast.LENGTH_LONG).show();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_LONG).show();

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });


    }
}
