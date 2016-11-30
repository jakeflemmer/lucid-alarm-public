package app.lucidalarm2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class REMAlarmBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		
		Log.d("graw", "remAlarmBroadcastReceiver onRecieve()");
		Intent intent = new Intent(context, REMAlarmPlayerService.class);
		context.startService(intent);
		
	
	}

}
