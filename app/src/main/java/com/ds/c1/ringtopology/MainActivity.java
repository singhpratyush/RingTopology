package com.ds.c1.ringtopology;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ds.c1.ringtopology.adapter.DevicesAdapter;
import com.ds.c1.ringtopology.network.NetworkListener;
import com.ds.c1.ringtopology.ricartagrawala.RicartAgrawala;
import com.ds.c1.ringtopology.ring.RingManager;
import com.ds.c1.ringtopology.tokenpassing.TokenManager;
import com.ds.c1.ringtopology.utils.IPAddress;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView textIPAddr;
    Button addIPButton;
    EditText addIPText;
    RecyclerView devicesRV;
    public static RecyclerView.Adapter devicesRVAdapter;
    RecyclerView.LayoutManager devicesRVLayoutManager;
    static TextView connectionCountSent, connectionCountRecv;
    static int numConnectionsSent = 0, numConnectionsRecv = 0;
    public static Activity mainActivity;
    static TextView tokenDisplay;
    Button tokenStartButton;
    public static Button requestCriticalSectionButton;
    public static TextView criticalSectionStatus;
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

        // Token actions
        tokenDisplay = (TextView) findViewById(R.id.current_token_value);
        tokenDisplay.setText("Token: Null");
        tokenStartButton = (Button) findViewById(R.id.start_token_passing_button);
        tokenStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tokenDisplay.setText("Token: 0");
                try {
                    TokenManager.startTokenPassing();
                } catch (JSONException | InterruptedException e) {
                    Log.e(logName, "Error while starting token passing", e);
                }
            }
        });

        // Critical section
        requestCriticalSectionButton = (Button) findViewById(R.id.request_critical_section);
        criticalSectionStatus = (TextView) findViewById(R.id.critical_status);
        requestCriticalSectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    RicartAgrawala.requestCriticalSection();
                } catch (JSONException | InterruptedException e) {
                    Log.e(logName, "Unable to enter critical section", e);
                }
            }
        });

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

    public static void setToken(final int value) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tokenDisplay.setText("Token: " + value);
            }
        });
    }

    public static void toast(final String text) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainActivity,
                        text,
                        Toast.LENGTH_SHORT).show();
            }
        });
        return;
    }

    public static void markCriticalSectionPending() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                criticalSectionStatus.setBackgroundColor(Color.BLUE);
                requestCriticalSectionButton.setEnabled(false);
            }
        });
    }

    public static void markCriticalSectionEnter() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                criticalSectionStatus.setBackgroundColor(Color.RED);
            }
        });
    }

    public static void markCriticalSectionLeave() {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                criticalSectionStatus.setBackgroundColor(Color.GREEN);
                requestCriticalSectionButton.setEnabled(true);
            }
        });
    }

}
