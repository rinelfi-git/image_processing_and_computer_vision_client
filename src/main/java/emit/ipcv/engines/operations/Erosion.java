/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.engines.operations;

import emit.ipcv.engines.interfaces.Operations;
import emit.ipcv.engines.pointProcessing.ThresholdingBinarization;
import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;
import emit.ipcv.utils.IntArrayHelper2D;
import emit.ipcv.utils.thresholding.Otsu;

/**
 *
 * @author rinelfi
 */
public class Erosion extends Operations {

  public Erosion(int[][] elementStructurant, int xOrigineElementStructurant, int yOrigineElementStructurant, int[][] element) {
    this.structuringElement = elementStructurant;
    this.xOrigin = xOrigineElementStructurant;
    this.yOrigin = yOrigineElementStructurant;
    this.image = element;
  }

  @Override
  public int[][] execute() {
    // Binariser si l'élément ne l'est pas encore
    GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);

    if (!grayscaleImageHelper.isBinary()) {
      Otsu otsu = new Otsu(image);
      ThresholdingBinarization binarization = new ThresholdingBinarization(image, otsu.execute());
      image = binarization.execute();
      grayscaleImageHelper = new GrayscaleImageHelper(image);
    }
  
    IntArrayHelper2D structH = new IntArrayHelper2D(structuringElement);
    int[][] erosion = new int[grayscaleImageHelper.lineLength()][grayscaleImageHelper.columnLength()];
    for (int x = 0; x < grayscaleImageHelper.lineLength(); x++) {
      for (int y = 0; y < grayscaleImageHelper.columnLength(); y++) {
        final boolean debordeAGauche = x - xOrigin < 0,
          debordeEnHaut = y - yOrigin < 0,
          debordeADroite = x + (structH.lineLength() - (xOrigin + 1)) > grayscaleImageHelper.lineLength() - 1,
          debordeEnBas = y + (structH.columnLength() - (yOrigin + 1)) > grayscaleImageHelper.columnLength() - 1;
        if (debordeAGauche || debordeEnHaut || debordeADroite || debordeEnBas) {
          erosion[x][y] = background;
        } else {
          boolean anomalie = false;
          final int xMin = x - xOrigin,
            yMin = y - yOrigin,
            xMax = x + (structH.lineLength() - 1 - xOrigin),
            yMax = y + (structH.columnLength() - 1 - yOrigin);
          int[][] slice = grayscaleImageHelper.slice(xMin, yMin, xMax, yMax);
          for (int sliceX = 0; sliceX < structH.lineLength(); sliceX++) {
            for (int sliceY = 0; sliceY < structH.columnLength(); sliceY++) {
              if (structuringElement[sliceX][sliceY] != slice[sliceX][sliceY]) {
                anomalie = true;
              };
            }
          }
          erosion[x][y] = anomalie ? background : form;
        }
      }
    }
    return erosion;
  }
}
