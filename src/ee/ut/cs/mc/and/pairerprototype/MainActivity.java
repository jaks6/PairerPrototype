package ee.ut.cs.mc.and.pairerprototype;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;

import ee.ut.cs.mc.and.pairerprototype.amplitudelogger.AmplitudeUtils;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BTCommon;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BluetoothHelper;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.ClientSocketThread;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.ServerSocketThread;
import ee.ut.cs.mc.and.pairerprototype.network.NetworkManager;
import ee.ut.cs.mc.and.simplerecorder.RecorderActivity;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private static final int TEXT_ID = 0;
	private static final int FILEUTILS_REQUEST_CHOOSER = 1234;
	/* 
	 * UI ELEMENTS
	 */
	static Button clientButton;
	static Button serverButton;
	Button toggleCaptureButton;
	EditText inputField;
	public static GroupList groupList;
	public static ProgressDialog loadingDialog;
	AppRunningNotification runningNotification = null;


	static BluetoothSocket socket = null;
	static NetworkManager mNetworkmanager;
	ScheduledThreadPoolExecutor scheduledThreadPool;

	static boolean isCapturing = false;

	MainActivityHandler mHandler;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");

		setContentView(R.layout.activity_main);
		groupList = (GroupList) findViewById(R.id.groupList);
		clientButton = (Button) findViewById(R.id.btnStartBtClient);
		serverButton = (Button) findViewById(R.id.btnStartBtServer);
		toggleCaptureButton = (Button) findViewById(R.id.btnAmplitudeCapture);
		inputField = (EditText) findViewById(R.id.inputField);

		runningNotification = new AppRunningNotification(this.getApplicationContext());
		runningNotification.display();

		mHandler = new MainActivityHandler(this);
		App.setMainActivityHandler(mHandler);

		mNetworkmanager = NetworkManager.getInstance();
		String nick = App.getPrefs().getString("pref_nickname", "MyNickname");
		Log.i(TAG, "Nick = " + nick);
		if (App.getPrefs().getString("pref_nickname", "MyNickname").equals("MyNickname")) createDialog();
		//Check if bluetooth is enabled, prompt user to enable it
		BTCommon.checkPhoneSettings(this);

		//Sync time to NTP server
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
			scheduledThreadPool.shutdownNow();
		}
	}

	private void createScheduledThreadPool() {
		scheduledThreadPool = (ScheduledThreadPoolExecutor)
				Executors.newScheduledThreadPool(3);

		long initialDelay = SntpClient.calculateInitialDelay();

		boolean scheduleOnce = App.getPrefs().getBoolean("pref_record_once", false);
		boolean dontSync = App.getPrefs().getBoolean("pref_nosync", false);

		initialDelay = (dontSync)? 0: initialDelay;
		Log.d(TAG, "initDelay= " + initialDelay);

		Thread doCaptureTask = AmplitudeUtils.doCaptureTask(mHandler, this);
		if (scheduleOnce){
			Log.d(TAG, "scheduling *ONE* recording");
			scheduledThreadPool.schedule(doCaptureTask, initialDelay, TimeUnit.MILLISECONDS);
			isCapturing=false;
			toggleCaptureButton.setText(R.string.captureSequences_turnOn);

		} else {
			Log.d(TAG, "SCHEDULING Periodic recording");
			ScheduledFuture<?> periodicFuture = scheduledThreadPool.scheduleAtFixedRate(doCaptureTask, initialDelay, 10000, TimeUnit.MILLISECONDS);
		}
	}


	public void startBluetoothClient(View view){
		Log.i("", "Starting BT client in MainActivity");

		BluetoothHelper btClient = BluetoothHelper.getInstance();
		btClient.setHandler(mHandler);
		String client1 = "CC:FA:00:16:2E:A4";
		String client3 = "CC:FA:00:16:1E:6C";
		String client4 = "CC:FA:00:16:2E:8A";
		String serverMACXperia = "D0:51:62:93:E8:CE";
		String serverNexus5 = "CC:FA:00:16:2B:9A";
		btClient.setInsecureRfcomm(true);

		//HACK FOR TESTING:
//		String server =(BTCommon.deviceMAC.equals(serverNexus5))? serverMACXperia:serverNexus5;

		btClient.connectToServer(serverNexus5);
	}

	public void startBluetoothServer(View view){
		Log.i("", "Starting BT server in MainActivity");

		BluetoothHelper btServer = BluetoothHelper.getInstance();
		btServer.setHandler(mHandler);
		btServer.setInsecureRfcomm(true);
		for (int i = 0; i<3; i++){
			btServer.startListening();
		}
	}

	public void openSettingsActivity(View view){
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	public void sendChatData(View view) throws Exception{
		//Obtain the string from the textinput, reset the inputField:
		//		ChatMsg chatmsg = new ChatMsg( inputField.getText().toString().trim());

		String destinationNick = "*";
		BTMessage chatMsg = new BTMessage(inputField.getText().toString().trim(), destinationNick);
		displayInChat(chatMsg.toString());
		inputField.setText("");

		//write data to all bt sockets:
		//		TransferUtils.sendMessage(chatmsg);
		TransferUtils.sendMessage(chatMsg);
	}

	public void sendFile(View v){
		// Create the ACTION_GET_CONTENT Intent
		Intent getContentIntent = FileUtils.createGetContentIntent();

		Intent intent = Intent.createChooser(getContentIntent, "Select a file");
		startActivityForResult(intent, FILEUTILS_REQUEST_CHOOSER);


	}

	public void displayInChat(CharSequence charseq){
		TextView tv = (TextView) findViewById(R.id.chatTextView);
		tv.setText(tv.getText().toString() +"\n"+charseq);
	}

	void displayToast(CharSequence message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Create and return an example alert dialog with an edit text box.
	 * @return 
	 */
	private void createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Hello User");
		builder.setMessage("What is your name:");
		// Use an EditText view to get user input.
		final EditText input = new EditText(this);
		input.setId(TEXT_ID);
		builder.setView(input);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				Editor editor = App.getPrefs().edit();
				editor.putString("pref_nickname", value);
				editor.commit();
				return;
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		builder.create().show();
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
		if (item.getItemId() == R.id.action_settings){
			startActivity(new Intent(this, SettingsActivity.class));
			return false;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if ((requestCode == BTCommon.REQUEST_ENABLE_BT) && (resultCode == Activity.RESULT_OK)){
			BTCommon.handleBluetoothTurnedOnEvent();
		} else if (requestCode == FILEUTILS_REQUEST_CHOOSER){
			if (resultCode == RESULT_OK) {
				MainActivity.loadingDialog.setMessage("Sending file via bluetooth..");
				MainActivity.loadingDialog.show();
				transmitFile(data);
			}
		}
	}

	private void transmitFile(final Intent data) {
		final Uri uri = data.getData();
		// Get the File path from the Uri
		final String path = FileUtils.getPath(this, uri);
		new Thread(new Runnable(){
			@Override
			public void run() {
				// Alternatively, use FileUtils.getFile(Context, Uri)
				if (path != null && FileUtils.isLocal(path)) {
					File file = new File(path);
					Log.i(TAG, "FILE " + path);
					String recipient = groupList.getRecipient();
					TransferUtils.sendMessage(new BTMessage(file, recipient));

					MainActivity.loadingDialog.dismiss();
				}
			}
		}).start();

	}

	@Override
	protected void onResume(){
		super.onResume();
		//Restore chat session, manage connection
		Log.i(TAG, "onResume");
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		//Release resources - kill running threads.

		Log.i(TAG, "onDestroy");
		ServerSocketThread.getInstance().cancel();
		for(ClientSocketThread thread: BTCommon.clientSocketList){
			thread.cancel();
		}
		if (scheduledThreadPool != null) scheduledThreadPool.shutdownNow();
		runningNotification.remove();
		System.exit(0);

	}
	@Override
	protected void onPause() {
		super.onPause();
		// Another activity is taking focus (this activity is about to be "paused").
		Log.i(TAG, "onPause");
	}
	@Override
	protected void onStop() {
		super.onStop();
		// The activity is no longer visible (it is now "stopped")
		Log.i(TAG, "onStop");
	}

}
