package tool_kit.gymanji.com.toolkit;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends ActionBarActivity {

    private TextView tvConnectedNetwork, tvApple_phobos443;
    private Button btnConnectivityTester;
    private final String nonWiFi = "Not connected to WiFi";
    private final String unknown = "<unknown ssid>";
    private int StandardSSLPort = 443;
    private final int SOCKET_TIMEOUT = 5000;
    private final String AppleMAM = "phobos.apple.com";
    private Socket socket;
    boolean socketStatus, returnedBoolFromOnPostExecute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Locating currently connected WiFi network
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentWiFi = wifiInfo.getSSID();
        String currentWiFi2 = currentWiFi.substring(1, currentWiFi.length()-1);
        Log.d("SSID", currentWiFi);
        Log.d("rBFOPtE - onCreate", returnedBoolFromOnPostExecute?"true":"false");
        tvConnectedNetwork = (TextView) findViewById(R.id.tvConnectedNetwork);
        tvApple_phobos443 = (TextView) findViewById(R.id.tvApple_phobos443);

        // Update TextView depending on network connection type
        if (currentWiFi == unknown) {
            tvConnectedNetwork.setText(nonWiFi);
        } else {
            tvConnectedNetwork.setText(currentWiFi2);
        }

        // Button listener and invocation of asyncTask Socket
        btnConnectivityTester = (Button) findViewById(R.id.btnConnectivityTester);
        btnConnectivityTester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClick", "onClick just started");
                ToolKitSocket donkey = new ToolKitSocket(AppleMAM, StandardSSLPort);
                Log.d("onClick", "new socket just created");
                donkey.execute();
                Log.d("onClick", "ToolKitSocket just executed");

                Log.d("rBFOPtE - onClick", returnedBoolFromOnPostExecute?"true":"false");
                if(returnedBoolFromOnPostExecute) {
                    tvApple_phobos443.setText(getResources().getString(R.string.successful_connection));
                    tvApple_phobos443.setTextAppearance(getApplicationContext(), R.style.Connection_successful);
                } else {
                    tvApple_phobos443.setText(getResources().getString(R.string.failed_connection));
                    tvApple_phobos443.setTextAppearance(getApplicationContext(), R.style.Connection_failed);
                }
            }
        });
    }

    // Network test connections using Socket
    public class ToolKitSocket extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        Boolean response;

        ToolKitSocket(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Void doInBackground(Void... arg0){

            boolean socketStatus;

            try {
                Log.d("Socket", "Just entered try block of socket");
                socket = new Socket(dstAddress, dstPort);
                    socketStatus = socket.isConnected();
                Log.d("Socket", "socketStatus = " + socketStatus);
                    if(socketStatus) {
                        socket.close();
                        response = socketStatus;
                        Log.d("Socket", "socket.close() just executed");
                    } else {
                        Log.d("Socket", "shit just got real");
                    }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            returnedBoolFromOnPostExecute = response;
            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
