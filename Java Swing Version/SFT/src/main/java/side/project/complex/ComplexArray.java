package main.java.side.project.complex;

import java.util.ArrayList;

class UnequalArrayLengthsException extends RuntimeException {}

public class ComplexArray extends ArrayList<Complex> {

    public ComplexArray() {}

    public ComplexArray(int n) {
        for (int i = 0; i < n; i++){
            this.add(new Complex());
        }
    }

    public ComplexArray(double [] reValues) {
        for (double real: reValues){
            this.add(new Complex(real, 0.0));
        }
    }

    public ComplexArray(double [] reValues, double [] imValues)
            throws UnequalArrayLengthsException{
        if (reValues.length != imValues.length){
            throw new UnequalArrayLengthsException();
        }
        for (int i = 0; i < reValues.length; i++){
            this.add(new Complex(reValues[i], imValues[i]));
        }
    }

    public static ComplexArray toComplexArray(double [] reValues) {
        ComplexArray array = new ComplexArray();
        for (double real: reValues){
            array.add(new Complex(real, 0.0));
        }
        return array;
    }

    public static String toString(ComplexArray array) {
        if (array.isEmpty()){
            return "[]";
        }
        StringBuilder str = new StringBuilder("[");
        if (array.size() < 100){
            for (int i = 0; i < array.size(); i++){
                str.append(Complex.toString(array.get(i)));
                if (i < array.size() - 1){
                    str.append(", ");
                }
                else{
                    str.append("]");
                }
            }
        }else{
            for (int i = 0; i < 5; i++){
                str.append(Complex.toString(array.get(i)));
                str.append(", ");
            }
            str.append("..., ");
            str.append(Complex.toString(array.get(array.size() - 1))).append("]");
        }
        return str.toString();
    }

    public void add(double re, double im) {
        super.add(new Complex(re, im));
    }

    @Override
    public ComplexArray clone() {
        ComplexArray newArr = new ComplexArray();
        for (Complex z: this) {
            newArr.add(z);
        }
        return newArr;
    }

    public static ComplexArray linspace(double start, double end,
                                        int n) {
        ComplexArray array = new ComplexArray();
        array.add(new Complex(start, 0.0));
        double val = start;
        double step = (end - start)/(n - 1);
        for (int i = 1; i < n; i++){
            val += step;
            array.add(new Complex(val, 0.0));
        }
        return array;
    }
}