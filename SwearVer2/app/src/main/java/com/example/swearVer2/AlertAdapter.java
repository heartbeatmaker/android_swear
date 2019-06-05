package com.example.swearVer2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.AlertViewHolder> {

    private static final String TAG = "AlrtRecyclerViewAdapter";

    private ArrayList<AlertItem> alertItemArrayList;
    private Context mContext;


    public AlertAdapter(Context mContext, ArrayList<AlertItem> alertItemArrayList) {
        this.alertItemArrayList = alertItemArrayList;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public AlertViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alert_item_layout, parent, false);
        AlertViewHolder holder = new AlertViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlertViewHolder holder, final int position) {

        Log.d(TAG, "onBindVieHolder: called.");

        final AlertItem item = alertItemArrayList.get(position);
        holder.content.setText(item.getAlertContent());
        holder.time.setText(item.getAlertTime());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: alert clicked on. position: "+position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return alertItemArrayList.size();
    }


    public class AlertViewHolder extends RecyclerView.ViewHolder{

        TextView content;
        TextView time;
        RelativeLayout parentLayout;

        public AlertViewHolder(@NonNull View itemView) {
            super(itemView);
            this.content = itemView.findViewById(R.id.alertContent);
            this.time = itemView.findViewById(R.id.alertTime);
            this.parentLayout = itemView.findViewById(R.id.alert_parentLayout);
        }
    }







}



