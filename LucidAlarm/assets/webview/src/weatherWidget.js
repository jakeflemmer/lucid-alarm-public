var weatherWidgetFinalBottom;
var worldWeatherArray = new Array();
var weatherLoopIndex = 0;
var weatherResults = new Array();
var weatherFontSize;
var weatherWidgetWidth;
var localWeatherBottom;

function startWeather() {	

	setWeatherWidgetCanvasHeightAndWidth();

	// SET LOCAL SCALING VARIABLES
	//--------------------------------
	weatherWidgetWidth = circleWidgetMaxRadius * 2 ;
	weatherFontSize =  minCircleSize * .4;
	//--------------------------------

	getLocalWeather();

	populateWorldWeatherArray();

	getWorldWeather();

}

function getLocalWeather()
{
	var localWeather = new Object();
	localWeather.wZip = 91801527;
	localWeather.locationName = "Brooklyn";

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

	  		var response = xmlhttp.response.toString();
	  		boogieLog("whole weather response : " + response, WEATHER_WIDGET);	  		

	  			  		

	  		var nameString = "Brooklyn, NY";
	  		var imgSrc = response.substring(response.indexOf('<img src="')+10,response.indexOf("/><br />")-1);		
	  		var descriptionAndTemperature = response.substring(response.indexOf("<b>Current Conditions:</b><br />")+32,response.indexOf("F<BR />")-1);
	  		var fiveDayString = response.substring(
	  			response.indexOf("<BR /><b>Forecast:</b><BR />")+28,
	  			response.indexOf('<a href="http:')-7
	  			);
	  		var fiveDayForecastArray = fiveDayString.split("<br />");

	  		//var locationTimeString = res.substring(res.indexOf(localWeather.locationName),res.indexOf("<endOfTitle>"));
	  		
	  		//var locationTimeStringsArray = locationTimeString.split(" at ");//.join("   ");
	  		boogieLog("NAMESTRING : " + nameString, WEATHER_WIDGET);	
	  		boogieLog("imgSrc : " + imgSrc, WEATHER_WIDGET);	  
	  		boogieLog("descriptionAndTemperature : " + descriptionAndTemperature, WEATHER_WIDGET);	  
	  		boogieLog("fiveDayString : " + fiveDayString, WEATHER_WIDGET);	    

	  			  		
	  		drawLocalResults(nameString,imgSrc,descriptionAndTemperature,fiveDayForecastArray);
	  	}
	  }	  	  
	  xmlhttp.open("GET","http://jakeflemmer.com/beta/src/weather.php?url=http://weather.yahooapis.com/forecastrss?w=" + localWeather.wZip,true);
	  xmlhttp.send();
}

function drawLocalResults(nameString,imgSrc,descriptionAndTemperature,fiveDayForecastArray){

	var c=document.getElementById("weatherWidgetCanvas");
	var ctx=c.getContext("2d");
	
		ctx.font= (weatherFontSize  * 1.5 )+ "px spaceFont";
		ctx.fillStyle= "rgba(0,255,0,1)";
		ctx.strokeStyle= "rgba(0,200,0,1)";
		ctx.lineWidth = 1;
		var widgetTop = circleWidgetMaxRadius * 2;
		//================
		// BROOKLYN , NY
		//=================
		widgetTop +=  weatherFontSize * 2;
		ctx.fillText(nameString,10, widgetTop + 5);
		
		//DESCRIPTION AND TEMPERATURE
		ctx.font= (weatherFontSize  * 1 )+ "px spaceFont";	
		widgetTop +=  weatherFontSize + weatherFontSize * .2;	
		var tempOnly = descriptionAndTemperature.split(",")[1];
		
		//drawTempCircle( tempOnly );			

		//border
		var borderWidth = circleWidgetMaxRadius * 3;
		roundRectNoLeftSide(ctx, 20, circleWidgetMaxRadius * 2, borderWidth - 20, widgetTop - (circleWidgetMaxRadius * 2) + (weatherFontSize*.5), weatherFontSize, false, true,1);

		var h = (weatherFontSize* 2.5);
		widgetTop +=  weatherFontSize + weatherFontSize * .5;		

		var wid = circleWidgetMaxRadius * 3;
		
		// LOCAL WEATHER BACKGROUND FILL
		//============================
		ctx.fillStyle = "rgba(0,0,0,.5)";
		roundRect(ctx, 5, widgetTop - weatherFontSize , wid, h*6, weatherFontSize, true, false,1);		
		
		// LOCAL WEATHER BORDER
		//===========================
		ctx.fillStyle= "rgba(0,255,0,1)";
		roundRectNoRightSide(ctx, 5, widgetTop - weatherFontSize , wid, h*6, weatherFontSize, false, true,1);		
		
		var rowCounter = 0;

		
		for ( i = 0 ; i < fiveDayForecastArray.length ; i++ ) {
			var day = fiveDayForecastArray[i];
			if ( day == "" || day.length < 5 ) break;
			ctx.fillText(day.split(".")[0],10, widgetTop + ( rowCounter * weatherFontSize ));		
			ctx.fillText("   " + day.split(".")[1],10, widgetTop + ( (rowCounter+1) * weatherFontSize ));				
			
			
			rowCounter += 3;
		}	

		

		// DRAW THE ICON IMAGE
		// var img = new Image;
		// img.onload = function(){	
		// 	ctx.drawImage(img,weatherWidgetWidth *.66,circleWidgetMaxRadius * 2 , weatherWidgetWidth * .25 ,  weatherFontSize * 3.5); 						
		// 	weatherGreenScreenCover();
		// };
		drawWeatherIcon(imgSrc);
		//img.src = imgSrc;		

		//IMPORTANT GLOBAL VARIABLE
		localWeatherBottom = widgetTop - weatherFontSize + h*6;			
	
}



function populateWorldWeatherArray () {

	var rome = new Object();
	rome.wZip = 721943;
	rome.locationName = "Rome";

	var london = new Object();
	london.wZip = 44418;
	london.locationName = "London";

	
	var Tokyo = new Object();
	Tokyo.wZip = 1118370;
	Tokyo.locationName = "Tokyo";

	var Seoul = new Object();
	Seoul.wZip = 1132599;
	Seoul.locationName = "Seoul";

	var Bangkok = new Object();
	Bangkok.wZip = 1225448;
	Bangkok.locationName = "Bangkok";

	var Delhi = new Object();
	Delhi.wZip = 2295019;
	Delhi.locationName = "Delhi";

	var Cairo = new Object();
	Cairo.wZip = 1521894;
	Cairo.locationName = "Cairo";

	var Jerusalem = new Object();
	Jerusalem.wZip = 1968222;
	Jerusalem.locationName = "Jerusalem";

	var Durban = new Object();
	Durban.wZip = 1580913;
	Durban.locationName = "Durban";

	var Athens = new Object();
	Athens.wZip = 2356940;
	Athens.locationName = "Athens";

	var Moscow = new Object();
	Moscow.wZip = 2122265;
	Moscow.locationName = "Moscow";

	var LosAngeles = new Object();
	LosAngeles.wZip = 2442047;
	LosAngeles.locationName = "Los Angeles";

	var Madrid = new Object();
	Madrid.wZip = 766273;
	Madrid.locationName = "Madrid";

	var Paris  = new Object();
	Paris.wZip = 615702;
	Paris.locationName = "Paris";

	var Oslo  = new Object();
	Oslo.wZip = 862592;
	Oslo.locationName = "Oslo";

	var Sydney  = new Object();
	Sydney.wZip = 1105779;
	Sydney.locationName = "Sydney";

	var Istanbul  = new Object();
	Istanbul.wZip = 2344116;
	Istanbul.locationName = "Istanbul";

	
	
	worldWeatherArray.push(rome);	
	worldWeatherArray.push(Seoul);
	worldWeatherArray.push(Bangkok);
	worldWeatherArray.push(Cairo);
	worldWeatherArray.push(Jerusalem);
	worldWeatherArray.push(Durban);
	worldWeatherArray.push(Athens);
	worldWeatherArray.push(Moscow);
	worldWeatherArray.push(LosAngeles);		
	worldWeatherArray.push(Madrid);			
	worldWeatherArray.push(Paris);			
	worldWeatherArray.push(Oslo);			
	worldWeatherArray.push(Sydney);			
	worldWeatherArray.push(Istanbul);				

}



function getWorldWeather()
{
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
	  		var locationObject = worldWeatherArray[weatherLoopIndex];
//	  		boogieLog("startWeather() weather result : " + xmlhttp.response, 999);
	  		var res = xmlhttp.response.toString();
	  		boogieLog("whole weather response : " + res, WEATHER_WIDGET);	  		
	  		var locationTimeString = res.substring(res.indexOf(locationObject.locationName),res.indexOf("<endOfTitle>"));
	  		
			locationTimeString += "   " + res.substring(res.indexOf("Current Conditions:</b><br />")+30,res.indexOf("<BR />"));

	  		var locationTimeStringsArray = locationTimeString.split(" at ");//.join("   ");
	  		boogieLog("clean string : " + locationTimeString, WEATHER_WIDGET);	  

	  		var imgSrc = res.substring(res.indexOf('<img src="')+10,res.indexOf("/><br />")-1);		
	  		locationTimeStringsArray.push(imgSrc);
	  		weatherLoopIndex++;
	  		weatherResults.push(locationTimeStringsArray);
	  		if ( weatherLoopIndex < worldWeatherArray.length ) 
	  			{
	  				getWorldWeather();
	  			}else{
	  				drawWorldResults();
	  			}
	  	}
	  }	  
	  var locationObject = worldWeatherArray[weatherLoopIndex];	  	  
	  xmlhttp.open("GET",ENVIRONMENT_URL + "src/weather.php?url=http://weather.yahooapis.com/forecastrss?w=" + locationObject.wZip,true);


	  xmlhttp.send();
}

function drawWorldResults(){

	if ( displayMode() != "desktop") return;
	var c=document.getElementById("weatherWidgetCanvas");
	var ctx=c.getContext("2d");

	localWeatherBottom += weatherFontSize * 1.5;
	
	sortWeatherResultsByTemperature();

	for ( i = 0 ; i < weatherResults.length; i++ ) {
		var resArray = weatherResults[i];
		ctx.font= weatherFontSize + "px spaceFont";
		ctx.fillStyle= "rgba(0,200,0,1)";
		ctx.strokeStyle= "rgba(0,200,0,1)";
		ctx.lineWidth = 1;
		ctx.fillText(resArray[0],10, localWeatherBottom+i*weatherFontSize  );	
		var justTempArray = (resArray[1].split(","));
		var widgetTop = localWeatherBottom+i*weatherFontSize;
		ctx.fillText(justTempArray[1],circleWidgetMaxRadius * 3 *.75, widgetTop  );	
	}

	//===================================
	//DRAW WORLD BORDER
	//===================================
	localWeatherBottom -= weatherFontSize * 1.5;
	var wid = circleWidgetMaxRadius * 3;
	var h = (weatherFontSize * 2.5);
	var bottomMargin = weatherFontSize*.5;
	roundRectNoLeftSide(ctx, 20, localWeatherBottom , wid - 20 , h*6 + bottomMargin, weatherFontSize, false, true,1);		
	weatherWidgetFinalBottom = localWeatherBottom +  h*6 + bottomMargin;

	//==================================
	// START THE NEWS
	//==================================
	setTimeout( startNews , 1000);
}

function sortWeatherResultsByTemperature () {

	weatherResults.sort(function( a , b ){

		var justTempA = a[1].split(",")[1].replace("F","");
		var justTempB = b[1].split(",")[1].replace("F","");
		justTempA.replace(" ", "");
		justTempB.replace(" ", "");
		return justTempA - justTempB;
	});	

}
