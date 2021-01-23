package emit.ipcv.engines.localProcessing.linear.discreteCovolution;

import emit.ipcv.engines.localProcessing.linear.DiscreteCovolution;

public class CustomFilter3 extends DiscreteCovolution {
	public CustomFilter3(int[][] image) {
		super(image);
		this.initFilter();
	}
	
	@Override
	protected void initFilter() {
		this.filter = new float[][]{
			{1f, 1f, 1f},
			{1f, 1f, 1f},
			{1f, 1f, 1f}
		};
	}
}
