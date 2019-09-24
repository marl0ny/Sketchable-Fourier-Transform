/* Sketchable Fourier Transform
 *
 * Written in pure javascript.
 *
 * Inspired by:
 *
 * https://www.youtube.com/watch?v=qS4H6PEcCCA
 * https://www.youtube.com/watch?v=r6sGWTCMz2k
 *
 * Tested with:
 *  Google Chrome Version 75.0.3770.100
 *  Firefox Quantum Version 60.0.1
 */

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
  abs: absolute value
  arg: complex argument
  conj: conjugate
  inv: multiplicative inverse
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
  get abs(){
    /*absolute value */
    if (this.length === 0){
      return [];
    }
    let absArr = new Array (this.length);
    for (let i = 0; i < this.length; i++){
      absArr[i] = Math.sqrt(this.real[i]*this.real[i]
         + this.imag[i]*this.imag[i]);
    }
    return absArr;
  }
  get conj(){
    /*conjugate*/
    let conjugate = new ComplexArray([],[]);
    for (let i = 0; i < this.length; i++){
      conjugate.push(this.real[i], -this.imag[i]);
    }
    return conjugate;
  }
  get inv(){
    /*Multiplicative inverse */
    let inverse = new ComplexArray([],[]);
    let real, imag, abs2;
    for (let i = 0; i < this.length; i++){
      real = this.real[i];
      imag = this.imag[i];
      abs2 = real*real + imag*imag;
      inverse.push(real/abs2, -imag/abs2);
    }
    return inverse;
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
  get arg (){
    /*Return an array of args that correspond
    to each complex number*/
    let angles = new Array();
    for (let i = 0; i < this.length; i++){
      angles.push(this.getArgElement(i));
    }
    return angles;
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
  setToZeroes(){
    /*Set all real and imaginary values to zero */
    this.real = this.real.map((elem) => 0.0);
    this.imag = this.imag.map((elem) => 0.0);
  }
  setToVal(x, y){
    /*Set all real parts to a single value.
    Likewise, set all imaginary elements to another value.  */
    this.real = this.real.map((elem) => x);
    this.imag = this.imag.map((elem) => y);
  }
  multiplyElement(i, x, iy){
    /*Multiply a single element by
    a complex number */
    let real = this.real[i];
    let imag = this.imag[i];
    this.real[i] = x*real - iy*imag;
    this.imag[i] = x*imag + iy*real;
  }
  multiply(W){
    /*multiply this complex array by
    another complex array, element by element*/
    if (!W.hasOwnProperty('real')
    | !W.hasOwnProperty('imag')){
      throw ("Input array must" +
      " have a real and imaginary part");
    }
    if (W.length != this.length){
      throw "Arrays must have same length!";
    }
    let Z = new ComplexArray([], []);
    for (let i = 0; i < this.length; i++){
      Z.push(
        this.real[i]*W.real[i] - W.imag[i]*this.imag[i],
        this.imag[i]*W.imag[i] + W.imag[i]*this.real[i]
        );
    }
    return Z;
  }
  scaleCopy(val){
    /*Obtain a new copy of the complex array,
    scaled by the input value*/
    let x = [];
    let y = [];
    for (let i = 0; i < this.length; i++){
      x.push(this.real[i]*val);
      y.push(this.imag[i]*val);
    }
    return new ComplexArray(x, y);
  }
}

/*
 * Fourier Transform Function and Their Helpers
 *
 */


function bit_reverse_2_size_of_power2 (x, iy){
  /*Bit reverse the order in which the elements
  of two equal-length arrays
  are arranged.
  Their lengths must be of power two.

  For example, an array with elements
  [0b00, 0b01, 0b10, 0b11]
  becomes
  [0b00, 0b10, 0b01, 0b11].
  */
	let tempx, tempy;
  let u, d, rev;
  let n = x.length;
	for (let i = 0; i < n; i++){
	  /*Iterate in order from i to n*/
		u = 1, d = n >> 1, rev = 0;
		while (u < n){
		  /*To obtain the bit reversal of
		    a given number n:
		  -Initialize the variable 'rev'
		   that will store the bit reversed
		   number. This should be initialized
		   as 0...0 in binary.
		  -Bitwise AND n with 'u', where
		   the variable u is equal to 0...01.
		   This gives either a 0...01 or a
		   0...00, which is basically just
		   the value of n for the first
		   binary digit.
		  -Multiply this by the variable 'd',
		   where d is equal to 10...0. Add
		   this number to rev.
		  -Bit shift u to the left, so that
		   u is now equal to 0...010.
		  -Bit shift d to the right, so
		   that d is now equal to 010...0.
		  -Now bitwise AND n with u.
		  -Divide the above by d,
		   giving either a 0...01 or 0...0.
		  -Multiply this by d.
		  -Add the above to rev.
		  -Continue this process until all
		   digits are reversed.
		  */
			rev += d*((i&u)/u);
			u <<= 1;
			d >>= 1;
		}
		if (rev > i){
      /* Swap values */
			tempx = x[i];
			x[i] = x[rev];
			x[rev] = tempx;
			tempy = iy[i];
			iy[i] = iy[rev];
			iy[rev] = tempy;
		}
	}
}

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

let cos_arr = new Array (131072);

function iterative_radix2_fft (x, iy){
  /*Compute the in-place radix-2 discrete fast fourier transform iteratively
  using the Cooley-Turkey algorithm.

  Please note, the array cos_arr should be
  first defined outside this function.

  References:

  https://en.wikipedia.org/wiki/Cooley%E2%80%93Tukey_FFT_algorithm

  Press W. et al. (1992). Fast Fourier Transform.
  In Numerical Recipes in Fortran 77, chapter 12. Cambridge University Press.
  https://websites.pmc.ucsc.edu/~fnimmo/eart290c_17/NumericalRecipesinF77.pdf

  Although the above is in Fortran, it still gives a good overview
  of the algorithm.
  */

  let N = x.length;

  /*First bit reverse the order of the arrays.
  This is equivalent to putting the even-numbered
  elements to the lower half of the array and the odd-numbered
  elements to the upper half, then recursively repeating this to the
  lower and upper half of the array, and so on, until one
  gets to size-one arrays.*/
  bit_reverse_2_size_of_power2(x, iy);
	let angle=tau/N;
  let c, s;

  /*Create a cosine array lookup table.
  By symmetry, this can be used to find
  sin values as well*/
  cos_arr[0] = 1.0;
  cos_arr[N/8] = invsqrt2;
  cos_arr[N/4] = 0.0;
  cos_arr[3*N/8] = -invsqrt2;
  for (let i = 1; i < N/8; i++){
    c = Math.cos(i*angle);
    s = Math.sin(i*angle);
    cos_arr[i] = c;
    cos_arr[N/4-i] = s;
    cos_arr[N/4+i] = -s;
    cos_arr[N/2-i] = -c;
  }

  let x_even, x_odd;
  let iy_even, iy_odd;
  let reOddExp, iImOddExp;
  let n_of_ffts = N/2;
  for (let fft_size = 2; fft_size <= N; fft_size *= 2){
    /*This for loop controls the fft size.
    First fourier transform each size-two blocks of
    the array and then fourier transform each size-four,
    and so on.*/
    for (let j = 0; j < N; j += fft_size){
      /*Loop over each fft_size block of the array*/
      for (let i = 0; i < fft_size/2; i++){

        /*Get even and odd elements*/
        x_even = x[j + i];
        iy_even = iy[j + i];
        x_odd = x[fft_size/2 + j + i];
        iy_odd = iy[fft_size/2 + j + i];

        /*By symmetry, the sin portion of the exponential
        is expressed using a cos*/
        reOddExp = cos_arr[i*n_of_ffts]*x_odd - iy_odd*(
          (i*n_of_ffts < N/4)?
          (-cos_arr[i*n_of_ffts + N/4]): (cos_arr[i*n_of_ffts - N/4])
        );
      	iImOddExp = cos_arr[i*n_of_ffts]*iy_odd + x_odd*(
          (i*n_of_ffts < N/4)?
          (-cos_arr[i*n_of_ffts + N/4]): (cos_arr[i*n_of_ffts - N/4])
        );

        /* Butterfly */
        x[j + i]  = x_even +  reOddExp;
        iy[j + i] = iy_even + iImOddExp;
        x[fft_size/2 + j + i]  =  x_even - reOddExp;
        iy[fft_size/2 + j + i] = iy_even - iImOddExp;

      }
    }
    n_of_ffts = n_of_ffts/2;
  }
}

function isPowerofTwo(size){
  /*Determine if the given size is a power of two,
  up to a certain value*/
  switch (size){
    case 2: case 4: case 16: case 32: case 64:
    case 128: case 256: case 512: case 1024:
    case 2048: case 4096: case 8192: case 16384:
    case 32768: case 65536: case 131072:
    case 262144: case 524288:
      return true;
    default:
      return false;
  }
}

function fourierTransform(FT_Input, divideByN=true){
  /* Perform a discrete fourier transform. Input must be a
  ComplexArray*/
  let FT_Output = new ComplexArray();
  if (isPowerofTwo(FT_Input.length)
      && FT_Input.length > 511){
      FT_Output = ((divideByN)?
        FT_Input.scaleCopy(1/FT_Input.length):
        FT_Output = FT_Input.scaleCopy(1)
      );
      iterative_radix2_fft(FT_Output.real,
                           FT_Output.imag);
  }
  else{
    FT_Output = ft(FT_Input.real, FT_Input.imag);
    if (divideByN){
      for (let i = 0; i < FT_Output.length; i++){
        FT_Output.real[i] = FT_Output.real[i]/FT_Output.length;
        FT_Output.imag[i] = FT_Output.imag[i]/FT_Output.length;
      }
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
  /*Animation object

  //Constants
  tau: 6.2831853071795864
  invaqrt2: 0.70710678118654752

  //HTML canvas Attributes
  canvas: html canvas
  ctx: 2D context of canvas

  drawn: whether the canvas has been drawn or not

  holdClick: bool to identify if the mouse has
  been clicked

  prevPoint: store a previous 2D value.
    Useful in loops.
  TotalPoints: the total number of points

  //Array Attributes
  Input_2D: 2D complex array storing the x and y values
  FT_Output: Fourier transform output of Input_2D
  FT_Sketch: The figure sketched by the fourier circles
  f: frequency bins for the FT_Output

  //Animation Attributes
  j: Animation index
  j_itvl: number of animation frames that passed from
          the last jth animation
  itvl_lngth: Number of animation frames between
              the each j interval
  x_cvs: x canvas coordinates for plotting a single point
  y_cvs: y canvas coordinates for plotting a single point
  tmpAngle: angle used in calculating phasor positions
  showNPosFreq: number of positive frequencies to show
  showNNegFreq: number of negative frequencies to show

  */

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
  //Ani.ctx.strokeStyle = 'rgba(255, 255, 255, 1)'; //Blue
  //Ani.ctx.strokeStyle = 'rgba(255, 255, 255, 1)'; //White
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
  //Ani.ctx.strokeStyle = 'rgb(255,165,0)';
  //Ani.ctx.strokeStyle = 'rgba(255, 204, 102, 1)'; //Orange
  //Ani.ctx.strokeStyle = 'rgba(0, 0, 0, 1)'; //Black
  //Ani.ctx.strokeStyle = 'rgba(51, 153, 255, 1)'; //Blue
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


document.addEventListener("mousemove", plotPoints);

function plotPoints(event){
  /*This function deals with plotting points in the canvas*/

  //Get button positions
  if (event.buttons !== 0){
    x = event.clientX - Ani.canvas.offsetLeft;
    y = event.clientY - Ani.canvas.offsetTop;

    //This if statement is only runned immediately after the mouse is
    //clicked
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

  //This portion of code is runned when nothing is being clicked.
  else if (event.buttons === 0){

    //The following if statement is only runned immediately after
    //the mouse is released, which should happpen directly after one is done
    //sketching.
    if (Ani.holdClick){
      Ani.holdClick = false;
      Ani.ctx.closePath();
      Ani.TotalPoints = Ani.Input_2D.length;

      /*Some of the stuff written in the
      Ani object assumes an even number of input,
      so change input to an even amount if it
      isn't already.*/

      /*
      if (Ani.TotalPoints %2 !== 0){
        Ani.TotalPoints += 1;
        Ani.Input_2D.push(
          Ani.Input_2D.real[0],
          Ani.Input_2D.imag[0]
          );
      }*/

      /*This block atttempts to alleviate the Gibbs effect.*/
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

    //This else if is only runned at the very beggining,
    //which gives instructions for how to use.
    else if (!Ani.drawn){
      Ani.canvas.width = document.documentElement.clientWidth;
      Ani.canvas.height = document.documentElement.clientHeight*0.95;
      Ani.ctx.font = "80px Arial";
      Ani.ctx.fillStyle = "White";
      Ani.ctx.fillText("Sketch here", 3*Ani.canvas.width/8,
                                   3*Ani.canvas.height/8);
    }
  }
}
