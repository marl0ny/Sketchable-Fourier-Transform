package side.project.gui;

import side.project.complex.Complex;
import side.project.complex.ComplexArray;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Canvas extends JPanel implements MouseListener, KeyListener {

    ComplexArray sketchArray;
    enum SketchState {
        START, CLEAR_START,
        SKETCH, CLEAR_SKETCH,
        ANIMATION, CLEAR_ANIMATION;
    }
    SketchState sketchState;
    int width, height;
    ComplexCircles circles;

    public Canvas(int WIDTH, int HEIGHT) {
        super();
        this.sketchState = SketchState.START;
        this.sketchArray = new ComplexArray();
        this.width = WIDTH;
        this.height = HEIGHT;
		this.setBackground(Color.DARK_GRAY);
        this.setVisible(true);
    }
    void getMouseLocation() {
        Point mouseLocation
                = MouseInfo.getPointerInfo().getLocation();
        Point canvasLocation
                = this.getLocationOnScreen();
        double x
                = mouseLocation.getX() - canvasLocation.getX();
        double y
                = mouseLocation.getY() - canvasLocation.getY();
        this.sketchArray.add(new Complex(x, y));
    }
    void showSketch(Graphics g) {
        if (this.sketchArray.size() >= 2) {
            Complex p0 = this.sketchArray.get(
                    this.sketchArray.size() - 1);
            Complex p1 = this.sketchArray.get(
                    this.sketchArray.size() - 2);
            g.drawLine((int)p0.getReal(), (int)p0.getImag(),
                    (int)p1.getReal(), (int)p1.getImag());
        }
    }
	void clear(Graphics g){
		Color color = g.getColor();
		g.setColor(this.getBackground());
		g.fillRect(0, 0, this.width, this.height);
		g.setColor(color);
	}
    @Override
    protected void paintComponent(Graphics g) {
        // out.println(sketchState);
        switch(sketchState) {
            case START:
                g.setColor(Color.WHITE);
                g.drawString("Sketch Here",
                        this.width/2, this.height/2);
                this.sketchState = SketchState.START;
                break;
            case CLEAR_START:
			    this.clear(g);
                this.sketchState = SketchState.SKETCH;
                break;
            case SKETCH:
                g.setColor(Color.WHITE);
                this.getMouseLocation();
                this.showSketch(g);
                break;
            case CLEAR_SKETCH:
                this.circles = new ComplexCircles(this.sketchArray);
				this.clear(g);
                this.sketchState = SketchState.ANIMATION;
                break;
            case ANIMATION:
                this.circles.update(g);
                doNothing(10);
                this.clear(g);
                this.circles.update(g);
                Toolkit.getDefaultToolkit().sync();
                break;
            case CLEAR_ANIMATION:
                this.circles = null;
                this.sketchArray = new ComplexArray();
                this.clear(g);
                this.sketchState = SketchState.SKETCH;
                break;
        }
        doNothing(5);
    }
    void doNothing(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {

        }
    }
    void setWidth(int width) { this.width = width; }
    void setHeight(int height) { this.height = height; }
    @Override
    public void keyTyped(KeyEvent keyEvent) { }
    @Override
    public void keyPressed(KeyEvent keyEvent) { }
    @Override
    public void keyReleased(KeyEvent keyEvent) { }
    @Override
    public void mouseClicked(MouseEvent mouseEvent) { }
    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == 1) {
            this.sketchState = (this.sketchState == SketchState.START) ?
                    SketchState.CLEAR_START : SketchState.CLEAR_ANIMATION;
        }
    }
    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == 1) {
            this.sketchState = SketchState.CLEAR_SKETCH;
        }
    }
    @Override
    public void mouseEntered(MouseEvent mouseEvent) { }
    @Override
    public void mouseExited(MouseEvent mouseEvent) { }
}

public class SwingFrame extends JFrame
        implements WindowListener, ComponentListener {

    int width = 900, height = 600;
    boolean running =  true;
    Canvas canvas;

    public SwingFrame () {
        canvas = new Canvas(width, height);
        this.add(canvas);
        this.setBackground(Color.DARK_GRAY);
        this.addMouseListener(canvas);
        this.addKeyListener(canvas);
        this.addWindowListener(this);
        this.addComponentListener(this);
        this.setSize(width, height);
        this.setVisible(true);

    }

    public void run() {
        while(running) {
            this.canvas.repaint();
        }
    }

    public static void main(String [] args) {
        SwingFrame gui = new SwingFrame();
        gui.run();
    }

    @Override
    public void windowClosing(WindowEvent windowEvent) {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent windowEvent) {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent windowEvent) { }
    @Override
    public void windowIconified(WindowEvent windowEvent) {
        // this.running = false;
    }
    @Override
    public void windowDeiconified(WindowEvent windowEvent) {
        // this.running = true;
    }
    @Override
    public void windowActivated(WindowEvent windowEvent) { }
    @Override
    public void windowDeactivated(WindowEvent windowEvent) { }
    @Override
    public void componentResized(ComponentEvent componentEvent) {
        this.height = this.getHeight();
        this.width = this.getWidth();
        this.canvas.setWidth(this.width);
        this.canvas.setHeight(this.height);
    }
    @Override
    public void componentMoved(ComponentEvent componentEvent) { }
    @Override
    public void componentShown(ComponentEvent componentEvent) { }
    @Override
    public void componentHidden(ComponentEvent componentEvent) { }
}