package ee.ut.cs.mc.and.pairerprototype.wifip2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
//    private MyWiFiActivity mActivity;
	private String TAG = "WiFiDirectBroadcastReceiver";

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
//        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG , "Received Intent: "+ action);
        
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        	Log.i(TAG,"WIFI_P2P_STATE_CHANGED_ACTION");
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
        	
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        	Log.i(TAG,"WIFI_P2P_PEERS_CHANGED_ACTION");
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
        	
            // Respond to new connection or disconnections
        	Log.i(TAG,"WIFI_P2P_CONNECTION_CHANGED_ACTION");
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        	
            // Respond to this device's wifi state changing
        	Log.i(TAG,"WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
        }
    }
}