package com.hexacta.trackonline.users;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hexacta.trackonline.R;

public class ListOnlineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
  public TextView txtEmail;

  ItemClickListener itemClickListener;

  public ListOnlineViewHolder(View itemView) {
    super(itemView);
    itemView.setOnClickListener(this);
    txtEmail = (TextView) itemView.findViewById(R.id.txt_email);
  }

  public void setItemClickListener(ItemClickListener itemClickListener) {
    this.itemClickListener = itemClickListener;
  }

  @Override

  public void onClick(View view) {
    itemClickListener.onClick(view, getAdapterPosition());
  }
}
