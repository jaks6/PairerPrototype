package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

public class Client {
	private BluetoothAdapter adapter;
	private Handler handler;
	
	public Client(Handler handler) {
		this.adapter = BluetoothAdapter.getDefaultAdapter();
		this.handler = handler;
	}




	public void initServerDevice(String address){
		BluetoothDevice server = adapter.getRemoteDevice(address);
		// Cancel discovery because it will slow down the connection
        adapter.cancelDiscovery();
        
		ConnectThread connectThread = new ConnectThread(server, handler);
		connectThread.start();
	}
}
