/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.database.dao;

import emit.ipcv.database.dao.entites.DBLanguage;
import emit.ipcv.database.dao.entites.DBSentence;
import emit.ipcv.database.dao.entites.DBTranslation;
import java.util.List;

/**
 *
 * @author rinelfi
 */
public interface TranslationDao {
  DBTranslation get(DBLanguage language, String phrase);
  DBTranslation get(DBLanguage language, DBSentence phrase);
  List<DBTranslation> get(DBLanguage language);
}
