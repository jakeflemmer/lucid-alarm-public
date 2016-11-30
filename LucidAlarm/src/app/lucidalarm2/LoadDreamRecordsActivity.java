//package app.lucidalarm2;
//
//import java.io.EOFException;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.util.ArrayList;
//
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.widget.Toast;
//
///*
// * I believe this class is no longer needed as all of its function and responsibilities have been consumed by RecordsActivity
// */
//
//public class LoadDreamRecordsActivity extends FragmentActivity {
//	
//	public static int LOAD_RECORDS_BEFORE_DISPLAYING_LIST = 12345;
//
//	 @Override
//	    public void onCreate(Bundle savedInstanceState) {
//	        super.onCreate(savedInstanceState);
//	        try{
//	        	//sing().allDreamRecordsLoadedArrayList = loadAllDreamRecords();
//	        }
//	        catch(Exception e){
//	        	Toast.makeText(this, "ERROR loading recorded dreams", Toast.LENGTH_LONG).show();
//	        }
//	        
//			finish();
//		}
//	
//	 /*
//	private ArrayList<DreamRecordDataObject> loadAllDreamRecords () throws FileNotFoundException , IOException, ClassNotFoundException {
//		
//		ArrayList<DreamRecordDataObject> readList = new ArrayList<DreamRecordDataObject>();
//		FileInputStream inFile;
//		ObjectInputStream inStream;
//		DreamRecordDataObject rr;
//		
//		try{
//			inFile = openFileInput(sing().filename);	
//		}
//		catch(FileNotFoundException fnfe)
//		{
//			Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show();
//			return readList;
//		}
//
//		if ( inFile == null )
//		{
//			Toast.makeText(this, "No loaded data found", Toast.LENGTH_LONG).show();
//			return readList;
//		}
//		inStream = new ObjectInputStream(inFile);
//
//		try{
//			while (true)
//			{
//				rr = (DreamRecordDataObject) inStream.readObject();
//				readList.add(rr);
//			}
//		}
//		catch(FileNotFoundException e)
//		{
//			Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show();
//		}
//		catch ( EOFException eof )
//		{
//			inStream.close();
//			sing().logg("graw", "data loaded : " + readList.size());
//		}
//		catch (IOException f)
//		{
//			Toast.makeText(this, "Read data I/O exception : " + f, Toast.LENGTH_LONG).show();
//		}
//		return readList;
//	}
//
//*/
//
//	//====================================================================================
//	//	Getter Setter
//	//====================================================================================
//	private Singleton sing(){
//		return Singleton.getInstance();
//	}
//}
//
