package emit.ipcv.engines.localProcessing.linear.discreteCovolution;

import emit.ipcv.engines.localProcessing.linear.DiscreteCovolution;

public class LaplacianFilter extends DiscreteCovolution {
	public LaplacianFilter(int[][] image) {
		super(image);
		this.initFilter();
	}
	
	@Override
	protected void initFilter() {
		this.filter = new float[][]{
			{0f, 1f, 0f},
			{1f, -4f, 1f},
			{0f, 1f, 0f}
		};
	}
}
