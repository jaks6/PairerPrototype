package ee.ut.cs.mc.and.pairerprototype;

import org.json.JSONArray;
import org.json.JSONObject;

import ee.ut.cs.mc.and.pairerprototype.amplitudelogger.AmplitudeUtils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class App extends Application {
	
	static SharedPreferences prefs;
	static Context mContext;
	static MainActivityHandler mainActivityHandler;
	
	public static final boolean fakeSequence = true;
	static String lastGroupId;
	private String TAG = App.class.getName();
	JSONObject lastInstructions;

	@Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getContext();
		prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		String prefsResult =(prefs == null)? "Null": "A OKAY";
		Log.d(TAG , prefsResult);
	}

	public static SharedPreferences getPrefs() {
		return prefs;
	}
	
	public Context getContext(){
		return mContext;
	}
	
	public static String getUserNick(){
		return prefs.getString("pref_nickname", "N//A");
	}

	public static MainActivityHandler getMainActivityHandler() {
		return mainActivityHandler;
	}

	public static void setMainActivityHandler(MainActivityHandler handler) {
		mainActivityHandler = handler;
	}

	public static void setGroupId(String groupId) {
		lastGroupId = groupId;
		
	}

	public static String getLastGroupId() {
		return lastGroupId;
	}
	
	public static JSONArray getFakeSequence(){
		JSONArray sequenceJson = new JSONArray();
		for (int i=0; i<AmplitudeUtils.NO_OF_SAMPLES_IN_SEQUENCE; i++){
			//gather a sample
			int sample = 5;
			sequenceJson.put(sample);
		}
		return sequenceJson;
	}

}
