
var newsLoopIndex = 0;
var newsFontSize	 = 16;
var newsBorderLineWidth = 2;
var newsArray;

function startNews() {	

	setNewsWidgetCanvasHeightAndWidth();

	getNews();	
}

function getNews () {

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
	  		boogieLog("whole news response : " + response, 999);	  		
	  		
	  		var responseArray = response.split("<wagwan>");
	  		newsArray = responseArray;
	  		//drawNewsResults(responseArray);	  		
	  		drawNewsResults();
	  		
	  	}
	  }	  	  
	  xmlhttp.open("GET","http://jakeflemmer.com/beta/src/news.php",true);
	  xmlhttp.send();
}

function drawNewsResults(){

	if ( displayMode() != "desktop") return;

	var c=document.getElementById("newsWidgetCanvas");
	var ctx=c.getContext("2d");
	
	ctx.clearRect ( 0 , 0 , screenWidth , screenHeight );

		ctx.font= newsFontSize + "px spaceFont";
		
		ctx.strokeStyle= "rgba(0,200,0,1)";
		ctx.lineWidth = 1;

		
			var news = newsArray[newsLoopIndex];
			if ( news == undefined || news == "" || news.length < 13 )
			{
				newsLoopIndex = 0;	
				news = newsArray[newsLoopIndex];			
			}

			if ( news != null && news.length > 5 ){
				ctx.fillStyle= "rgba(0,200,0,1)";
				ctx.fillText(newsLoopIndex	+1 + " - " + news.split('<udunno>')[0],10,screenHeight - newsFontSize * 2 );			
			}

			var wid = circleWidgetMaxRadius * 3 - 10;
			var hei = screenHeight - weatherWidgetFinalBottom - newsFontSize * 4;
			//======================================
			// DRAW BORDER 
			roundRectNoRightSide( ctx , 10 , weatherWidgetFinalBottom , wid  ,hei , newsFontSize, false, true, newsBorderLineWidth);	
			//============
			
			var img = new Image;
			img.onload = function(){

				//============= DRAW IMAGE ================
				ctx.drawImage(img,20,weatherWidgetFinalBottom + newsFontSize, circleWidgetMaxRadius * 3 - 40, hei - newsFontSize * 2); 
				
				// ANIMATE the IMAGE
				//img.hide(1000);
				// GREEN CANVAS COVER
				ctx.fillStyle= "rgba(0,200,0,.25)";
				roundRect( ctx , 10 , weatherWidgetFinalBottom , wid  ,hei , newsFontSize, true, false, 4);	
				//======================================

				
			};
			img.src = "http://" + news.split('udunno')[1].substring(3);		

			if ( newsLoopIndex < newsArray.length - 1 )
					{
						newsLoopIndex++;
					}else{
						newsLoopIndex = 0;
					}	

			setTimeout(drawNewsResults, 4000 );						
}


