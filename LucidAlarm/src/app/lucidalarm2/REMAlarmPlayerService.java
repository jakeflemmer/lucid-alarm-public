package app.lucidalarm2;

import android.content.*;
import android.media.*;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.*;
import android.util.*;
import android.widget.*;
import android.widget.MediaController.MediaPlayerControl;

import java.io.*;
import java.util.ArrayList;

import app.lucidalarm2.ContractSchema.GraphsTable;

import android.preference.*;

public class REMAlarmPlayerService extends AlarmPlayerService implements OnCompletionListener{
	
	public static String REM_INTENT_MESSAGE = "remIntentMessage";
	public static String REM_SERVICE_PLAY = "remServicePlay";
	public static String REM_NOT_STARTED_YET = "remNotStartedYet";
	public static String REM_TESTING_MAX_VOLUME = "remTestingMaxVolume";
	public static String REM_SOUND_TEST_BY_SONG_NAME = "remSoundTestBySongName";
	
	
	@Override
	public IBinder onBind(Intent arg0) {		
		return null;
	}

	@Override
	public void onCreate() {
	       
	   	if ( mediaPlayer == null)
		{			
			if ( sing().whatTheREMPlayerIsDoing.equals(REMAlarmPlayerService.REM_TESTING_MAX_VOLUME))
			{
				//do nothing
			}else{
				launchMainAppScreen();
				addNotificationIconToStatusBar();
			}
//			if ( sing().whatTheREMPlayerIsDoing.equals(REMAlarmPlayerService.REM_TESTING_MAX_VOLUME))
//			{
//				//do nothing
//			}else{
//				recordAlarmsPlayedData();	this should only be done after the brightness and volume have been set from apdos
//			}
			calculateInitialVolumeIncrements();
		}
	   				Utils.boogieLog("REMAlarmPlayerService","onCreate()", "graw");
	}
	    
	@Override
	public void onStart(Intent intent, int startId) {
	   	Log.d("graw", "remPlayerService onStart()");
	   	if ( mediaPlayer == null)
		{
	   		sing().logg("graw", "MEDIA PLAYER IS NULL ON ONSTART OF REMALARMPLAYERSERVICE");
		}
		
		if ( intent.getStringExtra(REMAlarmPlayerService.REM_INTENT_MESSAGE) != null )
		{
			if ( intent.getStringExtra(REMAlarmPlayerService.REM_INTENT_MESSAGE).equals(REMAlarmPlayerService.REM_TESTING_MAX_VOLUME))
			{
				float maxRemVol = Float.parseFloat( Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_MAX_VOL_PREF, "100"));
				maxRemVol = maxRemVol / 100; // convert from percent back to fraction of one
				sing().setRemVolume(maxRemVol);	
				setUpMediaPlayer();
				setTheVolumeOfTheMediaPlayer();
				sing().logg("graw", "Testing max vol ");
				return;		//---------------------------
			}
		}
		if ( intent.getStringExtra(REMAlarmPlayerService.REM_SOUND_TEST_BY_SONG_NAME) != null )
		{
			String songName = intent.getStringExtra(REMAlarmPlayerService.REM_SOUND_TEST_BY_SONG_NAME);
			
			float maxRemVol = Float.parseFloat( Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_MAX_VOL_PREF, "100"));
			sing().setRemVolume(maxRemVol);	
			setUpMediaPlayer( songName ); // which also starts it
			setTheVolumeOfTheMediaPlayer();
						Utils.boogieLog("REMAlarmPlayerService", "onStart", "testing sound by songName : " + songName );
			return;		//-----------------------------
		}
		
		// START REM CUES PLAYING FOR REM ALARM !
		//-------------------------------------------
		sing().viewState = "rem";	
		if ( mediaPlayer == null ) setUpMediaPlayer();	// which also starts it !
		boolean hasMoreCuesToPlay = getBrightnessAndVolumeFromGeneratedAlarmCuesListAndSetOnSingleton();
		if ( ! hasMoreCuesToPlay ) 
		{
			mediaPlayer.setVolume( 0f , 0f );
			Toast.makeText(this,"hasMoreCuesToPlay = false!",Toast.LENGTH_LONG).show();
			return;
		}
		setTheVolumeOfTheMediaPlayer();
		Singleton.getInstance().remToneCyclesPlayed++;
		recordAlarmsPlayedData();
		//send broadcast to let main activity update itself
		sendBroadcast(new Intent(REMAlarmPlayerService.REM_SERVICE_PLAY));
	}
	
	@Override
	public void onDestroy() {
		if (mediaPlayer != null)
		{
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
			Utils.boogieLog("REMAlarmPlayerService", "onDestroy", "release the player and nullify it!");
		}else{
			Utils.boogieLog("REMAlarmPlayerService", "onDestroy", "media player was already == null");
		}
		
		super.onDestroy();
	}
		//====================================================================================
		//	Button Handlers
		//====================================================================================
	
	private void recordAlarmsPlayedData(){

		if ( sing().newDiaryEntryRowIdInsertedOnStartOfNewAlarm == 0)
		{
			initializeNewDreamRecord();
		}
		Utils.recordAlarmsPlayedData( this );
		
	}
	private void initializeNewDreamRecord(){
		
		// also inserts graph - sets newDiaryEntryRowIdInsertedOnStartOfNewAlarm value on singleton
		
		Long unixtime = Utils.getCurrentUnixTimeStamp();
		ContentValues contentValues = RecordsActivity.getContentValuesForNewDiaryEntry(unixtime , this);
		
		Uri uri = Uri.parse( ContentProviderLA.CONTENT_PROVIDER_URI + "/" + "graph") ;
		getContentResolver().insert(uri, contentValues);		
		
					Utils.boogieLog("REMAlarmPlayerService", "initializeNewDreamRecord()", " inserting a new created diary when alarm plays" );
	}
	
	//====================================================================================
	//	Volume Calculator
	//====================================================================================
	
	private boolean getBrightnessAndVolumeFromGeneratedAlarmCuesListAndSetOnSingleton(){
		if ( sing().alarmCuesGeneratedFromSettingsArrayList == null )
		{
			sing().alarmCuesGeneratedFromSettingsArrayList = Utils.generateArrayListOfAlarmCuesFromSettings(getApplicationContext());
			sing().generatedAlarmCuesCounter = 0;
		}
		ArrayList<AlarmsPlayedDataObject> acgfsal = sing().alarmCuesGeneratedFromSettingsArrayList;
		int gacc = sing().generatedAlarmCuesCounter;
		if ( gacc >= acgfsal.size() )
		{
			
			// WTF ?!?!? just make more !!!!!
			sing().setRemVolume( 0f );
			sing().remBrightness = ( 0f );
					Utils.boogieLog("REMAlarmPlayerService", "getBrightnessAndVolumeFromGeneratedAlarmCuesListAndSetOnSingleton()", "we have run out of generated alarm cues so assume that this alarm is no longer really playing" );
			return false;
		}
		AlarmsPlayedDataObject apdo = acgfsal.get( gacc );
		sing().setRemVolume(Float.parseFloat(apdo.volume ));
		sing().remBrightness = (Float.parseFloat(apdo.brightness));
		sing().generatedAlarmCuesCounter ++;
			 	Utils.boogieLog("REMAlarmPlayerService", "getBrightnessAndVolumeFromGeneratedAlarmCuesListAndSetOnSingleton()", "acgfsal.size() = " + acgfsal.size() );
		return true;
	}

	private void setTheVolumeOfTheMediaPlayer(){
		float v = sing().getRemVolume();
		mediaPlayer.setVolume(v,v);
		// override device volume
		AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC,100,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
				Utils.boogieLog("REMAlarmPlayerService", "setTheVolumeOfTheMediaPlayer", "rem volume set to : " + v );
	}
	
	private void calculateInitialVolumeIncrements(){
		float maxRemVolume = Float.parseFloat( Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_MAX_VOL_PREF, "50"));
		float amountToIncrementBy = maxRemVolume/ Integer.parseInt( Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_MIN_TO_MAX_VOL_PREF, "30"));
		sing().amountToIncreaseRemVolumeEachMinute = amountToIncrementBy;
	}

	private void setUpMediaPlayer(){
		String songName = Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_SOUND_PREF, "classicalarm");
		boolean mediaPlayerIsStartingItself = setMediaPlayerSong(songName, "rem");
		mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
		if ( mediaPlayerIsStartingItself == false ) {
			// set the volume to zero in case the player starts playing loudly
			mediaPlayer.setVolume(0,0);
			mediaPlayer.start();
		}
		mediaPlayer.setLooping(false);  
		mediaPlayer.setOnCompletionListener(this);
	}
	
	private void setUpMediaPlayer( String songName ){			
		boolean mediaPlayerIsStartingItself = setMediaPlayerSong(songName, "rem");
		mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
		if ( mediaPlayerIsStartingItself == false ) {
			// set the volume to zero in case the player starts playing loudly
			mediaPlayer.setVolume(0,0);
			mediaPlayer.start();  
		}
		mediaPlayer.setLooping(false);  
		mediaPlayer.setOnCompletionListener(this);
	}
	
	//====================================================================================
	//	on completion of media player
	//====================================================================================
	@Override
    public void onCompletion(MediaPlayer mp) {
		Integer delayBetweenSounds = Integer.parseInt( Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_SOUNDS_DELAY_PREF, "0") );
		new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {                                                
                    	if ( mediaPlayer != null ) mediaPlayer.start(); 
                    }
                }, delayBetweenSounds * 1000 );
		
		Utils.boogieLog("REMAlarmPlayerService","ON COMPLETION OF MEDIA PLAYER LOOP", "ON COMPLETION OF MEDIA PLAYER LOOP");

    }   
		
		
		//====================================================================================
		//	Getters & Setters
		//====================================================================================
		private Singleton sing(){
			return Singleton.getInstance();
		}

}
