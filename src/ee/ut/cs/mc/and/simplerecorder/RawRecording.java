package ee.ut.cs.mc.and.simplerecorder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class RawRecording extends IntentService {
	public RawRecording(String name) {
		super(name);
	}
	public RawRecording() {
		super("RawRecordingService");
	}

	//Emulator only supports samplerate 8000. On an actual device, use 44100 for example
	//!TODO - write an automatic check to determine sample rate
	private final int RECORDER_SAMPLERATE = 44100;
	private final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	// the minimum buffer size needed for audio recording
	private int bufferSizeInBytes = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

	private String filePath = null;
	private int sourceMic = MediaRecorder.AudioSource.CAMCORDER;
	AudioRecord audioRecord = null;

	@Override
	protected void onHandleIntent(Intent workIntent) {
		// Gets data from the incoming Intent
		//		 String dataString = workIntent.getDataString();
		// Do work here, based on the contents of dataString
		doRecord();
	}


	public void doRecord(){
		Log.i("", "Creating the Audio Client with minimum buffer of "
				+ bufferSizeInBytes + " bytes");
		audioRecord = new AudioRecord(sourceMic, RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING,bufferSizeInBytes);

		audioRecord.startRecording();
		RecorderActivity.recording = true;
		filePath = CommonUtilities.FOLDER_PATH +  "/recording.pcm";
		FileOutputStream os = null;
		byte Data[] = new byte[bufferSizeInBytes];

		try {
			os = new FileOutputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while(RecorderActivity.recording) {
			audioRecord.read(Data, 0, Data.length);
			try {
				os.write(Data, 0, bufferSizeInBytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		//Recording has ended, finish up
		stopRecording();
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopRecording() {
		audioRecord.stop();
		audioRecord.release();
	}



}
