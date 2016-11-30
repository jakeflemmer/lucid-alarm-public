package app.lucidalarm2;

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
import android.widget.TextView;

public class CueSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener  {

	private Preference intervalPreference;
	private ArrayList<Preference> soundPreferences;
	private ArrayList<Preference> lightPreferences;
	private Preference minVolumePreference;
	
	private GraphFragment _graphFragment;
	public void setGraphFragment( GraphFragment gf ){
		_graphFragment = gf;
	}
	public GraphFragment getGraphFragment( ){
		return _graphFragment;
	}
		//====================================================================================
		//	STATE CHANGE HANDLERS
		//====================================================================================
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	Utils.setActivityThemeFromPreferences(this);
	        super.onCreate(savedInstanceState);
	        PreferenceManager.setDefaultValues(this, R.xml.cue_preferences, false);
	        addPreferencesFromResource(R.xml.cue_preferences);
	        				Utils.boogieLog("CueSettingsActivity", "onCreate", "graw");
	    }
		@Override
		protected void onResume(){ 
			super.onResume();
			sing().cuePreferencesHaveChangedSinceCueGraphActivity= false;  // dirty flag for redrawing graph 
			if ( soundPreferences == null ) populateSoundAndLightPreferenceArrays();
			setSelectSoundScreenSummary();
			setUpSummariesAndInitialValuesForAllPreferences();
			showOrHidePreferences();
			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
					Utils.boogieLog("CueSettingsActivity", "onResume", "graw");
		}
		@Override
		protected void onPause(){
			super.onPause();
			getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		}
		
	//====================================================================================
	// HANDLERS
	//====================================================================================
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		sing().cuePreferencesHaveChangedSinceCueGraphActivity= true; 
		Preference pref = findPreference(key);
		if (pref instanceof ListPreference) {
	        ListPreference listPref = (ListPreference) pref;
	        pref.setSummary(listPref.getEntry());
	    }
		if (pref instanceof EditTextPreference) {
			EditTextPreference etPref = (EditTextPreference) pref;
	        pref.setSummary(etPref.getText());
	    }
		if( key.equals(SettingsActivity.REM_SOUND_PREF))
		{
			String soundName = sharedPreferences.getString(SettingsActivity.REM_SOUND_PREF, "tibetanbell");
			
			// set the soundPath if its a song selected from the device
			if ( Utils.songNameIsInRawFolder(this, soundName) == false && Utils.songNameIsInCreatedSoundsFolder(this, soundName) == false )
			{
				//this song is selected from device so don't touch songPath which has already been set
			}else{
				Utils.setStringPreference(this, "", SettingsActivity.REM_SOUND_PATH_PREF);
			}
		}
		showOrHidePreferences();
		//redraw the graph 
//		CueGraphPreference cgp = (CueGraphPreference) findPreference(SettingsActivity.REM_CUE_GRAPH_PREF);
//		cgp.redrawTheGraphOnSettingsChange(this);		
		//getGraphFragment().generateTheGraphDataFromSettings();
	}
	
//	public void generateNewCueNameOnCueSettingsChange() {
//		Utils.generateNewCueNameOnCueSettingsChange(this);
//	}
	
	//====================================================================================
	// worker functions
	//====================================================================================
	
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
	            		if ( listPref.getKey().equals("remSound_preference") )
	            		{
	            			setDefautRemSoundPreference();
	            			continue;
	            		}
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
	
	 //====================================================================================
		//	Refresh the Graph
		//====================================================================================
	        
//	    public void onRefreshGraphClick(View view){
//	    	CueGraphPreference cgp = (CueGraphPreference) findPreference(SettingsActivity.REM_CUE_GRAPH_PREF);
//			cgp.redrawTheGraphOnSettingsChange(this);		
//	    }
	        
	
	//====================================================================================
	// SHOW OR HIDE PREFERENCES
	//====================================================================================
	
	private void showOrHidePreferences(){
		hideTheIntervalListPreferenceIfTheShapeIsNotIntervals();
		hideTheSoundPreferencesIfSoundIsOff();
		hideTheLightPreferencesIfLightIsOff();
		hideTheMinVolumePreferenceIfShapeIsNotOscillating();
		//disableTheShapePreferenceAndSetItToIncreasingIfStartFinalAlarmAfterSnoozeRemAlarmPreferenceIsSetToTrue();
	}
	/*
	private void disableTheShapePreferenceAndSetItToIncreasingIfStartFinalAlarmAfterSnoozeRemAlarmPreferenceIsSetToTrue() {
		Boolean boo = Utils.getBooleanPreference(getApplicationContext(), SettingsActivity.REM_START_FINAL_ALARM_AFTER_SNOOZE_REM_ALARM, false );
		ListPreference shapePref = ( ListPreference ) findPreference( SettingsActivity.REM_SHAPE_PREF);
		if ( boo )
		{
			if ( shapePref != null ) {
				shapePref.setEnabled( false );
				shapePref.setValue("increasing");
			}
		}else{
			if ( shapePref != null ) {
				shapePref.setEnabled( true );
			}
		}
	}*/
	private void hideTheIntervalListPreferenceIfTheShapeIsNotIntervals(){
		if ( intervalPreference == null)
		{
			intervalPreference =  findPreference(SettingsActivity.REM_INTERVAL_PREF);
		}
		String shape = Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_SHAPE_PREF, "increasing");
		Boolean soundOn = Utils.getBooleanPreference(getApplicationContext(), SettingsActivity.REM_SOUND_ON_PREF, true);
		PreferenceCategory soundCategory = (PreferenceCategory) findPreference("sound_category");
		if ( shape.toLowerCase().trim().equals("intervals") && soundOn == true ){
			soundCategory.addPreference(intervalPreference);
		}else{
			soundCategory.removePreference(intervalPreference);
		}
	}
	private void hideTheSoundPreferencesIfSoundIsOff(){
		Boolean soundOn = Utils.getBooleanPreference(getApplicationContext(), SettingsActivity.REM_SOUND_ON_PREF, true);
		PreferenceCategory soundCategory = (PreferenceCategory) findPreference("sound_category");
		for ( int i = 0 ; i < soundPreferences.size(); i++ )
		{
			Preference p = soundPreferences.get(i);
			if ( p == null )
			{
				Utils.boogieLog("CueSettingsActivity", "hideTheSoundPreferenceIfSoundIsOff()", "ERROR should not have null preference p");
				continue;
			}
			if ( soundOn ){
				soundCategory.addPreference( p );
			}else{
				soundCategory.removePreference( p );
			}
		}
	}
	private void hideTheLightPreferencesIfLightIsOff(){
		Boolean lightOn = Utils.getBooleanPreference(getApplicationContext(), SettingsActivity.REM_LIGHT_ON_PREF, true);
		PreferenceCategory lightCategory = (PreferenceCategory) findPreference("light_category");
		for ( int i = 0 ; i < lightPreferences.size(); i++ )
		{
			Preference p = lightPreferences.get(i);
			if ( lightOn ){
				lightCategory.addPreference( p );
			}else{
				lightCategory.removePreference( p );
			}
		}
	}
	private void hideTheMinVolumePreferenceIfShapeIsNotOscillating(){
		if ( minVolumePreference == null)
		{
			minVolumePreference = (Preference) findPreference(SettingsActivity.REM_MIN_VOL_PREF );
		}
		if ( minVolumePreference == null)
		{
			Utils.boogieLog("CueSettingsActivity", "hideTheMinVolumePreferenceIfShapeIsNotOscillating()" , "ERROR : minVolumePreference should not be null");
			return;
		}
		String shape = Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_SHAPE_PREF, "increasing");
		Boolean soundOn = Utils.getBooleanPreference(getApplicationContext(), SettingsActivity.REM_SOUND_ON_PREF, true);
		PreferenceCategory soundCategory = (PreferenceCategory) findPreference("sound_category");
		if ( shape.toLowerCase().trim().equals(Singleton.SHAPE_OSCILLATING) && soundOn == true ){
			soundCategory.addPreference(minVolumePreference);
		}else{
			soundCategory.removePreference(minVolumePreference);
		}
	}
	//====================================================================================
	// ---	---
	//====================================================================================
	private void populateSoundAndLightPreferenceArrays() {
		
		soundPreferences = new ArrayList<Preference>();
		soundPreferences.add( findPreference("selectRemSound_screen") ); 
		soundPreferences.add( findPreference(SettingsActivity.REM_SOUNDS_DELAY_PREF) );
		soundPreferences.add( findPreference(SettingsActivity.REM_SERIES_DELAY_PREF) );
		soundPreferences.add( findPreference(SettingsActivity.REM_SHAPE_PREF) );
		soundPreferences.add( findPreference(SettingsActivity.REM_MIN_TO_MAX_VOL_PREF) );
		soundPreferences.add( findPreference(SettingsActivity.REM_MIN_VOL_PREF) );
		soundPreferences.add( findPreference(SettingsActivity.REM_MAX_VOL_PREF) );
		
		lightPreferences = new ArrayList<Preference>();
		lightPreferences.add( findPreference(SettingsActivity.REM_MAX_BRIGHTNESS_PREF) );
		lightPreferences.add( findPreference(SettingsActivity.REM_FREQUENCY_PREF) );
		lightPreferences.add( findPreference(SettingsActivity.REM_COLOR_PREF) );
	}
	private void setDefautRemSoundPreference(){
		Utils.setStringPreference(this, SettingsActivity.REM_SOUND_PREF, Singleton.DEFAULT_REM_ALARM_SOUND);
		Utils.setStringPreference(this, "", SettingsActivity.REM_SOUND_PATH_PREF);
		// and set the summary of the list preference !
		ListPreference lp = (ListPreference) findPreference(SettingsActivity.REM_SOUND_PREF);
		lp.setSummary("tibetan bell");
	}
	private void setSelectSoundScreenSummary(){
		Preference p =  findPreference( "selectRemSound_screen" );
		if ( p != null ) p.setSummary( Utils.getStringPreference( this, SettingsActivity.REM_SOUND_PREF, "classicalarm" ));
	}
	
	 //====================================================================================
	//	Getters && Setters
	//====================================================================================
		private Singleton sing(){
			return Singleton.getInstance();
		}
}
