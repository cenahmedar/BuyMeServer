package com.cenah.buymeserver.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cenah.buymeserver.R;
import com.cenah.buymeserver.ViewHolder.MenuViewHolder;
import com.cenah.buymeserver.models.Category;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_login = findViewById(R.id.btn_login);
        TextView tx_appname = findViewById(R.id.tx_appname);
        tx_appname.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/NABILA.TTF"));

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignActivity.class));

            }
        });


    }
}
