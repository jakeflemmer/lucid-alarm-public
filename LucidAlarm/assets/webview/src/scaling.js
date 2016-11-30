

var screenHeight;
var screenWidth;

function  setGlobalScalingVariables () {
	
	setNewScreenHeightAndWidth();

	setScreenModeToPanoramic();
	
	scaleTheBackgrounPanPicToTheScreenHeightWhilePreservingAspectRatio();

	setNavCanvasHeightAndWidth();	

	setStageCanvasHeightAndWidth();	
	
}

function displayMode () {

	if ( killWidgets ) return "mobile";
	
	if (screenHeight > 700)
	{
		return "desktop";
	}else{
		return "mobile";
	}

}

function setNewScreenHeightAndWidth()
{
	screenHeight = (window.innerHeight > 0) ? window.innerHeight : window.screen.height;
	screenWidth = (window.innerWidth > 0) ? window.innerWidth : window.screen.width;

	boogieLog("setNewScreenHeightAndWidth() screen.height = " + window.screen.height + "  screen.width = " + window.screen.width , GET_SCREEN_DIMENSIONS);
	boogieLog("setNewScreenHeightAndWidth() window.innerHeight = " + window.innerHeight + "  window.innerWidth = " + window.innerWidth , GET_SCREEN_DIMENSIONS);
	boogieLog("setNewScreenHeightAndWidth() screenHeight = " + screenHeight + "  screenWidth = " + screenWidth , GET_SCREEN_DIMENSIONS);
}
function setScreenModeToPanoramic()
{
	// for lucid alarm backgrounds we dont want to letterbox
	return;

	// 1.8 - 3.0 width to height ratio is our comfort zone.
	// we are good all the way to almost 4.0 w x h ratio
	// so we need plan only for the eventuality of less than 1.8 w x h ratio
	// in this case we begin creating a panaramic effect immediatley and begin letterbox proceedings
	// ahh - but on iOS the device ratio is 1.5 so we have to design for that

	var whRatio = screenWidth / screenHeight;
	//if ( whRatio < 1.8 )
	if ( whRatio < 1.5 )
	{
		// make panoramic
		var windowHeight = screenHeight;

		screenHeight = screenWidth / 1.5;
		bodyTop = (windowHeight - screenHeight) / 2;

		boogieLog("setScreenModeToPanoramic() panoramification performed - new screenHeight = " + screenHeight, ON_WINDOW_RESIZE);

	}else{
		bodyTop = 0;
	}

	$('body').css( { "top" :  bodyTop + "px" }); 
}

function scaleTheBackgrounPanPicToTheScreenHeightWhilePreservingAspectRatio()
{
	var img = document.getElementById('backgroundPanPic');

	var imgWidth = img.clientWidth;
	var imgHeight = img.clientHeight;

	var screenHeightToPanPicHeightRatio = screenHeight / imgHeight;

	backgroundPanPicScaledWidth = imgWidth * screenHeightToPanPicHeightRatio;

	$("#backgroundPanPic").css({ height : screenHeight+"px" });

	// okay so this backgroundPanPicScaledWidth is the width of the whole pan pic image
	// we want to pan to a certain percentage of this based on the width of the screen divided by the width of the scaled pan pic width

	var screenWidthToScaledPanPicWidthRatio = screenWidth / backgroundPanPicScaledWidth;

	var pannableWidth = backgroundPanPicScaledWidth - screenWidth;

	homePlanetXPan = - ( 0.15 * pannableWidth );
	workPlanetXPan = - ( 0.47 * pannableWidth );
	mobilePlanetXPan = - ( 0.62 * pannableWidth );
	contactPlanetXPan = - ( 0.87 * pannableWidth );

	
	//...logs...
	boogieLog("scaleTheBackgrounPanPicToTheScreenHeightWhilePreservingAspectRatio scaled pan pic width = "  + backgroundPanPicScaledWidth + " , screenHeight = " + screenHeight,3);
	boogieLog("scaleTheBackgrounPanPicToTheScreenHeightWhilePreservingAspectRatio PERFORMANCE", PERFORMANCE);

}
function removeSideWidgets () {

	// clear all the canvases	
	setStageCanvasHeightAndWidth();
	setTimeAndDateWidgetCanvasHeightAndWidth();
	setCircleWidgetCanvasHeightAndWidth();
	setWeatherWidgetCanvasHeightAndWidth();
	setWeatherIconWidgetCanvasHeightAndWidth();
	setNewsWidgetCanvasHeightAndWidth();
	setFinanceWidgetCanvasHeightAndWidth();
	setBarCodeWidgetCanvasHeightAndWidth();

}


function makeCanvasContextFullScreen(ctx)
{		
	ctx.canvas.width = screenWidth;	
	ctx.canvas.height = screenHeight;	
}



// below is idiotic
function setNavCanvasHeightAndWidth()
{
	var c=document.getElementById("navCanvas");
	var ctx=c.getContext("2d");
	ctx.canvas.width = screenWidth;	
	ctx.canvas.height = screenHeight;	
}

function setStageCanvasHeightAndWidth()
{
	var c=document.getElementById("stageCanvas");
	var ctx=c.getContext("2d");
	ctx.canvas.width = screenWidth;	
	ctx.canvas.height = screenHeight;	
}

function setTimeAndDateWidgetCanvasHeightAndWidth()
{
	var c=document.getElementById("timeAndDateWidgetCanvas");
	var ctx=c.getContext("2d");
	ctx.canvas.width = screenWidth;	
	ctx.canvas.height = screenHeight;	
}

function setCircleWidgetCanvasHeightAndWidth()
{
	var c=document.getElementById("circleWidgetCanvas");
	var ctx=c.getContext("2d");
	ctx.canvas.width = screenWidth;	
	ctx.canvas.height = screenHeight;	
}


function setWeatherWidgetCanvasHeightAndWidth()
{
	var c=document.getElementById("weatherWidgetCanvas");
	var ctx=c.getContext("2d");
	ctx.canvas.width = screenWidth;	
	ctx.canvas.height = screenHeight;	
}

function setWeatherIconWidgetCanvasHeightAndWidth()
{
	var c=document.getElementById("weatherIconWidgetCanvas");
	var ctx=c.getContext("2d");
	ctx.canvas.width = screenWidth;	
	ctx.canvas.height = screenHeight;	
}

function setNewsWidgetCanvasHeightAndWidth()
{
	var c=document.getElementById("newsWidgetCanvas");
	var ctx=c.getContext("2d");
	ctx.canvas.width = screenWidth;	
	ctx.canvas.height = screenHeight;	
}

function setFinanceWidgetCanvasHeightAndWidth()
{
	var c=document.getElementById("financeWidgetCanvas");
	var ctx=c.getContext("2d");
	ctx.canvas.width = screenWidth;	
	ctx.canvas.height = screenHeight;	
}

function setBarCodeWidgetCanvasHeightAndWidth()
{
	var c=document.getElementById("barCodeWidgetCanvas");
	var ctx=c.getContext("2d");
	ctx.canvas.width = screenWidth;	
	ctx.canvas.height = screenHeight;	
}
