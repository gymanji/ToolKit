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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class Opener extends ActionBarActivity {

    public static final String Opener_onClick = "MainActivity.onClick", Opener_onCreate = "MainActivity.onCreate", NON_WIFI = "Not connected to WiFi", UNKNOWN = "<unknown ssid>", PHOBOS = "phobos.apple.com", COURIER = "1-courier.apple.com",
            OCSP = "ocsp.apple.com", ITUNES = "ax.itunes.apple.com", MTALK = "mtalk.google.com", PLAY = "play.google.com", NOTIFY_NET = "s.notify.live.net";
    private static final int EIGHTY = 80, SSL = 443, FIVETWOTWOTHREE = 5223, FIVETWOTWOEIGHT = 5228;
    public TextView tvConnectedNetwork, tvApple_phobos443, tvApple_phobos80, tvApple_courier5223, tvApple_courier443, tvApple_ocsp443, tvApple_ocsp80,
            tvApple_itunes443, tvApple_itunes80, tvAndroid_mtalk5228, tvAndroid_play443, tvWindows_net443, tvWindows_com443;
    public Button btnConnectivityTester;
    int buttonCounter = 0;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opener);

        MainActivity mainActivity = new MainActivity();

        // Locating currently connected WiFi network
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentWiFi = wifiInfo.getSSID();
        String currentWiFi_noQuotes = currentWiFi.substring(1, currentWiFi.length() - 1);
        Log.d(Opener_onCreate, currentWiFi);

        // Update TextView depending on network connection type
        tvConnectedNetwork = (TextView) findViewById(R.id.tvConnectedNetwork);
        if (currentWiFi == UNKNOWN) {
            tvConnectedNetwork.setText(NON_WIFI);
        } else {
            tvConnectedNetwork.setText(currentWiFi_noQuotes);
        }

        btnConnectivityTester = (Button) findViewById(R.id.btnConnectivityTester);
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

        final ArrayList<NetworkObject> arrayList = new ArrayList<NetworkObject>();

        arrayList.add(new NetworkObject(PHOBOS, SSL, tvApple_phobos443));
        arrayList.add(new NetworkObject(PHOBOS, EIGHTY, tvApple_phobos80));
        arrayList.add(new NetworkObject(COURIER, FIVETWOTWOTHREE, tvApple_courier5223));
        arrayList.add(new NetworkObject(COURIER, SSL, tvApple_courier443));
        arrayList.add(new NetworkObject(OCSP, SSL, tvApple_ocsp443));
        arrayList.add(new NetworkObject(OCSP, EIGHTY, tvApple_ocsp80));
        arrayList.add(new NetworkObject(ITUNES, SSL, tvApple_itunes443));
        arrayList.add(new NetworkObject(ITUNES, EIGHTY, tvApple_itunes80));
        arrayList.add(new NetworkObject(MTALK, FIVETWOTWOEIGHT, tvAndroid_mtalk5228));
        arrayList.add(new NetworkObject(PLAY, SSL, tvAndroid_play443));
        arrayList.add(new NetworkObject(NOTIFY_NET, SSL, tvWindows_net443));

        // Button listener and invocation of asyncTask Socket
        btnConnectivityTester = (Button) findViewById(R.id.btnConnectivityTester);
        btnConnectivityTester.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonCounter++;
                ToolKitSocket donkey = new ToolKitSocket(arrayList);
                Log.d(Opener_onClick, "new socket just created");
                donkey.execute();
//
//                Intent intent = new Intent(getParentActivityIntent());
//                startActivity(intent);
            }
        });
    }


    // Class for custom object to store necessary network connection-related values
    private static class NetworkObject {
        private String destAddr;
        private int port;
        private TextView textView;
        boolean response;

        public NetworkObject(String destAddr, int port, TextView textView) {
            this.destAddr = destAddr;
            this.port = port;
            this.textView = textView;
        }
    }

    public class ToolKitSocket extends AsyncTask<Void, Void, List<NetworkObject>> {

        public static final String TK_doInBackground = "TKS.doInBackground";
        public static final int SO_TIMEOUT = 2500;
        private final List<NetworkObject> lno;

        ToolKitSocket(List<NetworkObject> lno) {
            this.lno = lno;
        }

        @Override
        protected List<NetworkObject> doInBackground(Void... arg0) {

            for (NetworkObject networkObject : lno) {
                try {
                    Log.d(TK_doInBackground, networkObject.destAddr + ": " + networkObject.port + " Just entered try block of socket");
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(networkObject.destAddr, networkObject.port), SO_TIMEOUT);
                    networkObject.response = socket.isConnected();
                    Log.d(TK_doInBackground, "socketStatus = " + networkObject.response);
                    if (networkObject.response) {
                        socket.close();
                        Log.d(TK_doInBackground, "socket.close() just executed");

                    } else {
                        Log.d(TK_doInBackground, "shit just got real");
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return lno;
        }

        @Override
        protected void onPostExecute(List<NetworkObject> result) {
            for (NetworkObject networkObject : result) {
                if (networkObject.response) {
                    networkObject.textView.setText(getResources().getString(R.string.successful_connection));
                    networkObject.textView.setTextAppearance(getApplicationContext(), R.style.Connection_successful);
                } else {
                    networkObject.textView.setText(getResources().getString(R.string.failed_connection));
                    networkObject.textView.setTextAppearance(getApplicationContext(), R.style.Connection_failed);
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_opener, menu);
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
