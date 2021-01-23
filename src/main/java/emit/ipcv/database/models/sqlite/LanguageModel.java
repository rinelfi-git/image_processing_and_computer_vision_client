/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.database.models.sqlite;

import emit.ipcv.database.dao.DaoFactory;
import emit.ipcv.database.dao.entites.DBLanguage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import emit.ipcv.database.dao.LanguageDao;

/**
 *
 * @author rinelfi
 */
public class LanguageModel implements LanguageDao {

  private DaoFactory daoFactory;

  public LanguageModel(DaoFactory daoFactory) {
    this.daoFactory = daoFactory;
  }

  @Override
  public List<DBLanguage> get() {
    List<DBLanguage> get = new ArrayList<>();
    String query = "SELECT * FROM langage";
    Connection connection = null;
    ResultSet resultSet = null;
    PreparedStatement statement = null;
    try {
      connection = daoFactory.getConnection();
      statement = connection.prepareStatement(query);
      resultSet = statement.executeQuery();
      while (resultSet.next()) {
        get.add(new DBLanguage(resultSet.getString("lang_code"), resultSet.getString("lang_country"), resultSet.getString("lang_variant")).setId(resultSet.getInt("lang_id")));
      }
    } catch (SQLException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException ex) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
      }
    }
    return get;
  }

  @Override
  public DBLanguage get(String language, String country, String variant) {
    DBLanguage get = null;
    String query = "SELECT lang_id FROM langage WHERE "
      + "lang_code = ? and "
      + "lang_country = ? and "
      + "lang_variant = ?";
    Connection connection = null;
    ResultSet resultSet = null;
    PreparedStatement statement = null;
    try {
      connection = daoFactory.getConnection();
      statement = connection.prepareStatement(query);
      statement.setString(1, language);
      statement.setString(2, country);
      statement.setString(3, variant);
      resultSet = statement.executeQuery();
      if (resultSet.next()) {
        get = new DBLanguage(language, country, variant).setId(resultSet.getInt("lang_id"));
      }
    } catch (SQLException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        if (resultSet != null) {
          resultSet.close();
        }
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException ex) {
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
      }
    }
    return get;
  }

}
