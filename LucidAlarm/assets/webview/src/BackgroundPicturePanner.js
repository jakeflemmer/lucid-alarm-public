// this class must have a background picture
// it must pan its picture left and right to its edges
function BackgroundPicturePanner( picName, picWidth, screenWidth, panSpeed , frameRate)
{
	this.picName = picName;
	this.picWidth = picWidth;
	this.screenWidth = screenWidth;
	this.panSpeed = panSpeed;
	this.backgroundPanPosition = 0;
	this.frameRate = frameRate;
	this.panDirection = -1;
}

BackgroundPicturePanner.prototype.startPanning = function () {	
	
	if (this.backgroundPanPosition < ( this.picWidth - screenWidth) * -1 )
	{ 
		this.panDirection *= -1;
	}
	if (this.backgroundPanPosition > 0  )
	{
		this.panDirection *= -1;
	}
	this.backgroundPanPosition += this.panSpeed * this.panDirection;

	 $('#' + this.picName ).css( {"left": this.backgroundPanPosition +"px"} ); 
	//alert("panning");

	 var that = this;

	 setTimeout(function(){ that.startPanning(); }, this.frameRate);
}