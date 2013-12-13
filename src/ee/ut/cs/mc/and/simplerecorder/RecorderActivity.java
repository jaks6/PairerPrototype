package ee.ut.cs.mc.and.simplerecorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import ee.ut.cs.mc.and.pairerprototype.R;

public class RecorderActivity extends Activity {
	
	static StopWatch stopwatch;
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
		
		if (stopwatch == null){
			stopwatch = new StopWatch(mHandler);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		//Check if a recording was ongoing.
		Log.i("onResume", "Recording status="+recording);
		if (recording) recordButton.setText(R.string.RecordButtonRecording);
	}
	
	
	final Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Log.i("activity_handleMessage", "msg-what="+msg.what);
			switch (msg.what){
			case CommonUtilities.STOPWATCH_ONGOING:
				updateNotificationArea(msg.what, msg.arg1, msg.arg2);
				break;
			case CommonUtilities.STOPWATCH_STOPPED:
				updateNotificationArea(msg.what, msg.obj, msg.arg1,msg.arg2);
		}
		super.handleMessage(msg);

	}
	};
	
	
	
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
			case (CommonUtilities.STOPWATCH_STOPPED):
				notificationMsg = getResources().getString(R.string.recordingStoppedReport);
				formattedMsg = String.format(notificationMsg, msgs );
			break;
			case (CommonUtilities.STOPWATCH_ONGOING):
				notificationMsg = getResources().getString(R.string.recordingSeconds);
				formattedMsg = String.format(notificationMsg, msgs);
				break;
		}
		notifications.setText(formattedMsg);
	}
}
