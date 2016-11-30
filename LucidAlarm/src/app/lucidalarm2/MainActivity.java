package app.lucidalarm2;

import android.R.integer;
import android.annotation.*;
import android.app.*;
import android.app.ActivityManager.*;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.preference.*;
import android.provider.*;
import android.speech.RecognizerIntent;
import android.support.v4.app.*;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.widget.*;

import java.io.*;
import java.util.*;

import app.lucidalarm2.ContractSchema.GraphsTable;
import app.lucidalarm2.ContractSchema.ResetsClickedTable;

import android.net.*;
import android.media.*;



public class MainActivity extends Activity {

	public int storeInt = 0;

	/*
	 * ok so our central concept and hypotheses is:
	 * the key to lucid dreaming is to wake up within your dreams
	 * the way to wake up in your dreams is to almost wake up but remain asleep
	 * the ability to lucid dream is the ability to be very awake and aware while asleep
	 * this app achieves this by pushing the envelope of wakefullness within sleep
	 * by providing more and more stimulation to the sleeper until they are highly
	 * awake but have not yet stopped sleeping and are still dreaming
	 */

	//=========================
	// Technical notes
	//=========================
	/*
	 *  the rem graph is auto saved if
	 *  1) the rem alarm is reset to a different time manually
	 *  2) the final alarm begins to play
	 *  3) the rem alarm on/off switch is turned to off and there is graphed data
	 *
	 *  the final alarm song is stored in the preferences settings
	 *  	- if the song title is not recognized in the raw folder
	 *  	- then the song path is looked up to find the song on the disk
	 *
	 *  both the FinalAlarmPlayerService and RemAlarmPlayerService extend the base AlarmPlayerServiceClass
	 *
	 *  AlarmPlayerService sets the wakeLock and wakes the device up if it is asleep when the alarm goes off
	 *
	 *  The flashing lights are only played for REM alarms and are turned on by the REM Alarm Player service and turned off by the delay/reset buttons
	 *
	 *  Some devices don't have a default voice recorder app so we have written our own !! :)
	 */

	//====================================================================================
	//	VARIABLES
    //====================================================================================

	private REMPlayerIntentReceiver remPlayerIntentReceiver;
	private String mainDisplayState = "base";
	private Handler flashingLightHandler;
	private boolean lightsStarted = false;


	public static String INTENT_STRING_MESSAGE = "intentStringMessage";
	public static String INTENT_LONG_MESSAGE = "intentLongMessage";
	protected static final int VOICE_RECOGNITION_REQUEST_OK = 123;

	//====================================================================================
	//	STATE CHANGE HANDLERS
	//====================================================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Utils.setActivityThemeFromPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setBaseViewState();
        		Utils.boogieLog("Main", "onCreate", "remAlarmSet = " + remAlarmIsSet());
	}

    @Override
    public void onStart(){
    		super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	if ( sing().changedThemeMainActivity )
    	{
    		sing().changedThemeMainActivity = false;
    		this.recreate();
    	}
    	Singleton.getInstance().logg("enter onResume MainActivity() jboogie");
    	updateAlarmTimeTextViewsFromSingleton();
    	updateAlarmTimeWidgetsFromSingleton();
		updatePowerIconStatus();

    	//register remPlayerIntentReceiver
        if (remPlayerIntentReceiver == null) remPlayerIntentReceiver = new REMPlayerIntentReceiver();
        IntentFilter if1 = new IntentFilter(REMAlarmPlayerService.REM_SERVICE_PLAY);
		IntentFilter if2 = new IntentFilter(FinalAlarmPlayerService.FINAL_SERVICE_PLAY);
		IntentFilter if3 = new IntentFilter(TimePickerFragment.REM_ALARM_TIME_SET);
        IntentFilter if4 = new IntentFilter(TimePickerFragment.FINAL_ALARM_TIME_SET);
		registerReceiver(remPlayerIntentReceiver, if1);
		registerReceiver(remPlayerIntentReceiver, if2);
		registerReceiver(remPlayerIntentReceiver, if3);
		registerReceiver(remPlayerIntentReceiver, if4);

		setViewStateFromSingleton();
		setViewsBasedOnTheme();

		//=========== WEB VIEW
		if ( sing().webViewStatus.equals("not running"))
		{
			addAndLoadTheWebView();
		}else{
			updateWebViewTime();
		}
    };

    @Override
    protected void onPause() {
    	Utils.boogieLog("MainActivity", "onPause()", "graw");
    	super.onPause();
    	releaseWakeLock();
    	//if (remPlayerIntentReceiver != null) unregisterReceiver(remPlayerIntentReceiver);
    }
    @Override
    protected void onStop() {
    	Utils.boogieLog("MainActivity", "onStop()", "graw");
    	super.onStop();
    	turnWebViewOff();
    }
    @Override
    protected void onDestroy() {
    	Utils.boogieLog("MainActivity", "onDestroy()", "graw");
    	sing().webViewStatus = "not running";
    	super.onDestroy();
    }
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		savedInstanceState.putString("mainDisplayState", this.mainDisplayState);
		super.onSaveInstanceState(savedInstanceState);
	}
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		this.mainDisplayState = savedInstanceState.getString("mainDisplayState");
		if ( mainDisplayState == "base")
		{
			setBaseViewState();
		}
		else if ( mainDisplayState == "remAlarmPlaying")
		{
			setRemAlarmPlayingViewState();
		}
		else if (mainDisplayState == "finalAlarmPlaying")
		{
			setFinalAlarmPlayingViewState();
		}
	}

	//====================================================================================
  	//	The Web View
  	//====================================================================================

	public void addAndLoadTheWebView(){
    	final WebView webView = (WebView)findViewById(R.id.webView);
    	webView.setVisibility(View.INVISIBLE);
    	webView.getSettings().setJavaScriptEnabled(true);
    	webView.addJavascriptInterface(new WebAppInterface(this), "Android");

    	String theme = Utils.getStringPreference(this, SettingsActivity.GENERAL_THEME_PREF, SettingsActivity.THEME_SPACE);
    	if ( theme.equals(SettingsActivity.THEME_SPACE)){
        	webView.loadUrl("file:///android_asset/webView/space.html");
    	}
    	else if ( theme.equals(SettingsActivity.THEME_CLOUDS)){
        	webView.loadUrl("file:///android_asset/webView/clouds.html");
    	}


    	new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        updateAlarmTimeWidgetsFromSingleton();
                        webView.setVisibility(View.VISIBLE);
                    }
                }, 1000);

		sing().webViewStatus = "running";

    }

	private void updateWebViewTime() {

    	final WebView webView = (WebView)findViewById(R.id.webView);
        webView.loadUrl("javascript:updateTrueTime()");
    }

	private void turnWebViewOff(){
		if ( sing().webViewStatus.equals("running"))
		{
			final WebView webView = (WebView)findViewById(R.id.webView);
			webView.loadUrl("file:///android_asset/nonexistent.html");
			webView.setVisibility(View.INVISIBLE);
			sing().webViewStatus = "not running";
		}
	}

    //====================================================================================
  	//	Button Handlers
  	//====================================================================================

    public void onSettingsButtonClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onRemAlarmSettingsButtonClick( View view ) {
    		Intent intent = new Intent(this, CueSettingsActivity.class);
        startActivity(intent);
    }

    public void onStopButtonClicked(View view) {
    		Singleton.getInstance().logg("mainActivity stopButtonClicked()");
		killAllServicesAndAlarms();
//    	stopRepeatingFinalAlarm();
//    	stopFinalAlarmPlayerService();
//    	stopRepeatingREMAlarm();
//    	stopREMAlarmPlayerService();
		releaseWakeLock();
    		setBaseViewState();
    }

    public void onResetREMButtonClicked(View view) {
    	// TODO **** !!!! rethink reset rem concept
    	//sing().remToneCyclesPlayedAtLastReset = Singleton.getInstance().remToneCyclesPlayed;
    	//sing().remInitialMaxVolume = sing().getRemVolume() * sing().remPercentageVolumeDropAfterReset;
    	//sing().remValleyVolume = sing().remInitialMaxVolume * sing().remValleyPercentageOfPeak;	// drop down to 80% of the reset volume
		addAndLoadTheWebView();
    	recordResetClickData();
		pushTheREMAlarmBackDelayMinutes();
		cancelNotificationInStatusBar();
		releaseWakeLock();
		stopTheFlashingLights();
				Utils.boogieLog("main", "onResetREMButtonClicked()", "graw");
    }
	/*public void onDelayREMButtonClicked(View view) {
		// i think this button is effectively removed from action so this code never runs
		addAndLoadTheWebView();
		pushTheREMAlarmBackDelayMinutes();
		cancelNotificationInStatusBar();
		releaseWakeLock();
		stopTheFlashingLights();
				Utils.boogieLog("main", "onDelayREMButtonClicked()", "graw");
	}*/
    public void onSnoozeButtonClicked(View view) {
    		Singleton.getInstance().logg("mainActivity snoozeButtonClicked()");
    		pushTheFinalAlarmBackSnoozeMinutes();
    		releaseWakeLock();
    		cancelNotificationInStatusBar();
    		sing().amountToIncreaseFinalVolumeEverySecond = 0f;
    }
	public void onDismissButtonClicked(View view) {
		sing().logg("onDismissButtonClicked");
		sing().alarmHasBeenDismissed = true;
		killAllServicesAndAlarms();
		setBaseViewState();
		updatePowerIconStatus();
		updateAlarmTimeWidgetsFromSingleton();
		cancelNotificationInStatusBar();
		releaseWakeLock();
		sing().amountToIncreaseFinalVolumeEverySecond = 0f;
		sing().finalAlarmVolume = 0f;
	}
	public void onBigFlashingLightButtonClick(View view){
		if ( this.mainDisplayState.equals("remAlarmPlaying") )
		{
			onResetREMButtonClicked(null);
		}
		else if ( this.mainDisplayState.equals("finalAlarmPlaying") )
		{
			onSnoozeButtonClicked( null );
		}else{
			throw new Error();
		}
	}

    public void onFinalAlarmTextViewClick(View view) {
    		TimePickerFragment newFragment = new TimePickerFragment();
    		newFragment.fragType = "final";
    		newFragment.show( getFragmentManager(), "timePicker");
    		Utils.boogieLog("mainActivity", "ONFINALALARMTEXTVIEWCLICK", "graw");
    }
	public void onREMAlarmTextViewClick(View view) {
		TimePickerFragment newFragment = new TimePickerFragment();
    		newFragment.fragType = "rem";
		newFragment.show( getFragmentManager(), "timePicker");
				Utils.boogieLog("mainActivity", "ONREMALARMTEXTVIEWCLICK", "graw");
	}
	// Graph icon removed from home screen
    //public void onGraphIconClick(View view) {
	//	Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
    //    startActivityForResult(intent,2);
	//	Intent intent = new Intent(this, GraphActivity.class);
    //    startActivity(intent);
	//}

    public void onLoadIconClick(View view) {
    		sing().logg("graw", "clicked load records icon");

    		Intent intent = new Intent(this, RecordsActivity.class);
    		startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==VOICE_RECOGNITION_REQUEST_OK  && resultCode==RESULT_OK) {
    		ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
    		//((TextView)findViewById(R.id.text1)).setText(thingsYouSaid.get(0));
    		Toast.makeText(this, thingsYouSaid.get(0), Toast.LENGTH_LONG).show();
        }

    }

    public void onRemServiceTextViewClick(View view) throws FileNotFoundException , IOException, ClassNotFoundException {

    }
	public void onAlarmIconClick(View view) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
			.setMessage("Test Final or REM Alarm?")
			.setPositiveButton("Final", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					testTheFinalAlarm();
				}
			})
			.setNegativeButton("REM", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					testTheRemAlarm();
				}
			});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
	private void testTheRemAlarm(){

		// for testing REM alarm
		// we want to play one chime of the bell with ? seconds total play
		int secondsToTest = 15;
		sing().whatTheREMPlayerIsDoing = REMAlarmPlayerService.REM_TESTING_MAX_VOLUME;
		stopREMAlarmPlayerService();
		disableTheTestAlarmIconButton();

		Intent intent = new Intent(this, REMAlarmPlayerService.class);
		intent.putExtra(REMAlarmPlayerService.REM_INTENT_MESSAGE, REMAlarmPlayerService.REM_TESTING_MAX_VOLUME);
		this.startService(intent);

		int maxRemVolumeInt = Integer.parseInt( Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_MAX_VOL_PREF, "100"));
		int maxVol = maxRemVolumeInt;// * 100;
		Toast.makeText(this, "Testing REM alarm at " + Integer.toString(maxVol) + "% volume for 15 seconds", Toast.LENGTH_LONG).show();

		// set timer to kill alarm after 30 seconds
		final Handler handler = new Handler();
		Timer timer = new Timer();

		TimerTask timerTask = new TimerTask(){
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						stopREMAlarmPlayerService();
						sing().setRemVolume(0);
						sing().whatTheREMPlayerIsDoing = REMAlarmPlayerService.REM_NOT_STARTED_YET;
						enableTheTestAlarmIconButton();
					}
				});
			}};
			timer.schedule(timerTask,secondsToTest*1000);
	}
	private void testTheFinalAlarm(){

		int secondsToTest = 15;
		stopFinalAlarmPlayerService();
		disableTheTestAlarmIconButton();

		sing().whatTheFinalPlayerIsDoing = FinalAlarmPlayerService.FINAL_TESTING_MAX_VOLUME;
		Intent intent = new Intent(this, FinalAlarmPlayerService.class);
		intent.putExtra(FinalAlarmPlayerService.FINAL_INTENT_MESSAGE, FinalAlarmPlayerService.FINAL_TESTING_MAX_VOLUME);
		this.startService(intent);

		String maxVolPref = Utils.getStringPreference(getApplicationContext(), SettingsActivity.FINAL_MAX_VOL_PREF, "100");
		Toast.makeText(this, "Testing final alarm at " + maxVolPref + "% volume for 15 seconds", Toast.LENGTH_LONG).show();

		// set timer to kill alarm after 30 seconds
		final Handler handler = new Handler();
		Timer timer = new Timer();

		TimerTask timerTask = new TimerTask(){
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						stopFinalAlarmPlayerService();
						sing().finalAlarmVolume = 0;
						enableTheTestAlarmIconButton();
						sing().whatTheFinalPlayerIsDoing = "nothing";
					}
				});
			}};
			timer.schedule(timerTask,secondsToTest*1000);
	}



	 public void playRecordedAudio(Uri uri){
		MediaPlayer mp = MediaPlayer.create(this, uri);
		mp.start();
	 }

	public void onFinalAlarmPowerIconClick(View view){
			view.setEnabled(false);
			onFinalAlarmPowerIconClick();
	}
	public void onFinalAlarmPowerIconClick(){
		TextView tv = (TextView) findViewById(R.id.finalAlarmTime_textView);
		if ( tv.getText().equals(Singleton.TIME_NOT_SET))
		{
			// this should not happen - the button should be disabled and dimmed
		}
		else
		{
			stopFinalAlarmPlayerService();
			stopRepeatingFinalAlarm();
			updateAlarmTimeTextViewsFromSingleton();
			updateAlarmTimeWidgetsFromSingleton();
			dimPowerIcon("final");
			cancelNotificationInStatusBar();
			sing().amountToIncreaseFinalVolumeEverySecond = 0f;
		}
	}
	public void onRemAlarmPowerIconClick(View view ){
		view.setEnabled(false);
		onRemAlarmPowerIconClick();
	}
	public void onRemAlarmPowerIconClick(){
		TextView tv = (TextView) findViewById(R.id.remAlarmTime_textView);
		if ( tv.getText().equals(Singleton.TIME_NOT_SET))
		{
			// this should not happen - the button should be disabled and dimmed
		}
		else
		{
			sing().alarmCuesGeneratedFromSettingsArrayList = null;
			sing().generatedAlarmCuesCounter = 0;
			stopREMAlarmPlayerService();
			stopRepeatingREMAlarm();
			updateAlarmTimeTextViewsFromSingleton();
			updateAlarmTimeWidgetsFromSingleton();
			dimPowerIcon("rem");
			saveREMGraphDataWhenREMAlarmShutsOff();
			cancelNotificationInStatusBar();
			if ( sing().screenIsDimmed ) setScreenBright();
			sing().setRemVolume(0f);
		}
	}

	public void onInfoIconClick(View view){

		if ( sing().wakeLock != null )
		{
			sing().logg("graw", "wakeLock is " + sing().wakeLock.isHeld());
		}else{
			sing().logg("graw", "wakeLock is null");
		}


		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
			.setMessage("Lucid Alarm")
			.setPositiveButton("About", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					displayAboutInfo();
				}
			})
			.setNegativeButton("Instructions", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					displayInstructionsInfo();
				}
			});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

    public void onKillServicesTextViewClick(View view){
		killAllServicesAndAlarms();
    }

	//====================================================================================
  	//	Written Information
  	//====================================================================================

	private void displayAboutInfo(){
		Intent intent = new Intent(this,InfoActivity.class);
		intent.putExtra(INTENT_STRING_MESSAGE,InfoActivity.ABOUT_INFO);
		startActivity(intent);
	}

	private void displayInstructionsInfo(){
		Intent intent = new Intent(this,InfoActivity.class);
		intent.putExtra(MainActivity.INTENT_STRING_MESSAGE,InfoActivity.INSTRUCTIONS_INFO);
		startActivity(intent);
	}

    //====================================================================================
  	//	REM Reset / Delay functions
  	//====================================================================================

    private void pushTheREMAlarmBackDelayMinutes(){

		sing().setRemVolume(0);
		sing().whatTheREMPlayerIsDoing = REMAlarmPlayerService.REM_NOT_STARTED_YET;
		sing().numberOfRemPeaksHit = 0;
		sing().alarmCuesGeneratedFromSettingsArrayList = null;
		sing().generatedAlarmCuesCounter = 0;
		stopRepeatingREMAlarm();	// which also sets alarm time to TIME_NOT_SET
		stopREMAlarmPlayerService();
		setREMRepeatingAlarmDelayMinutesFromNow();
		recordGraphDataPointOfZeroVolume();
		updateRemVolumeTextView();
		updateAlarmTimeTextViewsFromSingleton();
		updateAlarmTimeWidgetsFromSingleton();
		setBaseViewState();
		releaseWakeLock();
	}
	private void setREMRepeatingAlarmDelayMinutesFromNow(){

		// if the setting StartFinalAlarmAfterSnoozingRemAlarm is set to true then obviously set the final alarm now - not the rem alarm
		Boolean boo = Utils.getBooleanPreference(getApplicationContext(), SettingsActivity.REM_START_FINAL_ALARM_AFTER_SNOOZE_REM_ALARM, false);
		if ( boo )
		{
			// the REM alarm is finished now so save the record - only the final alarm will play after this
			saveREMGraphDataWhenREMAlarmShutsOff();
			sing().alarmHasBeenDismissed = false;
			pushTheFinalAlarmBackSnoozeMinutes();
		}else{
			int minutesToAdd = Integer.parseInt( Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_SERIES_DELAY_PREF, "15"));
			Intent intent = new Intent(this, REMAlarmBroadcastReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 34324243, intent, 0);
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (minutesToAdd * 60 * 1000), 60*1000, pendingIntent);
			Toast.makeText(this, "REM Alarm set " + minutesToAdd + " minutes from now!", Toast.LENGTH_LONG).show();
			// set the singleton with selected time
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			String hourString = Integer.toString(hour);
			String minuteString = Integer.toString(minute + minutesToAdd);
			sing().setREMStartTimeSelected(hourString + ":" + minuteString);
		}
	}

	//====================================================================================
  	//	Final Alarm Snooze
  	//====================================================================================

	private void pushTheFinalAlarmBackSnoozeMinutes(){

		sing().finalAlarmVolume = 0;
		stopFinalAlarmPlayerService();
		stopRepeatingFinalAlarm();
		setFinalAlarmSnoozeMinutesFromNow( -1 );
		updateAlarmTimeTextViewsFromSingleton();
		updateAlarmTimeWidgetsFromSingleton();
		setBaseViewState();
		releaseWakeLock();
	}
	private void setFinalAlarmSnoozeMinutesFromNow( int minutesToAdd ){

		if ( minutesToAdd < 0 )
		{
			minutesToAdd = Integer.parseInt( Utils.getStringPreference(getApplicationContext(), SettingsActivity.SNOOZE_MINS_PREF, "3"));
		}
    	Intent intent = new Intent(this, FinalAlarmBroadcastReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 34324243, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (minutesToAdd * 60 * 1000), 1000, pendingIntent);
		Toast.makeText(this, "Final Alarm set " + minutesToAdd + " minutes from now!", Toast.LENGTH_LONG).show();
		// set the singleton with selected time
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		String hourString = Integer.toString(hour);
		String minuteString = Integer.toString(minute + minutesToAdd);
		sing().setFinalAlarmTimeSelected(hourString + ":" + minuteString);

				Utils.boogieLog("MainActivity", "setFinalAlarmSnoozeMinutesFromNow", " minutes from now = " + minutesToAdd);
	}

	//===================================================================================
	//  Check if alarms are set
	//===================================================================================

	private boolean finalAlarmIsSet() {
		Intent intent = new Intent(this, FinalAlarmBroadcastReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

	private boolean remAlarmIsSet() {
		Intent intent = new Intent(this, REMAlarmBroadcastReceiver.class);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

	//====================================================================================
  	//	My functions
  	//====================================================================================

	private void recordGraphDataPointOfZeroVolume(){
		ContentValues contentValues = new ContentValues();
		contentValues.put( GraphsTable.COLUMN_NAME_DIARY_ID, sing().newDiaryEntryRowIdInsertedOnStartOfNewAlarm );
		contentValues.put( GraphsTable.COLUMN_NAME_UNIX_TIME, Utils.getCurrentUnixTimeStamp() );
		contentValues.put( GraphsTable.COLUMN_NAME_VOLUME, "0" );
		contentValues.put( GraphsTable.COLUMN_NAME_BRIGHTNESS, "0" );

		Uri uri = Uri.parse( ContentProviderLA.CONTENT_PROVIDER_URI + "/" + "graph" + "/" + "data" ) ;
		getContentResolver().insert(uri, contentValues);
	}

	private void recordResetClickData(){
		ContentValues contentValues = new ContentValues();
		contentValues.put( ResetsClickedTable.COLUMN_NAME_DIARY_ID, sing().newDiaryEntryRowIdInsertedOnStartOfNewAlarm );
		contentValues.put( ResetsClickedTable.COLUMN_NAME_UNIX_TIME, Utils.getCurrentUnixTimeStamp() );
		Uri uri = Uri.parse( ContentProviderLA.CONTENT_PROVIDER_URI + "/" + "graph" + "/" + "resets" ) ;
		getContentResolver().insert(uri, contentValues);
		// AND in fact we want to record regular alarm data here to make our graphs look pretty
		Utils.recordAlarmsPlayedData( this );
				Utils.boogieLog("MainActivity", "recordResetsClickData", "recording new reset click");
	}
    private void displayTheTimeUntilFinalAlarmGoesOff(){

		Calendar now = Calendar.getInstance();
		long nowMillis = now.getTimeInMillis();
		long alarmTimeMillis = 0;
		if (Singleton.getInstance().finalAlarmCalendar != null)
		{
			alarmTimeMillis = Singleton.getInstance().finalAlarmCalendar.getTimeInMillis();
		}
		long millisToAlarm = alarmTimeMillis - nowMillis;
		long minutesToAlarm = millisToAlarm/1000/60;
		if ( alarmTimeMillis > 0 )
		{
			Toast.makeText(this, "Final alarm is set for "+ convertMinutesToTime(minutesToAlarm), Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "Final alarm is not set", Toast.LENGTH_SHORT).show();
		}

	}
    private void displayTheTimeUntilREMAlarmGoesOff(){

		Calendar now = Calendar.getInstance();
		long nowMillis = now.getTimeInMillis();
		long alarmTimeMillis = 0;
		if (sing().remAlarmCalendar != null)
		{
			alarmTimeMillis = Singleton.getInstance().remAlarmCalendar.getTimeInMillis();
		}
		long millisToAlarm = alarmTimeMillis - nowMillis;
		long minutesToAlarm = millisToAlarm/1000/60;
		if ( alarmTimeMillis > 0 )
		{
			Toast.makeText(this, "REM alarm is set for "+ convertMinutesToTime(minutesToAlarm), Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(this, "REM alarm is not set", Toast.LENGTH_SHORT).show();
		}

	}
    private String convertMinutesToTime(long minutesToAlarm){
    	if ( minutesToAlarm < 60)
    	{
    		return Long.toString(minutesToAlarm) + " minutes from now.";
    	}else{
    		int minsToAlarm = (int) minutesToAlarm;
    		int hrs = minsToAlarm / 60;
    		int mins = minsToAlarm % 60;
    		return Long.toString(hrs) + " hours and " + Long.toString(mins) + " minutes from now";
    	}
    }

	private void setScreenBrightnessToManual(){
		// BRIGHT Settings.System.putInt(getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	}

	/*
	private void randomizeTheRemAlarmSound(){
		ArrayList<Integer> list = new ArrayList<Integer>();
		Field[] fields = R.raw.class.getFields();
		int numNames = fields.length;
		Random randomGenerator = new Random();
	    int randomInt = randomGenerator.nextInt(numNames);
	    Field chosenField = fields[randomInt];
	    String soundName = chosenField.getName();
	    sing().remAlarmSound = soundName;

		SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("remAlarmSounds_preference", soundName);
		editor.commit();

		sing().logg("graw", "randomizing the rem sound : " + soundName);

		//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//String setString = prefs.getString("remAlarmSounds_preference", "mind");

		sing().logg("graw", "stored pref sound : " + setString);

	}*/
	private void setRecommendedRemVolumeBasedOnPastResetsOfChosenSound(){
		// all data has been loaded when todays graph was saved


		// TODO
		// we need to thouroughly rethink revisit this whole concept idea strategy


		/*
		// get all DreamRecords of selected sound
		SharedPreferences settings =  PreferenceManager.getDefaultSharedPreferences(this);
		String chosenSound = settings.getString("remAlarmSounds_preference", "tibetanbell");
		ArrayList<DreamRecordDataObject> recordsOfSound = new ArrayList<DreamRecordDataObject>();

		sing().logg("graw", "chosen sound : " + chosenSound);

		for ( int i = 0; i < sing().allDreamRecordsLoadedArrayList.size(); i++)
		{
			DreamRecordDataObject rr = sing().allDreamRecordsLoadedArrayList.get(i);
			if ( rr.sound.equals(chosenSound))
			{
				recordsOfSound.add(rr);
			}
		}
		// get all reset volumes clicked for current sound
		ArrayList<String> resetVolumeStrings = new ArrayList<String>();
		for ( int j = 0; j < recordsOfSound.size(); j ++)
		{
			DreamRecordDataObject rrs = recordsOfSound.get(j);
			for ( int k = 0; k < rrs.resetsClickedArrayList.size(); k++)
			{
				AlarmsPlayedDataObject apdo = rrs.resetsClickedArrayList.get(k);
				resetVolumeStrings.add(apdo.volume);
			}
		}

		sing().logg("graw", "reset volume strings : " + resetVolumeStrings.size());

		if ( resetVolumeStrings.size() < 1 ) return;

		// convert strings to floats
		ArrayList<Float> resetVolumeFloats = new ArrayList<Float>();
		for ( int l = 0 ; l < resetVolumeStrings.size(); l++)
		{
			resetVolumeFloats.add( Float.parseFloat(resetVolumeStrings.get(l)) );
		}
		// get the average
		float total=0;
		for ( int m=0; m < resetVolumeFloats.size(); m++)
		{
			total += resetVolumeFloats.get(m);
		}
		float average = total / resetVolumeFloats.size();
		// set the initialremmaxvolume to that average

		SharedPreferences.Editor editor = settings.edit();
		editor.putString("initialREMMaxVolume_preference", Float.toString(average*100) );
		editor.commit();

		*/
	}

    //====================================================================================
  	//	Stop the alarms
  	//====================================================================================

    private void killAllServicesAndAlarms(){
    		sing().logg("graw", "all services and alarms killed");
    		stopFinalAlarmPlayerService();
    		stopRepeatingFinalAlarm();
        stopREMAlarmPlayerService();
        stopRepeatingREMAlarm();
		updateAlarmTimeTextViewsFromSingleton();
		updateAlarmTimeWidgetsFromSingleton();
		cancelNotificationInStatusBar();
    }
	private void turnOffREMAlarmWhenFinalAlarmStarts(){
		if ( sing().getREMStartTimeSelected().equals(Singleton.TIME_NOT_SET))
		{
			// rem alarm already turned off
		}else{
			saveREMGraphDataWhenREMAlarmShutsOff();
			stopRepeatingREMAlarm();
			stopREMAlarmPlayerService();
			stopTheFlashingLights();
			sing().alarmCuesGeneratedFromSettingsArrayList = null;
			sing().generatedAlarmCuesCounter = 0;
		}
	}
	private void saveREMGraphDataWhenREMAlarmShutsOff(){
		sing().logg("saving the graph from main activity");

		// We insert new diary record entry when rem alarms begin play
		// and insert a row for each alarm played
		sing().newDiaryEntryRowIdInsertedOnStartOfNewAlarm = 0; /* by setting this to zero, the next time an REM alarm plays a new records will be generated */

		//randomizeTheRemAlarmSound();
		sing().numberOfRemPeaksHit = 0;
		sing().setRemVolume(0);
		setRecommendedRemVolumeBasedOnPastResetsOfChosenSound();
	}
	private void stopFinalAlarmPlayerService(){
    		Intent intent = new Intent(this, FinalAlarmPlayerService.class);
    		this.stopService(intent);
    }

    private void stopRepeatingFinalAlarm(){
    		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    		Intent intent = new Intent(this, FinalAlarmBroadcastReceiver.class);
    		PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 34324243, intent, 0);

    		alarmManager.cancel(pendingIntent);
    					sing().setFinalAlarmTimeSelected(Singleton.TIME_NOT_SET);
    }

    private void stopREMAlarmPlayerService(){
    		Intent intent = new Intent(this, REMAlarmPlayerService.class);
    		this.stopService(intent);
    					Utils.boogieLog("main", "stopREMAlarmPlayerService()", "graw");
    }

    private void stopRepeatingREMAlarm(){
    		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    		Intent intent = new Intent(this, REMAlarmBroadcastReceiver.class);
    		PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 34324243, intent, 0);

    		alarmManager.cancel(pendingIntent);
    					sing().setREMStartTimeSelected(Singleton.TIME_NOT_SET);
    					// turn off power icon by rem alarm

    }

	private void releaseWakeLock(){

		//this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if ( sing().wakeLock != null )
		{
			sing().logg("graw", "wakeLock released from MainActivity : " + sing().wakeLock);
			sing().wakeLock.release();
			sing().wakeLock = null;
		}else{
			sing().logg("graw","wakeLock is null");
		}
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	private void cancelNotificationInStatusBar(){
		sing().logg("enter cancelNotificationSatusBar");
		if ( sing().notificationManager != null)
		{
			sing().notificationManager.cancel(sing().NOTIFICATION);
		}
	}
    //====================================================================================
  	//	Update views
  	//====================================================================================

	private void updateRemVolumeTextView(){
    		TextView vtv = (TextView) findViewById(R.id.volume_textView);
    		vtv.setText( " " + Utils.formatFloatToPercentText( sing().getRemVolume() ) + "%");
    }
	private void updateAlarmTimeTextViewsFromSingleton(){
    		TextView fatTv = (TextView) findViewById(R.id.finalAlarmTime_textView);
    		TextView ratTv = (TextView) findViewById(R.id.remAlarmTime_textView);

        		fatTv.setText("   Final Alarm : " + Singleton.getInstance().getFinalAlarmTimeSelected());
        		ratTv.setText("   REM Start : " + Singleton.getInstance().getREMStartTimeSelected());

    }
	private void updatePowerIconStatus(){

		ImageView fiv = (ImageView) findViewById(R.id.finalAlarmPowerIcon_imageView);
		ImageView riv = (ImageView) findViewById(R.id.remAlarmPowerIcon_imageView);

		if ( sing().getFinalAlarmTimeSelected().equals(Singleton.TIME_NOT_SET))
		{
			fiv.setEnabled(false);
			dimPowerIcon("final");
		}
		else
		{
			fiv.setEnabled(true);
			brightenPowerIcon("final");
		}
		if ( sing().getREMStartTimeSelected().equals(Singleton.TIME_NOT_SET))
		{
			riv.setEnabled(false);
			dimPowerIcon("rem");
		}
		else
		{
			riv.setEnabled(true);
			brightenPowerIcon("rem");
		}
	}

	//====================================================================================
  	//	Update WEB views
  	//====================================================================================

	private void updateAlarmTimeWidgetsFromSingleton(){

    	final WebView webView = (WebView)findViewById(R.id.webView);
        String remText = Singleton.getInstance().getREMStartTimeSelected();
        String finalText = Singleton.getInstance().getFinalAlarmTimeSelected();
        webView.loadUrl("javascript:showAlarmTimeDisplays('" + remText + "','" + finalText+ "')");
	}

    //====================================================================================
  	//	Utility functions
  	//====================================================================================
   /*
	public void turnOnScreen(String remOrFinal){

		// turn screen on
		Window window = getWindow();
		// below does not appear to work
		//window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		//unlock screen
		window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		//keep it on until reset / delay
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setScreenBrightnessToManual();
		if ( remOrFinal.equals("rem"))
		{
			//dim it based on singleton
			if ( sing().turnOnScreenDimmed == true )
			{
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.screenBrightness = .01f;
				getWindow().setAttributes(lp);
				sing().screenIsDimmed = true;
			}
		}else if ( remOrFinal.equals("final"))
		{
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.screenBrightness = .8f;
			getWindow().setAttributes(lp);
			sing().screenIsDimmed = false;
		}
	}*/
	public void turnOnScreen(String remOrFinal){

		// turn screen on
		Window window = getWindow();
		// below does not appear to work
		//window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		//unlock screen
		window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

		//keep it on until reset / delay
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		//setScreenBrightnessToManual();
		if ( remOrFinal.equals("rem"))
		{
			//dim it based on singleton
			if ( sing().turnOnScreenDimmed == true )
			{
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.screenBrightness = .01f;
				// BRIGHT getWindow().setAttributes(lp);
			}
		}else if ( remOrFinal.equals("final"))
		{
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.screenBrightness = .8f;
			// BRIGHT getWindow().setAttributes(lp);
		}
	}
	private void setScreenBright(){
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = .8f;
		// BRIGHT getWindow().setAttributes(lp);
		sing().screenIsDimmed = false;
	}

//	private boolean isServiceRunning(String serviceName) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceName.equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

	//====================================================================================
	// HIDE / SHOW VIEWS BASED ON THEMES
	//====================================================================================

	private void setViewsBasedOnTheme(){
		String theme = Utils.getStringPreference(this, SettingsActivity.GENERAL_THEME_PREF, SettingsActivity.THEME_SPACE );
		 if ( theme.equals(SettingsActivity.THEME_SPACE))
		 {
			 // HIDE VIEWS
			 View digClock = findViewById(R.id.digitalClock1);
			 digClock.setVisibility(View.INVISIBLE);

			 TextView fatTv = (TextView) findViewById(R.id.finalAlarmTime_textView);
	    	 TextView ratTv = (TextView) findViewById(R.id.remAlarmTime_textView);
	    	 fatTv.setVisibility(View.INVISIBLE);
	    	 ratTv.setVisibility(View.INVISIBLE);
	    	 View fatPI = findViewById(R.id.finalAlarmPowerIcon_imageView);
	    	 View ratPI = findViewById(R.id.remAlarmPowerIcon_imageView);
	    	 fatPI.setVisibility(View.INVISIBLE);
	    	 ratPI.setVisibility(View.INVISIBLE);

	    	 View fatSI = findViewById(R.id.finalAlarmSettingsIcon_imageButton);
	    	 View ratSI = findViewById(R.id.remAlarmSettingsIcon_imageButton);
	    	 fatSI.setVisibility(View.INVISIBLE);
	    	 ratSI.setVisibility(View.INVISIBLE);

	    	 // SET COLORS OF SNOOZE / DISMISS BUTTONS
	    	 View snooze = findViewById(R.id.snooze_button);
	    	 snooze.setBackgroundColor(0xFF770000);
	    	 View dismiss = findViewById(R.id.dismiss_button);
	    	 dismiss.setBackgroundColor(0xFFCC0000);
	    	 View reset = findViewById(R.id.resetRem_button);
	    	 reset.setBackgroundColor(0xFF770000);
//	    	 View delay = findViewById(R.id.delayRem_button);
//	    	 delay.setBackgroundColor(0xFFCC0000);

		 }

		 if ( theme.equals(SettingsActivity.THEME_CLOUDS))
		 {
			 // HIDE VIEWS
			 View digClock = findViewById(R.id.digitalClock1);
			 digClock.setVisibility(View.INVISIBLE);

			 TextView fatTv = (TextView) findViewById(R.id.finalAlarmTime_textView);
	    	 TextView ratTv = (TextView) findViewById(R.id.remAlarmTime_textView);
	    	 fatTv.setVisibility(View.INVISIBLE);
	    	 ratTv.setVisibility(View.INVISIBLE);
	    	 View fatPI = findViewById(R.id.finalAlarmPowerIcon_imageView);
	    	 View ratPI = findViewById(R.id.remAlarmPowerIcon_imageView);
	    	 fatPI.setVisibility(View.INVISIBLE);
	    	 ratPI.setVisibility(View.INVISIBLE);

	    	 View fatSI = findViewById(R.id.finalAlarmSettingsIcon_imageButton);
	    	 View ratSI = findViewById(R.id.remAlarmSettingsIcon_imageButton);
	    	 fatSI.setVisibility(View.INVISIBLE);
	    	 ratSI.setVisibility(View.INVISIBLE);

	    	 // SET COLORS OF SNOOZE / DISMISS BUTTONS
	    	 View snooze = findViewById(R.id.snooze_button);
	    	 snooze.setBackgroundColor(0xFF000077);
	    	 View dismiss = findViewById(R.id.dismiss_button);
	    	 dismiss.setBackgroundColor(0xFF0000CC);
	    	 View reset = findViewById(R.id.resetRem_button);
	    	 reset.setBackgroundColor(0xFF000077);
	    	 //View delay = findViewById(R.id.delayRem_button);
	    	 //delay.setBackgroundColor(0xFF0000CC);

		 }
	}
    //====================================================================================
  	//	Set View States
   	//====================================================================================
	private void setViewStateFromSingleton(){
		if ( sing().viewState.equals("rem"))
		{
			setRemAlarmPlayingViewState();
		}
		else if ( sing().viewState.equals("final"))
		{
			setFinalAlarmPlayingViewState();
		}
	}
    public void setBaseViewState(){
    	 	 this.mainDisplayState = "base";
    	 	 sing().viewState = "base";
		 makeStopButtonInvisible();
         makeResetButtonInvisible();
		 //makeDelayButtonInvisible();
         makeAlternateClockInvisible();
		 makeSnoozeButtonInvisible();
		 makeDismissButtonInvisible();
		 makeVolumeTextViewInvisible();
		 makeBrightnessTextViewVisible( false );
		 makeFlashingLightButtonsVisible( false );
		 //makeAlarmTextViewsVisible();
		 //makePowerIconsVisible();
		 makeIconsVisible();
		 makeWebViewVisible();


		 			Utils.boogieLog("main", "setBaseViewState()", "base");
    }
    public void setRemAlarmPlayingViewState(){
    		this.mainDisplayState = "remAlarmPlaying";
		makeStopButtonVisible();
		makeResetButtonVisible();
		//makeDelayButtonVisible();
		//makeAlternateClockVisible();
		//makeVolumeTextViewVisible();
		//makeBrightnessTextViewVisible( true );
		makeAlarmTextViewsInvisible();
		makePowerIconsInvisible();
		makeIconsInvisible();
		colorTheFlashingLightBasedOnSettingsPreferences();
		makeWebViewInvisible();
					Utils.boogieLog("main", "setRemAlarmPlayingViewState()", "base");
    }
    public void setFinalAlarmPlayingViewState(){
    		this.mainDisplayState = "finalAlarmPlaying";
		makeSnoozeButtonVisible();
		makeDismissButtonVisible();
		//makeAlternateClockVisible();
		makeStopButtonVisible();
		makeAlarmTextViewsInvisible();
		makePowerIconsInvisible();
		//makeVolumeTextViewInvisible();
		//makeBrightnessTextViewVisible( false );
		makeIconsInvisible();
		makeWebViewInvisible();
					Utils.boogieLog("main", "setFinalAlarmPlayingViewState()", "graw");
    }

    //====================================================================================
  	//	TOGGLE VIEWS VISIBILITY
   	//====================================================================================

    private void makeWebViewVisible(){
    	// TODO - if theme is ironman??
		View wv = findViewById(R.id.webView);
       	wv.setVisibility(View.VISIBLE);
	}

	private void makeWebViewInvisible(){
		View wv = findViewById(R.id.webView);
       	wv.setVisibility(View.INVISIBLE);
	}

	private void makeVolumeTextViewVisible(){
		TextView tv = (TextView) findViewById(R.id.volume_textView);
       	tv.setVisibility(View.VISIBLE);
       	ImageView iv = ( ImageView ) findViewById(R.id.volumeIcon_imageView);
       	iv.setVisibility(View.VISIBLE);
		updateRemVolumeTextView();
	}
	private void makeBrightnessTextViewVisible( boolean boo ){
		boolean lightsOn = Utils.getBooleanPreference(getApplicationContext(), SettingsActivity.REM_LIGHT_ON_PREF, true);
		if ( ! lightsOn ) boo = false;
		TextView tv = (TextView) findViewById( R.id.brightness_textView );
		tv.setVisibility( ( boo == true ? View.VISIBLE : View.INVISIBLE) );
		ImageView iv = (ImageView) findViewById( R.id.brightnessIcon_imageView );
		iv.setVisibility( ( boo == true ? View.VISIBLE : View.INVISIBLE) );
       			Utils.boogieLog("main", "makeBrightnessTextViewVisible", "boo : " + boo );
	}
	private void makeVolumeTextViewInvisible(){
		TextView tv = (TextView) findViewById(R.id.volume_textView);
       	tv.setVisibility(View.INVISIBLE);
       	ImageView iv = ( ImageView ) findViewById(R.id.volumeIcon_imageView);
       	iv.setVisibility(View.INVISIBLE);
	}
	private void makeFlashingLightButtonsVisible( boolean boo ){
		Button bigLight = (Button) findViewById(R.id.bigFlashing_button);
		//Button smallLight = (Button) findViewById(R.id.smallFlashing_button);
		bigLight.setVisibility( ( boo == true ? View.VISIBLE : View.INVISIBLE) );
		//smallLight.setVisibility( ( boo == true ? View.VISIBLE : View.INVISIBLE) );
       			Utils.boogieLog("main", "makeFlashingLightButtonsVisible", "boo : " + boo );
	}
	private void setResetAndDelayButtonsBackgroundsBlack( boolean boo ) {
		// note : we don't want to hide these - they are covering up everything else on the screen :)
		Button rb = (Button) findViewById(R.id.resetRem_button);
//       	Button db = (Button) findViewById(R.id.delayRem_button);
       	rb.setBackgroundColor( Color.parseColor("#000000") );
//       	db.setBackgroundColor( Color.parseColor("#000000") );
       	rb.setTextColor( Color.parseColor("#000000") );
//       	db.setTextColor( Color.parseColor("#000000") );
	}
    public void makeSnoozeButtonVisible(){
     	Button b = (Button) findViewById(R.id.snooze_button);
       	b.setVisibility(View.VISIBLE);
       	Utils.boogieLog("main", "makeSnoozeButtonVisible", "graw");
    }
	public void makeSnoozeButtonInvisible(){
     	Button b = (Button) findViewById(R.id.snooze_button);
       	b.setVisibility(View.INVISIBLE);
       	Utils.boogieLog("main", "makeSnoozeButtonInvisible", "graw");
    }
	public void makeDismissButtonVisible(){
     	Button b = (Button) findViewById(R.id.dismiss_button);
       	b.setVisibility(View.VISIBLE);
    }
	public void makeDismissButtonInvisible(){
     	Button b = (Button) findViewById(R.id.dismiss_button);
       	b.setVisibility(View.INVISIBLE);
    }
	public void makeStopButtonVisible(){
     	Button b = (Button) findViewById(R.id.stop_button);
       	b.setVisibility(View.VISIBLE);
    }
    public void makeStopButtonInvisible(){
       	Button b = (Button) findViewById(R.id.stop_button);
       	b.setVisibility(View.INVISIBLE);
    }
    public void makeResetButtonVisible(){
       	Button b = (Button) findViewById(R.id.resetRem_button);
       	b.setVisibility(View.VISIBLE);
    }
    public void makeResetButtonInvisible(){
       	Button b = (Button) findViewById(R.id.resetRem_button);
       	b.setVisibility(View.INVISIBLE);
    }
//	public void makeDelayButtonVisible(){
//       	Button b = (Button) findViewById(R.id.delayRem_button);
//       	b.setVisibility(View.VISIBLE);
//    }
//    public void makeDelayButtonInvisible(){
//       	Button b = (Button) findViewById(R.id.delayRem_button);
//       	b.setVisibility(View.INVISIBLE);
//    }
    private void makeAlternateClockVisible(){
    		View v = findViewById(R.id.alternateClock_digitalClock);
    		v.setVisibility(View.VISIBLE);
    }
    private void makeAlternateClockInvisible(){
    		View v = findViewById(R.id.alternateClock_digitalClock);
    		v.setVisibility(View.INVISIBLE);
    }
    private void makeAlarmTextViewsInvisible(){
    		View ftv = findViewById(R.id.finalAlarmTime_textView);
    		ftv.setVisibility(View.INVISIBLE);
    		View rtv = findViewById(R.id.remAlarmTime_textView);
    		rtv.setVisibility(View.INVISIBLE);
    }
    private void makeAlarmTextViewsVisible(){
    		View ftv = findViewById(R.id.finalAlarmTime_textView);
    		ftv.setVisibility(View.VISIBLE);
    		View rtv = findViewById(R.id.remAlarmTime_textView);
    		rtv.setVisibility(View.VISIBLE);
    }
	private void makePowerIconsInvisible(){
		View fv = findViewById(R.id.finalAlarmPowerIcon_imageView);
    		fv.setVisibility(View.INVISIBLE);
    		View rv = findViewById(R.id.remAlarmPowerIcon_imageView);
    		rv.setVisibility(View.INVISIBLE);
	}
	private void makePowerIconsVisible(){
		View fv = findViewById(R.id.finalAlarmPowerIcon_imageView);
    		fv.setVisibility(View.VISIBLE);
    		View rv = findViewById(R.id.remAlarmPowerIcon_imageView);
    		rv.setVisibility(View.VISIBLE);
	}
	private void makeIconsInvisible(){
		LinearLayout ll = (LinearLayout) findViewById(R.id.icons_linearLayout);
		ll.setVisibility(View.INVISIBLE);
	}
	private void makeIconsVisible(){
		LinearLayout ll = (LinearLayout) findViewById(R.id.icons_linearLayout);
		ll.setVisibility(View.VISIBLE);
	}
    @TargetApi(11)
	@SuppressLint("NewApi")
	private void brightenPowerIcon(String alarmType){
    	if ( alarmType.equals("rem"))
    	{
    		ImageView riv = (ImageView) findViewById(R.id.remAlarmPowerIcon_imageView);
    		if (Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.ECLAIR_MR1) {
    		    riv.setAlpha(1f);
    		}else{
    			riv.getBackground().setAlpha(255);
    		}

    	}else if ( alarmType.equals("final"))
    	{
    		ImageView fiv = (ImageView) findViewById(R.id.finalAlarmPowerIcon_imageView);
    		if (Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.ECLAIR_MR1) {
    			fiv.setAlpha(1f);
    		}else{
    			fiv.getBackground().setAlpha(255);
    		}
    	}
    }
    @TargetApi(11)
	@SuppressLint("NewApi")
	private void dimPowerIcon(String alarmType){
    	if ( alarmType.equals("rem"))
    	{
    		ImageView riv = (ImageView) findViewById(R.id.remAlarmPowerIcon_imageView);
    		if (Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.ECLAIR_MR1) {
    		    riv.setAlpha(sing().powerIconDimmedAlpha);
    		}else{
    			riv.getBackground().setAlpha(70);
    		}

    	}else if ( alarmType.equals("final"))
    	{
    		ImageView fiv = (ImageView) findViewById(R.id.finalAlarmPowerIcon_imageView);
    		if (Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.ECLAIR_MR1) {
    			fiv.setAlpha(sing().powerIconDimmedAlpha);
    		}else{
    			fiv.getBackground().setAlpha(70);
    		}
    	} else{
    		sing().logg("graw", "EXCEPTION alarm type wrong");
    	}
    }
    @SuppressLint("NewApi")
	private void brightenAlarmIcon(){
   		ImageView iv = (ImageView) findViewById(R.id.alarmIcon_imageView);
   		if (Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.ECLAIR_MR1) {
   			iv.setAlpha(1f);
   		}else{
   			iv.getBackground().setAlpha(255);
   		}
    }
	@SuppressLint("NewApi")
	private void dimAlarmIcon(){
   		ImageView iv = (ImageView) findViewById(R.id.alarmIcon_imageView);
   		if (Integer.parseInt(Build.VERSION.SDK) > Build.VERSION_CODES.ECLAIR_MR1) {
   			iv.setAlpha(sing().powerIconDimmedAlpha);
   		}else{
   			iv.getBackground().setAlpha(70);
   		}
    }

	//====================================================================================
  	//	Color the flashing lights
   	//====================================================================================

	private void colorTheFlashingLightBasedOnSettingsPreferences() {
		Button bigLight = (Button) findViewById(R.id.bigFlashing_button);
//		Button smallLight = (Button) findViewById(R.id.smallFlashing_button);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String lightColor = prefs.getString("remLightColor_preference","white");

		if ( lightColor.equals("red"))
		{
			bigLight.setBackgroundColor( Color.parseColor("#FF0000") );
			//smallLight.setBackgroundColor( Color.parseColor("#AA0000") );
		}
		else if ( lightColor.equals("green"))
		{
			bigLight.setBackgroundColor( Color.parseColor("#00FF00") );
			//smallLight.setBackgroundColor( Color.parseColor("#00AA00") );
		}
		else if ( lightColor.equals("blue"))
		{
			bigLight.setBackgroundColor( Color.parseColor("#0000FF") );
			//smallLight.setBackgroundColor( Color.parseColor("#0000AA") );
		}
		else if ( lightColor.equals("white"))
		{
			bigLight.setBackgroundColor( Color.parseColor("#FFFFFF") );
			//smallLight.setBackgroundColor( Color.parseColor("#AAAAAA") );
		}else{
			throw new Error();
		}
		bigLight.setTag(lightColor);

				//Utils.boogieLog("main", "colorTheFlashingLightBasedOnSettingsPreferences()", "lightColor : " + lightColor );
	}

	private void startTheFlashingLights(){

		if ( lightsStarted ) return;

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean lightsOn = prefs.getBoolean("remLightOn_preference",true) ;
		if ( ! lightsOn ) return;

		flashingLightHandler = new Handler();
		int lightFrequency = Integer.parseInt( prefs.getString("remFrequency_preference","2") );
		flashingLightHandler.postDelayed( flashingLightRunnable, 1000 / lightFrequency );

		setResetAndDelayButtonsBackgroundsBlack ( true );
		makeFlashingLightButtonsVisible ( true );

		lightsStarted = true;

				Utils.boogieLog("main", "startTheFlashingLights()", "graw" );
	}

	private void stopTheFlashingLights(){
		if ( ! lightsStarted ) return;
		flashingLightHandler.removeCallbacks(flashingLightRunnable);
		makeFlashingLightButtonsVisible ( false );
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = -1f;	// negative number restores the brightness to default
		lightsStarted = false;
					Utils.boogieLog("main", "stopTheFlashingLights()", "graw" );
	}

	private Runnable flashingLightRunnable = new Runnable() {
		   @Override
		   public void run() {
			   SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			   int lightFrequency = Integer.parseInt( prefs.getString("remFrequency_preference","2") );
			   flashTheLightOnOrOff();
		       flashingLightHandler.postDelayed(this, 1000 / lightFrequency );
		   }
		};

	private void flashTheLightOnOrOff(){
		Button bigLight = (Button) findViewById(R.id.bigFlashing_button);
		//Button smallLight = (Button) findViewById(R.id.smallFlashing_button);

		if ( bigLight.getTag().equals("black"))
		{
			colorTheFlashingLightBasedOnSettingsPreferences();
		}
		else
		{
			bigLight.setBackgroundColor(0x000000);
			//smallLight.setBackgroundColor(0x000000);
			bigLight.setTag("black");
		}
				//Utils.boogieLog("main", "flashTheLightOnOrOff()", "graw" );
	}

	private void setScreenBrightnessToSingletonBrightnessValue() {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		boolean lightsOn = prefs.getBoolean("remLightOn_preference",true) ;
		if ( ! lightsOn ) return;

		WindowManager.LayoutParams lp = getWindow().getAttributes();
		float remBrightness = sing().remBrightness;
		lp.screenBrightness = remBrightness;
		getWindow().setAttributes(lp);

		TextView tv = (TextView) findViewById(R.id.brightness_textView);
		tv.setText( "Bright : " + Utils.formatFloatToPercentText( remBrightness ) + "%" );

		setFlashingLightsClockVolumeAndBrightnessViewsAlphaToScreenBrightness();
					Utils.boogieLog("main", "setScreenBrightnessToSingletonBrightnessValue()", "sing().remBrightness : " + remBrightness);
	}

	private void setFlashingLightsClockVolumeAndBrightnessViewsAlphaToScreenBrightness(){
		View clock = findViewById(R.id.alternateClock_digitalClock);
		View vol = findViewById(R.id.volume_textView);
		View bright = findViewById(R.id.brightness_textView);
		Button bigLight = (Button) findViewById(R.id.bigFlashing_button);
		View brightnessIcon = findViewById( R.id.brightnessIcon_imageView );
		View volumeIcon = findViewById( R.id.volumeIcon_imageView );
		clock.setAlpha( sing().remBrightness );
		vol.setAlpha( sing().remBrightness );
		bright.setAlpha( sing().remBrightness );
		bigLight.setAlpha( sing().remBrightness );
		brightnessIcon.setAlpha( sing().remBrightness );
		volumeIcon.setAlpha( sing().remBrightness );

	}

	//====================================================================================
  	//	Toggle Component Enable Disabled
   	//====================================================================================
	private void disableTheTestAlarmIconButton(){
		View v = findViewById(R.id.alarmIcon_imageView);
		v.setEnabled(false);
		v.setClickable(false);
		dimAlarmIcon();
	}
    public void enableTheTestAlarmIconButton(){
		View v = findViewById(R.id.alarmIcon_imageView);
		v.setEnabled(true);
		v.setClickable(true);
		brightenAlarmIcon();
	}


    //====================================================================================
  	//	Getters and Setters
  	//====================================================================================

    private Singleton sing(){
		return Singleton.getInstance();
	}

  	//====================================================================================
  	//	Handle intents passed from the REM alarm player service
  	//====================================================================================

    public class REMPlayerIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        		sing().logg("enter onReceive() remPlayerIntentReceiver main");
            if (intent.getAction().equals(REMAlarmPlayerService.REM_SERVICE_PLAY)) {
				turnOnScreen("rem");
				updateRemVolumeTextView();
				setRemAlarmPlayingViewState();
				startTheFlashingLights();
				setScreenBrightnessToSingletonBrightnessValue();
				turnWebViewOff();
						Utils.boogieLog("REMPlayerIntentReceiver", "onReceive()", "remServicePlay");
      		}
			else if (intent.getAction().equals(FinalAlarmPlayerService.FINAL_SERVICE_PLAY)) {
            	Singleton.getInstance().logg("enter onReceive() finalPlayerIntentReceiver sup");
				if ( sing().alarmHasBeenDismissed )
				{
					// do nothing
					sing().logg("mainActivity alarmHasBeenDismissed");
				}else{
					turnOnScreen("final");
					setFinalAlarmPlayingViewState();
					turnOffREMAlarmWhenFinalAlarmStarts();
				}
      		}
			else if (intent.getAction().equals(TimePickerFragment.REM_ALARM_TIME_SET))
			{
				sing().logg("mainActivity REM_ALARM_TIME_SET");
				updateAlarmTimeWidgetsFromSingleton();
				updateAlarmTimeTextViewsFromSingleton();
				updatePowerIconStatus();
				displayTheTimeUntilREMAlarmGoesOff();
			}
			else if (intent.getAction().equals(TimePickerFragment.FINAL_ALARM_TIME_SET))
			{
				sing().logg("mainActivity FINAL_ALARM_TIME_SET");
				updateAlarmTimeWidgetsFromSingleton();
				updateAlarmTimeTextViewsFromSingleton();
				updatePowerIconStatus();
				displayTheTimeUntilFinalAlarmGoesOff();
			}
			else
			{
				sing().logg("graw", "mainActivity intent action not recognized EXCEPTION");
			}
    	}
	}
}






