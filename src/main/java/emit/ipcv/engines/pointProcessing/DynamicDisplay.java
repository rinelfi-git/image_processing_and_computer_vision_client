package emit.ipcv.engines.pointProcessing;

import emit.ipcv.engines.interfaces.PointTransformation;
import emit.ipcv.utils.Const;
import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;

public class DynamicDisplay extends PointTransformation {
	private int min, max;
	
	public DynamicDisplay(int[][] image, int min, int max) {
		this.image = image;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public int[][] execute() {
		final GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
		final int lineLength = grayscaleImageHelper.lineLength(), columnLength = grayscaleImageHelper.columnLength();
		int[][] operation = new int[lineLength][columnLength];
		
		for (int line = 0; line < lineLength; line++) {
			for (int column = 0; column < columnLength; column++) {
				if (image[line][column] < min) {
					operation[line][column] = Const.PIXEL_FOREGROUND;
				} else if (image[line][column] > max) {
					operation[line][column] = Const.PIXEL_BACKGROUND;
				} else {
					operation[line][column] = (int) (((float)Const.PIXEL_BACKGROUND / (float)(max - min)) * ((float)image[line][column] - (float)min));
				}
			}
		}
		return operation;
	}
	
}
