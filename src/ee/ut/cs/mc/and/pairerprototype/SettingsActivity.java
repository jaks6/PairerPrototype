package ee.ut.cs.mc.and.pairerprototype;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new SettingsFragment())
		.commit();
	}


	/**
	 * This fragment shows the preferences for the first header.
	 */
	public static class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Make sure default values are applied.  In a real app, you would
			// want this in a shared function that is used to retrieve the
			// SharedPreferences wherever they are needed.
			PreferenceManager.setDefaultValues(getActivity(),
					R.xml.preferences, false);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);

			getPreferenceScreen().getSharedPreferences().
			registerOnSharedPreferenceChangeListener(this);

		}
		
		@Override
		public void onResume() {
			super.onResume();
			updateAllPreferenceGroups();
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			updatePreference(findPreference(key));
		}
		
		/** This method replaces preference summary field with the actual preference value*/
		private void updatePreference(Preference preference) {
			if (preference instanceof EditTextPreference) {
				EditTextPreference editTextPreference = (EditTextPreference) preference;
				editTextPreference.setSummary(editTextPreference.getText());
			}
		}
		
		private void updateAllPreferenceGroups() {
			for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
				Preference preference = getPreferenceScreen().getPreference(i);
				if (preference instanceof PreferenceGroup) {
					PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
					for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
						updatePreference(preferenceGroup.getPreference(j));
					}
				} else {
					updatePreference(preference);
				}
			}
		}
	}

}

