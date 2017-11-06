package com.ds.c1.ringtopology.tokenpassing;

import com.ds.c1.ringtopology.MainActivity;
import com.ds.c1.ringtopology.adapter.DevicesAdapter;
import com.ds.c1.ringtopology.network.NetworkSender;
import com.ds.c1.ringtopology.ring.RingManager;

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
        MainActivity.toast("I have the token for " + sleepTime + "ms!!");
        Thread.sleep(sleepTime);
        RingManager.toNextNode("TOKEN", message);
    }
}
