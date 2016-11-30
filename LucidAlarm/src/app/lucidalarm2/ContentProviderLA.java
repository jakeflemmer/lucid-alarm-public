package app.lucidalarm2;

import java.util.ArrayList;

import app.lucidalarm2.ContractSchema.DiaryTable;
import app.lucidalarm2.ContractSchema.GraphsTable;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class ContentProviderLA extends ContentProvider {

	public static final String PROVIDER_NAME = "app.lucidalarm2.contentproviderla";
	 
    public static final Uri CONTENT_PROVIDER_URI = Uri.parse("content://" + PROVIDER_NAME + "/LucidAlarmDB" );
    
    //Constants to identify the requested operation 
    private static final int LUCIDALARMDB = 1;
    private static final int DIARYENTRYDB = 2;
    private static final int GRAPHENTRY = 3;
    private static final int GRAPHDATAENTRY = 4;
    private static final int GRAPHRESETSENTRY = 5;
    private static final int PULLGRAPHDATA = 6;
    private static final int PULLRESETSDATA = 7;
    private static final int PULLCUESTABLE = 8;
    private static final int CUESTABLEENTRY = 9;
    private static final int PULLFOREXPORT = 10;
    private static final int FINDDREAMSIGN = 11;
    private static final int UPDATEDIARYENTRYROWID = 12;
    
    
    private static final UriMatcher uriMatcher ;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "LucidAlarmDB", LUCIDALARMDB);   // gets all records ( name , time )
        uriMatcher.addURI(PROVIDER_NAME, "LucidAlarmDB/#", DIARYENTRYDB);  // gets specific diary entry by row
        uriMatcher.addURI(PROVIDER_NAME, "LucidAlarmDB/graph", GRAPHENTRY);	  // inserts a new diary entry when rem alarms first begin to play
        uriMatcher.addURI(PROVIDER_NAME, "LucidAlarmDB/graph/data", GRAPHDATAENTRY);  // inserts a graph data row
        uriMatcher.addURI(PROVIDER_NAME, "LucidAlarmDB/graph/resets", GRAPHRESETSENTRY);  // inserts a graph data row
        uriMatcher.addURI(PROVIDER_NAME, "LucidAlarmDB/graph/data/#", PULLGRAPHDATA);  // pulls all graph rows by diary row id
        uriMatcher.addURI(PROVIDER_NAME, "LucidAlarmDB/graph/data/resets/#", PULLRESETSDATA);  // pulls all resets rows by diary row id
        uriMatcher.addURI(PROVIDER_NAME, "LucidAlarmDB/cuesTable", PULLCUESTABLE);  // pulls the whole cues table
        uriMatcher.addURI(PROVIDER_NAME, "LucidAlarmDB/cuesTable/entry", CUESTABLEENTRY);  // pulls all resets rows by diary row id
        uriMatcher.addURI(PROVIDER_NAME, "LucidAlarmDB/export", PULLFOREXPORT);  // pulls all rows all columns of diary table
        uriMatcher.addURI(PROVIDER_NAME, "LucidAlarmDB/dreamsign", FINDDREAMSIGN); 
        uriMatcher.addURI(PROVIDER_NAME, "LucidAlarmDB/diaryentry/#", UPDATEDIARYENTRYROWID); 
    }
 
    DataBaseHelper dataBaseHelper;
 
    
    //===============================================================================================
    
    
    // A callback method which is invoked when the content provider is starting up 
	@Override
	public boolean onCreate() {

		dataBaseHelper = new DataBaseHelper(getContext());
		
		Utils.boogieLog("ContentProviderLA", "onCreate()", "generic message");
		return true;
	}
	
	//=================================================================================
    // QUERY
    //=================================================================================
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder)
	{
		if(uriMatcher.match(uri)==LUCIDALARMDB){
        	
			Cursor c = dataBaseHelper.getAllRecords();	// this does all the work !
        	
			c.setNotificationUri(getContext().getContentResolver(), uri);
        				Utils.boogieLog("ContentProviderLA", "query()", "uri matches, returning cursor of all records");
            return c;
            
        }else if ( uriMatcher.match(uri) == DIARYENTRYDB ){ 
        	
        		String rowId = uri.getLastPathSegment();
		
        		Cursor c = dataBaseHelper.getDiaryEntry(rowId);	// this does all the work !
        	
        		c.setNotificationUri(getContext().getContentResolver(), uri);
        				Utils.boogieLog("ContentProviderLA", "query()", "uri matches, returning cursor of diary entry at row " + rowId );
            return c;
        	
		}else if ( uriMatcher.match(uri) == PULLGRAPHDATA ){ 
        	
			String rowId = uri.getLastPathSegment();
		
        		Cursor c = dataBaseHelper.getGraphDataByDiaryRow(rowId);	// this does all the work !
        	
        		//c.setNotificationUri(getContext().getContentResolver(), uri);
        				Utils.boogieLog("ContentProviderLA", "query()", "uri matches, returning cursor of graph rows by diary entry row " + rowId );
            return c;
        
		}else if ( uriMatcher.match(uri) == PULLRESETSDATA ){ 
        	
			String rowId = uri.getLastPathSegment();
		
        		Cursor c = dataBaseHelper.getResetsDataByDiaryRow(rowId);	// this does all the work !
        		//c.setNotificationUri(getContext().getContentResolver(), uri);
        	
        			Utils.boogieLog("ContentProviderLA", "query()", "uri matches, returning cursor of resets rows by diary entry row " + rowId );
            return c;
		
		}else if ( uriMatcher.match(uri) == PULLCUESTABLE ){ 
		
			Cursor c = dataBaseHelper.getAllCuesTable();	// this does all the work !
        	
			//c.setNotificationUri(getContext().getContentResolver(), uri);
        	
        			Utils.boogieLog("ContentProviderLA", "query()", "uri matches, returning cursor of all cue names ");
            return c;
		
		}else if ( uriMatcher.match(uri) == PULLFOREXPORT ){ 
		
			Cursor c = dataBaseHelper.getAllRecordsForExport();	// this does all the work !
        	
			//c.setNotificationUri(getContext().getContentResolver(), uri);
        	
        			Utils.boogieLog("ContentProviderLA", "query()", "uri matches, returning cursor of all cue names ");
            return c;
		
		}else if ( uriMatcher.match(uri) == FINDDREAMSIGN ){ 
		
			Cursor c = dataBaseHelper.getRecordsWithDreamSign(selection);	
        	
			//c.setNotificationUri(getContext().getContentResolver(), uri);
        	
        			Utils.boogieLog("ContentProviderLA", "query()", "uri matches, returning cursor of all cue names ");
            return c;
		
		}else {
        
        			Utils.boogieLog("ContentProviderLA", "query()", "uri does not match, returning null");
        	return null;
        }
	}
	
	//=================================================================================
    // INSERT
    //=================================================================================
	
	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		
		if ( uriMatcher.match(uri) == LUCIDALARMDB ) {
			insertDiaryEntry(uri, contentValues);	// new record created from the records activity - or imported from xml - there will be no graph
					Utils.boogieLog("ContentProviderLA", "insert()", "inserting diary entry through new record created in records activity");
		}
		if ( uriMatcher.match(uri) == GRAPHENTRY ) {	
			// rem alarms have begun to play and the value of newDiaryEntryRowIdInsertedOnStartOfNewAlarm on the singleton == 0
			// lets insert a new diary entry
			// and get its returned id which we store on the singleton
			// and put in the graph table and the resets clicked table
			
			long newRowId = insertDiaryEntry ( uri, contentValues );
			sing().newDiaryEntryRowIdInsertedOnStartOfNewAlarm = newRowId;
			Utils.boogieLog("ContentProviderLA", "insert()", "inserting new diary entry through start of rem alarm");
		}
		if ( uriMatcher.match(uri) == GRAPHDATAENTRY ) {
			dataBaseHelper.insertGraphRow(contentValues);
			Utils.boogieLog("ContentProviderLA", "insert()", "inserting new graph data row");
		}
		if ( uriMatcher.match(uri) == GRAPHRESETSENTRY ) {
			dataBaseHelper.insertResetsClicked(contentValues);
			Utils.boogieLog("ContentProviderLA", "insert()", "inserting new resets clicked row");
		}
		return null;
	}
	
	private long insertDiaryEntry( Uri uri, ContentValues contentValues ){
		// wtf - the below was also not necessary !?!?
//		String title = contentValues.getAsString(DiaryTable.COLUMN_NAME_TITLE);
//		String notes = contentValues.getAsString(DiaryTable.COLUMN_NAME_NOTES);
//		String other = contentValues.getAsString(DiaryTable.COLUMN_NAME_OTHER);
//		String lucid = contentValues.getAsString(DiaryTable.COLUMN_NAME_LUCID);
//		String color = contentValues.getAsString(DiaryTable.COLUMN_NAME_COLOR);
//		String unixtime = contentValues.getAsString(DiaryTable.COLUMN_NAME_UNIX_TIME);
//		String misc1 = contentValues.getAsString(DiaryTable.COLUMN_NAME_MISC_1);
//		String misc2 = contentValues.getAsString(DiaryTable.COLUMN_NAME_MISC_2);
//		String misc3 = contentValues.getAsString(DiaryTable.COLUMN_NAME_MISC_3);
//		String miscNames = contentValues.getAsString(DiaryTable.COLUMN_NAME_MISC_NAMES);
		
		long newRowId = dataBaseHelper.insertDiaryEntry(contentValues); //title, notes, other, lucid, color, unixtime, misc1, misc2, misc3, miscNames);
 		
		getContext().getContentResolver().notifyChange(uri, null);
		return newRowId;
	}
	
	//=================================================================================
    // UPDATE
    //=================================================================================
	
	@Override
	public int update(Uri uri, ContentValues contentValues, String arg2, String[] arg3) {
		if ( uriMatcher.match(uri) == UPDATEDIARYENTRYROWID ) {
			String rowId = uri.getLastPathSegment();
			dataBaseHelper.updateDiaryEntry( rowId, contentValues );
			getContext().getContentResolver().notifyChange(uri, null);
					Utils.boogieLog("ContentProviderLA", "update()", "updating diary entry on close of diaryactivity");
		}
		
				Utils.boogieLog("ContentProviderLA", "update()", "generic message");
		return 0;
	}
	
	//=================================================================================
    // DELETE
    //=================================================================================
	
	@Override
	public int delete(Uri uri, String where, String[] values) {
		
		dataBaseHelper.deleteDiaryEntry(where, values );
		getContext().getContentResolver().notifyChange(uri, null);
		
				Utils.boogieLog("ContentProviderLA", "delete()", " deleting diary entry where id = " + values[0].toString() );
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		
		Utils.boogieLog("ContentProviderLA", "getType()", "generic message");
		return null;
	}
	
	
	//====================================
	// GETTERS AND SETTERS
	public Singleton sing(){
		return Singleton.getInstance();
	}


}
