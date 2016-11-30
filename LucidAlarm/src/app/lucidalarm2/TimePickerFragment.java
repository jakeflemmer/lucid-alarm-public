package app.lucidalarm2;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

public class TimePickerFragment extends android.app.DialogFragment implements
		OnTimeSetListener {

	public static String REM_ALARM_TIME_SET = "remTimeSet";
	public static String FINAL_ALARM_TIME_SET = "finalTimeSet";
	
	public String fragType = "";
	public String screenContext = "";
	
	private int onTimeSetCalledCount = 0;	// work around due to android bug wherin method is called twice on done button and once on dismiss button
	
	public TimePickerFragment() {
		// TODO Auto-generated constructor stub
	}
		
		//====================================================================================
		//	STATE CHANGE HANDLERS
		//====================================================================================
	
	  @Override
	  public Dialog onCreateDialog(Bundle savedInstanceState) {
	      Log.d("graw","time picker frag enter onCreateDialog()");
		 
	      // Use the current time as the default values for the picker
	      final Calendar c = Calendar.getInstance();
	      int hour = c.get(Calendar.HOUR_OF_DAY);
	      int minute = c.get(Calendar.MINUTE);

	      // Create a new instance of TimePickerDialog and return it
	      return new TimePickerDialog(getActivity(), this, hour, minute,
	              DateFormat.is24HourFormat(getActivity()));
	  }

	public void onTimeSet(TimePicker arg0, int hour, int min) {
		
		sing().logg("time set");
		if ( onTimeSetCalledCount == 0 )
		{
			onTimeSetCalledCount = 1;
			return;
		}
		int hourOfDay = hour;
		int minute = min;
		Calendar now = Calendar.getInstance();    			
		
		
		if ( this.fragType == "final")
		{
		// FINAL ALARM
		sing().alarmHasBeenDismissed = false;
		// Set the final alarm
		Intent intent = new Intent(getActivity(), FinalAlarmBroadcastReceiver.class);    	
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 34324243, intent, 0);
    	    	
    	AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);        
    	
    	Calendar alarmCal = Calendar.getInstance();
    	alarmCal.setTimeInMillis(System.currentTimeMillis());
    	alarmCal.set(Calendar.HOUR_OF_DAY, hourOfDay );
    	alarmCal.set(Calendar.MINUTE, minute );
    	alarmCal.set(Calendar.SECOND, 0);
    	
    	// if time picked is earlier than current time make it for that time tomorrow
    	if ( alarmCal.before(now)) alarmCal.add(Calendar.DAY_OF_MONTH, 1); 
    	
    	sing().finalAlarmCalendar = alarmCal;
    	
    	alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(), 1*1000, pendingIntent);

    	sing().logg("Alarm set !");

    	updateSingletonWithFinalAlarmSelectedTime(hourOfDay, minute);
    	updateScreensFinalAlarmTextViewWithSelectedTime(hourOfDay, minute);
		}
		else if ( this.fragType == "rem")
		{
		// REM ALARM				
    	Intent remIntent = new Intent(getActivity(), REMAlarmBroadcastReceiver.class);    	
    	PendingIntent pendingRemIntent = PendingIntent.getBroadcast(getActivity(), 34324243, remIntent, 0);
    	
    	AlarmManager remAlarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        	
    	Calendar remAlarmCal = Calendar.getInstance();
    	remAlarmCal.setTimeInMillis(System.currentTimeMillis());
    	remAlarmCal.set(Calendar.HOUR_OF_DAY, hourOfDay );
    	remAlarmCal.set(Calendar.MINUTE, minute);	
    	remAlarmCal.set(Calendar.SECOND, 0);
    	
    	// if time picked is earlier than current time make it for that time tomorrow    	
    	if ( remAlarmCal.before(now)) remAlarmCal.add(Calendar.DAY_OF_MONTH, 1); 
    	
    	sing().remAlarmCalendar = remAlarmCal;
    	remAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, remAlarmCal.getTimeInMillis(), 6*10000, pendingRemIntent);

    	sing().logg("rem alarm set");		
    	
    	updateSingletonWithRemStartSelectedTime(hourOfDay, minute);		
    	
    	updateScreensRemStartTextViewWithSelectedTime(hourOfDay, minute);
		}
	}
	
	private void updateScreensFinalAlarmTextViewWithSelectedTime(int hourOfDay, int minute){
		getActivity().sendBroadcast(new Intent(TimePickerFragment.FINAL_ALARM_TIME_SET));	
	}
	private void updateScreensRemStartTextViewWithSelectedTime(int hourOfDay, int minute){
		getActivity().sendBroadcast(new Intent(TimePickerFragment.REM_ALARM_TIME_SET));	
	}
	private void updateSingletonWithFinalAlarmSelectedTime(int hourOfDay, int minute){
		Singleton.getInstance().logg("enter updateSingletonWithFinalAlarmSelectedTime()");	
		
		sing().setFinalAlarmTimeSelected(parseHourAndMinuteIntegersIntoTimeString(hourOfDay,minute));
	}
	private void updateSingletonWithRemStartSelectedTime(int hourOfDay, int minute){
		Singleton.getInstance().logg("enter updateSingletonWithRemStartSelectedTime()");	
		
		sing().setREMStartTimeSelected(parseHourAndMinuteIntegersIntoTimeString(hourOfDay,minute));
	}
	private String parseHourAndMinuteIntegersIntoTimeString(int hourOfDay, int minute){
		String hourString = Integer.toString(hourOfDay);
		String minuteString = Integer.toString(minute);
		return hourString + ":" + minuteString;
	}
	//====================================================================================
  	//	Getters and Setters
  	//====================================================================================

    private Singleton sing(){
		return Singleton.getInstance();
	}
}
