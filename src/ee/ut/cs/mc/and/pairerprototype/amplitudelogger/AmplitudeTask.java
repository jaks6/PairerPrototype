package ee.ut.cs.mc.and.pairerprototype.amplitudelogger;

import java.util.ArrayList;

import ee.ut.cs.mc.and.pairerprototype.MainActivity;
import ee.ut.cs.mc.and.pairerprototype.network.PostSequenceTask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AmplitudeTask extends AsyncTask<Void, Integer, ArrayList<Long>> {

	int UPDATE_PROGRESSBAR = 2;
	String TAG = "AmplitudeTask";
	MaxAmplitudeRecorder mMaxAmpRecorder;
	ArrayList<Long> capturedSequence;
	Handler mHandler;

	public AmplitudeTask(Handler handler, Context context) {
		this.mHandler = handler;
		
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
		mHandler.dispatchMessage(msg);
	}

	/** Creates a single timestamped max amplitude capturedSequence in the form
	 * of an arrayList, the first element of which is the timestamp
	 * @return 
	 */
	public ArrayList<Long> recordSequence(){
		Log.d(TAG, "started capturedSequence capture");
		capturedSequence = new ArrayList<Long>();

		mMaxAmpRecorder.start();
		long currentTime = System.currentTimeMillis();
		
		capturedSequence.add(currentTime + AmplitudeUtils.timeDiff);

		for (int i=0; i<AmplitudeUtils.NO_OF_SAMPLES_IN_SEQUENCE; i++){
			//gather a sample
			capturedSequence.add((long) mMaxAmpRecorder.mMediaRecorder.getMaxAmplitude());

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
	protected ArrayList<Long> doInBackground(Void... params) {
		return recordSequence();
	}


	@Override
	protected void onPostExecute(ArrayList<Long> result) {
		Log.i("AMPLITUDE SEQUENCE READ - ", result.toString());
		AmplitudeUtils.writeStringAsFile(result.toString(), "capturedSequence.txt");
		
		mMaxAmpRecorder.mMediaRecorder.release();
//		mMaxAmpRecorder.finish();
		
		/** Send recorded sequence to server */
		new PostSequenceTask(MainActivity.mNetworkmanager).execute(result.toString());
	}


}
