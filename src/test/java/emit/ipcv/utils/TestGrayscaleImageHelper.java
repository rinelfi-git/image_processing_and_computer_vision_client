package emit.ipcv.utils;

import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestGrayscaleImageHelper {
	
	@Test
	public void testTurnLeft() {
		final int back = 255, form = 0;
		int[][] image = new int[][]{
			{back, back, back, back, back, back},
			{back, back, form, form, back, back},
			{back, form, form, form, form, back},
			{back, form, form, form, form, back},
			{back, back, form, form, form, back},
			{back, back, back, back, back, back}
		};
		GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
		System.out.println();
		MatrixElement<Integer>[] points = new MatrixElement[]{
			grayscaleImageHelper.turnLeft(1, 2, Const.RIGHT_DIRECTION),
			grayscaleImageHelper.turnLeft(2, 2, Const.RIGHT_DIRECTION),
			grayscaleImageHelper.turnLeft(1, 1, Const.BOTTOM_DIRECTION)
		};
		assertEquals(back, (int) points[0].getContent());
		assertEquals(form, (int) points[1].getContent());
		assertEquals(form, (int) points[2].getContent());
	}
	
	@Test
	public void testTurnRight() {
		final int back = 255, form = 0;
		int[][] image = new int[][]{
			// it
			{back, back, back, back, back, back},
			{back, back, form, form, back, back},
			{back, form, form, form, form, back},
			{back, form, form, form, form, back},
			{back, back, form, form, form, back},
			{back, back, back, back, back, back}
		};
		GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
		System.out.println();
		MatrixElement<Integer>[] points = new MatrixElement[]{
			grayscaleImageHelper.turnRight(1, 2, Const.RIGHT_DIRECTION),
			grayscaleImageHelper.turnRight(2, 2, Const.RIGHT_DIRECTION),
			grayscaleImageHelper.turnRight(1, 1, Const.BOTTOM_DIRECTION),
		};
		assertEquals(form, (int) points[0].getContent());
		assertEquals(form, (int) points[1].getContent());
		assertEquals(back, (int) points[2].getContent());
	}
}
