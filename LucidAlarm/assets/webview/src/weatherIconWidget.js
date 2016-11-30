var weatherIconWidth;
var tempOnlyFontSize = 14;
var barCodeWidth = 50;
var numberOfBarCodeLines = 300;

function startWeatherIcon() {	

	setWeatherIconWidgetCanvasHeightAndWidth();
		
	weatherIconWidth = screenWidth * 0.05;

	drawWeatherIconBorder();

	drawBarCode();

}

function drawWeatherIcon (imgSrc ) {

	// var c=document.getElementById("weatherIconWidgetCanvas");
 //    var ctx=c.getContext("2d");

 //    var leftSide = circleWidgetMaxRadius * 2;
 //    var top = 0;//circleWidgetMaxRadius;

	// var img = new Image;
	// 	img.onload = function(){	
	// 		ctx.drawImage(img,leftSide,top,circleWidgetMaxRadius,  circleWidgetMaxRadius); 						
	// 		//weatherGreenScreenCover();
	// 		drawWeatherIconBorder();
	// 	};
	// 	img.src = imgSrc;
}


// function drawWeatherIconBorder () {

//     var c=document.getElementById("weatherIconWidgetCanvas");
//     var ctx=c.getContext("2d");

//     var leftSide = circleWidgetMaxRadius * 2;
//     var centerX = circleWidgetMaxRadius * 2.5;
//     var top = circleWidgetMaxRadius;
//     var height = leftSide-top;
//     var radius = circleWidgetMaxRadius * .125;
//     ctx.lineWidth = 10;
//     ctx.strokeStyle= "rgba(0,200,0,1)";
//     ctx.fillStyle= "rgba(0,200,0,.2)";
//     ctx.arc( centerX, circleWidgetMaxRadius * .5,circleWidgetMaxRadius * .5, 0, Math.PI * 2);
//     ctx.fill();
//     //ctx.stroke();
//     ctx.closePath();
//      //roundRect(ctx, leftSide, top, weatherIconWidth, circleWidgetMaxRadius, radius, true, true,3) ;
// //	ctx.strokeRect(leftSide, top ,weatherIconWidth,height);  


	
// }

// function drawTempCircle ( temp ) {

//     var c=document.getElementById("weatherIconWidgetCanvas");
//     var ctx=c.getContext("2d");

//     var leftSide = circleWidgetMaxRadius * 2;
//     var centerX = circleWidgetMaxRadius * 2.5;
//     var top = circleWidgetMaxRadius;
//     var height = leftSide-top;
//     var radius = circleWidgetMaxRadius * .125;

//     ctx.font= tempOnlyFontSize + "px Arial";
//     ctx.strokeStyle= "rgba(0,200,0,1)";
//     ctx.fillStyle= "rgba(0,255,0,1)";
//     ctx.lineWidth = 2;
//     ctx.arc( centerX, circleWidgetMaxRadius * 1.5,circleWidgetMaxRadius * .5, 0, Math.PI * 2);    
//     ctx.fillText( temp + "F", centerX - circleWidgetMaxRadius * .25, circleWidgetMaxRadius * 1.5 + tempOnlyFontSize * .5);
//     ctx.stroke();
//      //roundRect(ctx, leftSide, top, weatherIconWidth, circleWidgetMaxRadius, radius, true, true,3) ;
// //	ctx.strokeRect(leftSide, top ,weatherIconWidth,height);  


	
// }

// function weatherGreenScreenCover () {

// 	var c=document.getElementById("weatherIconWidgetCanvas");
// 	var ctx=c.getContext("2d");
	
// 	var leftSide = circleWidgetMaxRadius * 2;
// 	var top = circleWidgetMaxRadius;

// 	ctx.fillStyle= "rgba(0,200,0,0.33)";	
// 	ctx.fillRect(leftSide, top ,weatherIconWidth,circleWidgetMaxRadius);  
// 	//ctx.arc( weatherWidgetWidth *.82, circleWidgetMaxRadius * 2 + weatherWidgetWidth *.16, weatherWidgetWidth *.166, 0, Math.PI * 2);
// 	ctx.fill();
// }