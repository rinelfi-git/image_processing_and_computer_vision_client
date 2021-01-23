/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.utils;

import emit.ipcv.utils.colors.RGBA;

import java.io.Serializable;

/**
 *
 * @author rinelfi
 */
public class ImageProcessingProjectFile implements Serializable {

  private RGBA[][] image;
  private String imageFileExtension;
  private int imageWidth, imageHeight, imageType;

  public RGBA[][] getImage() {
    return image;
  }

  public void setImage(RGBA[][] image) {
    this.image = image;
  }

  public int getImageWidth() {
    return imageWidth;
  }

  public void setImageWidth(int imageWidth) {
    this.imageWidth = imageWidth;
  }

  public int getImageHeight() {
    return imageHeight;
  }

  public void setImageHeight(int imageHeight) {
    this.imageHeight = imageHeight;
  }

  public int getImageType() {
    return imageType;
  }

  public void setImageType(int imageType) {
    this.imageType = imageType;
  }
  
  public String getImageFileExtension() {
    return imageFileExtension;
  }
  
  public void setImageFileExtension(String imageFileExtension) {
    this.imageFileExtension = imageFileExtension;
  }
}
