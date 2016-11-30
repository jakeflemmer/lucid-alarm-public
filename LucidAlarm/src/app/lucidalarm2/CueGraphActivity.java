package app.lucidalarm2;

import java.util.Date;

import app.lucidalarm2.ContractSchema.CuesTable;
import app.lucidalarm2.ContractSchema.DiaryTable;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class CueGraphActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {
	
	
	// NOTE : hmmmm.... looks like this class isn't even used anymore
	/*
	 * okay we are including the same graph fragment in this screen and in the diary item screen
	 * however in the case of the diary item screen the fragment loads its graph data from data tables
	 * 
	 * in this case we want it to generate its data based on selected cue settings
	 * if the user changes the selected cue then all of the cue preference settings must be changed automatically
	 * 		( but at least our generating graph data from preferences function wont have to )
	 * 
	 * we need to initially load all of the saved cue settings and populate the spinner with them
	 * 
	 * we need to be able to delete named cues
	 * 
	 * we need to be able to save named cues
	 * 
	 * we need to be able to edit named cues
	 * 
	 */
	
	public static final int CUE_NAME_LOADER = 5000;
	
	private SimpleCursorAdapter mAdapter;
	
	public Spinner cueSpinner;
	
	private boolean cueSpinnerItemSelectedSetThroughInitialization = false;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Utils.setActivityThemeFromPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cue_graph_layout);
        
        /*
        // set up the cue names spinner with all the cue names saved in database by starting its loader
        String[] columnNames = { SettingsActivity.REM_CUE_NAME_PREF };
        int[] views = { android.R.id.text1 };
        
        cueSpinner = (Spinner) findViewById(R.id.cueName_spinner);
 
        mAdapter = new SimpleCursorAdapter(getBaseContext(),
                android.R.layout.simple_spinner_item,
                null,
                columnNames,
                views, 0);

        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        cueSpinner.setAdapter(mAdapter);
        
        cueSpinner.setOnItemSelectedListener( onSpinnerItemSelected );
         
        getSupportLoaderManager().initLoader(CUE_NAME_LOADER, null, this);
        
        Utils.boogieLog("RecordsActivity", "onCreate()" ," simple cursor adapter defined, initLoader called ");
        */
	}
	
	private void setSpinnerSelectionToCueNamePref(){
		String cueName = Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_CUE_NAME_PREF, "default intervals cue");
		cueSpinner.setSelection(getIndex(cueSpinner, cueName));
	}
	
	 private int getIndex(Spinner spinner, String myString)
	 {
		 int index = 0;

		 for (int i=0;i<spinner.getCount();i++){
			 if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
				 index = i;
			 }
		 }
		 return index;
	 } 
	
	//	On Resume
  	//====================================================================================
     
    @Override
    protected void onResume() {
    		super.onResume();
//    		if ( sing().newCueNameHasBeenGeneratedUponCueSettingsChange )
//        	{
//        		Utils.commitNewlyGeneratedCueName(this);
//        	}
//        	if ( sing().cuePreferencesHaveChangedSinceCueGraphActivity )
//        	{
//        		showSaveCueButton(true);
//        		popuplateSaveCueEditTextWithCueNamePreference();
//        		enableTheSpinner(false);	//disableTheSpinnerUntilUserSavesOrDiscardsNewGeneratedCueNameSettings();
//        	}else{
//        		showSaveCueButton(false);
//        	}
    };
	
    
    
    
	//====================================================================================
    //====================================================================================
    // LOADER CALL BACKS
    //====================================================================================
    //====================================================================================
      
    	
    	  // A callback method invoked by the loader when initLoader() is called 
        @Override
        public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {

        	if ( loaderId == CUE_NAME_LOADER )
        	{
        		Uri uri = Uri.parse( ContentProviderLA.CONTENT_PROVIDER_URI + "/" + "cuesTable") ;
                CursorLoader c = new CursorLoader(this, uri, null, null, null, null);
                
                		Utils.boogieLog("DiaryActivity", "onCreateLoader()", "CUE_NAME_LOADER fetching all cue names..." );
            	return c;
        	}
        	
        	return null;
            
        }
     
        /** A callback method, invoked after the requested content provider returned all the data */
        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
            
        	mAdapter.swapCursor(cursor);
        	setSpinnerSelectionToCueNamePref();
            		Utils.boogieLog("DiaryActivity", "onLoadFinished()", " blah " );
        }
     
        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            mAdapter.swapCursor(null);
        }
        
  	
      //====================================================================================
      // CUE SPINNER HANDLER
      //====================================================================================
        
        AdapterView.OnItemSelectedListener onSpinnerItemSelected = new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View spinnerView,
                    int arg2, long rowId) {
            	
            		if ( cueSpinnerItemSelectedSetThroughInitialization == false )
            		{
            			cueSpinnerItemSelectedSetThroughInitialization = true;
            			return;
            		}
            		// TODO - problem - its coming here on initialization of the spinner
            		// when the default value is set on the spinner when it is created
            		// check if it is a different item than was selected  -- think it does this out of the box

            		String cueName = cueSpinner.getSelectedItem().toString();
            			
            		Utils.setStringPreference(getApplicationContext(), cueName, SettingsActivity.REM_CUE_NAME_PREF);
            	
            		// keep the cursor from the load and make the initial load load all rows and columns of cuesTable  -- its on the mAdapter.getCursor()
            		// then take the columns from cursor and parse them into preference settings and set them in prefs editor
            	
            		parseCuesTableRowIntoPrefSettingsAndSetThem( rowId );	// TODO only if the cue names dont match !!
            	
            		// then redraw the graph
            		// TODO not sure about this solution at all !!!!
            		//onResume();	// will this cause a stack overflow ? I really only want to redraw the child fragment
            		redrawTheGraphFragment();
        		
            				Utils.boogieLog("CueGraphActivity", "onItemSelected", "from cue name spinner");
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            		Utils.boogieLog("CueGraphActivity", "onNothingSelected", "from cue name spinner");
            }
        };
        
        
        //====================================================================================
        //	HANDLERS
        //====================================================================================
        
        public void onDiscardCueButtonClick(View view) {
        		// TODO - i could save the previously selected spinner value here and restore it - but i dont feel like it right now
        		//sing().newCueNameHasBeenGeneratedUponCueSettingsChange = false;
        		sing().cuePreferencesHaveChangedSinceCueGraphActivity = false;
        		//sing().newlyGeneratedCueName = "";
        		enableTheSpinner(true);
        }
        
        /*
        public void onSaveCueButtonClick(View view) {
        	
        		// make sure we insert the current content of the save as edit text into the cue name preference before saving
        		// and obviously check that there is something there first
        		EditText saveAsET = ( EditText ) findViewById(R.id.saveAsEditText);
        		String saveAsEtText = saveAsET.getText().toString();
            	if ( saveAsEtText.equals("") || saveAsEtText.length() == 0 )
            	{
            		Toast.makeText(this,"Please provide a custom cue name",Toast.LENGTH_LONG).show();
            		return;
            	}
            	Utils.setStringPreference(getApplicationContext(), saveAsET.getText().toString(), SettingsActivity.REM_CUE_NAME_PREF);
            	// make a content uri to insert the new cueSetting
            	Uri uri = Uri.parse( ContentProviderLA.CONTENT_PROVIDER_URI + "/" + "cuesTable" + "/" + "entry" ) ;
            	
            	ContentValues contentValues = Utils.parseCueSettingsIntoContentValues(this);
        		getContentResolver().insert(uri, contentValues);
            	
           // 	sing().newCueNameHasBeenGeneratedUponCueSettingsChange = false;
            	sing().cuePreferencesHaveChangedSinceCueGraphActivity = false;
          // 	sing().newlyGeneratedCueName = "";	
        	
        }
        */
        
        
        
        public void enableTheSpinner( boolean enabled ) {
        	cueSpinner.setEnabled(enabled);
        }
        
	public void parseCuesTableRowIntoPrefSettingsAndSetThem( long rowId ){
		Utils.parseCuesTableRowIntoPrefSettingsAndSetThem( this, mAdapter.getCursor(), rowId);
	}
	
    public void onEditCueButtonClick(View view){
    	
    	Intent intent = new Intent(this, CueSettingsActivity.class);
    	startActivity(intent);
    }
    
//    private void popuplateSaveCueEditTextWithCueNamePreference() {
//    		EditText saveCueET = (EditText) findViewById(R.id.saveAsEditText);
//    		saveCueET.setText( Utils.getStringPreference(getApplicationContext(), SettingsActivity.REM_CUE_NAME_PREF, "default increasing cue"));
//    }
	
	
	
    //====================================================================================
    //	REGULAR CLASS METHODS
    //====================================================================================
//    private void showSaveCueButton( boolean showTheButton ) {
//    		View saveCueLinearLayout = (View) findViewById(R.id.saveCueLinearLayout);
//    		saveCueLinearLayout.setVisibility( ( showTheButton ? View.VISIBLE : View.INVISIBLE) );
//    }
    
    public void redrawTheGraphFragment(){
    	android.app.FragmentManager mgr = getFragmentManager(); 	// TODO CUE GRAPH getSupportFragmentManager();
    	GraphFragment graphFragment = (GraphFragment) mgr.findFragmentById(R.id.cue_settings_graph_fragment);
    	graphFragment.generateTheGraphDataFromSettings();
    }
    
    
    //====================================================================================
   	//	Getters && Setters
   	//====================================================================================
   		
   		private Singleton sing(){
   			return Singleton.getInstance();
   		}
       

}
