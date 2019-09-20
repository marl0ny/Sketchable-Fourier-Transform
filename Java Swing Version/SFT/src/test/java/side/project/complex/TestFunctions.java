import side.project.complex.Complex;
import side.project.complex.Functions;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;

import static java.lang.System.out;

public class TestFunctions {

    public static int testCount = 0;

    @Before
    public void before() {
        testCount++;
    }

    @Test
    public void expTest() {
        Complex z1 = new Complex(1.0, Math.PI);
        Complex z2 = Functions.exp(z1);
        Assert.assertEquals("Test Re(exp{i pi})",
                Functions.exp(0.0, Math.PI).getReal(),
                -1.0, 1e-50);
        Assert.assertEquals("Test Im(exp{i pi})",
                Functions.exp(0.0, Math.PI).getImag(),
                0.0, 1e-15);
        Assert.assertEquals("Test Re(exp{1 + i pi})",
                z2.getReal(), -Math.exp(1), 1e-50);
        Assert.assertEquals("Test Im(exp{1 + i pi})",
                z2.getImag(), 0.0, 1e-15);
    }

    @Test
    public void sinTest() {
        Complex z1 = new Complex(200*Math.PI, 0.0);
        Assert.assertEquals("Test Re(sin(200 pi)",
                0.0, Functions.sin(z1).getReal(), 1e-14);
        Assert.assertEquals("Test Im(sin(200 pi)",
                0.0, Functions.sin(z1).getImag(), 1e-14);
        Complex z2 = new Complex(133.0, 9.0);
        Assert.assertEquals("Test Re(sin(133.0 + 9.0i))",
                3520.6512800159285, Functions.sin(z2).getReal(), 1e-14);
        Assert.assertEquals("Test Im(sin(133.0 + 9.0i))",
                2004.9955371412484, Functions.sin(z2).getImag(), 1e-11);

    }

    @Test
    public void cosTest() {
        Complex z1 = new Complex(200*Math.PI, 0.0);
        Assert.assertEquals("Test Re(cos(200 pi)",
                1.0, Functions.cos(z1).getReal(), 1e-14);
        Assert.assertEquals("Test Im(cos(200 pi)",
                0.0, Functions.cos(z1).getImag(), 1e-14);
        Complex z2 = new Complex(133.0, 9.0);
        Assert.assertEquals("Test Re(cos(133.0 + 9.0i))",
                2004.9955982133324, Functions.cos(z2).getReal(), 1e-14);
        Assert.assertEquals("Test Im(cos(133.0 + 9.0i))",
                -3520.6512800159285, Functions.cos(z2).getImag(), 1e-3);

    }

    public static void main(String [] args) {
        Result result = JUnitCore.runClasses(TestFunctions.class);
        int failCount = 0;
        for (Failure failure: result.getFailures()) {
            failCount++;
            out.println(failure.toString());
        }
        out.printf("Passed %d/%d\n", (testCount - failCount), testCount);
    }
}