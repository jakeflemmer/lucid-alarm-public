package app.lucidalarm2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {
	
	// Context mContext;
	Activity mActivity;
	
	public static String CALL_FROM_JS = "callFromJS";

    /** Instantiate the interface and set the context */
    WebAppInterface(Activity activity) {
    	mActivity = activity;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mActivity, toast, Toast.LENGTH_SHORT).show();
    }
    @JavascriptInterface
    public void doEchoTest(String echo){
        Toast toast = Toast.makeText(mActivity, echo, Toast.LENGTH_SHORT);
        toast.show();
    }
    
    @JavascriptInterface
    public void onREMAlarmTextViewClick() {
		TimePickerFragment newFragment = new TimePickerFragment(); 
    		newFragment.fragType = "rem";
		newFragment.show( mActivity.getFragmentManager(), "timePicker");
				Utils.boogieLog("mainActivity", "ONREMALARMTEXTVIEWCLICK", "graw");
	}
    @JavascriptInterface
    public void onFinalAlarmTextViewClick() {
		TimePickerFragment newFragment = new TimePickerFragment();
		newFragment.fragType = "final";
		newFragment.show( mActivity.getFragmentManager(), "timePicker");
		Utils.boogieLog("mainActivity", "ONFINALALARMTEXTVIEWCLICK", "graw");
}
    
    @JavascriptInterface
    public void onREMPowerIconClick() {
    	mActivity.runOnUiThread(new Runnable() {
           @Override
           public void run() {              // 
              MainActivity mA = (MainActivity) mActivity;
              mA.onRemAlarmPowerIconClick();
           }
        });
     }
    
    @JavascriptInterface
    public void onFinalPowerIconClick() { 
    	mActivity.runOnUiThread(new Runnable() {
           @Override
           public void run() {              // 
              MainActivity mA = (MainActivity) mActivity;
              mA.onFinalAlarmPowerIconClick();
           }
        });
     }
    
    @JavascriptInterface
    public void onREMSettingsIconClick() {
    	Intent intent = new Intent(mActivity, CueSettingsActivity.class);
    	mActivity.startActivity(intent);
     }
    
    @JavascriptInterface
    public void onFinalSettingsIconClick() {
    	Intent intent = new Intent(mActivity, SettingsActivity.class);
    	mActivity.startActivity(intent);
     }
    

}
