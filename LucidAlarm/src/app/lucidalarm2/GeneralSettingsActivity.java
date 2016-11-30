package app.lucidalarm2;

import android.app.Activity;
import java.util.ArrayList;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class GeneralSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {


	private Preference intervalPreference;
	private ArrayList<Preference> soundPreferences;
	private ArrayList<Preference> lightPreferences;
	private Preference minVolumePreference;

	public GeneralSettingsActivity() {
		// TODO Auto-generated constructor stub
	}



			//====================================================================================
			//	STATE CHANGE HANDLERS
			//====================================================================================
		    @Override
		    public void onCreate(Bundle savedInstanceState) {
		    	
		    	Utils.setActivityThemeFromPreferences(this);
				
		        super.onCreate(savedInstanceState);
		        PreferenceManager.setDefaultValues(this, R.xml.cue_preferences, false);
		        addPreferencesFromResource(R.xml.general_preferences);
		        				Utils.boogieLog("CueSettingsActivity", "onCreate", "graw");
		    }
			@Override
			protected void onResume(){ 
				super.onResume();
				
				getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
				setUpSummariesAndInitialValuesForAllPreferences();
						Utils.boogieLog("GeneralSettingsActivity", "onResume", "graw");
			}
			@Override
			protected void onPause(){
				super.onPause();	
				getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
				
			}
			
			//================================================================================
			//  Init
			//================================================================================
			
			private void setUpSummariesAndInitialValuesForAllPreferences() {
				Map<String, ?> sharedPreferencesMap = getPreferenceScreen().getSharedPreferences().getAll();
			    Preference pref;
			    ListPreference listPref;
			    EditTextPreference etPref;
			    CheckBoxPreference cbPref;
			    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				
			    for (Map.Entry<String, ?> entry : sharedPreferencesMap.entrySet()) {
			        pref = findPreference(entry.getKey());
			        if (pref instanceof ListPreference) {
			            listPref = (ListPreference) pref;
			            CharSequence entryVal = listPref.getEntry();
			           
			            if ( entryVal == null ) {
			            		
			            		listPref.setValueIndex(0);
			            		entryVal = listPref.getEntry();
			            }
			            pref.setSummary(entryVal);	            
			        }
			        if (pref instanceof EditTextPreference) {
			        		etPref = (EditTextPreference) pref;
			            pref.setSummary(etPref.getText());
			        }
			    }
			}
			
			//================================================================================
			//  Preference change handler
			//================================================================================
			
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
				
				/*Preference pref = findPreference(key);
				if (pref instanceof ListPreference) {
			        ListPreference listPref = (ListPreference) pref;
			        pref.setSummary(listPref.getEntry());
			    }
				if (pref instanceof EditTextPreference) {
					EditTextPreference etPref = (EditTextPreference) pref;
			        pref.setSummary(etPref.getText());
			    }*/
				
				recreate();
				sing().changeTheThemeOfAllActivities();
				
			
			}
			
			
		
		 //====================================================================================
		//	Getters && Setters
		//====================================================================================
			private Singleton sing(){
				return Singleton.getInstance();
			}
	}


