package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

/** This class has methods to act as a both the client and the server in
 * a bluetooth connection
 */
public class BTCommunicator {
	
	private BluetoothAdapter adapter;
	private Handler handler;
	
	boolean useInsecureSecureRfcomm;  //Using this to see whether user notifications disappear in
	//insecure socket method
	
	public BTCommunicator(Handler handler) {
		
		
		this.adapter = BluetoothAdapter.getDefaultAdapter();
		this.handler = handler;
		this.useInsecureSecureRfcomm = false;
		
		// Cancel discovery because it will slow down the connection
        adapter.cancelDiscovery();
	}
	
	public void startListening (){
		AcceptThread acceptThread = new AcceptThread(adapter, handler, useInsecureSecureRfcomm);
		acceptThread.start();
	}
	
	public void connectToServer(String address){
		BluetoothDevice server = adapter.getRemoteDevice(address);
		// Cancel discovery because it will slow down the connection
        adapter.cancelDiscovery();
        
        
		ConnectThread connectThread = new ConnectThread(server, handler, useInsecureSecureRfcomm);
		connectThread.start();
	}

	public void setSecureRfcomm(boolean isSecureRfcomm) {
		this.useInsecureSecureRfcomm = isSecureRfcomm;
	}

}
