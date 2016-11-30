package app.lucidalarm2;

import java.util.ArrayList;
import java.util.Date;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import app.lucidalarm2.ContractSchema.GraphsTable;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.app.LoaderManager.LoaderCallbacks;
//import android.support.v4.content.CursorLoader;
import android.content.CursorLoader;

//import android.support.v4.content.Loader;
import android.content.Loader;
import android.text.format.DateFormat;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class GraphFragment extends Fragment implements LoaderCallbacks<Cursor> {
	
	public static final int GRAPH_LOADER = 1000;
	public static final int RESETS_LOADER = 3000;
	
	private boolean graphLoaded = false;
	private boolean resetsLoaded = false;
	
	private Cursor graphCursor;
	private Cursor resetsCursor;
	
	private double highestPointOnYAxis = 0;
	
	private LinearLayout parentLayout;
	
	private static View view;
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		
    		if (view != null) {
    	        ViewGroup parent = (ViewGroup) view.getParent();
    	        if (parent != null) parent.removeView(view);
    	    	}
    		try {
        		view = inflater.inflate(R.layout.graph_layout, container, false);
    		} catch (InflateException e) {
        		// map is already there, just return view as it is 
    		}
    			
    		return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parentLayout = (LinearLayout) view.findViewById( R.id.graph_linearLayout);
        
        Activity parentActivity = getActivity();
    	
//		if ( parentActivity instanceof DiaryActivity )
//		{
//			String rowId = parentActivity.getIntent().getStringExtra("selectedRecordId");
//				Utils.boogieLog("GraphFragment", "onCreateView", "from inside DiaryActivity rowId from intent : " + rowId );
//				
//			//getActivity().getSupportLoaderManager().initLoader(GRAPH_LOADER, null, this);
//			//getActivity().getSupportLoaderManager().initLoader(RESETS_LOADER, null, this);
//			
//			getActivity().getLoaderManager().initLoader(GRAPH_LOADER, null, this);
//			getActivity().getLoaderManager().initLoader(RESETS_LOADER, null, this);
//		}
        
        			Utils.boogieLog("GraphFragment", "onViewCreated()", "graw" );
    }
    
    // Called at the start of the visible lifetime.
    @Override
    public void onStart(){
    		super.onStart();
    		// Apply any required UI change now that the Fragment is visible.
    		Activity parentActivity = getActivity();
    		if ( parentActivity instanceof CueGraphActivity || parentActivity instanceof CueSettingsActivity || parentActivity instanceof MainActivity )
    		{
						Utils.boogieLog("GraphFragment", "onStart", "from inside CueGraphActivity ");
				generateTheGraphDataFromSettings();
				View refreshImageButton = view.findViewById(R.id.refreshGraph_imageButton);
				refreshImageButton.setVisibility(View.VISIBLE);
    		}
    		if ( parentActivity instanceof DiaryActivity )
    		{
    			String rowId = parentActivity.getIntent().getStringExtra("selectedRecordId");
    				Utils.boogieLog("GraphFragment", "onStart", "from inside DiaryActivity rowId from intent : " + rowId );
    				
    			getActivity().getLoaderManager().initLoader(GRAPH_LOADER, null, this);
    			getActivity().getLoaderManager().initLoader(RESETS_LOADER, null, this);
    			
    			View refreshImageButton = view.findViewById(R.id.refreshGraph_imageButton);
				refreshImageButton.setVisibility(View.GONE);
    		}
    }
    @Override
    public void onResume(){
    	super.onResume();
    	Activity parentActivity = getActivity();
//		if (  parentActivity instanceof CueSettingsActivity )  -- graph on rem settings screen put on back burner for now
//		{
//					Utils.boogieLog("GraphFragment", "onResume", "from inside CueGraphActivity ");
//			generateTheGraphDataFromSettings();
//			CueSettingsActivity csa = ( CueSettingsActivity ) parentActivity;
//			csa.setGraphFragment( this );
//		}
    }
    
    //====================================================================================
    //====================================================================================
    // LOADER CALL BACKS
    //====================================================================================
    //====================================================================================
    	
    	  // A callback method invoked by the loader when initLoader() is called 
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {

        		if ( id == GRAPH_LOADER )
        		{
            		String rowId = getActivity().getIntent().getStringExtra("selectedRecordId");
            		Uri uri = Uri.parse( ContentProviderLA.CONTENT_PROVIDER_URI + "/" + "graph" + "/" + "data" + "/" + rowId) ;
                
            		CursorLoader c = new CursorLoader(getActivity(), uri, null, null, null, null);
                
            				Utils.boogieLog("GraphFragment", "onCreateLoader()", "calling graph/data/rowId uri " );
            		return c;
            	}
            	if ( id == RESETS_LOADER )
            	{
            		String rowId = getActivity().getIntent().getStringExtra("selectedRecordId");
            		Uri uri = Uri.parse( ContentProviderLA.CONTENT_PROVIDER_URI + "/" + "graph" + "/" + "data" + "/" + "resets" + "/" + rowId) ;
                
            		CursorLoader c = new CursorLoader(getActivity(), uri, null, null, null, null);
                
            				Utils.boogieLog("GraphFragment", "onCreateLoader()", "calling graph/data/resets/rowId uri " );
            		return c;
            	}
            	return null;
        }
     
        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
            
        		int loaderId = arg0.getId();
        	
        		if ( loaderId == GRAPH_LOADER )
        		{
        			if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
        				//cursor is empty
        				// set no graph to draw message or something like that
        				// TODO
        							Utils.boogieLog("GraphFragment", "onLoadFinished()", " empty graph cursor " );
        			}else{
        							Utils.boogieLog("GraphFragment", "onLoadFinished()", " populated graph cursor " );
        				graphCursor = cursor;
        				graphLoaded = true;
            			if ( resetsLoaded ) prepareTheGraphData();
            		}
        		}
        		if ( loaderId == RESETS_LOADER )
            	{
            					Utils.boogieLog("GraphFragment", "onLoadFinished()", " resets cursor loaded " );
            			resetsCursor = cursor;
            			resetsLoaded = true;
            			if ( graphLoaded ) prepareTheGraphData();	// NB this is presupposing that graph cursor will always load before resets loader
            	}
        }
     
        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
            //mAdapter.swapCursor(null);
        }
        
      //====================================================================================
        
        public void prepareTheGraphData(){
        
        		ArrayList<AlarmsPlayedDataObject> graphToDrawArrayList = prepareGraphDataFromCursor( graphCursor );
        		ArrayList<AlarmsPlayedDataObject> resetsToDrawArrayList = prepareGraphResetsDataFromCursor( resetsCursor );
        
        		drawGraph(graphToDrawArrayList, resetsToDrawArrayList);
        }
        public void generateTheGraphDataFromSettings(){
        	
        		ArrayList<AlarmsPlayedDataObject> graphToDrawArrayList = Utils.generateArrayListOfAlarmCuesFromSettings(getActivity());
        		ArrayList<AlarmsPlayedDataObject> resetsToDrawArrayList = new ArrayList<AlarmsPlayedDataObject>();	// cause obviously no resets clicked
        	
        		drawGraph(graphToDrawArrayList, resetsToDrawArrayList);
        }
        
        //====================================================================================
        // PREPARE DATA
        //====================================================================================
        
        private ArrayList<AlarmsPlayedDataObject> prepareGraphDataFromCursor ( Cursor cursor ){
        		highestPointOnYAxis = 0.0;
        		ArrayList<AlarmsPlayedDataObject> graphToDrawArrayList = new ArrayList<AlarmsPlayedDataObject>();
            	cursor.moveToFirst();
            	while (cursor.isAfterLast() == false) 
            	{
            		AlarmsPlayedDataObject apdo = new AlarmsPlayedDataObject();
            		apdo.unixTime = cursor.getLong( cursor.getColumnIndex( GraphsTable.COLUMN_NAME_UNIX_TIME ));
            		apdo.volume = cursor.getString( cursor.getColumnIndex( GraphsTable.COLUMN_NAME_VOLUME ));
            		apdo.brightness = cursor.getString( cursor.getColumnIndex( GraphsTable.COLUMN_NAME_BRIGHTNESS ));
            		if ( Double.parseDouble(apdo.volume) >= highestPointOnYAxis )
            		{
            			highestPointOnYAxis = Double.parseDouble(apdo.volume);
            		}
            		if ( Double.parseDouble(apdo.brightness) >= highestPointOnYAxis )
            		{
            			highestPointOnYAxis = Double.parseDouble(apdo.brightness);
            		}
            		graphToDrawArrayList.add(apdo);
            	    cursor.moveToNext();
            	}
            				Utils.boogieLog("GraphFragment", "prepareGraphDataFromCursor(cursor)" , "highestPointOnYAxis = " + highestPointOnYAxis );
            	return graphToDrawArrayList;
        }
        
        private ArrayList<AlarmsPlayedDataObject> prepareGraphResetsDataFromCursor ( Cursor cursor ){
        		ArrayList<AlarmsPlayedDataObject> resetsToDrawArrayList = new ArrayList<AlarmsPlayedDataObject>();
        		if ( cursor == null ) return resetsToDrawArrayList;
            	
            	cursor.moveToFirst();
            	while (cursor.isAfterLast() == false) 
            	{
            		AlarmsPlayedDataObject apdo = new AlarmsPlayedDataObject();
            		apdo.unixTime = cursor.getLong( cursor.getColumnIndex( GraphsTable.COLUMN_NAME_UNIX_TIME ));
            		resetsToDrawArrayList.add(apdo);
            	    cursor.moveToNext();
            	}
            	return resetsToDrawArrayList;
        }
        
        
      //====================================================================================
      //====================================================================================
        
        //====================================================================================
        // DRAW GRAPH
        //====================================================================================
        
      //====================================================================================
      //====================================================================================
        
    private void drawGraph( ArrayList<AlarmsPlayedDataObject> graphToDrawArrayList , ArrayList<AlarmsPlayedDataObject> resetsToDrawArrayList){

    		// filter out null AlarmsPlayedDataObjects		
    		ArrayList<GraphViewData> filteredSoundGraphDataArray = new ArrayList<GraphViewData>();
    		ArrayList<GraphViewData> filteredLightGraphDataArray = new ArrayList<GraphViewData>();
    		for ( int i = 0; i < graphToDrawArrayList.size(); i++)
    		{
    			AlarmsPlayedDataObject apdo = graphToDrawArrayList.get(i);
    			if ( apdo != null )
    			{
    				if (apdo.unixTime > 0  && apdo.volume != null)
    				{
    					GraphViewData gvd = new GraphViewData(apdo.unixTime ,Double.parseDouble(apdo.volume) * 100 );
    					filteredSoundGraphDataArray.add(gvd);
    				}
    				if (apdo.unixTime > 0  && apdo.brightness != null)
    				{
    					GraphViewData gvd = new GraphViewData(apdo.unixTime ,Double.parseDouble(apdo.brightness) * 100 );
    					filteredLightGraphDataArray.add(gvd);
    				}
    			}
    		}
    		
    		// make the graphSoundDataArray		
    		GraphViewData[] graphSoundDataArray = new GraphViewData[filteredSoundGraphDataArray.size()];
    		for ( int j=0; j < filteredSoundGraphDataArray.size(); j++)
    		{
    			graphSoundDataArray[j] = filteredSoundGraphDataArray.get(j);
    		}
    		
    		// make the graphLightDataArray		
    		GraphViewData[] graphLightDataArray = new GraphViewData[filteredLightGraphDataArray.size()];
    		for ( int j=0; j < filteredLightGraphDataArray.size(); j++)
    		{
    			graphLightDataArray[j] = filteredLightGraphDataArray.get(j);
    		}
    		
    		// set the graph labels
    		final java.text.DateFormat dateTimeFormatter = DateFormat.getTimeFormat(getActivity());
    		GraphView graphView = new LineGraphView(getActivity(),"") {
    			@Override
    			protected String formatLabel(double value, boolean isValueX) {
    				if (isValueX) {
    					return dateTimeFormatter.format(new Date((long) value*1000));			
    				}else{
    					//return Long.toString(Math.round( value * 100)) + "%";
    					return Long.toString(Math.round( value )) + "%";
    					//return super.formatLabel(value, isValueX);
    				}
    			}
    		};
    		
    		graphView.setBackgroundColor(0);
    		
    		// set graph line colors
    		int lightColorInt = Utils.getColorIntFromLightColorPreference( getActivity() );
    		GraphViewStyle soundGraphStyle = new GraphViewStyle( 0xff00CC00, 5);	// green
    		GraphViewStyle lightGraphStyle = new GraphViewStyle( lightColorInt , 2 );	//0xaaffffff,2);
    		GraphViewStyle resetsGraphStyle = new GraphViewStyle( 0xaaaa0000,15);
    		
    		// THE SOUND LINE
    		String soundName = Utils.getStringPreference(getActivity(), SettingsActivity.REM_SOUND_PREF, "tibetanbell");
    		GraphViewSeries soundGraphSeries = new GraphViewSeries( "Sound : " + soundName, soundGraphStyle, graphSoundDataArray );
    		graphView.addSeries(soundGraphSeries);
    		
    		// THE LIGHT LINE
    		String lightColor = Utils.getStringPreference(getActivity(), SettingsActivity.REM_COLOR_PREF, "white");
    		GraphViewSeries lightGraphSeries = new GraphViewSeries( "Light Color : " + lightColor, lightGraphStyle, graphLightDataArray );
    		graphView.addSeries(lightGraphSeries);

    		// the reset line(s)
    		for ( int k=0; k < resetsToDrawArrayList.size(); k++)
    		{			
    			GraphViewData[] resetDataArray = new GraphViewData[2];
    			AlarmsPlayedDataObject startApdo = graphToDrawArrayList.get(0);
    			AlarmsPlayedDataObject resetApdo = resetsToDrawArrayList.get(k);
    			AlarmsPlayedDataObject endApdo = graphToDrawArrayList.get(graphToDrawArrayList.size()-1);
    			
    			// make these resets lines go vertically 
    			resetDataArray[0] = new GraphViewData(resetApdo.unixTime ,Double.parseDouble( "0" ));
    			resetDataArray[1] = new GraphViewData(resetApdo.unixTime ,Double.parseDouble(String.valueOf(highestPointOnYAxis * 100 )));
    			
    			GraphViewSeries resetGraphSeries = new GraphViewSeries( "Resets Clicked", resetsGraphStyle, resetDataArray );
    			graphView.addSeries(resetGraphSeries);
    		}
    		
    		
    		
    		// set legend  
//    		graphView.setShowLegend(true);  
//    		graphView.setLegendAlign(LegendAlign.BOTTOM);  
//    		graphView.setLegendWidth(250);  
    		
    		graphView.invalidate(); 
    		
    		LinearLayout graphLayout = new LinearLayout(getActivity());
    		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
    				LinearLayout.LayoutParams.MATCH_PARENT,
    				LinearLayout.LayoutParams.MATCH_PARENT);	//300);
    		graphLayout.setLayoutParams(lp);
    		graphLayout.setBackgroundResource(R.drawable.greenborder);
    		// add it to view
    		graphLayout.addView(graphView);
    		
    		// TODO heading
//    		TextView graphLabelTextView = makeLabelTextView("REM Alarms Played Graph :", null);
//    		parentLayout.addView(graphLabelTextView);
    		parentLayout.removeAllViews();
    		parentLayout.addView(graphLayout);
    	}
        
        
   
        
        
        
        //====================================================================================
    	//	Getters && Setters
    	//====================================================================================
    		
    		private Singleton sing(){
    			return Singleton.getInstance();
    		}
        
}