import side.project.complex.Complex;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;

import static java.lang.System.out;

public class TestComplex {

    public static int testCount = 0;

    @Before
    public void before() {
        testCount++;
    }

    @Test
    public void toStringTest() {
        Complex z1 = new Complex ();
        Assert.assertEquals("0", z1.toString());
        Complex z2 = new Complex (1.0, 2.0);
        Assert.assertEquals("1.0 + 2.0i", z2.toString());
        Complex z3 = new Complex (-1, 2);
        Assert.assertEquals("-1.0 + 2.0i", z3.toString());
        Complex z4 = new Complex (0, 2);
        Assert.assertEquals("2.0i", z4.toString());
        Complex z5 = new Complex (-1, -2);
        Assert.assertEquals("-1.0 - 2.0i", z5.toString());
        Complex z6 = new Complex (-1);
        Assert.assertEquals("-1.0", z6.toString());
        Complex z7 = new Complex (-1, 2);
        Assert.assertEquals("-1.0 + 2.0i", z7.toString());
        Complex z8 = new Complex (0, -2);
        Assert.assertEquals("-2.0i", z8.toString());
    }

    @Test
    public void conjTest() {
        Complex u = new Complex(1, -1);
        u.conjugate();
        Assert.assertEquals("Conjugate of 1 - 1i:",
                "1.0 + 1.0i", u.toString());
        Complex v = new Complex(10, 151);
        Assert.assertEquals("Conjugate of 10 + 151i:",
                "10.0 - 151.0i", v.getConj().toString());
    }

    @Test
    public void inverseTest() {
        Complex u = new Complex(2, 2);
        Assert.assertEquals("Inverse of 2 + 2i:",
                "0.25 - 0.25i",
                u.getInv().toString());
        Assert.assertEquals("1.0",
                u.multiply(u.getInv()).toString());
        Complex v = new Complex(4.0, 3.0);
        v.invert();
        Assert.assertEquals("Inverse of 4 + 3i:",
                "0.16 - 0.12i", v.toString());
    }

    @Test
    public void multiplyTest() {
        Complex u = new Complex(1.0, 7.0);
        Assert.assertEquals("Evaluate 4(1 + 7i):",
                "4.0 + 28.0i",
                u.multiply(4.0).toString());
        Complex v = new Complex(1.0, 7.0);
        Assert.assertEquals("Evaluate (1 + 7i)(1 + 7i):",
                "50.0",
                v.multiply(1.0, -7.0).toString());
    }

    @Test
    public void multiplyByTest() {
        Complex u = new Complex(1.0, 7.0);
        u.multiplyBy(4.0);
        Assert.assertEquals("Evaluate 4(1 + 7i):",
                "4.0 + 28.0i", u.toString());
        Complex v = new Complex(1.0, -7.0);
        v.multiplyBy(1.0, 7.0);
        Assert.assertEquals("Evaluate (1 + 7i)(1 - 7i):",
                "50.0", v.toString());
        Complex w = new Complex(1/50.0);
        v.multiplyBy(w);
        Assert.assertEquals("Evaluate (1 + 7i)(1 - 7i)/50:",
                "1.0", v.toString());

    }

    @Test
    public void divideTest() {
        Complex u = new Complex (1.0, 7.0);
        Assert.assertEquals(
                "Result of the division of 1 + 7i with  1 + 7i:",
                "1.0", u.divide(u).toString());
        Complex v2 = u.divide(0.0, 7.0);
        Assert.assertEquals(
                "Result of the division of 1 + 7i with  7i (real part):",
                1.0, v2.getReal(), 1e-30);
        Assert.assertEquals(
                "Result of the division of 1 + 7i with  7i (imaginary part):",
                -0.142857142857142857142857142, v2.getImag(), 1e-30);
    }

    @Test
    public void divideByTest() {
        Complex u = new Complex (1.0, 7.0);
        u.divideBy(u);
        Assert.assertEquals("Result of the division of 1 + 7i with  1 + 7i:",
                "1.0", u.toString());
        Complex u2 = new Complex (10, 10);
        u2.divideBy(10.0);
        Assert.assertEquals("Result of the division of 10 + 10i with  10:",
                "1.0 + 1.0i", u2.toString());
        Complex v1 = new Complex (1.0, 1.0);
        v1.divideBy(v1);
        Assert.assertEquals("Result of the division of 1 + 7i with  1 + 7i:",
                "1.0", v1.toString());
        Complex v2 = new Complex (1.0, 7.0);
        v2.divideBy(0.0, 7.0);
        Assert.assertEquals(
                "Result of the division of 1 + 7i with  7i (real part):",
                1.0, v2.getReal(), 1e-30);
        Assert.assertEquals(
                "Result of the division of 1 + 7i with  7i (imaginary part):",
                -0.142857142857142857142857142, v2.getImag(), 1e-30);
    }

    @Test
    public void addTest() {
        Complex u = new Complex(1.0, 7.0);
        Assert.assertEquals("Result of adding 1 + 7i with 4:",
                "5.0 + 7.0i", u.add(4.0, 0.0).toString());
        Complex x1 = new Complex(1.0, 1.0);
        Complex x2 = new Complex(2.0, -3.0);
        Assert.assertEquals("Result of adding 1 + 1i and 2 - 3i:",
                "3.0 - 2.0i", x2.add(x1).toString());
    }

    @Test
    public void addByTest() {
        Complex u = new Complex(1.0, 7.0);
        u.addBy(4.0);
        Assert.assertEquals("Result of adding 1 + 7i with 4:",
                "5.0 + 7.0i", u.toString());
        u.addBy(u);
        Assert.assertEquals("Result of adding 5 + 7i with 5 + 7i:",
                "10.0 + 14.0i", u.toString());
        Complex x1 = new Complex(1.0, 1.0);
        x1.addBy(2.0, -3.0);
        Assert.assertEquals("Result of adding 1 + 1i and 2 - 3i:",
                "3.0 - 2.0i", x1.toString());
    }

    @Test
    public void subTest() {
        Complex u = new Complex(4, 12);
        Assert.assertEquals(
                "Result of subtracting 4 + 12i by 2 - 7i",
                "2.0 + 19.0i",
                u.subtract(2.0, -7.0).toString());
        Complex v = new Complex(4, 12);
        Assert.assertEquals("Result of subtracting 4 + 12i by 7i",
                "4.0 + 5.0i",
                v.subtract(new Complex(0.0, 7.0)).toString());
    }

    @Test
    public void subByTest() {
        Complex u = new Complex(4, 12);
        u.subtractBy(2.0, -7.0);
        Assert.assertEquals(
                "Result of subtracting 4 + 12i by 2 - 7i",
                "2.0 + 19.0i", u.toString());
        Complex v = new Complex(4, 12);
        v.subtractBy(new Complex(0.0, 7.0));
        Assert.assertEquals("Result of subtracting 4 + 12i by 7i",
                "4.0 + 5.0i", v.toString());
        Complex w = new Complex(12, 11);
        w.subtractBy(10);
        Assert.assertEquals(
                "Result of subtracting 12 + 11i by 10",
                "2.0 + 11.0i",
                w.toString()
        );
    }

    public static void main(String [] args) {
        Result result = JUnitCore.runClasses(TestComplex.class);
        int failCount = 0;
        for (Failure failure: result.getFailures()) {
            failCount++;
            out.println(failure.toString());
        }
        out.printf("Passed %d/%d\n", (testCount - failCount), testCount);
    }

}