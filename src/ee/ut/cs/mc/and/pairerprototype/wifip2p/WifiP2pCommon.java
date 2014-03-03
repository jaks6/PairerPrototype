package ee.ut.cs.mc.and.pairerprototype.wifip2p;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

public class WifiP2pCommon {
	
	WifiP2pManager mManager;
	Channel mChannel;
	BroadcastReceiver mReceiver;
	
	IntentFilter mIntentFilter;
	static String TAG = "WifiP2pCommon";
	String MAC_GALAXYYS2 = "06:46:65:68:0a:2c";

	public WifiP2pCommon(Context context) {

		mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
		//register application with the Wi-Fi P2P framework
		mChannel = mManager.initialize(context, context.getMainLooper(), null); 
	    
		//create an instance of your broadcast receiver 
	    mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel);
		
	    
	    mIntentFilter = new IntentFilter();

	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
	    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    context.registerReceiver(mReceiver, mIntentFilter);
	    
	    ///Without doing discoverPeers, connect() cannot be called
	    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            	Log.d(TAG, "Discovery succesfull");
            }
            @Override
            public void onFailure(int reasonCode) {
            	Log.d(TAG, "Discovery failed, code:" + reasonCode);
            }
        });

	}
	
	public void connectTo(){
		WifiP2pConfig config = new WifiP2pConfig();
		config.deviceAddress = MAC_GALAXYYS2;
		config.wps.setup = WpsInfo.PBC;
		
		mManager.connect(mChannel, config, new ActionListener() {

			@Override
		    public void onSuccess() {
		    	Log.d(TAG, "succesfully connected to WiFi Device");
		        //success logic
		    }

		    @Override
		    public void onFailure(int reason) {
		    	Log.d(TAG, "failure when connecting to WiFi Device");
		    	//failure logic
		    }
		});

	}
	
	
	public void checkPhoneSettings(int state) {
	        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
	        	Log.d(TAG, "Wifi P2P is enabled");
	        } else {
	        	Log.d(TAG, "Wi-Fi P2P is not enabled");
	        }
	    }
	
	

}
