package emit.ipcv.utils;

public class MatrixElement<C> {
	private int x, y;
	private C content;
	
	public int getX() {
		return x;
	}
	
	public MatrixElement<C> setX(int x) {
		this.x = x;
		return this;
	}
	
	public int getY() {
		return y;
	}
	
	public MatrixElement<C> setY(int y) {
		this.y = y;
		return this;
	}
	
	public C getContent() {
		return content;
	}
	
	public MatrixElement<C> setContent(C content) {
		this.content = content;
		return this;
	}
	
	public <T> boolean equals(MatrixElement<T> point) {
		return point.x == this.x && point.y == y && point.content.equals(content);
	}
}
