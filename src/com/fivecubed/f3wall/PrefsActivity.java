/**
 * F3 Wallpaper
 * @author bendikv
 */
package com.fivecubed.f3wall;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Preferences Activity
 */
public class PrefsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}

}
