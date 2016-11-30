// WIDGET ORDER II

var dateXPos;




function startTimeAndDate() {	

	setTimeAndDateWidgetCanvasHeightAndWidth();
	
	//getTheTime();
  var currentdate = new Date(); 
          var hours = currentdate.getHours();
          var mins = currentdate.getMinutes();
          if ( mins < 10 ) mins = "0" + mins;
          var secs = currentdate.getSeconds();
          setTrueTime(hours+":"+mins+ ":" + secs + " am");   
	//drawFinanceResults();

    dateXPos = circleWidgetMaxRadius * 3;
	
	//drawBorder();

}

function getTheTime () {

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
        if (xmlhttp.readyState!=4) return;
        if (xmlhttp.status==200)
        {   

            var response = xmlhttp.response;
            boogieLog("whole time response : " + response, 1);            
            
            var responseArray = response.split(".");
           // drawDateResult(responseArray[0],responseArray[1]);  
            setTrueTime(responseArray[2]);         
            //drawTimeResults(response);            
        }else{
          // we are offline or failed to connect to time server for some reason
          var currentdate = new Date(); 
          var hours = currentdate.getHours();
          var mins = currentdate.getMinutes();
          if ( mins < 10 ) mins = "0" + mins;
          var secs = currentdate.getSeconds();
          setTrueTime(hours+":"+mins+ ":" + secs + " pm");         
        }
      }       
      xmlhttp.open("GET", ENVIRONMENT_URL + "src/time.php",true);
      xmlhttp.send();
}

function drawDateResult(day,date){

    var c=document.getElementById("timeAndDateWidgetCanvas");
    var ctx=c.getContext("2d");
    
        ctx.font= dateFontSize  + "px spaceFont";
        ctx.fillStyle= TIME_COLOR;
        ctx.strokeStyle= "rgba(0,0,0,1)";
        ctx.lineWidth = 1;
        
        ctx.fillText(day + ", " + date ,dateXPos, dateFontSize ); 

        //draw a rule line
        // ctx.moveTo(dateXPos , dateFontSize + dateFontSize*.2);
        // ctx.lineTo(dateXPos + dateFontSize*10, dateFontSize + dateFontSize*.2);        
        // ctx.stroke();


        //ctx.fillText(" " + day,dateXPos, dateFontSize  * 2 + 2 );                       
                   
    
} 

// function drawBorder () {

//     var c=document.getElementById("timeAndDateWidgetCanvas");
//     var ctx=c.getContext("2d");

//     ctx.strokeStyle = "rgba(0,255,0,.5)";
//     roundRectNoLeftSide(ctx, dateXPos, 0, dateFontSize*10, dateFontSize*2+6, dateFontSize*1, false, true,3);
// }