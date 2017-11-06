package com.ds.c1.ringtopology.ricartagrawala;

import android.util.Log;

import com.ds.c1.ringtopology.MainActivity;
import com.ds.c1.ringtopology.adapter.DevicesAdapter;
import com.ds.c1.ringtopology.network.NetworkSender;
import com.ds.c1.ringtopology.ring.RingManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.ds.c1.ringtopology.utils.IPAddress.getIPAddress;

public class RicartAgrawala {

    public static Long requestTime = null;
    private static String logName = "RicartAgrawala";

    public static boolean requestingCriticalSection() {
        return requestTime != null;
    }

    public static void requestCriticalSection() throws JSONException, InterruptedException {
        if (requestingCriticalSection()) {
            return;
        }
        MainActivity.markCriticalSectionPending();
        JSONObject message = new JSONObject();
        long time =  System.currentTimeMillis();
        message.put("time", time);
        message.put("crit_sender", getIPAddress(true));
        message.put("addresses", new JSONArray());
        RingManager.toNextNode("CRIT_REQ", message);
        RequestQueue.add(getIPAddress(true), time);
        requestTime = time;
    }

    public static void handleRequest(String from, long time) throws JSONException, InterruptedException {
        if (!requestingCriticalSection()) {
            sendReply(from);
        } else if (requestTime > time) {
            sendReply(from);
        } else {  // Add him to queue
            RequestQueue.add(from, time);
        }
    }

    public static void sendReply(String to) throws JSONException, InterruptedException {
        JSONObject message = new JSONObject();
        message.put("to", to);
        message.put("crit_sender", getIPAddress(true));
        RingManager.toNextNode("CRIT_REPLY", message);
    }

    public static void enterCriticalSection() {
        MainActivity.markCriticalSectionEnter();
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Log.e(logName, "Failed to execute critical section");
        }
        leaveCriticalSection();
    }

    private static void leaveCriticalSection() {
        while (!RequestQueue.isEmpty()) {
            try {
                sendReply(RequestQueue.pop());
            } catch (InterruptedException e) {
                Log.e(logName, "Network error while sending reply", e);
            } catch (JSONException e) {
                Log.e(logName, "JSON parsing error while sending reply", e);
            }
        }
        requestTime = null;
        MainActivity.markCriticalSectionLeave();
    }
}
