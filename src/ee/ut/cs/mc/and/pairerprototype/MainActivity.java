package ee.ut.cs.mc.and.pairerprototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.Bluetooth;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BluetoothCommon;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.Client;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.Server;
import ee.ut.cs.mc.and.simplerecorder.RecorderActivity;

public class MainActivity extends Activity {
	/*
     * Status indicators
     */
    static final int DOWNLOAD_COMPLETE = 2;
    static final int DECODE_STARTED = 3;
    public static final int TASK_COMPLETE = 4;

	final Handler mHandler = new Handler(){
		  @Override
		  public void handleMessage(Message msg) {
		    if(msg.what==TASK_COMPLETE){
		    	Toast.makeText(getApplicationContext(), (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
		    }
		    super.handleMessage(msg);
		  }
		};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button clientButton = (Button) findViewById(R.id.startBtClientBtn);
		Button serverButton = (Button) findViewById(R.id.startBtServerBtn);
		Bluetooth bluetoothHelper = new Bluetooth(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	/** Called when the user clicks the 'open recorder activity' button */
	public void openRecorderActivity(View view) {
	    Intent intent = new Intent(this, RecorderActivity.class);
	    startActivity(intent);
	}
	
	public void startBluetoothClient(View view){
		Client btClient = new Client(mHandler);
		String serverMAC = "0C:DF:A4:71:6D:06";
		btClient.initServerDevice(serverMAC);
	}
	
	public void startBluetoothServer(View view){
		Server btServer = new Server();
		btServer.startListening(BluetoothCommon.NAME, BluetoothCommon.MY_UUID, mHandler);
	}

}
