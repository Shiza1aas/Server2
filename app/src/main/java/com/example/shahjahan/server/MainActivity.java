package com.example.shahjahan.server;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SERVER_INFO";
    Button button_manage_server;
    TextView text_ip_address;
    TextView text_client_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_manage_server = (Button) findViewById(R.id.manage_server);
        text_client_message = (TextView) findViewById(R.id.client_text);
        text_ip_address = (TextView) findViewById(R.id.ip_address);
        showIPAddress(text_ip_address);

    }

//starting a server

    public void startServer(View view)
    {
        new ManageServer(this,this,text_client_message,80,button_manage_server).execute();
    }


    //   the method set the ip address of the server.pla
    private void showIPAddress(TextView text_ip_address) {

        if (LogConfig.TAG)
            Log.d(TAG, "I am in show IP.");
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        text_ip_address.setText(ip);

    }

}


class ManageServer extends AsyncTask<Void, Boolean, Void> {
    private static final String TAG = "SERVER_INFO";
    TextView text_client_message;
    Integer server_port;
    Button button_manage_server;
    private static ServerSocket serverSocket;
    Context context;
    Activity activity;
    String readLine;

    public ManageServer(Context context, Activity activity, TextView text_client_message, Integer server_port, Button button_manage_server) {
        this.text_client_message = text_client_message;
        this.server_port = server_port;
        this.button_manage_server = button_manage_server;
        this.context = context;
        this.activity = activity;
    }

    protected void onPreExecute(Void result) {

        if (LogConfig.TAG)
            Log.d(TAG, "On pre execute.");

    }

    @Override
    protected Void doInBackground(Void... params) {
        try {

            if (LogConfig.TAG)
                Log.d(TAG, "Starting a socket.");

            serverSocket = new ServerSocket(8080);

            publishProgress(true);
            if (LogConfig.TAG)
                Log.d(TAG, "Socket has been started. Waiting for client.");

            Socket socket = serverSocket.accept();

            if (LogConfig.TAG)
                Log.d(TAG, "Socket has been started. Reading from client.");

            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

//            read and display in the loop
            do {
                readLine = inputStream.readUTF();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //here you update the views
                        text_client_message.setText(readLine);
                    }
                });
            } while (!readLine.equals("bye"));

            socket.close();
            serverSocket.close();


        } catch (IOException e) {
            Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
        button_manage_server.setText("Server has been started...");
    }

    protected void onPostExecute(Void result) {
        button_manage_server.setText("Server has been stopped...");
    }

}
