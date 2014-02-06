package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;

public class BTCommon {
	public static String NAME = "PairerPrototype";
	public static UUID MY_UUID = UUID.fromString("ea738d90-52b0-11e3-8f96-0800200c9a66");
	
	
	static BluetoothAdapter mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
	private static int REQUEST_ENABLE_BT = 1;	
	
	
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
			}
		}
	}
	
	
	
}
