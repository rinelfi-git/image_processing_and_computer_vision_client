package emit.ipcv.engines.localProcessing.linear.discreteCovolution;

import emit.ipcv.engines.localProcessing.linear.DiscreteCovolution;

public class SobelFilter2 extends DiscreteCovolution {
	public SobelFilter2(int[][] image) {
		super(image);
		initFilter();
	}
	
	@Override
	protected void initFilter() {
		filter = new float[][]{
			{1f, 2f, 1f},
			{0f, 0f, 0f},
			{-1f, -2f, -1f}
		};
	}
}
