package ee.ut.cs.mc.and.pairerprototype.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import ee.ut.cs.mc.and.pairerprototype.MainActivity;
import ee.ut.cs.mc.and.pairerprototype.MainActivityHandler;

public class NetworkManager {
	Handler mHandler;
	Context context;
	Activity activity;
	
	URL url;
	URLConnection connection;
	OutputStreamWriter out;
	
	public NetworkManager(Activity activity){
		this.activity = activity;
	}

	public void initialize(){
		//get IP from preferences, default to 0.0.0.0 if unsuccessful
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
		final String serverIP = "http://" + prefs.getString("pref_serverip", "0.0.0.0");
		
		new Thread(new Runnable() {
			public void run() {
				try{
					URL url = new URL(serverIP + "/Server/Sequence");
					Log.d("URL", url.toString());
					connection = url.openConnection();
					connection.setDoOutput(true);

				}catch(Exception e)
				{
					Log.d("Exception",e.toString());
				}

			}
		}).start();
	}
	
	
	public void interactWithServer(final String inputString){
		
		new Thread(new Runnable() {
			private int doubledValue;

			public void run() {
				try{
					out = new OutputStreamWriter(connection.getOutputStream());
					Log.d("inputString", inputString);
					out.write(inputString);
					out.close();

					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String returnString="";
					doubledValue =0;

					while ((returnString = in.readLine()) != null)
					{
						Log.d("Returned string", returnString);
						doubledValue= Integer.parseInt(returnString);
					}
					in.close();
					Log.d("Doubled value", "="+doubledValue);

					((MainActivity) activity).runOnUiThread(new Runnable() {
                        public void run() {
                        	((MainActivity) activity).displayInChat("**Server says: "+ doubledValue + " **");
                       }
                   });


					Message msg = new Message();
					msg.what = MainActivityHandler.DISPLAY_TOAST;
					msg.obj = "Answer is = "+ doubledValue;
					mHandler.dispatchMessage(msg);
					

				}catch(Exception e)
				{
					Log.d("Exception",e.toString());
				}

			}
		}).start();

	}
	
	

}
