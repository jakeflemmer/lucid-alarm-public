
var positionObject = new Object();

var rightArrowLeft = 0;
var rightArrowRight = 0;
var leftArrowLeft = 0;
var leftArrowRight = 0;

var rightArrowBottom = 0;
var rightArrowTop = 0;
var leftArrowBottom = 0;
var leftArrowTop = 0;

var bodyTop = 0;

var moodysPageShowingMoreText = false;

var tvSignalAlpha = 0;
var tvSignalFrames = 0;
var tvSignalHeight = 0;
var tvSignalPlanet;
var totalTvSignalFrames = 4;


//-----------------------------------------------
// CURSOR
//-----------------------------------------------
function getCursorPosition(e) 
{	
    var x;
    var y;
    if (e.pageX != undefined && e.pageY != undefined) {
		x = e.pageX;
		y = e.pageY;
    }
    else 
    {
		x = e.clientX + document.body.scrollLeft +
            document.documentElement.scrollLeft;
		y = e.clientY + document.body.scrollTop +
            document.documentElement.scrollTop;
    }
    positionObject.xPos = x;
    positionObject.yPos = y - bodyTop;
}

function springFromCursorPosition() 
{
	//boogieLog("e is : " + e,1);
	
	
    var x = positionObject.xPos;
    var y = positionObject.yPos;
    
    if ( y > homePlanet.yPos - navPlanetRadius && y < homePlanet.yPos + navPlanetRadius)
    {
    	if ( x > homePlanet.xPos - navPlanetRadius && x < homePlanet.xPos + navPlanetRadius)
    	{
    		springToPlanet ( homePlanet );
    	}
    	if ( x > workPlanet.xPos - navPlanetRadius && x < workPlanet.xPos + navPlanetRadius)
    	{
    		springToPlanet ( workPlanet );
    	}
    	if ( x > mobilePlanet.xPos - navPlanetRadius && x < mobilePlanet.xPos + navPlanetRadius)
    	{
    		springToPlanet ( mobilePlanet );
    	}
    	if ( x > contactPlanet.xPos - navPlanetRadius && x < contactPlanet.xPos + navPlanetRadius)
    	{
    		springToPlanet ( contactPlanet );
    	}
    }
}

//-----------------------------------------------
// ADVANCE PAGES
//-----------------------------------------------
function advanceWorkOrMobilePages()
{
	var x = positionObject.xPos;
    var y = positionObject.yPos;
    
    if ( currentPlanet.name != "Work" && currentPlanet.name != "Mobile") return;

    if ( y > pageTop && y < pageTop + pageHeight)
    {
    	// TODO get rid of rightArrowLeft etc variables - we dont need them
    	// if ( x > rightArrowLeft && x < rightArrowRight )
    	// {
    	// 	window["advance" + currentPlanet.name + "Page"](1);
    	// 	return;
    	// }
    	//  if ( x > leftArrowLeft && x < leftArrowRight )
    	// {
    	//  	window["advance" + currentPlanet.name + "Page"](-1);
    	//  	return;
    	// }
    	if ( x > screenWidth * .66 && x < screenWidth )
    	{
    		window["advance" + currentPlanet.name + "Page"](1);
    		boogieLog("advanceWorkOrMobilePages() right arrow clicked ", ARROWS_CLICKED);
    		return;
    	}
    	if ( currentPlanet.name == "Work" && workPageState == 3 )
    	{
    		// freelance page
    		if ( x > pageLeft + pageWidth * 0.15 && x < pageLeft + pageWidth * 0.3 )
    		{
    			//navigate to low rate movers
    			window.location.href = 'http://www.lowratemovers.com';

    		}
    		if ( x > pageLeft + pageWidth * 0.3 && x < pageLeft + pageWidth * 0.6 )
    		{
    			//navigate to durban elephant
    			window.location.href = 'http://www.durbanelephant.com';
    		}
    	}
    	 if ( x > 0 && x < pageLeft + pageWidth * 0.3 )
    	{
    		if ( y > pageTop + ( pageHeight * 0.8 ) && workPageState > 1 )
    		{
    			onMoodysMoreLessSpanClick('less');
    			return;
    		}
    	 	window["advance" + currentPlanet.name + "Page"](-1);
    	 	boogieLog("advanceWorkOrMobilePages() left arrow clicked ", ARROWS_CLICKED);
    	 	return;
    	}
    	
    }	

    //...logs...
    boogieLog("advanceWorkOrMobilePages() neither arrow clicked ", ARROWS_CLICKED);
}

//-----------------------------------------------
// SCALE
//-----------------------------------------------



function scaleAndPositionTextDiv( textDiv, alpha, textLeft , textWidth, marginBottom)
{

	textDiv["css"]( {'opacity' : alpha , 'left' : textLeft , 'width' : textWidth , 'top' : pageTop  } );

	var fontSize = 80;

	for ( var i = 0 ; i < 100 ; i++)
	{

		textDiv["css"]( {'font-size' : fontSize + 'px'} );

		var newTextHeight = textDiv["height"]();

		if ( newTextHeight > pageHeight - marginBottom )
		{
			fontSize -= 1;
		}
		else{
			break;
		}
	} 

	//...logs...
	boogieLog("scaleAndPositionTextDiv PERFORMANCE", PERFORMANCE);

	return fontSize;

}



function scaleAndDrawImageOnCanvas(img,widthRatio,alpha)
{
	
	var c=document.getElementById("stageCanvas");
	var ctx=c.getContext("2d");

	ctx.save();
	ctx.globalAlpha = alpha;

	ctx.drawImage(img, pageLeft , pageTop, widthRatio, pageHeight);

	ctx.restore();
}

function scaleAndDrawImageWithPadding(img,widthRatio,alpha,top,left)
{
	
	var c=document.getElementById("stageCanvas");
	var ctx=c.getContext("2d");
	
	var height = pageHeight - ( top * 2);
	
	ctx.save();
	ctx.globalAlpha = alpha;

	ctx.drawImage(img, pageLeft + left , pageTop + top, widthRatio, height);

	ctx.restore();
}


//-----------------------------------------------
// CLEAR TEXT
//-----------------------------------------------

function clearAllPagesText()
{
	$("#homeTextDiv").css( {'opacity' : 0 } );
	$("#workPageDiv").css( {'opacity' : 0 } );
	$("#mobilePageDiv").css( {'opacity' : 0 } );
	$("#contactFormDiv").css( {'opacity' : 0 } );
	$("#visualAlchemyDiv").css( {'opacity' : 0 } );
	$("#moodysDiv").css( {'opacity' : 0 } );
	$("#freelanceDiv").css( {'opacity' : 0 } );
	$("#lucidAlarmDiv").css( {'opacity' : 0 } );
	$("#quickKicksDiv").css( {'opacity' : 0 } );
}
function clearArrows()
{
	$("#leftArrowDiv").css( { 'opacity' : 0.0 });
	$("#rightArrowDiv").css( { 'opacity' : 0.0 });
	boogieLog("clearArrows()", DRAW_ARROWS );
}
function calculateAndDisplayFrameRate()
{
	frameCounter++;
	var currentTime = new Date().getTime();
	var elapsedTime = (currentTime - startTime)/1000;

	if ( frameCounter == 100)
	{
		boogieLog( "frameRate = " + frameCounter / elapsedTime , 5);
	boogieLog( "elapsedTime = " +  elapsedTime  , 5);
		frameCounter = 0;
		startTime = new Date().getTime();
	}
}	

//-----------------------------------------------
// DRAW ARROWS
//-----------------------------------------------

function scaleAndDrawArrows(navPlanet, alpha)
{	
	var c=document.getElementById("stageCanvas");
	var ctx=c.getContext("2d");

	var rightImg;
	var leftImg;
	var imgHeight = pageHeight / 2;
	var imgWidth = pageWidth / 10;
	var horizontalOffset = 0;	//imgWidth/3;

	if ( alpha < 0 ) alpha = 0;

	var breadth = pageHeight * 0.1;
	var length = pageWidth * 0.1;

	var arrowColor = currentPlanet.color;

	var drawLeftArrow = false;
	var drawRightArrow = true;

	if ( workPageState > 0 )
	{
		arrowColor = "red";
		drawLeftArrow = true;
		if ( workPageState >= workPageStateMax)
		{
			drawRightArrow = false;
		}
	}
	

	if ( mobilePageState > 0 )
	{
		arrowColor = "blue";
		drawLeftArrow = true;
		if ( mobilePageState >= mobilePageStateMax)
		{	
			drawRightArrow = false;
		}
	}
	
	var fontSize = screenHeight / 8;
	var divTop = pageHeight / 2 + pageTop - fontSize / 2
	var divLeft = 0;

	if ( drawRightArrow)
	{
		//drawArrow(pageLeft + pageWidth - horizontalOffset , pageHeight / 2 + pageTop - breadth / 2,"right",length,breadth,arrowColor,alpha);	
		divLeft = pageLeft + pageWidth - horizontalOffset;
		$("#rightArrowDiv").css( { 'opacity' : alpha , 'left' : divLeft + "px" , 'font-size' : fontSize + 'px' , 'top' : divTop + 'px' , 'color' : arrowColor } );
	}else{
		$("#rightArrowDiv").css( { 'opacity' : '0.0' } );
	}
	
	if ( drawLeftArrow )
	{
		//drawArrow(pageLeft , pageHeight / 2 + pageTop - breadth / 2,"left",length,breadth,arrowColor,alpha);		
		divLeft = pageLeft - fontSize;

		$("#leftArrowDiv").css( { 'opacity' : alpha , 'left' : divLeft + "px" , 'font-size' : fontSize + 'px' , 'top' : divTop + 'px' , 'color' : arrowColor } );
	}else{
		$("#leftArrowDiv").css( { 'opacity' : '0.0' } );
	}

	//...logs...
	boogieLog("drawArrow alpha " + alpha + "  drawRightArrow = " + drawRightArrow, DRAW_ARROWS);

}

function clearStageCanvas()
{
	var c=document.getElementById("stageCanvas");
	var ctx=c.getContext("2d");
	ctx.clearRect ( 0 , 0 , screenWidth , screenHeight );
}






function contactFormTextInputOrAreaIsSelected()
{
	var contactFormTextInputOrAreaIsSelected  = false;

	if ( document.activeElement )
	{
		if ( document.activeElement.id )
		{
			if ( document.activeElement.id == "nameTextInput" || document.activeElement.id == "emailTextInput" || document.activeElement.id == "messageTextArea" )
			{
				contactFormTextInputOrAreaIsSelected = true;
				if ( document.activeElement.id == "nameTextInput" ) contactFormElementBeingEdited = document.getElementById('nameTextInput');
				if ( document.activeElement.id == "emailTextInput" ) contactFormElementBeingEdited = document.getElementById('emailTextInput');
				if ( document.activeElement.id == "messageTextArea" ) contactFormElementBeingEdited = document.getElementById('messageTextArea');
				boogieLog("contactFormTextInputOrAreaIsSelected() contactFormElementBeingEdited = " + document.activeElement.id , ZOOM_CONTACT_ELEMENT);
			}
		}
	}


	//...logs...
	boogieLog("contactFormTextInputOrAreaIsSelected() returns : " + contactFormTextInputOrAreaIsSelected, CONTACT_FORM_SELECTED);
	boogieLog("contactFormTextInputOrAreaIsSelected() returns : " + contactFormTextInputOrAreaIsSelected, ZOOM_CONTACT_ELEMENT);

	return contactFormTextInputOrAreaIsSelected;
}

function zoomedInInputIsSelected()
{
	var zoomedInInputIsSelected  = false;

	if ( document.activeElement )
	{
		if ( document.activeElement.id )
		{
			if ( document.activeElement.id == "zoomedInInput" )
			{
				zoomedInInputIsSelected = true;
			}
		}
	}


	//...logs...
	boogieLog("zoomedInInputIsSelected() returns : " + zoomedInInputIsSelected, ZOOMED_IN_INPUT_SELECTED);
	boogieLog("zoomedInInputIsSelected() returns : " + zoomedInInputIsSelected, ZOOM_CONTACT_ELEMENT);
	

	return zoomedInInputIsSelected;	
}



// we are done w click catching canvas
// function scaleClickCatchingCanvas()
// {
// 	$("#clickCatcherCanvas").css( { 'width' : screenWidth + "px" , 'height' : screenHeight + "px" } );	
// }

//==========================================================================================
// MOODYS MORE LESS SPANS CLICK
//==========================================================================================

function onMoodysMoreLessSpanClick( moreOrLess )
{
	scaledFontSize['moodysDiv'] = 0; 
	clearStageCanvas();
	drawPageBackground ( currentPlanet , 1 );
	
	if ( moreOrLess == "more")
	{
		$('#lessMoodysDiv').css( { 'display' : 'none' } );
		$('#moreMoodysDiv').css( { 'display' : 'block' } ); 
	
		moodysPageShowingMoreText = true;
		drawMoodysPage();
		boogieLog("onMoodysMoreSpanClick()", MOODYS_MORE_SPAN);
	}
	else
	{
		$('#moreMoodysDiv').css( { 'display' : 'none' } );
		$('#lessMoodysDiv').css( { 'display' : 'block' } );
	
		moodysPageShowingMoreText = false;
		drawMoodysPage(); 
		boogieLog("onMoodysLessSpanClick()", MOODYS_MORE_SPAN);
	}
}

//==========================================================================================
// the lost tv signal effect
//==========================================================================================

/*
function playLostTvSignalEffect()
{
	var c=document.getElementById("tvSignalCanvas");
	var ctx=c.getContext("2d");

	ctx.canvas.width = pageWidth;	
	ctx.canvas.height = pageHeight;	
	
	$('#tvSignalCanvas').css( { 'top' : pageTop + 'px' , 'left' : pageLeft + 'px' } );

	//ctx.top = pageTop + 'px';
	//ctx.left = pageLeft + 'px';
	tvSignalHeight = pageHeight;
	tvSignalPlanet = currentPlanet;
	tvSignalFrames = 0;
	tvSignalAlpha = 0;
	ctx.globalAlpha = tvSignalAlpha;
	
	lostTvSignal();
}
function lostTvSignal()
{
	
	var c=document.getElementById("tvSignalCanvas");
	var ctx=c.getContext("2d");


	tvSignalFrames ++;
	
	// black outer box
	ctx.fillStyle = "black";
	ctx.fillRect(0, 0, pageWidth, pageHeight);  
	

	if ( tvSignalFrames < totalTvSignalFrames * 0.5 )
	{
		tvSignalAlpha += 1 / ( totalTvSignalFrames * 0.5 );
		tvSignalHeight -= pageHeight * (1 / ( totalTvSignalFrames * 0.5 ));	//0.1;
		ctx.fillStyle = tvSignalPlanet.color;
	}
	else
	{
		ctx.fillStyle = currentPlanet.color;
		tvSignalAlpha -= 1 / ( totalTvSignalFrames * 0.5 );
		tvSignalHeight += pageHeight * (1 / ( totalTvSignalFrames * 0.5 ));	//0.1;
	}

	if ( tvSignalFrames == totalTvSignalFrames * 0.5 )
	{
		// switch from playing lost tv signal to found tv signal
		clearAllPagesText();
		clearStageCanvas();
		drawPages( currentPlanet , .8 );	
	}
	
	//ctx.globalAlpha = tvSignalAlpha;

	// colored inner box
	ctx.fillRect( 0, (pageHeight*0.5) - tvSignalHeight*0.5 , pageWidth, tvSignalHeight );  

	if ( tvSignalFrames < totalTvSignalFrames )
	{
		setTimeout( lostTvSignal , frameRate * .01 ); 
	}
	else
	{
		// our work here is done
		endTvSignal();
	}
}
function endTvSignal()
{
	var c=document.getElementById("tvSignalCanvas");
	var ctx=c.getContext("2d");

	ctx.canvas.width = 0;	
	ctx.canvas.height = 0;
}
*/
		// we overlay a rectangular 'tvSignalCanvas' over the entire planet page
		// the closeTvSignal function begins to play ( these r independent of the main onEnterFrame function )
			// inside the tvSignalCanvas a rectangle of the appropraite color -
			// gets drawn to cover the whole page
			// the canvas itself starts black and transparent
			// the inner rect dimishes in height while the opacity of the whole canvas increments
			// at the end of the effect
			// there is a frame of pure black then
			// the planet page itself gets hidden
		// the openTvSignal effect begins to play
			// the underlying planet page get shown
			// the tvSignalCanvas inner rect changes to new color
			// the inner rect increases in height as the canvas as a whole decreases in alpha





	


