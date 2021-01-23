package emit.ipcv.engines.transformation2D;

import emit.ipcv.engines.interfaces.PlaneTransformation;

public class SymmetryX2D extends PlaneTransformation {

  public SymmetryX2D(int[][] image) {
    this.image = image;
    this.transformationMatrix = this.initMatriceDeTransformation();
  }

  private float[][] initMatriceDeTransformation() {
    float line1[] = new float[]{1f, 0f};
    float line2[] = new float[]{0f, -1f};
    return new float[][]{line1, line2};
  }
}
