package emit.ipcv.utils.colors;

import java.io.Serializable;

public class RGBA implements Serializable{
	private int alpha, red, green, blue;
	
	public int getAlpha() {
		return alpha;
	}
	
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public int getRed() {
		return red;
	}
	
	public void setRed(int red) {
		this.red = red;
	}
	
	public int getGreen() {
		return green;
	}
	
	public void setGreen(int green) {
		this.green = green;
	}
	
	public int getBlue() {
		return blue;
	}
	
	public void setBlue(int blue) {
		this.blue = blue;
	}
	
	public RGBA(int alpha, int red, int green, int blue) {
		this.alpha = alpha;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public int max() {
		int sortie = red;
		sortie = blue > sortie ? blue : sortie;
		sortie = green > sortie ? green : sortie;
		return sortie;
	}
	
	public int min() {
		int sortie = red;
		sortie = blue < sortie ? blue : sortie;
		sortie = green < sortie ? green : sortie;
		return sortie;
	}
	
	public RGBA appendBlue() {
		this.blue++;
		return this;
	}
	
	public RGBA appendBlue(int pixel) {
		this.blue += pixel;
		return this;
	}
	
	public RGBA appendGreen() {
		this.green++;
		return this;
	}
	
	public RGBA appendGreen(int pixel) {
		this.green += pixel;
		return this;
	}
	
	public RGBA appendRed() {
		this.red++;
		return this;
	}
	
	public RGBA appendRed(int pixel) {
		this.red += pixel;
		return this;
	}
}
