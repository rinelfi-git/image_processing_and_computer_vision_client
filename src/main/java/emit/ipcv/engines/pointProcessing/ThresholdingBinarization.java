package emit.ipcv.engines.pointProcessing;

import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;
import emit.ipcv.engines.interfaces.PointTransformation;

public class ThresholdingBinarization extends PointTransformation {

  private int threshold;

  public ThresholdingBinarization(int[][] image, int threshold) {
    this.image = image;
    this.threshold = threshold;
  }

  @Override
  public int[][] execute() {
    final GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
    final int lineLength = grayscaleImageHelper.lineLength(), columnLength = grayscaleImageHelper.columnLength();
    int[][] operation = new int[lineLength][columnLength];
    for (int x = 0; x < lineLength; x++) {
      for (int y = 0; y < columnLength; y++) {
        operation[x][y] = image[x][y] <= threshold ? 0 : 255;
      }
    }
    return operation;
  }
}
