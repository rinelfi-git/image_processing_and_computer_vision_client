package emit.ipcv.utils.imageHelper;

import emit.ipcv.utils.colors.RGBA;

public class RgbImageHelper {
	private RGBA[][] image;
	
	public RgbImageHelper(RGBA[][] image) {
		this.image = image;
	}
	
	public RgbImageHelper(int[][] image) {
		this.image = parseRgb(image);
	}
	
	public RgbImageHelper(int[][] red, int[][] green, int[][] blue) {
		this.image = parseRgb(red, green, blue);
	}
	public RgbImageHelper(int[][] alpha, int[][] red, int[][] green, int[][] blue) {
		this.image = parseRgb(alpha, red, green, blue);
	}
	
	public int lineLength() {
		return image.length;
	}
	
	public int columnLength() {
		return image.length > 0 ? image[0].length : 0;
	}
	
	public int[][] getAlphas() {
		int[][] color = new int[lineLength()][columnLength()];
		for (int line = 0; line < lineLength(); line++) {
			for (int column = 0; column < columnLength(); column++) {
				color[line][column] = image[line][column].getAlpha();
			}
		}
		return color;
	}
	
	public int[][] getReds() {
		int[][] color = new int[lineLength()][columnLength()];
		for (int line = 0; line < lineLength(); line++) {
			for (int column = 0; column < columnLength(); column++) {
				color[line][column] = image[line][column].getRed();
			}
		}
		return color;
	}
	
	public int[][] getBlues() {
		int[][] color = new int[lineLength()][columnLength()];
		for (int line = 0; line < lineLength(); line++) {
			for (int column = 0; column < columnLength(); column++) {
				color[line][column] = image[line][column].getBlue();
			}
		}
		return color;
	}
	
	public int[][] getGreens() {
		int[][] color = new int[lineLength()][columnLength()];
		for (int line = 0; line < lineLength(); line++) {
			for (int column = 0; column < columnLength(); column++) {
				color[line][column] = image[line][column].getGreen();
			}
		}
		return color;
	}
	
	private RGBA[][] parseRgb(int[][] image) {
		GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
		RGBA[][] output = new RGBA[grayscaleImageHelper.lineLength()][grayscaleImageHelper.columnLength()];
		for (int line = 0; line < grayscaleImageHelper.lineLength(); line++) {
			for (int column = 0; column < grayscaleImageHelper.columnLength(); column++) {
				output[line][column] = new RGBA(255, image[line][column], image[line][column], image[line][column]);
			}
		}
		return output;
	}
	
	private RGBA[][] parseRgb(int[][] red, int[][] green, int[][] blue) {
		GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(red);
		RGBA[][] output = new RGBA[grayscaleImageHelper.lineLength()][grayscaleImageHelper.columnLength()];
		for (int line = 0; line < grayscaleImageHelper.lineLength(); line++) {
			for (int column = 0; column < grayscaleImageHelper.columnLength(); column++) {
				output[line][column] = new RGBA(255, red[line][column], green[line][column], blue[line][column]);
			}
		}
		return output;
	}
	
	private RGBA[][] parseRgb(int[][] alpha, int[][] red, int[][] green, int[][] blue) {
		GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(red);
		RGBA[][] output = new RGBA[grayscaleImageHelper.lineLength()][grayscaleImageHelper.columnLength()];
		for (int line = 0; line < grayscaleImageHelper.lineLength(); line++) {
			for (int column = 0; column < grayscaleImageHelper.columnLength(); column++) {
				output[line][column] = new RGBA(alpha[line][column], red[line][column], green[line][column], blue[line][column]);
			}
		}
		return output;
	}
	
	public RGBA[][] getImage() {
		return this.image;
	}
	
	public RgbImageHelper setAlphas(int[][] alphas) {
		GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(alphas);
		for (int line = 0; line < grayscaleImageHelper.lineLength(); line++) {
			for (int column = 0; column < grayscaleImageHelper.columnLength(); column++) {
				image[line][column].setAlpha(alphas[line][column]);
			}
		}
		return this;
	}
	private int[][] getLightningGrayscale() {
		final int lineLength = lineLength(), columnLength = columnLength();
		int[][] grayscale = new int[lineLength][columnLength];
		for (int x = 0; x < lineLength; x++) {
			for (int y = 0; y < columnLength; y++) {
				grayscale[x][y] = ((image[x][y].max() + image[x][y].min()) / 2);
			}
		}
		return grayscale;
	}
	
	private int[][] getAverageGayscale() {
		final int lineLength = lineLength(), columnLength = columnLength();
		int[][] grayscale = new int[lineLength][columnLength];
		for (int x = 0; x < lineLength; x++) {
			for (int y = 0; y < columnLength; y++) {
				grayscale[x][y] = (int) ((float) (image[x][y].getRed() + image[x][y].getGreen() + image[x][y].getBlue()) / (float) 3);
			}
		}
		return grayscale;
	}
	
	private int[][] getLuninanceGrayscale() {
		final int lineLength = lineLength(), columnLength = columnLength();
		int[][] grayscale = new int[lineLength][columnLength];
		for (int line = 0; line < lineLength; line++) {
			for (int column = 0; column < columnLength; column++) {
				grayscale[line][column] = (int) (0.21 * image[line][column].getRed() + 0.72 * image[line][column].getGreen() + 0.07 * image[line][column].getBlue());
			}
		}
		return grayscale;
	}
	
	public int[][] getGrayscale() {
		return this.getAverageGayscale();
	}
	
	
}
