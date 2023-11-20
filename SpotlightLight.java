import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class SpotlightLight extends Light {

    private Vec3 direction; //to be set
    private float cutOff;
    private float outerCutOff;

    //attenuation
    private float constant;
    private float linear;
    private float quadratic;

    public SpotlightLight(GL3 gl) {
        super(gl);
        cutOff = (float)Math.cos(12.5f * Math.PI / 180);
        outerCutOff = (float)Math.cos(17.5f * Math.PI / 180);

        constant = 1;
        linear = 0.09f;
        quadratic = 0.032f;

        onOff = 1f;
    }
    //Setters
    public void setDirection(Vec3 direction) {
        this.direction = direction;
    }
    public void setCutOff(float cutOff) {
        this.cutOff = cutOff;
    }
    public void setOuterCutOff(float outerCutOff) {
        this.outerCutOff = outerCutOff;
    }

    //Getters
    public Vec3 getDirection() {
        return direction;
    }
    public float getCutOff() {
        return cutOff;
    }
    public float getOuterCutOff() {
        return outerCutOff;
    }
    public float getConstant() {
        return constant;
    }
    public float getLinear() {
        return linear;
    }
    public float getQuadratic() {
        return quadratic;
    }
}
