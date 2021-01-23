package emit.ipcv.engines.formRecognition;

import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TestPapertTurtle {
	
	@Test
	public void test() {
		final int back = 255, form = 0;
		int[][] image = new int[][]{
			      //0     1      2     3    4     5    6
			/*0*/	{back, back, back, back, back, back, back},
			/*1*/ {back, back, form, back, back, back, back},
			/*2*/ {back, form, form, form, back, form, back},
			/*3*/ {back, form, form, form, back, form, back},
			/*3*/ {back, form, form, form, back, form, back},
			/*4*/ {back, back, form, back, back, form, back},
			/*5*/ {back, back, back, back, back, back, back}
		};
		PapertTurtle papertTurtle = new PapertTurtle(image);
		int[][] contour = papertTurtle.execute();
		GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(contour);
		
		for (int i = 0; i < grayscaleImageHelper.lineLength(); i++) {
			System.out.print("[");
			String result = "";
			for (int ii = 0; ii < grayscaleImageHelper.columnLength(); ii++) {
				result += String.format("%3d, ", contour[i][ii]);
			}
			result = result.substring(0, result.length() - 2);
			System.out.println(result + "]");
		}
		
		assertArrayEquals(new int[]{back, back, back, back, back, back, back}, contour[0]);
		assertArrayEquals(new int[]{back, back, form, back, back, back, back}, contour[1]);
		assertArrayEquals(new int[]{back, form, form, form, back, form, back}, contour[2]);
		assertArrayEquals(new int[]{back, form, back, form, back, form, back}, contour[3]);
		assertArrayEquals(new int[]{back, form, form, form, back, form, back}, contour[4]);
		assertArrayEquals(new int[]{back, back, form, back, back, form, back}, contour[5]);
		assertArrayEquals(new int[]{back, back, back, back, back, back, back}, contour[6]);
	}
}
