package side.project.sketchycircles;

import side.project.sketchycircles.complex.Complex;
import side.project.sketchycircles.complex.ComplexArray;
import side.project.sketchycircles.complex.Functions;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;

import static java.lang.System.out;

public class TestComplexArray {

    public static int testCount = 0;

    @Before
    public void before() {
        testCount++;
    }

    @Test
    public void argTest() {
        double invsqrt2 = 1.0/Math.sqrt(2.0);
        ComplexArray array = new ComplexArray(
                new double [] {1.0,  invsqrt2, 0.0, -invsqrt2,
                        -1.0, -invsqrt2,  0.0,  invsqrt2},
                new double [] {0.0,  invsqrt2, 1.0,  invsqrt2,
                        0.0, -invsqrt2, -1.0, -invsqrt2}
        );
        double val = 0.0;
        for (Complex cmplx:array){
            Assert.assertEquals(
                    "Argument of " + cmplx + ":",
                    cmplx.getArg()/Math.PI, val, 1e-60);
            val += 0.25;
        }
        out.println();
    }

    @Test
    public void complexArrayToStringTest() {
        ComplexArray a = new ComplexArray();
        Assert.assertEquals(
                "Empty array:", a.toString(), "[]");
        ComplexArray b = new ComplexArray(new double []{1.0, 2.0, 3.0});
        Assert.assertEquals(
                "Another array:", b.toString(),
                "[1.0, 2.0, 3.0]");
        ComplexArray z = new ComplexArray(
                new double[6],
                new double[] {0., 0.25, 0.5, 0.75, 1.0, 1.25});
        Assert.assertEquals(
                "Another array:", z.toString(),
                "[0, 0.25i, 0.5i, 0.75i, 1.0i, 1.25i]");
        /*
        out.println("Another array:");
        ComplexArray z = new ComplexArray(
                new double[6],
                new double[] {0., Math.PI/4, Math.PI/2,
                        3.0*Math.PI/4, Math.PI, 5*Math.PI/4});
        out.println(ComplexArray.toString(z));
        out.println("Exponentiation of the previous array:");
        out.println(ComplexArray.toString(Functions.exp(z)));
        // ComplexArray z1 = new ComplexArray(10);
        out.println();
        */
    }

    @Test
    public void testLinspace() {
        ComplexArray t = ComplexArray.linspace(
                -3*Math.PI, 10, 16);
        double [] actT = {-9.42477796, -8.12979276, -6.83480757,
                -5.53982237, -4.24483717, -2.94985197,
                -1.65486678, -0.35988158,  0.93510362,
                2.23008882, 3.52507401,  4.82005921,
                6.11504441,  7.41002961,  8.7050148 ,
                10.};
        for (int i = 0; i < 16; i++) {
            Assert.assertEquals(
                    t.get(i).getReal(), actT[i], 1e-8);
        }
    }

    @Test
    public void testArraySwap() {
        double [] x = new double [] {1.0, 2.0, 3.0};
        double [] iy = new double [] {-1.0, 1.0, 10.0};
        ComplexArray z = new ComplexArray(x, iy);
        Complex.swapComplex(z.get(0), z.get(2));
        Assert.assertEquals(
                "[3.0 + 10.0i, 2.0 + 1.0i, 1.0 - 1.0i]",
                z.toString()
        );
    }

    public static void main(String [] args) {
        Result result = JUnitCore.runClasses(TestComplexArray.class);
        int failCount = 0;
        for (Failure failure: result.getFailures()) {
            failCount++;
            out.println(failure.toString());
        }
        out.printf("Passed %d/%d\n", (testCount - failCount), testCount);
    }
}