package ee.ut.cs.mc.and.pairerprototype.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BTCommunicator;

public class PostSequenceTask extends AsyncTask<JSONObject, Object, JSONObject>
{
	NetworkManager networkManager;
	Handler handler;
	private String  TAG = "PostSequenceTask";
	public PostSequenceTask(Handler handler) {
		this.networkManager = NetworkManager.getInstance();
		this.handler = handler;
	}

	@Override
	protected JSONObject doInBackground(JSONObject... params) {
		URLConnection connection = null;
		try {
			connection = networkManager.initURLConnection("json");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Log.d("attempting to post json to sever=", params[0].toString());

		try {
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			out.write(params[0].toString());
			out.close();


			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String returnString="";
			while ((returnString = in.readLine()) != null)
			{
				Log.d("Returned string", returnString);
				try {
					JSONObject json = new JSONObject(returnString);
					in.close();
					return json;
				} catch (JSONException e) {
					e.printStackTrace();
				}


			}
			in.close();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(JSONObject responseJSON) {
		Log.v(TAG, "responseJSON = " + responseJSON.toString());
		if (responseJSON != null){
			BTCommunicator communicator = new BTCommunicator(handler);
			communicator.setInsecureRfcomm(true);

			//parse the UUID and MAC to connect to.
			String connectToMAC;
			String acceptFromMAC;
			try {
				connectToMAC = responseJSON.getString("connectto");
				communicator.connectToServer(connectToMAC);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
