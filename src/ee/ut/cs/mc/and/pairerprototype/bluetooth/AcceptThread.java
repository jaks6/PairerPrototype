package ee.ut.cs.mc.and.pairerprototype.bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import ee.ut.cs.mc.and.pairerprototype.MainActivityHandler;

class AcceptThread extends Thread {
	private final BluetoothServerSocket mmServerSocket;
	private BluetoothAdapter mBluetoothAdapter;
	private Handler handler;
	private String TAG = "AcceptThread";

	public AcceptThread(BluetoothAdapter mBluetoothAdapter, Handler handler, Boolean useInsecureRFCOMM) {
		this.handler = handler;
		// Use a temporary object that is later assigned to mmServerSocket,
		// because mmServerSocket is final
		this.mBluetoothAdapter = mBluetoothAdapter;
		BluetoothServerSocket tmp = null;
		UUID combinedUUID = UUID.fromString(BTCommon.UUID_BASE + BTCommon.deviceMAC.replace(":", ""));
		try {
			if (useInsecureRFCOMM){
				// MY_UUID is the app's UUID string, also used by the client code
				Log.v(TAG  , "LISTENING Using insecure RFCOMM channel");
				Log.v(TAG  , "UUID="+ combinedUUID.toString());
				tmp = this.mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(BTCommon.NAME, combinedUUID);
			} else {
				Log.v(TAG , "Using secure RFCOMM channel");
				tmp = this.mBluetoothAdapter.listenUsingRfcommWithServiceRecord(BTCommon.NAME, combinedUUID);
			}
		} catch (IOException e) { }
		mmServerSocket = tmp;
	}

	public void run() {
		BluetoothSocket socket = null;

		//Show connecting status on UI:
		Message msg = handler.obtainMessage(MainActivityHandler.SOCKET_LISTENING);
		handler.sendMessage(msg);

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
	private void manageConnectedSocket(final BluetoothSocket socket){
		//Create a new thread for the work
		Log.i("AcceptThread", "manageSocket started, notifying UI");
		Thread messageThread = (new Thread() {
			public void run() {

				Message msg_socket = handler.obtainMessage(MainActivityHandler.SOCKET_ESTABLISHED,socket);
				handler.sendMessage(msg_socket);

				Message msg_complete = handler.obtainMessage();
				msg_complete.what = MainActivityHandler.BT_CONNECTION_ESTABLISHED;
				msg_complete.arg1 = 1;
				msg_complete.obj = "Serverside - client connection accepted";
				handler.sendMessage(msg_complete);

			}
		});
		messageThread.start();
		
		ClientSocketThread clientThread = new ClientSocketThread();
		clientThread.setSocket(socket);
		clientThread.start();
		BTCommon.clientSocketList.add(clientThread);
		
//		ClientSocketThread.getInstance().setSocket(socket);
//		ClientSocketThread.getInstance().start();
	}

	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel() {
		try {
			mmServerSocket.close();
		} catch (IOException e) { }
	}
}