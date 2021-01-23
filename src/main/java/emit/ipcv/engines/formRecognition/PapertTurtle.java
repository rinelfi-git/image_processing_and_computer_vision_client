/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.engines.formRecognition;

import emit.ipcv.engines.pointProcessing.ThresholdingBinarization;
import emit.ipcv.utils.Const;
import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;
import emit.ipcv.utils.MatrixElement;
import emit.ipcv.utils.thresholding.Otsu;

/**
 * @author rinelfi
 */
public class PapertTurtle {
	int[][] image;
	
	public PapertTurtle(int[][] image) {
		this.image = image;
	}
	
	public int[][] execute() {
		GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
		int[][] outline = new int[grayscaleImageHelper.lineLength()][grayscaleImageHelper.columnLength()];
		
		removeBorders();
		
		if (!grayscaleImageHelper.isBinary()) {
			Otsu otsu = new Otsu(image);
			ThresholdingBinarization binarisation = new ThresholdingBinarization(image, otsu.execute());
			image = binarisation.execute();
			grayscaleImageHelper = new GrayscaleImageHelper(image);
		}
		
		boolean runTurtle = false;
		String currentDirection = Const.RIGHT_DIRECTION;
		final int form = 0, background = 255;
		
		// initialization of image container
		for (int line = 0; line < grayscaleImageHelper.lineLength(); line++) {
			for (int column = 0; column < grayscaleImageHelper.columnLength(); column++) {
				outline[line][column] = background;
			}
		}
		// initialization of image container
		
		for (int line = 0; line < grayscaleImageHelper.lineLength(); line++) {
			for (int column = 0; column < grayscaleImageHelper.columnLength(); column++) {
				if (image[line][column] == form) {
					runTurtle = true;
					if (outline[line][column] == form) {
						runTurtle = false;
					} else if (image[line][column - 1] == form && image[line][column + 1] == form && outline[line][column] == background) {
						runTurtle = false;
						column += 2;
					}
				}
				
				final int startLine = line, startColumn = column;
				while (runTurtle) {
					MatrixElement<Integer> point = null;
					switch (image[line][column]) {
						case form:
							outline[line][column] = form;
							point = grayscaleImageHelper.turnLeft(line, column, currentDirection);
							currentDirection = getCurrentDirection(line, column, point.getX(), point.getY());
							line = point.getX();
							column = point.getY();
							break;
						case background:
							point = grayscaleImageHelper.turnRight(line, column, currentDirection);
							currentDirection = getCurrentDirection(line, column, point.getX(), point.getY());
							line = point.getX();
							column = point.getY();
							break;
					}
					if (line == startLine && column == startColumn) {
						runTurtle = false;
						currentDirection = Const.RIGHT_DIRECTION;
					}
				}
			}
		}
		return outline;
	}
	
	private void removeBorders() {
		GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(this.image);
		for (int line = 0; line < grayscaleImageHelper.lineLength(); line++) {
			for (int column = 0; column < grayscaleImageHelper.columnLength(); column++) {
				if (line == 0 || column == 0 || line == grayscaleImageHelper.lineLength() - 1 || column == grayscaleImageHelper.columnLength() - 1) image[line][column] = 255;
			}
		}
	}
	
	private String getCurrentDirection(int currentLine, int currentColumn, int newLine, int newColumn) {
		String newDirection = "";
		if (newColumn == currentColumn && newLine < currentLine) newDirection = Const.TOP_DIRECTION;
		else if (newColumn == currentColumn && newLine > currentLine) newDirection = Const.BOTTOM_DIRECTION;
		else if (newLine == currentLine && newColumn < currentColumn) newDirection = Const.LEFT_DIRECTION;
		else if (newLine == currentLine && newColumn > currentColumn) newDirection = Const.RIGHT_DIRECTION;
		return newDirection;
	}
}
