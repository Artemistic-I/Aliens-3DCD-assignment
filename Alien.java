import gmaths.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

//modified L05 from ch8
public class Alien extends JFrame implements ActionListener {
  
  private static final int WIDTH = 1024;
  private static final int HEIGHT = 768;
  private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
  private GLCanvas canvas;
  private Alien_GLEventListener glEventListener;
  private final FPSAnimator animator; 

  public static void main(String[] args) {
    Alien b1 = new Alien("Alien");
    b1.getContentPane().setPreferredSize(dimension);
    b1.pack();
    b1.setVisible(true);
    b1.canvas.requestFocusInWindow();
  }

  public Alien(String textForTitleBar) {
    super(textForTitleBar);
    setUpCanvas();
    getContentPane().add(canvas, BorderLayout.CENTER);
    addWindowListener(new windowHandler());

    JPanel p = new JPanel();
      JButton b = new JButton("Global light 1");
      b.addActionListener(this);
      p.add(b);

      b = new JButton("Global light 2");
      b.addActionListener(this);
      p.add(b);

      b = new JButton("Spotlight");
      b.addActionListener(this);
      p.add(b);

      b = new JButton("Start alien animation");
      b.addActionListener(this);
      p.add(b);

      b = new JButton("Stop alien animation");
      b.addActionListener(this);
      p.add(b);

      b = new JButton("rock");
      b.addActionListener(this);
      p.add(b);

      b = new JButton("roll");
      b.addActionListener(this);
      p.add(b);
    this.add(p, BorderLayout.SOUTH);

    animator = new FPSAnimator(canvas, 60);
    animator.start();
  }
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equalsIgnoreCase("global light 1")) {
      glEventListener.switchGlobalLight1();
    }
    else if (e.getActionCommand().equalsIgnoreCase("global light 2")) {
       glEventListener.switchGlobalLight2();
    }
    else if (e.getActionCommand().equalsIgnoreCase("spotlight")) {
      glEventListener.switchSpotlight();
    }
    else if (e.getActionCommand().equalsIgnoreCase("start alien animation")) {
      glEventListener.startAnimation();
    }
    else if (e.getActionCommand().equalsIgnoreCase("stop alien animation")) {
      glEventListener.stopAnimation();
    }
    else if (e.getActionCommand().equalsIgnoreCase("rock")) {
      glEventListener.rockOnlyAnimation();
    }
    else if (e.getActionCommand().equalsIgnoreCase("roll")) {
      glEventListener.rollOnlyAnimation();
    }
  }

  private void setUpCanvas() {
    GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
    canvas = new GLCanvas(glcapabilities);
    Camera camera = new Camera(Camera.DEFAULT_POSITION,
        Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
    glEventListener = new Alien_GLEventListener(camera);
    canvas.addGLEventListener(glEventListener);
    canvas.addMouseMotionListener(new MyMouseInput(camera));
    canvas.addKeyListener(new MyKeyboardInput(camera));
  }
//-----------------------------------------
  private class windowHandler extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
      animator.stop();
      remove(canvas);
      dispose();
      System.exit(0);
    }
  }
}

class MyKeyboardInput extends KeyAdapter  {
  private Camera camera;
  
  public MyKeyboardInput(Camera camera) {
    this.camera = camera;
  }
  
  public void keyPressed(KeyEvent e) {
    Camera.Movement m = Camera.Movement.NO_MOVEMENT;
    switch (e.getKeyCode()) {
      case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
      case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
      case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
      case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
      case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
      case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
    }
    camera.keyboardInput(m);
  }
}

class MyMouseInput extends MouseMotionAdapter {
  private Point lastpoint;
  private Camera camera;
  
  public MyMouseInput(Camera camera) {
    this.camera = camera;
  }
  
    /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */    
  public void mouseDragged(MouseEvent e) {
    Point ms = e.getPoint();
    float sensitivity = 0.001f;
    float dx=(float) (ms.x-lastpoint.x)*sensitivity;
    float dy=(float) (ms.y-lastpoint.y)*sensitivity;
    //System.out.println("dy,dy: "+dx+","+dy);
    if (e.getModifiersEx()==MouseEvent.BUTTON1_DOWN_MASK)
      camera.updateYawPitch(dx, -dy);
    lastpoint = ms;
  }

  /**
   * mouse is used to control camera position
   *
   * @param e  instance of MouseEvent
   */  
  public void mouseMoved(MouseEvent e) {   
    lastpoint = e.getPoint(); 
  }

}