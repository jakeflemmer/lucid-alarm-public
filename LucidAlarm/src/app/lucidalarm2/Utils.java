package app.lucidalarm2;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import app.lucidalarm2.ContractSchema.CuesTable;
import app.lucidalarm2.ContractSchema.GraphsTable;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Utils {
	
	
	public static void boogieLog( String className, String methodName, String message){
		Log.v(className, methodName + " " + message);
	}
	
	//===================================================================
	// THEMES
	//===================================================================
	
	public static void setActivityThemeFromPreferences( Activity activity ) {
		String theme = getStringPreference( activity, SettingsActivity.GENERAL_THEME_PREF, SettingsActivity.THEME_SPACE );
		if ( theme.equals(SettingsActivity.THEME_SPACE) )
		{
			activity.setTheme(R.style.spaceTheme);
		}
		else if ( theme.equals(SettingsActivity.THEME_ZEN) )
		{
			activity.setTheme(R.style.zenTheme);
		}
		else if ( theme.equals(SettingsActivity.THEME_CLOUDS) )
		{
			activity.setTheme(R.style.cloudsTheme);
		}
		else {
			throw new Error("what style is this?");
		}
		
	}
	
	public static void setTextViewColorByTheme( TextView textView, Activity activity){
		String theme = Utils.getStringPreference( activity, SettingsActivity.GENERAL_THEME_PREF, SettingsActivity.THEME_SPACE );
		if ( theme.equals(SettingsActivity.THEME_CLOUDS) )
		{
			textView.setTextColor(0xff33ff33);
		}
		else if ( theme.equals(SettingsActivity.THEME_ZEN) )
		{
			textView.setTextColor(0xffffff00);
		}
		else if ( theme.equals(SettingsActivity.THEME_SPACE) )
		{
			textView.setTextColor(0xff33ff33);
		}
		else {
			throw new Error("what style is this?");
		}
	}
	public static void setMoreOrLessTextViewLinkColorByTheme( TextView textView, Activity activity, Boolean moreLink){
		String theme = Utils.getStringPreference( activity, SettingsActivity.GENERAL_THEME_PREF, SettingsActivity.THEME_SPACE );
		if ( theme.equals(SettingsActivity.THEME_CLOUDS) )
		{
			if ( moreLink )
			{
				textView.setTextColor(0xff33ff33);
			}else{
				textView.setTextColor(0xffffa500);
			}
		}
		else if ( theme.equals(SettingsActivity.THEME_ZEN) )
		{
			if ( moreLink )
			{
				textView.setTextColor(0xff33ff33);
			}else{
				textView.setTextColor(0xffffff00);
			}
			
		}
		else if ( theme.equals(SettingsActivity.THEME_SPACE) )
		{
			if ( moreLink )
			{
				textView.setTextColor(0xff33ff33);
			}else{
				textView.setTextColor(0xffffa500);
			}
			
		}
		else {
			throw new Error("what style is this?");
		}
	}
	
	//====================================================================
	 
	public static String formatFloatToPercentText( float val ) {
		String floatText = Float.toString( val * 100 ); 
		if ( floatText.contains("."))
		{
			floatText = floatText.substring(0, floatText.indexOf(".") );
		}
		//String percentText = Integer.toString( Integer.parseInt(floatText) );
		return floatText;
	}
	
	public static String takeIntReturnStringOfMinutesAndSeconds(int duration){
		int durationInSeconds = duration/1000;
		int secs;
		String minutes = "";
		String seconds = "";
		if (durationInSeconds > 59 )
		{
			minutes = Integer.toString(durationInSeconds/60);
			secs = durationInSeconds%60;
			if ( secs < 10 )
			{
				seconds = "0"+Integer.toString(secs);	
			}else{
				seconds = Integer.toString(secs);
			}
		}else{
			minutes = "0";
			if ( durationInSeconds < 10 )
			{
				seconds = "0" + Integer.toString(durationInSeconds);	
			}else{
				seconds = Integer.toString(durationInSeconds);
			}
			
		}
		return minutes + ":" + seconds;
	}
	
	public static EditText makeGreenBorderEditText(String text, int minHeight, boolean numbersOnly, Context context ){
    	final EditText et = new EditText( context );
        et.setBackgroundColor(0xFF000000);
        
        //tv.setTypeface(null, Typeface.BOLD);
        et.setText(	text);
        et.setGravity(Gravity.LEFT|Gravity.TOP);
        et.setBackgroundResource(R.drawable.greenborder);
        
		et.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		et.setTextColor(sing().greenColorAlpha);		
		
        // Defining the layout parameters of the TextView
       	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
       			LinearLayout.LayoutParams.WRAP_CONTENT);
       	 et.setLayoutParams(lp);
       	 
       	 et.setMinimumHeight(minHeight);
       	 et.setMinimumWidth(100);
       	 
       	 if ( numbersOnly )
       	 {
       		 et.setInputType(InputType.TYPE_CLASS_NUMBER);
       	 }
       	 
        return et;
    }
	
	public static String intToStringWithZeroAsEmptyQuotes(int theInt){
		if ( theInt == 0 )
		{
			return "";
		}else{
			return Integer.toString(theInt);
		}
	}
	
	public static long getCurrentUnixTimeStamp(){
		long unixTime = System.currentTimeMillis() / 1000L;
		return unixTime;
	}
	
	
	
	
	
	
	//====================================================================================
	//	Getters methods
	//====================================================================================
	
	public static String getStringPreference(Context context, String prefName, String defaultValue ){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String string = prefs.getString(prefName, defaultValue);
		return string.trim();
	}
	
	public static Boolean getBooleanPreference(Context context, String prefName, Boolean defaultValue ){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Boolean boo = prefs.getBoolean(prefName, defaultValue);
		return boo;
	}
	
	public static int getColorIntFromLightColorPreference( Context context ) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String colorString = prefs.getString( SettingsActivity.REM_COLOR_PREF, "white" );
		int colorInt = getColorIntFromColorString(colorString);
		return colorInt;
	}
	
	public static int getColorIntFromColorString ( String colorString ) {
		int color = Color.parseColor("#FFFFFF");
		if ( colorString.equals( "blue" ))
		{
			color = Color.parseColor("#6666FF");
		}
		if ( colorString.equals( "red" ))
		{
			color = Color.parseColor("#FF6666");
		}
		if ( colorString.equals( "green" ))
		{
			color = Color.parseColor("#66FF66");
		}
		if ( colorString.equals( "yellow" ))
		{
			color = Color.parseColor("#FFFF00");
		}
		if ( colorString.equals( "lucid" ))
		{
			color = Color.parseColor("#FFA500");
		}
		return color;
	}
	
			//====================================================================================
			//	Setter methods
			//====================================================================================
	
	public static void setStringPreference(Context context, String string, String prefName ){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(prefName, string );
		editor.commit();
	}
	public static void setBooleanPreference(Context context, boolean boo, String prefName ){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(prefName, boo);
		editor.commit();
	}
	
	
	
	
		//====================================================================================
		//	Utility methods
		//====================================================================================
	
	
	
//		public static void generateNewCueNameOnCueSettingsChange(Context context) {
//			
//			String cueName = getStringPreference(context, SettingsActivity.REM_CUE_NAME_PREF, "default intervals cue");
//			int indexOfOpenBracket = cueName.indexOf("(");
//			int indexOfCloseBracket = cueName.indexOf(")");
//			int previousVersionNumber = 0;
//			if ( indexOfOpenBracket > 0 && indexOfCloseBracket > 0 && indexOfCloseBracket > indexOfOpenBracket 
//							&& indexOfCloseBracket - indexOfOpenBracket < 3 )
//			{
//				String numberString = cueName.subSequence(indexOfOpenBracket, indexOfCloseBracket).toString();
//				try {
//					previousVersionNumber = Integer.parseInt(numberString);
//				}catch ( Exception e){
//					// do nothing
//				}
//			}
//			String newCueName;
//			if ( previousVersionNumber > 0 )
//			{
//				newCueName = cueName.substring(0,indexOfOpenBracket) + Integer.toString(previousVersionNumber) + ")";
//			}
//			else{
//				newCueName = cueName + " (1)";	
//			}
//			sing().newCueNameHasBeenGeneratedUponCueSettingsChange = true;
//			sing().newlyGeneratedCueName = newCueName;
//					boogieLog("Utils", "generateNewCueNameOnCueSettingsChange", "graw");
//		}
		
//		public static void commitNewlyGeneratedCueName(Context context) {
//			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//			SharedPreferences.Editor editor = prefs.edit();
//			editor.putString(SettingsActivity.REM_CUE_NAME_PREF, sing().newlyGeneratedCueName );
//			editor.commit();
//			//sing().newCueNameHasBeenGeneratedUponCueSettingsChange = false;
//			//sing().newlyGeneratedCueName = "";
//					boogieLog("Utils", "commitNewlyGeneratedCueName", "graw");
//		}
		
		public static void parseCuesTableRowIntoPrefSettingsAndSetThem( Context context, Cursor cursor , long rowId ) {
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			SharedPreferences.Editor editor = prefs.edit();
			
			if (cursor.moveToFirst()){
				   do{
					   
					   long cRowId = cursor.getLong(cursor.getColumnIndex(CuesTable._ID));
					   if ( cRowId == rowId )
					   {
						   // now we know we have the correct table row from the cursor rows in our hand
						   // we need to walk down all the columns of this row
						   // and transpose their values into the preferences settings of the same name as the column they are in
						   
						   int i = 0;	// which is the hidden _ID field 
						   for ( i = 1; i < SettingsActivity.remSettingPreferencesArray.length; i++ )
						   {
							   String stringFromCursor = cursor.getString(cursor.getColumnIndex(SettingsActivity.remSettingPreferencesArray[i]));
							   if ( stringFromCursor.trim().toLowerCase().equals("true"))
							   {
								   editor.putBoolean(SettingsActivity.remSettingPreferencesArray[i], true );
							   }
							   else if ( stringFromCursor.trim().toLowerCase().equals("false"))
							   {
								   editor.putBoolean(SettingsActivity.remSettingPreferencesArray[i], false );
							   }
							   else {
								   editor.putString(SettingsActivity.remSettingPreferencesArray[i], stringFromCursor );
							   }
						   }
						   editor.commit();
					   }
				   }while(cursor.moveToNext());
				}
			
						boogieLog("Utils", "parseCuesTableRowIntoPrefSettingsAndSetThem", "nada");
		}
		
		public static ContentValues parseCueSettingsIntoContentValues( Context context )
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			ContentValues contentValues = new ContentValues();
			
			// we want to walk down all the cue settings and create a content value for each one
			int i = 0;	// skip the hidden _ID field
			for ( i = 1; i < SettingsActivity.remSettingPreferencesArray.length; i++ )
			{
				String stringFromPref = prefs.getString(SettingsActivity.remSettingPreferencesArray[i],"3");	// TODO the string returned if no preference is set
				contentValues.put(SettingsActivity.remSettingPreferencesArray[i], stringFromPref);
			}
			
			return contentValues;
		}
		
		//====================================================================================
		//====================================================================================
		// REM ALARM CUES GENERATOR
		//====================================================================================
		//====================================================================================
		
		public static ArrayList<AlarmsPlayedDataObject> generateArrayListOfAlarmCuesFromSettings( Context context ) {
		
			float maxBrightness = Float.parseFloat(  Utils.getStringPreference(context , SettingsActivity.REM_MAX_BRIGHTNESS_PREF, "100") ); 
			float maxVolume = Float.parseFloat(  Utils.getStringPreference(context , SettingsActivity.REM_MAX_VOL_PREF, "100") );
			int minutesToMaxVol = Integer.parseInt(  Utils.getStringPreference(context , SettingsActivity.REM_MIN_TO_MAX_VOL_PREF, "30") );
			int minutesOfInterval = Integer.parseInt(  Utils.getStringPreference(context , SettingsActivity.REM_MIN_TO_MAX_VOL_PREF, "90") );
			
			maxBrightness = maxBrightness / 100; 
			maxVolume = maxVolume / 100;
			
			// get the start time
			long startUnixTime;
			if ( sing().remAlarmCalendar != null)
			{
				startUnixTime = sing().remAlarmCalendar.getTimeInMillis() / 1000;
			}else{
				startUnixTime = Utils.getCurrentUnixTimeStamp();
			}
			// get the end time
			double endUnixTime;
			double hoursOfAlarmPlayWhenNoEndTimeSet = 40;
			if  ( sing().finalAlarmCalendar != null && sing().finalAlarmCalendar.getTimeInMillis() > 0 && ( sing().finalAlarmCalendar.getTimeInMillis() / 1000 > startUnixTime) )
        		{
				endUnixTime = sing().finalAlarmCalendar.getTimeInMillis() / 1000;
        		}else{
        			endUnixTime = startUnixTime + ( minutesToMaxVol * 60 ) + ( hoursOfAlarmPlayWhenNoEndTimeSet * 60 * 60 );	
        		}
        	
			// split based on shape
        		String shape = Utils.getStringPreference( context , SettingsActivity.REM_SHAPE_PREF, Singleton.SHAPE_INCREASING ); // on a fresh install this default value will be read here
        		if ( shape.trim().equals("increasing"))
        		{
        			return generateIncreasingRemAlarmCues(startUnixTime, endUnixTime, minutesToMaxVol, maxVolume, maxBrightness, context);
        		}
        		else if ( shape.trim().equals( Singleton.SHAPE_OSCILLATING ))
        		{
        			return generateOscilatingRemAlarmCues(startUnixTime, endUnixTime, minutesToMaxVol, maxVolume, maxBrightness);
        		}
        		else	// it is intervals of minutes
        		{
        			return generateIntervalRemAlarmCues(startUnixTime, endUnixTime, minutesToMaxVol, maxVolume, maxBrightness, minutesOfInterval);
        		}
        }
		
		//-----------------------------
		// INCREASING 
		//-----------------------------
        public static ArrayList<AlarmsPlayedDataObject> generateIncreasingRemAlarmCues( long startUnixTime, double endUnixTime, int minutesToMaxVol, float maxVolume, float maxBrightness,Context context) {
        		ArrayList<AlarmsPlayedDataObject> graphToDrawArrayList = new ArrayList<AlarmsPlayedDataObject>();
        	
        		double minutesToPlayAlarm;
        		minutesToPlayAlarm = (( endUnixTime - startUnixTime) / 60 );	
        		Utils.boogieLog("Utils", "generateIncreasingRemAlarmCues", "endUnixTime = " + endUnixTime + " startUnixTime = " + startUnixTime );
        		int i = 0;
        		float amountToIncreaseVolumeEachMinute = maxVolume / minutesToMaxVol;
        		float amountToIncreaseBrightnessEachMinute = maxBrightness / minutesToMaxVol;
        	
        		Boolean soundOn = Utils.getBooleanPreference(context, SettingsActivity.REM_SOUND_ON_PREF, true);
        		Boolean lightOn = Utils.getBooleanPreference(context, SettingsActivity.REM_LIGHT_ON_PREF, true);        		        		
        		for ( i = 0 ; i < minutesToPlayAlarm; i++)
        		{
        			AlarmsPlayedDataObject apdo = new AlarmsPlayedDataObject();
        			apdo.unixTime = startUnixTime + ( i * 60 );
        			if ( i < minutesToMaxVol ) {
        				apdo.volume = Float.toString(i * amountToIncreaseVolumeEachMinute);
        				apdo.brightness = Float.toString(i * amountToIncreaseBrightnessEachMinute);
        			}else{
        				apdo.volume = Float.toString( maxVolume );
        				apdo.brightness = Float.toString( maxBrightness );
        			}
        			
        			if ( ! soundOn ) apdo.volume = Float.toString(0f);
        			if ( ! lightOn ) apdo.brightness = Float.toString(0f);
        			graphToDrawArrayList.add(apdo);
        		}
        	
        				Utils.boogieLog("Utils", "generateIncreasingRemAlarmCues", "shape = increasing");
        				
        		if ( graphToDrawArrayList.size() < 1 ) 
        			{
        			Toast.makeText(context, "no alarm cues generated", Toast.LENGTH_LONG).show();
        			}
        		return graphToDrawArrayList;
        }
        
        //-----------------------------
        // OSCILATING
        //-----------------------------
        public static ArrayList<AlarmsPlayedDataObject> generateOscilatingRemAlarmCues( long startUnixTime, double endUnixTime, int minutesToMaxVol, float maxVolume, float maxBrightness) {
        	
        		ArrayList<AlarmsPlayedDataObject> graphToDrawArrayList = new ArrayList<AlarmsPlayedDataObject>();
        	
        		double minutesToPlayAlarm;
        		minutesToPlayAlarm = (( endUnixTime - startUnixTime) / 60 );	
        		int  i = 0;
        		float threeQuartersMaxVol = ( 3 * maxVolume ) / 4;
        		float threeQuartersMaxBrightness = ( 3 * maxBrightness ) / 4;
        	
        		// first just go linearly to threeQuartersMaxVol
        		int threeQuarterMinsToMaxVol = ( 3 * minutesToMaxVol ) / 4;
        		float amountToIncreaseVolEachMinute = threeQuartersMaxVol / threeQuarterMinsToMaxVol;
        		float amountToIncreaseBrightnessEachMinute = threeQuartersMaxBrightness / threeQuarterMinsToMaxVol;
        	
        		for ( i = 0; i < threeQuarterMinsToMaxVol; i++)
        		{
        			AlarmsPlayedDataObject apdo = new AlarmsPlayedDataObject();
        			apdo.unixTime = startUnixTime + ( i * 60 );
        			apdo.volume = Float.toString(amountToIncreaseVolEachMinute * i);
        			apdo.brightness = Float.toString(amountToIncreaseBrightnessEachMinute * i);
        			graphToDrawArrayList.add(apdo);
        		}
        	
        		float newVol;
        		float newBright;
        		// then start the sinWave
        		double frequency =  ( Math.PI * 2 ) / ( minutesToMaxVol / 2 );
        		float quarterMaxVol = maxVolume / 4;
        		float quarterMaxBright = maxBrightness / 4;
        	
        		for ( i = threeQuarterMinsToMaxVol; i < minutesToPlayAlarm; i ++)
        		{
        			AlarmsPlayedDataObject apdo = new AlarmsPlayedDataObject();
        			apdo.unixTime = startUnixTime + ( i * 60 );
        			newVol =  (float) (( -1 * Math.sin( frequency * i )) * quarterMaxVol );
        			newVol += threeQuartersMaxVol;
        			newBright = (float) (( -1 * Math.sin( frequency * i )) * quarterMaxBright );
        			newBright += threeQuartersMaxBrightness;
        			apdo.volume = Float.toString( newVol );
        			apdo.brightness = Float.toString( newBright );
        			graphToDrawArrayList.add(apdo);
        		}

        				Utils.boogieLog("Utils", "generateOscilatingRemAlarmCues", "shape = oscillating");
        				if ( graphToDrawArrayList.size() < 1 ) 
            			{
            			//Toast.makeText(context, "no alarm cues generated", Toast.LENGTH_LONG);
        					throw new Error("no alarm cues genereated");
            			}
        		return graphToDrawArrayList;
        }
        
        //-----------------------------
        // INTERVALS 
        //-----------------------------
        public static ArrayList<AlarmsPlayedDataObject> generateIntervalRemAlarmCues( long startUnixTime, double endUnixTime, int minutesToMaxVol, float maxVolume, float maxBrightness, int minutesOfInterval) {
        		ArrayList<AlarmsPlayedDataObject> graphToDrawArrayList = new ArrayList<AlarmsPlayedDataObject>();
        	
        		// we need to fire one half sine wave at the interval time each interval time
        		// we play the cues for one third of the interval time !
        	
        		int minutesOfCueDuration = minutesOfInterval / 3;
        		int halfMinutesOfCueDuration = minutesOfCueDuration / 2;
        		long currentTime;
        		long endZerosUnixTime;
        		int i = 0;
        	
        		currentTime = startUnixTime;
        		endZerosUnixTime = currentTime + ( ( minutesOfInterval - halfMinutesOfCueDuration) * 60 );
        	
        		for ( i = 0; i < 1000; i ++)
        		{
        			ArrayList<AlarmsPlayedDataObject> phaseList = generateAPhaseOfZerosAndHalfSineWave( currentTime, endZerosUnixTime, minutesOfCueDuration, maxVolume, maxBrightness);
        			graphToDrawArrayList = addArrayListsOfAPDOsTogether(graphToDrawArrayList, phaseList);
        		
        			currentTime = endZerosUnixTime + ( minutesOfCueDuration * 60);
        			endZerosUnixTime = currentTime + ( ( minutesOfInterval - halfMinutesOfCueDuration) * 60 );
        		
        			if ( currentTime < endUnixTime )
        			{
        				// repeat another loop ( do nothing )
        			}
        			else if ( currentTime == endUnixTime )
        			{
        				// our work here is done
        				return graphToDrawArrayList;
        			}
        			else
        			{
        				// we have gone too far - trim down the APDO's as far as endUnixTime
        				return trimmedAPDOs(graphToDrawArrayList, endUnixTime);
        			}
        		}
        				boogieLog("Utils", "generateIntervalRemAlarmCues", "shape = intervals");
        				if ( graphToDrawArrayList.size() < 1 ) 
            			{
            			//Toast.makeText(context, "no alarm cues generated", Toast.LENGTH_LONG);
        					throw new Error("no alarm cues genereated");
            			}
        		return graphToDrawArrayList;
        }
        
        private static ArrayList<AlarmsPlayedDataObject> generateAPhaseOfZerosAndHalfSineWave( long startUnixTime, long endZerosUnixTime, int durationOfSineWave , float maxVol, float maxBright ) {

        		ArrayList<AlarmsPlayedDataObject> zerosArrayList = generateZerosAPDOs( startUnixTime,  endZerosUnixTime );
        		ArrayList<AlarmsPlayedDataObject> halfSineArrayList = generateStandAloneHalfSineWave( endZerosUnixTime, durationOfSineWave , maxVol, maxBright );
        		zerosArrayList = addArrayListsOfAPDOsTogether(zerosArrayList, halfSineArrayList);
        	
        				boogieLog("Utils", "generateAPhaseOfZerosAndHalfSineWave", "start - " + startUnixTime + " end - " + endZerosUnixTime + " duration - " + durationOfSineWave);
        		return zerosArrayList;
        }
        
        private static ArrayList<AlarmsPlayedDataObject> generateZerosAPDOs( long startUnixTime, long endZerosUnixTime ) {
        		ArrayList<AlarmsPlayedDataObject> graphToDrawArrayList = new ArrayList<AlarmsPlayedDataObject>();
        		int i = 0;
        		long minutesToPlayAlarm;
        		minutesToPlayAlarm = (( endZerosUnixTime - startUnixTime) / 60 );	
        		for ( i = 0; i < minutesToPlayAlarm; i ++)
        		{
        			AlarmsPlayedDataObject apdo = new AlarmsPlayedDataObject();
        			apdo.unixTime = startUnixTime + ( i * 60 );
        			apdo.volume = "0";
        			apdo.brightness = "0";
        			graphToDrawArrayList.add(apdo);
        		}
        				boogieLog("Utils", "generateZerosAPDOs", "start - " + startUnixTime + " end - " + endZerosUnixTime);
        		return graphToDrawArrayList;
        }
        
        private static ArrayList<AlarmsPlayedDataObject> generateStandAloneHalfSineWave( long startUnixTime, int durationOfSineWave , float maxVol, float maxBright ) {
        		ArrayList<AlarmsPlayedDataObject> graphToDrawArrayList = new ArrayList<AlarmsPlayedDataObject>();
        	
        		float newVol;
        		float newBright;
        		double frequency =   Math.PI / durationOfSineWave;
        		int i = 0;
        		for ( i = 0; i < durationOfSineWave; i ++)
        		{
        			AlarmsPlayedDataObject apdo = new AlarmsPlayedDataObject();
        			apdo.unixTime = startUnixTime + ( i * 60 );
        			newVol =  (float) ( Math.sin( frequency * i ) * maxVol );
        			newBright = (float) ( Math.sin( frequency * i ) * maxBright );
        			apdo.volume = Float.toString( newVol );
        			apdo.brightness = Float.toString( newBright );
        			graphToDrawArrayList.add(apdo);
        		}
        				boogieLog("Utils", "generateStandAloneHalfSineWave", "start - " + startUnixTime + " duration - " + durationOfSineWave + " maxVol - " + maxVol);
        		return graphToDrawArrayList;
        }
        
        private static ArrayList<AlarmsPlayedDataObject> trimmedAPDOs( ArrayList<AlarmsPlayedDataObject> graphToDrawArrayList, double endUnixTime){
        		ArrayList<AlarmsPlayedDataObject> trimmedAPDOsList = new ArrayList<AlarmsPlayedDataObject>();
        		for ( int i = 0 ; i <  graphToDrawArrayList.size(); i++)
        		{
        			AlarmsPlayedDataObject apdo = graphToDrawArrayList.get(i);
        			if ( apdo.unixTime < endUnixTime)
        			{
        				trimmedAPDOsList.add(apdo);
        			}else{
        						boogieLog("Utils", "trimmedAPDOs", "endUnixTime - " + endUnixTime);
        						return trimmedAPDOsList;
        			}
        		}
        		return null;
        }
        
        public static ArrayList<AlarmsPlayedDataObject> addArrayListsOfAPDOsTogether( ArrayList<AlarmsPlayedDataObject> listA , ArrayList<AlarmsPlayedDataObject> listB ) {
        	for ( int i = 0 ; i < listB.size(); i++)
        	{
        		listA.add( listB.get(i));
        	}
        	return listA;
        }
		
		//====================================================================================
		// POPULATE REM SOUNDS LIST PREFERENCES
		//====================================================================================
		
        // generic / original
		public static void populateRemSoundListPreferenceOptions( ListPreference lp, Context context){
			
			ArrayList<String> optionsEntriesArrayList = new ArrayList<String>();
			ArrayList<String> optionsEntryValuesArrayList = new ArrayList<String>();
			
			// CREATE NEW OPTION
			String createNew = SettingsActivity.RECORD_NEW_REM_SOUND_OPTION;
			optionsEntriesArrayList.add(createNew);
			optionsEntryValuesArrayList.add(createNew);
			
			// SELECT FROM DEVICE
			String selectFromDevice = SettingsActivity.SELECT_SONG_FROM_DEVICE_OPTION;
			optionsEntriesArrayList.add(selectFromDevice);
			optionsEntryValuesArrayList.add(selectFromDevice);
			
			// SOUNDS FROM RAW
			Field[] fields = R.raw.class.getFields();
			for ( Field field : fields )
			{
				optionsEntriesArrayList.add(field.getName());
				optionsEntryValuesArrayList.add(field.getName());
			}
			
			// CREATED SOUNDS
			File dir = new File(AudioActivity.getBasePathToCustomAudioRecordings()+AudioActivity.SOUNDS_DIRECTORY_NAME);
			File[] files = dir.listFiles(); // TODO filter only files of type ".3gp"
			
			if ( files != null)
			{
				if ( files.length > 0)
				{
					for ( File file : files )
					{
						String fileName = file.getName();
						int index = fileName.indexOf(".");
						String trimmedFileName = fileName.substring(0,index);				
						optionsEntriesArrayList.add(trimmedFileName);
						optionsEntryValuesArrayList.add(fileName);
					}
				}
			}
			
			// SONG SELECTED SAVED IN PREFERENCE
			boolean mustSetListPreferenceSelectedValueToSongSelectedFromDevice = false;
			String songPath = getStringPreference(context, SettingsActivity.REM_SOUND_PATH_PREF, "");
			if ( songPath != null && songPath != "" && songPath.length() > 0 )
			{
				// if there is a song path populated then we know the current song has been selected from the device
				String songName = getStringPreference(context, SettingsActivity.REM_SOUND_PREF, "tibetanbell");
				optionsEntriesArrayList.add(songName);
				optionsEntryValuesArrayList.add(songPath);
				// and we also need to set the entry value to this selected song then too !!
				mustSetListPreferenceSelectedValueToSongSelectedFromDevice = true;
			}
			
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
			// set them on the list preference
			lp.setEntries(optionsEntriesArray);
			lp.setEntryValues(optionsEntryValuesArray);
			
			if ( mustSetListPreferenceSelectedValueToSongSelectedFromDevice )
			{
				lp.setValueIndex(optionsEntriesArrayList.size() - 1);
			}
						boogieLog("Utils", "populateRemSoundListPreferenceOptions()", "option entry values length : " + optionsEntriesArray.length);
		}	
	
		public static void addCreateNewAndSelectFromDeviceCategories( ArrayList<String> optionsEntriesArrayList, ArrayList<String> optionsEntryValuesArrayList ) {
			
			String createNew = SettingsActivity.RECORD_NEW_REM_SOUND_OPTION;
			optionsEntriesArrayList.add(createNew);
			optionsEntryValuesArrayList.add(createNew);			
						
			String selectFromDevice = SettingsActivity.SELECT_SONG_FROM_DEVICE_OPTION;
			optionsEntriesArrayList.add(selectFromDevice);
			optionsEntryValuesArrayList.add(selectFromDevice);			
		}
		
		public static void addNatureSoundsList( ArrayList<String> optionsEntriesArrayList, ArrayList<String> optionsEntryValuesArrayList ) {
			Field[] fields = R.raw.class.getFields();
			for ( Field field : fields )
			{
				String soundName = field.getName().toLowerCase();
				int matchIso = soundName.indexOf("isochronic");
				int matchBi = soundName.indexOf("binaural");
				if ( matchIso < 0 && matchBi < 0 )
				{
					optionsEntriesArrayList.add(soundName);
					optionsEntryValuesArrayList.add(soundName);
				}
			}				
		}
		
		public static void addNatureSoundsCategory( ArrayList<String> optionsEntriesArrayList, ArrayList<String> optionsEntryValuesArrayList ) {
			String createNew = SettingsActivity.NATURE_SOUNDS_OPTION;
			optionsEntriesArrayList.add(createNew);
			optionsEntryValuesArrayList.add(createNew);				
		}
		
		public static void addIsochronicBinauralSoundsList( ArrayList<String> optionsEntriesArrayList, ArrayList<String> optionsEntryValuesArrayList ) {
			Field[] fields = R.raw.class.getFields();
			for ( Field field : fields )
			{
				String soundName = field.getName().toLowerCase();
				int match = soundName.indexOf("isochronic");
				if ( match >= 0 )
				{
					optionsEntriesArrayList.add(soundName);
					optionsEntryValuesArrayList.add(soundName);
				}
				match = soundName.indexOf("binaural");
				if ( match >= 0 )
				{
					optionsEntriesArrayList.add(soundName);
					optionsEntryValuesArrayList.add(soundName);
				}
			}			
		}
		
		public static void addIsochronicSoundsCategory( ArrayList<String> optionsEntriesArrayList, ArrayList<String> optionsEntryValuesArrayList ) {
			String createNew = SettingsActivity.ISOCHRONIC_TONES_OPTION;
			optionsEntriesArrayList.add(createNew);
			optionsEntryValuesArrayList.add(createNew);				
		}
		
//		public static void addBinauralSoundsList( ArrayList<String> optionsEntriesArrayList, ArrayList<String> optionsEntryValuesArrayList ) {
//			Field[] fields = R.raw.class.getFields();
//			for ( Field field : fields )
//			{
//				String soundName = field.getName().toLowerCase();
//				int match = soundName.indexOf("binaural");
//				if ( match >= 0 )
//				{
//					optionsEntriesArrayList.add(soundName);
//					optionsEntryValuesArrayList.add(soundName);
//				}
//			}						
//		}
//		
//		public static void addBinauralSoundsCategory( ArrayList<String> optionsEntriesArrayList, ArrayList<String> optionsEntryValuesArrayList ) {
//			String createNew = SettingsActivity.BINAURAL_BEATS_OPTION;
//			optionsEntriesArrayList.add(createNew);
//			optionsEntryValuesArrayList.add(createNew);				
//		}
		
		public static void addPreRecordedSoundsList( ArrayList<String> optionsEntriesArrayList, ArrayList<String> optionsEntryValuesArrayList ) {
			File dir = new File(AudioActivity.getBasePathToCustomAudioRecordings()+AudioActivity.SOUNDS_DIRECTORY_NAME);
			File[] files = dir.listFiles(); // TODO filter only files of type ".3gp"
			
			if ( files != null)
			{
				if ( files.length > 0)
				{
					for ( File file : files )
					{
						String fileName = file.getName();
						int index = fileName.indexOf(".");
						String trimmedFileName = fileName.substring(0,index);				
						optionsEntriesArrayList.add(trimmedFileName);
						optionsEntryValuesArrayList.add(fileName);
					}
				}
			}
		}
		
		public static void addPreRecordedSoundsCategory( ArrayList<String> optionsEntriesArrayList, ArrayList<String> optionsEntryValuesArrayList ) {
			String createNew = SettingsActivity.PRE_RECORDED_OPTION;
			optionsEntriesArrayList.add(createNew);
			optionsEntryValuesArrayList.add(createNew);				
		}
		
		public static void populateRemSoundsListWithCategories( ListPreference lp, Context context , String selectedCategory ){
			
			ArrayList<String> optionsEntriesArrayList = new ArrayList<String>();
			ArrayList<String> optionsEntryValuesArrayList = new ArrayList<String>();
			
			addCreateNewAndSelectFromDeviceCategories(optionsEntriesArrayList, optionsEntryValuesArrayList);
			
			//if ( selectedCategory != null && selectedCategory.equals(SettingsActivity.NATURE_SOUNDS_OPTION))
			//{
				addNatureSoundsList(optionsEntriesArrayList, optionsEntryValuesArrayList);
			//}else{
			//	addNatureSoundsCategory(optionsEntriesArrayList, optionsEntryValuesArrayList);
			//}
			
			//if ( selectedCategory != null && selectedCategory.equals(SettingsActivity.ISOCHRONIC_TONES_OPTION))
			//{
				addIsochronicBinauralSoundsList(optionsEntriesArrayList, optionsEntryValuesArrayList);
			//}else{
		//			(optionsEntriesArrayList, optionsEntryValuesArrayList);
		//	}
			
		//	if ( selectedCategory != null && selectedCategory.equals(SettingsActivity.BINAURAL_BEATS_OPTION))
		//	{
				//addBinauralSoundsList(optionsEntriesArrayList, optionsEntryValuesArrayList);
		//	}else{
		//		addBinauralSoundsCategory(optionsEntriesArrayList, optionsEntryValuesArrayList);
		//	}
			
		//	if ( selectedCategory != null && selectedCategory.equals(SettingsActivity.PRE_RECORDED_OPTION))
		//	{
				addPreRecordedSoundsList(optionsEntriesArrayList, optionsEntryValuesArrayList);
		//	}else{
		//		addPreRecordedSoundsCategory(optionsEntriesArrayList, optionsEntryValuesArrayList);
		//	}
			
			// SONG SELECTED SAVED IN PREFERENCE
			boolean mustSetListPreferenceSelectedValueToSongSelectedFromDevice = false;
			String songPath = getStringPreference(context, SettingsActivity.REM_SOUND_PATH_PREF, "");
			if ( songPath != null && songPath != "" && songPath.length() > 0 )
			{
				// if there is a song path populated then we know the current song has been selected from the device
				String songName = getStringPreference(context, SettingsActivity.REM_SOUND_PREF, "tibetanbell");
				optionsEntriesArrayList.add(songName);
				optionsEntryValuesArrayList.add(songPath);
				// and we also need to set the entry value to this selected song then too !!
				mustSetListPreferenceSelectedValueToSongSelectedFromDevice = true;
			}
			
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
			// set them on the list preference
			lp.setEntries(optionsEntriesArray);
			lp.setEntryValues(optionsEntryValuesArray);
			
			if ( mustSetListPreferenceSelectedValueToSongSelectedFromDevice )
			{
				lp.setValueIndex(optionsEntriesArrayList.size() - 1);
			}
						boogieLog("Utils", "populateRemSoundListPreferenceOptions()", "option entry values length : " + optionsEntriesArray.length);
		}	

		
		//==========================================================================================================
		// DELETE PRE-RECORDED SOUNDS
		//==========================================================================================================
		
		public static void deletePreRecordedSound( String soundName ) {
			
			// its a custom pre recorded sound
			File dir = new File(AudioActivity.getBasePathToCustomAudioRecordings()+AudioActivity.SOUNDS_DIRECTORY_NAME);
			File[] files = dir.listFiles(); // TODO filter only files of type ".3gp"
			for ( File file : files )
			{
				String fileName = file.getName();
				if ( soundName.equals(fileName) )
				{
					Utils.boogieLog("Utils", "deletePreRecordedSound", "filename = " + fileName );
					try{
						boolean deleted = file.delete();
						Utils.boogieLog("Utils", "deletePreRecordedSound()", fileName + " deleted successfully");
					}catch(Exception e)
					{
						// TODO Toast.makeText( this ,"ERROR : Unable to locate recorded sound : " + fileName,Toast.LENGTH_LONG).show();
						Utils.boogieLog("Utils", "deletePreRecordedSound", " ERROR : deleting soundName : " + soundName );
					}
					break;
				}
			}
		}
		
		//==========================================================================================================
		// SEEK BAR UTILITY METHODS
		//==========================================================================================================
		
		public static String milliSecondsToTimer(long milliseconds){
	        String finalTimerString = "";
	        String secondsString = "";
	 
	        // Convert total duration into time
	           int hours = (int)( milliseconds / (1000*60*60));
	           int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
	           int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
	           // Add hours if there
	           if(hours > 0){
	               finalTimerString = hours + ":";
	           }
	 
	           // Prepending 0 to seconds if it is one digit
	           if(seconds < 10){
	               secondsString = "0" + seconds;
	           }else{
	               secondsString = "" + seconds;}
	 
	           finalTimerString = finalTimerString + minutes + ":" + secondsString;
	 
	        // return timer string
	        return finalTimerString;
	    }
	 
	    /**
	     * Function to get Progress percentage
	     * @param currentDuration
	     * @param totalDuration
	     * */
	    public static int getProgressPercentage(long currentDuration, long totalDuration){
	        Double percentage = (double) 0;
	 
	        long currentSeconds = (int) (currentDuration / 1000);
	        long totalSeconds = (int) (totalDuration / 1000);
	 
	        // calculating percentage
	        percentage =(((double)currentSeconds)/totalSeconds)*100;
	 
	        // return percentage
	        return percentage.intValue();
	    }
	 
	    /**
	     * Function to change progress to timer
	     * @param progress -
	     * @param totalDuration
	     * returns current duration in milliseconds
	     * */
	    public static int progressToTimer(int progress, int totalDuration) {
	        int currentDuration = 0;
	        totalDuration = (int) (totalDuration / 1000);
	        currentDuration = (int) ((((double)progress) / 100) * totalDuration);
	 
	        // return current duration in milliseconds
	        return currentDuration * 1000;
	    }
	  //==========================================================================================================
	  // RECORD ALARMS PLAYED DATA
	  //==========================================================================================================
	    public static void recordAlarmsPlayedData( Context context ) {
	    		String volumeString = Float.toString(sing().getRemVolume());
			String brightnessString = Float.toString( sing().remBrightness); 
			ContentValues contentValues = new ContentValues();
			contentValues.put( GraphsTable.COLUMN_NAME_DIARY_ID, sing().newDiaryEntryRowIdInsertedOnStartOfNewAlarm );
			contentValues.put( GraphsTable.COLUMN_NAME_UNIX_TIME, Utils.getCurrentUnixTimeStamp() );
			contentValues.put( GraphsTable.COLUMN_NAME_VOLUME, volumeString );
			contentValues.put( GraphsTable.COLUMN_NAME_BRIGHTNESS, brightnessString );
			Uri uri = Uri.parse( ContentProviderLA.CONTENT_PROVIDER_URI + "/" + "graph" + "/" + "data" ) ;
			context.getContentResolver().insert(uri, contentValues);
	    }
		//==========================================================================================================
		// UNIQUE GETTERS
		//==========================================================================================================
		
		public static String getPathAudio(Uri uriAudio , Activity activity ) {    
	        //String selectedImagePath;
	        //1:MEDIA GALLERY --- query from MediaStore.Images.Media.DATA
			String selectedAudioPath="" ;
	        String[] projection = { MediaStore.Audio.Media.DATA };

	        Cursor cursor = activity.managedQuery(uriAudio, projection, null, null, null);
	        if(cursor != null){
	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
	            cursor.moveToFirst();
	            selectedAudioPath = cursor.getString(column_index);

	        }else{
	            selectedAudioPath = null;   
	        }

	        if(selectedAudioPath == null){
	            //2:OI FILE Manager --- call method: uri.getPath()
	            selectedAudioPath = uriAudio.getPath();
	        }

	        return selectedAudioPath;
	    }
		
		public static String getAudioTitle(Uri uriAudio, Activity activity) {    
	        //String selectedImagePath;
	        //1:MEDIA GALLERY --- query from MediaStore.Images.Media.DATA
			String selectedAudioTitle="" ;
	        String[] projection = { MediaStore.Audio.Media.TITLE };

	        Cursor cursor = activity.managedQuery(uriAudio, projection, null, null, null);
	        if(cursor != null){
	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
	            cursor.moveToFirst();
	            selectedAudioTitle = cursor.getString(column_index);

	        }else{
	        	selectedAudioTitle = null;   
	        }

	        if(selectedAudioTitle == null){
	            //2:OI FILE Manager --- call method: uri.getPath()
	            selectedAudioTitle = uriAudio.getPath();
	        }

	        return selectedAudioTitle;
	    }
		
		//==========================================================================================================
		
		public static boolean songNameIsInRawFolder(Context context, String songName){
			int resID= context.getResources().getIdentifier(songName, "raw", context.getPackageName());
	        if (resID > 0) // it's a raw sound file
			{
		    		return true;
			}else{
				return false;
			}
		}
		
		public static boolean songNameIsInCreatedSoundsFolder(Context context, String songName){
			File dir = new File(AudioActivity.getBasePathToCustomAudioRecordings()+AudioActivity.SOUNDS_DIRECTORY_NAME);
			File[] files = dir.listFiles(); // TODO filter only files of type ".3gp"
			for ( File file : files )
			{
				String fileName = file.getName();
				if ( songName.equals(fileName) )
				{
					return true;
				}
			}
			return false;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//==========================================================================================================
		
		/*	
		private DreamRecordDataObject takeListLineReturnDreamRecord(String tvText) {
			int i = tvText.indexOf(")");
			String num = tvText.substring(0,i);
			int index = Integer.parseInt(num);
			return sing().allDreamRecordsLoadedArrayList.get(index - 1);
		}*/
		
		/*
		private boolean stringMatchesUnixTime(String timeString, Long unixTime){
			if ( unixTime <= 0 ) return false;
			
			String onlyTimeString;
			if ( timeString.contains(" - "))
			{
				int i = timeString.indexOf(" - ");
				onlyTimeString = timeString.substring(0,i);
			}else{
				onlyTimeString = timeString;
			}
			if ( sing().dateFormat.format(unixTime * 1000L).equals(onlyTimeString) )
			{
				return true;
			}else{
				return false;
			}
		}*/
	
	
	
	
	
	
	
	
	//====================================================================================
  	//	Getters and Setters
  	//====================================================================================
    
    public static Singleton sing(){
		return Singleton.getInstance();
	}

}
