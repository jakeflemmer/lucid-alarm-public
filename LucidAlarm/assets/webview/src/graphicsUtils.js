function roundRect(ctx, x, y, width, height, radius, fill, stroke,lineWidth) {
  if (typeof stroke == "undefined" ) {
    stroke = true;
  }
  if (typeof radius === "undefined") {
    radius = 5;
  }
  ctx.beginPath();
  ctx.moveTo(x + radius, y);
  ctx.lineTo(x + width - radius, y);
  ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
  ctx.lineTo(x + width, y + height - radius);
  ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
  ctx.lineTo(x + radius, y + height);
  ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
  ctx.lineTo(x, y + radius);
  ctx.quadraticCurveTo(x, y, x + radius, y);
  ctx.closePath();
  if (stroke) {
  	ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  if (fill) {
    ctx.fill();
  }        
}

function roundRectNoLeftSide(ctx, x, y, width, height, radius, fill, stroke,lineWidth) {
  if (typeof stroke == "undefined" ) {
    stroke = true;
  }
  if (typeof radius === "undefined") {
    radius = 5;
  }
  ctx.beginPath();
  ctx.moveTo(x , y);
  ctx.lineTo(x + width - radius, y);
  ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
  ctx.lineTo(x + width, y + height - radius);
  ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
  ctx.lineTo(x , y + height);
  // ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
  // ctx.moveTo(x, y + radius);
  // ctx.quadraticCurveTo(x, y, x + radius, y);
  //ctx.closePath();
  if (stroke) {
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  if (fill) {
    ctx.fill();
  }        
}

function roundRectNoRightSide(ctx, x, y, width, height, radius, fill, stroke,lineWidth) {
  if (typeof stroke == "undefined" ) {
    stroke = true;
  }
  if (typeof radius === "undefined") {
    radius = 5;
  }
  ctx.beginPath();
  ctx.moveTo(x + radius , y);
  ctx.lineTo(x + width - radius, y);
  //ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
  //ctx.lineTo(x + width, y + height - radius);
  //ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
  ctx.moveTo(x + width - radius , y + height );
  ctx.lineTo(x + radius , y + height);
  ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
  ctx.lineTo(x, y + radius);
  ctx.quadraticCurveTo(x, y, x + radius, y);
  //ctx.closePath();
  if (stroke) {
    ctx.lineWidth = lineWidth;
    ctx.stroke();
  }
  if (fill) {
    ctx.fill();
  }        
}