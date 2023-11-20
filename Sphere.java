import gmaths.*;
public final class Sphere {
  
  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
 
  private static final int XLONG = 30;
  private static final int YLAT = 30;
  
  public static final float[] vertices = createVertices();
  public static final int[] indices = createIndices();

  private static float[] createVertices() {
    double r = 0.5;
    int step = 8;
    //float[] 
    float[] vertices = new float[XLONG*YLAT*step];
    for (int j = 0; j<YLAT; ++j) {
      double b = Math.toRadians(-90+180*(double)(j)/(YLAT-1));
      for (int i = 0; i<XLONG; ++i) {
        double a = Math.toRadians(360*(double)(i)/(XLONG-1));
        double z = Math.cos(b) * Math.cos(a);
        double x = Math.cos(b) * Math.sin(a);
        double y = Math.sin(b);
        int base = j*XLONG*step;
        vertices[base + i*step+0] = (float)(r*x);
        vertices[base + i*step+1] = (float)(r*y);
        vertices[base + i*step+2] = (float)(r*z); 
        vertices[base + i*step+3] = (float)x;
        vertices[base + i*step+4] = (float)y;
        vertices[base + i*step+5] = (float)z;
        vertices[base + i*step+6] = (float)(i)/(float)(XLONG-1);
        vertices[base + i*step+7] = (float)(j)/(float)(YLAT-1);
      }
    }
    return vertices;
    
    //debugging code:
    //for (int i=0; i<vertices.length; i+=step) {
    //  System.out.println(vertices[i]+", "+vertices[i+1]+", "+vertices[i+2]);
    //}
  }
  
  private static int[] createIndices() {
    int[] indices = new int[(XLONG-1)*(YLAT-1)*6];
    for (int j = 0; j<YLAT-1; ++j) {
      for (int i = 0; i<XLONG-1; ++i) {
        int base = j*(XLONG-1)*6;
        indices[base + i*6+0] = j*XLONG+i;
        indices[base + i*6+1] = j*XLONG+i+1;
        indices[base + i*6+2] = (j+1)*XLONG+i+1;
        indices[base + i*6+3] = j*XLONG+i;
        indices[base + i*6+4] = (j+1)*XLONG+i+1;
        indices[base + i*6+5] = (j+1)*XLONG+i;
      }
    }
    return indices;
    
    //debugging code:
    //for (int i=0; i<indices.length; i+=3) {
    //  System.out.println(indices[i]+", "+indices[i+1]+", "+indices[i+2]);
    //}
  }
  public static Vec3 calculateCenter() {
    float sumX = 0.0f;
    float sumY = 0.0f;
    float sumZ = 0.0f;

    // Sum up all x, y, and z coordinates
    for (int i = 0; i < vertices.length; i += 8) {
        sumX += vertices[i];
        sumY += vertices[i + 1];
        sumZ += vertices[i + 2];
    }

    // Calculate the average
    float centerX = sumX / (vertices.length / 8);
    float centerY = sumY / (vertices.length / 8);
    float centerZ = sumZ / (vertices.length / 8);

    return new Vec3(centerX, centerY, centerZ);
}
  
}