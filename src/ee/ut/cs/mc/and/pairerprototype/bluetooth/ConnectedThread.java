package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ee.ut.cs.mc.and.pairerprototype.MainActivity;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    static Handler handler;
 
    public ConnectedThread(BluetoothSocket socket, Handler handler) {
    	this.handler = handler;
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
        
        //Notify UI of connected status:
        Message msg_complete = handler.obtainMessage();
		msg_complete.what = MainActivity.BT_CONNECTION_ESTABLISHED;
		msg_complete.obj = "Connected";
		handler.sendMessage(msg_complete);
 
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }
 
    public void run() {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
 
        // Keep listening to the InputStream until an exception occurs
        while (true) {
            try {
                // Read from the InputStream
                bytes = mmInStream.read(buffer);
                // Send the obtained bytes to the UI activity
                handler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }
 
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}