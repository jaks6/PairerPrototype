package ee.ut.cs.mc.and.pairerprototype.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import ee.ut.cs.mc.and.pairerprototype.App;

public class NetworkManager {
	Handler mHandler;
	Context context;
//	static Activity activity;
	private static NetworkManager instance = null;

	URL url;
	URLConnection connection;
	OutputStreamWriter out;

//	public NetworkManager(Activity activity){
//		NetworkManager.activity = activity;
//	}
	
    private NetworkManager() { }
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }
	/** Creates a new URLConnection object , with DoOutput=true, and opens it.
	 * @return the URLConnection object
	 * @param String serverip - the URL to connect to
	 * @throws IOException
	 */
	private static URLConnection initURLConnection(String serverIP, String type) throws IOException {
		URL url = new URL(serverIP);
		Log.d("URL", url.toString());
		URLConnection connection = url.openConnection();
		connection.addRequestProperty("reqtype", type);
		connection.setDoOutput(true);
		return connection;
	}
	
	/** Creates a new URLConnection object, with DoOutput=true, and opens it.
	 * This method uses the server URL from app-s SharedPreferences. To specify
	 * a custom URL, use {@link #initURLConnection(String)}
	 * @return the URLConnection object
	 * @throws IOException
	 */
	public URLConnection initURLConnection(String type) throws IOException{
		//get IP from preferences, default to 0.0.0.0 if unsuccessful
		
		final String serverIP = "http://" + App.getPrefs().getString("pref_serverip", "0.0.0.0");
		return initURLConnection(serverIP + "/PairerPrototypeServer/sequence", type);
	}


//	public void interactWithServer(final String inputString){
//		new Thread(new Runnable() {
//			private String returnString = "";
//			public void run() {
//				try{
//					URLConnection connection = null;
//					connection = initURLConnection("");
//					out = new OutputStreamWriter(connection.getOutputStream());
//					Log.d("inputString", inputString);
//					out.write(inputString);
//					out.close();
//
//					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//					while ((returnString = in.readLine()) != null)
//					{
//						Log.d("Returned string", returnString);
//					}
//					in.close();
//
//					((MainActivity) activity).runOnUiThread(new Runnable() {
//						public void run() {
//							((MainActivity) activity).displayInChat("**Server says: "+ returnString + " **");
//						}
//					});
//
//				}catch(Exception e)
//				{
//					Log.d("Exception",e.toString());
//				}
//
//			}
//		}).start();
//
//	}

}
