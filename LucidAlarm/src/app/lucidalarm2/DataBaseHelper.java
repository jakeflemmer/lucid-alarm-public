package app.lucidalarm2;

import java.util.Iterator;
import java.util.Set;

import app.lucidalarm2.ContractSchema.CuesTable;
import app.lucidalarm2.ContractSchema.DiaryTable;
import app.lucidalarm2.ContractSchema.GraphsTable;
import app.lucidalarm2.ContractSchema.ResetsClickedTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final String CREATE_TABLE = " create table ";
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String OPEN_BRACKET = " ( ";
	private static final String CLOSE_BRACKET = " ) ";
	private static final String SINGLE_QUOTE = " ' ";
	
	private static final String SQL_CREATE_DIARY_TABLE =
	    CREATE_TABLE + DiaryTable.TABLE_NAME + OPEN_BRACKET +
	    DiaryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +  
	    DiaryTable.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
	    DiaryTable.COLUMN_NAME_NOTES + TEXT_TYPE + COMMA_SEP +
	    DiaryTable.COLUMN_NAME_OTHER + TEXT_TYPE + COMMA_SEP +
	    DiaryTable.COLUMN_NAME_LUCID + TEXT_TYPE + COMMA_SEP +
	    DiaryTable.COLUMN_NAME_COLOR + TEXT_TYPE + COMMA_SEP +
	    DiaryTable.COLUMN_NAME_UNIX_TIME + TEXT_TYPE + COMMA_SEP +
	    DiaryTable.COLUMN_NAME_MISC_1 + TEXT_TYPE + COMMA_SEP +
	    DiaryTable.COLUMN_NAME_MISC_2 + TEXT_TYPE + COMMA_SEP +
	    DiaryTable.COLUMN_NAME_MISC_3 + TEXT_TYPE + COMMA_SEP +
	    DiaryTable.COLUMN_NAME_MISC_NAMES + TEXT_TYPE + COMMA_SEP +
	    DiaryTable.COLUMN_NAME_SOUND + TEXT_TYPE + COMMA_SEP +
	    DiaryTable.COLUMN_NAME_LIGHT_COLOR + TEXT_TYPE + COMMA_SEP +
	    DiaryTable.COLUMN_NAME_LIGHT_FREQUENCY + TEXT_TYPE + COMMA_SEP + 
	    " UNIQUE " + OPEN_BRACKET + DiaryTable.COLUMN_NAME_UNIX_TIME + CLOSE_BRACKET + 
	    " ON CONFLICT REPLACE " + 
	    CLOSE_BRACKET;
	
	private static final String SQL_CREATE_GRAPHS_TABLE =
		    CREATE_TABLE + GraphsTable.TABLE_NAME + OPEN_BRACKET +
		    GraphsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
		    GraphsTable.COLUMN_NAME_DIARY_ID + TEXT_TYPE + COMMA_SEP +
		    GraphsTable.COLUMN_NAME_UNIX_TIME + TEXT_TYPE + COMMA_SEP +
		    GraphsTable.COLUMN_NAME_VOLUME + TEXT_TYPE + COMMA_SEP +
		    GraphsTable.COLUMN_NAME_BRIGHTNESS + TEXT_TYPE + 
		    CLOSE_BRACKET;
	
	private String createSqlForCreatingCuesTable (){
		String sql = CREATE_TABLE + CuesTable.TABLE_NAME + OPEN_BRACKET + 
				CuesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,";
		
		for ( int i = 1; i < SettingsActivity.remSettingPreferencesArray.length; i ++)
		{
			String colName = SettingsActivity.remSettingPreferencesArray[i];
			sql += colName + TEXT_TYPE;
			if ( i < SettingsActivity.remSettingPreferencesArray.length - 1 ) sql += COMMA_SEP;
		}
		sql += CLOSE_BRACKET;
		return sql;
		
		
	}
//	static final String SQL_CREATE_CUES_TABLE =
//		    CREATE_TABLE + CuesTable.TABLE_NAME + OPEN_BRACKET +
//		    CuesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
//		    CuesTable.COLUMN_NAME_CUE_NAME + TEXT_TYPE + COMMA_SEP +
//		    CuesTable.COLUMN_NAME_SOUND + TEXT_TYPE + COMMA_SEP +
//		    CuesTable.COLUMN_NAME_DELAY_BETWEEN_SOUNDS + TEXT_TYPE + COMMA_SEP +
//		    CuesTable.COLUMN_NAME_DELAY_AFTER_RESET + TEXT_TYPE + COMMA_SEP +
//		    CuesTable.COLUMN_NAME_SHAPE + TEXT_TYPE + COMMA_SEP +
//		    CuesTable.COLUMN_NAME_MIN_VOLUME + TEXT_TYPE + COMMA_SEP +
//		    CuesTable.COLUMN_NAME_MAX_VOLUME + TEXT_TYPE + COMMA_SEP +
//		    CuesTable.COLUMN_NAME_BRIGHTNESS + TEXT_TYPE + COMMA_SEP +
//		    CuesTable.COLUMN_NAME_FREQUENCY + TEXT_TYPE + COMMA_SEP +
//		    CuesTable.COLUMN_NAME_COLOR + TEXT_TYPE + 
//		    CLOSE_BRACKET;
	
	private static final String SQL_CREATE_RESETS_CLICKED_TABLE =
		    CREATE_TABLE + ResetsClickedTable.TABLE_NAME + OPEN_BRACKET +
		    ResetsClickedTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
		    ResetsClickedTable.COLUMN_NAME_DIARY_ID + TEXT_TYPE + COMMA_SEP +
		    ResetsClickedTable.COLUMN_NAME_UNIX_TIME + TEXT_TYPE + 
		    CLOSE_BRACKET;
	
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "LucidAlarmDB.db";
    
    
    private SQLiteDatabase mDB;
    
  //================================================================================================================
  //================================================================================================================
  //================================================================================================================
    
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = getWritableDatabase();	// calls onCreate method if db does not already exist
        
    Utils.boogieLog("DataBaseHelper", "constructor()", "generic message");
    
    }
    
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(SQL_CREATE_DIARY_TABLE);
		String createGraphsSql = SQL_CREATE_GRAPHS_TABLE;
		db.execSQL(SQL_CREATE_GRAPHS_TABLE);
		db.execSQL( createSqlForCreatingCuesTable() );
		db.execSQL(SQL_CREATE_RESETS_CLICKED_TABLE);
		
		//populateDummyData(db);
		
		populateDefaultCues(db);
		
			Utils.boogieLog("DataBaseHelper", "onCreate()", "generic message");

	}
	
//	private void populateDummyData(SQLiteDatabase db){
//
//		String sql = "";
//		insertDiaryEntry("original one title", "i was flying like an eagle", "gf woke me up", "true", "red", "12/29/2013", "", "", "", "", db);
//		insertDiaryEntry("original two title", "i was flying like a kite", "sparky woke me up", "true", "red", "12/30/2013", "", "", "", "", db);
//		insertDiaryEntry("original three title", "i was flying like a butterfly", "cat woke me up", "true", "red", "12/31/2013", "", "", "", "", db);
//	}
	
	private void populateDefaultCues( SQLiteDatabase db ) {
		
		/*
		 * 	public static final String REM_SOUND_ON_PREF = "remSoundOn_preference";
	public static final String REM_SOUND_PREF = "remSelectSoundOption_preference";
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
	public static final String REM_HIDDEN_CUE_NAME_PREF = "hiddenCueName_preference";	 
		 */
		
		String sql = "insert into " + CuesTable.TABLE_NAME + OPEN_BRACKET +
				SettingsActivity.REM_SOUND_ON_PREF + COMMA_SEP + 
				SettingsActivity.REM_SOUND_PREF + COMMA_SEP +
				SettingsActivity.REM_SOUND_PATH_PREF + COMMA_SEP +
				SettingsActivity.REM_SOUNDS_DELAY_PREF + COMMA_SEP +
				SettingsActivity.REM_SERIES_DELAY_PREF + COMMA_SEP +	// delay after reset
				SettingsActivity.REM_SHAPE_PREF + COMMA_SEP +
				SettingsActivity.REM_INTERVAL_PREF + COMMA_SEP +
				SettingsActivity.REM_MIN_TO_MAX_VOL_PREF + COMMA_SEP +
				SettingsActivity.REM_MIN_VOL_PREF + COMMA_SEP +
				SettingsActivity.REM_MAX_VOL_PREF + COMMA_SEP +
				SettingsActivity.REM_LIGHT_ON_PREF + COMMA_SEP +
				SettingsActivity.REM_MAX_BRIGHTNESS_PREF + COMMA_SEP +
				SettingsActivity.REM_FREQUENCY_PREF + COMMA_SEP +
				SettingsActivity.REM_COLOR_PREF + COMMA_SEP +
				SettingsActivity.REM_CUE_NAME_PREF + CLOSE_BRACKET +
				
				" values " + OPEN_BRACKET +
				
				SINGLE_QUOTE + "true" + SINGLE_QUOTE + COMMA_SEP +	// sound on
				SINGLE_QUOTE + "tibetan bell" + SINGLE_QUOTE + COMMA_SEP +	// sound
				SINGLE_QUOTE + "" + SINGLE_QUOTE + COMMA_SEP +	// sound path
				SINGLE_QUOTE + "0" + SINGLE_QUOTE + COMMA_SEP +	// delay between sounds
				SINGLE_QUOTE + "30" + SINGLE_QUOTE + COMMA_SEP +	// delay after resets
				SINGLE_QUOTE + "increasing" + SINGLE_QUOTE + COMMA_SEP +	// shape
				SINGLE_QUOTE + "90" + SINGLE_QUOTE + COMMA_SEP +	// interval
				SINGLE_QUOTE + "60" + SINGLE_QUOTE + COMMA_SEP +	// minutes to max vol
				SINGLE_QUOTE + "0" + SINGLE_QUOTE + COMMA_SEP +	// min volume
				SINGLE_QUOTE + "100" + SINGLE_QUOTE + COMMA_SEP +	// max volume
				SINGLE_QUOTE + "true" + SINGLE_QUOTE + COMMA_SEP +	// light on
				SINGLE_QUOTE + "100" + SINGLE_QUOTE + COMMA_SEP +	// brightness
				SINGLE_QUOTE + "4" + SINGLE_QUOTE + COMMA_SEP +	// frequency
				SINGLE_QUOTE + "white" + SINGLE_QUOTE + COMMA_SEP +  // color
				SINGLE_QUOTE + "default increasing cue" + SINGLE_QUOTE  // name
				+ CLOSE_BRACKET;
	
		db.execSQL(sql);
		
		sql = "insert into " + CuesTable.TABLE_NAME + OPEN_BRACKET +
				SettingsActivity.REM_SOUND_ON_PREF + COMMA_SEP + 
				SettingsActivity.REM_SOUND_PREF + COMMA_SEP +
				SettingsActivity.REM_SOUND_PATH_PREF + COMMA_SEP +
				SettingsActivity.REM_SOUNDS_DELAY_PREF + COMMA_SEP +
				SettingsActivity.REM_SERIES_DELAY_PREF + COMMA_SEP +	// delay after reset
				SettingsActivity.REM_SHAPE_PREF + COMMA_SEP +
				SettingsActivity.REM_INTERVAL_PREF + COMMA_SEP +
				SettingsActivity.REM_MIN_TO_MAX_VOL_PREF + COMMA_SEP +
				SettingsActivity.REM_MIN_VOL_PREF + COMMA_SEP +
				SettingsActivity.REM_MAX_VOL_PREF + COMMA_SEP +
				SettingsActivity.REM_LIGHT_ON_PREF + COMMA_SEP +
				SettingsActivity.REM_MAX_BRIGHTNESS_PREF + COMMA_SEP +
				SettingsActivity.REM_FREQUENCY_PREF + COMMA_SEP +
				SettingsActivity.REM_COLOR_PREF + COMMA_SEP +
				SettingsActivity.REM_CUE_NAME_PREF + CLOSE_BRACKET +
				
				" values " + OPEN_BRACKET +
				
				SINGLE_QUOTE + "true" + SINGLE_QUOTE + COMMA_SEP +	// sound on
				SINGLE_QUOTE + "tibetan bell" + SINGLE_QUOTE + COMMA_SEP +	// sound
				SINGLE_QUOTE + "" + SINGLE_QUOTE + COMMA_SEP +	// sound path
				SINGLE_QUOTE + "0" + SINGLE_QUOTE + COMMA_SEP +	// delay between sounds
				SINGLE_QUOTE + "30" + SINGLE_QUOTE + COMMA_SEP +	// delay after resets
				SINGLE_QUOTE + Singleton.SHAPE_OSCILLATING + SINGLE_QUOTE + COMMA_SEP +	// shape
				SINGLE_QUOTE + "90" + SINGLE_QUOTE + COMMA_SEP +	// interval
				SINGLE_QUOTE + "60" + SINGLE_QUOTE + COMMA_SEP +	// minutes to max vol
				SINGLE_QUOTE + "0" + SINGLE_QUOTE + COMMA_SEP +	// min volume
				SINGLE_QUOTE + "100" + SINGLE_QUOTE + COMMA_SEP +	// max volume
				SINGLE_QUOTE + "true" + SINGLE_QUOTE + COMMA_SEP +	// light on
				SINGLE_QUOTE + "100" + SINGLE_QUOTE + COMMA_SEP +	// brightness
				SINGLE_QUOTE + "4" + SINGLE_QUOTE + COMMA_SEP +	// frequency
				SINGLE_QUOTE + "red" + SINGLE_QUOTE + COMMA_SEP +  // color
				SINGLE_QUOTE + "default oscilating cue" + SINGLE_QUOTE  // name
				+ CLOSE_BRACKET;

		db.execSQL(sql);
		
		sql = "insert into " + CuesTable.TABLE_NAME + OPEN_BRACKET +
				SettingsActivity.REM_SOUND_ON_PREF + COMMA_SEP + 
				SettingsActivity.REM_SOUND_PREF + COMMA_SEP +
				SettingsActivity.REM_SOUND_PATH_PREF + COMMA_SEP +
				SettingsActivity.REM_SOUNDS_DELAY_PREF + COMMA_SEP +
				SettingsActivity.REM_SERIES_DELAY_PREF + COMMA_SEP +	// delay after reset
				SettingsActivity.REM_SHAPE_PREF + COMMA_SEP +
				SettingsActivity.REM_INTERVAL_PREF + COMMA_SEP +
				SettingsActivity.REM_MIN_TO_MAX_VOL_PREF + COMMA_SEP +
				SettingsActivity.REM_MIN_VOL_PREF + COMMA_SEP +
				SettingsActivity.REM_MAX_VOL_PREF + COMMA_SEP +
				SettingsActivity.REM_LIGHT_ON_PREF + COMMA_SEP +
				SettingsActivity.REM_MAX_BRIGHTNESS_PREF + COMMA_SEP +
				SettingsActivity.REM_FREQUENCY_PREF + COMMA_SEP +
				SettingsActivity.REM_COLOR_PREF + COMMA_SEP +
				SettingsActivity.REM_CUE_NAME_PREF + CLOSE_BRACKET +
				
				" values " + OPEN_BRACKET +
				
				SINGLE_QUOTE + "true" + SINGLE_QUOTE + COMMA_SEP +	// sound on
				SINGLE_QUOTE + "tibetan bell" + SINGLE_QUOTE + COMMA_SEP +	// sound
				SINGLE_QUOTE + "" + SINGLE_QUOTE + COMMA_SEP +	// sound path
				SINGLE_QUOTE + "0" + SINGLE_QUOTE + COMMA_SEP +	// delay between sounds
				SINGLE_QUOTE + "30" + SINGLE_QUOTE + COMMA_SEP +	// delay after resets
				SINGLE_QUOTE + "intervals" + SINGLE_QUOTE + COMMA_SEP +	// shape
				SINGLE_QUOTE + "90" + SINGLE_QUOTE + COMMA_SEP +	// interval
				SINGLE_QUOTE + "60" + SINGLE_QUOTE + COMMA_SEP +	// minutes to max vol
				SINGLE_QUOTE + "0" + SINGLE_QUOTE + COMMA_SEP +	// min volume
				SINGLE_QUOTE + "100" + SINGLE_QUOTE + COMMA_SEP +	// max volume
				SINGLE_QUOTE + "true" + SINGLE_QUOTE + COMMA_SEP +	// light on
				SINGLE_QUOTE + "100" + SINGLE_QUOTE + COMMA_SEP +	// brightness
				SINGLE_QUOTE + "4" + SINGLE_QUOTE + COMMA_SEP +	// frequency
				SINGLE_QUOTE + "red" + SINGLE_QUOTE + COMMA_SEP +  // color
				SINGLE_QUOTE + "default intervals cue" + SINGLE_QUOTE  // name
				+ CLOSE_BRACKET;

		db.execSQL(sql);
		
					Utils.boogieLog("DataBaseHelper", "populateDefaultCues()", "inserted three default cues into cuesTable");
				
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

		
		
	//=================================================================
	// INSERT METHODS
	//=================================================================
		
	Utils.boogieLog("DataBaseHelper", "onUpgrade()", "generic message");
	}
	
	public long insertDiaryEntry( ContentValues contentValues ) { 
		
		//long newRowId = mDB.insert(DiaryTable.TABLE_NAME, null, contentValues);
		
		String selection = DiaryTable.COLUMN_NAME_UNIX_TIME + "=?"; 
	    String[] selectionArgs = new String[] { contentValues.getAsString(DiaryTable.COLUMN_NAME_UNIX_TIME) };

	    //Do an update if the constraints match
	    mDB.update(DiaryTable.TABLE_NAME, contentValues, selection, null);
	    
	  //This will return the id of the newly inserted row if no conflict
	    //It will also return the offending row without modifying it if in conflict
	    long newRowId = mDB.insertWithOnConflict(DiaryTable.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE );   

				Utils.boogieLog("DataBaseHelper", "insertDiaryEntry()", "inserted new diary entry");
		return newRowId;
	}
	
//	public Uri insert(Uri uri, ContentValues values) {	    
//	    String selection = DiaryTable.COLUMN_NAME_UNIX_TIME + "=?"; 
//	    String[] selectionArgs = new String[] {
//	                contentValues.getAsString(DiaryTable.COLUMN_NAME_UNIX_TIME)};
//
//	    //Do an update if the constraints match
//	    db.update(DatabaseProperties.TABLE_NAME, values, selection, null);
//
//	    //This will return the id of the newly inserted row if no conflict
//	    //It will also return the offending row without modifying it if in conflict
//	    long id = db.insertWithOnConflict(DatabaseProperties.TABLE_NAME, null, values, CONFLICT_IGNORE);        
//
//	    return ContentUris.withAppendedId(uri, id);
//	}
//	public void insertDiaryEntry( String title, String notes, String other, String lucid, String color, String unixtime, String misc1, String misc2, String misc3, String miscNames, SQLiteDatabase db ){
//
//		if ( lucid == null )
//		{
//			lucid = "false";
//		}else{
//			lucid = ( lucid.equals("true") ? "true" : "false");
//		}
//		
//		String sql = "insert into " + DiaryTable.TABLE_NAME + OPEN_BRACKET +
//					DiaryTable.COLUMN_NAME_TITLE + COMMA_SEP +Switch and mobile home 
//					DiaryTable.COLUMN_NAME_NOTES + COMMA_SEP +
//					DiaryTable.COLUMN_NAME_OTHER + COMMA_SEP +
//					DiaryTable.COLUMN_NAME_LUCID + COMMA_SEP +
//					DiaryTable.COLUMN_NAME_COLOR + COMMA_SEP +
//					DiaryTable.COLUMN_NAME_UNIX_TIME + COMMA_SEP +
//					DiaryTable.COLUMN_NAME_MISC_1 + COMMA_SEP +
//					DiaryTable.COLUMN_NAME_MISC_2 + COMMA_SEP +
//					DiaryTable.COLUMN_NAME_MISC_3 + COMMA_SEP +
//					DiaryTable.COLUMN_NAME_MISC_NAMES + CLOSE_BRACKET +
//					" values " + OPEN_BRACKET +
//					SINGLE_QUOTE + title + SINGLE_QUOTE + COMMA_SEP +
//					SINGLE_QUOTE + notes + SINGLE_QUOTE + COMMA_SEP +
//					SINGLE_QUOTE + other + SINGLE_QUOTE + COMMA_SEP +
//					SINGLE_QUOTE + lucid + SINGLE_QUOTE + COMMA_SEP +
//					SINGLE_QUOTE + color + SINGLE_QUOTE + COMMA_SEP +
//					SINGLE_QUOTE + unixtime + SINGLE_QUOTE + COMMA_SEP +
//					SINGLE_QUOTE + misc1 + SINGLE_QUOTE + COMMA_SEP +
//					SINGLE_QUOTE + misc2 + SINGLE_QUOTE + COMMA_SEP +
//					SINGLE_QUOTE + misc3 + SINGLE_QUOTE + COMMA_SEP +
//					SINGLE_QUOTE + miscNames + SINGLE_QUOTE + CLOSE_BRACKET;
//		
//		db.execSQL(sql);
//					
//	Utils.boogieLog("DataBaseHelper", "insertDiaryEntry()", "inserted all ten columns and values of diary table");
//	
//	}
	
	public void insertGraphRow( ContentValues contentValues ){
		
		mDB.insert(GraphsTable.TABLE_NAME, null, contentValues);
				Utils.boogieLog("DataBaseHelper", "insertGraphRow()", "inserted graph row");
				Set<String> keys = contentValues.keySet();

			    for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
			    		String s = it.next();
			    		Utils.boogieLog("DataBaseHelper", "insertGraphRow", "contentValue key : " + s + " val : " + contentValues.get(s) );
			    }
					
	}
	
	
	public void insertResetsClicked( ContentValues contentValues ){
		mDB.insert( ResetsClickedTable.TABLE_NAME, null, contentValues );
				Utils.boogieLog("DataBaseHelper", "insertResetsClicked()", "inserted resets clicked row");
	}
	public void insertCue(){
		
		Utils.boogieLog("DataBaseHelper", "insertCue()", "generic message");
	}
	
	//=================================================================
	// UPDATE METHODS
	//=================================================================
	
	
	public void updateDiaryEntry( String rowId, ContentValues contentValues ) { 
		
		String stringFilter = "_id=" + rowId;
		mDB.update(DiaryTable.TABLE_NAME, contentValues, stringFilter, null );
		
				Utils.boogieLog("DataBaseHelper", "updateDiaryEntry()", "updated diary entry with rowId : " + rowId);
	} 
	
	//=================================================================
	// GET METHODS
	//=================================================================
	
	public Cursor getAllRecords(){
		
		String [] columns = { DiaryTable._ID, DiaryTable.COLUMN_NAME_UNIX_TIME, DiaryTable.COLUMN_NAME_TITLE, DiaryTable.COLUMN_NAME_COLOR };
		String orderBy = DiaryTable.COLUMN_NAME_UNIX_TIME + " desc";
		Cursor c = mDB.query(DiaryTable.TABLE_NAME, columns, null, null, null, null, orderBy);
		
				Utils.boogieLog("DataBaseHelper", "getAllRecords()", "returning all records");
		return c;
	}
	
	public Cursor getAllRecordsForExport(){
		
		String [] columns = null; 	// we want em all !
		String orderBy = DiaryTable.COLUMN_NAME_UNIX_TIME + " asc";
		Cursor c = mDB.query(DiaryTable.TABLE_NAME, columns, null, null, null, null, orderBy);
		
				Utils.boogieLog("DataBaseHelper", "getAllRecordsForExport()", "returning all rows all columns from diary table");
		return c;
	}
	
	public Cursor getRecordsWithDreamSign(String selection) {
		String [] columns = { DiaryTable._ID, DiaryTable.COLUMN_NAME_UNIX_TIME, DiaryTable.COLUMN_NAME_TITLE, DiaryTable.COLUMN_NAME_COLOR };
		String orderBy = DiaryTable.COLUMN_NAME_UNIX_TIME + " asc";
		Cursor c = mDB.query(DiaryTable.TABLE_NAME, columns, DiaryTable.COLUMN_NAME_NOTES + " like " + "'%" + selection +"%' OR " + DiaryTable.COLUMN_NAME_TITLE + " like " + "'%" + selection +"%'", null, null, null, orderBy);
		// TODO also if it is in the title or notes
				Utils.boogieLog("DataBaseHelper", "getRecordsWithDreamSign()", "dreamSign = " + selection + " results : " + c.getCount());
		return c;
		
//		mysql_query("
//				SELECT *
//				FROM `table`
//				WHERE `column` LIKE '%{$needle}%'
//				");
	}
	
	
	
	
	public Cursor getDiaryEntry(String rowId){
		
		String [] columns = null;	// get all columns { DiaryTable._ID, DiaryTable.COLUMN_NAME_UNIX_TIME, DiaryTable.COLUMN_NAME_TITLE , DiaryTable.COLUMN_NAME_NOTES };
		String selection = "_ID=?";
		String [] selectionArgs = { rowId };
		String groupBy = null;
		String having = null;
		String orderBy = null;
		
		Cursor c = mDB.query(DiaryTable.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);
		
				Utils.boogieLog("DataBaseHelper", "getDiaryEntry()", "returning diary entry for row : " + rowId );
		return c;
	}
	
	public Cursor getGraphDataByDiaryRow(String rowId){
		
		String [] columns = null;	// get all columns 
		String selection = GraphsTable.COLUMN_NAME_DIARY_ID + "=?";
		String [] selectionArgs = { rowId };
		String groupBy = null;
		String having = null;
		String orderBy = GraphsTable.COLUMN_NAME_UNIX_TIME + " ASC ";
		
		Cursor c = mDB.query( GraphsTable.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);	
		
				Utils.boogieLog("DataBaseHelper", "getGraphDataByDiaryRow()", "returning graph for row : " + rowId );
		return c;
	}

	public Cursor getResetsDataByDiaryRow(String rowId){
		
		String [] columns = null;	// get all columns 
		String selection = ResetsClickedTable.COLUMN_NAME_DIARY_ID + "=?";
		String [] selectionArgs = { rowId };
		String groupBy = null;
		String having = null;
		String orderBy = ResetsClickedTable.COLUMN_NAME_UNIX_TIME + " ASC ";
		
		Cursor c = mDB.query( ResetsClickedTable.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);	
		
				Utils.boogieLog("DataBaseHelper", "getResetsDataByDiaryRow()", "returning resets for row : " + rowId );
		return c;
	}
	
	public Cursor getAllCuesTable(){
		
		String [] columns = null; //{ CuesTable._ID , CuesTable.COLUMN_NAME_CUE_NAME };	// get all columns 
		String selection = null;
		String [] selectionArgs = null;
		String groupBy = null;
		String having = null;
		String orderBy = null;  // TODO low order alphabetically on name
		
		Cursor c = mDB.query( CuesTable.TABLE_NAME, columns, selection, selectionArgs, groupBy, having, orderBy);	
		
				Utils.boogieLog("DataBaseHelper", "getAllCueNames()", "returning all cue names" );
		return c;
	}

	//=================================================================
	// DELETE METHODS
	//=================================================================

	public void deleteDiaryEntry( String where, String [] values){
		// Define 'where' part of query.
		String selection = DiaryTable._ID + " LIKE ?";
		// Specify arguments in placeholder order.
		//String[] selectionArgs = { String.valueOf(rowId) };
		// Issue SQL statement.
		mDB.delete(DiaryTable.TABLE_NAME, selection, values);
	
				Utils.boogieLog("DataBaseHelper", "deleteDiaryEntry()", "deleted from diary table where _id = " + values[0].toString() );
	}

}
