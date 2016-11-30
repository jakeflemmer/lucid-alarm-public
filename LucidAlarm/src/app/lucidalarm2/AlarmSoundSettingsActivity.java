package app.lucidalarm2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class AlarmSoundSettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	public static final int ACTIVITY_RESULT_SELECT_SONG = 5;
	
	//====================================================================================
	//	STATE CHANGE HANDLERS
	//====================================================================================
			
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	Utils.setActivityThemeFromPreferences(this);
	        super.onCreate(savedInstanceState);
	        PreferenceManager.setDefaultValues(this, R.xml.alarm_sound_preferences, false);
	        addPreferencesFromResource(R.xml.alarm_sound_preferences);
	        
	        setSummaryOfCurrentlySelectedSoundPreference();
	        
	        				Utils.boogieLog("AlarmSoundSettingsActivity", "onCreate", "graw");
	    }
	    
		@Override
		protected void onResume(){ 
			super.onResume();
			
			setListenerForSelectFromDevicePreference();
			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
			
			getListView();
			
					Utils.boogieLog("AlarmSoundSettingsActivity", "onResume", "graw");
		}
		
		@Override
	    public void onPause() {
	        super.onPause();
	        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		}
		
		//====================================================================================
		// ON SHARED PREFERENCES CHANGED
		//====================================================================================
			
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			
			String soundToSet = "";
			soundToSet = Utils.getStringPreference(this , key , "");
			setSummaryOfCurrentlySelectedSoundPreference( soundToSet );
			writeSelectedSoundToRemOrFinalSongPreference( soundToSet );
		}
		
		private void writeSelectedSoundToRemOrFinalSongPreference( String soundToSet ){
			if ( getIntent().getStringExtra( "callingContext") != null && getIntent().getStringExtra("callingContext").equals("final") )
			{
				Utils.setStringPreference( this, soundToSet, SettingsActivity.FINAL_ALARM_SONG_PREF );
			}
			else if ( getIntent().getStringExtra( "callingContext") != null && getIntent().getStringExtra("callingContext").equals("rem") )
			{
				Utils.setStringPreference( this, soundToSet, SettingsActivity.REM_SOUND_PREF );
			}
		}
		
		
		
		//====================================================================================
		//	SELECT SONG FROM DEVICE
		//====================================================================================
		
		private void setListenerForSelectFromDevicePreference(){
			Preference selectFromDevicePref = super.findPreference( "selectFromDevice_preference" );
			selectFromDevicePref.setOnPreferenceClickListener( onSelectFromDevicePreferenceClick );
		}
		
		OnPreferenceClickListener onSelectFromDevicePreferenceClick = new Preference.OnPreferenceClickListener() {
		       public boolean onPreferenceClick(Preference preference) {
		           selectSongFromDevice();
		           return true;
		       }
		   };
		   
		   private void selectSongFromDevice() {
		  		
		  		String path = "";
		  		path = Environment.getExternalStorageDirectory().getAbsolutePath();
		  		Intent intent= new Intent(path);
		  		intent.setType("audio/mp3");
		  		intent.setAction(Intent.ACTION_GET_CONTENT);
		  		intent.addCategory(Intent.CATEGORY_OPENABLE);

		  		this.startActivityForResult(Intent.createChooser(intent, "select music"), ACTIVITY_RESULT_SELECT_SONG );
		  	}
		  	
		  	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		          if (resultCode == this.RESULT_OK ) {   
		              switch(requestCode)   
		              {
		  				case ACTIVITY_RESULT_SELECT_SONG:  
		  					Uri selectedAudioUri = data.getData();

		  					String selectedAudioPath = Utils.getPathAudio(selectedAudioUri, this );
		  					String selectedAudioTitle = Utils.getAudioTitle(selectedAudioUri, this );
		  					
		  					Utils.boogieLog("AlarmSoundSettingsActivity", "onActivityResult", "selected audio path : " + selectedAudioPath + "selected audio title : " + selectedAudioTitle);
		  					
		  					if ( selectedAudioPath != null && selectedAudioPath.length() > 0 && selectedAudioTitle != null && selectedAudioTitle.length() > 0)
		  					{
		  						if ( getIntent().getStringExtra( "callingContext") != null && getIntent().getStringExtra("callingContext").equals("final") )
		  						{
		  							Utils.setStringPreference( this, selectedAudioTitle, SettingsActivity.FINAL_ALARM_SONG_PREF );
		  							Utils.setStringPreference( this, selectedAudioPath, SettingsActivity.FINAL_ALARM_SONG_PATH_PREF );
		  						}
		  						else if ( getIntent().getStringExtra( "callingContext") != null && getIntent().getStringExtra("callingContext").equals("rem") )
		  						{
		  							Utils.setStringPreference( this, selectedAudioTitle, SettingsActivity.REM_SOUND_PREF);
		  							Utils.setStringPreference( this, selectedAudioPath, SettingsActivity.REM_SOUND_PATH_PREF);
		  						}
		  						Utils.setStringPreference( this, selectedAudioTitle, SettingsActivity.SELECTED_ALARM_SOUND_PREF );
		  						Preference p = (Preference) super.findPreference( SettingsActivity.SELECTED_ALARM_SOUND_PREF );
		  						p.setSummary( selectedAudioTitle );
		  					}
		  					break;
		  			}
		  		}
		  	}
		
	//====================================================================================
	// UPDATE HEADER SUMMARY
	//====================================================================================
		
	private void setSummaryOfCurrentlySelectedSoundPreference(){
		String callingContext = getIntent().getStringExtra("callingContext");
		String soundToSet = "";
		if ( callingContext.equals("final") ){
			// we no longer have finalAlarmSong_preference defined is the preference xml anymore. we believe it is persisted anyways like a preference
			// but we must make sure we set the default in case of fresh install
			soundToSet = Utils.getStringPreference(this , SettingsActivity.FINAL_ALARM_SONG_PREF , Singleton.DEFAULT_FINAL_ALARM_SOUND );
		}
		else if ( callingContext.equals("rem") )
		{
			// we no longer have remAlarmSong_preference defined is the preference xml anymore. we believe it is persisted anyways like a preference
			// but we must make sure we set the default in case of fresh install
			soundToSet = Utils.getStringPreference(this , SettingsActivity.REM_SOUND_PREF , Singleton.DEFAULT_REM_ALARM_SOUND );
		}else{
			throw new Error();
		}
		Utils.setStringPreference( this, soundToSet, SettingsActivity.SELECTED_ALARM_SOUND_PREF );
		// set summary for this preference
		Preference etp = (Preference) super.findPreference( SettingsActivity.SELECTED_ALARM_SOUND_PREF );
		etp.setSummary( soundToSet );
					Utils.boogieLog("AlarmSoundSettingsActivity", "setSummaryOfCurrentlySelectedSoundPreference()", "soundToSet = " + soundToSet);
	}
	
	private void setSummaryOfCurrentlySelectedSoundPreference( String soundToSet ){
		// set summary for this preference
		Preference etp = (Preference) super.findPreference( SettingsActivity.SELECTED_ALARM_SOUND_PREF );
		etp.setSummary( soundToSet );
					Utils.boogieLog("AlarmSoundSettingsActivity", "setSummaryOfCurrentlySelectedSoundPreference(soundToSet)", "soundToSet = " + soundToSet);
	}
	
		
	 //====================================================================================
	//	Getters && Setters
	//====================================================================================
		
		private Singleton sing(){
			return Singleton.getInstance();
		}
    

}
