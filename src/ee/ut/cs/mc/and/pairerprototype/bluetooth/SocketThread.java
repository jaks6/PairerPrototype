package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import ee.ut.cs.mc.and.pairerprototype.App;
import ee.ut.cs.mc.and.pairerprototype.ChatMsg;
import ee.ut.cs.mc.and.pairerprototype.MainActivityHandler;

public class SocketThread extends Thread {
    private BluetoothSocket mSocket;
    private volatile ObjectInputStream mObjectInStream;
    private volatile ObjectOutputStream mObjectOutStream;
	private String TAG = SocketThread.class.getName();
	protected String type = "none";
    
    protected static SocketThread instance = null;
 
    public SocketThread(){};
    /** This thread handles incoming and outcoming bluetooth data, forwarding 
     * data in the bt network chain if needed.
     * The bluetooth network of devices uses a simple chain structure, such as:
     *  ... -> ClientDevice -> ThisDevice -> ServerDevice -> ..., 
     *  where ThisDevice is a client to ServerDevice and at the same time a server to ClientDevice
     *  
     */
    public static SocketThread getInstance() {
        if(instance == null) {
           instance = new SocketThread();
        }
        return instance;
     }

	private void notifyUi(String message) {
		Handler handler = App.getMainActivityHandler();
        if (handler == null){
        	Log.e(TAG, "Could not get instance of MainActivityHandler");
        	return;
        }
		Message msg_complete;
		msg_complete = handler.obtainMessage();
		msg_complete.what = MainActivityHandler.BT_CONNECTION_ESTABLISHED;
		msg_complete.obj = message;
		handler.sendMessage(msg_complete);
	}
 
    public void run() {
    	if (mSocket== null) return;
        // Keep listening to the InputStream until an exception occurs
        Handler handler = App.getMainActivityHandler();
        if (handler == null){
        	Log.e(TAG, "Could not get instance of MainActivityHandler");
        	return;
        }
        while (true) {
        	ObjectInputStream inStream = mObjectInStream;
    		if (inStream!=null){
    			if (!readObjectFromInputStream(handler,inStream)) break;
        	}
        }
    }
	
	private boolean readObjectFromInputStream(Handler handler, ObjectInputStream inStream) {
		try {
		    // Read from the InputStream
			ChatMsg message = (ChatMsg) inStream.readObject();
		    Log.d(TAG, type +"READ OBJECT= " + message.toString());
		    // Send the obtained bytes to the UI activity
		    handler.obtainMessage(
		    		MainActivityHandler.BT_MESSAGE_READ,
		    		message)
		            .sendToTarget();
		} catch (IOException e) {
		    return false;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
        	if (mSocket != null) mSocket.close();
        	this.interrupt();
        } catch (IOException e) { }
    }
    
	public void setSocket(BluetoothSocket serverSocket) {
		Log.d(TAG, "Setting "+ type +" socket");
		mSocket = serverSocket;
		
		setObjectOutStream(mSocket);
		setObjectInputStream(mSocket);

        //Notify UI of connected status:
        notifyUi(type +" socket set");
	}
	protected void setObjectOutStream(BluetoothSocket socket) {
		ObjectOutputStream tmp=null;
        // Get the input and output streams
        try {
        	tmp = new ObjectOutputStream(socket.getOutputStream());
    		Log.d(TAG, "Set "+ type +" OOS");
    		mObjectOutStream = tmp;
    		mObjectOutStream.flush();
        } catch (IOException e) { e.printStackTrace();}
	}
	
	private void setObjectInputStream(BluetoothSocket socket) {
		ObjectInputStream tmp=null;
        // Get the input and output streams
        try {
        	tmp = new ObjectInputStream(socket.getInputStream());
    		Log.d(TAG, "Set "+ type +" OIS");
    		mObjectInStream = tmp;
        } catch (IOException e) { e.printStackTrace();}
	}
	
	
	@Override
	public synchronized void start() {
		if (!this.isAlive()){
			super.start();
		} else {
			Log.w(TAG, "Thread start requested but thread already alive");
		}
	}
	public BluetoothSocket getSocket() {
		return mSocket;
	}
	public ObjectOutputStream getObjectOutStream() {
		return mObjectOutStream;
	}
}
