package app.lucidalarm2;

import android.os.Bundle;
import android.preference.*;
import android.content.*;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import android.os.*;
import android.net.*;
import android.provider.*;
import android.database.*;

public class SettingsActivity extends PreferenceActivity	
		implements OnSharedPreferenceChangeListener {

	public static final String SELECTED_ALARM_SOUND_PREF = "selectedAlarmSound_preference";
	
	public static final String RECORD_NEW_REM_SOUND_OPTION = "RECORD NEW SOUND";
	public static final String SELECT_SONG_FROM_DEVICE_OPTION = "SELECT FROM DEVICE LIBRARY";
	public static final String NATURE_SOUNDS_OPTION = "NATURE SOUNDS";
	public static final String ISOCHRONIC_TONES_OPTION = "ISOCHRONIC TONES";
	public static final String BINAURAL_BEATS_OPTION = "BINAURAL BEATS";
	public static final String PRE_RECORDED_OPTION = "PRE-RECORDED SOUNDS";
	
	public static final String FINAL_MAX_VOL_PREF = "finalMaxVolume_preference";
	public static final String SNOOZE_MINS_PREF = "snoozeMinutes_preference";
	public static final String MINS_TO_FINAL_MAX_VOL_PREF = "minutesToFinalMaxVolume_preference";
	public static final String FINAL_ALARM_SONG_PREF = "finalAlarmSong_preference";
	public static final String FINAL_ALARM_SONG_PATH_PREF = "finalAlarmSongPath_preference";
	
	public static final String HIDDEN_ID_FIELD = "underscoreId";
	public static final String REM_SOUND_ON_PREF = "remSoundOn_preference";
	public static final String REM_SOUND_PREF = "remSound_preference";
	public static final String REM_SOUND_PATH_PREF = "remSoundPath_preference";
	public static final String REM_SOUNDS_DELAY_PREF = "remSoundsDelay_preference";
	public static final String REM_SERIES_DELAY_PREF = "remSeriesDelay_preference";
	public static final String REM_SHAPE_PREF = "remShape_preference";
	public static final String REM_INTERVAL_PREF = "remInterval_preference";
	public static final String REM_MIN_TO_MAX_VOL_PREF = "minutesToRemMaxVolume_preference";
	public static final String REM_MIN_VOL_PREF = "remMinVolume_preference";
	public static final String REM_MAX_VOL_PREF = "remMaxVolume_preference";
	public static final String REM_LIGHT_ON_PREF = "remLightOn_preference";
	public static final String REM_MAX_BRIGHTNESS_PREF = "remMaxBrightness_preference";
	public static final String REM_FREQUENCY_PREF = "remFrequency_preference";
	public static final String REM_COLOR_PREF = "remLightColor_preference";
	public static final String REM_CUE_NAME_PREF = "cueName_preference";	
	public static final String REM_START_FINAL_ALARM_AFTER_SNOOZE_REM_ALARM = "remStartFinalAlarmAfterSnoozeRemAlarm_preference";
	
	public static final String REM_CUE_GRAPH_PREF = "remCueGraph_preference";

	public static final String GENERAL_THEME_PREF = "generalTheme_preference";
	
	public static final String THEME_SPACE = "spaceTheme";
	public static final String THEME_ZEN = "zenTheme";
	public static final String THEME_CLOUDS = "cloudsTheme";
	
	public static final String[] remSettingPreferencesArray = { HIDDEN_ID_FIELD, REM_SOUND_ON_PREF , REM_SOUND_PREF , REM_SOUND_PATH_PREF ,
		REM_SOUNDS_DELAY_PREF , REM_SERIES_DELAY_PREF , REM_SHAPE_PREF , REM_INTERVAL_PREF , REM_MIN_TO_MAX_VOL_PREF , 
		REM_MIN_VOL_PREF , REM_MAX_VOL_PREF , REM_LIGHT_ON_PREF , REM_MAX_BRIGHTNESS_PREF , REM_FREQUENCY_PREF , 
		REM_COLOR_PREF , REM_CUE_NAME_PREF};
	
	//====================================================================================
	//	STATE CHANGE HANDLERS
	//====================================================================================
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Utils.setActivityThemeFromPreferences(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		// TODO we need to set list preferences for songs/sounds for rem and final alarms 
        // updatePreferenceTitles(false);
    }
	
	@Override
	protected void onResume(){
		super.onResume();
		
		if ( sing().changedThemeSettingsActivity == true )
		{
			sing().changedThemeSettingsActivity = false;
			recreate();
			return;
		}
		
		setSelectSoundScreenSummary();
		
		setUpSummariesAndInitialValuesForAllPreferences();
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	
	private void setSelectSoundScreenSummary(){
		PreferenceScreen ps = ( PreferenceScreen ) findPreference( "selectFinalSound_screen" );
		ps.setSummary( Utils.getStringPreference( this, SettingsActivity.FINAL_ALARM_SONG_PREF, "classicalarm" ));
	}
	
	private void setUpSummariesAndInitialValuesForAllPreferences(){
		// Set up initial values for all preferences
	    Map<String, ?> sharedPreferencesMap = getPreferenceScreen().getSharedPreferences().getAll();
	    Preference pref;
	    ListPreference listPref;
	    EditTextPreference etPref;
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
	
	private void populateFinalSongListPreferenceOptions(){
		ListPreference lp = (ListPreference) super.findPreference("finalAlarmSong_preference");
		
		ArrayList<String> optionsEntriesArrayList = new ArrayList<String>();
		ArrayList<String> optionsEntryValuesArrayList = new ArrayList<String>();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String songTitle = prefs.getString(SettingsActivity.FINAL_ALARM_SONG_PREF, "");
		String songPath = prefs.getString("hiddenFinalSongPath_preference", "");
		
		// add the sounds from the raw folder and see if the selected song is there
		Boolean selectedSongIsInRaw = false;
		Field[] fields = R.raw.class.getFields();
		for ( Field field : fields )
		{
			optionsEntriesArrayList.add(field.getName());
			optionsEntryValuesArrayList.add(field.getName());
			if ( songTitle != null && songTitle.length() > 0 && songPath != null && songPath.length() > 0 )
			{
				if ( field.getName() != null && field.getName().equals(songTitle))
				{
					selectedSongIsInRaw = true;
				}
			}
		}
		// add selected song if not in raw
		if ( selectedSongIsInRaw == false && songTitle != null && songTitle.length() > 0 && songPath != null && songPath.length() > 0 )
		{
			optionsEntriesArrayList.add(songTitle);
			optionsEntryValuesArrayList.add(songPath);
		}
		
		// create and add the select new option
		String selectNew = SELECT_SONG_FROM_DEVICE_OPTION;
		optionsEntriesArrayList.add(selectNew);
		optionsEntryValuesArrayList.add(selectNew);
		
		// transfer from arrayList to array
		String[] optionsEntriesArray = new String[optionsEntriesArrayList.size()];
		for ( int i=0; i < optionsEntriesArrayList.size(); i++)
		{
			optionsEntriesArray[i] = optionsEntriesArrayList.get(i);
		}
		String[] optionsEntryValuesArray = new String[optionsEntryValuesArrayList.size()];
		for ( int i=0; i < optionsEntryValuesArrayList.size(); i++)
		{
			optionsEntryValuesArray[i] = optionsEntryValuesArrayList.get(i);
		}
		// set them on the listpreference
		lp.setEntries(optionsEntriesArray);
		lp.setEntryValues(optionsEntryValuesArray);
	}

	
	
	//================================================================================
	//  Preference change handler
	//================================================================================
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
		
		Preference pref = findPreference(key);
		if (pref instanceof ListPreference) {
	        ListPreference listPref = (ListPreference) pref;
	        pref.setSummary(listPref.getEntry());
	    }
		if (pref instanceof EditTextPreference) {
			EditTextPreference etPref = (EditTextPreference) pref;
	        pref.setSummary(etPref.getText());
	    }
		
		/*
		Boolean remVolChanged;
		if ( key.equals("remMaxVolume_preference"))
		{
			remVolChanged = true;
		}else{
			remVolChanged = false;	
		}
		if( key.equals("remAlarmSounds_preference"))
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String REMAlarmSound = prefs.getString("remAlarmSounds_preference","tibetanbell");
			if ( REMAlarmSound.equals(CREATE_NEW_REM_SOUND_OPTION))
			{
				// first set the sound back to default ( mind )
				setDefautRemSoundPreference();
				Intent intent = new Intent(this, AudioActivity.class);
				intent.putExtra(MainActivity.INTENT_STRING_MESSAGE,AudioActivity.NEW_REM_SOUND);
				startActivity(intent);
			}
		}
		if ( key.equals("finalAlarmSong_preference"))
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String finalAlarmSound = prefs.getString("finalAlarmSong_preference","classicalarm");
			if ( finalAlarmSound.equals(SELECT_NEW_SONG_OPTION))
			{
				setDefaultFinalSongPreference();
				selectFinalAlarmSong();
				// TODO updatePreferenceTitles(remVolChanged);
				return;
			}
			ListPreference lp = (ListPreference) super.findPreference("finalAlarmSong_preference");
			String songTitle = lp.getEntry().toString();
			String songPath = lp.getValue();
			setSelectedSongIntoPreferences(songTitle,songPath);
			
		}	
		// TODO updatePreferenceTitles(remVolChanged);
		 
		 */
	}
	
//	private void setDefautRemSoundPreference(){
//		SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(this);
//		SharedPreferences.Editor editor = settings.edit();
//		editor.putString("remAlarmSounds_preference", Singleton.DEFAULT_REM_ALARM_SOUND );
//		editor.commit();
//	}
//	
//	private void setDefaultFinalSongPreference(){
//		ListPreference lp = (ListPreference) super.findPreference("finalAlarmSong_preference");
//		lp.setValue("classicalarm");
//		
//	}
	
	private String trimmed(String fileName){
		int index = fileName.indexOf(".");
		if ( index > 0 )
		{
			String trimmedFileName = fileName.substring(0,index);				
			return trimmedFileName;
		}else{
			return fileName;
		}
	}
	
	//================================================================================
	//  Select final alarm song
	//================================================================================
	
	private void selectFinalAlarmSong() {
		
		String path = "";
		path = Environment.getExternalStorageDirectory().getAbsolutePath();
		Intent intent1= new Intent(path);
		intent1.setType("audio/mp3");
		intent1.setAction(Intent.ACTION_GET_CONTENT);
		intent1.addCategory(Intent.CATEGORY_OPENABLE);

		startActivityForResult(Intent.createChooser(intent1, "select music"), 1);
		
		//onResume();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {  
            switch(requestCode)   
            {
				case 1:  
					Uri selectedAudioUri = data.getData();

					String selectedAudioPath = Utils.getPathAudio(selectedAudioUri, this);
					String selectedAudioTitle = Utils.getAudioTitle(selectedAudioUri, this);
					
					sing().logg("graw", "selected audio path : " + selectedAudioPath);
					sing().logg("graw", "selected audio title : " + selectedAudioTitle);
					
					if ( selectedAudioPath != null && selectedAudioPath.length() > 0 
						&& selectedAudioTitle != null && selectedAudioTitle.length() > 0)
						{
							setSelectedSongIntoPreferences(selectedAudioTitle, selectedAudioPath);
							// TODO updatePreferenceTitles(false);
						}
					break;

			}
		}
	}
	private void setSelectedSongIntoPreferences(String songTitle, String songPath){
		// select song here
		SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(SettingsActivity.FINAL_ALARM_SONG_PREF, songTitle);
		editor.putString("hiddenFinalSongPath_preference", songPath);
		editor.commit(); 
	}
	
	
	
	
	//================================================================================
	//  Getters and Setters
	//================================================================================
	
	private Singleton sing(){
		return Singleton.getInstance();
	}
}	
