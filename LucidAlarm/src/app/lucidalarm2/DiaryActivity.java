package app.lucidalarm2;

import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.LoaderManager.LoaderCallbacks;
//import android.support.v4.content.CursorLoader;
//import android.support.v4.content.Loader;
//import android.support.v4.widget.CursorAdapter;
//import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;
import app.lucidalarm2.ContractSchema.DiaryTable;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.*;
import android.database.Cursor;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.text.*;
import android.text.format.DateFormat;
import android.media.*;



public class DiaryActivity extends Activity implements LoaderCallbacks<Cursor> {

	private static final int DIARY_ENTRY_LOADER = 5577;
	private MediaPlayer mediaPlayerForGettingDuration = null;
	private String dreamUnixString;
	
	private Map<String, View> columnNamesToViewsMap = new HashMap<String, View>();
	
	private View textFieldThatHasFocus;
	
	private boolean recreatingDueToThemeChange = false;
	//====================================================================================
	//	TESTING NOTES
	//====================================================================================
	
	// test that each editable view sets its dirty flag correctly
	// test that on save each editable views text is correctly mapped onto the dreamRecord to be saved
	// test that the dream record is correctly saved
	
	//====================================================================================
	//	STATE CHANGE HANDLERS
	//====================================================================================
	
	/* Tech Notes :
	 * we start this activity with an intent that tells us the rowId we want
	 * so we kick off a loader and get a cursor and
	 * then we walk down the cursor setting the relevant properties on the diary_item
	 * the graph fragment inherits the intent and handles its own data querying and drawing
	 * note : we CANNOT use a simple cursor adapter because we have a fragment in the layout
	 * 
	 * we have two possible approaches 
	 * 1 ) set a change listener on every UI control and monitor a dirty flag
	 * when the flag is dirty update just the relevant fields in the database
	 * 2 ) assume that everything is dirty and when leaving the screen insert every field
	 * 
	 * we are taking the second approach because it may save coding time
	 */
	
	
				
	    //		mapEditableViewContentsOntoDreamRecordForSaving();
	    // edit text listeners and the dirty flag	
		// onResume !!
	
	//=================================================================================
    // STATE CHANGE HANDLERS
    //=================================================================================
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	Utils.setActivityThemeFromPreferences(this);
    	super.onCreate(savedInstanceState);
    	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);	// prevents keyboard from showing automatically
    }
	
    @Override
    public void onStart(){
    		super.onStart();
    }
    
    @Override
    public void onPause() {
    		super.onPause();
    		if ( recreatingDueToThemeChange == false ) saveAllOfTheDiaryEntryDataBackIntoTheDataBase();
//		View jarvisButton = findViewById(R.id.jarvisImageButton);
//		jarvisButton.setVisibility(View.GONE);
	}
    
    @Override
    public void onResume() {
    		super.onResume();    
    		
    		Fragment fragment = getFragmentManager().findFragmentById(R.id.graph_activity);
    	    if(fragment!=null) {
    	        Utils.boogieLog("DiaryActivity", "onResume", "fragment not == null !!");
    	        return;	// we don't want to load the loader manager again cause we get weird null pointer from graph frag
    	    }
//    	View jarvisButton = findViewById(R.id.jarvisImageButton);
//    	if ( jarvisButton == null || jarvisButton.getVisibility() == View.GONE )
//    	{
    		getLoaderManager().initLoader(DIARY_ENTRY_LOADER, null, this);
    	//}
    };
    
    
    
  //=================================================================================
    
    private void createColumnNamesToViewsHashmap() {
    	/*
    	 	{ HIDDEN_ID_FIELD,
	         COLUMN_NAME_TITLE,
	        COLUMN_NAME_NOTES ,
	        COLUMN_NAME_OTHER ,													
	        COLUMN_NAME_LUCID ,													
	       COLUMN_NAME_COLOR ,
	        COLUMN_NAME_UNIX_TIME , 																										
	        COLUMN_NAME_MISC_1 ,													
	        COLUMN_NAME_MISC_2 ,													
	        COLUMN_NAME_MISC_3 ,													
	        COLUMN_NAME_MISC_NAMES ,													
	        COLUMN_NAME_SOUND ,													
	        COLUMN_NAME_LIGHT_COLOR ,													
	        COLUMN_NAME_LIGHT_FREQUENCY													
	       };													
    	 	*/
    		columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_TITLE, findViewById(R.id.titleEditText));
    		columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_NOTES, findViewById(R.id.notesEditText));
        	columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_OTHER, findViewById(R.id.otherEditText));
        	columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_LUCID, findViewById(R.id.lucidCheckBox));
        	columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_COLOR, findViewById(R.id.colorRadioGroup));
        	columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_UNIX_TIME, findViewById(R.id.dateLabel));
        	columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_MISC_1, findViewById(R.id.misc1EditText));
        	columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_MISC_2, findViewById(R.id.misc2EditText));
        	columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_MISC_3, findViewById(R.id.misc3EditText));
        	columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_MISC_NAMES, null);	// TODO !!
        	columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_SOUND, null);		// displayed on graph fragment
        	columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_LIGHT_COLOR, null);	// displayed on graph fragment
        	columnNamesToViewsMap.put(DiaryTable.COLUMN_NAME_LIGHT_FREQUENCY, null);  // TODO low
    }
    
    //====================================================================================
    //====================================================================================
    // LOADER CALL BACKS
    //====================================================================================
    //====================================================================================
    	
    	  // A callback method invoked by the loader when initLoader() is called 
        @Override
        public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

        		String rowId = getIntent().getStringExtra("selectedRecordId");
            Uri uri = Uri.parse( ContentProviderLA.CONTENT_PROVIDER_URI + "/" + rowId) ;
            
            CursorLoader c = new CursorLoader(this, uri, null, null, null, null);
            
            			Utils.boogieLog("DiaryActivity", "onCreateLoader()", "A callback method invoked by the loader when initLoader() is called" );
            	return c;
        }
     
        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
            
        		Utils.setActivityThemeFromPreferences(this);
        		setContentView(R.layout.diary_item_layout); //  !!!!!!! SET CONTENT VEIW  !!!!!!!!!
        		setTheBackgrounImagesBasedOnTheme();  // THEMES
        		
        		createColumnNamesToViewsHashmap();
        		populateTheDiaryItemFieldsFromTheCursor(arg1); // we cannot use an adapter b/c you can't have fragments in a simple cursor adapter's layout !!!
            	copyOverNotesWrittenInAudioActivity();
        				Utils.boogieLog("DiaryActivity", "onLoadFinished()", " blah " );
        }
     
        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            //mAdapter.swapCursor(null);
        }
        
      //=================================================================================
      // LOAD / POPULATE
      //=================================================================================
     
    public void populateTheDiaryItemFieldsFromTheCursor( Cursor cursor){
    		cursor.moveToFirst();
    	
    		for ( int i = 1 ; i < DiaryTable.allColumnNamesArray.length; i++)	// start at one to skip hidden _ID field
        	{
        		String key = DiaryTable.allColumnNamesArray[i];
        		View view = columnNamesToViewsMap.get(key);
        		if ( view != null)
        		{
        			// === THEMES =============================================================
        			view.setBackgroundColor(getResources().getColor(R.color.low_alpha_black) );        			        			
        			//-------------------------------------------------------------------------
        			if ( view instanceof EditText )
        			{
        				EditText et = (EditText) view; 
            			// === THEMES =============================================================        				
        				Utils.setTextViewColorByTheme((TextView) et, this);
        				//-------------------------------------------------------------------------
        				String text = cursor.getString(cursor.getColumnIndex(key));
        				et.setText( text );
//        				et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//														
//							public void onFocusChange(View v, boolean hasFocus) {
//								if(hasFocus){
//        					        View jarvisButton = findViewById(R.id.jarvisImageButton);
//        					        jarvisButton.setVisibility(View.VISIBLE);
//        					        textFieldThatHasFocus = v;
//        					    }else {
//        					    	View jarvisButton = findViewById(R.id.jarvisImageButton);
//        					        jarvisButton.setVisibility(View.GONE);
//        					    }
//								
//							}
//						});
        				continue;
        			}
        			if ( view instanceof TextView )	// the date label
        			{
        				TextView tv = (TextView) view;
        				String unixString = cursor.getString( cursor.getColumnIndex( DiaryTable.COLUMN_NAME_UNIX_TIME ));
        				dreamUnixString = unixString;
        				Date date = new Date(Long.parseLong(unixString) * 1000l );
        							tv.setText(sing().dateOnlyFormat.format(date));
        							
//        							tv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//										
//        								public void onFocusChange(View v, boolean hasFocus) {
//        									if(hasFocus){
//        	        					        View jarvisButton = findViewById(R.id.jarvisImageButton);
//        	        					        jarvisButton.setVisibility(View.VISIBLE);
//        	        					        textFieldThatHasFocus = v;
//        	        					    }else {
//        	        					    	View jarvisButton = findViewById(R.id.jarvisImageButton);
//        	        					        jarvisButton.setVisibility(View.GONE);
//        	        					    }
//        									
//        								}
//        							});
        			}
        			if ( view instanceof CheckBox )
        			{
        				CheckBox lucidCB = (CheckBox) view;
        				String lucidData = cursor.getString(cursor.getColumnIndex( DiaryTable.COLUMN_NAME_LUCID )).trim().toLowerCase();
        				lucidCB.setChecked( ( lucidData.equals("true") ? true : false ) );
        				lucidCB.setText("Lucid Dream");
        				Utils.setTextViewColorByTheme((TextView) lucidCB, this);
        			}
        			if ( view instanceof RadioGroup )
        			{
        				RadioGroup colorRG = ( RadioGroup ) view;        				
        				String color = cursor.getString( cursor.getColumnIndex( DiaryTable.COLUMN_NAME_COLOR ));
        				if ( color.equals( "green" ))
        				{
        					colorRG.check(R.id.greenRadioButton);
        				}
        				if ( color.equals( "blue" ))
        				{
        					colorRG.check(R.id.blueRadioButton);
        				}
        				if ( color.equals( "red" ))
        				{
        					colorRG.check(R.id.redRadioButton);
        				}
        				if ( color.equals( "yellow" ))
        				{
        					colorRG.check(R.id.yellowRadioButton);
        				}
        				for ( int j = 0 ; j < colorRG.getChildCount(); j++ )
        				{
        					RadioButton rb = (RadioButton) colorRG.getChildAt(j);
        					Utils.setTextViewColorByTheme((TextView) rb, this);
        					setBackgroundColorOfView( (TextView) rb );
        				}
        			}
        			if ( view instanceof RadioButton )
        			{
        				RadioButton rb = ( RadioButton ) view;
        				rb.setTextColor(0xffff0000);  // TODO        				
        			}
        		}
        	}
    	
    				Utils.boogieLog("DiaryActivity", "populateTheDiaryItemFieldsFromTheCursor", "mapping keys to view values");
    }
    
    public void copyOverNotesWrittenInAudioActivity() {
    		if ( sing().dreamNotesCopiedDownFromAudioActivity.length() > 0 )
    		{
    			EditText notesET = ( EditText ) findViewById( R.id.notesEditText );
    			notesET.setText( sing().dreamNotesCopiedDownFromAudioActivity );
    			sing().dreamNotesCopiedDownFromAudioActivity = "";
    		}
    }
    
    //=================================================================================
    // THEMES
    //=================================================================================
    
    private void setBackgroundColorOfView( TextView view ){
    	
		view.setBackgroundColor(0x00000000);
    }
    
    private void setTheBackgrounImagesBasedOnTheme() {
    	hideAllBackgroundImages();
    	String theme = Utils.getStringPreference(this, SettingsActivity.GENERAL_THEME_PREF, SettingsActivity.THEME_SPACE);
    	if ( theme.equals(SettingsActivity.THEME_SPACE))
    	{
    		findViewById(R.id.planetImageView).setVisibility(View.VISIBLE);
    		findViewById(R.id.spaceRingImageView).setVisibility(View.VISIBLE);
    	}
    	else if ( theme.equals(SettingsActivity.THEME_CLOUDS))
    	{
    		findViewById(R.id.clouds4ImageView).setVisibility(View.VISIBLE);
    		findViewById(R.id.cloudsLakeImageView).setVisibility(View.VISIBLE);
    	}
    }
    
    private void hideAllBackgroundImages(){
    		findViewById(R.id.planetImageView).setVisibility(View.GONE);
    		findViewById(R.id.spaceRingImageView).setVisibility(View.GONE);
    		findViewById(R.id.clouds4ImageView).setVisibility(View.GONE);
    		findViewById(R.id.cloudsLakeImageView).setVisibility(View.GONE);    	
    }
    
    //=================================================================================
    // SAVE		( we just assume every field is dirty and re-insert every ui field )
    //=================================================================================
    
    private void saveAllOfTheDiaryEntryDataBackIntoTheDataBase() {
    	
    		ContentValues contentValues = new ContentValues();
    	
    		for ( int i = 1 ; i < DiaryTable.allColumnNamesArray.length; i++)	// start at one to skip hidden _ID field
        	{
        		String key = DiaryTable.allColumnNamesArray[i];
        		View view = columnNamesToViewsMap.get(key);
        		if ( view != null)
        		{
        			if ( view instanceof EditText )
        			{
        				EditText et = (EditText) view;
        				contentValues.put(key, et.getText().toString());
        			}
        			if ( view instanceof TextView )	// the date label
        			{
        				// this is not editable - has not changed 
        			}
        			if ( view instanceof CheckBox )
        			{
        				CheckBox lucidCB = (CheckBox) view;
        				String isChecked =  ( lucidCB.isChecked() ? "true" : "false" );
        				contentValues.put(key, isChecked);
        			}
        			if ( view instanceof RadioGroup )
        			{
        				RadioGroup rg = ( RadioGroup ) view;
        				RadioButton checkedRB = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
        				CheckBox lucidCB = (CheckBox) findViewById(R.id.lucidCheckBox);
        				if ( lucidCB.isChecked() )
        				{
        					contentValues.put(key, sing().lucidDreamColorString );
        				}else{
        					contentValues.put( key, ( String ) checkedRB.getTag());
        				}
        			}
        		}
        	}
    		String rowId = getIntent().getStringExtra("selectedRecordId");
    		Uri uri = Uri.parse( ContentProviderLA.CONTENT_PROVIDER_URI + "/" + "diaryentry" + "/" + rowId) ;
    		if ( contentValues.size() > 0 )
    		{
    			getContentResolver().update( uri, contentValues ,null , null);
    		}
    		
    	
    				Utils.boogieLog("DiaryActivity", "saveAllOfTheDiaryEntryDataBackIntoTheDataBase", "mapping keys to view values");
    }
    
    
    //=================================================================================
    // GRAPH
    //=================================================================================
    
    /*
    	Our beautiful graph is contained is a self rendering fragment :)
    */
	
    //====================================================================================
  	//	CLICK HANDLERS
  	//====================================================================================
    
    public void onAudioRecorderImageButtonClick( View view ) {
    		
    		//Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.graph_activity);
    		Fragment fragment = getFragmentManager().findFragmentById(R.id.graph_activity);
	    if(fragment!=null) {
	        getFragmentManager().beginTransaction().remove(fragment).commit();
	    }
	    
    		Intent recordDreamAudioIntent = new Intent ( this, AudioActivity.class );
    		TextView dateTV = ( TextView ) findViewById( R.id.dateLabel );
    		EditText notesET = ( EditText ) findViewById( R.id.notesEditText );
    		recordDreamAudioIntent.putExtra( AudioActivity.DREAM_RECORD_DATE_STRING, dreamUnixString );
    		recordDreamAudioIntent.putExtra( AudioActivity.DREAM_RECORD_NOTES_STRING, notesET.getText().toString() );
    		sing().dreamNotesCopiedDownFromAudioActivity = "";
    		startActivity(recordDreamAudioIntent);
    }
    /*
    public void onJarvisImageButtonClick( View view ) {
		
    	Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
       	 try {
            startActivityForResult(i, MainActivity.VOICE_RECOGNITION_REQUEST_OK);
        } catch (Exception e) {
       	 	Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==MainActivity.VOICE_RECOGNITION_REQUEST_OK  && resultCode==RESULT_OK) {
    		ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
    		//((TextView)findViewById(R.id.text1)).setText(thingsYouSaid.get(0));
    		Toast.makeText(this, thingsYouSaid.get(0), Toast.LENGTH_LONG).show();
    		if ( textFieldThatHasFocus instanceof TextView )
    		{
    			TextView tv = (TextView) textFieldThatHasFocus;
    			for ( int i = 0 ; i < thingsYouSaid.size(); i++ )
    			{
    				tv.setText(tv.getText() + thingsYouSaid.get(i) );
    			}
    		}    		
        }
        
    }
    */
    
	//====================================================================================
	//	Getters && Setters
	//====================================================================================
		
		private Singleton sing(){
			return Singleton.getInstance();
		}
	
		//=================================================================================
	    // ON THE CHOPPING BLOCK
	    //=================================================================================
    
		// onResume
		/*
    	String[] columnNames = {  
    			DiaryTable.COLUMN_NAME_UNIX_TIME , 
    			DiaryTable.COLUMN_NAME_TITLE , 
    			DiaryTable.COLUMN_NAME_LUCID ,
    			DiaryTable.COLUMN_NAME_NOTES ,
    			DiaryTable.COLUMN_NAME_OTHER ,
    			DiaryTable.COLUMN_NAME_COLOR ,
    			DiaryTable.COLUMN_NAME_MISC_1 ,
    			DiaryTable.COLUMN_NAME_MISC_2 ,
    			DiaryTable.COLUMN_NAME_MISC_3 
    			// TODO DiaryTable.COLUMN_NAME_MISC_NAMES ,
    			};
    	int[] views = { 
    			R.id.dateLabel , 
    			R.id.titleEditText , 
    			R.id.lucidCheckBox ,
    			R.id.notesEditText ,
    			R.id.otherEditText ,
    			R.id.colorRadioGroup ,
    			R.id.misc1EditText ,
    			R.id.misc2EditText ,
    			R.id.misc3EditText
    			};
    	
    	mListView = (ListView) findViewById(R.id.diary_listview);
    	
        mAdapter = new SimpleCursorAdapter(getBaseContext(),
        		R.layout.diary_item_layout,
                null,
                columnNames,
                views, 0);
        
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            	if(columnIndex == 4) { // lucid
            		CheckBox cb = (CheckBox) view;
            		String lucidData = cursor.getString(columnIndex).trim().toLowerCase();
            		cb.setChecked( ( lucidData.equals("true") ? true : false ) );
            		return true;
            	}
            	if(columnIndex == 5) { // color
            		RadioGroup rg = (RadioGroup) view;
            		String colorData = cursor.getString(columnIndex).trim().toLowerCase();
            		
            		// get selected radio button from radioGroup
        			int selectedId = rg.getCheckedRadioButtonId();
         
        			// find the radiobutton by returned id
        			//RadioButton radioSexButton = (RadioButton) findViewById(selectedId);
        			//radioSexButton.setChecked(true);
        			
        			RadioButton blueRadioButton = (RadioButton) rg.getChildAt(0);	//(RadioButton) findViewById(R.id.blueRadioButton);
        			RadioButton orangeRadioButton = (RadioButton) rg.getChildAt(1);	//(RadioButton) findViewById(R.id.orangeRadioButton);
        			RadioButton greenRadioButton = (RadioButton) rg.getChildAt(2);	//(RadioButton) findViewById(R.id.greenRadioButton);
        			RadioButton pinkRadioButton = (RadioButton) rg.getChildAt(3);	//(RadioButton) findViewById(R.id.pinkRadioButton);
        			
        			blueRadioButton.setChecked(false);
        			orangeRadioButton.setChecked(false);
        			greenRadioButton.setChecked(false);
        			pinkRadioButton.setChecked(false);
        			
            		if ( colorData.equals("blue") )
            		{
            			blueRadioButton.setChecked(true);
            		}else if ( colorData.equals("orange") )
            		{
            			orangeRadioButton.setChecked(true);
            		}else if ( colorData.equals("green") )
            		{
            			greenRadioButton.setChecked(true);
            		}else if ( colorData.equals("pink") )
            		{
            			pinkRadioButton.setChecked(true);
            		}else{
            			blueRadioButton.setChecked(true);
            		}
            		return true;
            	}
            	return false;
            }
        });
        */
        
        //mListView.setAdapter(mAdapter);
		
		
		
		// POPULATE
		
		/*
    	TextView dateLabel = (TextView) findViewById(R.id.dateLabel);
    	String unixString = cursor.getString( cursor.getColumnIndex( DiaryTable.COLUMN_NAME_UNIX_TIME ));
    	Date date = new Date(Long.parseLong(unixString) * 1000l );
    	dateLabel.setText(sing().dateOnlyFormat.format(date));
    	
    	EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
    	String theTitle = cursor.getString(cursor.getColumnIndex( DiaryTable.COLUMN_NAME_TITLE ));
    	titleEditText.setText( theTitle );
    	
    	CheckBox lucidCB = (CheckBox) findViewById(R.id.lucidCheckBox);
		String lucidData = cursor.getString(cursor.getColumnIndex( DiaryTable.COLUMN_NAME_LUCID )).trim().toLowerCase();
		lucidCB.setChecked( ( lucidData.equals("true") ? true : false ) );
    	
		EditText notesEditText = (EditText) findViewById(R.id.notesEditText);
    	String notes = cursor.getString(cursor.getColumnIndex( DiaryTable.COLUMN_NAME_NOTES ));
    	notesEditText.setText( notes );
    	
    	EditText otherEditText = (EditText) findViewById(R.id.otherEditText);
    	String other = cursor.getString(cursor.getColumnIndex( DiaryTable.COLUMN_NAME_OTHER ));
    	otherEditText.setText( other );
    	
    	// TODO color
    	
    	EditText misc1EditText = (EditText) findViewById(R.id.otherEditText);
    	String misc1 = cursor.getString(cursor.getColumnIndex( DiaryTable.COLUMN_NAME_MISC_1 ));
    	misc1EditText.setText( misc1 );
    	
    	EditText misc2EditText = (EditText) findViewById(R.id.otherEditText);
    	String misc2 = cursor.getString(cursor.getColumnIndex( DiaryTable.COLUMN_NAME_MISC_2 ));
    	misc2EditText.setText( misc2 );
    	
    	EditText misc3EditText = (EditText) findViewById(R.id.otherEditText);
    	String misc3 = cursor.getString(cursor.getColumnIndex( DiaryTable.COLUMN_NAME_MISC_3 ));
    	misc3EditText.setText( misc3 );
    	*/
}
