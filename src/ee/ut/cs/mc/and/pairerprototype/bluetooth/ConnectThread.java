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
        this.mmDevice = device;
        this.handler = handler;
        
        
 
        // Get activity BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = mmDevice.createRfcommSocketToServiceRecord(BTCommon.MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }
 
    public void run() {
        try {
        	
        	//Show connecting status on UI:
        	Message msg = handler.obtainMessage(MainActivity.SOCKET_CONNECTING);
	        handler.sendMessage(msg);
	        
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
    
    private void manageConnectedSocket(final BluetoothSocket socket){
    	//Create new thread for the work
    	Thread messageThread = (new Thread() {
    	    public void run() {
    	    	
    	    	Message msg_socket = handler.obtainMessage(MainActivity.SOCKET_ESTABLISHED,socket);
				handler.sendMessage(msg_socket);
				
    	    	//Display a toast in the UI:
    	    	Message msg = handler.obtainMessage();
    	        msg.what = MainActivity.BT_CONNECTION_ESTABLISHED;
    	        msg.arg1 = 2;
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