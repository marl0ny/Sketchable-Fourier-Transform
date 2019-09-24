package side.project.sketchycircles;

import side.project.sketchycircles.complex.Complex;
import side.project.sketchycircles.complex.ComplexArray;

import android.app.Activity;
import android.os.Bundle;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.content.Context;
import android.util.DisplayMetrics;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity
        implements
        View.OnTouchListener {

    public static int WIDTH = 750;
    public static int HEIGHT = 1500;

    ComplexArray sketchArray;
    enum SketchState {
        START, CLEAR_START,
        SKETCH, CLEAR_SKETCH,
        ANIMATION, CLEAR_ANIMATION;
    }
    SketchState sketchState;

    Canvas canvas;
    Paint paint;
    ImageView imageView;
    Bitmap bitmap;
    long t = 0;

    Animator animator;
    Timer animatorTimer = new Timer();

    public class Animator extends TimerTask{

        public class SurfaceViewAnimator extends SurfaceView {

            SurfaceHolder holder;
            ComplexCircles circles;

            SurfaceViewAnimator(Context context, ComplexArray array) {
                super(context);
                super.setZOrderOnTop(true);
                super.setBackgroundColor(Color.BLACK);
                holder = this.getHolder();
                circles = new ComplexCircles(array);
            }

            void hide() {
                super.setVisibility(View.GONE);
                super.setZOrderOnTop(false);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                this.circles.update(canvas, paint);
                super.onDraw(canvas);
            }
        }
        SurfaceViewAnimator animator;
        Animator(Context context, ComplexArray array) {
            animator = new SurfaceViewAnimator(context, array);
        }

        @Override
        public void run() {
            if(animator.holder.getSurface().isValid()) {
                Canvas c = animator.holder.lockCanvas();
                if (c != null) {
                    this.animator.draw(c);
                    animator.holder.unlockCanvasAndPost(c);
                }
            }
        }
        public void hide() {
            this.animator.hide();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        The following StackOverflow response shows
        how to find the screen dimensions:

        https://stackoverflow.com/a/4744499
        [answer by Parag Chauhan]

        https://stackoverflow.com/q/4743116
        [question by rebel_UA]
        */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        WIDTH = displayMetrics.widthPixels;
        HEIGHT = displayMetrics.heightPixels;

        sketchArray = new ComplexArray();
        sketchState = SketchState.START;

        bitmap = Bitmap.createBitmap(
                WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        imageView = new ImageView(this);
        this.addContentView(imageView,
                new LayoutParams(WIDTH, HEIGHT));
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        canvas.drawColor(Color.BLACK);
        canvas.drawText("Touch Here!",
                WIDTH/2, HEIGHT/2, paint);
        imageView.setImageBitmap(bitmap);

    }
    @Override
    public boolean onTouchEvent(MotionEvent m) {
        if (m.getAction() == MotionEvent.ACTION_DOWN){
            this.sketchState = (
                    this.sketchState == SketchState.START) ?
                    SketchState.CLEAR_START : SketchState.CLEAR_ANIMATION;
        }
        else if (m.getAction() == MotionEvent.ACTION_UP) {
            this.sketchState = SketchState.ANIMATION;
        }
        double x = m.getX();
        double y = m.getY();
        sketchArray.add(x, y);
        this.paintFunc();
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent m) {
        if (m.getAction() == MotionEvent.ACTION_DOWN){
            this.sketchState = (
                    this.sketchState == SketchState.START) ?
                    SketchState.CLEAR_START : SketchState.CLEAR_ANIMATION;
        }
        else if (m.getAction() == MotionEvent.ACTION_UP) {
            this.sketchState = SketchState.ANIMATION;
        }
        double x = m.getX();
        double y = m.getY();
        sketchArray.add(x, y);
        this.paintFunc();
        return false;
    }

    protected void paintFunc() {
        switch(sketchState) {
            case CLEAR_START:
                canvas.drawColor(Color.BLACK);
                this.sketchState = SketchState.SKETCH;
                break;
            case SKETCH:
                paint.setColor(Color.WHITE);
                this.showSketch();
                imageView.setImageBitmap(bitmap);
                break;
            case ANIMATION:
                this.animator = new Animator(
                        this, this.sketchArray);
                this.addContentView(
                        this.animator.animator,
                        new LayoutParams(WIDTH, HEIGHT));
                this.animatorTimer.scheduleAtFixedRate(
                        this.animator, 0, 15);
                break;
            case CLEAR_ANIMATION:
                //Always call cancel first!
                this.animator.cancel();
                this.animator.hide();
                this.animator = null;
                this.sketchArray = new ComplexArray();
                canvas.drawColor(Color.BLACK);
                this.sketchState = SketchState.SKETCH;
                break;
        }
    }
    void showSketch() {
        if (this.sketchArray.size() >= 2) {
            Complex p0 = this.sketchArray.get(
                    this.sketchArray.size() - 1);
            Complex p1 = this.sketchArray.get(
                    this.sketchArray.size() - 2);
            canvas.drawLine((float)p0.getReal(), (float)p0.getImag(),
                    (float)p1.getReal(), (float)p1.getImag(), paint);
        }
    }

}