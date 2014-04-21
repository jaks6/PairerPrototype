package ee.ut.cs.mc.and.pairerprototype;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class App extends Application {
	
	static SharedPreferences prefs;
	static Context mContext;
	static MainActivityHandler mainActivityHandler;
	private String TAG = App.class.getName();

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

	public static MainActivityHandler getMainActivityHandler() {
		return mainActivityHandler;
	}

	public static void setMainActivityHandler(MainActivityHandler handler) {
		mainActivityHandler = handler;
	}

}
