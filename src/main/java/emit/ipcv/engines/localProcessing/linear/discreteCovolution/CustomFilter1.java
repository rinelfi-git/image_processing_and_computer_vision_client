package emit.ipcv.engines.localProcessing.linear.discreteCovolution;

import emit.ipcv.engines.localProcessing.linear.DiscreteCovolution;

public class CustomFilter1 extends DiscreteCovolution {
	public CustomFilter1(int[][] image) {
		super(image);
		this.initFilter();
	}
	
	@Override
	protected void initFilter() {
		filter = new float[][]{
			{0.5f, 0.5f, 1f},
			{0.5f, 0f, -0.5f},
			{0f, -0.5f, -1f}
		};
	}
}
