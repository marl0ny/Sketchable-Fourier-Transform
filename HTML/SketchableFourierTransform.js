// Useful constants
const tau = 6.2831853071795864;
const invsqrt2 = 0.70710678118654752;

/*
 * Complex Array Class for Handling Complex Numbers
 *
 */

class ComplexArray{
  /*Complex-valued array.
  length: number of elements
  real: real part
  imag: imaginary part
  */
  constructor(x=[0.0], y=[0.0]){
    if (!Array.isArray(x) | !Array.isArray(y)){
      throw "Input must be arrays!";
    }
    if (x.length != y.length){
      throw "Array lengths are not equal!";
    }
    this.length = x.length;
    this.real = x;
    this.imag = y;
  }
  getArgElement(i){
    /*Get the angle for a single element*/
    let arg = 0.0;
    let real = this.real[i];
    let imag = this.imag[i];
    if (real >= 0.0 && imag >= 0.0){
      arg = Math.atan(imag/real);
    }else if (real > 0.0 && imag < 0.0){
      arg = tau + Math.atan(imag/real);
    }else if (real < 0.0 && imag > 0.0){
      arg = tau/2 + Math.atan(imag/real);
    }else if (real <= 0.0 && imag <= 0.0){
      arg = tau/2 + Math.atan(imag/real);
    }
    return arg;
  }
  push (x, y){
    /*Append new elements at the end of the array */
    this.length += 1;
    this.real.push(x);
    this.imag.push(y);
  }
  pop (){
    /*pop the last values at the end of the array */
    this.length -= 1;
    x = this.real.pop();
    y = this.imag.pop();
    return new ComplexArray([x], [y]);
  }
  clear (){
    /* Clear arrays of contents */
    this.length = 0.0;
    this.real = [];
    this.imag = [];
  }
  multiplyElement(i, x, iy){
    /*Multiply a single element by
    a complex number */
    let real = this.real[i];
    let imag = this.imag[i];
    this.real[i] = x*real - iy*imag;
    this.imag[i] = x*imag + iy*real;
  }
}

/*
 * Fourier Transform Function and Their Helpers
 *
 */

function ft(x, iy){
  /* This is the general, *slow* fourier transform. */
  let N = x.length;
  let u = new Array (N);
  let iv = new Array (N);
  let angle = tau/N;
  for (let i = 0; i < N; i++){
    u[i] = 0.0;
    iv[i] = 0.0;
    for (let j = 0; j < N; j++){
      u[i] += Math.cos(angle*i*j)*x[j] - Math.sin(angle*i*j)*iy[j];
      iv[i] += Math.cos(angle*i*j)*iy[j] + Math.sin(angle*i*j)*x[j];
    }
  }
  return new ComplexArray(u, iv);
}


function fourierTransform(FT_Input, divideByN=true){
  /* Perform a discrete fourier transform. Input must be a
  ComplexArray*/
  FT_Output = ft(FT_Input.real, FT_Input.imag);
  if (divideByN){
	  for (let i = 0; i < FT_Output.length; i++){
		  FT_Output.real[i] = FT_Output.real[i]/FT_Output.length;
		  FT_Output.imag[i] = FT_Output.imag[i]/FT_Output.length;
      }
    }
  return FT_Output;
}

function fftfreq (N){
  /*Obtain the corresponding frequencies of the fft output */
	var freq = new Array (N);
  for (let i = 0; i < N/2; i++){
    freq[i] = i;
  }
  let k=-1;
  for (let j = N-1; j >= N/2; j--){
    freq[j] = k;
    k--;
  }
	return freq;
}

/*
 * Other functions
 *
 */

let euclideanDistance = (x, y) => Math.sqrt(x*x + y*y);

/*
 * Animation and Canvas Drawing
 *
 */

class AnimationObject{

  constructor(){

    /*The tau and 1/sqrt(2) constants are not in the
    scope when the animate method is put in
    requestAnimationFrame; so define it as attributes*/
    this.tau = 6.2831853071795864;
    this.invsqrt2 = 0.70710678118654752;

    this.canvas = document.getElementById("sketchCanvas");
    this.canvas.width = document.documentElement.clientWidth;
    this.canvas.height = document.documentElement.clientHeight*0.95;
    this.ctx = this.canvas.getContext("2d");

    this.drawn = false;

    this.holdClick = false;

    this.prevPoint = {x:0, y:0};
    this.TotalPoints = 1024;
    this.Input_2D = new ComplexArray();
    this.FT_Output = new ComplexArray();
    this.FT_Sketch = new ComplexArray();
    this.f = new Array([]);

    this.j = 0;
    this.j_itvl = 0;
    this.itvl_lngth = 4;
    this.x_cvs = 0;
    this.y_cvs = 0;
    this.tmpAngle = 0.0;
    this.showNPosFreq = 999999;
    this.showNNegFreq = 999999;
  }

  updateCircle (index){
    /*update the cicles and phasors in the animation */
    this.FT_Output.multiplyElement(
      index,
      Math.cos(tau*this.f[index]/(this.TotalPoints*this.itvl_lngth)),
      -Math.sin(tau*this.f[index]/(this.TotalPoints*this.itvl_lngth))
      );
    if ((Math.abs(index) <= this.showNPosFreq && this.f[index] > 0) ||
        (Math.abs(index) >= this.showNNegFreq && this.f[index] < 0)
      )
      {
        this.x_cvs = this.FT_Output.real[index] + this.prevPoint.x;
        this.y_cvs = this.FT_Output.imag[index] + this.prevPoint.y;
        this.tmpAngle = this.FT_Output.getArgElement(index);
        this.ctx.arc(
          this.prevPoint.x, this.prevPoint.y,
          Math.sqrt(
            this.FT_Output.real[index]*this.FT_Output.real[index]
            + this.FT_Output.imag[index]*this.FT_Output.imag[index]),
            this.tmpAngle,
            tau+this.tmpAngle
          );
        this.ctx.lineTo(this.x_cvs, this.y_cvs);
        this.ctx.moveTo(this.x_cvs, this.y_cvs);
        this.prevPoint.x = this.x_cvs;
        this.prevPoint.y = this.y_cvs;
        }
  }

  increment(){
    /*This controls the speed of the animation */
    this.j_itvl += 1;
    if (this.j_itvl % this.itvl_lngth === 0){
      this.j_itvl = 0;
      return 1;
    }
    else{
      return 0;
    }
  }

  setFrequencyBins(negFlipped, pos){
    /*Set which frequency bins to show*/
    this.showNPosFreq = pos;
    this.showNNegFreq = this.TotalPoints - negFlipped;
    this.FT_Sketch.clear();
  }
}

Ani = new AnimationObject();

Ani.animate = function(){
  /*Animation function. Apparently this has to be
  defined with respect to an instance of an
  AnimationObject and not in the object definition itself.
  */

  //j =Ani.j % (Ani.TotalPoints);

  Ani.ctx.clearRect(0,0, Ani.canvas.width, Ani.canvas.height);


  //*** Draw the original path***//
  Ani.ctx.beginPath()
  Ani.ctx.strokeStyle = 'rgba(100, 100, 100, 1)'; //Gray
  Ani.ctx.moveTo(Ani.Input_2D.real[0], Ani.Input_2D.imag[0]);
  for (let i = 1; i < Ani.Input_2D.length; i++){
    Ani.ctx.lineTo(Ani.Input_2D.real[i], Ani.Input_2D.imag[i]);
    Ani.ctx.moveTo(Ani.Input_2D.real[i], Ani.Input_2D.imag[i]);
  }
  Ani.ctx.stroke();
  Ani.ctx.closePath();
  //*****************************//

  //*** Draw the fourier circles ***//
  Ani.ctx.beginPath();
  Ani.ctx.strokeStyle = 'rgba(255, 204, 102, 1)'; //Orange
  Ani.ctx.lineWidth = 1.0;
  Ani.ctx.moveTo(Ani.FT_Output.real[0], Ani.FT_Output.imag[0]);
  Ani.prevPoint.x = Ani.FT_Output.real[0];
  Ani.prevPoint.y = Ani.FT_Output.imag[0];
  for (let i = 1, k = Ani.FT_Output.length - 1;
  i < Ani.FT_Output.length/2; i++, k--){
    Ani.updateCircle(i);
    Ani.updateCircle(k);
  }
  if (Ani.FT_Output.length%2 == 0){
    Ani.updateCircle(Ani.FT_Output.length/2);
  }else{
    //Ani.updateCircle((Ani.FT_Output.length+1)/2);
  }
  Ani.ctx.stroke();
  Ani.ctx.closePath();
  //********************************//

  //*** Draw the path made by the rotating fourier circles ***//
  Ani.ctx.beginPath();
  Ani.ctx.strokeStyle = 'rgba(255, 255, 255, 1)'; //White
  Ani.ctx.lineWidth = 1.5;
  Ani.FT_Sketch.push(Ani.prevPoint.x, Ani.prevPoint.y);
  for (let i = Ani.FT_Sketch.length; i >= 0; i--){
    Ani.ctx.lineTo(Ani.FT_Sketch.real[i], Ani.FT_Sketch.imag[i]);
    Ani.ctx.moveTo(Ani.FT_Sketch.real[i], Ani.FT_Sketch.imag[i]);
  }
  Ani.ctx.stroke();
  Ani.ctx.closePath();
  if (Ani.FT_Sketch.length === Ani.FT_Output.length*Ani.itvl_lngth){
      Ani.FT_Sketch.clear();
    }
  //**********************************************************//

  Ani.j += Ani.increment();
  req = requestAnimationFrame(Ani.animate);
};

document.addEventListener("touchstart", touchPlotPointsOnMove);
document.addEventListener("touchmove", touchPlotPointsOnMove);
document.addEventListener("touchend", ev => plotPointsOnRelease());
document.addEventListener("mousemove", mousePlotPoints);


/*
This function deals with plotting points in the canvas
*/
function mousePlotPoints(event){

  // Get button positions
  if (event.buttons !== 0){
    x = event.clientX - Ani.canvas.offsetLeft;
    y = event.clientY - Ani.canvas.offsetTop;
    plotPointsOnMove(x, y);

  // When nothing is being clicked.
  } else if (event.buttons === 0) {
    // The following if statement is only reached immediately after
    // the mouse is released, which should happen directly after one is done
    // sketching.
    if (Ani.holdClick) {
      plotPointsOnRelease();
    // This else if is only reached at the very beginning,
    // which gives instructions for how to use.
    } else if (!Ani.drawn) {
      onStart();
    }
  }
}


function touchPlotPointsOnMove(event) {
  var touches = event.changedTouches;
  for (var i = 0; i < touches.length; i++) {
    x = touches[i].pageX - Ani.canvas.offsetLeft;
    y = touches[i].pageY - Ani.canvas.offsetTop;
    plotPointsOnMove(x, y);
  }
}


function plotPointsOnMove(x, y) {
  // The code within this
  // if statement is only reached immediately after the mouse is
  // clicked
  if (!Ani.holdClick){
    if(Ani.drawn){
      cancelAnimationFrame(req);
      Ani.ctx.clearRect(0,0, Ani.canvas.width, Ani.canvas.height);
    }
    Ani.canvas.width = document.documentElement.clientWidth;
    Ani.canvas.height = document.documentElement.clientHeight*0.95;
    Ani.FT_Sketch.clear();
    Ani.Input_2D.clear();
    Ani.holdClick = true;
    Ani.ctx.beginPath();
    Ani.ctx.strokeStyle = 'rgba(255, 255, 255, 1)'; //White
    Ani.ctx.lineWidth = 2.0;
    Ani.ctx.moveTo(x, y);
    //console.log(x, y);
    Ani.prevPoint.x = x;
    Ani.prevPoint.y = y;
    Ani.Input_2D.push(x, y);
    Ani.drawn = true;
  }
  Ani.ctx.bezierCurveTo(Ani.prevPoint.x, Ani.prevPoint.y, x, y, x, y);
  Ani.ctx.stroke();
  Ani.ctx.moveTo(x, y);
  Ani.prevPoint.x = x;
  Ani.prevPoint.y = y;
  Ani.Input_2D.push(x, y);
}


function plotPointsOnRelease() {
  Ani.holdClick = false;
  Ani.ctx.closePath();
  Ani.TotalPoints = Ani.Input_2D.length;
  // Alleviate the Gibbs effect
  let tmpx = Ani.Input_2D.real[0] - Ani.Input_2D.real[Ani.TotalPoints - 1];
  let tmpy = Ani.Input_2D.imag[0] - Ani.Input_2D.imag[Ani.TotalPoints - 1];
  let tmpdist = euclideanDistance(tmpx, tmpy);
  let tmpN = Math.round(tmpdist*10/500);
  for (let i = 1; i < tmpN; i++){
    Ani.Input_2D.push(
      Ani.Input_2D.real[Ani.TotalPoints - i] + i*tmpx/tmpN,
        Ani.Input_2D.imag[Ani.TotalPoints - i] + i*tmpy/tmpN
      )
      Ani.TotalPoints += 1;
  }
  if (Ani.TotalPoints%2 === 0){
    Ani.showNPosFreq = (Ani.TotalPoints - 1)/2;
    Ani.showNNegFreq = Ani.TotalPoints/2 + 1;
  }else{
    Ani.showNPosFreq = (Ani.TotalPoints - 1)/2;
    Ani.showNNegFreq = (Ani.TotalPoints - 1)/2;
  }
  Ani.f = fftfreq(Ani.TotalPoints);
  Ani.FT_Output = fourierTransform(Ani.Input_2D);
  Ani.j = 0;
  for (let i = 1; i < tmpN; i++){
    Ani.Input_2D.pop();
  }
  //Only request drawing if something is already sketched out
  if (Ani.drawn){
    requestAnimationFrame(Ani.animate);
  }
}


function onStart() {
  Ani.canvas.width = document.documentElement.clientWidth;
  Ani.canvas.height = document.documentElement.clientHeight*0.95;
  Ani.ctx.font = "80px Arial";
  Ani.ctx.fillStyle = "White";
  Ani.ctx.fillText("Sketch here", 3*Ani.canvas.width/8,
                                3*Ani.canvas.height/8);
}
