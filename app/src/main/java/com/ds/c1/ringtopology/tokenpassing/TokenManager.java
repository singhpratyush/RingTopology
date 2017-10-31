package com.ds.c1.ringtopology.tokenpassing;

import com.ds.c1.ringtopology.MainActivity;
import com.ds.c1.ringtopology.adapter.DevicesAdapter;
import com.ds.c1.ringtopology.network.NetworkSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ThreadLocalRandom;

public class TokenManager {
    public static void startTokenPassing() throws JSONException, InterruptedException {
        sendToken(0);
    }

    public static void sendToken(int value) throws JSONException, InterruptedException {
        long sleepTime = ThreadLocalRandom.current().nextLong(2000, 8000);
        JSONObject message = new JSONObject();
        message.put("token", value);
        String nextIP;
        try {
            nextIP = ((DevicesAdapter) MainActivity.devicesRVAdapter).get(0);
        } catch (IndexOutOfBoundsException e) {
            MainActivity.toast("No device connect to the current device in ring");
            return;
        }
        MainActivity.toast("I have the token for " + sleepTime + "ms!!");
        Thread.sleep(sleepTime);
        NetworkSender.sendMessage(nextIP, "TOKEN", message);
    }
}
