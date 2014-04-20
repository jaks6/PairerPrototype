package ee.ut.cs.mc.and.pairerprototype;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class App extends Application {
	
	static SharedPreferences prefs;
	private String TAG = App.class.getName();

	@Override
    public void onCreate() {
        super.onCreate();
		prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		String prefsResult =(prefs == null)? "Null": "A OKAY";
		Log.d(TAG , prefsResult);
	}

	public static SharedPreferences getPrefs() {
		return prefs;
	}

}
