// SpinningCirclesWidget
// We are aiming for an agnostic circle that animates at fixed rate

function SpinningCirclesWidget(canvasName, minimumCircleSize, centerX, centerY, numberOfCircles , maxStrokeWidth, maxAlpha, frameRate)
{
	this.canvasName = canvasName;

	var c=document.getElementById(this.canvasName );
	var ctx=c.getContext("2d");	
	this.canvasContext = ctx;
	makeCanvasContextFullScreen(ctx);

	this.minCircleSize = minimumCircleSize;// screenWidth * .1;

	this.centerX = centerX;
	this.centerY = centerY;

	this.numberOfCircles = numberOfCircles;

	this.minStokeAlpha = .2;	// TODO	

	this.maxStrokeWidth = maxStrokeWidth;	//this.minCircleSize * .25;


	this.circlesArray = new Array();

	this.frameRate = frameRate;

	// define the corcle objects
	for ( i = 0 ; i < this.numberOfCircles; i++ ) {
		var newCircle = new Object();
		if ( i == 0 )
		{
			newCircle.radius = this.minCircleSize;		
		}
		this.circlesArray.push(newCircle);		
	}

	var lastCircle;
	var circle;
	
	for ( i = 0 ; i < this.circlesArray.length; i++ ) {
		circle = this.circlesArray[i];
		circle.startArc = Math.PI*Math.random();
		circle.endArc = circle.startArc + Math.PI + ( Math.PI  *  Math.random() );
		circle.strokeWidth = 2+( this.maxStrokeWidth * Math.random() );

		var alpha = Math.random();
		if ( alpha < 0.1 ) alpha = 0.1;
		if ( alpha > maxAlpha ) alpha = maxAlpha;

		circle.strokeColor = CIRCLE_COLOR + alpha + ")";
		if ( i > 0 )
		{
			circle.radius = lastCircle.radius + ( lastCircle.strokeWidth  ) + 5;
		}
		
		circle.speed = i*400;
		if ( Math.random() < 0.5 ) circle.speed *= -1;
		lastCircle = circle;
		circleWidgetMaxRadius = circle.radius + circle.strokeWidth;
	}	
	// this.centerX = circleWidgetMaxRadius;
	// this.centerY = circleWidgetMaxRadius;

	//---------------------------------

	this.maxRadius = circleWidgetMaxRadius;

	this.circleWidgetBottom =  circleWidgetMaxRadius * 2;

}


SpinningCirclesWidget.prototype.startSpinning = 
	function () {
	
	var ctx = this.canvasContext;

	ctx.clearRect ( 0 , 0 , screenWidth , screenHeight );

	// DRAW THE CIRCLES
	//=============================
	for ( i = 0 ; i < this.circlesArray.length; i++ ) {
		var circle = this.circlesArray[i];
		
		ctx.beginPath();
        ctx.arc(this.centerX, this.centerY, circle.radius, circle.startArc, circle.endArc, false);                
        ctx.lineWidth = circle.strokeWidth;
        ctx.strokeStyle = circle.strokeColor;
        ctx.stroke();
	} 
			
	ctx.fillStyle= "green";
	ctx.strokeStyle= "green";
	ctx.lineWidth = 1;

	for ( i = 0 ; i < this.circlesArray.length; i++ ) {

		var circle = this.circlesArray[i];
		circle.startArc += Math.PI / circle.speed;
		circle.endArc += Math.PI / circle.speed;								
	}

	var that = this;

	setTimeout ( function() { that.startSpinning(); }, this.frameRate);
	
}

