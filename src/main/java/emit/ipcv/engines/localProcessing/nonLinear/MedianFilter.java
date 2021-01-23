/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.engines.localProcessing.nonLinear;

import emit.ipcv.engines.interfaces.NonLinearLocalTransformation;
import emit.ipcv.utils.Const;
import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;
import emit.ipcv.utils.IntArrayHelper2D;

/**
 *
 * @author rinelfi
 */
public class MedianFilter extends NonLinearLocalTransformation {

  public MedianFilter(int[][] image) {
    this.image = image;
  }

  @Override
  public int[][] execute() {
    final GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
    final int lineLength = grayscaleImageHelper.lineLength(), columnLength = grayscaleImageHelper.columnLength();
    final int filterLine = Const.NEIGHBORHOOD, filterColumn = Const.NEIGHBORHOOD;
    final int filterLineAxis = (filterLine - 1) / 2, filterColumnAxis = (filterColumn - 1) / 2;
    int[][] operation = new int[lineLength][columnLength];

    for (int line = 0; line < lineLength; line++) {
      for (int column = 0; column < columnLength; column++) {
        if ((line - filterLineAxis) < 0 || (column - filterColumnAxis) < 0) {
          operation[line][column] = 0;
        } else if ((line + (filterLineAxis + 1)) > lineLength || (column + (filterColumnAxis + 1)) > columnLength) {
          operation[line][column] = 0;
        } else {
          final int[][] grayscaleSlice = grayscaleImageHelper.slice(line - 1, column - 1, (Const.NEIGHBORHOOD - 1) + line - 1, (Const.NEIGHBORHOOD - 1) + column - 1);
          operation[line][column] = new IntArrayHelper2D(grayscaleSlice).getMeidan();
        }
      }
    }
    return operation;
  }

}
