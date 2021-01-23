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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author rinelfi
 */
public class ProjectExplorer extends JFileChooser {

  private List<FileNameExtensionFilter> authorizedFileFilter, saveExtensionFilter;
  private String[] saveExtensionArray;
  private String location;
  private TranslationDao translation;
  private DBLanguage language;

  {
    this.authorizedFileFilter = this.initAuthorizedFileFilter();
    this.saveExtensionFilter = this.initSaveExtensionFilter();
    this.saveExtensionArray = new String[]{"imgproc"};
  }

  public ProjectExplorer() {
    this.location = System.getProperty("user.home");
  }

  public ProjectExplorer(String location) {
    this.authorizedFileFilter = this.initAuthorizedFileFilter();
    this.location = location;
  }

  public ProjectExplorer setTranslation(TranslationDao translation) {
    this.translation = translation;
    return this;
  }

  public ProjectExplorer setLanguage(DBLanguage language) {
    this.language = language;
    return this;
  }

  public int open(Component composantParent) {
    this.authorizedFileFilter.forEach((extension) -> {
      addChoosableFileFilter(extension);
    });
    setAcceptAllFileFilterUsed(false);
    setFileFilter(this.authorizedFileFilter.get(0));
    setFileSelectionMode(JFileChooser.FILES_ONLY);
    setDialogTitle(translation.get(language, "open_project").toString());
    setCurrentDirectory(new File(location));
    setApproveButtonText(translation.get(language, "open").toString());
    return showOpenDialog(composantParent);
  }

  public int saveAs(Component composantParent) {
    this.saveExtensionFilter.forEach((extension) -> {
      addChoosableFileFilter(extension);
    });
    setAcceptAllFileFilterUsed(false);
    setFileFilter(this.saveExtensionFilter.get(0));
    setFileSelectionMode(JFileChooser.FILES_ONLY);
    setDialogTitle(translation.get(language, "save").toString());
    setCurrentDirectory(new File(location));
    if (!Files.isDirectory(Paths.get(location))) {
      setSelectedFile(new File(location));
    }
    setApproveButtonText(translation.get(language, "save").toString());
    return showOpenDialog(composantParent);
  }

  private List<FileNameExtensionFilter> initAuthorizedFileFilter() {
    List<FileNameExtensionFilter> ext = new ArrayList<>();
    ext.add(new FileNameExtensionFilter("(*.imgproc)", "imgproc", "IMGPROC"));
    return ext;
  }

  private List<FileNameExtensionFilter> initSaveExtensionFilter() {
    List<FileNameExtensionFilter> ext = new ArrayList<>();
    ext.add(new FileNameExtensionFilter("(*.imgproc)", "imgproc", "IMGPROC"));
    return ext;
  }

  public String[] getExtensionEnregistrement() {
    return this.saveExtensionArray;
  }
}
