package ee.ut.cs.mc.and.pairerprototype;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

public class MainActivityHandler extends Handler {
	/*
	 * Status codes
	 */
	public static final int DISPLAY_TOAST = 1;
	public static final int UPDATE_PROGRESSBAR = 2;
	public static final int DISPLAY_LOADING_DIALOG = 3;
	public static final int REMOVE_LOADING_DIALOG = 4;
	public static final int BT_CONNECTION_ESTABLISHED = 5;
	public static final int MESSAGE_READ = 6;
	
	public static final int SOCKET_ESTABLISHED = 11;
	public static final int SOCKET_LISTENING = 12;
	public static final int SOCKET_CONNECTING = 13;
	private static final int LOADING_DIALOG_ADDTEXT = 0;
	
	private Activity mActivity;
	
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

		case MESSAGE_READ:
			//convert read bytes into a string and display them
			message = new String((byte[]) msg.obj, 0, msg.arg1);
			((MainActivity) mActivity).displayInChat(message);
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
			MainActivity.chatSession = new Chat(this, MainActivity.socket);
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
