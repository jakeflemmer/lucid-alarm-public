package app.lucidalarm2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import app.lucidalarm2.ContractSchema.DiaryTable;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class ImportDreamRecordsActivity extends FragmentActivity {
	
	// so there an xml file somewhere containing a bunch of dream records
	// 1) we need to locate that file
	// 2) we need to open /read that file
	// 3) we need to parse out all the remReocrds from the xml
	// 4) we need to write all the DreamRecords to internal storage as though they were "in" the app
	
	public static int PICKFILE_RESULT_CODE = 9872;
	
	@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
//	        try{
//	        	String xmlString = openTheXmlFileForImport();
//	        	if ( xmlString == null )
//	        	{
//	        		Utils.boogieLog("ImportDreamRecordsActivity", "onCreate()", "ERROR trying to read xml string from disk" );
//	        		finish();
//	        	}
//	        	
//	        	insertTheXmlStringIntoDiaryTableRows( xmlString );
//	        	// below two steps are the old way with dream record objects. now we just read the xml and insert it straight into db !
//	        	//ArrayList<DreamRecordDataObject> DreamRecordsArrayList = parseTheXmlFileIntoDreamRecords(xmlString);
//	        	//saveTheDreamRecords(DreamRecordsArrayList);
//	        }
//	        catch(Exception e){
//	        	sing().logg("graw", "onCreate importdreamrecordsActivity");
//	        	sing().logg("graw", "ERROR exporting dreams records");
//	        }
//			finish();
	        
	        //openTheFileBrowserToLocateTheTextFile();
	        String importedDreamsXMLString = openTheXmlFileForImport();
	        if ( importedDreamsXMLString != null ) insertTheXmlStringIntoDiaryTableRows(importedDreamsXMLString);
	        finish();
	        
		}
	 

	private String openTheXmlFileForImport() {
		
		String xmlString = "";
		
//	    File dir = new File (root.getAbsolutePath() + sing().pathToExportedDreamsXmlFile);    
		File root = android.os.Environment.getExternalStorageDirectory(); 
	    File dir = new File (root.getAbsolutePath() + "/Download/");
	    File file = new File(dir, sing().fileNameOfExportedDreamsXmlFile);
	    
	    if (file == null || file.exists() == false )
	    {
	    	Utils.boogieLog("ImportDreamRecordsActivity", "openTheXmlFileForImport()", "ERROR cannot find exported dreams xml file");
	    	Toast.makeText(this, "Unable to locate the XML file for importing. Please make sure that the file named 'exportedDxXml.txt' has been saved to your 'Download' folder.", Toast.LENGTH_LONG).show();
	    	finish();
	    	return null;
	    }
		
	  //Read text from file
	    StringBuilder text = new StringBuilder();

	    try {
	        BufferedReader br = new BufferedReader(new FileReader(file));
	        String line;

	        while ((line = br.readLine()) != null) {
	            text.append(line);
	            text.append('\n');
	        }
	        br.close();
	        Utils.boogieLog("ImportDreamRecordsActivity", "openTheXmlFileForImport", "xml string successfully read from disk" );
	    }
	    catch (IOException e) {
	        Toast.makeText(this, "There has been an error reading the import xml file from disk and the records have not been imported", Toast.LENGTH_LONG).show();
	    	Utils.boogieLog("ImportDreamRecordsActivity", "openTheXmlFileForImport", "xml string failed to be read from disk!" );
	    	finish();
	    }

	    
		xmlString = text.toString();
		return xmlString;
	}
	
	private void insertTheXmlStringIntoDiaryTableRows( String xmlString ) {
		
		// take off the opening xml tag "<LucidAlarmDreamExports>"
		String [] dreamsArray = xmlString.split("<Dream>");
		
		for ( int i = 1 ; i < dreamsArray.length; i++ )
		{
			String dreamXML = dreamsArray[i];
			ContentValues contentValues = new ContentValues();
		
			for ( int j = 1; j < DiaryTable.allColumnNamesArray.length; j++ )
			{
				String columnName = DiaryTable.allColumnNamesArray[j];
				String xmlColumnValue = dreamXML.substring(dreamXML.indexOf( "<" + columnName + ">") + ( columnName.length() + 2 ), dreamXML.indexOf( "</" + columnName + ">"));
				contentValues.put(columnName, xmlColumnValue);
			}
			// inserThis row
			getContentResolver().insert(ContentProviderLA.CONTENT_PROVIDER_URI, contentValues);
		}
		Toast.makeText(this, "Dream records have been successfully imported.", Toast.LENGTH_LONG).show();
					Utils.boogieLog("ImportDreamRecordsActivity", "insertTheXmlStringIntoDiaryTableRows", "all records inserted from xml" );
	}
	
	
//	private void openTheFileBrowserToLocateTheTextFile(){
//	Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
//    fileintent.setType("gagt/sdf");
//    try {
//        startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
//    } catch (ActivityNotFoundException e) {
//        //Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
//    }
//}

//protected void onActivityResult(int requestCode, int resultCode, Intent data) {        // 
//    if (data == null)
//        return;
//    if (requestCode == PICKFILE_RESULT_CODE){
//        if (resultCode == RESULT_OK) {
//            String FilePath = data.getData().getPath();
//            //FilePath is your file as a string
//        }
//    }
//}
	
	/*
	
	private ArrayList<DreamRecordDataObject> parseTheXmlFileIntoDreamRecords(String xmlString) {
		
		ArrayList<DreamRecordDataObject> DreamRecordsArrayList = new ArrayList<DreamRecordDataObject>();
		
		sing().logg("graw", "xmlSting : " + xmlString);
		
		int cursor = 0;
		int startIndex;
		int endIndex;
		String dreamString;
		String notesString;
		String dreamTitleString;
		String unixTimeString;
		String lucidString;
		
		DreamRecordDataObject rr;
		
		
		
//		  			xmlString += "<notes>";
//					xmlString += "</notes>";
//					xmlString += "<dreamTitle>";
//					xmlString += "</dreamTitle>";
//					xmlString += "<unixTime>";
//					xmlString += "</unixTime>";
//					xmlString += "<lucid>";
//					xmlString += "</lucid>";
		
		
		for ( int i = 0 ; i < 10000; i++ )
		{
			rr = new DreamRecordDataObject();
			
			startIndex = xmlString.indexOf("<dream>", cursor);
			if ( startIndex == -1)
			{
				break; // we are finished
			}
			endIndex = xmlString.indexOf("</dream>", cursor+7);
			
			cursor = endIndex;
			
			dreamString = xmlString.substring(startIndex, endIndex);
			
			startIndex = dreamString.indexOf("<notes>");
			endIndex = dreamString.indexOf("</notes>");
			
			notesString = dreamString.substring(startIndex+7, endIndex);
			
			startIndex = dreamString.indexOf("<dreamTitle>");
			endIndex = dreamString.indexOf("</dreamTitle>");
			
			dreamTitleString = dreamString.substring(startIndex+12, endIndex); 
			
			startIndex = dreamString.indexOf("<unixTime>");
			endIndex = dreamString.indexOf("</unixTime>");
			
			unixTimeString = dreamString.substring(startIndex+10, endIndex);
			
			startIndex = dreamString.indexOf("<lucid>");
			endIndex = dreamString.indexOf("</lucid>");
			
			lucidString = dreamString.substring(startIndex+7, endIndex); 
			
			rr.lucid = ( lucidString.equals("true") ? true : false );
			rr.dream = notesString;
			rr.dreamTitle = dreamTitleString;
			rr.unixTime = Long.parseLong(unixTimeString);
			
			DreamRecordsArrayList.add(rr);
			
			sing().logg("graw", "dream notes : " + notesString);
		}
		
		return DreamRecordsArrayList;
		
	}
	
	private void saveTheDreamRecords(ArrayList<DreamRecordDataObject> DreamRecordsArrayList){
		
		// 1) load all current records
		// 2) add these records to those loaded records
		// 3) save all of them together
		
		// note : skipping the above steps and just adding the imported list without adding it to previously recorded dreams
		
		sing().dreamRecordsToSaveArrayList = DreamRecordsArrayList;
		
		Intent intent = new Intent(this, SaveDreamRecordsActivity.class);
		startActivity(intent);
		
		sing().logg("graw", "dream records imported successfully");
	}
	*/
	//====================================================================================
	//	Getter Setter
	//====================================================================================
	private Singleton sing(){
		return Singleton.getInstance();
	}
			
			
}
