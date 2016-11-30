var barCodeWidth;// = 50;
var numberOfBarCodeLines = 300;
var barCodeColor = "rgba(0,125,0,";

function startBarCode() {	

	setBarCodeWidgetCanvasHeightAndWidth();
		
	barCodeWidth = circleWidgetMaxRadius * 1;

	drawBarCodeBorder();

	drawBarCode();

}

function drawBarCode () {



}


function drawBarCodeBorder () {

    var c=document.getElementById("barCodeWidgetCanvas");
    var ctx=c.getContext("2d");

    var leftSide = circleWidgetMaxRadius * 2;
    var top = 0;
    var height = circleWidgetMaxRadius * 2;
    ctx.strokeStyle= "rgba(0,200,0,1)";
	//ctx.strokeRect(leftSide, top ,barCodeWidth,height);  
	 roundRect(ctx, leftSide, top, barCodeWidth, height, circleWidgetMaxRadius*.1, false, true,1);


	for ( i = 0 ; i < height - 10 ; i++ ) {

		var blackOrWhite;
		if ( Math.random() > .5 )
		{
			blackOrWhite = 1;
		}else{
			blackOrWhite = 0;	
		}		

		ctx.strokeStyle = barCodeColor + blackOrWhite + ")";
		// var  r = (Math.random() > 0.5 ? 255 : 0 );
		// var  g = (Math.random() > 0.5 ? 255 : 0 );
		// var  b =(Math.random() > 0.5 ? 255 : 0 );
		// if ( r > 0 ) { g = 0 ; b = 0 };
		// var primaryColor = Math.random() * 4;
		// if ( primaryColor < 1 )
		// {
		// 	r = 255; g = 0; b = 0;
		// }else if ( primaryColor < 2)
		// {
		// 	r=0;g=255;b=0;
		// }else  if ( primaryColor < 3)
		// {
		// 	r=0;g=0;b=255;
		// }else{
		// 	r=255;g=255;b=0;
		// }		
		// var col = "rgba(" + Math.floor(r) + "," + Math.floor(g) + "," + Math.floor(b) + ","  + blackOrWhite + ")";	
		// ctx.strokeStyle = col;
		ctx.lineWidth = 1;
		ctx.beginPath();
		ctx.moveTo( leftSide+5, top + 5 + i);
		ctx.lineTo( leftSide + barCodeWidth - barCodeWidth * .20 , top + i + 5 );
		ctx.stroke();		
	}  

	ctx.save();
	ctx.translate(0, circleWidgetMaxRadius * 4);
	ctx.rotate(-Math.PI/2);	
	ctx.font= 11 + "px spaceFont";
		ctx.fillStyle = "rgba(0,0,255,1)";
	ctx.fillText("J C F 0 2 0 1 7 ", leftSide  + barCodeWidth , 100);	
	ctx.restore();




}