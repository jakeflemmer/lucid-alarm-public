package app.lucidalarm2;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class CueGraphPreference extends Preference {

	private static View view;
	
	/*
	 *  Putting this whole class on the back burner because it somehow gets orphaned making it impossible
	 *  to tell it to redraw itself when settings are changed - rendering it worthless for now :(
	 *  
	 *  pity to put it aside as a tremendous amount of work has already gone into it :'(
	 * 
	 * 
	 * 
	 */

	private GraphFragment graphFragment;
	
	public CueGraphPreference(Context context) {
		super(context);
	}

	public CueGraphPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPreference(context, attrs);
	}

	public CueGraphPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPreference(context, attrs);
	}
	
	private void initPreference(Context context, AttributeSet attrs) {
		
		setWidgetLayoutResource(R.layout.cue_graph_preference_layout);
	}
	
	@Override
	public View getView(final View convertView, final ViewGroup parent) {
	    final View v = super.getView(convertView, parent);
	    final int hieght = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
	    final int width = 300;	
	    final LayoutParams params = new LayoutParams(hieght, width);
	    v.setLayoutParams(params );
	    view = v;
	    return v;
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		if ( view == null )
		{
			return super.onCreateView(parent);
		}else{
			return view;
		}
	}
	
//	public void redrawTheGraphOnSettingsChange(Activity activity, Context con){
//		FragmentManager fm = activity.getFragmentManager();
//		
//		GraphFragment gf = (GraphFragment) fm.findFragmentById(R.id.cue_settings_graph_fragment);
//		if ( gf != null ){
//			gf.generateTheGraphDataFromSettings();
//			graphFragment = gf;
//			Utils.boogieLog("CueGraphPreference", "redrawTheGraphOnSettingsChange", "Graf fragment is NOT null ");
//		}else{
//			if ( graphFragment != null )
//			{
//				graphFragment.generateTheGraphDataFromSettings();
//				Utils.boogieLog("CueGraphPreference", "redrawTheGraphOnSettingsChange", "graphFragment is NOT null ");
//			}
//			Utils.boogieLog("CueGraphPreference", "redrawTheGraphOnSettingsChange", "Graf fragment is NULL ");
//		}
//	}
}
 