/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.utils.imageHelper;

import emit.ipcv.utils.Const;
import emit.ipcv.utils.IntArrayHelper2D;
import emit.ipcv.utils.MatrixElement;

/**
 * @author rinelfi
 */
public class GrayscaleImageHelper {
	private int[][] image;
	
	public GrayscaleImageHelper(int[][] image) {
		this.image = image;
	}
	
	public int[] getHistogram() {
		IntArrayHelper2D arrayH = new IntArrayHelper2D(image);
		final int ligne = arrayH.lineLength(), colonne = arrayH.columnLength();
		final int[] histogramme = new int[Const.COLOR_MAX_LEVEL];
		for (int x = 0; x < ligne; x++) {
			for (int y = 0; y < colonne; y++) {
				histogramme[image[x][y]]++;
			}
		}
		return histogramme;
	}
	
	public int[] getCumulativeHistogram() {
		int[] sortie = new int[Const.COLOR_MAX_LEVEL];
		int[] histogramme = this.getHistogram();
		sortie[0] = histogramme[0];
		for (int i = 1; i < Const.COLOR_MAX_LEVEL; i++) {
			sortie[i] = sortie[i - 1] + histogramme[i];
		}
		return sortie;
	}
	
	public boolean isBinary() {
		int[] histogram = getHistogram();
		boolean firstAndLastExist = histogram[0] != 0 && histogram[histogram.length - 1] != 0, otherNotEMpty = false;
		for (int i = 1; i < histogram.length - 1; i++) {
			if (histogram[i] != 0) otherNotEMpty = true;
		}
		return firstAndLastExist && !otherNotEMpty;
	}
	
	public int lineLength() {
		return image.length;
	}
	
	public int columnLength() {
		return image.length > 0 ? image[0].length : 0;
	}
	
	public int[][] slice(int lineMin, int columnMin, int lineMax, int columnMax) {
		final int xLength = lineMax - lineMin + 1, yLength = columnMax - columnMin + 1;
		int[][] slice = new int[xLength][yLength];
		for (int x = 0; x < xLength; x++) {
			for (int y = 0; y < yLength; y++) {
				slice[x][y] = image[lineMin + x][columnMin + y];
			}
		}
		return slice;
	}
	
	public MatrixElement<Integer> turnLeft(int currentLine, int currentColumn, String direction) {
		MatrixElement<Integer> matrixElement = new MatrixElement<>();
		int newLine = 0, newColumn = 0;
		
		switch (direction) {
			case Const.RIGHT_DIRECTION:
				newLine = currentLine - 1;
				newColumn = currentColumn;
				break;
			case Const.LEFT_DIRECTION:
				newLine = currentLine + 1;
				newColumn = currentColumn;
				break;
			case Const.TOP_DIRECTION:
				newLine = currentLine;
				newColumn = currentColumn - 1;
				break;
			default:
				newLine = currentLine;
				newColumn = currentColumn + 1;
				break;
		}
		
		return matrixElement.setX(newLine).setY(newColumn).setContent(image[newLine][newColumn]);
	}
	
	public MatrixElement<Integer> turnRight(int currentLine, int currentColumn, String direction) {
		MatrixElement<Integer> matrixElement = new MatrixElement<>();
		int newLine = 0, newColumn = 0;
		
		switch (direction) {
			case Const.RIGHT_DIRECTION:
				newLine = currentLine + 1;
				newColumn = currentColumn;
				break;
			case Const.LEFT_DIRECTION:
				newLine = currentLine - 1;
				newColumn = currentColumn;
				break;
			case Const.TOP_DIRECTION:
				newLine = currentLine;
				newColumn = currentColumn + 1;
				break;
			default:
				newLine = currentLine;
				newColumn = currentColumn - 1;
				break;
		}
		
		return matrixElement.setX(newLine).setY(newColumn).setContent(image[newLine][newColumn]);
	}
	
	public long getDefinition() {
		return lineLength() * columnLength();
	}
	
	public int[][] getImage() {
		return this.image;
	}
}
