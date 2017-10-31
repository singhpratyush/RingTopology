package com.ds.c1.ringtopology.network;

import android.util.Log;

import com.ds.c1.ringtopology.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static com.ds.c1.ringtopology.utils.IPAddress.getIPAddress;

public class NetworkSender {
    private static String logName = "NetworkSender";

    public static void sendMessage(final String IP, String type, final JSONObject message) throws JSONException, InterruptedException {
        String myIpAddress = getIPAddress(true);
        if (IP.equals(myIpAddress)) {  // Do not send messages to self
            return;
        }
        message.put("type", type);
        message.put("sender", myIpAddress);
        Thread connThread = new Thread() {
            @Override
            public void run() {
                Socket s = null;
                try {
                    s = new Socket(IP, 8800);
                    DataOutputStream outputStream = new DataOutputStream(s.getOutputStream());
                    outputStream.writeUTF(message.toString());
                    outputStream.flush();
                    outputStream.close();
                    s.close();
                    Log.i(logName, "Sent message to " + IP + " : " +  message.toString());
                } catch (IOException e) {
                    Log.e("NetworkSender", "Failed to send the message " + message, e);
                }
            }
        };
        connThread.start();
        connThread.join();
        MainActivity.notifyConnectionSent();
    }
}
