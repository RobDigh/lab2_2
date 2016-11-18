package com.example.robert.lab2_2;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class MainActivity extends AppCompatActivity {

    TextView txv_temp_indoor = null;
    Switch btnToggle = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txv_temp_indoor = (TextView) findViewById(R.id.indoorTempShow);
        txv_temp_indoor.setText("the fetched indoor temp value");
        btnToggle = (Switch) findViewById(R.id.btnToggle);

        btnToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                // below you write code to change switch status and action to take
                if (isChecked) {
                    run("tdtool --off 1");


                } else {
                    run("tdtool --on 1");

                }}
        });
    }
    public void run (String command) {
        String hostname = "130.237.177.209";
        String username = "pi";
        String password = "IoT09dsv";

        try
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);
            Connection conn = new Connection(hostname); //init connection
            conn.connect(); //start connection to the hostname
            Boolean isAuthenticated = conn.authenticateWithPassword(username, password);

            if (isAuthenticated == false)
                throw new IOException("Authentication failed.");

            Session sess = conn.openSession();
            sess.execCommand(command);
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout)); //reads text

            while (true)
            {
                String line = br.readLine(); // read line
                if (line == null)
                    break;
                Log.d("line", line); }
                /* Show exit status, if available (otherwise "null") */
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close(); // Close this session
            conn.close();

        } catch (IOException e)
        { e.printStackTrace(System.err);
            System.exit(2); }
    }
}
