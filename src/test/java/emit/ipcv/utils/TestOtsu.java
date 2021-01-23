/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.utils;

import static junit.framework.Assert.assertEquals;

import emit.ipcv.utils.thresholding.Otsu;
import org.junit.Test;

/**
 *
 * @author rinelfi
 */
public class TestOtsu {

  @Test
  public void testExecution() {
    int[] histogram = new int[]{8, 7, 2, 6, 9, 4};
    Otsu otsuThresholding = new Otsu(histogram);
    final int threshold = otsuThresholding.execute();
    assertEquals(3, threshold);
  }
}
