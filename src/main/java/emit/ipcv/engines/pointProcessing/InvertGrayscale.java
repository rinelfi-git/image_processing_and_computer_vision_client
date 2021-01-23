package emit.ipcv.engines.pointProcessing;

import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;
import emit.ipcv.engines.interfaces.PointTransformation;

public class InvertGrayscale extends PointTransformation {

  public InvertGrayscale(int[][] image) {
    this.image = image;
  }

  @Override
  public int[][] execute() {
    GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
    final int lineLength = grayscaleImageHelper.lineLength(), columnLength = grayscaleImageHelper.columnLength();
    int[][] operation = new int[lineLength][columnLength];
    for (int line = 0; line < lineLength; line++) {
      for (int column = 0; column < columnLength; column++) {
        operation[line][column] = 255 - image[line][column];
      }
    }
    return operation;
  }
}
