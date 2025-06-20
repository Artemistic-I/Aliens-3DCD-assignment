import gmaths.*;

import java.nio.*;
import java.util.Map;

import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

/**
     * This class models an alien object and its animation
     * 
     * @author Artem Iakovlev aiakolev1@sheffield.ac.uk
     * @param gl
     * @param cameraIn
     * @param lightIn
     * @param textures
     * @param alienOffsetX
*/
public class AlienModel {
    private Camera camera;
    private Light[] lights;
    private ModelMultipleLights sphere, sphereNoTex;

    private SGNode alienRoot;
    private Map<String,ModelMultipleLights> textures;

    private float rotateAllAngleStart = 0;//25;
    private float rotateAllAngle = rotateAllAngleStart;

    private float rotateUpperAngleStart = 0;//-20;
    private float rotateUpperAngle = rotateUpperAngleStart;

    private TransformNode rotateHead;
    private float alienSize = 2.0f;
    private boolean isAnimated;
    private boolean rock;
    private boolean roll;

    public AlienModel(GL3 gl, Camera cameraIn, Light[] lightIn, TextureLibrary textures, float alienOffsetX) {
        isAnimated = false;
        this.camera = cameraIn;
        this.lights = lightIn;
        sphere = makeSphere(gl, textures.get("head diff"), textures.get("head spec"));
        sphereNoTex = makeSphere(gl);
        alienRoot = new NameNode("root");
        rock = true;
        roll = true;

        //Creating nodes
        TransformNode translateX = new TransformNode("translateX", Mat4Transform.translate(alienOffsetX,0,0));
        //body
        SGNode lowerBranch = makeLowerBranch(makeSphere(gl, textures.get("body diff"), textures.get("body spec")));
        //right arm
        TransformNode translateToRightSide = new TransformNode("translateToRightSide",Mat4Transform.translate(0.35f,-0.11f,0));
        float armAngle = -45f;
        SGNode rightArm = makeArm(makeSphere(gl, textures.get("arm")), armAngle);
        //left arm
        TransformNode translateToLeftSide = new TransformNode("translateToLeftSide",Mat4Transform.translate(-0.35f,-0.11f,0));
        armAngle = 45;
        SGNode leftArm = makeArm(makeSphere(gl, textures.get("arm")), armAngle);
        //head
        Mat4 m = Mat4.multiply(Mat4Transform.translate(0, -0.07f,0), Mat4Transform.rotateAroundZ(rotateUpperAngle));
        
        rotateHead = new TransformNode("rotate head", m);
        SGNode head = makeHead(sphere);

        m = Mat4Transform.translate(0.35f, 0.7f,0);
        TransformNode moveRightEar = new TransformNode("move right ear", m);
        SGNode rightEar = makeEar(makeSphere(gl, textures.get("ear"), textures.get("ear spec")));

        m = Mat4Transform.translate(-0.35f, 0.7f,0);
        TransformNode moveLeftEar = new TransformNode("move left ear", m);
        SGNode leftEar = makeEar(makeSphere(gl, textures.get("ear"), textures.get("ear spec")));

        m = Mat4Transform.translate(-0.05f, 0.52f,0.32f);
        TransformNode moveRightEye = new TransformNode("move right eye", m);
        Float eyeAngle = -20f;
        SGNode rightEye = makeEye(makeSphere(gl, textures.get("eye")), eyeAngle);

        m = Mat4Transform.translate(0.05f, 0.52f,0.32f);
        TransformNode moveLeftEye = new TransformNode("move left eye", m);
        eyeAngle = 20f;
        SGNode leftEye = makeEye(makeSphere(gl, textures.get("eye")), eyeAngle);

        m = Mat4Transform.translate(0, 0.87f,0);
        TransformNode moveAntennaStick = new TransformNode("move antenna stick", m);
        SGNode antennaStick = makeAntennaStick(makeSphere(gl, textures.get("head spec")));

        m = Mat4Transform.translate(0, 1f,0);
        TransformNode moveAntennaBall = new TransformNode("move antenna ball", m);
        SGNode antennaBall = makeAntennaBall(makeSphere(gl, textures.get("eye")));
        

        //Building the scene graph
        alienRoot.addChild(translateX);
         translateX.addChild(lowerBranch);
          lowerBranchT.addChild(translateToRightSide);
           translateToRightSide.addChild(rightArm);
          lowerBranchT.addChild(translateToLeftSide);
           translateToLeftSide.addChild(leftArm);
          lowerBranchT.addChild(rotateHead);
           rotateHead.addChild(head);
           rotateHead.addChild(moveRightEar);
            moveRightEar.addChild(rightEar);
           rotateHead.addChild(moveLeftEar);
            moveLeftEar.addChild(leftEar);
           rotateHead.addChild(moveRightEye);
            moveRightEye.addChild(rightEye);
           rotateHead.addChild(moveLeftEye);
            moveLeftEye.addChild(leftEye);
           rotateHead.addChild(moveAntennaStick);
            moveAntennaStick.addChild(antennaStick);
           rotateHead.addChild(moveAntennaBall);
            moveAntennaBall.addChild(antennaBall);

        alienRoot.update();
    }
    private TransformNode lowerBranchT;

    private SGNode makeLowerBranch(ModelMultipleLights sphere) {
        NameNode lowerBranchName = new NameNode("lower branch");
        Mat4 m = Mat4Transform.scale(alienSize,alienSize,alienSize);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(rotateAllAngle));
        lowerBranchT = new TransformNode("body transform", m);
        ModelNode sphereNode = new ModelNode("Sphere(0)", sphere);
        lowerBranchName.addChild(lowerBranchT);
         lowerBranchT.addChild(sphereNode);
        return lowerBranchName;
    }
    private SGNode makeArm(ModelMultipleLights sphere, float armAngle) {
        Mat4 m = Mat4Transform.scale(0.08f,0.6f,0.08f);
        m = Mat4.multiply(Mat4Transform.translate(0.0f,0.5f,0), m);
        m = Mat4.multiply(Mat4Transform.rotateAroundZ(armAngle), m);
        return buildBranch(sphere, m);
    }
    private SGNode makeHead(ModelMultipleLights sphere) {
        Mat4 m = Mat4Transform.scale(0.7f,0.7f,0.7f);
        m = Mat4.multiply(Mat4Transform.translate(0.0f,0.9f,0), m);
        return buildBranch(sphere, m);
    }
    private SGNode makeEar(ModelMultipleLights sphere) {
        Mat4 m = Mat4Transform.scale(0.08f,0.6f,0.08f);
        m = Mat4.multiply(Mat4Transform.translate(0.0f,0.5f,0), m);    
        return buildBranch(sphere, m);
    }
    private SGNode makeEye(ModelMultipleLights sphere, float eyeAngle) {
        Mat4 m = Mat4Transform.scale(0.19f,0.15f,0.08f);
        m = Mat4.multiply(Mat4Transform.translate(0.0f,0.5f,0), m);
        m = Mat4.multiply(Mat4Transform.rotateAroundZ(eyeAngle), m);    
        return buildBranch(sphere, m);
    }
    private SGNode makeAntennaStick(ModelMultipleLights sphere) {
        Mat4 m = Mat4Transform.scale(0.03f,0.25f,0.03f);
        m = Mat4.multiply(Mat4Transform.translate(0.0f,0.5f,0), m);    
        return buildBranch(sphere, m);
    }
    private SGNode makeAntennaBall(ModelMultipleLights sphere) {
        Mat4 m = Mat4Transform.scale(0.05f,0.05f,0.05f);
        m = Mat4.multiply(Mat4Transform.translate(0.0f,0.5f,0), m);    
        return buildBranch(sphere, m);
    }
    private SGNode buildBranch(ModelMultipleLights sphere, Mat4 m) {
        NameNode upperBranchName = new NameNode("upper branch");
        TransformNode upperBranch = new TransformNode("transform", m);
        ModelNode sphereNode = new ModelNode("Sphere(1)", sphere);
        upperBranchName.addChild(upperBranch);
          upperBranch.addChild(sphereNode);
        return upperBranchName;
    }
    
    // private void updateBranches(double elapsedTime) {
    //     //update whole body rotation
    //     rotateAllAngle = rotateAllAngleStart*(float)Math.sin(elapsedTime);
    //     float offsetX = 0.4f*2.0f*(float)Math.PI*1.5f*rotateAllAngle/360;
    //     Mat4 m = Mat4Transform.scale(alienSize,alienSize,alienSize);
    //     m = Mat4.multiply(m, Mat4Transform.translate(0 - offsetX,0.5f,0));
    //     m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(rotateAllAngle));
    //     lowerBranchT.setTransform(m);
    //     //update head rotation
    //     rotateUpperAngle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*2f);
    //     m = Mat4.multiply(Mat4Transform.translate(0, -0.07f,0), Mat4Transform.rotateAroundZ(rotateUpperAngle));
    //     rotateHead.setTransform(m);
    //     alienRoot.update(); // IMPORTANT – the scene graph has changed
    // }
    private void updateRock(double elapsedTime) {
        rotateAllAngle = rotateAllAngleStart*(float)Math.sin(elapsedTime);
        float offsetX = 0.4f*2.0f*(float)Math.PI*1.5f*rotateAllAngle/360;
        Mat4 m = Mat4Transform.scale(alienSize,alienSize,alienSize);
        m = Mat4.multiply(m, Mat4Transform.translate(0 - offsetX,0.5f,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(rotateAllAngle));
        lowerBranchT.setTransform(m);
        alienRoot.update();
    }
    private void updateRoll(double elapsedTime) {
        rotateUpperAngle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*2f);
        Mat4 m = Mat4.multiply(Mat4Transform.translate(0, -0.07f,0), Mat4Transform.rotateAroundZ(rotateUpperAngle));
        rotateHead.setTransform(m);
        alienRoot.update();
    }
    public void render(GL3 gl, double elapsedTime) {
        if (isAnimated) {
           //updateBranches(elapsedTime);
           if (rock) {
            updateRock(elapsedTime);
           }
           if (roll) {
            updateRoll(elapsedTime);
           }
        }
        
        alienRoot.draw(gl);
    }
    public void startAnimation() {
        isAnimated = true;
        rock = true;
        roll = true;
        if (rotateAllAngle < 1e-6f && rotateAllAngle > -1e-6f) {
            rotateAllAngleStart = 25;
            rotateAllAngle = rotateAllAngleStart;
            rotateUpperAngleStart = -20;
            rotateUpperAngle = rotateUpperAngleStart;
        }
    }
    public void stopAnimation() {
        isAnimated = false;
    }
    public void rockOnly() {
        if (rotateAllAngle < 1e-6f && rotateAllAngle > -1e-6f) {
            rotateAllAngleStart = 25;
            rotateAllAngle = rotateAllAngleStart;
            rotateUpperAngleStart = -20;
            rotateUpperAngle = rotateUpperAngleStart;
        }
        isAnimated = true;
        rock = true;
        roll = false;
    }
    public void rollOnly() {
        if (rotateAllAngle < 1e-6f && rotateAllAngle > -1e-6f) {
            rotateAllAngleStart = 25;
            rotateAllAngle = rotateAllAngleStart;
            rotateUpperAngleStart = -20;
            rotateUpperAngle = rotateUpperAngleStart;
        }
        isAnimated = true;
        roll = true;
        rock = false;
    }
    public void resetAlien() {
        isAnimated = false;
        rock = true;
        roll = true;
        rotateAllAngleStart = 0;
        rotateAllAngle = rotateAllAngleStart;
        rotateUpperAngleStart = 0;
        rotateUpperAngle = rotateUpperAngleStart;

        Mat4 m = Mat4Transform.scale(alienSize,alienSize,alienSize);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(rotateAllAngle));
        lowerBranchT.setTransform(m);
        //update head rotation
        //rotateUpperAngle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*2f);
        m = Mat4.multiply(Mat4Transform.translate(0, -0.07f,0), Mat4Transform.rotateAroundZ(rotateUpperAngle));
        rotateHead.setTransform(m);
        alienRoot.update(); // IMPORTANT – the scene graph has changed
    }
    
    private ModelMultipleLights makeSphere(GL3 gl, Texture t1, Texture t2) {
        String name= "sphere";
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_ms_2t.txt");
        Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
        ModelMultipleLights sphere = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, camera, t1, t2);
        return sphere;
    }

    private ModelMultipleLights makeSphere(GL3 gl, Texture t) {
        String name= "sphere";
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_ms_1t.txt");
        Material material = new Material(new Vec3(1.0f, 0.5f, 0.31f), new Vec3(1.0f, 0.5f, 0.31f), new Vec3(0.7f, 0.7f, 0.7f), 32.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
        ModelMultipleLights sphere = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, camera, t);
        return sphere;
    }
    private ModelMultipleLights makeSphere(GL3 gl) {
        String name= "sphere";
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_ms_0t.txt");
        Material material = new Material(new Vec3(0.1f, 0f, 0.5f), new Vec3(0.1f, 0.0f, 0.5f), new Vec3(0.7f, 0.7f, 0.7f), 32.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
        ModelMultipleLights sphere = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, camera);
        return sphere;
    }
    public void dispose(GL3 gl) {
        sphere.dispose(gl);
    }
}
