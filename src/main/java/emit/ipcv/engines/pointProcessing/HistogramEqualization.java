package emit.ipcv.engines.pointProcessing;

import emit.ipcv.utils.Const;
import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;

public class HistogramEqualization {
	
	private int[][] image;
	
	public HistogramEqualization(int[][] image) {
		this.image = image;
	}
	
	public int[] execute() {
		GrayscaleImageHelper grayscaleImageHelper = new GrayscaleImageHelper(image);
		int[] hci = grayscaleImageHelper.getCumulativeHistogram(),
			operation = new int[Const.COLOR_MAX_LEVEL],
			hciPrime = new int[Const.COLOR_MAX_LEVEL];
		int imageDefinition = hci[Const.COLOR_MAX_LEVEL - 1], hPrimeI = imageDefinition / Const.COLOR_MAX_LEVEL, hPrimeCumulle = 0;
		for (int i = 0; i < Const.COLOR_MAX_LEVEL; i++) {
			hPrimeCumulle += hPrimeI;
			hciPrime[i] += hPrimeCumulle;
		}
		for (int i = 0; i < Const.COLOR_MAX_LEVEL; i++) {
			int color = 0;
			for (int j = 0; j < Const.COLOR_MAX_LEVEL; j++) {
				int min = hciPrime[j], max = j < Const.COLOR_MAX_LEVEL - 1 ? hciPrime[j + 1] : hciPrime[j];
				if (j == Const.COLOR_MAX_LEVEL - 1) {
					color = j;
					break;
				} else if (max - hci[i] >= 0) {
					int ecart[] = new int[]{Math.abs(hci[i] - min), Math.abs(max - hci[i])};
					if (ecart[0] == 0 || ecart[0] < ecart[1]) {
						color = j;
						break;
					} else {
						color = j + 1;
						break;
					}
				}
			}
			operation[i] = color;
		}
		return operation;
	}
}
