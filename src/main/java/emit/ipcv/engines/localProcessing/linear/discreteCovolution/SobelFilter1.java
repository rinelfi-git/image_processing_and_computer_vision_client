package emit.ipcv.engines.localProcessing.linear.discreteCovolution;

import emit.ipcv.engines.localProcessing.linear.DiscreteCovolution;

public class SobelFilter1 extends DiscreteCovolution {
	public SobelFilter1(int[][] image) {
		super(image);
		this.initFilter();
	}
	
	@Override
	protected void initFilter() {
		filter = new float[][]{
			{-1f, 0f, 1f},
			{-2f, 0f, 2f},
			{-1f, 0f, 1f}
		};
	}
	
}
