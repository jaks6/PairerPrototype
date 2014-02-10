package ee.ut.cs.mc.and.pairerprototype;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BTCommon;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BTCommunicator;
import ee.ut.cs.mc.and.simplerecorder.RecorderActivity;

public class MainActivity extends Activity {

	/*
	 * Status codes
	 */
	public static final int DISPLAY_TOAST = 1;
	public static final int BT_CONNECTION_ESTABLISHED = 4;
	public static final int SOCKET_ESTABLISHED = 11;
	public static final int SOCKET_LISTENING = 12;
	public static final int SOCKET_CONNECTING = 13;
	public static final int MESSAGE_READ = 5;


	/* 
	 * UI ELEMENTS
	 */
	Button clientButton;
	Button serverButton;
	EditText inputField;


	static BluetoothSocket socket = null;
	static Chat chatSession = null;
	AppRunningNotification runningNotification = null;

	/*
	 * STRING TAGS FOR LOGGING
	 */
	String appState = "App State:";

	final Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Log.i("", "msg-what="+msg.what);
			switch (msg.what){

			case DISPLAY_TOAST:
				displayToast((CharSequence)msg.obj);
				break;

			case BT_CONNECTION_ESTABLISHED:
				displayToast( (CharSequence)msg.obj);
				if (msg.arg1 == 1){ //1 for server side, 2 for client
					serverButton.setText("*Connected*");
				} else if (msg.arg1 == 2){
					clientButton.setText("*Connected*");
				}
				break;

			case MESSAGE_READ:
				//convert read bytes into a string and display them
				String message = new String((byte[]) msg.obj, 0, msg.arg1);
				displayInChat(message);
				break;

			case SOCKET_ESTABLISHED:
				socket = (BluetoothSocket) msg.obj;
				chatSession = new Chat(this, socket);
				break;

			case SOCKET_LISTENING:
				serverButton.setText("Listening for connections..");
				break;

			case SOCKET_CONNECTING:
				clientButton.setText("Trying to connect..");
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(appState, "onCreate");
		setContentView(R.layout.activity_main);

		clientButton = (Button) findViewById(R.id.startBtClientBtn);
		serverButton = (Button) findViewById(R.id.startBtServerBtn);
		inputField = (EditText) findViewById(R.id.inputField);


		runningNotification = new AppRunningNotification(this.getApplicationContext());
		runningNotification.display();

		//Check if bluetooth is enabled, prompt user to enable it
		BTCommon.checkPhoneSettings(this);
	}

	/** Called when the user clicks the 'open recorder activity' button */
	public void openRecorderActivity(View view) {
		Intent intent = new Intent(this, RecorderActivity.class);
		startActivity(intent);
	}

	public void startBluetoothClient(View view){
		Log.i("", "Starting BT client in MainActivity");

		BTCommunicator btClient = new BTCommunicator(mHandler);
		String serverMAC = "0C:DF:A4:71:6D:06";
		btClient.connectToServer(serverMAC);
	}

	public void startBluetoothServer(View view){
		Log.i("", "Starting BT server in MainActivity");

		BTCommunicator btServer = new BTCommunicator(mHandler);
		btServer.startListening();
	}

	public void sendChatData(View view) throws Exception{
		//Obtain the string from the textinput, reset the inputField:
		String message = inputField.getText().toString().trim();
		displayInChat("me: "+ message);
		inputField.setText("");

		//write data to bt socket:
		if (chatSession !=null){
			chatSession.sendMessage(message);
		} else {
			displayToast("Chat class not initialized. Is a data connetion running?");
		}
	}

	public void displayInChat(CharSequence charseq){
		TextView tv = (TextView) findViewById(R.id.chatTextView);
		tv.setText(tv.getText().toString() +"\n"+charseq);
	}


	private void displayToast(CharSequence message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	


	/*
	 * ACTIVITY LIFE CYCLE RELATED METHODS
	 */


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume(){
		super.onResume();
		//Restore chat session, manage connection
		Log.i(appState, "onResume");
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		//Release resources - kill running threads.
		
		Log.i(appState, "onDestroy");
		runningNotification.remove();
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		// Another activity is taking focus (this activity is about to be "paused").
		Log.i(appState, "onPause");
	}
	@Override
	protected void onStop() {
		super.onStop();
		// The activity is no longer visible (it is now "stopped")
		Log.i(appState, "onStop");
	}



}
