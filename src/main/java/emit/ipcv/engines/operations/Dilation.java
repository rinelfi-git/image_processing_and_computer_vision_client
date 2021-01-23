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
public class Dilation extends Operations {

  public Dilation(int[][] structuringElement, int xOrigin, int yOrigin, int[][] image) {
    this.structuringElement = structuringElement;
    this.xOrigin = xOrigin;
    this.yOrigin = yOrigin;
    this.image = image;
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
    int[][] operation = new int[grayscaleImageHelper.lineLength()][grayscaleImageHelper.columnLength()];
    for (int line = 0; line < grayscaleImageHelper.lineLength(); line++) {
      for (int column = 0; column < grayscaleImageHelper.columnLength(); column++) {
        final boolean debordeAGauche = line - xOrigin < 0,
          debordeADroite = line + (structH.lineLength() - (xOrigin + 1)) > grayscaleImageHelper.lineLength() - 1,
          debordeEnHaut = column - yOrigin < 0,
          debordeEnBas = column + (structH.columnLength() - (yOrigin + 1)) > grayscaleImageHelper.columnLength() - 1;
        int[][] elementCopy = null, structCopy = null;
        int xMin = 0, yMin = 0, xMax = 0, yMax = 0;
        if (debordeAGauche && !debordeEnHaut && !debordeADroite && !debordeEnBas) {
          // déborde seulement à gauche
          structCopy = structH.slice(xOrigin, 0, structH.lineLength() - 1, structH.columnLength() - 1);
          xMin = 0;
          yMin = column - yOrigin;
          xMax = line + (structH.lineLength() - 1 - xOrigin);
          yMax = column + (structH.columnLength() - 1 - yOrigin);
        } else if (!debordeAGauche && debordeEnHaut && !debordeADroite && !debordeEnBas) {
          // déborde seulement en haut
          structCopy = structH.slice(0, yOrigin, structH.lineLength() - 1, structH.columnLength() - 1);
          xMin = line - xOrigin;
          yMin = 0;
          xMax = line + (structH.lineLength() - 1 - xOrigin);
          yMax = column + (structH.columnLength() - 1 - yOrigin);
        } else if (!debordeAGauche && !debordeEnHaut && debordeADroite && !debordeEnBas) {
          // déborde seulement à droite
          structCopy = structH.slice(0, 0, xOrigin, structH.columnLength() - 1);
          xMin = line - xOrigin;
          yMin = column - yOrigin;
          xMax = grayscaleImageHelper.lineLength() - 1;
          yMax = column + (structH.columnLength() - 1 - yOrigin);
        } else if (!debordeAGauche && !debordeEnHaut && !debordeADroite && debordeEnBas) {
          // déborde seulement en bas
          structCopy = structH.slice(0, 0, structH.lineLength() - 1, yOrigin);
          xMin = line - xOrigin;
          yMin = column - yOrigin;
          xMax = line + (structH.lineLength() - 1 - xOrigin);
          yMax = grayscaleImageHelper.columnLength() - 1;
        } else if (debordeAGauche && debordeEnHaut && !debordeADroite && !debordeEnBas) {
          // déborde en haut à gauche
          structCopy = structH.slice(xOrigin, yOrigin, structH.lineLength() - 1, structH.columnLength() - 1);
          xMin = 0;
          yMin = 0;
          xMax = line + (structH.lineLength() - 1 - xOrigin);
          yMax = column + (structH.columnLength() - 1 - yOrigin);
        } else if (!debordeAGauche && debordeEnHaut && debordeADroite && !debordeEnBas) {
          // déborde en haut à droite
          structCopy = structH.slice(0, yOrigin, xOrigin, structH.columnLength() - 1);
          xMin = line - xOrigin;
          yMin = 0;
          xMax = grayscaleImageHelper.lineLength() - 1;
          yMax = column + (structH.columnLength() - 1 - yOrigin);
        } else if (debordeAGauche && !debordeEnHaut && !debordeADroite && debordeEnBas) {
          // déborde en bas à gauche
          structCopy = structH.slice(xOrigin, 0, structH.lineLength() - 1, yOrigin);
          xMin = 0;
          yMin = column - yOrigin;
          xMax = grayscaleImageHelper.lineLength() - 1;
          yMax = grayscaleImageHelper.columnLength() - 1;
        } else if (!debordeAGauche && !debordeEnHaut && debordeADroite && debordeEnBas) {
          // déborde en bas à droite
          structCopy = structH.slice(0, 0, xOrigin, yOrigin);
          xMin = line - xOrigin;
          yMin = column - yOrigin;
          xMax = grayscaleImageHelper.lineLength() - 1;
          yMax = grayscaleImageHelper.columnLength() - 1;
        } else {
          // aucun débordement
          structCopy = structH.slice(0, 0, structH.lineLength() - 1, structH.columnLength() - 1);
          xMin = line - xOrigin;
          yMin = column - yOrigin;
          xMax = line + (structH.lineLength() - 1 - xOrigin);
          yMax = column + (structH.columnLength() - 1 - yOrigin);
        }
        boolean inclusion = false;
        elementCopy = grayscaleImageHelper.slice(xMin, yMin, xMax, yMax);
        IntArrayHelper2D structCopyH = new IntArrayHelper2D(structCopy);
        for (int sliceX = 0; sliceX < structCopyH.lineLength(); sliceX++) {
          for (int sliceY = 0; sliceY < structCopyH.columnLength(); sliceY++) {
            if (structCopy[sliceX][sliceY] == elementCopy[sliceX][sliceY]) {
              inclusion = true;
            };
          }
        }
        operation[line][column] = inclusion ? form : background;
      }
    }
    return operation;
  }
}
