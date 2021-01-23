package emit.ipcv.utils.thresholding;

import emit.ipcv.utils.Const;
import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;

public class Otsu {

  int[] histogram;
  int[] cumulateHistogram;
  
  public Otsu(int[] histogram) {
    this.histogram = histogram;
    this.cumulateHistogram = getCumulativeHistogram();
  }

  public Otsu(int[][] image) {
    GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
    this.histogram = grayscaleImageHelper.getHistogram();
    this.cumulateHistogram = grayscaleImageHelper.getCumulativeHistogram();
  }

  public int execute() {
    final int tableLength = histogram.length;
    int threshold = 0;
    float maximalVariance = 0f;
    float definitionImage = (float) cumulateHistogram[tableLength - 1];
    for (int i = 0; i < tableLength; i++) {
      float backgroungClass = sum(histogram, 0, i) / definitionImage;
      float backgroundAverage = average(histogram, 0, i);

      float formClass = sum(histogram, i, tableLength) / definitionImage;
      float formAverage = average(histogram, i, tableLength);

      float interClassVariance = backgroungClass * formClass * ((float) Math.pow(backgroundAverage - formAverage, 2));

      if (interClassVariance > maximalVariance) {
        maximalVariance = interClassVariance;
        threshold = i;
      }
    }
    return threshold;
  }

  private float sum(int[] histogram, int debut, int fin) {
    float sum = 0f;
    for (int i = debut; i < fin; i++) {
      sum += (float) histogram[i];
    }
    return sum;
  }

  private float average(int[] histogram, int debut, int fin) {
    float sum = 0f;
    for (int i = debut; i < fin; i++) {
      sum += i * histogram[i];
    }
    return sum / sum(histogram, debut, fin);
  }

  private int[] getCumulativeHistogram() {
    final int tableLength = histogram.length;
    int[] output = new int[tableLength];
    output[0] = histogram[0];
    for (int i = 1; i < tableLength; i++) {
      output[i] = output[i - 1] + histogram[i];
    }
    return output;
  }

  private int[] getHistogram(int[][] image) {
    int[] histogram = new int[Const.COLOR_MAX_LEVEL];
    GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
    for (int line = 0; line < grayscaleImageHelper.lineLength(); line++) {
      for (int column = 0; column < grayscaleImageHelper.columnLength(); column++) {
        histogram[image[line][column]]++;
      }
    }
    return histogram;
  }
}
