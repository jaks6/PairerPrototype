package ee.ut.cs.mc.and.pairerprototype.amplitudelogger;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import ee.ut.cs.mc.and.pairerprototype.MainActivity;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BTCommon;
import ee.ut.cs.mc.and.pairerprototype.network.PostSequenceTask;

public class CaptureTask extends AsyncTask<Void, Integer, JSONObject> {

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
	 * @throws JSONException 
	 * @throws InterruptedException 
	 */
	public JSONObject recordSequence(JSONObject json) throws JSONException, InterruptedException{
		Log.d(TAG, "started capturedSequence capture");

		mMaxAmpRecorder.start();
		long currentTime = SystemClock.elapsedRealtime();
		json.put("timestamp", currentTime + AmplitudeUtils.TIME_DIFF);
		json.put("device", Build.MODEL);
		json.put("mac", BTCommon.deviceMAC);

		JSONArray sequenceJson = new JSONArray();
		for (int i=0; i<AmplitudeUtils.NO_OF_SAMPLES_IN_SEQUENCE; i++){
			//gather a sample
			int sample = mMaxAmpRecorder.mMediaRecorder.getMaxAmplitude();
			sequenceJson.put(sample);
			Thread.sleep(AmplitudeUtils.SAMPLING_INTERVAL);//!TODO replace with something more effective

			publishProgress(i);
		}
		json.put("sequence", sequenceJson);
		
		return json;
	}


	@Override
	protected JSONObject doInBackground(Void... params) {
		JSONObject json = new JSONObject();
		try {
			return recordSequence(json);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return json;
	}


	@Override
	protected void onPostExecute(JSONObject result) {
		Log.i("AMPLITUDE SEQUENCE READ - ", result.toString());
		
		mMaxAmpRecorder.mMediaRecorder.release();
//		mMaxAmpRecorder.finish();
		
		/** Send recorded sequence to server */
		new PostSequenceTask(MainActivity.mNetworkmanager, handler).execute(result);
		
	}
}
