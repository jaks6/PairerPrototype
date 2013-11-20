package ee.ut.cs.mc.and.simplerecorder;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;

public class StopWatch {
	Context context;

	private long startTime = 0;
	private int elapsedSecs = 0;
	private int elapsedMins = 0;
	private Handler mHandler = new Handler();

	private final WeakReference<RecorderActivity> mTarget;
	
	public StopWatch(RecorderActivity target) {
		mTarget = new WeakReference<RecorderActivity>(target);
	}
	public void start(){
		elapsedSecs = 0;
		elapsedMins = 0;
		startTime = SystemClock.uptimeMillis();
		
		mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	public void stop(){
		RecorderActivity target = mTarget.get();
		
		int msgType = CommonUtilities.NOTIFICATION_RECORDING_STOPPED;
		target.updateNotificationArea(msgType, CommonUtilities.FOLDER_PATH, elapsedMins, elapsedSecs);
		
		mHandler.removeCallbacks(mUpdateTimeTask);

	}

	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   RecorderActivity target = mTarget.get();
			   
		       final long start = startTime;
		       long millis = SystemClock.uptimeMillis() - start;
		       elapsedSecs= (int) (millis / 1000);
		       elapsedMins = elapsedSecs / 60;
		       elapsedSecs     = elapsedSecs % 60;

		       int msgType = CommonUtilities.NOTIFICATION_RECORDING_ONGOING;
		       target.updateNotificationArea(msgType, elapsedMins, elapsedSecs);
		     
		       mHandler.postAtTime(this,
		               start + (((elapsedMins * 60) + elapsedSecs + 1) * 1000));
		   }
		};


}
