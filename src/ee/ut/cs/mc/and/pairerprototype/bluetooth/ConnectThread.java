package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private Context c;
	private Activity activity;
 
    public ConnectThread(BluetoothDevice device, Activity a) {
        // Use activity temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.activity = a;
        
        
 
        // Get activity BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(BluetoothCommon.MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }
 
    public void run() {
    	
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
 
        // Do work to manage the connection (in activity separate thread)
        manageConnectedSocket(mmSocket);
    }
    
    private void manageConnectedSocket(BluetoothSocket socket){
    	//!TODO
    	//Create activity new thread for the work
    	
    	activity.runOnUiThread(new Runnable() {
    	    public void run() {
    	        Toast.makeText(activity, "Client successfully connected", Toast.LENGTH_SHORT).show();
    	    }
    	});
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}