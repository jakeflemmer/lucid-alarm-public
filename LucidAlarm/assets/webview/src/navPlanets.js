// this file is here to handle everything regarding the navigation planets
var navPlanetLabelMargin = 0;

function calculateTheGradientsOfTheNavPlanets()
{

	//var screenHeight = $(window).height();
	navPlanetRadius = screenHeight / 15;

	var gradientXOffset = 0;	//-1 * (screenWidth / 200);
	var gradientYOffset = 0;	//-1 * (screenHeight / 200);

	defineNavPlanetGradient(homePlanet,gradientXOffset,gradientYOffset,navPlanetRadius,1,66,1);
	defineNavPlanetGradient(workPlanet,gradientXOffset,gradientYOffset,navPlanetRadius,66,1,1);
	defineNavPlanetGradient(mobilePlanet,gradientXOffset,gradientYOffset,navPlanetRadius,1,1,66);
	defineNavPlanetGradient(contactPlanet,gradientXOffset,gradientYOffset,navPlanetRadius,66,66,1);


	//...logs...
	boogieLog("calculateTheGradientsOfTheNavPlanets() PERFORMANCE " , PERFORMANCE );
}

function nameNavPlanets()
{
	homePlanet.name = "Home";
	workPlanet.name = "Work";
	mobilePlanet.name = "Mobile";
	contactPlanet.name = "Contact";

	//...logs...
	boogieLog("nameNavPlanets() PERFORMANCE " , PERFORMANCE );
}
function defineNavPlanetsColors()
{
	homePlanet.color = "green";
	workPlanet.color = "red";
	mobilePlanet.color = "blue";
	contactPlanet.color = "yellow";

	//...logs...
	boogieLog("defineNavPlanetsColors() PERFORMANCE " , PERFORMANCE );
}
function defineNavPlanetGradient(navPlanet,gradientXOffset,gradientYOffset,navPlanetRadius,red, green , blue)
{

	var c=document.getElementById("navCanvas");
	var ctx=c.getContext("2d");

	var first = .1;
	var second = 1.5;
	var third = 3;
	var fourth = 3;

	var innerCirleRadius = 7 * navPlanetRadius/12;

	var gradient =ctx.createRadialGradient(navPlanet.xPos,navPlanet.yPos, innerCirleRadius, navPlanet.xPos + gradientXOffset,navPlanet.yPos +gradientYOffset ,navPlanetRadius);
	gradient.addColorStop(0,'rgba(' + parseInt(red * first) + ',' + parseInt(green * first) + ',' + parseInt(blue *first) + ',1)');
	gradient.addColorStop(.8,'rgba(' + parseInt(red * second) + ',' + parseInt(green * second) + ',' + parseInt(blue *second) + ',1)');
	gradient.addColorStop(.9,'rgba(' + parseInt(red * third) + ',' + parseInt(green * third) + ',' + parseInt(blue *third) + ',1)');
	gradient.addColorStop(1,'rgba(' + parseInt(red * fourth) + ',' + parseInt(green * fourth) + ',' + parseInt(blue *fourth) + ',0)');
	navPlanet.gradient = gradient;
	
	var altGradient =ctx.createRadialGradient(navPlanet.altXPos, pageTop+pageHeight/2, pageHeight/6, navPlanet.altXPos,pageTop+pageHeight/2  ,pageHeight/3);
	altGradient.addColorStop(0,'rgba(' + parseInt(red * first) + ',' + parseInt(green * first) + ',' + parseInt(blue *first) + ',1)');
	altGradient.addColorStop(.8,'rgba(' + parseInt(red * second) + ',' + parseInt(green * second) + ',' + parseInt(blue *second) + ',1)');
	altGradient.addColorStop(.9,'rgba(' + parseInt(red * third) + ',' + parseInt(green * third) + ',' + parseInt(blue *third) + ',1)');
	altGradient.addColorStop(1,'rgba(' + parseInt(red * fourth) + ',' + parseInt(green * fourth) + ',' + parseInt(blue *fourth) + ',0)');
	navPlanet.altGradient = altGradient;


	//...logs...
	boogieLog("defineNavPlanetGradient() PERFORMANCE " , PERFORMANCE );
	boogieLog("defineNavPlanetGradient() navPlanetRadius : " + navPlanetRadius + " navcanvas height : " + $("#navCanvas").height(), NAV_PLANET_GRADIENT );

}



function calculateTheCoordinatesOfTheNavPlanets()
{
	var planetsYPos = screenHeight/8;
	var fifthOfWindow = screenWidth/5;

	homePlanet.xPos = fifthOfWindow;
	homePlanet.yPos = planetsYPos;
	homePlanet.xPan = homePlanetXPan;
	var widthRatio = pageWidth * .68;
	homePlanet.altXPos =  pageLeft+widthRatio/2;

	workPlanet.xPos = 2 * fifthOfWindow;
	workPlanet.yPos = planetsYPos;
	workPlanet.xPan = workPlanetXPan;
	 widthRatio = pageWidth * .4;
	 workPlanet.altXPos =  pageLeft+widthRatio/2;

	mobilePlanet.xPos = 3*fifthOfWindow;
	mobilePlanet.yPos = planetsYPos;
	mobilePlanet.xPan = mobilePlanetXPan;
	 widthRatio = pageWidth * .68;
	 mobilePlanet.altXPos =  pageLeft+widthRatio/2;
	
	contactPlanet.xPos = 4*fifthOfWindow;
	contactPlanet.yPos = planetsYPos;
	contactPlanet.xPan = contactPlanetXPan;
	 widthRatio = pageWidth * .68;
	 contactPlanet.altXPos =  pageLeft+widthRatio/2;

	//...logs...
	boogieLog("calculateTheCoordinatesOfTheNavPlanets() PERFORMANCE " , PERFORMANCE );
}

function drawAllTheNavPlanets()
{
	drawNavPlanet(homePlanet);

	drawNavPlanet(workPlanet);

	drawNavPlanet(mobilePlanet);

	drawNavPlanet(contactPlanet);

	//...logs...
	boogieLog("screenHeight : " + screenHeight + " navPlanetRadius * 2 : " + navPlanetRadius * 2, DRAW_NAV_PLANETS);
	boogieLog("drawAllTheNavPlanets() PERFORMANCE " , PERFORMANCE );

}

function drawNavPlanet(navPlanet)
{
	var c=document.getElementById("navCanvas");
	var ctx=c.getContext("2d");
	

	// DRAW THE CIRCLE
	ctx.fillStyle=navPlanet.gradient;
	ctx.arc( navPlanet.xPos, navPlanet.yPos, navPlanetRadius, 0, Math.PI * 2);
	ctx.fill();

	scaleAndPositionNavPlanetLabel(navPlanet);

	//...logs...
	boogieLog("drawNavPlanet() PERFORMANCE " , PERFORMANCE );
}

function scaleAndPositionNavPlanetLabel(navPlanet)
{
	var labelDiv = $("#" + navPlanet.name.toLowerCase() + "LabelDiv");
	labelDiv.css( { 'color' : navPlanet.color , 'opacity' : "1.0" } );
	
	increaseFontSizeToMatchPlanetDiameter(labelDiv);
	positionNavPLanetLabelOverPlanet(navPlanet, labelDiv);

	//...logs...
	boogieLog("scaleAndPositionNavPlanetLabel() PERFORMANCE " , PERFORMANCE );

}

function increaseFontSizeToMatchPlanetDiameter(labelDiv)
{
	var fontSize = 1;
	var planetDiameter = navPlanetRadius * 2;
	navPlanetLabelMargin = navPlanetRadius / 2;

	for ( var i = 0 ; i < 100 ; i++)
	{

		labelDiv["css"]( {'font-size' : fontSize + 'px'} );

		var newTextWidth = labelDiv["width"]();

		if ( newTextWidth < planetDiameter - navPlanetLabelMargin )
		{
			fontSize ++;
		}
		else{
			break;
		}
	}

	//...logs...
	boogieLog("increaseFontSizeToMatchPlanetDiameter() PERFORMANCE " , PERFORMANCE );
}

function positionNavPLanetLabelOverPlanet(navPlanet, labelDiv)
{
	var halfLabelHeight = labelDiv["height"]() / 1.6 ;
	labelDiv["css"]( { 'left' : navPlanet.xPos - navPlanetRadius + ( navPlanetLabelMargin / 2 ) + "px" , 'top' : navPlanet.yPos - halfLabelHeight + "px" } );
	boogieLog("positionNavPLanetLabelOverPlanet() halfLabelHeight : " + halfLabelHeight,NAV_PLANET_LABEL_HEIGHT);

	//...logs...
	boogieLog("positionNavPLanetLabelOverPlanet() PERFORMANCE " , PERFORMANCE );
}

function springToPlanet( navPlanet )
{
	if ( currentPlanet == navPlanet ) return;
	
	//playLostTvSignalEffect();


	currentPlanet = navPlanet;
	panDestination = navPlanet.xPan;
	workPageState = 0;
	mobilePageState = 0;
	//clearAllPagesText();
	clearArrows();
	if ( currentPlanet == contactPlanet )
	{
		disableOrEnableContactFormInputs(false);
	}else{
		disableOrEnableContactFormInputs(true);
	}

	clearAllPagesText();
		clearStageCanvas();
		drawPages( currentPlanet , 1 );	

	//.... log ........
	boogieLog("springToPlanet() navPlanet : " + navPlanet + " navPlanet.name : " + navPlanet.name, NAV_PLANET_SPRING);
	boogieLog("springToPlanet() PERFORMANCE " , PERFORMANCE );
}

