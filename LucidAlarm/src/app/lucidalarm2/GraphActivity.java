
package app.lucidalarm2;


import android.os.Bundle; 
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.jjoe64.graphview.*;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;
import android.widget.*;
import java.util.ArrayList;
import java.util.Date;

import android.text.format.*;
import java.io.*;
import android.content.*;


public class GraphActivity extends FragmentActivity {

	public static String SAVE_THE_GRAPH = "saveTheGraph";
	
	//public String FILENAME = "DreamRecords";
	
	/*
	 * NOTE : This class is being completely re-tooled to be a standalone module
	 * 			that manages its own database manipulations
	 * 			and draws its graphs
	 * 
	 * 			THIS CLASS IS ON THE CHOPPING BLOCK !!
	 */
	
	//====================================================================================
	//	STATE CHANGE HANDLERS
	//====================================================================================
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.graph_layout); 
	}
    
 

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.settings_layout, menu);
//        return true;
//    }
    
  
  	//	On Resume
  	//====================================================================================
    @Override
    protected void onResume() {
    	/*
    	super.onResume();
    	Singleton.getInstance().logg("enter onResume GraphActivity()");     	
		if ( sing().graphOrNotesDataDirty )
		{
			try
			{
				saveDirtyGraphData();	
			}
			catch (IOException ioe)
			{
				Toast.makeText(this, "IOException on save : " + ioe, Toast.LENGTH_LONG).show();
			}
			catch ( Exception e)
			{
				sing().logg("graw", "No graph to save today ");
			}
		}
		try
		{
			showLoadButtonIfThereIsDataToLoad(); // also shows/hides notes button
		}
		catch(ClassNotFoundException cnfe)
		{			
		}
		catch (IOException ioe)
		{			
		}
		
		drawGraph();   
		*/ 
    };
	
	//====================================================================================
	//	Button Handlers
	//====================================================================================
	public void onGraphNotesButtonClick(View view){
		Intent intent = new Intent(this, DiaryActivity.class); 
		startActivity(intent);		
	}
	public void onLoadGraphButtonClick(View view) throws FileNotFoundException , IOException, ClassNotFoundException {

//		sing().allDreamRecordsLoadedArrayList = loadAllDreamRecords();
//		Intent intent = new Intent(this, RecordsActivity.class);
//		startActivity(intent);
	}
    
	//====================================================================================
	//	Draw Graph
	//====================================================================================
	
	private void drawGraph(){
/*
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph_linearLayout);
		layout.removeAllViews();
		
		ArrayList<AlarmsPlayedDataObject> graphToDrawArrayList;
		ArrayList<AlarmsPlayedDataObject> resetsToDrawArrayList;
		DreamRecordDataObject DreamRecordToDraw; 
		
		// choose which graph to draw and its title		
		if ( sing().dreamRecordSelected != null && sing().dreamRecordSelected.remAlarmsPlayedArrayList != null && sing().dreamRecordSelected.remAlarmsPlayedArrayList.isEmpty() == false)
		{
			graphToDrawArrayList = sing().dreamRecordSelected.remAlarmsPlayedArrayList;
			resetsToDrawArrayList = sing().dreamRecordSelected.resetsClickedArrayList;
			DreamRecordToDraw = sing().dreamRecordSelected; 
		}	
		else
		{
			sing().logg("graw","No REM Graph To Draw");
			hideGraphLabels();
			hideNotesButton(); 
			return;
		}

		// filter out null AlarmsPlayedDataObjects		
		ArrayList<GraphViewData> filteredGraphDataArray = new ArrayList<GraphViewData>();
		for ( int i = 0; i < graphToDrawArrayList.size(); i++)
		{
			AlarmsPlayedDataObject apdo = graphToDrawArrayList.get(i);
			if ( apdo != null )
			{
				if (apdo.unixTime > 0  && apdo.volume != null)
				{
					GraphViewData gvd = new GraphViewData(apdo.unixTime ,Double.parseDouble(apdo.volume));
					filteredGraphDataArray.add(gvd);
				}
			}
		}
		// make the graphDataArray		
		GraphViewData[] graphDataArray = new GraphViewData[filteredGraphDataArray.size()];
		for ( int j=0; j < filteredGraphDataArray.size(); j++)
		{
			graphDataArray[j] = filteredGraphDataArray.get(j);
		}
		
		setGraphLabels(DreamRecordToDraw);		
		
		// set the graph labels
		final java.text.DateFormat dateTimeFormatter = DateFormat.getTimeFormat(this);
		GraphView graphView = new LineGraphView(this,"") {
			@Override
			protected String formatLabel(double value, boolean isValueX) {
				if (isValueX) {
					return dateTimeFormatter.format(new Date((long) value*1000));			
				}else{
					return Long.toString(Math.round( value * 100)) + "%";
					//return super.formatLabel(value, isValueX);
				}
			}
		};
		
		
		graphView.setBackgroundColor(0);
		
		// set graph line colors
		GraphViewStyle mainGraphStyle = new GraphViewStyle(0xff00CC00,2);
		GraphViewStyle resetsGraphStyle = new GraphViewStyle(0xaaaa0000,5);
		
		// the main graph line
		GraphViewSeries mainGraphSeries = new GraphViewSeries( "graw", mainGraphStyle, graphDataArray );
		graphView.addSeries(mainGraphSeries);

		// the reset line(s)
		for ( int k=0; k < resetsToDrawArrayList.size(); k++)
		{			
			GraphViewData[] resetDataArray = new GraphViewData[2];
			AlarmsPlayedDataObject startApdo = graphToDrawArrayList.get(0);
			AlarmsPlayedDataObject resetApdo = resetsToDrawArrayList.get(k);
			AlarmsPlayedDataObject endApdo = graphToDrawArrayList.get(graphToDrawArrayList.size()-1);

			resetDataArray[0] = new GraphViewData(startApdo.unixTime ,Double.parseDouble(resetApdo.volume));
			//resetDataArray[1] = new GraphViewData(resetApdo.unixTime ,Double.parseDouble(resetApdo.volume));			
			resetDataArray[1] = new GraphViewData(endApdo.unixTime ,Double.parseDouble(resetApdo.volume));
			
			GraphViewSeries resetGraphSeries = new GraphViewSeries( "graw", resetsGraphStyle, resetDataArray );
			graphView.addSeries(resetGraphSeries);
		}

		graphView.invalidate();

		// add it to view
		layout.addView(graphView);
		*/
	}
	
	/*
	private void setGraphLabels(DreamRecordDataObject rr){
		
		//set the graph title
		String title = "Title: ";
		title += rr.dreamTitle;
		TextView ttv = (TextView) findViewById(R.id.graphTitle_textView);
		ttv.setText(title);
		
		//set the sound label
		String sound = "Sound: ";
		sound += rr.sound;
		TextView stv = (TextView) findViewById(R.id.graphSound_textView);
		stv.setText(sound);
		
		//set the lucidity scale label
		String lucidity = "";
		int lucidScale = rr.lucidityScale;
		if ( lucidScale > 0 )
		{
			lucidity = "Lucid: " + Integer.toString(lucidScale) + "%";
		}
		TextView ltv = (TextView) findViewById(R.id.graphLucidityScale_textView);
		ltv.setText(lucidity);
		
	}
	*/
	/*
	private void hideGraphLabels(){
		//set the graph title
		String title = "No graph to display";
		TextView ttv = (TextView) findViewById(R.id.graphTitle_textView);
		ttv.setText(title);
				
		//set the sound label
		String sound = "";
		TextView stv = (TextView) findViewById(R.id.graphSound_textView);
		stv.setText(sound);
			
		//set the lucidity scale label
		String lucidity = "";
		TextView ltv = (TextView) findViewById(R.id.graphLucidityScale_textView);
		ltv.setText(lucidity);
	}
	*/
	//====================================================================================
	//	Save data
	//====================================================================================
	
	public void saveTodaysGraph() throws FileNotFoundException , IOException , ClassNotFoundException {
		/*
		sing().logg("saving todays graph");
		//presupposes there is a today's graph !
		if ( sing().newDreamRecord == null)
		{
			sing().logg("graw", "no today graph to save EXCEPTION");
			return;
		}
		
		giveTodaysGraphATitle();
		
		ArrayList<DreamRecordDataObject> loadedRecords = loadAllDreamRecords();
		
		loadedRecords.add( sing().newDreamRecord );  // adds todays
		
		saveDreamRecords(loadedRecords);  // saves todays with all

		sing().allDreamRecordsLoadedArrayList = loadedRecords;		
		
		sing().dreamRecordSelected = sing().newDreamRecord; // selects todays
		
		sing().newDreamRecord = null;  // wipes graph and closes record
		*/
	}
	/*
	private void saveDreamRecords(ArrayList<DreamRecordDataObject> records) throws FileNotFoundException , IOException {	
		
		FileOutputStream fos = openFileOutput(sing().filename,Context.MODE_PRIVATE);
		ObjectOutputStream outStream;
		DreamRecordDataObject rr;

			try{
				outStream = new ObjectOutputStream(fos);

				for ( int i=0; i < records.size(); i++)
				{
					rr = records.get(i);
					outStream.writeObject(rr);
				}
				
				outStream.close();
				sing().logg("graw", "data saved");

			}catch (IOException e) {
				Toast.makeText(this, "Write IO exception", Toast.LENGTH_LONG).show();
			}
	}
	*/
	//====================================================================================
	//	Load data
	//====================================================================================
	
	private void giveTodaysGraphATitle(){
//		DreamRecordDataObject rr = sing().newDreamRecord;
//		Date date = new Date(rr.unixTime*1000L);
//		String title = sing().dateOnlyFormat.format(date);		
//		if ( rr.sound.length() > 0 )
//		{
//			title += " - " + rr.sound;
//		}
		
		//sing().newDreamRecord.dreamTitle = sing().defaultdreamTitle;
		//sing().newDreamRecord.dreamTitle = title;
	}
	
	//====================================================================================
	//	Load all records
	//====================================================================================
	
	/*
	private ArrayList<DreamRecordDataObject> loadAllDreamRecords () throws FileNotFoundException , IOException, ClassNotFoundException {
		
		ArrayList<DreamRecordDataObject> readList = new ArrayList<DreamRecordDataObject>();
		FileInputStream inFile;
		ObjectInputStream inStream;
		DreamRecordDataObject rr;
		
		try{
			inFile = openFileInput(sing().filename);	
		}
		catch(FileNotFoundException fnfe)
		{
			Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show();
			return readList;
		}

		if ( inFile == null )
		{
			sing().logg("graw", "No loaded data found");
			return readList;
		}
		inStream = new ObjectInputStream(inFile);

		try{
			while (true)
			{
				rr = (DreamRecordDataObject) inStream.readObject();
				readList.add(rr);
			}
		}
		catch(FileNotFoundException e)
		{
			Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show();
		}
		catch ( EOFException eof )
		{
			inStream.close();
			sing().logg("graw", "data loaded : " + readList.size());
		}
		catch (IOException f)
		{
			Toast.makeText(this, "Read I/O exception : " + f, Toast.LENGTH_LONG).show();
		}
		return readList;
	}
	
	//====================================================================================
	//	Miscellaneous function
	//====================================================================================
	
	private void saveDirtyGraphData() throws FileNotFoundException , IOException {
		
		sing().logg("saving graph due to dirty graph data");
		saveDreamRecords(sing().allDreamRecordsLoadedArrayList);		
		sing().graphOrNotesDataDirty = false;
	}
	
	private void showLoadButtonIfThereIsDataToLoad() throws FileNotFoundException , IOException, ClassNotFoundException {
		ArrayList<DreamRecordDataObject> loadableRecords = loadAllDreamRecords();
		if ( loadableRecords != null )
		{
			if ( loadableRecords.size() > 0 )
			{
				showLoadButton();
				showNotesButton();
				return;
			}
		}
		hideLoadButton();
		hideNotesButton();
	}
	private void showLoadButton(){
		ImageButton b = (ImageButton) findViewById(R.id.loadGraph_imageButton);
		b.setVisibility(View.VISIBLE);
	}
	private void hideLoadButton(){
		ImageButton b = (ImageButton) findViewById(R.id.loadGraph_imageButton);
		b.setVisibility(View.INVISIBLE);
	}
	private void showNotesButton(){
		ImageButton b = (ImageButton) findViewById(R.id.graphNotes_imageButton);
		b.setVisibility(View.VISIBLE);
	}
	private void hideNotesButton(){
		ImageButton b = (ImageButton) findViewById(R.id.graphNotes_imageButton);
		b.setVisibility(View.INVISIBLE);
	}

*/
	//====================================================================================
	//	Generate Mock Data
	//====================================================================================
	/*
	private void generateMockGraphData(){
		for ( int i = 0; i < 50; i++)
		{
			AlarmsPlayedDataObject apdo = new AlarmsPlayedDataObject();
			apdo.timePlayed = Integer.toString(i);
			apdo.volume = Double.toString(Math.random()*100);
			sing().remAlarmsPlayedArrayList.add(apdo);
		}
	}
	*/
	private Singleton sing(){
		return Singleton.getInstance();
	}
	
	// ON THE CHOPPING BLOCK
	//==============================================
	/*
	 * @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String intentMessage = intent.getStringExtra(MainActivity.INTENT_STRING_MESSAGE);
		if (intentMessage != null )
		{
			if ( intentMessage.equals(GraphActivity.SAVE_THE_GRAPH))
			{
				try
				{
					saveTodaysGraph();
				}
				catch (ClassNotFoundException cnfe)
				{
					Toast.makeText(this, "ClassNotFound on save : " + cnfe, Toast.LENGTH_LONG).show();
				}
				catch (IOException ioe)
				{
					Toast.makeText(this, "IOException on save : " + ioe, Toast.LENGTH_LONG).show();
				}
				catch ( Exception e)
				{
					sing().logg("graw", "No graph to save today ");
				}
				finish();
			}
        }
		setContentView(R.layout.graph_layout); 
	}
	 */
	
}
