package ee.ut.cs.mc.and.pairerprototype.amplitudelogger;

import java.io.IOException;

import android.media.MediaRecorder;
import android.util.Log;

public class MaxAmplitudeRecorder {
	
	String TAG = "MaxAmplitudeRecorder";
	MediaRecorder mMediaRecorder;
	
	public MaxAmplitudeRecorder(String sdCardPath) {
		try {
			prepareMediaRecorder(sdCardPath);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// when an error occurs just stop recording
		mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener(){
			@Override
			public void onError(MediaRecorder mr, int what, int extra){
				Log.e(TAG, "Error on MediaRecorder");
			}
		});

	}
	
	public void finish(){
		mMediaRecorder.stop();
	}


	/** Initializes the MediaRecorder which is used to gather
	 * maximum heard amplitude data.
	 * @param sdCardPath
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	private void prepareMediaRecorder(String sdCardPath) throws IllegalStateException, IOException {
		
			mMediaRecorder = new MediaRecorder();

			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mMediaRecorder.setOutputFile(sdCardPath);
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			
			
			Log.d("AudioUtil", "recording to: " + sdCardPath);
			mMediaRecorder.prepare();
		
	}

	public void start() {
		//possible RuntimeException if Audio recording channel is occupied
		mMediaRecorder.start();
		mMediaRecorder.getMaxAmplitude(); // First call always returns 0
		
	}
	
	
}
