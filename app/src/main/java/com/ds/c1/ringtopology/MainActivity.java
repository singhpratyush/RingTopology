package com.ds.c1.ringtopology;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ds.c1.ringtopology.utils.IPAddress;

public class MainActivity extends AppCompatActivity {

    TextView textIPAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show IP address
        textIPAddr = new TextView(this);
        textIPAddr = (TextView) findViewById(R.id.ip_addr);
        String ipAddr = IPAddress.getIPAddress(true);
        textIPAddr.setText("IP Address: " + ipAddr);
    }
}
