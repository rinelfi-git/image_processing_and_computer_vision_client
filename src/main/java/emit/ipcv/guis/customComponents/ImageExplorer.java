/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.guis.customComponents;

import emit.ipcv.database.dao.TranslationDao;
import emit.ipcv.database.dao.entites.DBLanguage;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author rinelfi
 */
public class ImageExplorer extends JFileChooser {

  private List<FileNameExtensionFilter> authorizedFileFilter,
    saveExtensionFilter;
  private String[] saveExtentions;
  private String location, imageOriginalExtension;
  private TranslationDao translation;
  private DBLanguage language;
  
  {
    this.authorizedFileFilter = this.initAuthorizedExtensions();
    this.saveExtensionFilter = this.initSaveExtension();
    this.saveExtentions = new String[]{
      "jpg",
      "jpeg",
      "png",
      "bpm",
      "gif"
    };
  }

  public ImageExplorer() {
    this.location = System.getProperty("user.home");
  }

  public ImageExplorer(String location) {
    this.authorizedFileFilter = this.initAuthorizedExtensions();
    this.location = location;
  }

  public ImageExplorer setTranslation(TranslationDao translation) {
    this.translation = translation;
    return this;
  }

  public ImageExplorer setLanguage(DBLanguage language) {
    this.language = language;
    return this;
  }

  public int open(Component parentComponent) {
    this.authorizedFileFilter.forEach((extension) -> {
      addChoosableFileFilter(extension);
    });
    setAcceptAllFileFilterUsed(false);
    setFileFilter(this.authorizedFileFilter.get(0));
    setFileSelectionMode(JFileChooser.FILES_ONLY);
    setDialogTitle(translation.get(language, "open_an_image").toString());
    setCurrentDirectory(new File(location));
    setApproveButtonText(translation.get(language, "open").toString());
    return showOpenDialog(parentComponent);
  }

  public int saveAs(Component parentComponent) {
    this.saveExtensionFilter.forEach((extension) -> {
      addChoosableFileFilter(extension);
    });
    setAcceptAllFileFilterUsed(false);
    setFileFilter(this.saveExtensionFilter.get(0));
    if(this.imageOriginalExtension != null) {
      for(int i = 0; i < this.saveExtentions.length; i++) {
        if(this.imageOriginalExtension.equalsIgnoreCase(this.saveExtentions[i])) {
          setFileFilter(this.saveExtensionFilter.get(i));
        }
      }
    }
    setFileSelectionMode(JFileChooser.FILES_ONLY);
    setDialogTitle(translation.get(language, "save_image").toString());
    setCurrentDirectory(new File(location));
    setApproveButtonText(translation.get(language, "save").toString());
    return showOpenDialog(parentComponent);
  }

  private List<FileNameExtensionFilter> initAuthorizedExtensions() {
    List<FileNameExtensionFilter> ext = new ArrayList<>();
    ext.add(new FileNameExtensionFilter("images (*.png, *.jpg, *.jpeg, *.bmp, *.gif)", "png", "PNG", "jpg", "jpeg", "JPG", "JPEG", "bmp", "BMP", "gif", "GIF"));
    ext.add(new FileNameExtensionFilter("PNG (*.png)", "png", "PNG"));
    ext.add(new FileNameExtensionFilter("JPG (*.jpg)", "jpg", "JPG"));
    ext.add(new FileNameExtensionFilter("JPEG (*.jpeg)", "jpg", "jpeg", "JPG", "JPEG"));
    ext.add(new FileNameExtensionFilter("Bitmaps (*.bmp)", "bmp", "BMP"));
    ext.add(new FileNameExtensionFilter("Gifs (*.gif)", "gif", "GIF"));
    return ext;
  }

  private List<FileNameExtensionFilter> initSaveExtension() {
    List<FileNameExtensionFilter> ext = new ArrayList<>();
    ext.add(new FileNameExtensionFilter("(*.jpg)", "jpg", "JPG"));
    ext.add(new FileNameExtensionFilter("(*.jpeg)", "jpeg", "JPEG"));
    ext.add(new FileNameExtensionFilter("(*.png)", "png", "PNG"));
    ext.add(new FileNameExtensionFilter("(*.bmp)", "bmp", "BMP"));
    ext.add(new FileNameExtensionFilter("(*.gif)", "gif", "GIF"));
    return ext;
  }

  public String[] getSaveExtensions() {
    return this.saveExtentions;
  }
	
	public void setOutputExtension(String imageOriginalExtension) {
    this.imageOriginalExtension = imageOriginalExtension;
	}
}
