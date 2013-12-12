package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.io.IOException;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import ee.ut.cs.mc.and.pairerprototype.MainActivity;

class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
	private Handler handler;
 
    public ConnectThread(BluetoothDevice device, Handler handler) {
        // Use activity temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.handler = handler;
        
        
 
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
    	
    	Thread messageThread = (new Thread() {
    	    public void run() {
    	    	Message msg = handler.obtainMessage();
    	        msg.what = MainActivity.TASK_COMPLETE;
    	        msg.obj = "Client succesfully connected";
    	        handler.sendMessage(msg);
    	    }
    	});
    	messageThread.start();
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}