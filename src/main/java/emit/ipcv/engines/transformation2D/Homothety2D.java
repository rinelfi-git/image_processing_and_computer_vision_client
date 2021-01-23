package emit.ipcv.engines.transformation2D;

import emit.ipcv.engines.interfaces.PlaneTransformation;

public class Homothety2D extends PlaneTransformation {

  private float[] constants;
  
  public Homothety2D(int[][] image, final float[] constants) {
    this.image = image;
    this.constants = constants;
    this.transformationMatrix = this.initMatriceDeTransformation();
  }

  private float[][] initMatriceDeTransformation() {
    float line1[] = new float[]{this.constants[0], 0f};
    float line2[] = new float[]{0f, this.constants[1]};
    return new float[][]{line1, line2};
  }
}
