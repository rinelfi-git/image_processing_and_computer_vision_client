package emit.ipcv.engines.interfaces;

import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;

public abstract class PlaneTransformation {
	
	protected int[][] image;
	public float[][] transformationMatrix;
	
	public int[][] execute() {
		GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
		final int lineLength = grayscaleImageHelper.lineLength(), columnLength = grayscaleImageHelper.columnLength();
		final int[][] transformation = new int[lineLength][columnLength];
		for (int line = 0; line < lineLength; line++) {
			for (int column = 0; column < columnLength; column++) {
				float imaginaryLine = (float) changeLineAxis(line) * this.transformationMatrix[0][0] + (float) changeColumnAxis(column) * this.transformationMatrix[1][0];
				float imaginaryColumn = (float) changeLineAxis(line) * this.transformationMatrix[0][1] + (float) changeColumnAxis(column) * this.transformationMatrix[1][1];
				boolean validLine = restoreLineAxis(imaginaryLine) < lineLength && restoreLineAxis(imaginaryLine) >= 0;
				boolean validColumn = restoreColumnAxis(imaginaryColumn) < columnLength && restoreColumnAxis(imaginaryColumn) >= 0;
				if (validLine && validColumn) {
					transformation[line][column] = this.image[restoreLineAxis(imaginaryLine)][restoreColumnAxis(imaginaryColumn)];
				}
			}
		}
		return transformation;
	}
	
	protected int changeLineAxis(int line) {
		float lineCenter = (float) (new GrayscaleImageHelper(image).lineLength() - 1) / 2.0f;
		float axe = (float) line - lineCenter;
		return (int) (axe <= 0 ? Math.floor(axe) : Math.ceil(axe));
	}
	
	protected int changeColumnAxis(int column) {
		float columnCenter = (float) (new GrayscaleImageHelper(image).columnLength() - 1) / 2.0f;
		float axe = (float) column - columnCenter;
		return -(int) (axe <= 0 ? Math.floor(axe) : Math.ceil(axe));
	}
	
	protected int restoreLineAxis(int line) {
		float lineCenter = (float) (new GrayscaleImageHelper(image).lineLength() - 1) / 2.0f;
		float axe = (float) line + lineCenter;
		return (int) (line <= 0 ? Math.ceil(axe) : Math.floor(axe));
	}
	
	protected int restoreLineAxis(float line) {
		float lineCenter = (float) (new GrayscaleImageHelper(image).lineLength() - 1) / 2.0f;
		float axe = line + lineCenter;
		return (int) (line <= 0 ? Math.ceil(axe) : Math.floor(axe));
	}
	
	protected int restoreColumnAxis(int column) {
		float columnCenter = (float) (new GrayscaleImageHelper(image).columnLength() - 1) / 2.0f;
		float axe = -(float) column + columnCenter;
		return (int) (-column <= 0 ? Math.ceil(axe) : Math.floor(axe));
	}
	
	protected int restoreColumnAxis(float column) {
		float columnCenter = (float) (new GrayscaleImageHelper(image).columnLength() - 1) / 2.0f;
		float axe = -column + columnCenter;
		return (int) (-column <= 0 ? Math.ceil(axe) : Math.floor(axe));
	}
}
