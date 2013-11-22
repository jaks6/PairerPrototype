package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

public class Server {
	
	BluetoothAdapter adapter;
	private Activity activity;
	
	public void startListening (String name, UUID uuid, Activity activity){
		this.activity = activity;
		BluetoothAdapter mAdapter =  BluetoothAdapter.getDefaultAdapter();
		AcceptThread acceptThread = new AcceptThread(mAdapter, activity);
		acceptThread.start();
	}
}
