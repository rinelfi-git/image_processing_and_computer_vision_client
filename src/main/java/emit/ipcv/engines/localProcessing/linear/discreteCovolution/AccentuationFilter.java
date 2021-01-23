package emit.ipcv.engines.localProcessing.linear.discreteCovolution;

import emit.ipcv.engines.localProcessing.linear.DiscreteCovolution;

public class AccentuationFilter extends DiscreteCovolution {
	public AccentuationFilter(int[][] image) {
		super(image);
		this.initFilter();
	}
	
	@Override
	protected void initFilter() {
		this.filter = new float[][]{
			{0f, -0.5f, 0f},
			{-0.5f, 3f, -0.5f},
			{0f, -0.5f, 0f}
		};
	}
}
