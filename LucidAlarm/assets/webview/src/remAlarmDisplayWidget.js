var remAlarmLabelPosition = new Object();
var finalAlarmLabelPosition = new Object();
var remAlarmPowerIconPosition = new Object();
var finalAlarmPowerIconPosition = new Object();
var remAlarmPowerIcon = new Object();
var finalAlarmPowerIcon = new Object();
var remAlarmSettingsIconPosition = new Object();
var finalAlarmSettingsIconPosition = new Object();




// function showAlarmTimeDisplays ( remTimeSet , finalTimeSet) {

// 	var c=document.getElementById("alarmTimeCanvas");
// 	var ctx=c.getContext("2d");
// 	ctx.canvas.width = screenWidth;	
// 	ctx.canvas.height = screenHeight;	

// 	var remYPos = screenHeight * .80;
// 	var finalYPos = screenHeight * .90;

// 	var prefix;
// 	prefix = "REM Staryyy : ";
// 	drawAlarmTimeWidget( remTimeSet , remAlarmPowerIcon , remAlarmPowerIconPosition , remAlarmLabelPosition, remYPos, ctx, prefix,remAlarmSettingsIconPosition, false);
// 	prefix = "Final Alarm : ";
// 	drawAlarmTimeWidget( finalTimeSet , finalAlarmPowerIcon , finalAlarmPowerIconPosition , finalAlarmLabelPosition, finalYPos, ctx, prefix, finalAlarmSettingsIconPosition,true);
// }

function drawAlarmTimeWidget( timeSet , alarmPowerIcon , alarmPowerIconPosition , alarmLabelPosition, labelYPos, ctx, prefix, alarmSettingsIconPosition,dontDrawSettingsIcon)
{
	// DRAW THE POWER ICON
	//-----------------------------------------------
	var  globalAlpha = 1;
	if ( timeSet == "Not Set" ) 
	{
		ctx.fillStyle = POWER_ICON_COLOR + ".25)";
		globalAlpha = 0.4;
		alarmPowerIcon.enabled = false;
	}else{
		ctx.fillStyle = POWER_ICON_COLOR + "1)";
		globalAlpha = 1;
		alarmPowerIcon.enabled = true;
	}

	var fontSize = screenHeight * .03;
	var labelsXPos = screenWidth * 0.33;
	var settingsIconsXpos = screenWidth * 0.22;

	/*  DRAW IT YOURSELF
	// outer circle
	ctx.beginPath();
        ctx.arc(labelsXPos, labelYPos- fontSize*.5 , fontSize, 0, Math.PI*2, false);                // outer circle
        ctx.lineWidth = 2;        
        ctx.stroke();
        ctx.closePath();
    // inner circle
	ctx.beginPath();
        ctx.arc(labelsXPos, labelYPos- fontSize*.5 , fontSize*.7, ( Math.PI* 1.6), ( Math.PI* 1.4), false);        // inner circle
        ctx.lineWidth = 3;        
        ctx.stroke();
        ctx.closePath();
    ctx.beginPath();
    	ctx.moveTo(labelsXPos, labelYPos - fontSize );
    	ctx.lineTo(labelsXPos, labelYPos - fontSize *.6 );
    	ctx.lineWidth = 2;        
        ctx.stroke();
        ctx.closePath();
        */

   

    // ==============  DRAW THE POWER ICON IMAGE  ===============================
	alarmPowerIconPosition.left = labelsXPos - fontSize * 3;
	alarmPowerIconPosition.right = alarmPowerIconPosition.left + fontSize * 2;
	alarmPowerIconPosition.top = labelYPos - fontSize * 2;
	alarmPowerIconPosition.bottom = labelYPos + fontSize;
	 
        var powerIcon = new Image;
		powerIcon.onload = function(){	
				ctx.save();
				ctx.globalAlpha = globalAlpha;			
				ctx.drawImage(powerIcon,alarmPowerIconPosition.left , alarmPowerIconPosition.top  , fontSize * 3,fontSize * 3);
				ctx.restore;
			};			
			powerIcon.src =  "images/ic_power.png";


	//=====================  LABELS  ============================
	alarmLabelPosition.left = labelsXPos + fontSize;
	alarmLabelPosition.right = screenWidth * 0.72;
	alarmLabelPosition.top = labelYPos - fontSize * 2;
	alarmLabelPosition.bottom = labelYPos + fontSize;

		ctx.fillStyle = ALARM_LABELS_COLOR;
		ctx.font = fontSize  + "px " + TIME_FONT;
	
		ctx.fillText(prefix + timeSet, alarmLabelPosition.left  , labelYPos);

		// if ( THEME == "cloudsTheme")
		// {
		// 	ctx.strokeStyle = ALARM_LABELS_STROKE_COLOR;
		// 	ctx.strokeText(prefix + timeSet, alarmLabelPosition.left  , labelYPos);
		// }

	//====================== SETTING ICON  ======================
	
	alarmSettingsIconPosition.left = settingsIconsXpos ;
	alarmSettingsIconPosition.right = alarmSettingsIconPosition.left + fontSize * 2;
	alarmSettingsIconPosition.top = alarmPowerIconPosition.top ;
	alarmSettingsIconPosition.bottom = labelYPos + fontSize;
	
	if ( dontDrawSettingsIcon == true) return;

	var settingsIcon = new Image;
		settingsIcon.onload = function(){	
				ctx.globalAlpha = .66;			
				ctx.drawImage(settingsIcon,alarmSettingsIconPosition.left, alarmPowerIconPosition.top, alarmSettingsIconPosition.right - alarmSettingsIconPosition.left, alarmSettingsIconPosition.right - alarmSettingsIconPosition.left);
			};			
			settingsIcon.src =  "images/ic_settings.png";


	boogieLog("showAlarmTimeDisplays timeSet : " + timeSet ,3);

}
//=====================================
// REM
//=====================================
function hitTestRemAlarmLabel () {

	boogieLog("positionObject.xPos " + positionObject.xPos + " , remAlarmLabelPosition.left " + remAlarmLabelPosition.left + " remAlarmLabelPosition.right " + remAlarmLabelPosition.right ,30);

	if ( positionObject.xPos > remAlarmLabelPosition.left && positionObject.xPos < remAlarmLabelPosition.right)
	{
		if ( positionObject.yPos > remAlarmLabelPosition.top && positionObject.yPos < remAlarmLabelPosition.bottom)	
		{
			Android.onREMAlarmTextViewClick();
		}
	}
}

function hitTestRemPowerIcon () {

	if ( positionObject.xPos > remAlarmPowerIconPosition.left && positionObject.xPos < remAlarmPowerIconPosition.right)
	{
		if ( positionObject.yPos > remAlarmPowerIconPosition.top && positionObject.yPos < remAlarmPowerIconPosition.bottom)	
		{
			if ( remAlarmPowerIcon.enabled == true )
			{
				Android.onREMPowerIconClick();
			}
		}
	}
}

function hitTestRemSettingsIcon () {

	if ( positionObject.xPos > remAlarmSettingsIconPosition.left && positionObject.xPos < remAlarmSettingsIconPosition.right)
	{
		if ( positionObject.yPos > remAlarmSettingsIconPosition.top && positionObject.yPos < remAlarmSettingsIconPosition.bottom)	
		{
			Android.onREMSettingsIconClick();
		}
	}
}

// FINAL
//----------------------
function hitTestFinalAlarmLabel () {

	if ( positionObject.xPos > finalAlarmLabelPosition.left && positionObject.xPos < finalAlarmLabelPosition.right)
	{
		if ( positionObject.yPos > finalAlarmLabelPosition.top && positionObject.yPos < finalAlarmLabelPosition.bottom)	
		{
			Android.onFinalAlarmTextViewClick();
		}
	}
}

function hitTestFinalPowerIcon () {

	if ( positionObject.xPos > finalAlarmPowerIconPosition.left && positionObject.xPos < finalAlarmPowerIconPosition.right)
	{
		if ( positionObject.yPos > finalAlarmPowerIconPosition.top && positionObject.yPos < finalAlarmPowerIconPosition.bottom)	
		{
			if ( finalAlarmPowerIcon.enabled == true )
			{
				Android.onFinalPowerIconClick();
			}
		}
	}
}

function hitTestFinalSettingsIcon () {

	if ( positionObject.xPos > finalAlarmSettingsIconPosition.left && positionObject.xPos < finalAlarmSettingsIconPosition.right)
	{
		if ( positionObject.yPos > finalAlarmSettingsIconPosition.top && positionObject.yPos < finalAlarmSettingsIconPosition.bottom)	
		{
			Android.onFinalSettingsIconClick();
		}
	}
}

