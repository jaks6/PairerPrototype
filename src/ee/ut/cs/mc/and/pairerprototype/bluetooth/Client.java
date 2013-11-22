package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

public class Client {
	private BluetoothAdapter adapter;
	private Activity activity;
	
	public Client(Activity activity) {
		this.adapter = BluetoothAdapter.getDefaultAdapter();
		this.activity = activity;
	}




	public void initServerDevice(String address){
		BluetoothDevice server = adapter.getRemoteDevice(address);
		// Cancel discovery because it will slow down the connection
        adapter.cancelDiscovery();
        
		ConnectThread connectThread = new ConnectThread(server, activity);
		connectThread.start();
	}
}
