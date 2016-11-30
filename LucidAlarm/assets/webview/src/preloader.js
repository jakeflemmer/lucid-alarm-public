
var preloadFrameCounter = 0;

var verticalCenter = 0;


var finishedLoadingImages = false;
var startedAnimation = false;

var imagesLoadedCounter = 0;
var imagesToLoad = 10;

var fontSizeDivisor = 15;


var preloaderSpring = 0.2;
var preloaderFriction = 0.7;

var onFirstImageLoadCycle = true;

var imageLoadDelayTime = 0;

var panArt= new Object(); 
var aboutArt= new Object(); 
var workArt= new Object();
var mobileArt= new Object();
var contactArt= new Object(); 

function preloadArt()
{

	setNewScreenHeightAndWidth();

	setScreenModeToPanoramic();

	initailizeArtObjects();
	
	loadAllImages();
}


function initailizeArtObjects () {

	initailizeArtObject(panArt,"background",1);
	initailizeArtObject(aboutArt,"Home",1);
	initailizeArtObject(workArt,"Work",1);
	initailizeArtObject(mobileArt,"Mobile",1);
	initailizeArtObject(contactArt,"Contact",1);

}
function initailizeArtObject (obj,name,toLoad) {
	obj.name = name;
	obj.imagesToLoad = toLoad;
	obj.imagesLoaded = 0;
}


function scaleAndMakeVisibleBackgroundPic()
{
	// this should just be set to 100% on the html element
	return;
	var preLoadBackgroundPic = document.getElementById("preloadbackgroundPic");
	preLoadBackgroundPic.style.width = screenWidth + "px";
	preLoadBackgroundPic.style.height = screenHeight + "px";
	preLoadBackgroundPic.style.opacity = 1.0;
}


function loadAllImages(){

	//if ( onFirstImageLoadCycle ) initProgressLoaderDiv();

	setTimeout( function () {


	var picYScale = calculatePicYScale();

	var panPic = document.getElementById("backgroundPanPic");
	if ( THEME == "spaceTheme" )
	{
		setPicSrcAttribute( panPic , "panPic4420", "png", picYScale , panArt);	
	}else if ( THEME == "cloudsTheme")
	{
		setPicSrcAttribute( panPic , "cloudsPanPic", "jpg", picYScale , panArt);
	}
	

	}, imageLoadDelayTime);
}


function setPicSrcAttribute( pic , picFile, picMime, picYScale , artObject)
{
	var FILE_PATH = "images/";

	pic.setAttribute("src", FILE_PATH + picFile + picYScale + "." + picMime);

	pic.onload = function () {
		artObject.isLoaded = true;
		artObject.imagesLoaded++;
		if ( currentPlanet.name == artObject.name )
		{
			window["draw" + artObject.name + "Page"](1);
		}else if ( artObject.name == "background" )
		{
			$("#backgroundPanPic").css({ opacity : .66 });	

			scaleTheBackgrounPanPicToTheScreenHeightWhilePreservingAspectRatio();		

			var backgroundPicturePanner = new BackgroundPicturePanner( "backgroundPanPic", backgroundPanPicScaledWidth, screenWidth, .1 ,100 );
			backgroundPicturePanner.startPanning();
		}
			
	} ;
	

}
function easeInBackgroundArt(){

	var curNewAlpha = $("#backgroundPanPic").css("opacity");
	if ( curNewAlpha < 1 )
	{
		var curOldAlpha = Number($("#preloadbackgroundPic").css("opacity")) - 0.01;
		var newA = Number(curNewAlpha) + 0.005;
		$("#preloadbackgroundPic").css({ opacity : curOldAlpha });
		$("#backgroundPanPic").css({ opacity : newA });
		setTimeout( easeInBackgroundArt , 250);
	}
}

function imageLoadedHandler()
{
	imagesLoadedCounter++;
	if ( imagesLoadedCounter == imagesToLoad )
	{
		finishedLoadingImages = true;
		//$("#progressLoaderDiv").css( { 'opacity' : '0.0' } );
		return;

	}

	$("#progressLoaderDiv").html( initializingPrefix + imagesLoadedCounter + " 0 %");
}

function calculatePicYScale()
{
	var is_chrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;
	var picYScale;

	return "Max";

	/*
	DISPLAY_TYPE = "DEVICE_MODE";
	if ( screenHeight > 0 && screenHeight < 300)
	{
		picYScale = "240";
	}
	else if ( screenHeight > 300 && screenHeight < 500 )
	{
		picYScale = "480";
	}
	else if ( screenHeight > 500 && screenHeight < 700 )
	{
		
		picYScale = "480";
		if ( is_chrome ) picYScale = "600";
	}
	else if ( screenHeight >= 700 )
	{	
		DISPLAY_TYPE = "DESKTOP_MODE";
		picYScale = "600";
		if ( is_chrome ) picYScale = "Max";
	}

	//...logs...
	boogieLog("calculatePicYScale() screenHeight : " + screenHeight + " picYScale : " + picYScale , SCALE_IMAGES);

	return picYScale;
	*/
}


			
function endPreload()
{

	$("#preloadbackgroundPic").css( { 'opacity' : '0' } );
	for ( var i = 0 ; i < lettersArray.length ; i++)
	{
		lettersArray[i].style.opacity = 0.0;
	}
	document.getElementById("progressLoaderDiv").style.opacity = 0.0;

	//...logs...
	boogieLog("endPreload() starting main after finished animation and loading images", PRE_LOAD);
	boogieLog("endPreload() preLoadBackgroundPic, letters and progressLoaderDiv opacity = 0.0" , PRE_LOAD_ANIMATION);

	//...
	startMain();	

	onFirstImageLoadCycle = false;
	// DANGER TODO loadAllImages();	
}



