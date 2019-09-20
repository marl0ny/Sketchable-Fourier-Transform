package side.project.complex;

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
            return new Complex(
                    Math.sin(z.getReal()), 0.0);
        }
        else{
            double re = z.getReal();
            double im = z.getImag();
            double real = Math.sin(re)*Math.cosh(im);
            double imag = Math.cos(re)*Math.sinh(im);
            return new Complex(real, imag);
        }
    }

    public static ComplexArray sin(ComplexArray array) {
        ComplexArray array2 = new ComplexArray();
        for (int i = 0; i < array.size(); i++){
            array2.add(sin(array.get(i)));
        }
        return array2;
    }

    public static Complex cos(Complex z){
        if (z.getImag() == 0.0){
            return new Complex(
                    Math.cos(z.getReal()), 0.0);
        }
        else{
            double re = z.getReal();
            double im = z.getImag();
            double real = Math.cos(re)*Math.cosh(im);
            double imag = -Math.sin(re)*Math.sinh(im);
            return new Complex(real, imag);
        }
    }

    public static ComplexArray cos(ComplexArray array) {
        ComplexArray array2 = new ComplexArray();
        for (int i = 0; i < array.size(); i++){
            array2.add(cos(array.get(i)));
        }
        return array2;
    }
}