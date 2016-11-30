package app.lucidalarm2;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import java.util.*;

import app.lucidalarm2.ContractSchema.DiaryTable;
import app.lucidalarm2.ContractSchema.ResetsClickedTable;

import android.content.*;
import android.database.Cursor;
import android.graphics.*;
import android.app.*;

import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;


public class RecordsActivity extends FragmentActivity implements AdapterView.OnItemSelectedListener, LoaderCallbacks<Cursor> {

	public static final int RECORDS_LOADER = 5423;
	public static final int DREAMSIGN_LOADER = 9423;
	
	final Context context = this;
	final int DATE_PICKER = 999;
	private Boolean datePickerOkayed = false;
	//private String lastSortedDateBy = "Date (newest first)";
	
	private boolean listAlreadyPopulated = false;
	
	private ListView mListView;
	private SimpleCursorAdapter mAdapter;
	
	private String dreamSignToSearchFor = "";
	
	public Object textViewsMap;
	
	// So this is how the order goes for all of this :
	/*
	 	this class onCreate method calls the initLoader manager method
	 	that method than makes the contentProvider perform a query through the databasehelper class
	 	when the cursor returns with the data then a simple cursor adapter is called
	 	the cursor adapter takes the info from the cursor and binds its data into the views
	 	the views are the listView in the records_layout
	 	and each line of the listView comes from the listview_item_layout
	 */
	
	//====================================================================================
	//	STATE CHANGE HANDLERS
	//====================================================================================
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Utils.setActivityThemeFromPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.records_layout);

        String[] columnNames = { DiaryTable.COLUMN_NAME_UNIX_TIME, DiaryTable.COLUMN_NAME_TITLE, DiaryTable.COLUMN_NAME_COLOR };
        int[] views = { R.id.dateLabel, R.id.titleLabel, R.id.hiddenTextView };
        
        mListView = (ListView) findViewById(R.id.listview);
 
        mAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.listview_item_layout,
                null,
                columnNames,
                views, 0);
        
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            		if(columnIndex == 1) { // unix time stamp
            			TextView dateTV = (TextView) view;
            			String unixString = cursor.getString(columnIndex).trim();
            			long unixLong;
            			try{
            				unixLong = Long.parseLong(unixString);
            			}catch(NumberFormatException nfe){
            				// this try catch block is here to clean up from a bad date inserted into DB
            				dateTV.setText("N/A");
            				RelativeLayout rl = ( RelativeLayout ) view.getParent();
                    		rl.setTag("N/A");
                    		return true;
            			}
                		Date date = new java.util.Date(unixLong * 1000l );
                		dateTV.setText( sing().dateOnlyFormat.format(date) );
                		RelativeLayout rl = ( RelativeLayout ) view.getParent();
                		rl.setTag(unixString);
                		return true;
            		}
            		if(columnIndex == 3) { // hidden color
                		RelativeLayout rl = ( RelativeLayout ) view.getParent();
                		TextView dateTv = ( TextView ) rl.getChildAt(0);
                		TextView titleTv = ( TextView ) rl.getChildAt(1);
                		String colorString = cursor.getString(columnIndex).trim();
                		int color = Color.parseColor("#FFFFFF");
                		color = Utils.getColorIntFromColorString(colorString);
                		dateTv.setTextColor( color );
                		titleTv.setTextColor( color);
                		return true;
            		}
            		return false;
            }
        });

        mListView.setAdapter(mAdapter);
        
        mListView.setOnItemClickListener(onRecordClick);
        
        mListView.setOnItemLongClickListener(onRecordLongClick);
        
        // prevent the keyboard from automatically popping up because of the search edit text
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        
        setOnEnterListenerForSearchEditText();
         
        getSupportLoaderManager().initLoader(RECORDS_LOADER, null, this);
        
        				Utils.boogieLog("RecordsActivity", "onCreate()" ," simple cursor adapter defined, initLoader called ");
 	}
    
    
    
    
  
  	//	On Resume
  	//====================================================================================
    @Override
    protected void onResume() {
    	super.onResume();
    	if ( ! listAlreadyPopulated)
    	{
    		
    		//
    		
    		//  TODO		TODO
    		
    		//
    		//initiateSortSpinner();
    		//sortRecords("default");
    		// populateListOfLoadedGraphs();  -- this populates a list from DreamRecordDataObjects read from data files on the SDCard
    		// instead we want to bind views to cursor data returned from our SQLite database
    		// we want a simple cursor adaptor baby !! :)
    		
    		listAlreadyPopulated = true;
    	}
		
    };
    
    //====================================================================================
  	//	LIST ITEM CLICK HANDLERS
  	//====================================================================================
    
    ListView.OnItemClickListener onRecordClick = new ListView.OnItemClickListener() {
    	@Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id)   {

    		Intent intent = new Intent( context, DiaryActivity.class );
    		intent.putExtra("selectedRecordId", Long.toString(id) );
    		intent.putExtra("selectedRecordUnixTime", view.getTag().toString() );
    		startActivity(intent);
    		Utils.boogieLog("RecordsActivity", "onRecordClick ", "cliked list item with id : " + id );
        }
	};
    
    ListView.OnItemLongClickListener onRecordLongClick = new ListView.OnItemLongClickListener() {
    	@Override
        public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id)   {

    		final long rowId = id;
    		Utils.boogieLog("RecordsActivity", "onRecordLongClick", "long cliked list item with id : " + id );
    		AlertDialog.Builder builder = new AlertDialog.Builder(context); 
    			// the view is a relative layout that holds the date and title text fields so the first child is the date
    			RelativeLayout rl = ( RelativeLayout ) view;
    			TextView tv = ( TextView ) rl.getChildAt(0);
    			String date = tv.getText().toString();
			builder
				.setMessage("Delete this record? (" + date  +")")	// we should have this information available in the method arguments
				.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						deleteDiaryEntry(rowId);
					}
				}) 
				.setNegativeButton("No", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						dialog.cancel();
					}
				});
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
    		
    		
    		
    		return true;
        }
	};
	
	// ENTER ON SEARCH EDIT TEXT FIELD
	private void setOnEnterListenerForSearchEditText(){
		final EditText edittext = (EditText) findViewById(R.id.search_edit_text);
		edittext.setOnEditorActionListener(
			    new EditText.OnEditorActionListener() {
			  @Override
			  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			      if (actionId == EditorInfo.IME_ACTION_DONE) {
			        // your action here
			    	  onSearchIconClick(null);
			    	  return  false;
			      }
			      return false;
			   }
			 }
			 );
	}

    
  //====================================================================================
  //====================================================================================
  // LOADER CALL BACKS
  //====================================================================================
  //====================================================================================
    
  	
  	  // A callback method invoked by the loader when initLoader() is called 
      @Override
      public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {
          
    	  	 if ( loaderId == RECORDS_LOADER)
    	  	 {
       		    Uri uri = ContentProviderLA.CONTENT_PROVIDER_URI;
           				Utils.boogieLog("RecordsActivity", "onCreateLoader()", "RECORDS_LOADER loading cursor..." );
           		return new CursorLoader(this, uri, null, null, null, null);
       	  }
       	  if ( loaderId == DREAMSIGN_LOADER)
       	  {
       		    Uri uri = Uri.parse( ContentProviderLA.CONTENT_PROVIDER_URI + "/" + "dreamsign" ); 
       		    String [] columns = null;	// get all columns 
       			String selection = dreamSignToSearchFor;
       			String [] selectionArgs = null;
       			String orderBy = null;
       			
       		    		Utils.boogieLog("RecordsActivity", "onCreateLoader()", "DREAMSIGN_LOADER loading cursor for sign : " + dreamSignToSearchFor );
       		    // the only one the database really cares about is selection
           		return new CursorLoader(this, uri, columns, selection, selectionArgs, orderBy);
       	  }
       	  Utils.boogieLog("RecordsActivity", "onCreateLoader()", "FAIL loader id not recognized" );
          return null;	   
      }
   
      /** A callback method, invoked after the requested content provider returned all the data */
      @Override
      public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
          mAdapter.swapCursor(arg1);
          
          // TODO low
          // depending on which cursor it is ... maybe either of em?
          dreamSignToSearchFor = "";
          
          
          		Utils.boogieLog("RecordsActivity", "onLoadFinished()", " graw " );
          		//createAndInsertNewDiaryEntry(234258l);
      	//deleteDiaryEntry();
          
      }
   
      @Override
      public void onLoaderReset(Loader<Cursor> arg0) {
          mAdapter.swapCursor(null);
      }

    //====================================================================================
      //	CREATE NEW RECORD
      //====================================================================================
      
      public void createAndInsertNewDiaryEntry(Long unixtime)
      {
    	  		ContentValues conentValues = getContentValuesForNewDiaryEntry(unixtime, this);
    	  		getContentResolver().insert(ContentProviderLA.CONTENT_PROVIDER_URI, conentValues);
    	  					Utils.boogieLog("RecordsActivity", "createAndInsertNewDiaryEntry()", " inserting a new created diary entry" );
      }
      
      public static ContentValues getContentValuesForNewDiaryEntry(Long unixtime , Context context){
    	    ContentValues contentValues = new ContentValues();
    	    contentValues.put(DiaryTable.COLUMN_NAME_TITLE, "Dream");
    	    contentValues.put(DiaryTable.COLUMN_NAME_UNIX_TIME, unixtime);
    	    contentValues.put(DiaryTable.COLUMN_NAME_NOTES, "");
    	    contentValues.put(DiaryTable.COLUMN_NAME_LUCID, "false");
    	    contentValues.put(DiaryTable.COLUMN_NAME_COLOR, "fuschia");
    		contentValues.put(DiaryTable.COLUMN_NAME_LIGHT_COLOR, Utils.getStringPreference(context, SettingsActivity.REM_COLOR_PREF, "white"));
    		contentValues.put(DiaryTable.COLUMN_NAME_LIGHT_FREQUENCY, Utils.getStringPreference(context, SettingsActivity.REM_FREQUENCY_PREF, "4"));
    		return contentValues;
      }
      
      private void deleteDiaryEntry(long id)
      {
      	getContentResolver().delete(ContentProviderLA.CONTENT_PROVIDER_URI, "_id=?", new String [] { String.valueOf(id) });	
      }
    
    //====================================================================================
    //	MENU ITEMS AND MENU ITEM CLICK HANDLERS
    //====================================================================================
      
      @Override
      public boolean onCreateOptionsMenu(Menu menu) {
          getMenuInflater().inflate(R.menu.settings_layout, menu);
          return true;
      }
      
  	@Override
  	public boolean onOptionsItemSelected(MenuItem item) {
  	    // Handle item selection
  	    switch (item.getItemId()) {
  	        case R.id.import_xml_records:
  	            Intent importIntent = new Intent( this, ImportDreamRecordsActivity.class);
  	            startActivity(importIntent);
  	            return true;
  	        case R.id.export_xml_records:
  	        		Intent exportIntent = new Intent( this, ExportDreamRecordsActivity.class);
  	            startActivity(exportIntent);
  	            return true;
  	        case R.id.dreamSign_search:
  	        		//onSearchForDreamSigns();
  	            return true;
  	        default:
  	            return super.onOptionsItemSelected(item);
  	    }
  	}  
      
      
      
      
      
      
    //====================================================================================
  	// SEARCH FOR DREAMSIGNS
    //====================================================================================
    
  	public void onSearchIconClick( View view ) {
  		EditText searchWord = ( EditText ) findViewById( R.id.search_edit_text);
  		dreamSignToSearchFor = searchWord.getText().toString().trim();
  		
  		//getSupportLoaderManager().initLoader( DREAMSIGN_LOADER, null, this );
  		getSupportLoaderManager().restartLoader(DREAMSIGN_LOADER, null, this );
  		
  	}
//    private void onSearchForDreamSigns() {
//
//    		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
//    		final EditText input = new EditText(this);
//	    alert.setView(input);
//	    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int whichButton) {
//	            String value = input.getText().toString().trim();
//	            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
//	            // TODO make a query uri passing this value into contentValues
//	        }
//	    });
//
//	    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//	        public void onClick(DialogInterface dialog, int whichButton) {
//	            dialog.cancel();
//	        }
//	    });
//	    alert.show();                   
//    }
    
    
    
    
    
    
	
    
	//@SuppressWarnings("deprecation")
	public void onCreateNewRecordClick(View view){
		showDialog(DATE_PICKER);
	}

	//====================================================================================
	//	Handlers
	//====================================================================================
	
	
	public void onDateColumnHeadingClick(View view){
		
//		if ( lastSortedDateBy.equals("Date (newest first)") )
//		{
//			lastSortedDateBy = "Date (oldest first)";
//		}else{
//			lastSortedDateBy = "Date (newest first)";
//		}
//		sortRecords(lastSortedDateBy);
//		populateListOfLoadedGraphs();
	}
	
	public void onTitleColumnHeadingClick(View view){
		// TODO - i guess we could sort the titles alphabetically but why bother?
//		sortRecords(sortBy);
//		populateListOfLoadedGraphs();
	}
	
	
	
	//====================================================================================
	//	Create New Record
	//====================================================================================
	
	@Override 
	protected Dialog onCreateDialog(int id){
				
		final Calendar c = Calendar.getInstance();
		DatePickerDialog dpd = new DatePickerDialog(this, datePickerListener, 
													c.get(Calendar.YEAR), 
													c.get(Calendar.MONTH), 
													c.get(Calendar.DAY_OF_MONTH));
		dpd.setCancelable(true);
		dpd.setCanceledOnTouchOutside(false);
		dpd.setOnDismissListener(new DialogInterface.OnDismissListener(){
			public void onDismiss(DialogInterface dialog){
				Utils.boogieLog("RecordsActivity", "onDismiss()", "dismiss date picker" );
			}
		});
		
		dpd.setButton(DialogInterface.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener(){
					@Override 
					public void onClick(DialogInterface dialog, int which){
						Utils.boogieLog("RecordsActivity", "onClick()", "cancel date OKayed" );
						datePickerOkayed = true;
					}
				});
		dpd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
			new DialogInterface.OnClickListener(){
				@Override 
				public void onClick(DialogInterface dialog, int which){
					Utils.boogieLog("RecordsActivity", "onClick()", "cancel date picker" );					
				}
			});		
		return dpd;
	}
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener(){
		public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay){
			if (datePickerOkayed == false) return;
			Utils.boogieLog("RecordsActivity", "onDateSet()", "create the new record" );
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, selectedYear);
			c.set(Calendar.MONTH, selectedMonth);
			c.set(Calendar.DAY_OF_MONTH, selectedDay);
			Long unixTime = c.getTimeInMillis()/1000L;
			createAndInsertNewDiaryEntry(unixTime);
		}	
	};
	
	//====================================================================================
	//	Export Dream Records
	//====================================================================================
	
	public void onExportRecordsClick(View view){
		Intent exportIntent = new Intent( this, ExportDreamRecordsActivity.class);
        startActivity(exportIntent);          
	}
	
	//====================================================================================
	//	Import Dream Records
	//====================================================================================
		
		public void onImportRecordsClick(View view){
			Intent importIntent = new Intent( this, ImportDreamRecordsActivity.class);
	        startActivity(importIntent);          
		}
	
	//====================================================================================
	//	Sorting
	//====================================================================================
	
//	private void initiateSortSpinner(){
//		Spinner spinner = (Spinner) findViewById(R.id.sortOption_spinner);
//		//ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sort_options_array
//		//		,android.R.layout.simple_spinner_item);
//		spinner.setOnItemSelectedListener(this);
//				
//	}
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		sing().logg("graw",parent.getItemAtPosition(pos).toString());
		String sortBy = parent.getItemAtPosition(pos).toString();
		//set the text color
		((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(0,200,0));
		//((TextView) parent.getChildAt(0)).setTextAppearance(this,android.R.style.TextAppearance_Large);
		
		// TODO
		
		//sortRecords(sortBy);
		//populateListOfLoadedGraphs();
		/*
		 * <item>Date (oldest first)</item>
		<item>Date (newest first)</item>
		<item>REM Sound</item>
		<item>% Lucidity</item>
		<item>Only lucid</item>
		 */
	}
	public void onNothingSelected(AdapterView<?> parent) {

	}
	//sortOption_spinner
	
	
	
	//====================================================================================
	//====================================================================================
	//		ON THE CHOPPING BLOCK
	//====================================================================================
	//====================================================================================
	
	
	//====================================================================================
		//	Populate the list
		//====================================================================================
	    /*
		private void populateListOfLoadedGraphs(){
			
			LinearLayout layout = (LinearLayout) findViewById(R.id.listview);	// TODO we need to totally get rid of all of this !!
			layout.removeAllViews();	
			
			//addCreateNewRecordOption(layout);
			
			for ( int i=0; i < sortedRecords.size(); i++)
			{
				DreamRecordDataObject rr = sortedRecords.get(i);
				
				if ( rr == null)
				{
					sing().logg("graw","rr is null EXCEPTION" );				
					continue;
				}
				if ( rr.unixTime == null)
				{
					sing().logg("graw","unixtime null EXCEPTION");				
					continue;
				}
				
				//============================================================================================================
		        //  LINEAR LAYOUT
		        //============================================================================================================
				
				// Creating a new LinearLayout
		        LinearLayout linearLayout = new LinearLayout(this);
		         
		        // Setting the orientation to vertical
		        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		         
		        // Defining the LinearLayout layout parameters to fill the parent.
		        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
		            LinearLayout.LayoutParams.MATCH_PARENT,  // width
		            LinearLayout.LayoutParams.WRAP_CONTENT);  // height
		        
				llp.setMargins(0,10,0,10);
		        //============================================================================================================
		        //  TextViews
		        //============================================================================================================
		         
		        // creating a date TextView
				TextView dtv = makeTextView(sing().dateOnlyFormat.format(rr.unixTime*1000L).toString()+"   ",1f,rr.lucid);
		        linearLayout.addView(dtv);
				
		        // make a title text view
		        TextView ttv = makeTextView(rr.dreamTitle+"   ",3f,rr.lucid);
		        linearLayout.addView(ttv);
		     
	            // make a sound text view
		        //TextView stv = makeTextView(rr.sound+"  ",0.20f,rr.lucid);
		        //linearLayout.addView(stv);
		        
		        // make a percentage view
		        //TextView ptv;
				//if ( rr.lucidityScale > 0 )
				//{
				//	ptv = makeTextView(Integer.toString(rr.lucidityScale),0.05f,rr.lucid);	
				//}else{
				//	ptv = makeTextView("",0.05f,rr.lucid);	
				//}
				
		        //linearLayout.addView(ptv);
		        	
				// secret view
		        TextView xtv = makeTextView(Integer.toString(i),0.0f,rr.lucid);	// this view's text is its index number so when we click this line we
		        																// know which dream record to open
		        xtv.setVisibility(View.INVISIBLE);
		        linearLayout.addView(xtv);

		        linearLayout.setOnClickListener(onListLineClick);
				linearLayout.setOnLongClickListener(onListLineLongClick);
		        layout.addView(linearLayout,llp);
			}
			
		}	
		
		private TextView makeTextView(String text,Float weight,boolean lucid){
			final TextView tv = new TextView(this);
	        tv.setBackgroundColor(0xFF000000);
	        
	        //tv.setTypeface(null, Typeface.BOLD);
	        tv.setText(	text);
	        tv.setGravity(Gravity.LEFT);
	        
			tv.setTextAppearance(this, android.R.style.TextAppearance_Medium);
			if ( lucid )
			{
				tv.setTextColor(sing().lucidDreamColor);
			}else{
				tv.setTextColor(0xFF00aa00);	
			}
			
			
	        // Defining the layout parameters of the TextView
	        if ( weight == null){
	        	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
	        			LinearLayout.LayoutParams.WRAP_CONTENT);
	        	 tv.setLayoutParams(lp);
	        }else{
	        	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
	        			0,
	                    LinearLayout.LayoutParams.WRAP_CONTENT,weight);
	        	 tv.setLayoutParams(lp);
	        }
	        tv.setMinLines(2);
			tv.setMaxLines(2);
	        return tv;
		}

		
//		private void addCreateNewRecordOption(LinearLayout layout){
//			TextView tv = makeTextView(" + Create New Record",null,true);
//			//tv.setTextAppearance(this, android.R.style.TextAppearance_Large);
//			tv.setOnClickListener(new View.OnClickListener(){
//				public void onClick(View v){
//					showDialog(DATE_PICKER);
//				}
//			});
//			layout.addView(tv);
//		}
		*/
	
	
	//====================================================================================
	//	Delete list
	//====================================================================================
	
//	public void deleteGraph(DreamRecordDataObject rr){
//		sing().allDreamRecordsLoadedArrayList.remove(rr);
//		populateListOfLoadedGraphs();
//		sing().graphOrNotesDataDirty = true;
//		sing().dreamRecordSelected = null;
//		Intent intent = new Intent(this,GraphActivity.class);
//		intent.putExtra(MainActivity.INTENT_STRING_MESSAGE, GraphActivity.SAVE_THE_GRAPH);	// TODO investigate this work as expected
//		startActivity(intent);
//	}
	
	
	//====================================================================================
		//	Sort function
		//====================================================================================
		/*
		private void sortRecords(String sortBy){
			
			sortedRecords = sing().allDreamRecordsLoadedArrayList;
			if ( sortBy.equals("Date (oldest first)"))
			{
				Collections.sort(sortedRecords, new Comparator<DreamRecordDataObject>(){
						public int compare(DreamRecordDataObject rr1, DreamRecordDataObject rr2){
							return rr1.unixTime.compareTo(rr2.unixTime);
						}
					});
				
			}
			else if ( sortBy.equals("Date (newest first)"))
			{
				Collections.sort(sortedRecords, new Comparator<DreamRecordDataObject>(){
						public int compare(DreamRecordDataObject rr1, DreamRecordDataObject rr2){
							return rr2.unixTime.compareTo(rr1.unixTime);
						}
					});
				
			}
			else if ( sortBy.equals("REM Sound"))
			{
				Collections.sort(sortedRecords, new Comparator<DreamRecordDataObject>(){
						public int compare(DreamRecordDataObject rr1, DreamRecordDataObject rr2){
							return rr1.sound.compareToIgnoreCase(rr2.sound);
						}
					});
			}
			else if ( sortBy.equals("% Lucidity"))
			{
				Collections.sort(sortedRecords, new Comparator<DreamRecordDataObject>(){
						public int compare(DreamRecordDataObject rr1, DreamRecordDataObject rr2){
							Long rr1Long = Long.valueOf(rr1.lucidityScale);
							Long rr2Long = Long.valueOf(rr2.lucidityScale);
							return rr1Long.compareTo(rr2Long);
						}
					});
				
			}
			else if ( sortBy.equals("Lucid Dreams")) 
			{
				sortedRecords = new ArrayList<DreamRecordDataObject>();
				for (int i = 0; i < sing().allDreamRecordsLoadedArrayList.size(); i++)
				{
					DreamRecordDataObject rr = sing().allDreamRecordsLoadedArrayList.get(i);
					if ( rr.lucid )
					{
						sortedRecords.add(rr);
					}
				}
				if ( sortedRecords.size() < 1 )
				{
					sing().logg("graw","No Lucid Dreams Recorded");
					sortRecords("default");
				}
			}
			else if ( sortBy.equals("default")) 
			{
				// mke default sort by newest first
//				Collections.sort(sortedRecords, new Comparator<DreamRecordDataObject>(){
//					public int compare(DreamRecordDataObject rr1, DreamRecordDataObject rr2){
//						return rr1.unixTime.compareTo(rr2.unixTime);
//					}
//				});
				Collections.sort(sortedRecords, new Comparator<DreamRecordDataObject>(){
						public int compare(DreamRecordDataObject rr1, DreamRecordDataObject rr2){
							return rr2.unixTime.compareTo(rr1.unixTime);
						}
					});
			}
		}
	*/
	
	// CLICK HANDLERS
	//============================================================================
	
	/*
	View.OnClickListener onListLineClick = new View.OnClickListener (){		
		public void onClick(View v) {
			LinearLayout ll = (LinearLayout) v; 
			TextView tv = (TextView) ll.getChildAt(ll.getChildCount()-1);
			int index = Integer.parseInt(tv.getText().toString());
			sing().dreamRecordSelected = sortedRecords.get(index);
			Intent displayDreamRecordIntent = new Intent(getApplicationContext(), DiaryActivity.class);
			startActivity(displayDreamRecordIntent);
		}
	};
	
	View.OnLongClickListener onListLineLongClick = new View.OnLongClickListener (){
		public boolean onLongClick(View v) {
			LinearLayout ll = (LinearLayout) v; 
			TextView tv = (TextView) ll.getChildAt(ll.getChildCount()-1);
			//String tvText = (String) tv.getText();

				int index = Integer.parseInt(tv.getText().toString());
				final DreamRecordDataObject rr = sortedRecords.get(index);
				
					AlertDialog.Builder builder = new AlertDialog.Builder(context); 
					builder
						.setMessage("Delete this record? (" + sing().dateOnlyFormat.format(rr.unixTime*1000L)  +")")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface dialog, int id){
								deleteGraph(rr);
							}
						}) 
						.setNegativeButton("No", new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface dialog, int id){
								dialog.cancel();
							}
						});
					AlertDialog alertDialog = builder.create();
					alertDialog.show();
			return true;
		}
	};
	*/
    
	
	private Singleton sing(){
		return Singleton.getInstance();
	}
	
	
	
	
	
	
	
	
	
	
	
	
    
}
