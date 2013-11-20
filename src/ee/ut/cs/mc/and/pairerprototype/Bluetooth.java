package ee.ut.cs.mc.and.pairerprototype;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;

public class Bluetooth {
	static BluetoothAdapter mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();
	private int REQUEST_ENABLE_BT = 1;
	
	
	
	
	public Bluetooth(Activity activity){
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
