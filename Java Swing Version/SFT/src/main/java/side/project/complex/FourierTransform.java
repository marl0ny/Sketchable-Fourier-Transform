package main.java.side.project.complex;

public class FourierTransform{

    public static ComplexArray fourierTransform(ComplexArray array) {
        ComplexArray FT = new ComplexArray();
        double angle = Complex.TAU/array.size();
        for (int i = 0; i < array.size(); i++){
            FT.add(new Complex());
            for (int j = 0; j < array.size(); j++){
                FT.set(i,
                        FT.get(i).add(
                                array.get(j).multiply(
                                        Functions.exp(
                                                new Complex(
                                                        0.0, angle*i*j))).divide(
                                                                array.size())));
            }
        }
        return FT;
    }

    public static double [] fourierFrequencies(int n) {
        double [] freq = new double[n];
        for (int i = 0; i < n/2; i++){
            freq[i] = i;
        }
        int k=-1;
        for (int j = n - 1; j >= n/2; j--){
            freq[j] = k;
            k--;
        }
        return freq;
    }
}