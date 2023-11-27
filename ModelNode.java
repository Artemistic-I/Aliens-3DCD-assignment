import com.jogamp.opengl.*;
import gmaths.*;

public class ModelNode extends SGNode {

  protected ModelMultipleLights model;

  public ModelNode(String name, ModelMultipleLights m) {
    super(name);
    model = m; 
  }

  //modified slightly
  public void draw(GL3 gl) {
    if (name.equals("spotlight")) {
      model.renderSpotlight(gl, worldTransform);
    } else {
      model.render(gl, worldTransform);
    }
    for (int i=0; i<children.size(); i++) {
      children.get(i).draw(gl);
    }
  }
  //new: a getter method used in setting spotlightLight's position
  public Mat4 getWorldTransform() {
    return worldTransform;
  }
}