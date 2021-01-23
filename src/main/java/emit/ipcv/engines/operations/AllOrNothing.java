/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.engines.operations;

import emit.ipcv.engines.interfaces.Operations;
import emit.ipcv.engines.pointProcessing.ThresholdingBinarization;
import emit.ipcv.utils.Const;
import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;
import emit.ipcv.utils.IntArrayHelper2D;
import emit.ipcv.utils.thresholding.Otsu;

/**
 *
 * @author rinelfi
 */
public class AllOrNothing extends Operations {

  private int rien;

  {
    rien = Const.NOTHING;
  }

  public AllOrNothing(int[][] elementStructurant, int[][] element) {
    this.structuringElement = elementStructurant;
    this.xOrigin = (elementStructurant.length - 1) / 2;
    this.yOrigin = (elementStructurant[0].length - 1) / 2;
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
    int[][] operation = new int[grayscaleImageHelper.lineLength()][grayscaleImageHelper.columnLength()];
    for (int line = 0; line < grayscaleImageHelper.lineLength(); line++) {
      for (int column = 0; column < grayscaleImageHelper.columnLength(); column++) {
        final boolean debordeAGauche = line - xOrigin < 0,
          debordeADroite = line + (structH.lineLength() - (xOrigin + 1)) > grayscaleImageHelper.lineLength() - 1,
          debordeEnHaut = column - yOrigin < 0,
          debordeEnBas = column + (structH.columnLength() - (yOrigin + 1)) > grayscaleImageHelper.columnLength() - 1;
        int[][] elementCopy = null, structCopy = null;
        int lineMin = 0, columnMin = 0, lineMax = 0, columnMax = 0;
        boolean considererDebordement = false;
        if (debordeAGauche && !debordeEnHaut && !debordeADroite && !debordeEnBas) {
          // déborde seulement à gauche
          structCopy = structH.slice(xOrigin, 0, structH.lineLength() - 1, structH.columnLength() - 1);
          lineMin = 0;
          columnMin = column - yOrigin;
          lineMax = line + (structH.lineLength() - 1 - xOrigin);
          columnMax = column + (structH.columnLength() - 1 - yOrigin);

          // Considération du gauche
          for (int i = 0; i < structH.columnLength(); i++) {
            if (structH.get(0, i) != rien && structH.get(0, i) != background) {
              considererDebordement = true;
            }
          }
        } else if (!debordeAGauche && debordeEnHaut && !debordeADroite && !debordeEnBas) {
          // déborde seulement en haut
          structCopy = structH.slice(0, yOrigin, structH.lineLength() - 1, structH.columnLength() - 1);
          lineMin = line - xOrigin;
          columnMin = 0;
          lineMax = line + (structH.lineLength() - 1 - xOrigin);
          columnMax = column + (structH.columnLength() - 1 - yOrigin);

          // Considération du haut
          for (int i = 0; i < structH.lineLength(); i++) {
            if (structH.get(i, 0) != rien && structH.get(i, 0) != background) {
              considererDebordement = true;
            }
          }
        } else if (!debordeAGauche && !debordeEnHaut && debordeADroite && !debordeEnBas) {
          // déborde seulement à droite
          structCopy = structH.slice(0, 0, xOrigin, structH.columnLength() - 1);
          lineMin = line - xOrigin;
          columnMin = column - yOrigin;
          lineMax = grayscaleImageHelper.lineLength() - 1;
          columnMax = column + (structH.columnLength() - 1 - yOrigin);

          // Considération de l' à droite
          for (int i = 0; i < structH.columnLength(); i++) {
            if (structH.get(structH.lineLength() - 1, i) != rien && structH.get(structH.lineLength() - 1, i) != background) {
              considererDebordement = true;
            }
          }
        } else if (!debordeAGauche && !debordeEnHaut && !debordeADroite && debordeEnBas) {
          // déborde seulement en bas
          structCopy = structH.slice(0, 0, structH.lineLength() - 1, yOrigin);
          lineMin = line - xOrigin;
          columnMin = column - yOrigin;
          lineMax = line + (structH.lineLength() - 1 - xOrigin);
          columnMax = grayscaleImageHelper.columnLength() - 1;

          // Considération d'en bas
          for (int i = 0; i < structH.lineLength(); i++) {
            if (structH.get(i, structH.columnLength() - 1) != rien && structH.get(i, structH.columnLength() - 1) != background) {
              considererDebordement = true;
            }
          }
        } else if (debordeAGauche && debordeEnHaut && !debordeADroite && !debordeEnBas) {
          // déborde en haut à gauche
          structCopy = structH.slice(xOrigin, yOrigin, structH.lineLength() - 1, structH.columnLength() - 1);
          lineMin = 0;
          columnMin = 0;
          lineMax = line + (structH.lineLength() - 1 - xOrigin);
          columnMax = column + (structH.columnLength() - 1 - yOrigin);

          // considération du gauche
          for (int i = 0; i < structH.columnLength(); i++) {
            if (structH.get(0, i) != rien && structH.get(0, i) != background) {
              considererDebordement = true;
            }
          }
          // Considération du haut
          for (int i = 0; i < structH.lineLength(); i++) {
            if (structH.get(i, 0) != rien && structH.get(i, 0) != background) {
              considererDebordement = true;
            }
          }
        } else if (!debordeAGauche && debordeEnHaut && debordeADroite && !debordeEnBas) {
          // déborde en haut à droite
          structCopy = structH.slice(0, yOrigin, xOrigin, structH.columnLength() - 1);
          lineMin = line - xOrigin;
          columnMin = 0;
          lineMax = grayscaleImageHelper.lineLength() - 1;
          columnMax = column + (structH.columnLength() - 1 - yOrigin);

          // Considération du haut
          for (int i = 0; i < structH.lineLength(); i++) {
            if (structH.get(i, 0) != rien && structH.get(i, 0) != background) {
              considererDebordement = true;
            }
          }
          // Considération de l' à droite
          for (int i = 0; i < structH.columnLength(); i++) {
            if (structH.get(structH.lineLength() - 1, i) != rien && structH.get(structH.lineLength() - 1, i) != background) {
              considererDebordement = true;
            }
          }
        } else if (debordeAGauche && !debordeEnHaut && !debordeADroite && debordeEnBas) {
          // déborde en bas à gauche
          structCopy = structH.slice(xOrigin, 0, structH.lineLength() - 1, yOrigin);
          lineMin = 0;
          columnMin = column - yOrigin;
          lineMax = grayscaleImageHelper.lineLength() - 1;
          columnMax = grayscaleImageHelper.columnLength() - 1;

          // considération du gauche
          for (int i = 0; i < structH.columnLength(); i++) {
            if (structH.get(0, i) != rien && structH.get(0, i) != background) {
              considererDebordement = true;
            }
          }
          // Considération d'en bas
          for (int i = 0; i < structH.lineLength(); i++) {
            if (structH.get(i, structH.columnLength() - 1) != rien && structH.get(i, structH.columnLength() - 1) != background) {
              considererDebordement = true;
            }
          }
        } else if (!debordeAGauche && !debordeEnHaut && debordeADroite && debordeEnBas) {
          // déborde en bas à droite
          structCopy = structH.slice(0, 0, xOrigin, yOrigin);
          lineMin = line - xOrigin;
          columnMin = column - yOrigin;
          lineMax = grayscaleImageHelper.lineLength() - 1;
          columnMax = grayscaleImageHelper.columnLength() - 1;

          // Considération de l' à droite
          for (int i = 0; i < structH.columnLength(); i++) {
            if (structH.get(structH.lineLength() - 1, i) != rien && structH.get(structH.lineLength() - 1, i) != background) {
              considererDebordement = true;
            }
          }
          // Considération d'en bas
          for (int i = 0; i < structH.lineLength(); i++) {
            if (structH.get(i, structH.columnLength() - 1) != rien && structH.get(i, structH.columnLength() - 1) != background) {
              considererDebordement = true;
            }
          }
        } else {
          // aucun débordement
          structCopy = structH.slice(0, 0, structH.lineLength() - 1, structH.columnLength() - 1);
          lineMin = line - xOrigin;
          columnMin = column - yOrigin;
          lineMax = line + (structH.lineLength() - 1 - xOrigin);
          columnMax = column + (structH.columnLength() - 1 - yOrigin);
        }
        if ((debordeAGauche || debordeEnHaut || debordeADroite || debordeEnBas) && considererDebordement) {
          operation[line][column] = background;
        } else {
          boolean anomalie = false;
          elementCopy = grayscaleImageHelper.slice(lineMin, columnMin, lineMax, columnMax);
          IntArrayHelper2D structCopyH = new IntArrayHelper2D(structCopy);
          for (int sliceX = 0; sliceX < structCopyH.lineLength(); sliceX++) {
            for (int sliceY = 0; sliceY < structCopyH.columnLength(); sliceY++) {
              if (structCopy[sliceX][sliceY] != rien && structCopy[sliceX][sliceY] != elementCopy[sliceX][sliceY]) {
                anomalie = true;
              }
            }
          }
          operation[line][column] = anomalie ? background : form;
        }
      }
    }
    return operation;
  }

}
