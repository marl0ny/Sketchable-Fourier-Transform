package side.project.complex;

public class FourierTransform{

    private static final double INVSQRT2 = 0.70710678118654752;

    public static ComplexArray fourierTransform(ComplexArray array) {
        ComplexArray FT = new ComplexArray();
        double angle = Complex.TAU/array.size();
        Complex z = new Complex();
        for (int i = 0; i < array.size(); i++){
            FT.add(new Complex());
            for (int j = 0; j < array.size(); j++){
                z.setImag(angle*i*j);
                FT.get(i).addBy(array.get(j).multiply(
                        Functions.exp(z)).divide(array.size()));
            }
        }
        return FT;
    }

    private static boolean isPowerOfTwo(int size) {
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

    private static void bitReverseSize2(ComplexArray array) {
        int n = array.size();
        int u, d, rev;
        for (int i = 0; i < n; i++){
            u = 1;
            d = n >> 1;
            rev = 0;
            while (u < n){
                rev += d*((i&u)/u);
                u <<= 1;
                d >>= 1;
            }
            if (rev >= i){
                Complex.swapComplex(
                        array.get(i),
                        array.get(rev));
            }
        }
    }

    private static void setCosArr(double [] cosArr, int n) {
        double c, s;
        double angle = Complex.TAU/n;
        cosArr[0] = 1.0;
        cosArr[n/8] = INVSQRT2;
        cosArr[n/4] = 0.0;
        cosArr[3*n/8] = -INVSQRT2;
        for (int i = 1; i < n/8; i++){
            c = Math.cos(i*angle);
            s = Math.sin(i*angle);
            cosArr[i] = c;
            cosArr[n/4 - i] = s;
            cosArr[n/4 + i] = -s;
            cosArr[n/2 - i] = -c;
        }
    }

    /*
    Implementation of the iterative in place
    radix-2 fast Fourier transform algorithm.

    References:

    https://en.wikipedia.org/wiki/Cooley%E2%80%93Tukey_FFT_algorithm

    Press W. et al. (1992). Fast Fourier Transform.
    In Numerical Recipes in Fortran 77, chapter 12. Cambridge University Press.
    https://websites.pmc.ucsc.edu/~fnimmo/eart290c_17/NumericalRecipesinF77.pdf

    Although the above reference is in Fortran, it still has an
    excellent overview of the algorithm.*/
    public static void inPlaceFastFourierTransform(ComplexArray array) {
        if (isPowerOfTwo(array.size())){

            int n = array.size();

            FourierTransform.bitReverseSize2(array);

            double [] cosArr = new double [n/2];
            FourierTransform.setCosArr(cosArr, n);

            int numberOfBlocks = n/2;
            Complex even, odd;
            Complex exp = new Complex();
            double cosVal, sinVal;
            for (int blockSize = 2; blockSize <= n; blockSize *= 2) {
                for (int j = 0; j < n; j += blockSize) {
                    for (int i = 0; i < blockSize/2; i++) {

                        even = array.get(j + i);
                        //even.divideBy(n);
                        odd = array.get(blockSize/2 + j + i);
                        //odd.divideBy(n);

                        cosVal = cosArr[i*numberOfBlocks];
                        sinVal = (i*numberOfBlocks < n/4)?
                                (-cosArr[i*numberOfBlocks + n/4]):
                                ( cosArr[i*numberOfBlocks - n/4]);

                        exp.setReal(
                                cosVal*odd.getReal() - odd.getImag()*sinVal);
                        exp.setImag(
                                cosVal*odd.getImag() + odd.getReal()*sinVal);

                        array.set(i + j, even.add(exp));
                        array.set(blockSize/2 + i + j, even.subtract(exp));
                    }
                }
                numberOfBlocks = numberOfBlocks/2;
            }
        }
        else{
            throw new UnsupportedOperationException();
        }
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