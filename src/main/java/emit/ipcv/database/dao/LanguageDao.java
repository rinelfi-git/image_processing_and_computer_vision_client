/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.database.dao;

import emit.ipcv.database.dao.entites.DBLanguage;
import java.util.List;

/**
 *
 * @author rinelfi
 */
public interface LanguageDao {
  List<DBLanguage> get();
  DBLanguage get(String language, String country, String variant);
}
