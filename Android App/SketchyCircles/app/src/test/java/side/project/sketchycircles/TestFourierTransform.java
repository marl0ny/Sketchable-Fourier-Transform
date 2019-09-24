package side.project.sketchycircles;

import side.project.sketchycircles.complex.ComplexArray;
import side.project.sketchycircles.complex.FourierTransform;
import side.project.sketchycircles.complex.Functions;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;

import static java.lang.System.out;

public class TestFourierTransform {

    static int testCount = 0;

    @Before
    public void before() {
        testCount++;
    }

    @Test
    public void testFourierTransform1() {
        ComplexArray t = ComplexArray.linspace(-Math.PI, Math.PI, 32);
        ComplexArray z = Functions.sin(t);
        ComplexArray w = FourierTransform.fourierTransform(z);
        double [] actReal = {   1.73472348e-17, -4.81430483e-02,  4.30223071e-03,  3.58014089e-03,
                3.38162853e-03,  3.29715347e-03,  3.25316344e-03,  3.22736613e-03,
                3.21101913e-03,  3.20010220e-03,  3.19255382e-03,  3.18723078e-03,
                3.18346254e-03,  3.18084116e-03,  3.17911513e-03,  3.17813321e-03,
                3.17781440e-03,  3.17813321e-03,  3.17911513e-03,  3.18084116e-03,
                3.18346254e-03,  3.18723078e-03,  3.19255382e-03,  3.20010220e-03,
                3.21101913e-03,  3.22736613e-03,  3.25316344e-03,  3.29715347e-03,
                3.38162853e-03,  3.58014089e-03,  4.30223071e-03, -4.81430483e-02};
        double [] actImag = {  0.00000000e+00,  4.88804573e-01, -2.16287744e-02, -1.18021428e-02,
                -8.16397345e-03, -6.16854028e-03, -4.86870315e-03, -3.93255701e-03,
                -3.21101913e-03, -2.62625601e-03, -2.13319626e-03, -1.70361035e-03,
                -1.31863336e-03, -9.64897616e-04, -6.32365317e-04, -3.13018799e-04,
                0.00000000e+00,  3.13018799e-04,  6.32365317e-04,  9.64897616e-04,
                1.31863336e-03,  1.70361035e-03,  2.13319626e-03,  2.62625601e-03,
                3.21101913e-03,  3.93255701e-03,  4.86870315e-03,  6.16854028e-03,
                8.16397345e-03,  1.18021428e-02,  2.16287744e-02, -4.88804573e-01};
        for (int i = 0; i < 32; i++) {
            Assert.assertEquals( actReal[i], w.get(i).getReal(), 1e-8);
            Assert.assertEquals(-actImag[i], w.get(i).getImag(), 1e-8);
        }
    }

    @Test
    public void testInPlaceFastFourierTransform1() {
        ComplexArray t = ComplexArray.linspace(-Math.PI, Math.PI, 32);
        ComplexArray z = Functions.sin(t);
        FourierTransform.inPlaceFastFourierTransform(z);
        double [] actReal = {    5.55111512e-16, -1.54057755e+00,  1.37671383e-01,  1.14564509e-01,
                1.08212113e-01,  1.05508911e-01,  1.04101230e-01,  1.03275716e-01,
                1.02752612e-01,  1.02403271e-01,  1.02161722e-01,  1.01991385e-01,
                1.01870801e-01,  1.01786917e-01,  1.01731684e-01,  1.01700263e-01,
                1.01690061e-01,  1.01700263e-01,  1.01731684e-01,  1.01786917e-01,
                1.01870801e-01,  1.01991385e-01,  1.02161722e-01,  1.02403271e-01,
                1.02752612e-01,  1.03275716e-01,  1.04101230e-01,  1.05508911e-01,
                1.08212113e-01,  1.14564509e-01,  1.37671383e-01, -1.54057755e+00};
        double [] actImag = {   0.00000000e+00,  1.56417463e+01, -6.92120780e-01, -3.77668571e-01,
                -2.61247151e-01, -1.97393289e-01, -1.55798501e-01, -1.25841824e-01,
                -1.02752612e-01, -8.40401922e-02, -6.82622804e-02, -5.45155311e-02,
                -4.21962675e-02, -3.08767237e-02, -2.02356901e-02, -1.00166016e-02,
                0.00000000e+00,  1.00166016e-02,  2.02356901e-02,  3.08767237e-02,
                4.21962675e-02,  5.45155311e-02,  6.82622804e-02,  8.40401922e-02,
                1.02752612e-01,  1.25841824e-01,  1.55798501e-01,  1.97393289e-01,
                2.61247151e-01,  3.77668571e-01,  6.92120780e-01, -1.56417463e+01};
        for (int i = 0; i < 32; i++) {
            Assert.assertEquals( actReal[i], z.get(i).getReal(), 1e-5);
            Assert.assertEquals(-actImag[i], z.get(i).getImag(), 1e-5);
        }
    }

    public static void main(String [] args) {
        Result result = JUnitCore.runClasses(TestFourierTransform.class);
        int failCount = 0;
        for (Failure failure: result.getFailures()) {
            failCount++;
            out.println(failure.toString());
        }
        out.printf("Passed %d/%d\n", (testCount - failCount), testCount);
    }
}