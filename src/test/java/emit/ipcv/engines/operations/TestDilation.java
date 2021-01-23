/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.engines.operations;

import emit.ipcv.engines.interfaces.Operations;
import emit.ipcv.utils.IntArrayHelper2D;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author rinelfi
 */
public class TestDilation {

  @Test
  public void execute() {
    int structure[][] = {
      {0, 0, 0},
      {0, 0, 0},
      {0, 0, 0}
    },
      element[][] = {
        {0, 0, 0, 0, 255, 255},
        {0, 255, 255, 255, 0, 0},
        {0, 255, 255, 255, 0, 255},
        {0, 255, 255, 255, 255, 0}
      };

    Operations operation = new Dilation(structure, 1, 1, element);
    int reponse[][] = operation.execute();
    IntArrayHelper2D reponseH = new IntArrayHelper2D(reponse);
    for (int i = 0; i < reponseH.lineLength(); i++) {
      System.out.print("[");
      String result = "";
      for (int ii = 0; ii < reponseH.columnLength(); ii++) {
        result += String.format("%3d, ", reponseH.get(i, ii));
      }
      result = result.substring(0, result.length() - 2);
      System.out.println(result + "]");
    }
    Assert.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, reponse[0]);
    Assert.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, reponse[1]);
    Assert.assertArrayEquals(new int[]{0, 0, 255, 0, 0, 0}, reponse[2]);
    Assert.assertArrayEquals(new int[]{0, 0, 255, 0, 0, 0}, reponse[3]);
  }
}
