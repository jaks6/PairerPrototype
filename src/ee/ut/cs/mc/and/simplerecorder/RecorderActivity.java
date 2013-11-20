package ee.ut.cs.mc.and.simplerecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ee.ut.cs.mc.and.pairerprototype.R;

public class RecorderActivity extends Activity {
	
	StopWatch stopwatch = new StopWatch(this);
	static boolean recording = false;
	Button recordButton;
	public static TextView notifications;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recorder_activity);
		CommonUtilities.FOLDER_PATH = Environment.getExternalStorageDirectory().getPath();
		
		recordButton = (Button) findViewById(R.id.Btn_Record);
		notifications = (TextView) findViewById(R.id.Textview_notifications);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public void handleClick(View v){
		
		if (!recording){
			Intent intent = new Intent(this, RawRecording.class);
			startService(intent);
			
			stopwatch.start();
			notifications.setText("Recording...");
			recordButton.setText(R.string.RecordButtonRecording);
			
		} else {
			recording = false;
			stopwatch.stop();
			recordButton.setText(R.string.RecordButton);
		}
	}
	
	public void updateNotificationArea(int msgType, Object...msgs){
		String notificationMsg = "";
		String formattedMsg = "";
		
		switch (msgType){
			case (CommonUtilities.NOTIFICATION_RECORDING_STOPPED):
				notificationMsg = getResources().getString(R.string.recordingStoppedReport);
				formattedMsg = String.format(notificationMsg, msgs );
			break;
			case (CommonUtilities.NOTIFICATION_RECORDING_ONGOING):
				notificationMsg = getResources().getString(R.string.recordingSeconds);
				formattedMsg = String.format(notificationMsg, msgs);
				break;
		}
		
		notifications.setText(formattedMsg);
	}
}
