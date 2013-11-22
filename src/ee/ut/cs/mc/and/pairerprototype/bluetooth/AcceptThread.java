package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.io.IOException;

import android.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
	private BluetoothAdapter mBluetoothAdapter;
	private Activity activity;
	
    public AcceptThread(BluetoothAdapter mBluetoothAdapter, Activity activity) {
    	this.activity = activity;
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
    	this.mBluetoothAdapter = mBluetoothAdapter;
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(BluetoothCommon.NAME, BluetoothCommon.MY_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;
    }
 
    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                manageConnectedSocket(socket);
                try {
					mmServerSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
                break;
            }
        }
    }
    private void manageConnectedSocket(BluetoothSocket socket){
    	//!TODO
    	//Create a new thread for the work
    	activity.runOnUiThread(new Runnable() {
    	    public void run() {
    	        Toast.makeText(activity, "Server-side successfully accepted connection", Toast.LENGTH_SHORT).show();
    	    }
    	});
    }
 
    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}