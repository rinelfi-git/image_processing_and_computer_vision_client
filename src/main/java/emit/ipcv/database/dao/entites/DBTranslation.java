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
public class DBTranslation {

  private DBLanguage langage;
  private DBSentence phrase;
  private String contenu;

  public DBLanguage getLangage() {
    return langage;
  }

  public DBTranslation(DBLanguage langage, DBSentence phrase, String contenu) {
    this.langage = langage;
    this.phrase = phrase;
    this.contenu = contenu;
  }
public DBTranslation(DBLanguage langage, String phrase, String contenu) {
    this.langage = langage;
    this.phrase = new DBSentence(phrase);
    this.contenu = contenu;
  }
  public void setLangage(DBLanguage langage) {
    this.langage = langage;
  }

  public DBSentence getPhrase() {
    return phrase;
  }

  public void setPhrase(DBSentence phrase) {
    this.phrase = phrase;
  }

  public void setContenu(String contenu) {
    this.contenu = contenu;
  }

  @Override
  public String toString() {
    return this.contenu;
  }
}
