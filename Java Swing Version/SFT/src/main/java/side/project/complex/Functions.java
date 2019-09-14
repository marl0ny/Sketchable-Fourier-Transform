package main.java.side.project.complex;

public final class Functions{

    public static final double TAU = 2*Math.PI;

    public static Complex exp(Complex z) {
        double re = z.getReal();
        double im = z.getImag();
        return new Complex(Math.exp(re)*Math.cos(im),
                Math.exp(re)*Math.sin(im));
    }

    public static Complex exp(double re, double im) {
        return new Complex(Math.exp(re)*Math.cos(im),
                Math.exp(re)*Math.sin(im));
    }

    public static ComplexArray exp(ComplexArray array) {
        ComplexArray array2 = new ComplexArray();
        for (int i = 0; i < array.size(); i++){
            array2.add(exp(array.get(i)));
        }
        return array2;
    }

    public static Complex sin(Complex z){
        if (z.getImag() == 0.0){
            return new Complex(Math.sin(z.getReal()), 0.0);
        }
        else{
            return (
                    exp(z).subtract(exp(z).getConj())).multiply(
                    new Complex(0, 0.5));
        }
    }

    public static ComplexArray sin(ComplexArray array) {
        ComplexArray array2 = new ComplexArray();
        for (int i = 0; i < array.size(); i++){
            array2.add(sin(array.get(i)));
        }
        return array2;
    }
}