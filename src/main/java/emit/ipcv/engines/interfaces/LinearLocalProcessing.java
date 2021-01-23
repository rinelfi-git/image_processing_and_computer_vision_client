/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.engines.interfaces;

import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;

/**
 * @author rinelfi
 */
public abstract class LinearLocalProcessing {
	
	protected int[][] image;
	protected float[][] filter;
	
	public int[][] execute() {
		final GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
		final int lineLength = grayscaleImageHelper.lineLength(), columnLength = grayscaleImageHelper.columnLength();
		final int filterLine = filter.length, filterColumn = filter[0].length;
		final int filterLineAxis = (filterLine - 1) / 2, columnFilterAxis = (filterColumn - 1) / 2;
		int[][] output = new int[lineLength][columnLength];
		
		for (int line = 0; line < lineLength; line++) {
			for (int column = 0; column < columnLength; column++) {
				if ((line - filterLineAxis) < 0 || (column - columnFilterAxis) < 0) {
					output[line][column] = 0;
				} else if ((line + (filterLineAxis + 1)) > lineLength || (column + (columnFilterAxis + 1)) > columnLength) {
					output[line][column] = 0;
				} else {
					final int[][] grayscaleExtracted = grayscaleImageHelper.slice(line - 1, column - 1, (filterLine - 1) + (line - 1), (filterColumn - 1) + (column - 1));
					float result = 0;
					for (int imaginaryLineAxis = -filterLineAxis; imaginaryLineAxis <= filterLineAxis; imaginaryLineAxis++) {
						for (int imaginaryColumnAxis = -columnFilterAxis; imaginaryColumnAxis <= columnFilterAxis; imaginaryColumnAxis++) {
							final int realLine = restoreXAxis(imaginaryLineAxis, filter),
								realColumn = restoreYAxis(imaginaryColumnAxis, filter);
							result += filter[realLine][realColumn] * grayscaleExtracted[restoreXAxis(-imaginaryLineAxis, grayscaleExtracted)][restoreYAxis(-imaginaryColumnAxis, grayscaleExtracted)];
						}
					}
					output[line][column] = (int) (result < 0 || result > 255 ? (result < 0 ? 0 : 255) : result);
				}
			}
		}
		return output;
	}
	
	protected int changeXAxis(int x, float[][] matrice) {
		float centreLigne = (matrice.length - 1) / 2.0f;
		float axe = (float) x - centreLigne;
		return (int) (axe <= 0 ? Math.floor(axe) : Math.ceil(axe));
	}
	
	protected int changeXAxis(int x, int[][] matrice) {
		float centreLigne = (float) (matrice.length - 1) / 2.0f;
		float axe = (float) x - centreLigne;
		return (int) (axe <= 0 ? Math.floor(axe) : Math.ceil(axe));
	}
	
	protected int chaneYAxis(int y, float[][] matrice) {
		float centreColonne = (matrice[0].length - 1) / 2.0f;
		float axe = (float) y - centreColonne;
		return (int) (axe <= 0 ? Math.floor(axe) : Math.ceil(axe));
	}
	
	protected int chaneYAxis(int y, int[][] matrice) {
		float centreColonne = (float) (matrice[0].length - 1) / 2.0f;
		float axe = (float) y - centreColonne;
		return (int) (axe <= 0 ? Math.floor(axe) : Math.ceil(axe));
	}
	
	protected int restoreXAxis(int x, float[][] matrice) {
		float centreLigne = (matrice.length - 1) / 2.0f;
		float axe = (float) x + centreLigne;
		return (int) (x <= 0 ? Math.ceil(axe) : Math.floor(axe));
	}
	
	protected int restoreXAxis(int x, int[][] matrice) {
		float centreLigne = (float) (matrice.length - 1) / 2.0f;
		float axe = (float) x + centreLigne;
		return (int) (x <= 0 ? Math.ceil(axe) : Math.floor(axe));
	}
	
	protected int restoreYAxis(int y, float[][] matrice) {
		float centreColonne = (matrice[0].length - 1) / 2.0f;
		float axe = (float) y + centreColonne;
		return (int) (y <= 0 ? Math.ceil(axe) : Math.floor(axe));
	}
	
	protected int restoreYAxis(int y, int[][] matrice) {
		float centreColonne = (float) (matrice[0].length - 1) / 2.0f;
		float axe = (float) y + centreColonne;
		return (int) (y <= 0 ? Math.ceil(axe) : Math.floor(axe));
	}
}
