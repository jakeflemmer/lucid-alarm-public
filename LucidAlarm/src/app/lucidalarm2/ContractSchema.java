package app.lucidalarm2;

import android.provider.BaseColumns;

public final class ContractSchema {
	
	// constants
//    public static final String COLOR_RED = "red";
//    public static final String COLOR_BLUE = "blue";
//    public static final String COLOR_YELLOW = "yellow";
//    public static final String COLOR_ORANGE = "orange";
//    public static final String COLOR_PINK = "pink";
//    public static final String COLOR_WHITE = "white";
    
    
    
    
    
	    // To prevent someone from accidentally instantiating the contract class,
	    // give it an empty constructor.
	    public ContractSchema() {}
	    
	    
	    

	    /* DIARY TABLE */
	    public static abstract class DiaryTable implements BaseColumns {
	        public static final String TABLE_NAME = "diary";
	        public static final String HIDDEN_ID_FIELD = "underscoreId";
	        public static final String COLUMN_NAME_TITLE = "title";
	        public static final String COLUMN_NAME_NOTES = "notes";
	        public static final String COLUMN_NAME_OTHER = "other";
	        public static final String COLUMN_NAME_LUCID = "lucid";
	        public static final String COLUMN_NAME_COLOR = "color";
	        public static final String COLUMN_NAME_UNIX_TIME = "unixTime";
	        public static final String COLUMN_NAME_MISC_1 = "misc1";
	        public static final String COLUMN_NAME_MISC_2 = "misc2";
	        public static final String COLUMN_NAME_MISC_3 = "misc3";
	        public static final String COLUMN_NAME_MISC_NAMES = "miscNames";	// a comma separated list - default = "misc1,misc2,misc3";
	        public static final String COLUMN_NAME_SOUND = "sound";
	        public static final String COLUMN_NAME_LIGHT_COLOR = "alarmLightColor";  // displayed on graph
	        public static final String COLUMN_NAME_LIGHT_FREQUENCY = "frequency";
	        
	        public static final String[] allColumnNamesArray = { HIDDEN_ID_FIELD,
	        													COLUMN_NAME_TITLE,
	        													COLUMN_NAME_NOTES ,
	        													COLUMN_NAME_OTHER ,
	        													COLUMN_NAME_LUCID ,
	        													COLUMN_NAME_COLOR ,
	        													COLUMN_NAME_UNIX_TIME ,
	        													COLUMN_NAME_MISC_1 ,
	        													COLUMN_NAME_MISC_2 ,
	        													COLUMN_NAME_MISC_3 ,
	        													COLUMN_NAME_MISC_NAMES ,
	        													COLUMN_NAME_SOUND ,
	        													COLUMN_NAME_LIGHT_COLOR ,
	        													COLUMN_NAME_LIGHT_FREQUENCY
	        													};
	    
	    }
	    
	    
	    /* GRAPH TABLE */
	    public static abstract class GraphsTable implements BaseColumns {
	        public static final String TABLE_NAME = "graphs";
	        public static final String COLUMN_NAME_DIARY_ID = "diaryId";
	        public static final String COLUMN_NAME_UNIX_TIME = "unixTime";
	        public static final String COLUMN_NAME_VOLUME = "volume";
	        public static final String COLUMN_NAME_BRIGHTNESS = "brightness";
	    }
	    
	    /* RESETS CLICKED TABLE */
	    public static abstract class ResetsClickedTable implements BaseColumns {
	    	public static final String TABLE_NAME = "resetsClicked";
	    	public static final String COLUMN_NAME_DIARY_ID = "diaryId";
	        public static final String COLUMN_NAME_UNIX_TIME = "unixTime";
	    }
	    
	    
	    
	    
	    /* CUES TABLE */
	    /*  NOTE : with hindsight of 20/20 - it is better to just name the columns from the array of preferences defined in the SettingsActivity ( now conveniently in an array :) */
	    
	    public static abstract class CuesTable implements BaseColumns {
	        public static final String TABLE_NAME = "cues";
	        /*
	        public static final String COLUMN_NAME_CUE_NAME = "cueName";
	        public static final String COLUMN_NAME_SOUND = "sound";
	        public static final String COLUMN_NAME_DELAY_BETWEEN_SOUNDS = "delaySounds";		// int - number of seconds pause between playing sounds
	        public static final String COLUMN_NAME_DELAY_AFTER_RESET = "delayResets";
	        public static final String COLUMN_NAME_SHAPE = "shape";
	        public static final String COLUMN_NAME_MIN_VOLUME = "minVolume";
	        public static final String COLUMN_NAME_MAX_VOLUME = "maxVolume";
	        public static final String COLUMN_NAME_BRIGHTNESS = "brightness";	// this is the maximum brightness and is zero is there is no light
	        public static final String COLUMN_NAME_FREQUENCY = "frequency";		// number of flashes per second
	        public static final String COLUMN_NAME_COLOR = "color";	
	        
	        
	        */
	    }
	    
	    
	}