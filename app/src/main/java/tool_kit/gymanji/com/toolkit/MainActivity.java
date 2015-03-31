package tool_kit.gymanji.com.toolkit;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends ActionBarActivity {

    private TextView tvConnectedNetwork;
    private final String nonWiFi = "Not connected to WiFi";
    private final String unknown = "<unknown ssid>";
    private int StandardSSLPort = 443;
    private final String AppleMAM = "phobos.apple.com";
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        new Thread(new ClientThread()).start();

        // Locating currently connected WiFi network
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentWiFi = wifiInfo.getSSID();
        String currentWiFi2 = currentWiFi.substring(1, currentWiFi.length()-1);
        Log.d("SSID", currentWiFi);
        tvConnectedNetwork = (TextView) findViewById(R.id.tvConnectedNetwork);

        // Update TextView depending on network connection type
        if (currentWiFi == unknown) {
            tvConnectedNetwork.setText(nonWiFi);
        } else {
            tvConnectedNetwork.setText(currentWiFi2);
        }
    }

//    public void onClick(View v) {
//        try {
//            Socket s = new Socket(AppleMAM,StandardSSLPort);
//            OutputStream out = s.getOutputStream();
//
//            PrintWriter output = new PrintWriter(out);
//            output.println("Hello Android");
//            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
//
//            String st = input.readLine();
//            s.close();
//
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

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

//    @Override
//    private boolean portOpen(String dstName, int dstPort) {
//        try {
//
//        } catch ()
//
//    }

}
