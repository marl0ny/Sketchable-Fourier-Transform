package main.java.side.project.complex;

class DivisionByZeroException extends ArithmeticException {}

public class Complex {

    private double r = 0.0;
    private double i = 0.0;
    public static final double TAU = 2*Math.PI;

    public Complex() {}

    public Complex(double real) { this.r = real; }

    public Complex(double real, double imag) {
        this.r = real;
        this.i = imag;
    }

    public double getReal() {
        return this.r;
    }

    public double getImag() {
        return this.i;
    }

    private double getAbs2() {
        return this.r*this.r + this.i*this.i;
    }

    public double getAbs() {
        return Math.sqrt(this.getAbs2());
    }

    public double getArg() {
        double arg = 0.;
        if (r >= 0.0 && i >= 0.0){
            arg = Math.atan(i/r);
        }else if (r >= 0.0 && i < 0.0){
            arg = TAU + Math.atan(i/r);
        }else if (r <= 0.0 && i > 0.0){
            arg = TAU/2 + Math.atan(i/r);
        }else if (r <= 0.0 && i <= 0.0){
            arg = TAU/2 + Math.atan(i/r);
        }
        return arg;
    }

    public Complex getConj() {
        return new Complex(this.r, -this.i);
    }

    public Complex getInv() {
        double abs2 = this.getAbs2();
        return new Complex(this.r/abs2, -this.i/abs2);
    }

    public void setReal(double r) {
        this.r = r;
    }

    public void setImag(double i) {
        this.i = i;
    }

    public void conjugate() {
        this.i *= -1.0;
    }

    public void invert() {
        double abs2 = this.getAbs2();
        this.r = this.r/abs2;
        this.i = -this.i/abs2;
    }

    public Complex divide(Complex z) {
        double re = z.getReal();
        double im = z.getImag();
        double abs2 = re*re + im*im;
        if (abs2 == 0.0){
            throw new DivisionByZeroException();
        }
        re *= (1.0/abs2);
        im *= -(1.0/abs2);
        return new Complex(re*this.r - im*this.i,
                this.r*im + this.i*re
        );
    }

    public Complex divide(double real) {
        if (real == 0.0) { throw new DivisionByZeroException(); }
        return new Complex(this.r/real, this.i/real);
    }

    public Complex divide(double real, double imag) {
        double abs2 = real*real + imag*imag;
        if (abs2 == 0.0){
            throw new DivisionByZeroException();
        }
        real *= (1.0/abs2);
        imag *= (-1.0/abs2);
        return this.multiply(real, imag);
    }

    public Complex multiply(Complex z) {
        return new Complex(this.r*z.r - this.i*z.i,
                this.i*z.r + this.r*z.i);
    }

    public Complex multiply(double real) {
        return new Complex(real*this.r, real*this.i);
    }

    public Complex multiply(double real, double imag) {
        return new Complex(real*this.r - imag*this.i,
                this.r*imag + this.i*real
        );
    }

    public Complex add(Complex z) {
        return new Complex(this.r + z.r, this.i + z.i);
    }

    public Complex add(double real, double imag) {
        return new Complex(this.r + real, this.i + imag);
    }

    public Complex subtract(Complex z) {
        return new Complex(this.r - z.r, this.i - z.i);
    }

    public Complex subtract(double real, double imag) {
        return new Complex(this.r - real, this.i - imag);
    }

    public static String toString(Complex z){
        String str = "";
        if (z.r == 0.0){
            if (z.i == 0.0){
                str += "0";
            }
            else if (z.i < 0.0){
                str += "-";
            }
        }
        else{
            str += z.r;
            if (z.i != 0){
                str += (z.i > 0)? " + " : " - ";
            }
        }
        if (z.i != 0.0){
            str += (z.i > 0.0)? (z.i + "i") : ((-1.0)*z.i + "i");
        }
        return str;
    }

    public static void swapComplex(Complex z1, Complex z2) {
        double tmpX = z1.getReal();
        double tmpY = z1.getImag();
        z1.setReal(z2.getReal());
        z1.setImag(z2.getImag());
        z2.setReal(tmpX);
        z2.setImag(tmpY);
    }

}