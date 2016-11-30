package app.lucidalarm2;


import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.content.*;
import android.preference.*;
import android.net.*;
import android.media.*;

public class FinalAlarmPlayerService extends AlarmPlayerService { 
	
	public static String FINAL_SERVICE_PLAY = "finalServicePlay";	
	public static String FINAL_INTENT_MESSAGE = "finalIntentMessage";	
	public static String FINAL_TESTING_MAX_VOLUME = "finalTestingMaxVolume";
	
	//private float amountToIncreaseVolume = 0;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}	
	
	// MediaPlayer finalAlarm_mediaPlayer;	

	@Override
	public void onCreate() {		
		
		if ( sing().alarmHasBeenDismissed){
			Utils.boogieLog("FinalAlarmPlayerService", "onCreate()", "alarmHasBeenDismissed = true" );
			return;
		}
		
		checkMediaPlayerIsNotNull();
	}	
	
	@Override
	public void onStart(Intent intent, int startId) {

		// note this alarm and function is called repeatedly every 1 second
		
		if ( sing().alarmHasBeenDismissed) return;
		
		checkMediaPlayerIsNotNull();
		
		if ( intent.getStringExtra(FinalAlarmPlayerService.FINAL_INTENT_MESSAGE) != null )
		{
			if ( intent.getStringExtra(FinalAlarmPlayerService.FINAL_INTENT_MESSAGE).equals(FinalAlarmPlayerService.FINAL_TESTING_MAX_VOLUME))
			{					
				float maxVolPref = Float.parseFloat( Utils.getStringPreference(getApplicationContext(), SettingsActivity.FINAL_MAX_VOL_PREF, "100"));
				maxVolPref = maxVolPref / 100; // convert from percent to fraction of one
				mediaPlayer.setVolume(maxVolPref, maxVolPref );
				overrideDeviceVolume();
				return;
			}	
		}
		//send broadcast to be received on mainActivity
		sendBroadcast(new Intent(FinalAlarmPlayerService.FINAL_SERVICE_PLAY)); 
		sing().viewState = "final";
		
		incrementTheVolume();		
		overrideDeviceVolume();
	}
		
	@Override
	public void onDestroy() {
		
		if (mediaPlayer != null)
		{
			mediaPlayer.release();
			mediaPlayer = null;
		}
		super.onDestroy();
	}
	
	
	//====================================================================================
	//	My function
	//====================================================================================
	
	private void checkMediaPlayerIsNotNull(){
		if ( mediaPlayer == null)
		{
			launchMainAppScreen();
			if ( sing().whatTheFinalPlayerIsDoing.equals(FinalAlarmPlayerService.FINAL_TESTING_MAX_VOLUME))
			{
				// do nothing
			}else{
				addNotificationIconToStatusBar();
			}
			setUpMediaPlayer();
		}
	}
	private void setUpMediaPlayer(){
		String songName = Utils.getStringPreference(getApplicationContext(), SettingsActivity.FINAL_ALARM_SONG_PREF, "classicalarm");
		setMediaPlayerSong( songName , "final" );
		mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
		mediaPlayer.start(); 
		mediaPlayer.setLooping(true); 
	}

	private void incrementTheVolume(){
		
		if ( sing().amountToIncreaseFinalVolumeEverySecond == 0f)
		{
			sing().logg("graw", "amountToIncreaseFinalVolume == 0");
			calculateTheAmountToIncreaseVolume();
		}
		float maxVolPercentPref = Float.parseFloat( Utils.getStringPreference(getApplicationContext(), SettingsActivity.FINAL_MAX_VOL_PREF, "100"));
		float maxVolPref = maxVolPercentPref / 100f;
		if ( sing().finalAlarmVolume < maxVolPref )
		{
			sing().finalAlarmVolume += sing().amountToIncreaseFinalVolumeEverySecond;
			if ( sing().finalAlarmVolume > maxVolPref )
			{
				sing().finalAlarmVolume = maxVolPref;
			}
		}
		mediaPlayer.setVolume(sing().finalAlarmVolume,sing().finalAlarmVolume);		
					
		Log.d("graw","final volume is : " + sing().finalAlarmVolume);		 
	}
	private void overrideDeviceVolume(){
		AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC,100,AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	}
	private void calculateTheAmountToIncreaseVolume(){
		float maxVolPercentPref = Float.parseFloat( Utils.getStringPreference(getApplicationContext(), SettingsActivity.FINAL_MAX_VOL_PREF, "100"));
		float maxVolPref = maxVolPercentPref / 100f;
		int minutesToMaxVol = Integer.parseInt( Utils.getStringPreference(getApplicationContext(), SettingsActivity.MINS_TO_FINAL_MAX_VOL_PREF, "3"));
		float amountToIncrementBy = maxVolPref/(minutesToMaxVol * 60);// *60 cause this is called every second not every minute
		sing().amountToIncreaseFinalVolumeEverySecond = amountToIncrementBy;
	}
	
	//====================================================================================
	//	Gettes and Setters
	//====================================================================================
		
	private Singleton sing(){
		return Singleton.getInstance();
	}

}
