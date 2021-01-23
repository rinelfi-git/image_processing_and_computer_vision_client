/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.database.dao;

import emit.ipcv.database.dao.entites.DBProjectHistory;
import java.util.List;

/**
 *
 * @author rinelfi
 */
public interface ProjectHistoryDao {

  List<DBProjectHistory> selectionnerTout();

  boolean deleteAll();

  boolean effacer(String localisation);

  boolean addProject(DBProjectHistory projet);

  boolean saveProject(String localisation);
}
