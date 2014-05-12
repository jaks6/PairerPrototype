package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;

/** This class has methods to act as a both the client and the server in
 * a bluetooth connection
 */
public class BluetoothHelper {
	
	private BluetoothAdapter adapter;
	private Handler handler;
	public boolean listening = false;
	public String currentServer = null;
	private static BluetoothHelper instance = null;
	boolean useInsecureSecureRfcomm;  //Using this to see whether user notifications disappear in
	//insecure socket method
	private String TAG = "BluetoothHelper";
	
	// Private constructor prevents instantiation from other classes
    private BluetoothHelper() {
    	this.adapter = BluetoothAdapter.getDefaultAdapter();
    	this.useInsecureSecureRfcomm = false;
		// Cancel discovery because it will slow down the connection
        adapter.cancelDiscovery();
    }
    public static synchronized BluetoothHelper getInstance() {
        if (instance == null) {
            instance = new BluetoothHelper();
        }
        return instance;
    }
	public void startListening (){
		Log.d(TAG , "Starting to listen for incoming BT connections");
		listening = true;
		AcceptThread acceptThread = new AcceptThread(adapter, handler, useInsecureSecureRfcomm);
		acceptThread.start();
	}
	
	public void connectToServer(String address){
		BluetoothDevice server = adapter.getRemoteDevice(address);
		currentServer = address;
        
        Log.d(TAG , "Started connecting to BT server");
		ConnectThread connectThread = new ConnectThread(server, handler, useInsecureSecureRfcomm);
		connectThread.start();
	}

	public void setInsecureRfcomm(boolean isInsecureRfcomm) {
		this.useInsecureSecureRfcomm = isInsecureRfcomm;
	}

	public void setHandler(Handler handler) {
		if (this.handler == null) this.handler = handler;
	}

}
