// WIDGET ORDER I

// We are aiming for an agnostic circle that animates at fixed rate

// 'GLOBAL' VARIABLES
//----------------------------
var centerX;
var centerY;

var circleWidgetMaxRadius;
var circleWidgetBottom;



//------------------------------

var circlesArray = new Array();

var minCircleSize;
var maxStrokeWidth;
var minStokeAlpha = .2;
var numberOfCircles = 4;

var trueTimeSet = false;
var trueTime;
var hoursAndMinsTime;
var secondsTime;
var meridian;

function initCircleWidget () {	

	setCircleWidgetCanvasHeightAndWidth();

	localCircleScalingVariables();
	
	defineCircleObject();

	widgetEnterFrame();

	circleWidgetBottom =  circleWidgetMaxRadius * 2;

	boogieLog("circleLoadingWidget",3);
}

function localCircleScalingVariables () {

	minCircleSize = screenWidth * .1;
	maxStrokeWidth = minCircleSize * .25;

}

function defineCircleObject () {

	for ( i = 0 ; i < numberOfCircles; i++ ) {
		var newCircle = new Object();
		if ( i == 0 )
		{
			newCircle.radius = minCircleSize;
		}
		circlesArray.push(newCircle);		
	}	
	var lastCircle;
	var circle;
	

	for ( i = 0 ; i < circlesArray.length; i++ ) {
		circle = circlesArray[i];
		circle.startArc = Math.PI*Math.random();
		circle.endArc = circle.startArc + Math.PI + ( Math.PI  *  Math.random() );
		circle.strokeWidth = 2+( maxStrokeWidth * Math.random() );

		var alpha = Math.random();
		if ( alpha < 0.1 ) alpha = 0.1;
		circle.strokeColor = CIRCLE_COLOR + alpha + ")";
		if ( i > 0 )
		{
			circle.radius = lastCircle.radius + ( lastCircle.strokeWidth  ) + 5;
		}
		
		circle.speed = i*200;
		if ( Math.random() < 0.5 ) circle.speed *= -1;
		lastCircle = circle;
		circleWidgetMaxRadius = circle.radius + circle.strokeWidth;
	}	
	centerX = circleWidgetMaxRadius;
	centerY = circleWidgetMaxRadius;	

	boogieLog("defineCircleObject centerX : " + centerX + " centerY " + centerY ,3);

}


function widgetEnterFrame () {
	

	var c=document.getElementById("circleWidgetCanvas");
	var ctx=c.getContext("2d");

	ctx.clearRect ( 0 , 0 , screenWidth , screenHeight );

	// DRAW THE CIRCLES
	//=============================
	for ( i = 0 ; i < circlesArray.length; i++ ) {
		var circle = circlesArray[i];
		
		ctx.beginPath();
        ctx.arc(centerX, centerY, circle.radius, circle.startArc, circle.endArc, false);                
        ctx.lineWidth = circle.strokeWidth;
        ctx.strokeStyle = circle.strokeColor;
        ctx.stroke();
	}
		
	ctx.font= "" + minCircleSize * .75 + "px spaceFont";
	ctx.fillStyle= "green";
	ctx.strokeStyle= "green";
	ctx.lineWidth = 1;
	
	if ( trueTimeSet )
	{
		//====================================================================================================
		//DRAW THE TIME
		//====================================================================================================
		 ctx.fillStyle = TIME_COLOR;
		 if ( THEME == "spaceTheme")
		 {
		 	ctx.font= "" + minCircleSize * .5 + "px " + TIME_FONT;	
		 	ctx.fillText(hoursAndMinsTime ,centerX - minCircleSize, centerY  );	  // HOURS	 
		 }else{
		 	ctx.font= "" + minCircleSize * .66 + "px " + TIME_FONT;	
		 	ctx.fillText(hoursAndMinsTime ,centerX - minCircleSize*.8, centerY  );	  // HOURS	 
		 }		 		 
		
		ctx.font= "" + minCircleSize *.33 + "px " + TIME_FONT;  // SECONDS + AM/PM
		ctx.fillText(secondsTime ,centerX - minCircleSize * .5 , centerY + minCircleSize*.5);							
		//ctx.strokeText(secondsTime ,centerX - minCircleSize * .5 , centerY + minCircleSize*.5);							
		ctx.fillText(meridian ,centerX + minCircleSize * .25 , centerY + minCircleSize*.5);									
		//ctx.strokeText(meridian ,centerX + minCircleSize * .25 , centerY + minCircleSize*.5);	

		//====================================================================================================								
	}
	
	// DRAW THE BORDER
//	roundRect(ctx, 0 , 0 , circleWidgetMaxRadius * 2 , circleWidgetMaxRadius * 2, circleWidgetMaxRadius * 0.33, false, true,3);

	//increment

	for ( i = 0 ; i < circlesArray.length; i++ ) {

		var circle = circlesArray[i];
		circle.startArc += Math.PI / circle.speed;
		circle.endArc += Math.PI / circle.speed;								
	}


//	boogieLog("circle widget enter frame " , 3 );
	
	

	setTimeout ( widgetEnterFrame , 40);
	
}

function setTrueTime( timeString ) {

	boogieLog("setTrueTime() timeString = " + timeString , 999);

	if ( timeString && timeString.length > 3 )
	{
		trueTimeSet = true;
		trueTime = timeString;
		var timeArray = timeString.split(":");
		var hoursOnlyNumber = Number(timeArray[0]) ;
		if ( hoursOnlyNumber < 12 )
		{
			meridian = "AM";
		}else if ( hoursOnlyNumber == 12 )
		{
			meridian = "PM";
		}else if ( hoursOnlyNumber > 12 )
		{
			meridian = "PM";
			hoursOnlyNumber -= 12;
		}
		hoursAndMinsTime = hoursOnlyNumber + ":" + timeArray[1];
		secondsTime = timeArray[2].split(" ")[0];
		//meridian = timeArray[2].split(" ")[1];
		if ( hoursAndMinsTime ) setTimeout( incrementTime , 1000);
	}else{
		return;
	}

}

function updateTrueTime () {

	var currentdate = new Date(); 
          var hours = currentdate.getHours();
          var mins = currentdate.getMinutes();
          if ( mins < 10 ) mins = "0" + mins;
          var secs = currentdate.getSeconds();
          var trueTime = hours+":"+mins+ ":" + secs + " am";   

          trueTimeSet = true;		  
		
		var hoursOnlyNumber = hours ;
		if ( hoursOnlyNumber < 12 )
		{
			meridian = "AM";
		}else if ( hoursOnlyNumber == 12 )
		{
			meridian = "PM";
		}else if ( hoursOnlyNumber > 12 )
		{
			meridian = "PM";
			hoursOnlyNumber -= 12;
		}
		hoursAndMinsTime = hoursOnlyNumber + ":" + mins;
		secondsTime = secs;
}
function incrementTime () {
		
//	boogieLog("incrementTime()", 3);
	if ( secondsTime < 59 )
	{
		secondsTime++;
	}else{
		secondsTime = 0;
		var hoursAndMinsArray = hoursAndMinsTime.split(":");
		var minsOnly = hoursAndMinsArray[1];
		if ( minsOnly < 59 )
		{
			minsOnly++;
			var leadingZero = "";
			if ( minsOnly < 10 )
			{
				leadingZero = "0";
			}
			hoursAndMinsTime = hoursAndMinsArray[0] + ":" + leadingZero +  minsOnly;
		}else{
			var hoursOnly = hoursAndMinsArray[0];
			if ( hoursOnly < 12 )
			{
				hoursOnly++;
				if ( hoursOnly == 12 )
				{
					if ( meridian == "AM")
					{
						meridian = "PM";
					}else{
						meridian = "PM";
					}
				}
				if ( hoursOnly > 12 )
				{
					hoursOnly -= 12;
				}
				hoursAndMinsTime = hoursOnly + ":" + "00";
			}
		}
	}


	setTimeout( incrementTime , 1000);

}



