package ee.ut.cs.mc.and.pairerprototype.amplitudelogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

public class AmplitudeUtils {

	public static final String TAG = "Amplitude Utilities";
	public static final int SAMPLING_INTERVAL = 140;
	public static final int NO_OF_SAMPLES_IN_SEQUENCE = 50; // 50*140ms = 7s
	public static long TIME_DIFF = 0;


	public static void writeStringAsFile(final String fileContents, String fileName) {
		try {
			String path = Environment.getExternalStorageDirectory().getAbsolutePath();
			FileWriter out = new FileWriter(new File(path, fileName));
			out.write(fileContents);
			out.close();
		} catch (IOException e) {
			//!TODO
		}
	}
	public static Thread doCaptureTask(final Handler handler, final Context context){
		return new Thread() {       
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {       
						try {
							CaptureTask captureTask = new CaptureTask(handler, context);
							captureTask.execute();
						} catch (Exception e) {
						}
					}
				});
			}
		};
	}
}
