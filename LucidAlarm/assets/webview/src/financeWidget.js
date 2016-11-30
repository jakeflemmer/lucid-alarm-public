var financeFontSize	 = 14;
var widgetWidth = 200;
var financeArray = new Array();
var financeLoopIndex = 0;


function startFinance() {	

	setFinanceWidgetCanvasHeightAndWidth();
	
	greenScreenCover(); 

	populateFinanceArray();

	drawFinanceResults();

}

function populateFinanceArray () {

	var sp500 = new Object();
	sp500.symbol = "^GSPC";//^INX";
	sp500.name = "S&P 500";

	var dowJones = new Object();
	dowJones.symbol = "^DJI";
	dowJones.name = "Dow Jones";

	var nasdaq = new Object();
	nasdaq.symbol = "^IXIC";
	nasdaq.name = "NASDAQ";

	var google = new Object();
	google.symbol = "GOOG";	
	google.name = "GOOG";

	var apple = new Object();
	apple.symbol = "AAPL";	
	apple.name = "AAPL";

	var yahoo = new Object();
	yahoo.symbol = "YHOO";	
	yahoo.name = "YHOO";

	var bac = new Object();
	bac.symbol = "BAC";	
	bac.name = "BAC";

	var fb = new Object();
	fb.symbol = "FB";		
	fb.name = "FB";

	var siri = new Object();
	siri.symbol = "SIRI";		
	siri.name = "SIRI";

	var ford = new Object();
	ford.symbol = "F";		
	ford.name = "F";

	financeArray.push(sp500);
	financeArray.push(dowJones);
	financeArray.push(nasdaq);
	financeArray.push(google);
	financeArray.push(apple);
	financeArray.push(yahoo);
	financeArray.push(bac);
	financeArray.push(fb);
	financeArray.push(siri);
	financeArray.push(ford);	

}





function drawFinanceResults(){

	var c=document.getElementById("financeWidgetCanvas");
	var ctx=c.getContext("2d");

		var img = new Image;
		img.onload = function(){
				ctx.globalAlpha = 0.66;
				//draw the image
				ctx.drawImage(img,screenWidth - widgetWidth, financeLoopIndex * ( widgetWidth * 0.5 ) + (widgetWidth * 0.125 ) ,widgetWidth,widgetWidth*.375);
				
				//green border
				ctx.strokeStyle= "rgba(0,255,0,1)";
				ctx.strokeRect(screenWidth - widgetWidth, financeLoopIndex * ( widgetWidth * 0.5 ) + (widgetWidth * 0.125 ) ,widgetWidth,widgetWidth*.375);

				//text
				ctx.font= financeFontSize + "px spaceFont";
				ctx.strokeStyle= "rgba(0,255,0,1)";		
				ctx.fillStyle= "rgba(0,255,0,1)";		
				var yPos =  financeLoopIndex * ( widgetWidth * 0.5) + financeFontSize + ( widgetWidth * .025);
				ctx.fillText(financeArray[financeLoopIndex].name,screenWidth - widgetWidth,yPos);	

				financeLoopIndex++;
				if ( financeLoopIndex < financeArray.length )
				{
					drawFinanceResults();
				}else{
					financeLoopIndex = 0;
					getCurrentPrices();
				}						
		};
		img.src = "http://chart.finance.yahoo.com/z?s=" + financeArray[financeLoopIndex].symbol + "&t=1d&q=l&l=on&z=s";					
	
}

function greenScreenCover () {

	var c=document.getElementById("financeWidgetCanvas");
	var ctx=c.getContext("2d");
	
	ctx.fillStyle= "rgba(0,200,0,0.1)";	
	ctx.fillRect(screenWidth - widgetWidth,0,screenWidth,screenHeight);
}

function getCurrentPrices () {

//http://finance.google.com/finance/info?client=ig&q=NASDAQ:STEM
	var c=document.getElementById("financeWidgetCanvas");
	var ctx=c.getContext("2d");

	var xmlhttp;
      if (window.XMLHttpRequest)
      {// code for IE7+, Firefox, Chrome, Opera, Safari
        xmlhttp=new XMLHttpRequest();
      }
      else
      {// code for IE6, IE5
        xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
      }
      xmlhttp.onreadystatechange=function()
      {     
        if (xmlhttp.readyState==4 && xmlhttp.status==200)
        {   

            var response = xmlhttp.response;
            boogieLog("whole current price response : " + response, FINANCE_WIDGET);            
            

            var lastPrice = response.substring(response.indexOf('"l" :')+7,response.indexOf(',"l_fix"') -2);                  

            ctx.font= financeFontSize + "px spaceFont";				
				ctx.fillStyle= "rgba(0,255,0,1)";		
				var yPos =  financeLoopIndex * ( widgetWidth * 0.5) + financeFontSize + ( widgetWidth * .025);
				ctx.fillText( lastPrice ,screenWidth - widgetWidth*.5,yPos);	          

            financeLoopIndex++;
				if ( financeLoopIndex < financeArray.length )
				{
					getCurrentPrices();
				}else{
					// we are finished here
				}
        }
      }           
      var symbol = financeArray[financeLoopIndex].symbol;
      symbol = symbol.replace("^",".");
      if ( financeLoopIndex == 0 ) symbol = ".INX";
      xmlhttp.open("GET", "http://finance.google.com/finance/info?client=ig&q=NASDAQ:" + symbol ,true);
      xmlhttp.send();
}




