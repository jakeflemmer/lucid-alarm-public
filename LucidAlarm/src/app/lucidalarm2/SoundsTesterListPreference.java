package app.lucidalarm2;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class SoundsTesterListPreference extends ListPreference implements
		OnClickListener  {
	
	private int mSelectedIndex;
	public static final int ACTIVITY_RESULT_SELECT_SONG = 5;
	public Activity mActivity;

	public SoundsTesterListPreference(Context context) {
		super(context);
		
		populateTheList();
	}

	public SoundsTesterListPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		populateTheList();
		
		
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
//        switch(which) {
//            case DialogInterface.BUTTON_POSITIVE:
//                Toast.makeText(getContext(), "OK",Toast.LENGTH_LONG).show();
//                break;
//            case DialogInterface.BUTTON_NEGATIVE:
//                Toast.makeText(getContext(), "KO",Toast.LENGTH_LONG).show();
//                break;
//            case DialogInterface.BUTTON_NEUTRAL:
//                Toast.makeText(getContext(), "CANCEL",Toast.LENGTH_LONG).show();
//                break;
//        }
    }
	
	@Override
    protected void onPrepareDialogBuilder(Builder builder) {
		
		populateTheList();

		CharSequence[] values = this.getEntries();

        mSelectedIndex = this.findIndexOfValue(this.getValue());

        
        
        if ( getKey().equals( "preRecordedSoundsList_preference" ))
		{
        		builder.setSingleChoiceItems(values, mSelectedIndex, mClickListener)
        			.setPositiveButton(android.R.string.ok, mClickListener)
        			.setNegativeButton(android.R.string.cancel, mClickListener)
        			.setNeutralButton("delete", mClickListener );
		}
        else
        {
        		builder.setSingleChoiceItems(values, mSelectedIndex, mClickListener)
        			.setPositiveButton(android.R.string.ok, mClickListener)
        			.setNegativeButton(android.R.string.cancel, mClickListener);
        }
        
	}
	
	@Override
	public void onDismiss(DialogInterface dialog) {
		stopREMAlarmPlayerService();		// note - untested 11/11
	};
	
	private void populateTheList(){
		ArrayList<String> optionsEntriesArrayList = new ArrayList<String>();
		ArrayList<String> optionsEntryValuesArrayList = new ArrayList<String>();
		
		if ( getKey().equals( "natureSoundsList_preference" ))
		{
			Utils.addNatureSoundsList(optionsEntriesArrayList, optionsEntryValuesArrayList);
		}
		else if ( getKey().equals( "isochronicBinauralSoundsList_preference" ))
		{
			Utils.addIsochronicBinauralSoundsList(optionsEntriesArrayList, optionsEntryValuesArrayList);
		}
//		else if ( getKey().equals( "isochronicSoundsList_preference" ))
//		{
//			Utils.addIsochronicSoundsList(optionsEntriesArrayList, optionsEntryValuesArrayList);
//		}
		else if ( getKey().equals( "preRecordedSoundsList_preference" ))
		{
			Utils.addPreRecordedSoundsList(optionsEntriesArrayList, optionsEntryValuesArrayList);
		}
				
		String[] optionsEntriesArray = new String[optionsEntriesArrayList.size()];
		for ( int i=0; i < optionsEntriesArrayList.size(); i++)
		{
			optionsEntriesArray[i] = optionsEntriesArrayList.get(i);
		}
		String[] optionsEntryValuesArray = new String[optionsEntryValuesArrayList.size()];
		for ( int i=0; i < optionsEntryValuesArrayList.size(); i++)
		{
			optionsEntryValuesArray[i] = optionsEntryValuesArrayList.get(i);
		}
				
		this.setEntries( optionsEntriesArray );
		this.setEntryValues( optionsEntryValuesArray );
	}
	
	
	OnClickListener mClickListener = new OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which)
        {
            if (which >= 0)//if which is zero or greater, one of the options was clicked
            {
                String clickedValue = (String) SoundsTesterListPreference.this
                        .getEntryValues()[which]; //get the value

                onChoiceClick(clickedValue);

                mSelectedIndex = which;//update current selected index
            } 
            else //if which is a negative number, one of the buttons (positive,negative or neutral) was pressed.
            {
                if (which == DialogInterface.BUTTON_POSITIVE) //if the positive button was pressed, persist the value.
                {
                		SoundsTesterListPreference.this.setValueIndex(0);	// if they selected what was already selected it does nothing so force it by changing
                		SoundsTesterListPreference.this.setValueIndex(mSelectedIndex);
                		SoundsTesterListPreference.this.onClick(dialog,	DialogInterface.BUTTON_POSITIVE);	// does nothing!
                }
                if ( which == DialogInterface.BUTTON_NEUTRAL)		// Delete this sound from pre recorded list
                {
                		String soundName = (String) SoundsTesterListPreference.this
                                .getEntryValues()[mSelectedIndex]; //get the value
                		// TODO if this soundName is the currently selected sound then set the current sound to default
                		Utils.deletePreRecordedSound( soundName ); 
                }
                stopREMAlarmPlayerService();
                dialog.dismiss(); //close the dialog
            }
            Utils.boogieLog("soundsTesterListPreference", "mClickListener", "which : " + which );
        }
    };
    
    public void onChoiceClick ( String soundName) {
    		stopREMAlarmPlayerService();		// in case another sound is already playing
		testSound(soundName);
    }
    
    public void testSound ( String soundName) {
    	
		sing().whatTheREMPlayerIsDoing = REMAlarmPlayerService.REM_TESTING_MAX_VOLUME;
		Intent testRemSoundIntent = new Intent( getContext(), REMAlarmPlayerService.class );
		testRemSoundIntent.putExtra( REMAlarmPlayerService.REM_SOUND_TEST_BY_SONG_NAME, soundName );
		getContext().startService(testRemSoundIntent);
					Utils.boogieLog("SoundsTesterListPreference", "onChoiceClick", "soundName = " + soundName );
					
    }
    
    private void stopREMAlarmPlayerService(){  
    		sing().whatTheREMPlayerIsDoing = REMAlarmPlayerService.REM_NOT_STARTED_YET;
		Intent intent = new Intent( getContext(), REMAlarmPlayerService.class);
		getContext().stopService(intent);
    }
    
    
    		//====================================================================================
  		//	Getters & Setters
  		//====================================================================================
  		private Singleton sing(){
  			return Singleton.getInstance();
  		}
}
