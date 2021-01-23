/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.database.dao;

import emit.ipcv.database.dao.entites.DBLanguage;
import emit.ipcv.database.dao.entites.DBSetting;

/**
 *
 * @author rinelfi
 */
public interface SettingDao {
  boolean languageDefined();
  void setLanguage(int language);
  DBLanguage getLanguage();
  DBSetting loadSetting();
	void changeEngineDecoder(boolean useLocalHardware, String address, int port);
}
