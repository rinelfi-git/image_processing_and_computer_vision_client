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
public abstract class NonLinearLocalTransformation {

  protected int[][] image;

  protected abstract int[][] execute();

  protected int changeLineAxis(int line, float[][] image) {
    float lineCenter = (image.length - 1) / 2.0f;
    float axe = (float) line - lineCenter;
    return (int) (axe <= 0 ? Math.floor(axe) : Math.ceil(axe));
  }

  protected int changeLineAxis(int line, int[][] image) {
    float lineCenter = (float) (image.length - 1) / 2.0f;
    float axe = (float) line - lineCenter;
    return (int) (axe <= 0 ? Math.floor(axe) : Math.ceil(axe));
  }

  protected int changeColumnAxis(int column, float[][] image) {
    float columnCenter = (image[0].length - 1) / 2.0f;
    float axe = (float) column - columnCenter;
    return (int) (axe <= 0 ? Math.floor(axe) : Math.ceil(axe));
  }

  protected int changeColumnAxis(int column, int[][] image) {
    float columnCenter = (float) (image[0].length - 1) / 2.0f;
    float axe = (float) column - columnCenter;
    return (int) (axe <= 0 ? Math.floor(axe) : Math.ceil(axe));
  }

  protected int retablirAxeX(int line, float[][] image) {
    float centreLigne = (image.length - 1) / 2.0f;
    float axe = (float) line + centreLigne;
    return (int) (line <= 0 ? Math.ceil(axe) : Math.floor(axe));
  }

  protected int retablirAxeX(int line, int[][] image) {
    float centreLigne = (float) (image.length - 1) / 2.0f;
    float axe = (float) line + centreLigne;
    return (int) (line <= 0 ? Math.ceil(axe) : Math.floor(axe));
  }

  protected int retablirAxeY(int column, float[][] image) {
    float columnCenter = (image[0].length - 1) / 2.0f;
    float axe = (float) column + columnCenter;
    return (int) (column <= 0 ? Math.ceil(axe) : Math.floor(axe));
  }

  protected int retablirAxeY(int column, int[][] image) {
    float columnCenter = (float) (image[0].length - 1) / 2.0f;
    float axe = (float) column + columnCenter;
    return (int) (column <= 0 ? Math.ceil(axe) : Math.floor(axe));
  }
}
