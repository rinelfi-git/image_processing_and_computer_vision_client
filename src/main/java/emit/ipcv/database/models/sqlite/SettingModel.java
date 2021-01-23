/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.database.models.sqlite;

import emit.ipcv.database.dao.DaoFactory;
import emit.ipcv.database.dao.SettingDao;
import emit.ipcv.database.dao.entites.DBLanguage;
import emit.ipcv.database.dao.entites.DBSetting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rinelfi
 */
public class SettingModel implements SettingDao {

  DaoFactory daoFactory;
  String[] tables;

  public SettingModel(DaoFactory daoFactory) {
    this.daoFactory = daoFactory;
    tables = new String[]{"settings", "language"};
  }

  @Override
  public boolean languageDefined() {
    boolean languageDefined = false;
    String query = "SELECT COUNT(sett_language) AS count FROM " + tables[0];
    Connection connection = null;
    ResultSet resultSet = null;
    Statement statement = null;
    try {
      connection = daoFactory.getConnection();
      statement = connection.createStatement();
      resultSet = statement.executeQuery(query);
      if (resultSet.next()) {
        languageDefined = resultSet.getInt("count") > 0;
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
    return languageDefined;
  }

  @Override
  public void setLanguage(int language) {
    String query = "UPDATE " + tables[0] + " SET sett_language = ?";
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = daoFactory.getConnection();
      statement = connection.prepareStatement(query);
      statement.setInt(1, language);
      statement.executeUpdate();
    } catch (SQLException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
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
  }

  @Override
  public DBLanguage getLanguage() {
    DBLanguage get = null;
    String query = "SELECT lang_id, lang_code, lang_country, lang_variant FROM " + tables[1] + " INNER JOIN " + tables[0] + " ON lang_id=sett_language";
    Connection connection = null;
    ResultSet resultSet = null;
    Statement statement = null;
    try {
      connection = daoFactory.getConnection();
      statement = connection.createStatement();
      resultSet = statement.executeQuery(query);
      if (resultSet.next()) {
        get = new DBLanguage(
          resultSet.getString("lang_code"),
          resultSet.getString("lang_country"),
          resultSet.getString("lang_variant")
        ).setId(resultSet.getInt("lang_id"));
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
  public DBSetting loadSetting() {
    DBSetting setting = null;
    String query = "SELECT * FROM " + tables[0];
    Connection connection = null;
    ResultSet resultSet = null;
    Statement statement = null;
    try {
      connection = daoFactory.getConnection();
      statement = connection.createStatement();
      resultSet = statement.executeQuery(query);
      if (resultSet.next()) {
        setting = new DBSetting(
          resultSet.getInt("sett_language"),
          resultSet.getInt("sett_remote_port_address"),
          resultSet.getString("sett_remote_ip_address"),
          Boolean.valueOf(resultSet.getString("sett_use_local_hardware"))
        );
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
    return setting;
  }
  
  @Override
  public void changeEngineDecoder(boolean useLocalHardware, String address, int port) {
    String query = "UPDATE " + tables[0] + " set sett_use_local_hardware = ?, sett_remote_ip_address = ?, sett_remote_port_address = ?";
    Connection connection = null;
    PreparedStatement statement = null;
    try {
      connection = daoFactory.getConnection();
      statement = connection.prepareStatement(query);
      statement.setString(1, String.valueOf(useLocalHardware));
      statement.setString(2, address);
      statement.setInt(3, port);
      statement.executeUpdate();
    } catch (SQLException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
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
  }
}
