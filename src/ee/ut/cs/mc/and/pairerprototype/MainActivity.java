package ee.ut.cs.mc.and.pairerprototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.Bluetooth;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BluetoothCommon;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.Client;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.Server;
import ee.ut.cs.mc.and.simplerecorder.RecorderActivity;

public class MainActivity extends Activity {

	public Button clientButton = (Button) findViewById(R.id.startBtClientBtn);
	public Button serverButton = (Button) findViewById(R.id.startBtServerBtn);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
		Client btClient = new Client(this);
		String serverMAC = "43:25:C4:96:4B:AA";
		btClient.initServerDevice(serverMAC);
		
	}
	
	public void startBluetoothServer(View view){
		Server btServer = new Server();
		btServer.startListening(BluetoothCommon.NAME, BluetoothCommon.MY_UUID, this);
	}

}
