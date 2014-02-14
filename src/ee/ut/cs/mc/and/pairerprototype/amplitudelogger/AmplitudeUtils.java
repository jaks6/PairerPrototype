package ee.ut.cs.mc.and.pairerprototype.amplitudelogger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class AmplitudeUtils {

	public static String TAG = "Amplitude Utilities";
	public static int timeDiff = 0;
	public static int SAMPLING_INTERVAL = 200;
	public static int NO_OF_SAMPLES_IN_SEQUENCE = 50; //50*200ms = 10s
	
	
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
}
