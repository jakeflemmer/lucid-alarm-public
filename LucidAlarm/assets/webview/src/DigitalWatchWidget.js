


function DigitalWatchWidget (canvasName, watchFaceRadius, centerX, centerY)
{

	this.canvasName = canvasName;

	this.watchFaceRadius = watchFaceRadius;

	this.watchCenterX = centerX;
	this.watchCenterY = centerY;

	var c=document.getElementById(this.canvasName );
	var ctx=c.getContext("2d");	
	this.canvasContext = ctx;
	makeCanvasContextFullScreen(ctx);
			
}

DigitalWatchWidget.prototype.setCurrentTime = function () {

	var currentdate = new Date(); 
    var hours = currentdate.getHours();
    var mins = currentdate.getMinutes();
    if ( mins < 10 ) mins = "0" + mins;
    var secs = currentdate.getSeconds();
    var trueTime = hours+":"+mins+ ":" + secs + " am";   

          this.trueTimeSet = true;		  
		
		var hoursOnlyNumber = hours ;
		if ( hoursOnlyNumber < 12 )
		{
			this.meridian = "AM";
		}else if ( hoursOnlyNumber == 12 )
		{
			this.meridian = "PM";
		}else if ( hoursOnlyNumber > 12 )
		{
			this.meridian = "PM";
			hoursOnlyNumber -= 12;
		}
		this.hoursAndMinsTime = hoursOnlyNumber + ":" + mins;
		this.secondsTime = secs;
}


DigitalWatchWidget.prototype.clockTick = function () {
		
	if ( ! this.trueTimeSet )	return;


	if ( this.secondsTime < 59 )
	{
		this.secondsTime++;
	}else{
		this.secondsTime = 0;
		var hoursAndMinsArray = this.hoursAndMinsTime.split(":");
		var minsOnly = hoursAndMinsArray[1];
		if ( minsOnly < 59 )
		{
			minsOnly++;
			var leadingZero = "";
			if ( minsOnly < 10 )
			{
				leadingZero = "0";
			}
			this.hoursAndMinsTime = hoursAndMinsArray[0] + ":" + leadingZero +  minsOnly;
		}else{
			var hoursOnly = hoursAndMinsArray[0];
			if ( hoursOnly < 12 )
			{
				hoursOnly++;
				if ( hoursOnly == 12 )
				{
					if ( this.meridian == "AM")
					{
						this.meridian = "PM";
					}else{
						this.meridian = "PM";
					}
				}
				if ( hoursOnly > 12 )
				{
					hoursOnly -= 12;
				}
				this.hoursAndMinsTime = hoursOnly + ":" + "00";
			}
		}
	}



	var ctx = this.canvasContext;

	ctx.clearRect ( 0 , 0 , screenWidth , screenHeight );

	ctx.lineWidth = 1;
	
		//====================================================================================================
		//DRAW THE TIME
		//====================================================================================================
		 ctx.fillStyle = TIME_COLOR;
		 if ( THEME == "spaceTheme")
		 {
		 	ctx.font= "" + this.watchFaceRadius * .5 + "px " + TIME_FONT;	
		 	ctx.fillText(this.hoursAndMinsTime ,this.watchCenterX - this.watchFaceRadius, this.watchCenterY  );	  // HOURS	 
		 }else{
		 	ctx.font= "" + this.watchFaceRadius * .66 + "px " + TIME_FONT;	
		 	ctx.fillText(this.hoursAndMinsTime ,this.watchCenterX - this.watchFaceRadius*.8, this.watchCenterY  );	  // HOURS	 
		 }		 		 
		
		ctx.font= "" + this.watchFaceRadius *.33 + "px " + TIME_FONT;  // SECONDS + AM/PM
		ctx.fillText(this.secondsTime ,this.watchCenterX - this.watchFaceRadius * .5 , this.watchCenterY + this.watchFaceRadius*.5);							
		ctx.fillText(this.meridian ,this.watchCenterX + this.watchFaceRadius * .25 , this.watchCenterY + this.watchFaceRadius*.5);									

		//====================================================================================================								

	var that = this;
	setTimeout( function() { that.clockTick();}, 1000);
}






/* TODO nice feature
function setTrueTime( timeString ) {

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
		if ( hoursAndMinsTime ) setTimeout( incrementTime , 1000);
	}else{
		return;
	}

}*/
