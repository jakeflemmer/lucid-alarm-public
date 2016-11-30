package app.lucidalarm2;

import android.content.Context;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class RemSoundsDialogPreference extends DialogPreference {

	public RemSoundsDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		

		setDialogLayoutResource(R.layout.rem_sounds_dialog_preference);
	}
	
	
	
	
	
	@Override
	protected void onBindDialogView(View view) {

//		ListView lv = (ListView) view.findViewById(R.id.rem_sounds_list_view);
//		Utils.populateRemSoundListPreferenceOptions( lv, this);

	    super.onBindDialogView(view);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {

	    if(!positiveResult)
	        return;

//	    SharedPreferences.Editor editor = getEditor();
//	    editor.putString(getKey() + "_host",hostBox.getText().toString());
//	    editor.putString(getKey() + "_ip",ipBox.getText().toString());
//	    editor.commit();

	    super.onDialogClosed(positiveResult);
	}

	

}
