package app.lucidalarm2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.lucidalarm2.ContractSchema.CuesTable;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.Toast;

public class ExportDreamRecordsActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

	public static final int EXPORT_LOADER = 7575;
	
	private ArrayList<String> allDreamsAsXmlString = new ArrayList<String>();
	private ArrayList<String> allDreamsAsTextDoc = new ArrayList<String>();
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        getSupportLoaderManager().initLoader(EXPORT_LOADER, null, this);	 //  LOAD THE CURSOR
		}
	 
	 
	 // okay everything below is on the chopping block
	 // now we simply want to :
	 // 1) get a cursor of diary records
	 // 2) walk down cursor rows
	 // 3) creating an xml node for each column
	 // 4) and populating it with the columns value
	 // 
	 // note : the entire above should all be done with one loop

	 
	
	//====================================================================================
    //====================================================================================
    // LOADER CALL BACKS
    //====================================================================================
    //====================================================================================
      
    	
    	  // A callback method invoked by the loader when initLoader() is called 
        @Override
        public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {

        	if ( loaderId == EXPORT_LOADER )
        	{
        		Uri uri = Uri.parse( ContentProviderLA.CONTENT_PROVIDER_URI + "/" + "export") ;
                CursorLoader c = new CursorLoader(this, uri, null, null, null, null);
                
                		Utils.boogieLog("ExportDreamRecordsActivity", "onCreateLoader()", "EXPORT_LOADER fetching all rows all columns..." );
            	return c;
        	}
        	
        	return null;
            
        }
     
        /** A callback method, invoked after the requested content provider returned all the data */
        @Override
        public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
            
        	//=====================================================
        	turnTheDiaryRecordsCursorIntoAnXMLString( cursor );
        	turnTheDiaryRecordsCursorIntoATextDocument(cursor);
        	//=====================================================
        	try {        		
      		
            	saveDreamRecordsToDiskAndSendAsEmailAttachment();
        		
        	}catch ( IOException e) {
        		Toast.makeText(this, "There has been an I/O error writing the dream records to disk for export." , Toast.LENGTH_LONG ).show();
        		Utils.boogieLog("ExportDreamRecordsActivity", "turnTheDiaryRecordsCursorIntoAnXMLString()", " There has been an I/O error writing the dream records to disk for export " );
        		finish();
        	}

        	
        	Utils.boogieLog("ExportDreamRecordsActivity", "onLoadFinished()", " blah " );
        }
     
        @Override
        public void onLoaderReset(Loader<Cursor> arg0) {
        	// dont think we have to do anything here
        }
	 
	 
	 
        //==================================================================================== 
        // XML STRING FACTORY
        //====================================================================================
	 
        public void turnTheDiaryRecordsCursorIntoAnXMLString( Cursor cursor ) {
        	
        	if ( cursor == null )
        	{
        		Toast.makeText(this, "There are no dream records to export !" , Toast.LENGTH_LONG ).show();
        		finish();
        		return;
        	}
        	
        	ArrayList<String> allDreams = new ArrayList<String>();
        	        	        	
        	if (cursor.moveToFirst()){
				   do{
					   String eachDream = "<Dream>";
					   int i = 0;	// which is the hidden _ID field 
					   for ( i = 1; i < cursor.getColumnCount(); i++ )
					   {
						   String columnValue = cursor.getString(i);
						   String columnName = cursor.getColumnName(i);
						   eachDream += "<" + columnName + ">";
						   eachDream += columnValue;
						   eachDream += "</" + columnName + ">";
					   }
					   eachDream += "</Dream>";
					   allDreams.add(eachDream);
				   }while(cursor.moveToNext());
				}
        	        	
        				Utils.boogieLog("ExportDreamRecordsActivity", "turnTheDiaryRecordsCursorIntoAnXMLString()", " xmlString created " );
        				
//        	try {        		
//        		saveXmlStringExportingDreamRecords( allDreams );
//        		
        		allDreamsAsXmlString = allDreams;
        		
//        	}catch ( IOException e) {
//        		Toast.makeText(this, "There has been an I/O error writing the dream records to disk for export." , Toast.LENGTH_LONG ).show();
//        		Utils.boogieLog("ExportDreamRecordsActivity", "turnTheDiaryRecordsCursorIntoAnXMLString()", " There has been an I/O error writing the dream records to disk for export " );
//        	}
        }
        
        public void turnTheDiaryRecordsCursorIntoATextDocument( Cursor cursor ) {
        	
        	if ( cursor == null )
        	{
        		Toast.makeText(this, "There are no dream records to export !" , Toast.LENGTH_LONG ).show();
        		finish();
        		return;
        	}    
        	ArrayList<String> allDreams = new ArrayList<String>();
        	
        	if (cursor.moveToFirst()){
				   String eachDream = "Dream Records" + "\n"+"\n";
				   do{

					   int i = 0;	// which is the hidden _ID field 
					   for ( i = 1; i < cursor.getColumnCount(); i++ )
					   {
						   String columnValue = cursor.getString(i);
						   String columnName = cursor.getColumnName(i);
						   if ( columnName.equals("title") )
						   {
							   columnName = "Title";
							   eachDream = addColumnAndValue(eachDream, columnValue, columnName);
						   }
						   if ( columnName.equals("notes") )
						   {
							   columnName = "Notes";
							   eachDream = addColumnAndValue(eachDream, columnValue, columnName);
						   }
						   if ( columnName.equals("other") )
						   {
							   columnName = "Other";
							   if ( columnValue != null && ! columnValue.equals("null") && columnValue.length() > 0 )
							   {
								   // don't add it
							   }else{
								   eachDream = addColumnAndValue(eachDream, columnValue, columnName);
							   }
						   }
						   if ( columnName.equals("lucid") )
						   {
							   columnName = "Lucid";
							   eachDream = addColumnAndValue(eachDream, columnValue, columnName);							   
						   }
						   if ( columnName.equals("unixTime") )
						   {
							   columnName = "Date";
							   long dv = Long.valueOf(columnValue)*1000;// its need to be in milisecond
							   Date df = new java.util.Date(dv);
							   String vv = new SimpleDateFormat("MM dd, yyyy hh:mma").format(df);
							   columnValue = vv;
							   eachDream = addColumnAndValue(eachDream, columnValue, columnName);							   
						   }
						   						   
					   }
					   eachDream += "\n";
					   allDreams.add(eachDream);
				   }while(cursor.moveToNext());
				}
        	
        				Utils.boogieLog("ExportDreamRecordsActivity", "turnTheDiaryRecordsCursorIntoATextDocument()", " text doc created " );
        				
        	//try {
        		//saveXmlStringExportingDreamRecords( allDreams );
        		allDreamsAsTextDoc = allDreams;
//        	}catch ( IOException e) {
//        		Toast.makeText(this, "There has been an I/O error writing the dream records to disk for export." , Toast.LENGTH_LONG ).show();
//        		Utils.boogieLog("ExportDreamRecordsActivity", "turnTheDiaryRecordsCursorIntoAnXMLString()", " There has been an I/O error writing the dream records to disk for export " );
//        	}
        }
        
        private String addColumnAndValue(String eachDream, String columnValue, String columnName ){
        	eachDream += "" + columnName + " : ";
			   eachDream += columnValue + "\n";
			   return eachDream;
			   
        }
        //==================================================================================== 
        // SAVE STRING TO DISK
        //====================================================================================
        
        private void saveDreamRecordsToDiskAndSendAsEmailAttachment() throws FileNotFoundException , IOException {	
			
        	// make the directory
        	File root = android.os.Environment.getExternalStorageDirectory(); 
		    File dir = new File (root.getAbsolutePath() + sing().pathToExportedDreamsXmlFile);
		    dir.mkdirs();
		    
		    
		    File xmlFile = new File(dir, sing().fileNameOfExportedDreamsXmlFile);
		    FileOutputStream fosXml = new FileOutputStream(xmlFile);		
			writeStringArrayToFile(allDreamsAsXmlString, fosXml);
			
			MediaScannerConnection.scanFile(this, new String[] { xmlFile.getAbsolutePath() }, null, null); // a fix to make newly created files show up on PC windows explorer
			
			File docFile = new File(dir, sing().fileNameOfExportedDreamsTextDocFile);
		    FileOutputStream fosDoc = new FileOutputStream(docFile);		
			writeStringArrayToFile(allDreamsAsTextDoc, fosDoc);
			
			MediaScannerConnection.scanFile(this, new String[] { docFile.getAbsolutePath() }, null, null); // a fix to make newly created files show up on PC windows explorer
			
			sendEmailWithDreamRecordsAttachments(docFile,xmlFile);

		}
        
        private void writeStringArrayToFile( ArrayList<String> stringsArray, FileOutputStream fos){
        	try {        		
        		for (String string: stringsArray){
    				fos.write(string.getBytes());
    				fos.flush();
    			}
            	fos.close();
        	}catch ( IOException e) {
        		Toast.makeText(this, "There has been an I/O error writing the dream records to disk for export." , Toast.LENGTH_LONG ).show();
        		finish();
        		Utils.boogieLog("ExportDreamRecordsActivity", "turnTheDiaryRecordsCursorIntoAnXMLString()", " There has been an I/O error writing the dream records to disk for export " );
        	}
        }
        
        
        //================================================================================
        // EMAIL
        //================================================================================
        
        private void sendEmailWithDreamRecordsAttachments( File docFile, File xmlFile ) {
        	        	
			  String subject = "Lucid Alarm Dream Diary Documents";
			 String message = "Attached is a text document of all dreams recorded into the Lucid Alarm 2.0 dream diary. Also attached is an XML file named 'exportedDxXml.txt' which you can save into the 'Download' folder of another Android device and use to import these dreams into another Lucid Alarm 2.0 app.\n\nStay lucid !";
			  
			//need to "send multiple" to get more than one attachment
			    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
			    emailIntent.setType("text/plain");
			    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, 
			        new String[]{});
			    emailIntent.putExtra(android.content.Intent.EXTRA_CC, 
			        new String[]{});
			    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); 
			    emailIntent.putExtra(Intent.EXTRA_TEXT, message);
			    //has to be an ArrayList
			    ArrayList<Uri> uris = new ArrayList<Uri>();
			    //convert from paths to Android friendly Parcelable Uri's
//			    for (String file : filePaths)
//			    {			        
			        Uri docUri = Uri.fromFile(docFile);
			        uris.add(docUri);
			        Uri xmlUri = Uri.fromFile(xmlFile);
			        uris.add(xmlUri);
			        
			        
//			    }
			    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			    finish();
        	
        }
        
        
        
        
        
        
      //====================================================================================
      //	Getter Setter
      //====================================================================================
      private Singleton sing(){
      	return Singleton.getInstance();
      }
	 
	 /*
	public void makeXmlStringOfAllDreamRecords(){
	
			// 1) load up all the dreams and store them in our array
			// 2) combine them all into one humungous xml file
			// 3) write said xml file to disk
			
		sing().logg("graw", "beginning dream records export");
			
			if ( sing().allDreamRecordsLoadedArrayList == null || sing().allDreamRecordsLoadedArrayList.isEmpty())
			{
				Intent intent = new Intent(this, LoadDreamRecordsActivity.class);
				this.startService(intent);
			}
			
			String xmlString = "<dreamRecords>";
			
			
			// loop through the records and make one giant xml file
			for ( int i=0; i < sing().allDreamRecordsLoadedArrayList.size(); i++)
			{
				DreamRecordDataObject rr = sing().allDreamRecordsLoadedArrayList.get(i);
				// NOTE : below exports ALL dream records
			    public ArrayList<AlarmsPlayedDataObject> remAlarmsPlayedArrayList = new ArrayList<AlarmsPlayedDataObject>(); --done
				public String sound = ""; --done
				public String notes = ""; -- done
				public String dreamTitle = ""; --done
				public Long unixTime; --done
				public ArrayList<AlarmsPlayedDataObject> resetsClickedArrayList = new ArrayList<AlarmsPlayedDataObject>(); --done
				public int lucidityScale = 0; --done
				public boolean lucid = false; --done
				
				xmlString += "<dream>";
				
					xmlString += "<notes>";
					xmlString += rr.dream;
					xmlString += "</notes>";
					xmlString += "<dreamTitle>";
					xmlString += rr.dreamTitle;
					xmlString += "</dreamTitle>";
					xmlString += "<unixTime>";
					xmlString += rr.unixTime.toString();
					xmlString += "</unixTime>";
					xmlString += "<lucid>";
					xmlString += ( rr.lucid ? "true" : "false");
					xmlString += "</lucid>";
					xmlString += "<sound>";
					xmlString += rr.sound;
					xmlString += "</sound>";
					xmlString += "<lucidityScale>";
					xmlString += Integer.toString(rr.lucidityScale);
					xmlString += "</lucidityScale>";
				
					if ( rr.remAlarmsPlayedArrayList != null && rr.remAlarmsPlayedArrayList.isEmpty() == false)
					{
						xmlString += "<remAlarmsPlayedArrayList>";
						for ( int j=0; j < rr.remAlarmsPlayedArrayList.size(); j++)
						{
							AlarmsPlayedDataObject apdo = rr.remAlarmsPlayedArrayList.get(j);
							//public String volume;
							//public long unixTime;
							xmlString += "<volume>";
							xmlString += apdo.volume;
							xmlString += "</volume>";
							xmlString += "<unixTime>";
							Long unixTimeLong = apdo.unixTime;
							xmlString += unixTimeLong.toString();
							xmlString += "</unixTime>";
						}
						xmlString += "</remAlarmsPlayedArrayList>";
					}
					if ( rr.resetsClickedArrayList != null && rr.resetsClickedArrayList.isEmpty() == false)
					{
						xmlString += "<resetsClickedArrayList>";
						for ( int k=0; k < rr.resetsClickedArrayList.size(); k++)
						{
							AlarmsPlayedDataObject apdor = rr.resetsClickedArrayList.get(k);
							//public String volume;
							//public long unixTime;
							xmlString += "<volume>";
							xmlString += apdor.volume;
							xmlString += "</volume>";
							xmlString += "<unixTime>";
							Long unixTimeLong = apdor.unixTime;
							xmlString += unixTimeLong.toString();
							xmlString += "</unixTime>";
						}
						xmlString += "</resetsClickedArrayList>";
					}
					
				xmlString += "</dream>";
			}
			
			xmlString += "</dreamRecords>";
			
			try{
				saveXmlStringExportingDreamRecords(xmlString);
			}
			catch ( Exception e){
				sing().logg("graw", "ERROR saving xml file of dream records");
			}
			
		}
		
		
		*/
        
        
        
		
		
		
	 
		
}
