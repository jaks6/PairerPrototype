package ee.ut.cs.mc.and.pairerprototype;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
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
	public static final int BT_MESSAGE_READ = 6;

	public static final int UPDATE_GROUP = 7;

	public static final int SOCKET_ESTABLISHED = 11;
	public static final int SOCKET_LISTENING = 12;
	public static final int SOCKET_CONNECTING = 13;

	static TransferUtils chatSession = null;
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
			BTMessage btMessage = (BTMessage) msg.obj;
			Log.i(TAG, "Handling message from" + btMessage.fromMAC + ", to = "+ btMessage.destinationNick);
			if (btMessage.destinationNick.equals(App.getUserNick()) ||
					btMessage.destinationNick.equals("*")){
				//we are the intended recipients
				Log.i(TAG, "this message is for me");
				if (btMessage.type == BTMessage.TYPE_CHATMESSAGE){
					handleChatMessage(btMessage);
				} else if (btMessage.type == BTMessage.TYPE_FILE){
					handleFileMessage(btMessage);
				}
			}
			if (btMessage.destinationNick.equals(App.getUserNick())){
				break;
			} else {
				TransferUtils.rebroadCastMsg(btMessage);
			}

			break;

		case DISPLAY_LOADING_DIALOG:
			message = (String) msg.obj;
			String dialogTitle = mActivity.getString(R.string.loading_dialog_title);
			MainActivity.loadingDialog = ProgressDialog.show(mActivity, dialogTitle, 
					message, true);
			break;

		case UPDATE_GROUP:
			JSONArray group = (JSONArray) msg.obj;
			Log.i(TAG, "group json array="+ group.toString());
			ArrayList<String> groupList = new ArrayList<String>();
			groupList.add("*Select All*");
			for (int i = 0; i<group.length(); i++){
				try {
					groupList.add(group.getString(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					mActivity,  android.R.layout.simple_list_item_1, groupList);
			MainActivity.groupList.setAdapter(adapter);
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

	private void handleFileMessage(BTMessage btMessage) {
		Log.i(TAG, "Handling received file=" + btMessage.fileContent.getAbsolutePath() +"size "+ btMessage.fileContent.getTotalSpace());

		File localFile = null;
		try {
			localFile = btMessage.getFileFromBytes();
			Intent openIntent = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.fromFile(localFile);

			String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(uri.toString());
			String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
			openIntent.setDataAndType(uri,mimetype);
			mActivity.startActivity(openIntent);
		} catch (IOException e) {
			((MainActivity) mActivity).displayToast((CharSequence)"problem opening file");
		}
	}

	private void handleChatMessage(BTMessage btMessage) {
		//Display the msg in main chat window
		((MainActivity) mActivity).displayInChat(btMessage.toString());
		// Check if it should be broadcasted to other devices and do so.
		TransferUtils.rebroadCastMsg(btMessage);
	}



}
