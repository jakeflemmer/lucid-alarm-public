var logPiority = 3;

const NAV_PLANET_LABEL_HEIGHT = 1;
const NAV_PLANET_GRADIENT = 1;
const NAV_PLANET_SPRING = 1;
const DRAW_NAV_PLANETS = 1;


const ERROR_TODO = 20;

const ON_WINDOW_RESIZE = 20;
const ACTIVE_ELEMENT = 3;
const CONTACT_FORM_SELECTED = 1;
const MINIMUM_SCREEN_HEIGHT = 1;
const DRAW_CONTACT_PAGE = 1;
const ZOOM_CONTACT_ELEMENT = 1;
const ZOOMED_IN_INPUT_SELECTED = 1;
const ZOOMED_INPUT_VALUE = 1;

const GET_SCREEN_DIMENSIONS = 1;

const PRE_LOAD = 1;
const PRE_LOAD_ANIMATION = 1;

const SCALE_IMAGES = 1;

const CONTACT_EMAIL  = 1;

const PERFORMANCE = 1;

const EVENT_LISTENERS = 1;
const ARROWS_CLICKED = 20;
const DRAW_ARROWS = 1;

const MOODYS_MORE_SPAN = 20;

const DRAW_PAGE_BACKGROUND = 20;

const LOADING_PLANET = 1;

const FINANCE_WIDGET = 1;
const BAR_CODE_WIDGET = 999;
const WEATHER_WIDGET = 1;



function boogieLog(msg,priority)
{
	if ( priority >= logPiority)
	{
		console.log(msg);
	}
}

// TESTS
/*

Zoom Contact Element
----------------------

* When a contact form input element is selected the screen should resize on mobile when the keyboard comes up
* A contact form input element should be the documents activeElement when the screen resizes
* The screen height should be resized below MinimumScreenHeight when the keyboard comes up
* contactFormInZoomedInMode should be set to true
* contactFormElementBeingEdited should be set to a contact form element
* zoomedInInput should become full screen
* zoomedInInput should get focus
* values should be bound between the zoomedInInput and the contactFormElementBeingEdited
* The screen height should be resized above MinimumScreenHeight when the keyboard goes down
* contactFormInZoomedInMode should be set to false
* zoomedInInput should be hidden


Pre Load Animation
--------------------

* all of the name letters can be found on the DOM
* each of the letters gets an appropriate xPosition / left set
* onEnterFrame event listener is set and begins
* all the letter of the name spring and animate
* when the animation is finished it calls the main page

Pre Load Function
-----------------------

* when all of the images have finished loading and the animation has finished it should call startMain()





Pre Load All Images
------------------------

backgroundPanPic
src="../images/aboutPic.jpg" id="aboutPic" 
src="../images/runPicM.jpg" id="workPic" 
src="../images/keyPointPic.png" id="keyPointPic" 
src="../images/moodysPic.png" id="moodysPic" 
src="../images/lowRatePic.png" id="lowRatePic" 
src="../images/freelancePic.png" id="freelancePic" 
src="../images/mobilePicMed.jpg" id="mobilePic" 
src="../images/lucidAlarmIcon.png" id="lucidAlarmIcon" 
src="../images/quickKicksPic.png" id="quickKicksPic" 
src="../images/contactPicM.jpg" id="contactPic" 


Performance
-------------------------

* every single function must have a performance log tag in it and then we simply see which function are getting called too often







*/