package emit.ipcv.engines.localProcessing.linear.discreteCovolution;

import emit.ipcv.engines.localProcessing.linear.DiscreteCovolution;

public class RehausseurFilter extends DiscreteCovolution {
	public RehausseurFilter(int[][] image) {
		super(image);
		this.initFilter();
	}
	
	@Override
	protected void initFilter() {
		this.filter = new float[][]{
			{0f, -1f, 0f},
			{-1f, 5f, -1f},
			{0f, -1f, 0f}
		};
	}
}
