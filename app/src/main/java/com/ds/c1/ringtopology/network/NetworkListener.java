package com.ds.c1.ringtopology.network;


import android.util.Log;

import com.ds.c1.ringtopology.MainActivity;
import com.ds.c1.ringtopology.adapter.DevicesAdapter;
import com.ds.c1.ringtopology.ricartagrawala.PendingList;
import com.ds.c1.ringtopology.ricartagrawala.RequestQueue;
import com.ds.c1.ringtopology.ricartagrawala.RicartAgrawala;
import com.ds.c1.ringtopology.ring.RingManager;
import com.ds.c1.ringtopology.tokenpassing.TokenManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static com.ds.c1.ringtopology.utils.IPAddress.getIPAddress;

public class NetworkListener {

    private ServerSocket listenerSocketServer;
    private Socket socket;
    private DevicesAdapter adapter;
    private Thread serverListenerThread;
    private String logName = "Socket Listener Server";
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
//                                    RingManager.acceptConnection(senderIP, adapter);
                                    break;
                                case "CONNECT_REPLY":
                                    RingManager.acceptConnection(senderIP, adapter);
                                    break;
                                case "DISCONNECT":
                                    RingManager.acceptDisconnection(senderIP, adapter);
                                    break;
                                case "TOKEN":
                                    int token = root.getInt("token");
                                    MainActivity.setToken(token);
                                    TokenManager.sendToken(token + 1);
                                    MainActivity.setToken(token + 1);
                                    break;
                                case "CRIT_REQ":
                                    String sendingUser = root.getString("crit_sender");
                                    if (!getIPAddress(true).equals(sendingUser)) {
                                        // Add to request queue or send reply
                                        long time = root.getLong("time");
                                        RicartAgrawala.handleRequest(sendingUser, time);

                                        // Transfer to next nodes
                                        if (RicartAgrawala.requestingCriticalSection()) {
                                            JSONArray addresses = root.getJSONArray("addresses");
                                            addresses.put(getIPAddress(true));
                                            root.put("addresses", addresses);
                                        }
                                        RingManager.toNextNode("CRIT_REQ", root);
                                    } else {  // I had  sent it
                                        // Add all to pending list
                                        JSONArray addresses = root.getJSONArray("addresses");
                                        if (addresses.length() == 0) {  // Nobody else was requesting
                                            RicartAgrawala.enterCriticalSection();
                                        }
                                        else {
                                            for (int i = 0; i < addresses.length(); i++) {
                                                PendingList.add(addresses.getString(i));
                                            }
                                        }
                                    }
                                    break;
                                case "CRIT_REPLY":
                                    String toUser = root.getString("to");
                                    if (getIPAddress(true).equals(toUser)) {  // It was for me
                                        PendingList.remove(senderIP);
                                    } else {  // Not for me
                                        String critSender = root.getString("crit_sender");
                                        if (!getIPAddress(true).equals(critSender)) {  // I didn't send it
                                            // pass it on
                                            RingManager.toNextNode("CRIT_REPLY", root);
                                        } else {
                                            // It was not for me but I had sent it
                                            // Break the flow, the node wasn't found
                                        }
                                    }
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
