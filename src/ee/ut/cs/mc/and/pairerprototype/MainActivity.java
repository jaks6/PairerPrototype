package ee.ut.cs.mc.and.pairerprototype;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ee.ut.cs.mc.and.pairerprototype.amplitudelogger.AmplitudeUtils;
import ee.ut.cs.mc.and.pairerprototype.amplitudelogger.CaptureTask;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BTCommunicator;
import ee.ut.cs.mc.and.pairerprototype.network.NetworkManager;
import ee.ut.cs.mc.and.simplerecorder.RecorderActivity;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	/* 
	 * UI ELEMENTS
	 */
	static Button clientButton;
	static Button serverButton;
	Button toggleCaptureButton;
	EditText inputField;
	public static ProgressDialog loadingDialog;
	AppRunningNotification runningNotification = null;


	static BluetoothSocket socket = null;
	static Chat chatSession = null;
	public static NetworkManager mNetworkmanager;
	ScheduledThreadPoolExecutor sch;

	static boolean isCapturing = false;

	/*
	 * STRING TAGS FOR LOGGING
	 */
	String appState = "App State:";

	MainActivityHandler mHandler = new MainActivityHandler(this);


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(appState, "onCreate");

		setContentView(R.layout.activity_main);
		clientButton = (Button) findViewById(R.id.btnStartBtClient);
		serverButton = (Button) findViewById(R.id.btnStartBtServer);
		toggleCaptureButton = (Button) findViewById(R.id.btnAmplitudeCapture);
		inputField = (EditText) findViewById(R.id.inputField);

		runningNotification = new AppRunningNotification(this.getApplicationContext());
		runningNotification.display();


		mNetworkmanager = new NetworkManager(this);

		//Check if bluetooth is enabled, prompt user to enable it
		//		BTCommon.checkPhoneSettings(this);

		//find device's difference to a server
		try {
			doTimeSync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void doTimeSync() throws InterruptedException {
		new SyncTimeTask(this).execute();
	}

	/** Called when the user clicks the 'open recorder activity' button */
	public void openRecorderActivity(View view) {
		Intent intent = new Intent(this, RecorderActivity.class);
		startActivity(intent);
	}

	public void toggleAmplitudeCapture(View view) {
		if (!isCapturing){
			toggleCaptureButton
			.setText(getString(R.string.captureSequences_turnOff));

			isCapturing = true;
			createScheduledThreadPool();

		} else {
			toggleCaptureButton
			.setText(getString(R.string.captureSequences_turnOn));

			isCapturing = false;
			sch.shutdownNow();

		}
	}

	private void createScheduledThreadPool() {
		sch = (ScheduledThreadPoolExecutor)
				Executors.newScheduledThreadPool(3);
		System.out.println("Submission Time: " + SystemClock.elapsedRealtime());

		Thread doCaptureTask = AmplitudeUtils.doCaptureTask(mHandler, this);
		long initialDelay = calculateInitialDelay();
		Log.d(TAG, "initDelay= " + initialDelay);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean scheduleOnce = prefs.getBoolean("pref_record_once", false);

		if (scheduleOnce){
			Log.d(TAG, "scheduling *ONE* recording");
			sch.schedule(doCaptureTask, initialDelay, TimeUnit.MILLISECONDS);
			isCapturing=false;
			toggleCaptureButton.setText(R.string.captureSequences_turnOn);

		} else {
			Log.d(TAG, "SCHEDULING Periodic recording");
			ScheduledFuture<?> periodicFuture = sch.scheduleAtFixedRate(doCaptureTask, initialDelay, 10000, TimeUnit.MILLISECONDS);
		}
	}

	private long calculateInitialDelay() {
		long delay = 0;
		long curTime = SystemClock.elapsedRealtime() + AmplitudeUtils.TIME_DIFF;
		long upToLastFour = curTime / 10000;
		long LastFourNullified = upToLastFour * 10000;   // same no of digits as curTime, but last four are 0-s
		long lastFour = curTime- ( upToLastFour *10000);

		if (lastFour > 9000){
			delay = 20000 - (SystemClock.elapsedRealtime() + AmplitudeUtils.TIME_DIFF - LastFourNullified) ;
		} else {
			delay = 10000 - (SystemClock.elapsedRealtime() + AmplitudeUtils.TIME_DIFF - LastFourNullified);
		}
		return delay;
	}

	public void startBluetoothClient(View view){
		Log.i("", "Starting BT client in MainActivity");

		BTCommunicator btClient = new BTCommunicator(mHandler);
		String serverMACSII = "0C:DF:A4:71:6D:06";
		String serverMACXperia = "D0:51:62:93:E8:CE";
		String serverNexus5 = "CC:FA:00:16:2B:9A";
		btClient.setInsecureRfcomm(true);
		btClient.connectToServer(serverNexus5);
	}

	public void startBluetoothServer(View view){
		Log.i("", "Starting BT server in MainActivity");

		BTCommunicator btServer = new BTCommunicator(mHandler);
		btServer.setInsecureRfcomm(true);
		btServer.startListening();
	}

	public void openSettingsActivity(View view){
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return false;

		case R.id.action_connect:
			mNetworkmanager.interactWithServer(inputField.getText().toString());
			return false;

		default:
			return super.onOptionsItemSelected(item);
		}
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
