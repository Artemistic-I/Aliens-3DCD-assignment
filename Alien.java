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

public class Alien {
    private Camera camera;
    private Light[] lights;
    private ModelMultipleLights sphere;
    private Texture t0;

    private SGNode alienRoot;

    private float rotateAllAngleStart = 25;
    private float rotateAllAngle = rotateAllAngleStart;

    private float rotateUpperAngleStart = -60;
    private float rotateUpperAngle = rotateUpperAngleStart;

    private TransformNode rotateAll;

    public Alien(GL3 gl, Camera cameraIn, Light[] lightIn, Texture t0) {
        this.camera = cameraIn;
        this.lights = lightIn;
        this.t0 = t0;
        sphere = makeSphere(gl);
        alienRoot = new NameNode("root");
        float offsetX = 0.0f;

        //Creating nodes
        SGNode lowerBranch = makeLowerBranch(sphere);
        TransformNode translateToRightSide = new TransformNode("translateToRightSide",Mat4Transform.translate(0.35f,-0.11f,0));
        float armAngle = -45f;
        SGNode rightArm = makeArm(sphere, armAngle);
        TransformNode translateToLeftSide = new TransformNode("translateToLeftSide",Mat4Transform.translate(-0.35f,-0.11f,0));
        armAngle = 45;
        SGNode leftArm = makeArm(sphere, armAngle);
        
        TransformNode translateX = new TransformNode("translateX", Mat4Transform.translate(offsetX,0,0)); 

        //Building the scene graph
        alienRoot.addChild(translateX);
         translateX.addChild(lowerBranch);
          lowerBranchT.addChild(translateToRightSide);
           translateToRightSide.addChild(rightArm);
          lowerBranchT.addChild(translateToLeftSide);
           translateToLeftSide.addChild(leftArm);

        alienRoot.update();
    }
    private TransformNode lowerBranchT;

    private SGNode makeLowerBranch(ModelMultipleLights sphere) {
        NameNode lowerBranchName = new NameNode("lower branch");
        Mat4 m = Mat4Transform.scale(3,3,3);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(rotateAllAngle));
        //rotateAll = new TransformNode("rotateAll", Mat4Transform.rotateAroundZ(rotateAllAngle));
        lowerBranchT = new TransformNode("lower branch transform", m);
        ModelNode sphereNode = new ModelNode("Sphere(0)", sphere);
        lowerBranchName.addChild(lowerBranchT);
         lowerBranchT.addChild(sphereNode);
        return lowerBranchName;
    }
    private SGNode makeArm(ModelMultipleLights sphere, float armAngle) {
        NameNode upperBranchName = new NameNode("upper branch");
        Mat4 m = Mat4Transform.scale(0.08f,0.6f,0.08f);
        m = Mat4.multiply(Mat4Transform.translate(0.0f,0.5f,0), m);
        m = Mat4.multiply(Mat4Transform.rotateAroundZ(armAngle), m);
        
        TransformNode upperBranch = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m);
        ModelNode sphereNode = new ModelNode("Sphere(1)", sphere);
        upperBranchName.addChild(upperBranch);
          upperBranch.addChild(sphereNode);
        return upperBranchName;
    }
    // private SGNode makeLeftArm(ModelMultipleLights sphere) {
    //     NameNode upperBranchName = new NameNode("upper branch");
    //     Mat4 m = Mat4Transform.scale(0.08f,0.6f,0.08f);
    //     m = Mat4.multiply(Mat4Transform.translate(0.0f,0.5f,0), m);
    //     m = Mat4.multiply(Mat4Transform.rotateAroundZ(45), m);
        
    //     TransformNode upperBranch = new TransformNode("scale(1.4f,3.1f,1.4f);translate(0,0.5,0)", m);
    //     ModelNode sphereNode = new ModelNode("Sphere(1)", sphere);
    //     upperBranchName.addChild(upperBranch);
    //       upperBranch.addChild(sphereNode);
    //     return upperBranchName;
    // }
    private void updateBranches(double elapsedTime) {
        rotateAllAngle = rotateAllAngleStart*(float)Math.sin(elapsedTime);
        rotateUpperAngle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*0.7f);
        float offsetX = 0.4f*2.0f*(float)Math.PI*1.5f*rotateAllAngle/360;
        Mat4 m = Mat4Transform.scale(3,3,3);
        m = Mat4.multiply(m, Mat4Transform.translate(0 - offsetX,0.5f,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(rotateAllAngle));
        lowerBranchT.setTransform(m);
        //rotateAll.setTransform(Mat4Transform.rotateAroundZ(rotateAllAngle));
        //rotateUpper.setTransform(Mat4Transform.rotateAroundZ(rotateUpperAngle));
        //rotateUpper2.setTransform(Mat4Transform.rotateAroundZ(rotateUpperAngle+90));
        alienRoot.update(); // IMPORTANT – the scene graph has changed
      }
    public void render(GL3 gl, double elapsedTime) {
        updateBranches(elapsedTime);
        alienRoot.draw(gl);
    }
    
    private ModelMultipleLights makeSphere(GL3 gl) {
        String name= "sphere";
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_m_1t.txt");
        Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
        ModelMultipleLights sphere = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, camera, t0);
        return sphere;
    }
    public void dispose(GL3 gl) {
        sphere.dispose(gl);
      }
}
