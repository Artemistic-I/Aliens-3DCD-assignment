import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

//heavily modified
public class Alien_GLEventListener implements GLEventListener {
  
  private static final boolean DISPLAY_SHADERS = false;
  private Camera camera;
    
  public Alien_GLEventListener(Camera camera) {
    this.camera = camera;
    this.camera.setPosition(new Vec3(0f,2f,14f));
    this.camera.setTarget(new Vec3(0f,3f,0f));
  }
  
  // ***************************************************
  /*
   * METHODS DEFINED BY GLEventListener
   */

  /* Initialisation */
  public void init(GLAutoDrawable drawable) {   
    GL3 gl = drawable.getGL().getGL3();
    System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
    gl.glClearDepth(1.0f);
    gl.glEnable(GL.GL_DEPTH_TEST);
    gl.glDepthFunc(GL.GL_LESS);
    gl.glFrontFace(GL.GL_CCW);    // default is 'CCW'
    gl.glEnable(GL.GL_CULL_FACE); // default is 'not enabled' so needs to be enabled
    gl.glCullFace(GL.GL_BACK);   // default is 'back', assuming CCW
    initialise(gl);
    startTime1 = getSeconds(); //for spotlight
    startTime2 = getSeconds(); //for aliens
  }
  
  /* Called to indicate the drawing surface has been moved and/or resized  */
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL3 gl = drawable.getGL().getGL3();
    gl.glViewport(x, y, width, height);
    float aspect = (float)width/(float)height;
    camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
  }

  /* Draw */
  public void display(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    render(gl);
  }

  /* Clean up memory */
  public void dispose(GLAutoDrawable drawable) {
    GL3 gl = drawable.getGL().getGL3();
    room.dispose(gl);
    alien1.dispose(gl);
    alien2.dispose(gl);
    spotlight.dispose(gl);
    for (int i = 0; i < lights.length-1; i++) {
      lights[i].dispose(gl);
    }
  }

  // ***************************************************
  /* THE SCENE */
  private TextureLibrary sceneTextures, alien1Textures, alien2Textures;
  private AlienModel alien1, alien2;
  private Room room;
  private Light[] lights = new Light[3];
  private SpotlightModel spotlight;


  //falling snow image by Dillon Kydd is free to use under unsplash licence https://unsplash.com/photos/a-black-and-white-photo-of-snow-falling-7o7m1xCEiY8

  //all other images are in public domain under C0 taken from
  //https://polyhaven.com/textures
  //https://polyhaven.com/a/snowy_forest_path_01
  
  private void loadTextures(GL3 gl) {
    sceneTextures = new TextureLibrary();
    alien1Textures = new TextureLibrary();
    alien2Textures = new TextureLibrary();
    //alien 1
    alien1Textures.add(gl, "head spec", "textures/slabTiles_tex_spec.jpg");
    alien1Textures.add(gl, "head diff", "textures/slabTiles_tex_diff.jpg");
    alien1Textures.add(gl, "body diff", "textures/snow_tex_diff.png");
    alien1Textures.add(gl, "body spec", "textures/snow_tex_spec.png");
    alien1Textures.add(gl, "eye", "textures/eye.jpg");
    alien1Textures.add(gl, "arm", "textures/arm.jpg");
    alien1Textures.add(gl, "ear", "textures/brick.jpg");
    alien1Textures.add(gl, "ear spec", "textures/brick_spec.jpg");
    //alien 2
    alien2Textures.add(gl, "head spec", "textures/head spec.jpg");
    alien2Textures.add(gl, "head diff", "textures/head.jpg");
    alien2Textures.add(gl, "body diff", "textures/body2.jpg");
    alien2Textures.add(gl, "body spec", "textures/body2 spec.jpg");
    alien2Textures.add(gl, "eye", "textures/eye2.jpg");
    alien2Textures.add(gl, "arm", "textures/arm2.jpg");
    alien2Textures.add(gl, "ear", "textures/ear2.jpg");
    alien2Textures.add(gl, "ear spec", "textures/ear2 spec.jpg");
    //scene
    sceneTextures.add(gl, "snowyLeft", "textures/snowyLeft.png");
    sceneTextures.add(gl, "snowyCentre", "textures/snowyCentre.png");
    sceneTextures.add(gl, "snowyRight", "textures/snowyRight.png");
    sceneTextures.add(gl, "snowyFloor", "textures/snowyFloor.png");
    sceneTextures.add(gl, "snow3", "textures/snow3.jpg");
  }
  

  public void initialise(GL3 gl) {
    loadTextures(gl);

    lights[0] = new Light(gl);
    lights[0].setCamera(camera);
    lights[0].setPosition(getLight0Position());

    lights[1] = new Light(gl);
    lights[1].setCamera(camera);
    lights[1].setPosition(getLight1Position());

    float alienOffsetX = -2f;
    alien1 = new AlienModel(gl, camera, lights, alien1Textures, alienOffsetX);
    alienOffsetX = 2f;
    alien2 = new AlienModel(gl, camera, lights, alien2Textures, alienOffsetX);
    spotlight = new SpotlightModel(gl, camera, lights);

    room = new Room(gl, camera, lights, sceneTextures);
  }
  
  public void render(GL3 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

    lights[0].render(gl);
    lights[1].render(gl);
    
    double elapsedTime1 = getSeconds()-startTime1;
    double elapsedTime2 = getSeconds()-savedTime;
    alien1.render(gl, elapsedTime2);
    alien2.render(gl, elapsedTime2);
    spotlight.render(gl, elapsedTime1);
    room.render(gl);
  }
  
  private Vec3 getLight0Position() {
    //double elapsedTime = getSeconds()-startTime;
    float x = -2.0f;//8.0f*(float)(Math.sin(Math.toRadians(elapsedTime*50)));
    float y = 12.0f;//3.4f;
    float z = 2.0f;//5.0f*(float)(Math.cos(Math.toRadians(elapsedTime*50)));
    return new Vec3(x,y,z);
  }

  private Vec3 getLight1Position() {
    //double elapsedTime = getSeconds()-startTime;
    float x = 0.0f;//8.0f*(float)(Math.sin(Math.toRadians(elapsedTime*80)));
    float y = 10.0f;//7.4f;
    float z = 12.0f;//3.0f*(float)(Math.cos(Math.toRadians(elapsedTime*80)));
    return new Vec3(x,y,z);
  }

    // ***************************************************
  /* TIME
   */
  private double startTime1;
  private double startTime2;
  private double savedTime = 0;
  
  private double getSeconds() {
    return System.currentTimeMillis()/1000.0;
  }
  
  public void switchGlobalLight1() {
    lights[0].turnOnOff();
  }
  public void switchGlobalLight2() {
    lights[1].turnOnOff();
  }
  public void switchSpotlight() {
    lights[2].turnOnOff();
  }
  public void startAnimation() {
    startTime2 = getSeconds()-savedTime;
    alien1.startAnimation();
    alien2.startAnimation();
  }
  public void stopAnimation() {
    alien1.stopAnimation();
    alien2.stopAnimation();
    double elapsedTime = getSeconds()-startTime2;
    savedTime = elapsedTime;
  }
  public void resetAliens() {
    alien1.resetAlien();
    alien2.resetAlien();
  }
  public void rockOnlyAnimation() {
    alien1.rockOnly();
    alien2.rockOnly();
  }
  public void rollOnlyAnimation() {
    alien1.rollOnly();
    alien2.rollOnly();
  }
}