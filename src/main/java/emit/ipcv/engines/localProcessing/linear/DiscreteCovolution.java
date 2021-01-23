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
public abstract class DiscreteCovolution extends LinearLocalProcessing {

  public DiscreteCovolution(int[][] image) {
    this.image = image;
  }

  protected abstract void initFilter();
}
