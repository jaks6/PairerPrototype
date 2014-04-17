package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import ee.ut.cs.mc.and.pairerprototype.MainActivityHandler;

class ConnectThread extends Thread {
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;
	private Handler handler;
	private String TAG = "ConnectThread";

	public ConnectThread(BluetoothDevice device, Handler handler, Boolean useInsecureRfcomm) {
		//Form valid UUID based on server's mac and our global UUID
		String mac = device.getAddress().replace(":", "");
		UUID combinedUUID = UUID.fromString(BTCommon.UUID_BASE + mac );
		// Use activity temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket tmp = null;
		this.mmDevice = device;
		this.handler = handler;
		


		// Get activity BluetoothSocket to connect with the given BluetoothDevice
		// MY_UUID is the app's UUID string, also used by the server code
		try {
			if (useInsecureRfcomm){
				Log.v(TAG , "CONNECTING Using insecure RFCOMM channel");
				Log.v(TAG  , "UUID="+ combinedUUID.toString());
				tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(combinedUUID);
			} else {
				Log.v(TAG , "Using secure RFCOMM channel");
				tmp = mmDevice.createRfcommSocketToServiceRecord(combinedUUID);
				
			}
		} catch (IOException e) { }
		mmSocket = tmp;
	}

	
	/** If no bluetooth socket security mode is provided, default to
	 * the securer option */
	public ConnectThread(BluetoothDevice server, Handler handler) {
		this(server,handler,false);
	}

	public void run() {
		try {

			//Show connecting status on UI:
			Message msg = handler.obtainMessage(MainActivityHandler.SOCKET_CONNECTING);
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

				Message msg_socket = handler.obtainMessage(
						MainActivityHandler.SOCKET_ESTABLISHED,socket);
				handler.sendMessage(msg_socket);

				//Display a toast in the UI:
				Message msg = handler.obtainMessage();
				msg.what = MainActivityHandler.BT_CONNECTION_ESTABLISHED;
				msg.arg1 = 2;
				msg.obj = "Client succesfully connected";
				handler.sendMessage(msg);

			}
		});
		messageThread.start();
		new ConnectedThread(socket, handler).start();

	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) { }
	}
}