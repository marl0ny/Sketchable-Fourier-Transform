package side.project.sketchycircles;

import side.project.sketchycircles.complex.Complex;
import side.project.sketchycircles.complex.ComplexArray;
import side.project.sketchycircles.complex.Functions;
import side.project.sketchycircles.complex.FourierTransform;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.RectF;

class ComplexCircles {

    ComplexArray ftAmps;
    ComplexArray lineDrawn;
    ComplexArray original;

    double [] frequencies;

    int pointsPerInterval;
    Complex prev;
    Complex next;
    double freq = 0.0;

    int numberOfCirclePoints = 40;
    Complex circlePoint1;
    Complex circlePoint2;

    ComplexCircles(ComplexArray array) {
        pointsPerInterval = 4;
        lineDrawn = new ComplexArray();
        original = array.clone();
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
    void drawOriginal(Canvas canvas, Paint paint) {
        for (int i = 0; i < original.size() - 1; i++) {
            paint.setColor(Color.argb(
                    100, 100, 100, 100));
            canvas.drawLine(
                    (float)original.get(i).getReal(),
                    (float)original.get(i).getImag(),
                    (float)original.get(i + 1).getReal(),
                    (float)original.get(i + 1).getImag(),
                    paint
            );
        }
    }
    void drawCircle(Canvas canvas, Paint paint,
                    Complex center, double radius) {
        paint.setColor(Color.argb(
                255, 255, 190, 0));
        for (int k = 0;
             k <= numberOfCirclePoints; k++) {
            if (k == 0){
                circlePoint1 = new Complex(
                        center.getReal(),
                        center.getImag());
                circlePoint1.addBy(radius);
            }
            circlePoint2 = Functions.exp(0.0,
                    k*Complex.TAU/numberOfCirclePoints
            );
            circlePoint2.multiplyBy(radius);
            circlePoint2.addBy(
                    center.getReal(), center.getImag());
            canvas.drawLine(
                    (float)circlePoint1.getReal(),
                    (float)circlePoint1.getImag(),
                    (float)circlePoint2.getReal(),
                    (float)circlePoint2.getImag(),
                    paint
            );
            Complex.swapComplex(circlePoint1, circlePoint2);
        }
    }
    void updateOneCircle(Canvas canvas, Paint paint, int i) {
        freq = -frequencies[i] * Math.PI * 2.0 / (
                pointsPerInterval * ftAmps.size());
        ftAmps.get(i).multiplyBy(Functions.exp(0.0, freq));
        next = ftAmps.get(i).add(prev);
        canvas.drawLine((float)prev.getReal(), (float)prev.getImag(),
                (float)next.getReal(), (float)next.getImag(), paint);
        double radius = ftAmps.get(i).getAbs();
        paint.setColor(Color.argb(
                255, 255, 190, 0));
        canvas.drawCircle((float)prev.getReal(), (float)prev.getImag(), (float)radius, paint);
        //this.drawCircle(canvas, paint, prev, radius);
        paint.setColor(Color.WHITE);
        Complex.swapComplex(prev, next);
    }
    void update(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        this.drawOriginal(canvas, paint);
        paint.setColor(Color.WHITE);
        prev = new Complex(
                ftAmps.get(0).getReal(),
                ftAmps.get(0).getImag()
        );
        for (int i = 1, k =ftAmps.size() - 1;
             i < ftAmps.size()/2; i++, k--) {
            updateOneCircle(canvas, paint, i);
            updateOneCircle(canvas, paint, k);
        }
        if (ftAmps.size()%2 == 0) {
            updateOneCircle(canvas, paint,
                    ftAmps.size()/2);
        }
        paint.setColor(Color.WHITE);
        int n = lineDrawn.size();
        lineDrawn.add(prev.getReal(), prev.getImag());
        if (!(lineDrawn.isEmpty() || lineDrawn.size() == 1)) {
            for (int i = 0; i < lineDrawn.size() - 1; i++) {
                canvas.drawLine(
                        (float)lineDrawn.get(i).getReal(),
                        (float)lineDrawn.get(i).getImag(),
                        (float)lineDrawn.get(i + 1).getReal(),
                        (float)lineDrawn.get(i + 1).getImag(),
                        paint
                );
            }
        }
        if (lineDrawn.size()
                == pointsPerInterval*(ftAmps.size())) {
            lineDrawn = new ComplexArray();
        }
    }
}