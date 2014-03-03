package ee.ut.cs.mc.and.pairerprototype;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ee.ut.cs.mc.and.pairerprototype.amplitudelogger.AmplitudeTask;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BTCommon;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BTCommunicator;
import ee.ut.cs.mc.and.pairerprototype.wifip2p.WifiP2pCommon;
import ee.ut.cs.mc.and.simplerecorder.RecorderActivity;

public class MainActivity extends Activity {

	/* 
	 * UI ELEMENTS
	 */
	static Button clientButton;
	static Button serverButton;
	EditText inputField;


	static BluetoothSocket socket = null;
	static Chat chatSession = null;
	AppRunningNotification runningNotification = null;
	WifiP2pCommon mWifiManager;
	
	/*
	 * STRING TAGS FOR LOGGING
	 */
	String appState = "App State:";

	MainActivityHandler mHandler = new MainActivityHandler(this);
	private String TAG = "MainActivity";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(appState, "onCreate");
		
		setContentView(R.layout.activity_main);

		clientButton = (Button) findViewById(R.id.btnStartBtClient);
		serverButton = (Button) findViewById(R.id.btnStartBtServer);
		inputField = (EditText) findViewById(R.id.inputField);


		runningNotification = new AppRunningNotification(this.getApplicationContext());
		runningNotification.display();

		//Check if bluetooth is enabled, prompt user to enable it
		BTCommon.checkPhoneSettings(this);
		
		mWifiManager = new WifiP2pCommon(this);
//		mWifiManager.checkPhoneSettings(0);
	}

	/** Called when the user clicks the 'open recorder activity' button */
	public void openRecorderActivity(View view) {
		Intent intent = new Intent(this, RecorderActivity.class);
		startActivity(intent);
	}

	public void logSingleSequence(View view) {
		AmplitudeTask ampTask = new AmplitudeTask(mHandler, this);
		ampTask.execute();

	}

	public void startBluetoothClient(View view){
		Log.i("", "Starting BT client in MainActivity");

		BTCommunicator btClient = new BTCommunicator(mHandler);
		String serverMACSII = "0C:DF:A4:71:6D:06";
		String serverMACXperia = "D0:51:62:93:E8:CE";
		btClient.setInsecureRfcomm(true);
		btClient.connectToServer(serverMACXperia);
	}

	public void startBluetoothServer(View view){
		Log.i("", "Starting BT server in MainActivity");

		BTCommunicator btServer = new BTCommunicator(mHandler);
		btServer.setInsecureRfcomm(true);
		btServer.startListening();
	}
	
	public void connectToWifi(View view){
		Log.i(TAG, "Connecting to WiFi device");
		mWifiManager.connectTo();
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


	void displayToast(CharSequence message) {
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
