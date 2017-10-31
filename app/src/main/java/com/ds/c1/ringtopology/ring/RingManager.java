package com.ds.c1.ringtopology.ring;


import android.widget.Toast;

import com.ds.c1.ringtopology.MainActivity;
import com.ds.c1.ringtopology.adapter.DevicesAdapter;
import com.ds.c1.ringtopology.network.NetworkSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class RingManager {


    public static void requestConnection(String IP) throws IOException, JSONException, InterruptedException {
        JSONObject message = new JSONObject();
        NetworkSender.sendMessage(IP, "CONNECT", message);
    }

    public static void acceptConnection(String IP, DevicesAdapter adapter) {
        if (adapter.getItemCount() >= 1) {
            MainActivity.mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.mainActivity,
                            "Already connected to next device in network",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        adapter.addDevice(IP);
    }

    public static void disconnect(String IP, DevicesAdapter adapter) throws JSONException, InterruptedException {
        JSONObject message = new JSONObject();
        NetworkSender.sendMessage(IP, "DISCONNECT", message);
        adapter.removeDevice(IP);
    }

    public static void acceptDisconnection(String IP, DevicesAdapter adapter) {
//        adapter.removeDevice(IP);
    }

}
