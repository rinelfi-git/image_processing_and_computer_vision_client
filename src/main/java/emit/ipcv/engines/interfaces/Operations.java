/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.engines.interfaces;

/**
 *
 * @author rinelfi
 */
public abstract class Operations {

  protected int[][] structuringElement, image;
  protected int xOrigin, yOrigin, form, background;

  {
    form = 0;
    background = 255;
  }

  public void setForm(int form) {
    this.form = form;
  }

  public void setBackground(int background) {
    this.background = background;
  }

  public abstract int[][] execute();
}
