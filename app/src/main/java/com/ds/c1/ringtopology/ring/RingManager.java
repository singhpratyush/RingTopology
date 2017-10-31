package com.ds.c1.ringtopology.ring;


import android.support.v7.widget.RecyclerView;

import com.ds.c1.ringtopology.MainActivity;
import com.ds.c1.ringtopology.adapter.DevicesAdapter;
import com.ds.c1.ringtopology.network.NetworkSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;


public class RingManager {

    private static HashMap<String, Boolean> connectionStatus = new HashMap<>();


    public static void requestConnection(String IP) throws IOException, JSONException, InterruptedException {
        connectionStatus.put(IP, false);
        JSONObject message = new JSONObject();
        NetworkSender.sendMessage(IP, "CONNECT", message);
    }

    public static void acceptConnection(String IP, DevicesAdapter adapter) {
        connectionStatus.put(IP, true);
        adapter.addDevice(IP);
    }

    public static void disconnect(String IP, DevicesAdapter adapter) throws JSONException, InterruptedException {
        JSONObject message = new JSONObject();
        NetworkSender.sendMessage(IP, "DISCONNECT", message);
        adapter.removeDevice(IP);
    }

    public static void acceptDisconnection(String IP, DevicesAdapter adapter) {
        connectionStatus.put(IP, false);
        adapter.removeDevice(IP);
    }

}
