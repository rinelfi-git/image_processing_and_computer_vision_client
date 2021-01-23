/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.database.dao.entites;

/**
 *
 * @author rinelfi
 */
public class DBSentence {

  private String original;

  public DBSentence() {
  }

  public DBSentence(String original) {
    this.original = original;
  }

  public String getOriginal() {
    return original;
  }

  public void setOriginal(String original) {
    this.original = original;
  }

}
