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
import android.os.Message;
import android.util.Log;
import ee.ut.cs.mc.and.pairerprototype.MainActivityHandler;
import ee.ut.cs.mc.and.pairerprototype.bluetooth.BluetoothHelper;

public class PostSequenceTask extends AsyncTask<JSONObject, Object, JSONObject>
{
	NetworkManager networkManager;
	Handler handler;
	static final private String  TAG = "PostSequenceTask";
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
					return new JSONObject();
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
			followInstructions(responseJSON);
		}
	}

	private void followInstructions(JSONObject responseJSON) {
		String connectToMac = null;
		boolean listenForConnections = false;
		JSONArray group = null;
		try {
			if (responseJSON.has("connectto")) connectToMac = responseJSON.getString("connectto");
			if (responseJSON.has("group")) group = responseJSON.getJSONArray("group");
			if (responseJSON.has("listento")) listenForConnections = responseJSON.getBoolean("listento");
		} catch (JSONException e1) { e1.printStackTrace();}
		BluetoothHelper communicator = BluetoothHelper.getInstance();
		communicator.setHandler(handler);
		communicator.setInsecureRfcomm(true);
		
		if (connectToMac!=null && !connectToMac.equals(communicator.currentServer)){
			communicator.connectToServer(connectToMac);
		}
		if (listenForConnections && communicator.listening != true) communicator.startListening();
		
		if (group != null) showGroupOnUI(group);
		
	}

	private void showGroupOnUI(JSONArray group) {
		if (group != null){
		
		Message msg = new Message();
		msg.what = MainActivityHandler.UPDATE_GROUP;
		msg.obj = group;
		handler.dispatchMessage(msg);
		}
		
	}

}
