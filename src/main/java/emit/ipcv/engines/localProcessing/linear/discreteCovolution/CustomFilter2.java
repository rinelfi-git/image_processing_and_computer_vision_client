package emit.ipcv.engines.localProcessing.linear.discreteCovolution;

import emit.ipcv.engines.localProcessing.linear.DiscreteCovolution;

public class CustomFilter2 extends DiscreteCovolution {
	public CustomFilter2(int[][] image) {
		super(image);
		initFilter();
	}
	
	@Override
	protected void initFilter() {
		filter = new float[][]{
			{-0.5f, -0.5f, 0.5f},
			{-1f, 0f, 1f},
			{-0.5f, -0.5f, 0.5f}
		};
	}
}
