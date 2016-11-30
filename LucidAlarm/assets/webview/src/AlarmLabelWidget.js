function AlarmLabelWidget(alarmName, canvasName, alarmLabelPrefix, iconSrc, xPos, yPos)
{
	this.alarmName = alarmName;
	this.canvasName = canvasName;
	var c=document.getElementById(this.canvasName );
	var ctx=c.getContext("2d");	
	this.canvasContext = ctx;
	makeCanvasContextFullScreen(ctx);

	this.alarmLabelPosition =  new Object();
	this.alarmPowerIconPosition = new Object();
	this.alarmPowerIcon = new Object();

	this.labelPrefix = alarmLabelPrefix;
	this.labelsXPos = xPos;
	this.labelYPos = yPos;

	this.iconSource = iconSrc;

}

AlarmLabelWidget.prototype.drawAlarmTimeWidget = function (timeSet) {
	
	this.canvasContext.clearRect ( 0 , 0 , screenWidth , screenHeight );

	// DRAW THE POWER ICON
	//-----------------------------------------------
	var  globalAlpha = 1;
	if ( timeSet == "Not Set" ) 
	{
		this.canvasContext.fillStyle = POWER_ICON_COLOR + ".25)";
		globalAlpha = 0.25;
		this.alarmPowerIcon.enabled = false;
	}else{
		this.canvasContext.fillStyle = POWER_ICON_COLOR + "1)";
		globalAlpha = 1;
		this.alarmPowerIcon.enabled = true;
	}
	boogieLog("drawAlarmTimeWidget() name: " + this.alarmName + " globalAlpha : " + globalAlpha + " timeSet : " + timeSet, 100);
	
	var fontSize; 
	if ( screenIsInPortraitMode() )
	{
		fontSize = screenHeight * .03;	
	}else{
		fontSize = screenWidth * .03;	
	}
	var settingsIconsXpos = screenWidth * 0.22;
   

    // ==============  DRAW THE POWER ICON IMAGE  ===============================
	this.alarmPowerIconPosition.left = this.labelsXPos - fontSize * 3;
	this.alarmPowerIconPosition.right = this.alarmPowerIconPosition.left + fontSize * 2;
	this.alarmPowerIconPosition.top = this.labelYPos - fontSize * 2;
	this.alarmPowerIconPosition.bottom = this.labelYPos + fontSize;
	 
	 	var that = this;

        var powerIcon = new Image;
		powerIcon.onload = function(){	
				that.canvasContext.save();
				that.canvasContext.globalAlpha = globalAlpha;			
				that.canvasContext.drawImage(powerIcon,that.alarmPowerIconPosition.left , that.alarmPowerIconPosition.top  , fontSize * 3,fontSize * 3);
				that.canvasContext.restore;
			};		
						
			powerIcon.src =  this.iconSource ;


	//=====================  LABELS  ============================
	this.canvasContext.globalAlpha = 1;
	this.alarmLabelPosition.left = this.labelsXPos + fontSize;
	this.alarmLabelPosition.right = screenWidth * 0.9;
	this.alarmLabelPosition.top = this.labelYPos - fontSize * 2;
	this.alarmLabelPosition.bottom = this.labelYPos + fontSize;

		this.canvasContext.fillStyle = ALARM_LABELS_COLOR;
		this.canvasContext.font = fontSize  + "px " + TIME_FONT;
	
		this.canvasContext.fillText(this.labelPrefix + timeSet, this.alarmLabelPosition.left  , this.labelYPos);
}

//=====================================
// HIT TESTS
//=====================================

AlarmLabelWidget.prototype.hitTestAlarmLabel = function () 
{	
	if ( positionObject.xPos > this.alarmLabelPosition.left && positionObject.xPos < this.alarmLabelPosition.right)
	{
		if ( positionObject.yPos > this.alarmLabelPosition.top && positionObject.yPos < this.alarmLabelPosition.bottom)	
		{
			if ( this.alarmName == "rem")
			{
				Android.onREMAlarmTextViewClick();
			}else{
				Android.onFinalAlarmTextViewClick();
			} 
		}
	}
}

AlarmLabelWidget.prototype.hitTestPowerIcon = function () 
{
	if ( positionObject.xPos > this.alarmPowerIconPosition.left && positionObject.xPos < this.alarmPowerIconPosition.right)
	{
		if ( positionObject.yPos > this.alarmPowerIconPosition.top && positionObject.yPos < this.alarmPowerIconPosition.bottom)	
		{
			if ( this.alarmPowerIcon.enabled == true )
			{
				if ( this.alarmName == "rem")
				{
					Android.onREMPowerIconClick();
				}else{
					Android.onFinalPowerIconClick();
				} 
			}
		}
	}
}

