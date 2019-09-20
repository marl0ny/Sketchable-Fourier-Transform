package side.project.gui;

import side.project.complex.Complex;
import side.project.complex.ComplexArray;
import side.project.complex.Functions;
import side.project.complex.FourierTransform;

import java.awt.*;

class ComplexCircles {
    ComplexArray ftAmps;
    ComplexArray lineDrawn;
    ComplexArray original;
    double [] frequencies;
    int pointsPerInterval;
    Complex prev;
    Complex next;
    double freq = 0.0;
    ComplexCircles(ComplexArray array) {
        pointsPerInterval = 4;
        lineDrawn = new ComplexArray();
        original = array.clone();
        //original = array;
        this.alleviateGibbs(array);
        ftAmps = FourierTransform.fourierTransform(array);
        frequencies =
                FourierTransform.fourierFrequencies(
                        array.size());
    }
    void alleviateGibbs(ComplexArray array) {
        Complex distance
                = array.get(0).subtract(array.get(array.size()-1));
        double d = distance.getAbs();
        int l = (int)(d*10/300);
        for (int i = 1; i < l; i++){
            array.add(array.get(array.size()-1).getReal()
                            + distance.getReal()/l,
                    array.get(array.size()-1).getImag()
                            + distance.getImag()/l
            );
        }
    }
    void drawOriginal(Graphics g) {
        for (int i = 0; i < original.size() - 1; i++) {
            g.drawLine(
                    (int)original.get(i).getReal(),
                    (int)original.get(i).getImag(),
                    (int)original.get(i + 1).getReal(),
                    (int)original.get(i + 1).getImag()
            );
        }
    }
    void updateOneCircle(Graphics g, int i) {
        freq = -frequencies[i] * Math.PI * 2.0 / (
                pointsPerInterval * ftAmps.size());
        ftAmps.get(i).multiplyBy(Functions.exp(0.0, freq));
        next = ftAmps.get(i).add(prev);
        g.drawLine((int)prev.getReal(), (int)prev.getImag(),
                (int)next.getReal(), (int)next.getImag());
        double radius = ftAmps.get(i).getAbs();
        g.setColor(Color.ORANGE);
        g.drawOval(
                (int)(prev.getReal() - radius),
                (int)(prev.getImag() - radius),
                2*(int)radius,
                2*(int)radius
        );
        g.setColor(Color.WHITE);
        Complex.swapComplex(prev, next);
    }
    void update(Graphics g) {
        g.setColor(Color.GRAY);
        this.drawOriginal(g);
        g.setColor(Color.WHITE);
        prev = new Complex(
                ftAmps.get(0).getReal(),
                ftAmps.get(0).getImag()
        );
        for (int i = 1, k =ftAmps.size() - 1;
             i < ftAmps.size()/2; i++, k--) {
            updateOneCircle(g, i);
            updateOneCircle(g, k);
        }
        if (ftAmps.size()%2 == 0) {
            updateOneCircle(g, ftAmps.size()/2);
        }
        g.setColor(Color.WHITE);
        int n = lineDrawn.size();
        lineDrawn.add(prev.getReal(), prev.getImag());
        if (!(lineDrawn.isEmpty() || lineDrawn.size() == 1)) {
            for (int i = 0; i < lineDrawn.size() - 1; i++) {
                g.drawLine(
                        (int)lineDrawn.get(i).getReal(),
                        (int)lineDrawn.get(i).getImag(),
                        (int)lineDrawn.get(i + 1).getReal(),
                        (int)lineDrawn.get(i + 1).getImag()
                );
            }
        }
        if (lineDrawn.size()
                == pointsPerInterval*(ftAmps.size())) {
            lineDrawn = new ComplexArray();
        }
    }
}