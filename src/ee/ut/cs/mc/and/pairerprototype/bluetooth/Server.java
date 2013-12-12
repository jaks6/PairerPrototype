package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.os.Handler;

public class Server {
	
	BluetoothAdapter adapter;
	private Handler handler;
	
	public void startListening (String name, UUID uuid, Handler handler){
		this.handler = handler;
		BluetoothAdapter mAdapter =  BluetoothAdapter.getDefaultAdapter();
		AcceptThread acceptThread = new AcceptThread(mAdapter, handler);
		acceptThread.start();
	}
}
