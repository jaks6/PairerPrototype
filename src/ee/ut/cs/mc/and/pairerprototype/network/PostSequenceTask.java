package ee.ut.cs.mc.and.pairerprototype.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.util.Log;

public class PostSequenceTask extends AsyncTask<String, Object, Object>
{
	NetworkManager networkManager;
	private String  TAG ="PostSequenceTask";
	public PostSequenceTask(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	@Override
	protected Object doInBackground(String... params) {
		URLConnection connection = null;
		try {
			connection = NetworkManager.initURLConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String inputString = (String) params[0]; 
		Log.d("attempting to post string to sever=", inputString);
		
		try {
			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			out.write(inputString);
			out.close();
			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String returnString="";
			while ((returnString = in.readLine()) != null)
			{
				Log.d("Returned string", returnString);
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

}
