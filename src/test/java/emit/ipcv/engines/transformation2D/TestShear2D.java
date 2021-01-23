/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.engines.transformation2D;

import emit.ipcv.engines.interfaces.PlaneTransformation;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author rinelfi
 */
public class TestShear2D {

  @Test
  public void test() {
    final float[] constants = new float[]{3f, 2f};
    int[][] matrix = new int[][]{
      {0, 1, 2, 3},
      {0, 1, 2, 3},
      {0, 1, 2, 3},
      {0, 1, 2, 3}
    };
    PlaneTransformation transformation = new Shear2D(matrix, constants);
    int output = 0;
    Assert.assertEquals(0, output);
  }
}
