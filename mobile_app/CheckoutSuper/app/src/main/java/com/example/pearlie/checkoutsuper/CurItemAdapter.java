package com.example.pearlie.checkoutsuper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import co.dift.ui.SwipeToAction;

/**
 * Created by nicolasjoukhdar on 18/10/17.
 */

public class CurItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SCEntry> entries;

    public class CurItemHolder extends SwipeToAction.ViewHolder<SCEntry>{

        public TextView item;

        public CurItemHolder(View v) {
            super(v);
            item = (TextView) v.findViewById(R.id.title);
        }
    }

    public CurItemAdapter(List<SCEntry> entries) {
        this.entries = entries;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);

        return new CurItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SCEntry item = entries.get(position);
        CurItemHolder vh = (CurItemHolder) holder;
        vh.item.setText(item.getItem_name() + "\nPrice = " + item.getPrice() + "x" + item.getQuantity());
        vh.data = item;
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
}
