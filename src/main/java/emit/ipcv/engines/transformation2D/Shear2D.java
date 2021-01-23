package emit.ipcv.engines.transformation2D;

import emit.ipcv.engines.interfaces.PlaneTransformation;

public class Shear2D extends PlaneTransformation {

  private float[] constants;

  public Shear2D(int[][] image, float[] constants) {
    this.image = image;
    this.constants = constants;
    this.transformationMatrix = this.initMatriceDeTransformation();
  }

  private float[][] initMatriceDeTransformation() {
    float ligne1[] = new float[]{1f, this.constants[0]};
    float ligne2[] = new float[]{this.constants[1], 1f};
    return new float[][]{ligne1, ligne2};
  }
}
