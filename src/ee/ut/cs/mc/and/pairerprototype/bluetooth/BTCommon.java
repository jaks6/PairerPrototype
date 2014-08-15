package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

public class BTCommon {
	private static final String TAG = "BTCommon";
	public static final String NAME = "PairerPrototype";
	public static final String UUID_BASE ="7d0cea40-c618-11e3-9c1a-"; // UUID_BASE+ MAC_ADDRESS will form a valid UUID for use in bluetooth connections
	public static String deviceMAC;
	
	
	public static ArrayList<ClientSocketThread> clientSocketList = new ArrayList<ClientSocketThread>();
	static BluetoothAdapter mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
	public static int REQUEST_ENABLE_BT = 1;	
	
	public static String getMACAddress(){
		if ((mBluetoothAdapter != null) && (mBluetoothAdapter.isEnabled())){
			return BluetoothAdapter.getDefaultAdapter().getAddress();
		} else return "00000000";
	}
	public static void checkPhoneSettings(Activity activity){
		//Check if device supports bluetooth:
		if (mBluetoothAdapter == null) {
		    //!TODO
		} else {
			Log.v("", "Bluetooth is supported");
			//Check if bluetooth is enabled. If not, request the user to enable it.
			if (!mBluetoothAdapter.isEnabled()) { 
				Log.v("", "Bluetooth is not turned on");
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    activity.startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
			} else {
				deviceMAC = getMACAddress();
			}
		}
	}
	public static void handleBluetoothTurnedOnEvent() {
		  Log.d(TAG, "activity result OK");
		    boolean isEnabling = BTCommon.mBluetoothAdapter.enable();
		    if (!isEnabling)
		    {	Log.d(TAG, "not enabling");
		      // an immediate error occurred - perhaps the bluetooth is already on?
		    }
		    else if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON)
		    {
		    	Log.d(TAG, "STATE TURNING ON");
		      // the system, in the background, is trying to turn the Bluetooth on
		      // while your activity carries on going without waiting for it to finish;
		      // of course, you could listen for it to finish yourself - eg, using a
		      // ProgressDialog that checked mBluetoothAdapter.getState() every x
		      // milliseconds and reported when it became STATE_ON (or STATE_OFF, if the
		      // system failed to start the Bluetooth.)
		    } else {
		    	//BT should now be turned on
		    	Log.d(TAG,"Fetching Bluetooth MAC address");
		    	deviceMAC = getMACAddress();
		    }
	}
	
	public static ArrayList<BluetoothSocket> getSockets(){
		ArrayList<BluetoothSocket> resultList = new ArrayList<BluetoothSocket>();
		for (ClientSocketThread thread: clientSocketList){
			resultList.add(thread.getSocket());
		}
		return resultList;
	}
	public static ArrayList<ObjectOutputStream> getOOSes(){
		ArrayList<ObjectOutputStream> resultList = new ArrayList<ObjectOutputStream>();
		for (ClientSocketThread thread: clientSocketList){
			resultList.add(thread.getObjectOutStream());
		}
		return resultList;
	}
}
