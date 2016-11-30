package app.lucidalarm2;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
//import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.webkit.WebView.FindListener;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class AudioFragment extends Fragment implements MediaPlayer.OnCompletionListener , OnClickListener , OnSeekBarChangeListener {
	
	public static final String NEW_REM_SOUND = "newRemSound";
	public static final String DEFAULT_NEW_FILENAME = "New Sound Cue";
	public static final String SOUNDS_DIRECTORY_NAME = "/LucidAlarmSounds/";
	public static final String RECORDINGS_DIRECTORY_NAME = "/DreamRecordings/";
	
	public static final String DREAM_RECORD_DATE_STRING = "dreamRecordDateString";
	
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;
	private Chronometer chrono = null;
	//private long timeWhenStopped = 0;
	private SeekBar seekBar;
	private Handler seekHandler = new Handler();
	private Handler soundHandler = new Handler();
	private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private String _currentState = "notStarted";
    private String mode = "playing";
    private String getCurrentState() {
    	 return _currentState;
    }
    private void setCurrentState( String value  ) {
    		_currentState = value;
    }
	private static View view;
	
	
	private String _newRemSoundSaveAsName = "New Sound Cue";
	
	public void setNewRemSoundSaveAsName( String name ) {
		_newRemSoundSaveAsName = name;
	}
	public String getNewRemSoundSaveAsName () {
		return _newRemSoundSaveAsName;
	}
	
	ImageButton playBtn;
	ImageButton recBtn;
	ImageButton stopBtn;
	ImageButton micBtn;
	
	//====================================================================================
    // STATE CHANGE START
    //====================================================================================
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    		
    		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);	// do we need to keep screen on whole time?
    	
    		if (view != null) {
    	        ViewGroup parent = (ViewGroup) view.getParent();
    	        if (parent != null) parent.removeView(view);
    	    	}
    		try {
        		view = inflater.inflate(R.layout.audio_fragment_layout, container, false);
    		} catch (InflateException e) {
        		// map is already there, just return view as it is 
    		}
    		playBtn = (ImageButton) view.findViewById(R.id.playIcon_imageButton);
    		recBtn = (ImageButton) view.findViewById(R.id.recordIcon_imageButton);
    		stopBtn = (ImageButton) view.findViewById(R.id.stopIcon_imageButton);
    		micBtn = (ImageButton) view.findViewById(R.id.microphone_imageButton);
    		playBtn.setOnClickListener(this);
    		recBtn.setOnClickListener(this);
    		stopBtn.setOnClickListener(this);
    		micBtn.setOnClickListener(this);
    		
    		return view;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Activity parentActivity = getActivity();
        // TODO below
//		if ( parentActivity instanceof DiaryActivity )
//		{
//			String rowId = parentActivity.getIntent().getStringExtra("selectedRecordId");
//				Utils.boogieLog("AudioFragment", "onCreateView", "from inside DiaryActivity rowId from intent : " + rowId );
//		}
        			Utils.boogieLog("AudioFragment", "onViewCreated()", "graw" );
    }
    // Called at the start of the visible lifetime.
    @Override
    public void onStart(){
    		super.onStart();

    		mPlayer = null;
    		mRecorder = null;
    		
    		Activity parentActivity = getActivity();
    		if ( parentActivity instanceof AudioActivity )
    		{
    			setTheFileName();
    			disableButton( ( ImageButton ) view.findViewById( R.id.playIcon_imageButton ) );
    			switchToRecordMode();
						Utils.boogieLog("AudioFragment", "onStart", "from inside AudioActivity ");
    		}
    		if ( parentActivity instanceof DiaryActivity )
    		{
    				boolean recordingAlreadyExists = setTheFileName();
        		if ( recordingAlreadyExists )
        		{
        			enableButton( ( ImageButton ) view.findViewById( R.id.playIcon_imageButton ) );
        			switchToPlayMode();
        		}else{
        			hideEverythingExceptTheMicrophoneIcon(true);
        			mode = "hidden";
        		}
						Utils.boogieLog("AudioFragment", "onStart", "from inside DiaryActivity ");
    		}
    		
    		//scaleRecordingSoundMeter();
	    		
    }

    //================================================================
  	// SAVE ON PAUSE
  	//================================================================
  	@Override
      public void onPause() {
          super.onPause();
  		Intent intent = getActivity().getIntent();
  		String intentMessage = intent.getStringExtra(MainActivity.INTENT_STRING_MESSAGE);
  		if (intentMessage != null )
  		{
  			if ( intentMessage.equals(AudioActivity.NEW_REM_SOUND))
  			{
  				// rename file to above
  				String basePath = getBasePathToCustomAudioRecordings();
  				File oldFile = new File( basePath + SOUNDS_DIRECTORY_NAME,DEFAULT_NEW_FILENAME + ".3gp");
  				File newFile = new File( basePath + SOUNDS_DIRECTORY_NAME, _newRemSoundSaveAsName + ".3gp");
  				oldFile.renameTo(newFile);
  				
  				Utils.setStringPreference(getActivity().getApplicationContext(), _newRemSoundSaveAsName, SettingsActivity.SELECTED_ALARM_SOUND_PREF );
  			}
  		}
  		if (mRecorder != null) {
              mRecorder.release();
              mRecorder = null;
          }
          if (mPlayer != null) {
              mPlayer.release();
              mPlayer = null;
          }
          seekHandler.removeCallbacks(runSeekBar);
          soundHandler.removeCallbacks(runSoundBar);
      }
  	//====================================================================================
  	//	HIDE EVERYTHING IF NO RECORDING ALREADY EXISTS
  	//====================================================================================
  	private void hideEverythingExceptTheMicrophoneIcon(boolean forReal) {
  		View everythingLL = view.findViewById(R.id.everything_linearLayout);
  		everythingLL.setVisibility( ( forReal ? View.GONE : View.VISIBLE ) );
  		View mic = view.findViewById(R.id.microphone_imageButton);	
  		mic.setVisibility( ( forReal ? View.VISIBLE : View.GONE ) );
  	}
  	//====================================================================================
  	//	INIT STUFF
  	//====================================================================================
  	
//  	private void scaleRecordingSoundMeter() {
//  		View pic = view.findViewById(R.id.soundBar_imageView);
//  		LayoutParams picLP = pic.getLayoutParams();
////  		int picHeight = pic.getHeight();  		
//  		int picHeight = picLP.height;
//  		View relativeLayout =  view.findViewById( R.id.soundBar_relativeLayout);
//        android.view.ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
//        params.height = picHeight;
//        relativeLayout.setLayoutParams(params);
//  	}
  	private void initChronometer() {
  		chrono = ( Chronometer ) view.findViewById( R.id.chronometer );
  		chrono.setBase(SystemClock.elapsedRealtime());
  	}
  	private void switchToPlayMode() {
  		songCurrentDurationLabel = ( TextView ) view.findViewById( R.id.songCurrentDuration_textView );
  		songTotalDurationLabel = ( TextView ) view.findViewById( R.id.songTotalDuration_textView );
  		mPlayer = new MediaPlayer();
  		try {
  			mPlayer.setDataSource(mFileName);
  			mPlayer.prepare();
  			mPlayer.setOnCompletionListener(this);
  		} catch (IOException e) {
  			Utils.boogieLog("AudioFragment", "initPlayerAndSeekBar()", " ERROR PREPARING MEDIA PLAYER - filename : " + mFileName);
  			return;
  		}
  		seekBar = (SeekBar) view.findViewById(R.id.audioSeekBar);
  		seekBar.setVisibility(View.VISIBLE);
  		View soundBar = view.findViewById(R.id.soundBar_relativeLayout);
  		soundBar.setVisibility(View.GONE);
  		seekBar.setMax( mPlayer.getDuration() );
  		seekBar.setOnSeekBarChangeListener(this);
  		updateSeekBarLabels( false );
  		initChronometer();
  		setChronoDisplayToDurationOfPlayer();
  		// show the time labels
		  View timeLabels = view.findViewById(R.id.timeLabels_relativeLayout);
		  timeLabels.setVisibility(View.VISIBLE);
		  
		  mode = "playing";
  					Utils.boogieLog("AudioFragment", "initMediaPlayerAndSeekBar()", " success - filename : " + mFileName);
  	}
  	private void switchToRecordMode() {
  		  mRecorder = new MediaRecorder();
          mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
          mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
          mRecorder.setOutputFile(mFileName);
          mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
          try {
              mRecorder.prepare();
          } catch (IOException e) {
        	  		Utils.boogieLog("AudioActivity", "initRecorderAndSoundBar", "ERROR PREPARING RECORDER - filename = " + mFileName );
          }
          seekBar = (SeekBar) view.findViewById(R.id.audioSeekBar);
    		  seekBar.setVisibility(View.GONE);
    		  View soundBar = view.findViewById(R.id.soundBar_relativeLayout);
    		  soundBar.setVisibility(View.VISIBLE);
          //disableButton( ( ImageButton ) view.findViewById( R.id.playIcon_imageButton ) );
          if ( chrono == null ) chrono = new Chronometer(getActivity());
  		  chrono.setBase(SystemClock.elapsedRealtime());
  		  // hide the time labels
  		  View timeLabels = view.findViewById(R.id.timeLabels_relativeLayout);
  		  timeLabels.setVisibility(View.GONE);
  		  
  		  mode = "recording";
          			Utils.boogieLog("AudioActivity", "initRecorderAndSoundBar", "success filename = " + mFileName );
      }
  	
    //====================================================================================
  	//	SEEK BAR STUFF 
  	//====================================================================================
  	Runnable runSeekBar = new Runnable() {
  		@Override
  		public void run() {
  			updateSeekBar();
  			updateSeekBarLabels( true );
  		}
  	};
  	private void updateSeekBarLabels( boolean updateChronoToMatchCurrentDuration ){
		 long totalDuration = mPlayer.getDuration();
         long currentDuration = mPlayer.getCurrentPosition();
         songTotalDurationLabel.setText(""+Utils.milliSecondsToTimer(totalDuration));
         songCurrentDurationLabel.setText(""+Utils.milliSecondsToTimer(currentDuration));
         if ( updateChronoToMatchCurrentDuration ) chrono.setText(""+Utils.milliSecondsToTimer(currentDuration));
  	}
  	public void updateSeekBar() {
  		if ( seekBar == null || mPlayer == null ) return; 	// TODO  
          seekBar.setProgress(mPlayer.getCurrentPosition());
          seekHandler.postDelayed(runSeekBar, 100);
      }
  	
    //====================================================================================
  	//	SOUND BAR STUFF 
  	//====================================================================================
  	Runnable runSoundBar = new Runnable() {
  		@Override
  		public void run() {
  			updateSoundBar();
  		}
  	};
  	public void updateSoundBar() {
          
  		if ( mRecorder == null ) return;
  		float recordingVolume = mRecorder.getMaxAmplitude();
          soundHandler.postDelayed(runSoundBar, 200);
          // 0 = min , 32767 = max
          float relativeVolume = recordingVolume / 32767; 
          int totalWidth = view.findViewById( R.id.soundBar_relativeLayout ).getWidth();
          int blackWidth = totalWidth - Math.round( totalWidth * relativeVolume) ;
          if ( blackWidth > ( totalWidth * .95 ))
          {
        	  Double tenPercent = (totalWidth * .95);
        	  blackWidth = tenPercent.intValue();
          }
          ImageView iv = ( ImageView ) view.findViewById( R.id.darkBar_imageView );
          android.view.ViewGroup.LayoutParams params = iv.getLayoutParams();
          params.width = blackWidth;
          
//		View pic = view.findViewById(R.id.soundBar_imageView);
//		LayoutParams picLP = pic.getLayoutParams();		  	
//		int picHeight = picLP.height;
//		
//		params.height = picHeight;
          
          iv.setLayoutParams(params);
          			Utils.boogieLog("AudioActivity", "updateSoundBar()", "recordingVolume = " + recordingVolume );
      }

  	//====================================================================================
  	//	SETUP 
  	//====================================================================================
  	
  	private boolean setTheFileName(){  // return true if file already exists
  		
  		Intent intent = getActivity().getIntent();
  		String intentMessage = intent.getStringExtra(MainActivity.INTENT_STRING_MESSAGE);
  		if (intentMessage != null )
  		{
  			// RECORD NEW CUSTOM REM ALARM SOUND
  			if ( intentMessage.equals(AudioActivity.NEW_REM_SOUND))
  			{
  				mFileName = getBasePathToCustomAudioRecordings();
  				String basePath = getBasePathToCustomAudioRecordings();
  				File newDirectory = new File(basePath + SOUNDS_DIRECTORY_NAME );
  				if ( !newDirectory.exists()){
  					newDirectory.mkdir();	
  				}
  				mFileName +=  SOUNDS_DIRECTORY_NAME + DEFAULT_NEW_FILENAME + ".3gp";
  				
  				File isFile = new File(mFileName);
  				if ( isFile.exists())
  				{
  					return true;
  				}else{
  					return false;
  				}
  			}
  		}else{
  			String dateName = getUnixTime();
  			String basePath = getBasePathToCustomAudioRecordings();
  			mFileName = getBasePathToCustomAudioRecordings();
  			File newDirectory = new File(basePath + RECORDINGS_DIRECTORY_NAME );
  			if ( !newDirectory.exists()){
  				newDirectory.mkdir();	
  			}
  			mFileName +=  RECORDINGS_DIRECTORY_NAME + dateName + ".3gp";
  			File isFile = new File( basePath + RECORDINGS_DIRECTORY_NAME, dateName + ".3gp");
  			if ( isFile.exists() && isFile.length() > 0 )
  			{
  				return true;
  			}else{
  				return false;
  			}
  		}
  		return false;	
  	}
      
      //====================================================================================
      //====================================================================================
    	//  CLICK HANDLERS
    	//====================================================================================
      //====================================================================================
      
      @Override
      public void onClick(View v) {
          switch (v.getId()) {
          	//====================================================
          	// PLAY  ----------------------
          	//====================================================
          	case R.id.playIcon_imageButton:			
          		if ( mode.equals("playing") )
  				{
          			if ( getCurrentState().equals("notStarted") )
          			{
          				mPlayer.start();
          				updateSeekBar();
          				playBtn.setImageResource(R.drawable.ic_media_pause);
          				setCurrentState("playing");
          				return;		// these return statement are necessary like break statements in a switch case block
          			}
          			if ( getCurrentState().equals("playing"))
          			{
          				mPlayer.pause();
          				seekHandler.removeCallbacks(runSeekBar);
          				playBtn.setImageResource(R.drawable.ic_media_play);
          				setCurrentState("paused");
          				return;
          			}
          			if ( getCurrentState().equals("paused"))
          			{
          				mPlayer.start();
          				updateSeekBar();
          				playBtn.setImageResource(R.drawable.ic_media_pause);
          				setCurrentState("playing");
          				return;
          			}
  				}
          		else if ( mode.equals("recording"))
          		{
          			// clicked on play while in recording mode so we need to change modes !
          			switchToPlayMode();          			
          			mPlayer.start();
          			updateSeekBar();
          			setCurrentState("playing");
          		}
          				Utils.boogieLog("AudioFragment", "OnPLAYiconCLICK()", "graw");
              break;
            //====================================================
            // RECORD  ----------------------
            //====================================================
          	case R.id.recordIcon_imageButton:		
          		if ( mode.equals("recording"))
          		{
          			if ( getCurrentState().equals("notStarted") )
          			{
          				startRecording();
          				return;
          			}
          		}
          		else if ( mode.equals("playing"))
          		{
          			if ( getCurrentState().equals("playing"))return;
          			// clicked on record while in play mode to an overwrite is required
          			// open dialog box to confirm over write of prerecorded track
          			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); 
          			builder
          				.setMessage("Overwrite your previous recording?")
          				.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
          					public void onClick(DialogInterface dialog, int id){
          						// YES  --------------------------
          						// reset the chrono
          						// switch to record mode          						          						//
          	        			switchToRecordMode();
          	        			setCurrentState("notStarted");    
          	        			startRecording();
          						Utils.boogieLog("AudioFragment", "onRecordButtonClick", "overwrite recording");
          					}
          				}) 
          				.setNegativeButton("No", new DialogInterface.OnClickListener(){
          					public void onClick(DialogInterface dialog, int id){
          						// NO  ----------------------------
          						// okay so do nothing
          						Utils.boogieLog("AudioFragment", "onRecordButtonClick", "overwrite recording");
          					}
          				});
          			AlertDialog alertDialog = builder.create();
          			alertDialog.show();		
          			return;
          		}
          				Utils.boogieLog("AudioFragment", "OnRECORDiconCLICK()", "graw");
          		break;
          	//====================================================
            // STOP  ----------------------
            //====================================================
          	case R.id.stopIcon_imageButton:		
          		if ( mode.equals("playing") )
          		{
          			mPlayer.pause();
          			mPlayer.seekTo(0);
          			seekBar.setProgress(0);
          			playBtn.setImageResource(R.drawable.ic_media_play);
          			seekHandler.removeCallbacks(runSeekBar);
          			setCurrentState("notStarted");
          			setChronoDisplayToDurationOfPlayer();
          		}
          		else if ( mode.equals("recording"))
          		{
          			stopRecording();
          		}
  						Utils.boogieLog("AudioFragment", "OnSTOPiconCLICK()", "graw");
  				break;
  			//====================================================
  	        // MICROPHONE  ----------------------
  	        //====================================================
          	case R.id.microphone_imageButton:		
          		if ( mode.equals("hidden") )
          		{
          			hideEverythingExceptTheMicrophoneIcon(false);
          			disableButton( ( ImageButton ) view.findViewById( R.id.playIcon_imageButton ) );
        			switchToRecordMode();        			
          		}
          		else 
          		{
          			// throw new Error("wtf");
          		}
  						Utils.boogieLog("AudioFragment", "OnMICROPHONEiconCLICK()", "graw");
  				break;
          }
      }
      
    //====================================================================================
    //	WIDGET FUNCTIONS
    //====================================================================================
 
    private void startRecording(){
    	mRecorder.start();
		updateSoundBar();
		setCurrentState("recording");
		initChronometer();
		chrono.start();
		disableButton( ( ImageButton ) view.findViewById( R.id.playIcon_imageButton ) );
    }
    private void stopRecording(){
    	mRecorder.stop();
		chrono.stop();     
		initChronometer();	// sets chrono to 0:00
		soundHandler.removeCallbacks(runSoundBar);
		setCurrentState("notStarted");
		switchToPlayMode();
		
		enableButton( (ImageButton ) view.findViewById( R.id.playIcon_imageButton ) );
    }
      
    //====================================================================================
  	//	SEEK BAR CHANGE HANDLERS
  	//====================================================================================
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
   
      }
      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
          seekHandler.removeCallbacks(runSeekBar);
      }
      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
    	  	seekHandler.removeCallbacks(runSeekBar);
          mPlayer.seekTo(seekBar.getProgress());
          updateSeekBar();
      }
   
      //====================================================================================
    	//	DISABLE AND ENABLE BUTTONS
    	//====================================================================================
  	
      private void enableButton ( ImageButton button ) {
      		button.setEnabled( true );
      }
      private void disableButton ( ImageButton button ) {
      		button.setEnabled( false );
      }
//      private void switchToPlayButton ( ) {
//      		ImageButton ib = (ImageButton) view.findViewById( R.id.playPauseIcon_imageButton );
//      		ib.setImageResource( R.drawable.ic_play );
//      }
//      private void switchToPauseButton () {
//      		ImageButton ib = (ImageButton) view.findViewById( R.id.playPauseIcon_imageButton );
//  		ib.setImageResource( R.drawable.ic_pause );
//      }
      private void setChronoDisplayToDurationOfPlayer(){
    	  	long duration = mPlayer.getDuration();
    	  	chrono.setText( Utils.milliSecondsToTimer(duration));
      }
      
      //====================================================================================
  	//	Getters & setters
  	//====================================================================================
  	public String getUnixTime(){
  		String dateString = getActivity().getIntent().getStringExtra( "selectedRecordUnixTime" );
  		Utils.boogieLog("AudioActivity", "getUnixTime()", "dateString : " + dateString );
  		return dateString;
  	}
  	private Singleton sing(){
  		return Singleton.getInstance();
  	}
  	public static String getBasePathToCustomAudioRecordings(){
  		String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
  		return basePath;
  	}
  	//====================================================================================
  	//	ON COMPLETION LISTENER
  	//====================================================================================
  	
  	@Override
  	public void onCompletion(MediaPlayer mp){
  		mPlayer.pause();
  		mPlayer.seekTo(0);
		seekBar.setProgress(0);
		playBtn.setImageResource(R.drawable.ic_media_play);
		seekHandler.removeCallbacks(runSeekBar);
		setCurrentState("notStarted");
		setChronoDisplayToDurationOfPlayer();
  	}
  	
  	
  	private void setAlphaz(Float alphaVal, View v){
  		AlphaAnimation alpha = new AlphaAnimation(alphaVal, alphaVal);
  		alpha.setDuration(0); // Make animation instant
  		alpha.setFillAfter(true); // Tell it to persist after the animation ends
  		v.startAnimation(alpha);
  	}

}
