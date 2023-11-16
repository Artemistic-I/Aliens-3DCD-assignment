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

        //Creating nodes
        // Mat4 m = new Mat4(1.0f);
        // m = Mat4.multiply(Mat4Transform.translate(0,0.5f,0), m);
        // TransformNode aboveSurfaceT = new TransformNode("aboveSurfaceT", m);

        // m = Mat4.multiply(Mat4Transform.scale(3, 3, 3), new Mat4(1.0f));
        // TransformNode scaleBodyT = new TransformNode("scaleBodyT", m);
        // ModelNode body = new ModelNode("body", sphere);
        
        // ModelNode rightHand = new ModelNode("rightHand", sphere);
        // m = Mat4.multiply(Mat4Transform.scale(0, 2, 0), new Mat4(1.0f));
        // m = Mat4.multiply(Mat4Transform.rotateAroundZ(30), m);
        // m = Mat4.multiply(Mat4Transform.translate(3,3,0), m);
        // TransformNode rightHandT = new TransformNode("rightHandT", m);


        
        SGNode lowerBranch = makeLowerBranch(sphere);

        //Building the scene graph
        alienRoot.addChild(lowerBranch);

        // alienRoot.addChild(aboveSurfaceT);
        //  aboveSurfaceT.addChild(scaleBodyT);
        //   scaleBodyT.addChild(body);
        //   aboveSurfaceT.addChild(rightHandT);
        //    rightHandT.addChild(rightHand);

        alienRoot.update();
    }
    private TransformNode lowerBranch;

    private SGNode makeLowerBranch(ModelMultipleLights sphere) {
        NameNode lowerBranchName = new NameNode("lower branch");
        Mat4 m = Mat4Transform.scale(3,3,3);
        m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(rotateAllAngle));
        //rotateAll = new TransformNode("rotateAll", Mat4Transform.rotateAroundZ(rotateAllAngle));
        lowerBranch = new TransformNode("lower branch transform", m);
        ModelNode sphereNode = new ModelNode("Sphere(0)", sphere);
        lowerBranchName.addChild(lowerBranch);
         lowerBranch.addChild(sphereNode);
        return lowerBranchName;
    }
    private void updateBranches(double elapsedTime) {
        rotateAllAngle = rotateAllAngleStart*(float)Math.sin(elapsedTime);
        rotateUpperAngle = rotateUpperAngleStart*(float)Math.sin(elapsedTime*0.7f);
        float offsetX = 0.4f*2.0f*(float)Math.PI*1.5f*rotateAllAngle/360;
        Mat4 m = Mat4Transform.scale(3,3,3);
        m = Mat4.multiply(m, Mat4Transform.translate(0 - offsetX,0.5f,0));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(rotateAllAngle));
        lowerBranch.setTransform(m);
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
