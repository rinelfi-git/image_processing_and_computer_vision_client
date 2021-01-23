package emit.ipcv.engines.transformation2D;

import emit.ipcv.engines.interfaces.PlaneTransformation;

public class Rotation2D extends PlaneTransformation {

  private float angle;

  public Rotation2D(int[][] image, final float angle) {
    this.image = image;
    this.angle = angle;
    this.transformationMatrix = this.initMatriceDeTransformation();
  }

  private float[][] initMatriceDeTransformation() {
    float ligne1[] = new float[]{(float) Math.cos(Math.toRadians(this.angle)), (float) Math.sin(Math.toRadians(this.angle))};
    float ligne2[] = new float[]{(float) -Math.sin(Math.toRadians(this.angle)), (float) Math.cos(Math.toRadians(this.angle))};
    return new float[][]{ligne1, ligne2};
  }
}
