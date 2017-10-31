package com.ds.c1.ringtopology.network;


import android.util.Log;

import com.ds.c1.ringtopology.MainActivity;
import com.ds.c1.ringtopology.adapter.DevicesAdapter;
import com.ds.c1.ringtopology.ring.RingManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkListener {

    private ServerSocket listenerSocketServer;
    private Socket socket;
    private DevicesAdapter adapter;
    Thread serverListenerThread;
    public String logName = "Socket Listener Server";
    private boolean shallRun;

    public NetworkListener() {
        shallRun = true;
        this.serverListenerThread = new Thread() {
            @Override
            public void run() {
                try {
                    listenerSocketServer = new ServerSocket(8800);
                    while (shallRun) {
                        try {
                            socket = listenerSocketServer.accept();
                            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                            String request = inputStream.readUTF();
                            JSONObject root = new JSONObject(request);
                            String type = root.getString("type");
                            String senderIP = root.getString("sender");
                            Log.i(logName, "Received message from " + senderIP + " : " + root);
                            MainActivity.notifyConnectionReceived();
                            switch (type) {
                                case "CONNECT":
                                    JSONObject message = new JSONObject();
                                    message.put("response", "OK");
                                    NetworkSender.sendMessage(senderIP, "CONNECT_REPLY", message);
                                    RingManager.acceptConnection(senderIP, adapter);
                                    break;
                                case "CONNECT_REPLY":
                                    RingManager.acceptConnection(senderIP, adapter);
                                    break;
                                case "DISCONNECT":
                                    RingManager.acceptDisconnection(senderIP, adapter);
                                    break;
                            }
                        } catch (IOException e) {
                            Log.e(logName, "Failed to receive object", e);
                        } catch (JSONException e) {
                            Log.e(logName, "Unable to parse JSON object", e);
                        } catch (InterruptedException e) {
                            Log.e(logName, "Interrupted while receiving", e);
                        }
                    }
                } catch (IOException e) {
                    Log.e(logName, "Error while starting listener", e);
                }
            }
        };
    }

    public void setDevicesAdapter(DevicesAdapter adapter) {
        this.adapter = adapter;
    }

    public void start() {
        this.serverListenerThread.start();
    }

    public void stop() {
        this.shallRun = false;
    }
}
