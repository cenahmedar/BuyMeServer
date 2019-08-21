package com.cenah.buymeserver.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cenah.buymeserver.Interface.ItemClickListener;
import com.cenah.buymeserver.Maps.TrakingOrderActivity;
import com.cenah.buymeserver.R;
import com.cenah.buymeserver.ViewHolder.OrderViewHolder;
import com.cenah.buymeserver.models.Common;
import com.cenah.buymeserver.models.Request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderStatusActivity extends AppCompatActivity {


    private RecyclerView recyclerView;

    private DatabaseReference requests;

    private FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    private MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.recyclerView);


        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        laodOrderlist(Common.currnetUser.getPhone());
    }

    private void laodOrderlist(String phone) {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class, R.layout.order_item, OrderViewHolder.class, requests.orderByChild("phone").equalTo(phone)) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, int position) {
                viewHolder.tx_orderid.setText(adapter.getRef(position).getKey());
                viewHolder.tx_order_adress.setText(model.getAddress());
                viewHolder.tx_order_phone.setText(model.getPhone());
                viewHolder.tx_order_statu.setText(convertCodeToSatus(model.getStatus()));

                viewHolder.setItemClickListner(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLong) {
                        Intent intent = new Intent(OrderStatusActivity.this, TrakingOrderActivity.class);
                        Common.currentRquest = model;
                        startActivity(intent);
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private String convertCodeToSatus(String status) {

        switch (status) {
            case "0":
                return "was given";
            case "1":
                return "on the way";
            default:
                return "shipped";
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.DELETE)) {
            deleteCat(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
            Toast.makeText(OrderStatusActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();

        } else if (item.getTitle().equals(Common.UPDATE)) {

            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }

    private void deleteCat(String key, Request item) {
        requests.child(key).removeValue();
    }

    private void showUpdateDialog(String key, final Request item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatusActivity.this);
        alertDialog.setTitle("Update order");
        alertDialog.setMessage("Select status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order,null);

        spinner = view.findViewById(R.id.spinner);
        spinner.setItems("was given","on the way","shipped");

        alertDialog.setView(view);

        final String localKey = key;
        alertDialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));

                requests.child(localKey).setValue(item);
            }
        });

        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();


    }


}
