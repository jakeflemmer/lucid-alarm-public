package app.lucidalarm2;

import java.util.Calendar;

import android.util.Log;
import java.util.*;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;

import java.text.*;

public class Singleton {

	private static Singleton _instance;
	
	// STRING CONSTANTS
	//----------------------------------------------------------------
	public static final String TIME_NOT_SET = "Not Set";
	public static final String DEFAULT_REM_ALARM_SOUND = "tibetanbell";
	public static final String DEFAULT_FINAL_ALARM_SOUND = "classicalarm";
	public static final String SHAPE_OSCILLATING = "oscillating";
	public static final String SHAPE_INCREASING = "increasing";
	
	// Theme change -------------------------------------
	public boolean changedThemeMainActivity = false;
	public boolean changedThemeAudioFragment = false;
	public boolean changedThemeAudioActivity = false;
	public boolean changedThemeCueSettingsActivity = false;
	public boolean changedThemeDiaryActivity = false;
	public boolean changedThemeRecordsActivity = false;
	public boolean changedThemeSettingsActivity = false;
	
	// Web View
	//-----------------------------------------------------
	public String webViewStatus = "not running";
	
	public void changeTheThemeOfAllActivities() {
		changedThemeMainActivity = true;
		changedThemeAudioFragment = true;
		changedThemeAudioActivity = true;
		changedThemeCueSettingsActivity = true;
		changedThemeDiaryActivity = true;		
		changedThemeRecordsActivity = true;		
		changedThemeSettingsActivity = true;
	}//---------------------------------------------------
	
	
	public String viewState = "";
	public String whatTheREMPlayerIsDoing = REMAlarmPlayerService.REM_NOT_STARTED_YET;
	public String whatTheFinalPlayerIsDoing = "nothing";
	public boolean toastingOn = true;
	public boolean isLoggingOn = true;
	
	// Final alarm
	public float finalAlarmVolume = 0;
	//public float finalAlarmMaxVolume = 1f;
	//public int minutesOfSnooze = 3;
	//public int minutesToFinalAlarmMaxVolume = 3;
	public boolean alarmHasBeenDismissed = false;
	public float amountToIncreaseFinalVolumeEverySecond = 0;
	
	// REM alarm SETTINGS
	public float remBrightness = 1.0f;
	//public float remInitialMaxVolume = 1.0f;
	//public int minutesToMaxREMVolume = 30;
	//public int delayMinutes = 30;
	public String remAlarmSound = "tibetanbell";
	public float remPercentageVolumeDropAfterReset = 0.7f;
	public float remValleyPercentageOfPeak = 0.6f;
	public boolean turnOnScreenDimmed = true;
	public float amountToIncreaseRemVolumeEachMinute = 0; 
	public float remValleyVolume;	
	private float _remVolume = 0;
	public int remToneCyclesPlayed = 0;
	public int remToneCyclesPlayedAtLastReset=-10;
	public int timesResetButtonPushed = 0;
	public int minutesToValleyREMVolume = 4;
	public int numberOfRemPeaksHit = 0;
	
	//public ArrayList<DreamRecordDataObject> allDreamRecordsLoadedArrayList = new ArrayList<DreamRecordDataObject>();
	
	
	//public ArrayList<AlarmsPlayedDataObject> resetsClickedArrayList = new ArrayList<AlarmsPlayedDataObject>();
	//public DreamRecordDataObject dreamRecordSelected;
	
	//public DreamRecordDataObject newDreamRecord;
	public String defaultdreamTitle = "Dream";
	public boolean graphOrNotesDataDirty = false;	
	
	public float powerIconDimmedAlpha = 0.3f; 
	
	public boolean screenIsDimmed = false;
	public int greenColor = 0x00aa00;
	public int greenColorAlpha = 0xFF009900;
	public int yellowColor = 0x777700;
	public int lucidDreamColor = 0xFFccaa00;
	public String lucidDreamColorString = "lucid";
	public int moreLinkColor = 0xFF229900;
	
	private String _finalAlarmTimeSelected = "Not Set";
	private String _remStartTimeSelected = "Not Set";
	
	public Calendar finalAlarmCalendar;
	public Calendar remAlarmCalendar;
	public SimpleDateFormat dateAndTimeFormat = new SimpleDateFormat("HH:mm:ss a  MM/dd/yyyy ");
	public SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("MM/dd/yyyy");
	public PowerManager powerManager;
	public WakeLock wakeLock;
	
	//public String filename = "DreamRecords";
	public String filename = "50";
	public int NOTIFICATION = 10002; //Any unique number for this notification
	public NotificationManager notificationManager;
	
	public String pathToExportedDreamsXmlFile = "/exports";	
	public String fileNameOfExportedDreamsXmlFile = "exportedDxXml.txt";
	public String fileNameOfExportedDreamsTextDocFile = "exportedDreams.txt";
		
	
	public long newDiaryEntryRowIdInsertedOnStartOfNewAlarm = 0; /* determines if new diary records should be created each time an REM alarm plays */
	
	
	// note : we need at least two dirty flags for this - one to draw /redraw the graph - the other for saving the cue settings
	public boolean cuePreferencesHaveChangedSinceCueGraphActivity = false;
	//public boolean newCueNameHasBeenGeneratedUponCueSettingsChange = false;
	//public String newlyGeneratedCueName = "";
	
	
	public ArrayList<AlarmsPlayedDataObject> alarmCuesGeneratedFromSettingsArrayList;
	public int generatedAlarmCuesCounter = 0;
	
	
	public String dreamNotesCopiedDownFromAudioActivity = "";
	
	
	
	
	protected Singleton() {
		
	}
	
	public static Singleton getInstance(){
		if ( _instance == null)
		{
			_instance = new Singleton();
		}
		return _instance;
	}
	
	
	
	
	public void toast(String message){
		if ( ! toastingOn ) return;
		
	}
	public void logg(String message){
		if ( ! isLoggingOn ) return;
		//Log.d("graw",message);		
	}
	public void logg(String tag, String message){
		if ( ! isLoggingOn ) return;
		//Log.d(tag,message);		
	}
	
	public float getRemVolume(){
		return _remVolume;
	}
	
	public void setRemVolume(float vol){
		if (vol > 1 ) vol = 1f;
		_remVolume = vol;
	}
	public void setFinalAlarmTimeSelected(String unformattedTime){		
		if ( unformattedTime.equals(Singleton.TIME_NOT_SET))
		{
			_finalAlarmTimeSelected = unformattedTime;
		}else{
			_finalAlarmTimeSelected = takeUnformattedTimeStringReturnFormattedString(unformattedTime);
		}
	}
	public String getFinalAlarmTimeSelected(){
		return _finalAlarmTimeSelected;
	}
	public void setREMStartTimeSelected(String unformattedTime){		
		if ( unformattedTime.equals(Singleton.TIME_NOT_SET))
		{
			_remStartTimeSelected = unformattedTime;
		}else{
			_remStartTimeSelected = takeUnformattedTimeStringReturnFormattedString(unformattedTime);
		}
	}
	public String getREMStartTimeSelected(){
		return _remStartTimeSelected;
	}
	
	
	// Utility Function
	public String takeUnformattedTimeStringReturnFormattedString(String unformattedTime){
		
		String[] timesArray = unformattedTime.split(":");
		int unformattedHour = Integer.parseInt(timesArray[0]);
		int unformattedMinute = Integer.parseInt(timesArray[1]);
		
		
		String hourString="";
		String minuteString="";
		String meridian="";
		
		if ( unformattedMinute > 59 )
		{
			unformattedHour ++;
			unformattedMinute -= 60;
		}
		if ( unformattedMinute > 59 )	// The maximum number of minutes is 90 so we check need to check minutes twice
		{
			unformattedHour ++;
			unformattedMinute -= 60;
		}		
		if (unformattedHour == 12 )
		{
			hourString = Integer.toString(12);
			meridian = "pm";
		}else if (unformattedHour < 12 )
		{
			hourString = Integer.toString(unformattedHour);
			meridian = "am"; 
		}else if (unformattedHour > 12)
		{
			hourString = Integer.toString(unformattedHour - 12);
			meridian = "pm";
		}
		
		if (unformattedMinute < 10 )
		{
			minuteString = "0" + Integer.toString(unformattedMinute);
		}else{
			minuteString = Integer.toString(unformattedMinute);
		}
		
		return hourString + ":" + minuteString + meridian;
	}
	

}
