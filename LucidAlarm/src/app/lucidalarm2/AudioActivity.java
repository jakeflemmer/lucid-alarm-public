package app.lucidalarm2;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.app.Activity;
import android.content.Intent;
import android.widget.*;
import android.view.*;

public class AudioActivity extends Activity  {

	public static final String NEW_REM_SOUND = "newRemSound";
	public static final String DEFAULT_NEW_FILENAME = "new rem sound";
	public static final String SOUNDS_DIRECTORY_NAME = "/LucidAlarmSounds/";
	public static final String RECORDINGS_DIRECTORY_NAME = "/DreamRecordings/";
	
	public static final String DREAM_RECORD_DATE_STRING = "dreamRecordDateString";
	public static final String DREAM_RECORD_NOTES_STRING = "dreamRecordNotesString";
	
	private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

	//====================================================================================
	// OKAY FOR V2 WE WANT A COOL TRICKED OUT AUDIO
	//====================================================================================
	/*
	 *   The microphone icon
	 *   
	 *   The widget should take up the correct space in the layout
	 *   There must be that cool thing that shows the sound level of the recording
	 *   	that is in a bar and has cool colors like the HI-Q recorder app
	 *   While it plays the progress bar that shows the length of the audio and where the play head is at on the line
	 */
	
	//====================================================================================
	//	STATE CHANGE HANDLERS
	//====================================================================================
	
	@Override
    public void onCreate(Bundle icicle) {
		Utils.setActivityThemeFromPreferences(this);
        super.onCreate(icicle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);	
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);	// prevents keyboard from showing automatically
        setContentView(R.layout.audio_layout);
        
        final EditText saveAsET = (EditText) findViewById(R.id.saveAs_editText);
        
        saveAsET.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {				
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {				 
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {				
				String newSaveAsName = saveAsET.getText().toString(); 
				AudioFragment audioFragment = ( AudioFragment ) getFragmentManager().findFragmentById(R.id.audio_fragment);
				audioFragment.setNewRemSoundSaveAsName(newSaveAsName);
			}
		});
        
        setThemePropertiesOfViews( (TextView) saveAsET );
        
	}	
	
	@Override
    public void onResume() {
		super.onResume();		
		
       
	}
	//================================================================
	// SAVE ON PAUSE
	//================================================================
	@Override
    public void onPause() {
        super.onPause();
        
        
        
//		Intent intent = getIntent();
//		String intentMessage = intent.getStringExtra(MainActivity.INTENT_STRING_MESSAGE);
//		if (intentMessage != null )
//		{
//			if ( intentMessage.equals(AudioActivity.NEW_REM_SOUND))
//			{
//				
//						// rename file to above
//						String basePath = getBasePathToCustomAudioRecordings();
//						File oldFile = new File( basePath + SOUNDS_DIRECTORY_NAME,DEFAULT_NEW_FILENAME + ".3gp");
//						File newFile = new File( basePath + SOUNDS_DIRECTORY_NAME, recordingName.trim() + ".3gp");
//						oldFile.renameTo(newFile);
//				
//				Utils.setStringPreference(getApplicationContext(), recordingName, SettingsActivity.SELECTED_ALARM_SOUND_PREF );
//			}
//		}
		
    }

	//====================================================================================
		//	THEMES 
		//====================================================================================
	
	private void setThemePropertiesOfViews( TextView view  ){
		Utils.setTextViewColorByTheme(view, this);
    }
	
	//====================================================================================
	//	SETUP 
	//====================================================================================
	
	
	private void hideView( View view){
		view.setVisibility(View.GONE);
	}
	private void showView( View view) {
		view.setVisibility(View.VISIBLE);
	}
	
	 
	
	


    
	
    
    
    
    
    //====================================================================================
  	//	DISABLE AND ENABLE BUTTONS
  	//====================================================================================
	
    private void enableButton ( ImageButton button ) {
    		button.setEnabled( true );
    }
    private void disableButton ( ImageButton button ) {
    		button.setEnabled( false );
    }
    
    
    //====================================================================================
	//	Getters & setters
	//====================================================================================
	
	public String getDateName(){
		//return Long.toString(Singleton.getInstance().dreamRecordSelected.unixTime);
		String dateString = getIntent().getStringExtra( AudioActivity.DREAM_RECORD_DATE_STRING );
		Utils.boogieLog("AudioActivity", "getDateName()", "dateString : " + dateString );
		return dateString;
	}
	private Singleton sing(){
		return Singleton.getInstance();
	}
	
	public static String getBasePathToCustomAudioRecordings(){
		String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		return basePath;
	}

}
