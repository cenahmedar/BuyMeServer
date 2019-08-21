package com.cenah.buymeserver.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cenah.buymeserver.Interface.ItemClickListener;
import com.cenah.buymeserver.R;
import com.cenah.buymeserver.models.Common;


public class ProductsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

    public TextView tx_name,tx_about,tx_price,tx_descount;
    public ImageView iv_image;
    private ItemClickListener itemClickListner;


    public ProductsViewHolder(@NonNull View itemView) {
        super(itemView);

        tx_name = itemView.findViewById(R.id.tx_name);
        tx_about = itemView.findViewById(R.id.tx_about);
        iv_image = itemView.findViewById(R.id.iv_image);
        tx_price = itemView.findViewById(R.id.tx_price);
        tx_descount = itemView.findViewById(R.id.tx_descount);

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
        menu.add(0,0,getAdapterPosition(), Common.DELETE);
        menu.add(0,1,getAdapterPosition(), Common.UPDATE);
    }


}
