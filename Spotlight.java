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


public class Spotlight {
    private Camera camera;
    private Light[] lights;
    //private ModelMultipleLights sphere, sphereNoTex;

    private SGNode spotlightRoot;
    private ModelMultipleLights sphere;

    private float verticalAngle = -25;
    private float rotateTopAngleStart = 0;//25;
    private float rotateTopAngle = rotateTopAngleStart;
    private TransformNode translateRotatingTop;
    private ModelNode spotlightBulbNode;
    private Light light;

    public Spotlight(GL3 gl, Camera cameraIn, Light[] lightIn) {
        this.camera = cameraIn;
        this.lights = lightIn;
        sphere = makeSphere(gl);
        spotlightRoot = new NameNode("root");
        SGNode pillar = makePillar(sphere);
        TransformNode translateSpotlight = new TransformNode("translate spotlight", Mat4Transform.translate(-10,2.5f,2));
        SGNode rotatingTop = makeRotatingTop(sphere);
        //Mat4 m = Mat4.multiply(Mat4Transform.rotateAroundY(rotateTopAngle), Mat4Transform.translate(-0.15f, 3.1f,0));
        Mat4 m = Mat4.multiply(Mat4Transform.translate(-0.15f, 3.1f,0), Mat4Transform.rotateAroundZ(verticalAngle));
        m = Mat4.multiply(Mat4Transform.rotateAroundY(rotateTopAngle), m);
        translateRotatingTop = new TransformNode("transform top", m);

        SGNode bulb = makeBulb(gl);
        TransformNode translateBulb = new TransformNode("translate bulb", Mat4Transform.translate(0.5f, 0,0));


        spotlightRoot.addChild(translateSpotlight);
         translateSpotlight.addChild(pillar);
         translateSpotlight.addChild(translateRotatingTop);
          translateRotatingTop.addChild(rotatingTop);
          translateRotatingTop.addChild(translateBulb);
           translateBulb.addChild(bulb);
        spotlightRoot.update();
        //spotlight
        light = new SpotlightLight(gl);
        lights[2] = light;
        light.setCamera(cameraIn);

        Vec4 lightPos = Mat4.multiply(spotlightBulbNode.getWorldTransform(), new Vec4(Sphere.calculateCenter(), 1));
        light.setPosition(lightPos.toVec3());
        Mat4 lightDirectionTransform = Mat4.multiply(Mat4Transform.rotateAroundY(rotateTopAngle), Mat4Transform.rotateAroundZ(verticalAngle));
        Vec3 lightDirection = Mat4.multiply(lightDirectionTransform, new Vec4(1, 0, 0, 1)).toVec3();
        lightDirection.normalize();
        ((SpotlightLight)light).setDirection(lightDirection);
    }
    public void render(GL3 gl, double elapsedTime) {
        //updateBranches(elapsedTime);
        rotateTopAngle = (float)elapsedTime * 50;
        Mat4 m = Mat4.multiply(Mat4Transform.translate(-0.15f, 3.1f,0), Mat4Transform.rotateAroundZ(-25));
        m = Mat4.multiply(Mat4Transform.rotateAroundY(rotateTopAngle), m);
        translateRotatingTop.setTransform(m);
        spotlightRoot.update();
        Vec4 lightPos = Mat4.multiply(spotlightBulbNode.getWorldTransform(), new Vec4(Sphere.calculateCenter(), 1));
        light.setPosition(lightPos.toVec3());
        Mat4 lightDirectionTransform = Mat4.multiply(Mat4Transform.rotateAroundY(rotateTopAngle), Mat4Transform.rotateAroundZ(verticalAngle));
        Vec3 lightDirection = Mat4.multiply(lightDirectionTransform, new Vec4(1, 0, 0, 1)).toVec3();
        lightDirection.normalize();
        ((SpotlightLight)light).setDirection(lightDirection);
        spotlightRoot.draw(gl);
    }
    private SGNode makePillar(ModelMultipleLights sphere) {
        Mat4 m = Mat4Transform.scale(0.2f,6f,0.2f);
        m = Mat4.multiply(Mat4Transform.translate(0.0f,0.5f,0), m);
        return buildBranch(sphere, m);
    }
    private SGNode makeRotatingTop(ModelMultipleLights sphere) {
        Mat4 m = Mat4Transform.scale(1f,0.3f,0.3f);
        m = Mat4.multiply(Mat4Transform.translate(0f,0.5f,0), m);
       // m = Mat4.multiply(Mat4Transform.rotateAroundZ(-25), m);
        return buildBranch(sphere, m);
    }
    private SGNode makeBulb(GL3 gl) {
        Mat4 m = Mat4Transform.scale(0.4f,0.25f,0.25f);
        m = Mat4.multiply(Mat4Transform.translate(0f,0.5f,0), m);
        float ambientStrength = 0.3f;
        float diffuseStrength = 0.5f;
        float specularStrength = 0.3f;
        Material material = new Material();
        material.setAmbient(ambientStrength, ambientStrength, ambientStrength);//(0.3f, 0.3f, 0.3f);
        material.setDiffuse(diffuseStrength, diffuseStrength, diffuseStrength);//(0.7f, 0.7f, 0.7f);
        material.setSpecular(specularStrength, specularStrength, specularStrength);//(0.7f, 0.7f, 0.7f);

        String name = "spotlight sphere";
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader shader = new Shader(gl, "shaders/vs_light_01.txt", "shaders/fs_light_01.txt");
        //new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_m_0t.txt");
        //Material material = new Material(new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
        ModelMultipleLights sphere = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, camera);
        NameNode upperBranchName = new NameNode("light bulb");
        TransformNode upperBranch = new TransformNode("transform", m);
        spotlightBulbNode = new ModelNode("spotlight", sphere);
        upperBranchName.addChild(upperBranch);
          upperBranch.addChild(spotlightBulbNode);
        return upperBranchName;
    }
    private SGNode buildBranch(ModelMultipleLights sphere, Mat4 m) {
        NameNode upperBranchName = new NameNode("upper branch");
        TransformNode upperBranch = new TransformNode("transform", m);
        ModelNode sphereNode = new ModelNode("Sphere(1)", sphere);
        upperBranchName.addChild(upperBranch);
          upperBranch.addChild(sphereNode);
        return upperBranchName;
    }
    private ModelMultipleLights makeSphere(GL3 gl) {
        String name= "sphere";
        Mesh mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        Shader shader = new Shader(gl, "shaders/vs_standard.txt", "shaders/fs_standard_ms_0t.txt");
        Material material = new Material(new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.3f, 0.3f, 0.3f), new Vec3(0.5f, 0.5f, 0.5f), 32.0f);
        Mat4 modelMatrix = Mat4.multiply(Mat4Transform.scale(4,4,4), Mat4Transform.translate(0,0.5f,0));
        ModelMultipleLights sphere = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, camera);
        return sphere;
    }
    public void dispose(GL3 gl) {
        sphere.dispose(gl);
    }
}
