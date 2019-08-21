package com.cenah.buymeserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import com.cenah.buymeserver.Interface.ItemClickListener;
import com.cenah.buymeserver.R;
import com.cenah.buymeserver.models.Common;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

    public TextView tx_orderid,tx_order_statu,tx_order_phone,tx_order_adress;

    private ItemClickListener itemClickListner;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        tx_order_adress = itemView.findViewById(R.id.tx_order_adress);
        tx_order_phone = itemView.findViewById(R.id.tx_order_phone);
        tx_order_statu = itemView.findViewById(R.id.tx_order_statu);
        tx_orderid = itemView.findViewById(R.id.tx_orderid);

        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onClick(View v) {
        itemClickListner.onClick(v,getAdapterPosition(),false);

    }

    public void setItemClickListner(ItemClickListener itemClickListner){
        this.itemClickListner = itemClickListner;
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select action");
        menu.add(0,0,getAdapterPosition(), Common.UPDATE);
        menu.add(0,1,getAdapterPosition(), Common.DELETE);

    }
}
