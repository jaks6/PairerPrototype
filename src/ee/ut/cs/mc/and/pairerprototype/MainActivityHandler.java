package ee.ut.cs.mc.and.pairerprototype;

import java.io.ObjectOutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.ClientSocketThread;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.ServerSocketThread;

public class MainActivityHandler extends Handler {
	/*
	 * Status codes
	 */
	public static final int DISPLAY_TOAST = 1;
	public static final int UPDATE_PROGRESSBAR = 2;
	public static final int DISPLAY_LOADING_DIALOG = 3;
	public static final int REMOVE_LOADING_DIALOG = 4;
	public static final int BT_CONNECTION_ESTABLISHED = 5;
	public static final int BT_MESSAGE_READ = 6;
	
	public static final int SOCKET_ESTABLISHED = 11;
	public static final int SOCKET_LISTENING = 12;
	public static final int SOCKET_CONNECTING = 13;
	private static final int LOADING_DIALOG_ADDTEXT = 0;
	
	static Chat chatSession = null;
	private Activity mActivity;
	private String TAG = MainActivityHandler.class.toString();
	
	public MainActivityHandler(Activity activity) {
		this.mActivity = activity;
	}
	
	@Override
	public void handleMessage(Message msg) {
		String message = "";
		switch (msg.what){

		case DISPLAY_TOAST:
			((MainActivity) mActivity).displayToast((CharSequence)msg.obj);
			break;

		case BT_CONNECTION_ESTABLISHED:
			((MainActivity) mActivity).displayToast( (CharSequence)msg.obj);
			if (msg.arg1 == 1){ //1 for server side, 2 for client
				MainActivity.serverButton.setText("*Connected*");
			} else if (msg.arg1 == 2){
				MainActivity.clientButton.setText("*Connected*");
			}
			break;

		case BT_MESSAGE_READ:
			//A message has been received via bluetooth.
			ChatMsg chatMsg = (ChatMsg) msg.obj;
			//Display the msg in main chat window
			((MainActivity) mActivity).displayInChat(chatMsg.toString());
			
			
			// Check if it should be broadcasted to other devices and do so.
			ServerSocketThread serverThread = (ServerSocketThread) ServerSocketThread.getInstance();
			ClientSocketThread clientThread = (ClientSocketThread) ClientSocketThread.getInstance();
			ObjectOutputStream[] streams = {serverThread.getObjectOutStream(), 
					clientThread.getObjectOutStream()};
			
			BluetoothSocket[] sockets = {serverThread.getSocket(), clientThread.getSocket()};
			//broadcast to other BT devices
			Log.d(TAG, "Starting rebroadcast checks");
			for (int i= 0; i<2; i++){
				if (streams[i] !=null && ! sockets[i].getRemoteDevice().getAddress().equals(chatMsg.from)){
					Log.d(TAG, String.format("Rebroadcasting, SOCKETADDRESS= %s, FROM=%s",
							sockets[i].getRemoteDevice().getAddress(),
							chatMsg.from));
					Chat.sendMessage(chatMsg, streams[i]);
					return; //this return ensures were only sending data one-way
				}
			}
			break;
			
		case DISPLAY_LOADING_DIALOG:
			message = (String) msg.obj;
			String dialogTitle = mActivity.getString(R.string.loading_dialog_title);
			MainActivity.loadingDialog = ProgressDialog.show(mActivity, dialogTitle, 
					message, true);
			break;
			
		case REMOVE_LOADING_DIALOG:
			if (MainActivity.loadingDialog != null){
				MainActivity.loadingDialog.dismiss();
			}
			break;
			
		case SOCKET_ESTABLISHED:
			MainActivity.socket = (BluetoothSocket) msg.obj;
			break;

		case SOCKET_LISTENING:
			MainActivity.serverButton.setText("Listening for connections..");
			break;

		case SOCKET_CONNECTING:
			MainActivity.clientButton.setText("Trying to connect..");
			break;
		
		case UPDATE_PROGRESSBAR:
			((ProgressBar)mActivity.findViewById(R.id.progressBar1)).setProgress(msg.arg1);
			break;
		}
		super.handleMessage(msg);
	}
	


}
