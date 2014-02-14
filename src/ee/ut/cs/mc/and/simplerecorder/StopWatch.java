package ee.ut.cs.mc.and.simplerecorder;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

public class StopWatch {
	Context context;

	private long startTime = 0;
	private int elapsedSecs = 0;
	private int elapsedMins = 0;
	private Handler mUIHandler;

	public StopWatch(Handler handler) {
		this.mUIHandler = handler;
	}
	public void start(){
		elapsedSecs = 0;
		elapsedMins = 0;
		startTime = SystemClock.uptimeMillis();

		mUIHandler.removeCallbacks(mUpdateTimeTask);
		mUIHandler.postDelayed(mUpdateTimeTask, 100);
	}

	public void stop(){
		mUIHandler.removeCallbacks(mUpdateTimeTask);

		int msgType = CommonUtilities.STOPWATCH_STOPPED;
		Message msg = mUIHandler.obtainMessage(msgType, elapsedMins, elapsedSecs);
		msg.obj = CommonUtilities.FOLDER_PATH;
		mUIHandler.sendMessage(msg);

	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {

			final long start = startTime;
			long millis = SystemClock.uptimeMillis() - start;
			elapsedSecs= (int) (millis / 1000);
			elapsedMins = elapsedSecs / 60;
			elapsedSecs     = elapsedSecs % 60;

			int msgType = CommonUtilities.STOPWATCH_RUNNING;
			Message msg = mUIHandler.obtainMessage(msgType, elapsedMins, elapsedSecs);
			mUIHandler.sendMessage(msg);

			mUIHandler.postAtTime(this,
					start + (((elapsedMins * 60) + elapsedSecs + 1) * 1000));
		}
	};


}
