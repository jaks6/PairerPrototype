package ee.ut.cs.mc.and.pairerprototype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import ee.ut.cs.mc.and.pairerprototype.amplitudelogger.AmplitudeUtils;

public class SyncTimeTask extends AsyncTask<Void, Integer, List<Long>> {

	private static final int REQUEST_INTERVAL_LENGTH = 2000;
	private static final int TIMEOUT_PERIOD = 3000;
	private static final int NO_OF_REQUESTS = 6;
	private static final String NTP_SERVER = "ntp.estpak.ee";

	long timeDiff = 0;
	Context context;
	private String TAG = "SyncTimeTask";

	public SyncTimeTask(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected List<Long> doInBackground(Void... values) {
		List<Long> timeDiffs = new ArrayList<Long>();
		Long time=(long) 0 ;
		int i = 0;
		while (i <= NO_OF_REQUESTS-1) {
			SntpClient client = new SntpClient();
			if (client.requestTime(NTP_SERVER, TIMEOUT_PERIOD)) {
				long now = client.getNtpTime()
						- client.getNtpTimeReference();
				Log.d("Time Difference", now+"");
				timeDiffs.add(now);
				time=now;
				i++;
				try {
					Thread.sleep(REQUEST_INTERVAL_LENGTH);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				publishProgress(i);
			}
		}
		return timeDiffs;
	}

	@Override
	protected void onPostExecute(List<Long> diffsList) {
		super.onPostExecute(diffsList);
		//get the minimum difference and assign to for use when timing recording starting moments
		AmplitudeUtils.TIME_DIFF = (Collections.min(diffsList));
		MainActivity.loadingDialog.dismiss();
	}

	@Override
	protected void onPreExecute() {
		//Display progress dialog to user
		String message = context.getString(R.string.loading_dialog_title);
		message = message.concat( "\nSyncing with time server...");
		
		MainActivity.loadingDialog = ProgressDialog.show(context, "", 
				message, true, true);
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		String message = values[0].toString() + "/" + NO_OF_REQUESTS; 
		MainActivity.loadingDialog.setMessage(message);
		super.onProgressUpdate(values);
	}  


}
