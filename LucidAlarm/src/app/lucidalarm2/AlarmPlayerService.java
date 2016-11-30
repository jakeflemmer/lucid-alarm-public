package app.lucidalarm2;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.PowerManager;
import android.widget.Toast;
import android.content.*;
import android.app.*;

public abstract class AlarmPlayerService extends Service {	
	
	
	//=========================================================================================
		    
	MediaPlayer mediaPlayer;
	
	//====================================================================================
	//	Special functions
	//====================================================================================
	
		protected void launchMainAppScreen(){
			sing().logg("enter launchMainAppScreen()");
			Intent launchIntent = getPackageManager().getLaunchIntentForPackage("app.lucidalarm2");
			startActivity(launchIntent);
			// and turn screen on
			if ( sing().powerManager == null)
			{
				sing().powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
			}
			if ( sing().wakeLock == null )
			{
				sing().wakeLock = sing().powerManager.newWakeLock(
					PowerManager.SCREEN_DIM_WAKE_LOCK |
					PowerManager.ACQUIRE_CAUSES_WAKEUP |
					PowerManager.ON_AFTER_RELEASE ,"remWakeLock");
			}
			sing().wakeLock.acquire();
			
						Utils.boogieLog("AlarmPlayerService", "launchMainAppScreen", "we ");
			//addNotificationIconToStatusBar(); called from sub class if not testing
		}
		
		protected void addNotificationIconToStatusBar(){

			sing().logg("enter addNotificationIconToStatusBar");
			CharSequence text = "Select to snooze or dismiss this alarm";

		    // Set the icon, scrolling text and timestamp
		    Notification notification = new Notification(R.drawable.alarm_icon, text, System.currentTimeMillis());

		    // The PendingIntent to launch our activity if the user selects this notification
		    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		            new Intent(this, MainActivity.class), 0);

		    // Set the info for the views that show in the notification panel.
		    notification.setLatestEventInfo(this, "Lucid Alarm", text, contentIntent);

		    // Send the notification.
		    if ( sing().notificationManager == null)
		    {
		    	sing().notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		    } 
		    sing().notificationManager.notify(sing().NOTIFICATION, notification);
		}
		
		
		//====================================================================================
		//	Set Media Player Song 
		//====================================================================================
		
		protected boolean setMediaPlayerSong( String songName , String remOrFinal ){
			
			if ( mediaPlayer == null ) 
			{
				mediaPlayer = new MediaPlayer();
			}else{
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
				mediaPlayer = new MediaPlayer();
			}
			
			int resID=getResources().getIdentifier( songName, "raw", getPackageName() );
	        
			mediaPlayer.setVolume( 0f , 0f ); // we will set the correct volume immediately after this
			
	        if (resID > 0) // it's a raw sound file
			{
	        		mediaPlayer = MediaPlayer.create( this ,resID);
	        		// set the volume to zero in case the player starts playing loudly
	        		mediaPlayer.setVolume(0,0);
	        		mediaPlayer.start();
	        		Utils.boogieLog("AlarmPlayerService", "setMediaPlayerSong()", "resId = " + resID + " it's a raw sound");
	        		return true;
			}
			
	        // its a custom pre recorded sound
			File dir = new File(AudioActivity.getBasePathToCustomAudioRecordings()+AudioActivity.SOUNDS_DIRECTORY_NAME);
			File[] files = dir.listFiles(); // TODO filter only files of type ".3gp"
			for ( File file : files )
			{
				String fileName = file.getName();
				if ( songName.equals(fileName) )
				{
					sing().logg("graw","custom sound " + fileName);
					try{
						mediaPlayer.setDataSource(AudioActivity.getBasePathToCustomAudioRecordings() + AudioActivity.SOUNDS_DIRECTORY_NAME + fileName);
						mediaPlayer.prepare();
						mediaPlayer.start();
						Utils.boogieLog("AlarmPlayerService", "setMediaPlayerSong()", songName + " found in custom sounds");
			        		return false;
					}catch(Exception e)
					{
						Toast.makeText(getApplicationContext(),"ERROR : Unable to locate recorded sound : " + fileName,Toast.LENGTH_LONG).show();
					}
					break;
				}
			}
			
			// its selected from the device
			String songPath = "";
			
			if ( remOrFinal.equals("rem" ))
			{
				songPath = Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_SOUND_PATH_PREF, "");
			}
			else if ( remOrFinal.equals("final"))
			{
				songPath = Utils.getStringPreference(getApplicationContext(), SettingsActivity.FINAL_ALARM_SONG_PATH_PREF, "");
			}
			else{
				throw new Error();
			}
			
			
			
			Uri uri = Uri.parse(songPath);
			//remAlarm_mediaPlayer = MediaPlayer.create(this,uri);
			mediaPlayer = MediaPlayer.create(this,uri);  
						Utils.boogieLog("REMAlarmPlayerService", "setMediaPlayerSong()", "sound selected from device");
			return false;
		}
		
		//====================================================================================
		//	My function
		//====================================================================================	
			
		private Singleton sing(){
			return Singleton.getInstance();
		}
			
	



}
