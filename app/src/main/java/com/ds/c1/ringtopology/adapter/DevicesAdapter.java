package com.ds.c1.ringtopology.adapter;


import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ds.c1.ringtopology.R;
import com.ds.c1.ringtopology.ring.RingManager;

import org.json.JSONException;

import java.util.ArrayList;


public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {
    private ArrayList<String> devices;
    private static String logname = "DeviceRecyclerViewAdapter";
    private Activity activity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        Button disconnectButton;
        ViewHolder(View v) {
            super(v);
            this.deviceName = v.findViewById(R.id.devices_rv_name);
            this.disconnectButton = v.findViewById(R.id.devices_rv_disconnect);
        }
    }

    public String get() {
        return this.devices.get(0);
    }

    public DevicesAdapter(Activity act) {
        this.devices = new ArrayList<>();
        this.activity = act;
    }

    public void addDevice(String name) {
        if (!this.devices.contains(name)) {
            this.devices.add(name);
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void removeDevice(String name) {
        this.devices.remove(name);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public DevicesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DevicesAdapter.ViewHolder holder, int position) {
        final String deviceName = this.devices.get(position);
        holder.deviceName.setText(deviceName);
        final DevicesAdapter self = this;
        holder.disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    RingManager.disconnect(deviceName, self);
                } catch (JSONException | InterruptedException e) {
                    Log.e(logname, "Unable to disconnect from " + deviceName, e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.devices.size();
    }
}
