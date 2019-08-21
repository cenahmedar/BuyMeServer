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

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener ,View.OnCreateContextMenuListener{
    public TextView menu_name;
    public ImageView menu_image;
    private ItemClickListener itemClickListner;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);

        menu_name = itemView.findViewById(R.id.menu_name);
        menu_image = itemView.findViewById(R.id.menu_image);

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
