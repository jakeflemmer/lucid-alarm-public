package app.lucidalarm2;

import android.app.Activity;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.View;
import android.content.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;

public class InfoActivity extends Activity {

	public static final String ABOUT_INFO = "aboutInfo";
	public static final String INSTRUCTIONS_INFO = "instructionsInfo";
	
	//====================================================================================
	//	ABOUT TEXT
	//====================================================================================
	
	public final String INTRO = "\n    Lucid Alarm is a tool to help you get lucid and stay lucid. Lucid dreams are dreams in which you are aware that you are dreaming.  If you knew you were dreaming, what would you do? Fly like Superman? Explore distant galaxies? Reconnect with an old friend? Experience an erotic fantasy? Beyond the boundless pleasure of getting lucid, developing these dreaming skills has real life benefits ranging from problem solving to personal growth. So are you ready to dream more boldly?\n";
	
	//public final String BENEFITS = "    If you knew you were dreaming while you were dreaming what would you do? Would you fly through the sky like superman? Explore distant galaxies? Have an erotic encounter with the partner of your wildest fantasies? Meditate with the Dalia Lama for enlightnement? The potential of this state is boundless and offers many substantive benefits including improving mental and physical health, developing athletic skills, reducing anxiety, gaining self-knowledge, visualizing success and much more.\n"; 
	
	public final String MORE_LUCID_DREAMING = "		We spend a third of our lives sleeping. Why not transform these hours from a black hole of consciousness to an altered state that can awaken us more fully to our waking lives? In our waking lives, we are constantly bombarded by mental stimuli that we filter out and store in our subconscious. This untapped creative repository can be accessed through lucid dreaming, allowing one to dissolve the boundary between the conscious and subconscious. In a lucid dream, preconceived questions (such as, “what can I do to improve my career happiness?”) can be answered by the subconscious’ wealth of sensory knowledge, allowing you amazing creative problem solving powers—while you sleep!\n\n";
	
	//public final String ISOCHRONIC = "    The Lucid Alarm app can improve dream lucidity in several different ways depending on the sounds that are chosen to be played. Isochronic tones are sounds that have been shown to produce brainwave entrainment. Basically this means that listening to these sounds induces a light meditative trance and transports the listener to a certain mental state depending on the frequency of the tones. Wakefulness, sleep, trance and lucid dreaming all have unique brainwave patterns and the isochronic tones of the Lucid Alarm app can assist in achieving the lucid dream state.\n";
	
	public final String MORE_ISOCHRONIC = "		Dreams are conscious experiences influenced by our fears, hopes and expectations. This app help you tap into those experiences by playing isochronic tones, or sounds that have been proven to train the brain. Hearing these unobtrusive sounds while you sleep induces a meditative state conducive to lucid dreaming.  Over time, hearing these sounds allows your brain to synchronize its own electric cycles to the rhythm of the isochronic tones. Aligning these rhythms can induce the desired mental state for achieving lucidity on a regular basis.\n\n";
	
	//public final String MILD = "    The Lucid Alarm app can be used along with the practice of MILD. The Mnemonic Induction of Lucid Dreams (MILD) is a technique that was developed by Dr Stephen LeBerge when he was researching lucid dreaming for his doctoral dissertation at Stanford University. He realized that the key to becoming lucid in his dreams was prospective memory and by intending to remember something (that he was dreaming) in the future (the next time he was dreaming) he was able to have lucid dreams at will. The Lucid Alarm app allows the recording of voice cues that can be played at low volume throughout the night. Such voice cues often enter dreams in perfect fidelity so record your own voice prompts and remember to become lucid when you hear them in your dreams.\n";
	
	public final String MORE_MILD = "	  Lucid Alarm can also be used with the Mnenomic Induction of Lucid Dreams (MILD) technique. This technique, developed by lucid dreaming pioneer Dr. Stephen LeBerge, is based on the knowledge that the key to lucid dreaming is prospective memory. By intending to remember something (that he was dreaming) during a future time (in the dream itself), he was able to have lucid dreams at will. This app can be programmed to play prerecorded voice cues (such as, “I CAN lucid dream”) throughout the night at a low volume. Record your own cues and remember to get lucid when you hear them in your dreams.\n\n";
	
	public final String MILD_TECHNIQUE = "	   1. Set up dream recall\n    Before going to bed resolve to wake up and recall dreams during each dream period throughout the night (or the first dream period after dawn, or after 6 a. m. or whenever you find convenient).\n    2. Recall your dream.\n    When you awaken from a dream period, no matter what time it is, try to recall as many details as possible from your dream. If you find yourself so drowsy that you are drifting back to sleep, do something to arouse yourself.\n    3. Focus your intent.\n    While returning to sleep, concentrate singlemindedly on your intention to remember to recognize that youâ€™re dreaming. Tell yourself: â€œNext time Iâ€™m dreaming, I want to remember Iâ€™m dreaming.â€? Really try to feel that you mean it. Narrow your thoughts to this idea alone. If you find yourself thinking about anything else, just let go of these thoughts and bring your mind back to your intention to remember.\n    4. See yourself becoming lucid.\n    At the same time, imagine that you are back in the dream from which you have just awakened, but this time you recognize that it is a dream. Find a dreamsign in the experience; when you see it say to yourself: â€œI'm dreaming!â€? and continue your fantasy. For example, you might decide that when you are lucid you want to fly. In that case, imagine yourself taking off and flying as soon as you come to the point in your fantasy that you realize you are dreaming.\n    5. Repeat.\n    Repeat Steps 3 and 4 until your intention is set, then let yourself fall asleep. If, while falling asleep, you find yourself thinking of anything else, repeat the procedure so that the last thing in your mind before falling asleep is your intention to remember to recognize the next time you are dreaming.\n    Commentary.\n    If all goes well, youâ€™ll fall asleep and find yourself in a dream, at which point youâ€™ll remember to notice that you are dreaming. If it takes you a long time to fall asleep while practicing this method, donâ€™t worry: The longer youâ€™re awake, the more likely you are to have a lucid dream when you eventually return to sleep. This is because the longer you are awake, the more times you will repeat the MILD procedure, reinforcing your intention to have a lucid dream. Furthermore, the wakefulness may activate your brain, making lucidity easier to attain. In fact, if you are a very deep sleeper, you should get up after memorizing your dream and engage in ten to fifteen minutes of any activity requiring full wakefulness. Turn on the light and read a book. Get out of bed and go into another room. One of the best things to do is to write out your dream and read it over, noting all dream signs, in preparation for the MILD visualization.\n    Many people meet with success after only one or two nights of MILD; others take longer. Continued practice of MILD can lead to greater proficiency at lucid dreaming. Many of our advanced oneironauts have used it to cultivate the ability to have several lucid dreams any night they choose.\n\n";
	
	//public final String WBTB = "    The Lucid Alarm app can be used with the Wake Back To Bed technique for added effectiveness. The WBTB technique involves interrupting a normal nights' sleep by injecting a period of wakefulness of usually 20 to 60 minutes and then returning to bed. This highly effective technique works because sleep and dreams are regulated by neurotranmitters within the brain which become altered by the period of wakefulness. Lucid Alarm app takes advantage of this natural phenomenon by playing sounds which are just loud enough to trigger increased wakefulness within the brain producing a similar effect to WBTB.\n"; 
	
	public final String MORE_WBTB = "  	  Lucid Alarm also works with the Wake Back to Bed (WBTB) technique, in which you purposely interrupt your sleep cycle with a short (20-60 mins) period of wakefulness. Research has proven that this interruption triggers neurotransmitters within the brain to induce a dream state when one goes back to sleep. Lucid Alarm mimics this natural phenomenon by playing sounds just loud enough to induce this WBTB pattern in your brain.\n\n";
	
	//public final String RECALL = "    The single greatest indicator of success in developing the ability to lucid dream is dream recall. The greater the quantity, quality and vividness of your remembered dreams the more familiar you can become with their content and style and the easier it becomes for you to recognize them as dreams while having them. The Lucid Alarm app serves as a dream diary companion, letting you record your dreams as voice or written records and access and sort the records easily. You can mark your lucid dreams and you can rate each dream on a scale of lucidity to monitor your progress.\n";
	
	public final String MORE_RECALL = "		How do you know if you’re getting closer to achieving dream lucidity? Dream recall. The more you can remember your dreams, the more familiar you become with their style, texture and recurring content—so it becomes easier for you to recognize them as dreams while you’re having them.  Lucid Alarm features a dream diary to record your dreams and organize them. By getting to know the dreams you’ve been having, you can increase your chances of manipulating future dreams.\n\n";
	
	public final String CONCLUSION = "Lucid Alarm is a powerful and sophisticated tool designed to transport the ancient practice of lucid dreaming into the digital realm of the 21st century. Dream on!  ";
	
	
	//====================================================================================
	//	ABOUT INFO 
	//====================================================================================

	private void createAndSetAboutLayout(){
		
		this.ll = makeLinearLayout(true);

		TextView title = makeTextView("About Lucid Alarm",false);
		ll.addView(title);

		TextView intro = makeTextView(INTRO,false);
		ll.addView(intro);

//		TextView benefits = makeTextView(BENEFITS,false);
//		ll.addView(benefits);
			
		TextView moreBenefits = makeTextView("About Lucid Dreaming",true);
		ll.addView(moreBenefits);
		attachOnClickHandler(moreBenefits, MORE_LUCID_DREAMING );
		
//		TextView isochronic = makeTextView(ISOCHRONIC,false);
//		ll.addView(isochronic);

		TextView moreIsochronic = makeTextView("About Isochronic Tones",true);
		ll.addView(moreIsochronic);
		attachOnClickHandler(moreIsochronic, MORE_ISOCHRONIC );
		
//		TextView mild = makeTextView(MILD ,false);
//		ll.addView(mild);
		
		TextView moreMild = makeTextView("About MILD",true);
		ll.addView(moreMild);
		attachOnClickHandler(moreMild, MORE_MILD);
		
		TextView moreMildTechnique = makeTextView("The MILD Technique",true);
		ll.addView(moreMildTechnique);
		attachOnClickHandler(moreMildTechnique, MILD_TECHNIQUE);

//		TextView wbtb = makeTextView(WBTB ,false);
//		ll.addView(wbtb);

		TextView moreWbtb = makeTextView("About WBTB",true);
		ll.addView(moreWbtb);
		attachOnClickHandler(moreWbtb, MORE_WBTB);
		
//		TextView recall = makeTextView(RECALL ,false);
//		ll.addView(recall);

		TextView moreRecall = makeTextView("About Dream Recall",true);
		ll.addView(moreRecall);
		attachOnClickHandler(moreRecall, MORE_RECALL);
		
		TextView conclusion = makeTextView(CONCLUSION ,false);
		ll.addView(conclusion);

		ScrollView sv = new ScrollView(this);
		sv.addView(ll);
		setContentView(sv);
	}
	
	//====================================================================================
	//	INSTRUCTIONS TEXT
	//====================================================================================
	
	// reviewed ok 6/6/14
	public final String SET_FINAL = "1) Setting the Final Alarm\n\na) Tap the final alarm time display to bring up a time picker to set the time.\nb) Check that the power icon to the left of the final alarm time display is on.\nc) Test the alarm by tapping the alarm icon and choosing the \"Final Alarm\" option.\nd) The final alarm song/sound, minutes of snooze time, and minutes it takes to get to full final alarm volume can all be adjusted through the settings menu >> Final Alarm Settings .\ne) To turn the final alarm off tap the power icon to the left of the display time.\n\n ";

	public final String SET_REM = "2) Setting the REM alarm.\n\na) Tap the REM alarm time display to bring up a time picker to set the time.\nb) Check that the power icon to the left of the REM alarm time display is on.\nc) Test the alarm by tapping the alarm icon and choosing the \"REM Alarm\" option.\nd) Go to Settings Menu >> REM Alarm Settings for a wide variety of REM settings to customize.\ne) To turn the REM alarm off tap the power icon to the left of the display time.\nf) If the final alarm begins to play the REM alarm will be automatically turned off.\ng) When the REM alarm begins to play a new dream diary record is created and the REM alarms that have been played can be viewed on its graph of REM alarms played.\n\n ";

	public final String RECORDING_DREAMS = "3) Recording dreams, dream quality, lucidity and more.\n\na) Tap the load icon to get a list of dream records and select a record by tapping it to see the dream notes screen.\nb)Long click any record to delete it.\nc) The dream notes screen allows you to record the dream title, dream notes, if the dream was lucid or not and has several other fields for tracking lucid dreaming activities.\nd) Tapping the microphone icon allows you to record a new voice note or play a previously recorded voice note if one has been recorded.\nd) Tap the \"Lucid Dream\" check box to mark the dream as lucid.\ne) Use the additional fields for recording more information.\n\n ";
	
	public final String MANAGE_DREAM_RECORDS = "4) Managing dream records.\n\na) To create a new record without having played REM alarms, click the CREATE NEW option on the dream records screen.\nb) The EXPORT option allows you to send all of your dream records as a text document attached to an email.\nc) If you have exported your dreams from another device running Lucid Alarm then you can open the email and download the attached XML file. Once this file is in your device's downloads folder, clicking IMPORT will automatically import all of those dream records into Lucid Alarm.\nd) Long click any row to delete that dream record.\n\n";
	
	public final String VIEW_GRAPH = "4) Viewing the REM alarms played graph.\n\na) When the REM alarm turns off, the alarms it has played are viewable as a graph on the dream records screen.\nb) The graph shows the time the alarms were played and the volume they were played at.\nc) If the alarm was snoozed/dismissed this is shown on the graph with a red line.\n\n ";
	
	//public final String LOADING = "6) Loading previously saved dream notes and graphs.\n\na) Tap on the load icon to see previously saved dream records. If you used the REM alarm then a new record will have been automatically created.\nb) Tap any record to view it.\nc) Long tap any record to delete it.\nd) Select the \"Create New Record\" option and choose a date to create a new record for which no REM alarms were played but dream journal entries can be recorded on.\n\n ";

	//public final String ADVANCED = "7) Advanced settings and usage.\n\na) Set the max REM volume through the settings menu. Each time the REM alarm plays and is shut off the max REM volume is automatically recalculated and set by the app. It is set to a volume level that is the average volume that \"Reset\" has been tapped at for this alarm sound. So over time the volume should become more and more closely calibrated to the perfect volume that enters your dreams without waking you. However feel free to set this volume manually for greater control over the REM alarm player. Test the volume chosen by clicking the alarm icon on the main screen and choosing the \"REM alarm\" option.\nb) Set the minutes of REM delay through the settings menu. If \"Reset\" or \"Delay\" is tapped when the REM alarm goes off then the REM alarm will be automatically set to the number of delay minutes in the future. Sleep cycles are generally 90 minutes so if you are going straight back to sleep after a reset / delay then it is recommended that the combined time for delay minutes and minutes to get to full volume are somewhere around 90. ( eg. 60 minutes of delay and 30 minutes to get to full REM volume ).\nc) Set the minutes to get to full REM alarm volume through the settings menu.\nd) Check the \"Occilate REM Volume After Max\" checkbox to cause the REM volume to drop below max for a couple of minutes and then rise back up to max. The idea is for an REM alarm cue to enter your dream and trigger lucidity and then reduce the volume so you can enjoy your lucidity without distraction. Then a couple of minutes later the REM alarm will get louder, again entering your dreams and further solidifying your lucidity.\ne) Use the \"REM Valley Volume Percentage\" setting to choose how far to occilate the REM volume down.\nf) Check the \"Increase the REM Volume Beyond Max\" checkbox to cause the REM alarm max volume to increase by 20% after every 3 occilations of the volume. This way the REM cues will gradually enter your dreams in slow waves until they eventually wake you up. (*Note the \"Occilate Max REM Volume\" option must be checked to allow this feature.)";

	
	
	//====================================================================================
	//	INSTRUCTIONS INFO 
	//====================================================================================
	private void createAndSetInstructionsLayout(){

		LinearLayout ll = makeLinearLayout(true);
		
		TextView heading = makeTextView("INSTRUCTIONS\n\n" ,false);
		ll.addView(heading);
		
		TextView setFinal = makeTextView(SET_FINAL ,false);
		ll.addView(setFinal);
		
		TextView setRem = makeTextView(SET_REM ,false);
		ll.addView(setRem);
		
		TextView recordDreams = makeTextView(RECORDING_DREAMS ,false);
		ll.addView(recordDreams);

		TextView manageRecords = makeTextView(MANAGE_DREAM_RECORDS ,false);
		ll.addView(manageRecords);
		
		TextView viewGraph = makeTextView(VIEW_GRAPH ,false);
		ll.addView(viewGraph);
				
//		TextView remPlayer= makeTextView(REM_PLAYER ,false);
//		ll.addView(remPlayer);
		
//		TextView loading = makeTextView(LOADING ,false);
//		ll.addView(loading);
		
//		TextView advanced = makeTextView(ADVANCED ,false);
//		ll.addView(advanced);
		
		ScrollView sv = new ScrollView(this);
		sv.addView(ll);
		setContentView(sv);

	}
	
	//====================================================================================
	//	VARIABLES
	//====================================================================================

	private LinearLayout ll = null;

	//====================================================================================
	//	STATE CHANGE HANDLERS
	//====================================================================================

	@Override
    public void onCreate(Bundle icicle) {
		
		Utils.setActivityThemeFromPreferences(this);
		
        super.onCreate(icicle);
		//createAndSetLayout();
	}

	@Override
	public void onResume(){
		super.onResume();

		Intent intent = getIntent();
		if ( intent == null )
		{
			sing().logg("graw","ERROR : null intent");	
			return;
		}
		String intentMessage = intent.getStringExtra(MainActivity.INTENT_STRING_MESSAGE);
		if (intentMessage != null )
		{
			if ( intentMessage.equals(INSTRUCTIONS_INFO))
			{
				createAndSetInstructionsLayout();
			}
			else if ( intentMessage.equals(ABOUT_INFO))
			{
				createAndSetAboutLayout();
			}
		}else{
			sing().logg("graw","intent not set on infoactivity");
		}

	}
	
	//====================================================================================
	//	ON CLICK MORE HANDLER 
	//====================================================================================
	
	private void attachOnClickHandler(final TextView tv, final String moreText){
		
		final Activity activity = (Activity ) this;
		
		tv.setOnClickListener(new View.OnClickListener(){
				public void onClick(View view){
					
					// find out tv's position
					int childCount = ll.getChildCount();
					for ( int i=0; i < childCount; i++)
					{
						View v = ll.getChildAt(i);
						if ( v instanceof TextView )
						{
							if ( ((TextView)v).getText().toString().equals(tv.getText().toString()) )
							{
								// if it contains "more" than do below
								if (((TextView)v).getText().toString().contains(">>") )
								{
									// change the "more" to "less"
									String changedLabel = ((TextView)v).getText().toString().replace(">>","<<");
									Utils.setMoreOrLessTextViewLinkColorByTheme(tv,activity,false);
									((TextView)v).setText(changedLabel);
									// create a new textView with text of moreText	
									TextView newTV = makeTextView(moreText,false);
									// add it right after tv's position
									ll.addView(newTV, i+1);
								}
								else if (((TextView)v).getText().toString().contains("<<") )
								{
									// change the "less" to more
									String changedLabel = ((TextView)v).getText().toString().replace("<<",">>");
									Utils.setMoreOrLessTextViewLinkColorByTheme(tv,activity,true);
									((TextView)v).setText(changedLabel);
									// remove the child after tv's position
									ll.removeViewAt(i+1);
								}
								return;
							}
						}
					}
				}
			});
	}
	
	
	//====================================================================================
	//	MAKE VIEW FUNCTIONS
	//====================================================================================
	
	private TextView makeTextView(String text, boolean moreLink){
		final TextView tv = new TextView(this);
        //tv.setBackgroundColor(0xFF000000);

        //tv.setTypeface(null, Typeface.BOLD);
        tv.setText(	text);
        tv.setGravity(Gravity.LEFT);

		tv.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		Utils.setTextViewColorByTheme(tv, this);
		
		if ( moreLink )
		{
			// set special text with square brackets around it
			// make it special color
			//tv.setTextColor(sing().moreLinkColor);
			Utils.setMoreOrLessTextViewLinkColorByTheme(tv,this,true);
			tv.setText("" + text + "    >> \n");
		}else{
			//tv.setTextColor(sing().greenColorAlpha);	
		}


        // Defining the layout parameters of the TextView
        //if ( weight == null){
        	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
			tv.setLayoutParams(lp);
        //}else{
        //	LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		//		0,
		//		LinearLayout.LayoutParams.WRAP_CONTENT,weight);
		//	tv.setLayoutParams(lp);
        //}
		//tv.setMaxLines(1);
        return tv;
	}
	
	private LinearLayout makeLinearLayout(Boolean leftAlign){
		LinearLayout parentLL = new LinearLayout(this);
		parentLL.setOrientation(LinearLayout.VERTICAL);
		parentLL.setLayoutParams(new LinearLayout.LayoutParams(
									 ViewGroup.LayoutParams.MATCH_PARENT,
									 ViewGroup.LayoutParams.MATCH_PARENT));
		
		if (leftAlign)  
		{
			parentLL.setGravity(Gravity.LEFT);
		}else
		{
			parentLL.setGravity(Gravity.CENTER);
		}
		
		//parentLL.setBackgroundColor(Color.parseColor("#000000"));
		return parentLL;
	}
    //====================================================================================
	//	Getters & setters
	//====================================================================================
	private Singleton sing(){
		return Singleton.getInstance();
	}
}
