//package app.lucidalarm2;
//
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectOutputStream;
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
//import android.widget.Toast;
//
//public class SaveDreamRecordsActivity extends FragmentActivity {
//
//	
//	 @Override
//	    public void onCreate(Bundle savedInstanceState) {
//	        super.onCreate(savedInstanceState);
//	        
////	        if ( sing().allDreamRecordsLoadedArrayList == null || sing().allDreamRecordsLoadedArrayList.isEmpty() )
////	        {
////	        	sing().logg("graw", "ERROR saving dream records - no dreams to save");
////	        	return;
////	        }
////	        
////	        ArrayList<DreamRecordDataObject> dreamRecordsToSaveArrayList = sing().allDreamRecordsLoadedArrayList;
////	       
////	        try{
////	        	//saveDreamRecords(DreamRecordsToSaveArrayList);
////	        	saveDreamRecords(dreamRecordsToSaveArrayList);
////	        }
////	        catch(Exception e){
////	        	Toast.makeText(this, "ERROR saving dream records", Toast.LENGTH_LONG).show(); 
////	        }
////	        
////			finish();
//		}
//
//	 /*
//	private void saveDreamRecords(ArrayList<DreamRecordDataObject> records) throws FileNotFoundException , IOException {	
//		
//		FileOutputStream fos = openFileOutput(sing().filename,Context.MODE_PRIVATE);
//		ObjectOutputStream outStream;
//		DreamRecordDataObject dr;
//
//			try{
//				outStream = new ObjectOutputStream(fos);
//
//				for ( int i=0; i < records.size(); i++)
//				{
//					dr = records.get(i);
//					outStream.writeObject(dr);
//				}
//				
//				outStream.close();
//				sing().logg("graw", "dream records saved successfully");
//				sing().dreamRecordsToSaveArrayList = null;
//
//			}catch (IOException e) {
//				Toast.makeText(this, "Write I/O exception", Toast.LENGTH_LONG).show();
//			}
//	}
//	
//	
//	
//	*/
//	
//	
//	
//		//====================================================================================
//		//	Getter Setter
//		//====================================================================================
//		private Singleton sing(){
//			return Singleton.getInstance();
//		}
//}
