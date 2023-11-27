import gmaths.*;

import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

//modified
public class Room {

  private ModelMultipleLights[] wall;
  private Camera camera;
  private Light[] lights;
  private Texture t0, t1, t2, t3, t4;
  private float size = 16f;

  public Room(GL3 gl, Camera c, Light[] l, TextureLibrary textures) {
    camera = c;
    lights = l;
    this.t0 = textures.get("snowyFloor");
    this.t1 = textures.get("snowyCentre");
    this.t2 = textures.get("snowyLeft");
    this.t3 = textures.get("snowyRight");

    this.t4 = textures.get("snow3");
    wall = new ModelMultipleLights[4];
    wall[0] = makeWall0(gl);
    wall[1] = makeWall1(gl);
    wall[2] = makeWall2(gl);
    wall[3] = makeWall3(gl);
  }

 
  private ModelMultipleLights makeWall0(GL3 gl) {
    String name="floor";
    Vec3 basecolor = new Vec3(0.5f, 0.5f, 0.5f);
    Material material = new Material(basecolor, basecolor, new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(2.5f*size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(1,0,0), modelMatrix);
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_ms_1t.txt");
    ModelMultipleLights model = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, camera, t0);
    return model;
  }

  private ModelMultipleLights makeWall1(GL3 gl) {
    String name="wall";
    Vec3 basecolor = new Vec3(0.5f, 0.5f, 0.5f);
    Material material = new Material(basecolor, basecolor, new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(0,size*0.5f,-size*0.5f), modelMatrix);
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_animated.txt", "shaders/fs_animated_2t.txt");
    ModelMultipleLights model = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, camera, t1, t4);
    return model;
  }

  private ModelMultipleLights makeWall2(GL3 gl) {
    String name="wall";
    Material material = new Material(new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(-45), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(-1.705f*size*0.5f,size*0.5f,-0.147f*size), modelMatrix);
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_animated.txt", "shaders/fs_animated_2t.txt");
    ModelMultipleLights model = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, camera, t2, t4);
    return model;
  }
  private ModelMultipleLights makeWall3(GL3 gl) {
    String name="wall";
    Material material = new Material(new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.1f, 0.5f, 0.91f), new Vec3(0.3f, 0.3f, 0.3f), 4.0f);
    Mat4 modelMatrix = new Mat4(1);
    modelMatrix = Mat4.multiply(Mat4Transform.scale(size,1f,size), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(-90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(90), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(45), modelMatrix);
    modelMatrix = Mat4.multiply(Mat4Transform.translate(1.705f*size*0.5f, size*0.5f,-0.147f*size), modelMatrix);
    Mesh mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
    Shader shader = new Shader(gl, "shaders/vs_animated.txt", "shaders/fs_animated_2t.txt");
    ModelMultipleLights model = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, camera, t3, t4);
    return model;
  }

  public void render(GL3 gl) {
    for (int i=0; i<wall.length; i++) {
       wall[i].render(gl);
    }
    
    //test
    //wall[2].render(gl);
  }

  public void dispose(GL3 gl) {
    for (int i=0; i<wall.length; i++) {
      wall[i].dispose(gl);
    }
  }
}