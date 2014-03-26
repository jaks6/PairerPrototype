package ee.ut.cs.mc.and.pairerprototype.amplitudelogger;

import java.util.ArrayList;

import ee.ut.cs.mc.and.pairerprototype.MainActivity;
import ee.ut.cs.mc.and.pairerprototype.network.PostSequenceTask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class CaptureTask extends AsyncTask<Void, Integer, ArrayList<String>> {

	int UPDATE_PROGRESSBAR = 2;
	String TAG = "CaptureTask";
	MaxAmplitudeRecorder mMaxAmpRecorder;
	ArrayList<String> capturedSequence;
	Handler handler;

	public CaptureTask(Handler handler, Context context) {
		this.handler = handler;
		
		//!TODO make the recording path some secure in-app location
		String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		mMaxAmpRecorder = new MaxAmplitudeRecorder(sdCardPath+  "/recording.3gp");
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		double progressPercentage = 100 * ( values[0] + 1.0)/AmplitudeUtils.NO_OF_SAMPLES_IN_SEQUENCE;
		
		Message msg = new Message();
		msg.what = UPDATE_PROGRESSBAR;
		msg.arg1 = (int) progressPercentage;
		handler.dispatchMessage(msg);
	}

	/** Creates a single timestamped max amplitude capturedSequence in the form
	 * of an arrayList, the first element of which is the timestamp
	 * @return 
	 */
	public ArrayList<String> recordSequence(){
		Log.d(TAG, "started capturedSequence capture");
		capturedSequence = new ArrayList<String>();

		mMaxAmpRecorder.start();
		long currentTime = SystemClock.elapsedRealtime();
		capturedSequence.add(""+ (currentTime + AmplitudeUtils.TIME_DIFF));
		
		capturedSequence.add(Build.MODEL);

		for (int i=0; i<AmplitudeUtils.NO_OF_SAMPLES_IN_SEQUENCE; i++){
			//gather a sample
			capturedSequence.add(""+ (long) mMaxAmpRecorder.mMediaRecorder.getMaxAmplitude());

			try {
				Thread.sleep(AmplitudeUtils.SAMPLING_INTERVAL);//!TODO replace with something more effective
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

			publishProgress(i);
		}
		mMaxAmpRecorder.mMediaRecorder.stop();
		return capturedSequence;
	}


	@Override
	protected ArrayList<String> doInBackground(Void... params) {
		return recordSequence();
	}


	@Override
	protected void onPostExecute(ArrayList<String> result) {
		Log.i("AMPLITUDE SEQUENCE READ - ", result.toString());
//		AmplitudeUtils.writeStringAsFile(result.toString(), "capturedSequence.txt");
		
		mMaxAmpRecorder.mMediaRecorder.release();
//		mMaxAmpRecorder.finish();
		
		/** Send recorded sequence to server */
		new PostSequenceTask(MainActivity.mNetworkmanager).execute(result.toString());
	}


}
