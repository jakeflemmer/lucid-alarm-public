function setEventListeners()
{
	window.onresize = onWindowResize;
	window.onorientationchange = onWindowResize;

	var c=document.getElementById("stageCanvas");
	c.onmousemove = function(event) { onCanvasMouseOver(event); };

	window.onclick = function(event){ onCanvasClick(event) };

	//...logs...
	boogieLog("setEventListeners() PERFORMANCE " , PERFORMANCE ); 
}


function onWindowResize()
{


	setNewScreenHeightAndWidth();

	
	setScreenModeToPanoramic();
	
	scaleTheBackgrounPanPicToTheScreenHeightWhilePreservingAspectRatio();

	setNavCanvasHeightAndWidth();	

	setStageCanvasHeightAndWidth();

	

	//.... log ........
	boogieLog("onWindowResize() screenHeight : " + screenHeight + " screenWidth : " + screenWidth + " ratio : " + screenWidth / screenHeight, ON_WINDOW_RESIZE);
	if ( document.activeElement)
	{
		boogieLog("onWindowResize() document.activeElement.id : " + document.activeElement.id , ACTIVE_ELEMENT);	
	}else{
		boogieLog("onWindowResize() document.activeElement is null" , ACTIVE_ELEMENT);	
	}
	boogieLog("onWindowResize() PERFORMANCE " , PERFORMANCE );	

}

// THIS FUNCTION IS CALLED ONCE AT APP STARTUP - it differs from onWindowResize in that it doesn't kill the widgets;
function emulateWindowResize()
{


	setNewScreenHeightAndWidth();

		

	setScreenModeToPanoramic();
	
	scaleTheBackgrounPanPicToTheScreenHeightWhilePreservingAspectRatio();

	setNavCanvasHeightAndWidth();	

	setStageCanvasHeightAndWidth();
	
	




	//.... log ........
	boogieLog("onWindowResize() screenHeight : " + screenHeight + " screenWidth : " + screenWidth + " ratio : " + screenWidth / screenHeight, ON_WINDOW_RESIZE);
	if ( document.activeElement)
	{
		boogieLog("onWindowResize() document.activeElement.id : " + document.activeElement.id , ACTIVE_ELEMENT);	
	}else{
		boogieLog("onWindowResize() document.activeElement is null" , ACTIVE_ELEMENT);	
	}
	boogieLog("onWindowResize() PERFORMANCE " , PERFORMANCE );
	// if the window is resizing because a keyboad came up on a mobile device then we just want to zoom in to just the element on the contact form that is selected.
	//boogieLog("onWindowResize() nameTextInput form has focus : " + document.getElementById("nameTextInput").hasFocus()  , ACTIVE_ELEMENT);	

}

function onCanvasMouseOver(e)
{
	getCursorPosition(e);	
}

function onCanvasClick(e)
{
	//boogieLog("onCanvasClick" ,7);
	getCursorPosition(e);
	
	// hitTestRemAlarmLabel();
	// hitTestRemPowerIcon();
	// hitTestFinalAlarmLabel();
	// hitTestFinalPowerIcon();
	// hitTestRemSettingsIcon();
	// hitTestFinalSettingsIcon();

	remAlarmLabelWidget.hitTestAlarmLabel();
	remAlarmLabelWidget.hitTestPowerIcon();
	finalAlarmLabelWidget.hitTestAlarmLabel();
	finalAlarmLabelWidget.hitTestPowerIcon();


	//...logs...
	boogieLog("onCanvasClick()" , EVENT_LISTENERS);
}