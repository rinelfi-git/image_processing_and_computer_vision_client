/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.engines.localProcessing.linear;

import emit.ipcv.engines.interfaces.LinearLocalProcessing;

/**
 *
 * @author rinelfi
 */
public class GaussianFilter extends LinearLocalProcessing {

  public GaussianFilter(int[][] image) {
    this.image = image;
    this.filter = initFilter();
  }

  private float[][] initFilter() {
    filter = new float[][]{
      {1f / 16f, 2f / 16f, 1f / 16f},
      {2f / 16f, 4f / 16f, 2f / 16f},
      {1f / 16f, 2f / 16f, 1f / 16f}
    };
    return filter;
  }
}
