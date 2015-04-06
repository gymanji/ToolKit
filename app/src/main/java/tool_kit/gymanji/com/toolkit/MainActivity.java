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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MainActivity extends ActionBarActivity {

    private TextView tvConnectedNetwork, tvApple_phobos443, tvApple_phobos80, tvApple_courier5223, tvApple_courier443, tvApple_ocsp443, tvApple_ocsp80,
            tvApple_itunes443, tvApple_itunes80, tvAndroid_mtalk5228, tvAndroid_play443, tvWindows_net443, tvWindows_com443;
    private static final String NON_WIFI = "Not connected to WiFi", UNKNOWN = "<unknown ssid>", PHOBOS = "phobos.apple.com", COURIER = "1-courier.apple.com",
            OCSP = "ocsp.apple.com", ITUNES = "ax.itunes.apple.com", MTALK = "mtalk.google.com", PLAY = "play.google.com", NOTIFY_NET = "s.notify.live.net";
    private static final int EIGHTY = 80, SSL = 443, FIVETWOTWOTHREE = 5223, FIVETWOTWOEIGHT = 5228;
    private Button btnConnectivityTester;
    private Socket socket;
    boolean returnedBoolFromOnPostExecute;
//    HashMap networkEndPointsById;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Locating currently connected WiFi network
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentWiFi = wifiInfo.getSSID();
        String currentWiFi_noQuotes = currentWiFi.substring(1, currentWiFi.length() - 1);
        Log.d("SSID", currentWiFi);
        Log.d("rBFOPtE - onCreate", returnedBoolFromOnPostExecute ? "true" : "false");

        // Hooking up TextViews for network connection results
        tvConnectedNetwork = (TextView) findViewById(R.id.tvConnectedNetwork);
        tvApple_phobos443 = (TextView) findViewById(R.id.tvApple_phobos443);
        tvApple_phobos80 = (TextView) findViewById(R.id.tvApple_phobos80);
        tvApple_courier5223 = (TextView) findViewById(R.id.tvApple_courier5223);
        tvApple_courier443 = (TextView) findViewById(R.id.tvApple_courier443);
        tvApple_ocsp443 = (TextView) findViewById(R.id.tvApple_ocsp443);
        tvApple_ocsp80 = (TextView) findViewById(R.id.tvApple_ocsp80);
        tvApple_ocsp443 = (TextView) findViewById(R.id.tvApple_ocsp443);
        tvApple_itunes443 = (TextView) findViewById(R.id.tvApple_itunes443);
        tvApple_itunes80 = (TextView) findViewById(R.id.tvApple_itunes80);
        tvAndroid_mtalk5228 = (TextView) findViewById(R.id.tvAndroid_mtalk5228);
        tvAndroid_play443 = (TextView) findViewById(R.id.tvAndroid_play443);
        tvWindows_net443 = (TextView) findViewById(R.id.tvWindows_net443);
        tvWindows_com443 = (TextView) findViewById(R.id.tvWindows_com443);

        // Update TextView depending on network connection type
        if (currentWiFi == UNKNOWN) {
            tvConnectedNetwork.setText(NON_WIFI);
        } else {
            tvConnectedNetwork.setText(currentWiFi_noQuotes);
        }

        // HashMap for storing values related to network connection testing and corresponding view updates
//        class NetworkHashTable {
//            public void main(String[] args) {

                final HashMap networkEndPointsById = new HashMap<Integer, NetworkObject>();

                networkEndPointsById.put(1, new NetworkObject(PHOBOS, SSL, tvApple_phobos443));
                networkEndPointsById.put(2, new NetworkObject(PHOBOS, EIGHTY, tvApple_phobos80));
                networkEndPointsById.put(3, new NetworkObject(COURIER, FIVETWOTWOTHREE, tvApple_courier5223));
                networkEndPointsById.put(4, new NetworkObject(COURIER, SSL, tvApple_courier443));
                networkEndPointsById.put(5, new NetworkObject(OCSP, SSL, tvApple_ocsp443));
                networkEndPointsById.put(6, new NetworkObject(OCSP, EIGHTY, tvApple_ocsp80));
                networkEndPointsById.put(7, new NetworkObject(ITUNES, SSL, tvApple_itunes443));
                networkEndPointsById.put(8, new NetworkObject(ITUNES, EIGHTY, tvApple_itunes80));
                networkEndPointsById.put(9, new NetworkObject(MTALK, FIVETWOTWOEIGHT, tvAndroid_mtalk5228));
                networkEndPointsById.put(10, new NetworkObject(PLAY, SSL, tvAndroid_play443));
                networkEndPointsById.put(11, new NetworkObject(NOTIFY_NET, SSL, tvWindows_net443));
//            }
//        }

        // Button listener and invocation of asyncTask Socket
        btnConnectivityTester = (Button) findViewById(R.id.btnConnectivityTester);
        btnConnectivityTester.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("onClick", "onClick just started");

                Set<Map.Entry<Integer, NetworkObject>> entrySet = networkEndPointsById.entrySet();

                //Debug logging
                boolean hashMapEmpty = networkEndPointsById.isEmpty();
                String hashMapEmpty2 = Boolean.toString(hashMapEmpty);
                Log.d("onClick - hasMapEmptyBool", hashMapEmpty2);

                // Loop through network connections to test endpoints and update view
                for (Map.Entry<Integer, NetworkObject> entry : entrySet) {

                    NetworkObject networkobject = entry.getValue();

                    ToolKitSocket donkey = new ToolKitSocket(networkobject.getDestAddr(), networkobject.getPort());
                    Log.d("onClick", "new socket just created");
                    donkey.execute();
                    Log.d("onClick", "ToolKitSocket just executed");

                    TextView currentTV = networkobject.getTextView();
                    Log.d("rBFOPtE - onClick", returnedBoolFromOnPostExecute ? "true" : "false");
                    if (returnedBoolFromOnPostExecute) {
                        currentTV.setText(getResources().getString(R.string.successful_connection));
                        currentTV.setTextAppearance(getApplicationContext(), R.style.Connection_successful);
                    } else {
                        currentTV.setText(getResources().getString(R.string.failed_connection));
                        currentTV.setTextAppearance(getApplicationContext(), R.style.Connection_failed);
                    }
                }
            }
        });
    }

    // Class for custom object to store necessary network connection-related values
    public class NetworkObject {
        private String destAddr;
        private int port;
        private TextView textView;

        public NetworkObject(String destAddr, int port, TextView textView) {
            this.destAddr = destAddr;
            this.port = port;
            this.textView = textView;
        }

        public String getDestAddr() {
            return destAddr;
        }

        public int getPort() {
            return port;
        }

        public TextView getTextView() {
            return textView;
        }
    }

    // Implementation of network test connection using Socket
    public class ToolKitSocket extends AsyncTask<Void, Void, Boolean> {

        String dstAddress;
        int dstPort;
        Boolean response;

        ToolKitSocket(String addr, int port) {
            dstAddress = addr;
            dstPort = port;
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {

            boolean socketStatus;

            try {
                Log.d("Socket", "Just entered try block of socket");
                socket = new Socket(dstAddress, dstPort);
                socketStatus = socket.isConnected();
                Log.d("Socket", "socketStatus = " + socketStatus);
                if (socketStatus) {
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
            return response;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            returnedBoolFromOnPostExecute = response;
            super.onPostExecute(returnedBoolFromOnPostExecute);
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
