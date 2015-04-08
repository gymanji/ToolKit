package tool_kit.gymanji.com.toolkit;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    public static final String MA_onClick = "MainActivity.onClick", MA_onCreate = "MainActivity.onCreate";
    private static final String NON_WIFI = "Not connected to WiFi", UNKNOWN = "<unknown ssid>", PHOBOS = "phobos.apple.com", COURIER = "1-courier.apple.com",
            OCSP = "ocsp.apple.com", ITUNES = "ax.itunes.apple.com", MTALK = "mtalk.google.com", PLAY = "play.google.com", NOTIFY_NET = "s.notify.live.net";
    private static final int EIGHTY = 80, SSL = 443, FIVETWOTWOTHREE = 5223, FIVETWOTWOEIGHT = 5228;
    private TextView tvConnectedNetwork, tvApple_phobos443, tvApple_phobos80, tvApple_courier5223, tvApple_courier443, tvApple_ocsp443, tvApple_ocsp80,
            tvApple_itunes443, tvApple_itunes80, tvAndroid_mtalk5228, tvAndroid_play443, tvWindows_net443, tvWindows_com443;
    private Button btnConnectivityTester, btnEmailResults;
    private Socket socket;
    int buttonCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiConnectionStatus();

        final ArrayList<NetworkObject> arrayList = new ArrayList<NetworkObject>();
        addItemstoArrayList(arrayList);

        // Button listener and invocation of asyncTask Socket
        btnConnectivityTester = (Button) findViewById(R.id.btnConnectivityTester);
        btnConnectivityTester.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonCounter++;
                ToolKitSocket donkey = new ToolKitSocket(arrayList);
                Log.d(MA_onClick, "new socket just created");
                donkey.execute();
            }
        });

        // Send results via email to Administrator
        btnEmailResults = (Button) findViewById(R.id.btnEmailResults);
        btnEmailResults.setOnClickListener(new View.OnClickListener() {

            String Apple_phobos443, Apple_phobos80,Apple_courier5223,Apple_courier443,Apple_ocsp443,Apple_ocsp80,Apple_itunes443,
                    Apple_itunes80,Android_mtalk5228,Android_play443,Windows_net443, ConnectedNetwork, currentDateTime;

            @Override
            public void onClick(View v) {

                if (buttonCounter < 1) {
                    Toast testNotRun = Toast.makeText(getApplicationContext(), "Click Run Test first!", Toast.LENGTH_LONG);
                    testNotRun.show();
                } else {
                    convertTextViewsToStrings();
                    SimpleDateFormat emailDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
                    currentDateTime = emailDF.format(Calendar.getInstance().getTime());

                    String message = "Test time: " + currentDateTime + "\n\n" +
                            "Connected WiFi Network: " + ConnectedNetwork + "\n\n" +
                            "phobos.apple.com 443: \t" + Apple_phobos443 + "\n" +
                            "phobos.apple.com 80: \t" + Apple_phobos80 + "\n" +
                            "*-courier.apple.com 5223: \t" + Apple_courier5223 + "\n" +
                            "*-courier.apple.com 443: \t" + Apple_courier443 + "\n" +
                            "ocsp.apple.com 443: \t" + Apple_ocsp443 + "\n" +
                            "ocsp.apple.com 80: \t" + Apple_ocsp80 + "\n" +
                            "ax.itunes.apple.com 443: \t" + Apple_itunes443 + "\n" +
                            "ax.itunes.apple.com 80: \t" + Apple_itunes80 + "\n" +
                            "mtalk.google.com 5228: \t" + Android_mtalk5228 + "\n" +
                            "play.google.com 443: \t" + Android_play443 + "\n" +
                            "s.notify.com 443: \t" + Windows_net443 + "\n";

                    Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:ZachReed@air-watch.com"));
                    sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "ToolKit Network Connectivity Results");
                    sendEmailIntent.putExtra(Intent.EXTRA_TEXT, message);
                    startActivity(sendEmailIntent);
                }
            }

            private void convertTextViewsToStrings() {
                Apple_phobos443 = tvApple_phobos443.getText().toString();
                Apple_phobos80 = tvApple_phobos80.getText().toString();
                Apple_courier5223 = tvApple_courier5223.getText().toString();
                Apple_courier443 = tvApple_courier443.getText().toString();
                Apple_ocsp443 = tvApple_ocsp443.getText().toString();
                Apple_ocsp80 = tvApple_ocsp80.getText().toString();
                Apple_itunes443 = tvApple_itunes443.getText().toString();
                Apple_itunes80 = tvApple_itunes80.getText().toString();
                Android_mtalk5228 = tvAndroid_mtalk5228.getText().toString();
                Android_play443 = tvAndroid_play443.getText().toString();
                Windows_net443 = tvWindows_net443.getText().toString();
                ConnectedNetwork = tvConnectedNetwork.getText().toString();
            }
        });

    }

    private void wifiConnectionStatus() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentWiFi = wifiInfo.getSSID();
        String currentWiFi_noQuotes = currentWiFi.substring(1, currentWiFi.length() - 1);
        Log.d(MA_onCreate, currentWiFi);

        createTextViewReferences();

        // Update TextView depending on network connection type
        if (currentWiFi == UNKNOWN) {
            tvConnectedNetwork.setText(NON_WIFI);
        } else {
            tvConnectedNetwork.setText(currentWiFi_noQuotes);
        }
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

    // Implementation of network test connection using Socket
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

    private void addItemstoArrayList(ArrayList<NetworkObject> arrayList) {
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
    }

    private void createTextViewReferences() {
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
