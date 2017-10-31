package com.ds.c1.ringtopology;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ds.c1.ringtopology.adapter.DevicesAdapter;
import com.ds.c1.ringtopology.network.NetworkListener;
import com.ds.c1.ringtopology.ring.RingManager;
import com.ds.c1.ringtopology.utils.IPAddress;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView textIPAddr;
    Button addIPButton;
    EditText addIPText;
    RecyclerView devicesRV;
    RecyclerView.Adapter devicesRVAdapter;
    RecyclerView.LayoutManager devicesRVLayoutManager;
    static TextView connectionCountSent, connectionCountRecv;
    static int numConnectionsSent = 0, numConnectionsRecv = 0;
    static Activity mainActivity;
    static String logName = "Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Devices list
        addIPButton = (Button) findViewById(R.id.connect_to_ip_button);
        addIPText =  (EditText) findViewById(R.id.connect_to_ip);
        devicesRV = (RecyclerView) findViewById(R.id.devices_rv);

        // Show IP address
        textIPAddr = new TextView(this);
        textIPAddr = (TextView) findViewById(R.id.ip_addr);
        String ipAddr = IPAddress.getIPAddress(true);
        textIPAddr.setText("My IP Address: " + ipAddr);

        // Connection Count
        connectionCountSent = (TextView) findViewById(R.id.conn_sent);
        connectionCountRecv = (TextView) findViewById(R.id.conn_received);
        connectionCountSent.setText("Sent: 0");
        connectionCountRecv.setText("Received: 0");


        // What happens on clicking the connect button
        addIPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String IP = String.valueOf(addIPText.getText());
                try {
                    RingManager.requestConnection(IP);
                } catch (IOException | JSONException | InterruptedException e) {
                    Log.e(logName, "Failed to connect to device", e);
                }
            }
        });

        // Recycler view for showing devices
        devicesRVAdapter = new DevicesAdapter(this);
        devicesRVLayoutManager = new LinearLayoutManager(this);
        devicesRV.setLayoutManager(devicesRVLayoutManager);
        devicesRV.setAdapter(devicesRVAdapter);

        // Start network listener
        NetworkListener listener = new NetworkListener();
        listener.setDevicesAdapter((DevicesAdapter) devicesRVAdapter);
        listener.start();

        // Activity
        mainActivity = this;
    }

    public static void notifyConnectionSent() {
        numConnectionsSent += 1;
        notifyConnection();
    }

    public static void notifyConnectionReceived() {
        numConnectionsRecv += 1;
        notifyConnection();
    }

    public static void notifyConnection() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connectionCountSent.setText("Sent: " + numConnectionsSent);
                connectionCountRecv.setText("Received: " + numConnectionsRecv);
            }
        });

    }
}
