package emit.ipcv.utils;

import emit.ipcv.utils.colors.RGBA;

import java.awt.Color;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class ImageLoader {
  
  private BufferedImage bufferedImage;
  private RGBA[][] originalColor;
  private String imageOriginalExtension;
  
  public ImageLoader(File localisation) throws IOException {
    this.bufferedImage = ImageIO.read(localisation);
  }
  
  public ImageLoader(String localisation) throws IOException {
    this.bufferedImage = ImageIO.read(new File(localisation));
  }
  
  public ImageLoader(BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
  }
  
  public RGBA[][] getOriginalColor() {
    if(originalColor != null) return originalColor;
    final int ligne = bufferedImage.getWidth(), colonne = bufferedImage.getHeight();
    originalColor = new RGBA[ligne][colonne];
    for (int x = 0; x < ligne; x++) {
      for (int y = 0; y < colonne; y++) {
        Color color = new Color(bufferedImage.getRGB(x, y), true);
        originalColor[x][y] = new RGBA(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
      }
    }
    return originalColor;
  }
  
  public BufferedImage getBufferedImage() {
    return this.bufferedImage;
  }
  
  public void setOriginalColor(RGBA[][] image) {
    this.originalColor = image;
  }
  
  public String getImageOriginalExtension() {
    return imageOriginalExtension;
  }
  
  public void setImageOriginalExtension(String imageOriginalExtension) {
    this.imageOriginalExtension = imageOriginalExtension.toLowerCase();
  }
}
