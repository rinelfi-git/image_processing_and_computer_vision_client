/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.database.models.sqlite;

import emit.ipcv.database.dao.DaoFactory;
import emit.ipcv.database.dao.TranslationDao;
import emit.ipcv.database.dao.entites.DBLanguage;
import emit.ipcv.database.dao.entites.DBSentence;
import emit.ipcv.database.dao.entites.DBTranslation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rinelfi
 */
public class TranslationModel implements TranslationDao {

  private DaoFactory daoFactory;
  private String[] tables;

  public TranslationModel(DaoFactory daoFactory) {
    this.daoFactory = daoFactory;
    tables = new String[]{"translation", "language"};
  }

  @Override
  public DBTranslation get(DBLanguage language, DBSentence sentence) {
    DBTranslation get = null;
    String query = "SELECT * FROM " + tables[0] + " where tran_language = ? and tran_sentence = ?";
    Connection connection = null;
    ResultSet resultSet = null;
    PreparedStatement statement = null;
    try {
      connection = daoFactory.getConnection();
      statement = connection.prepareStatement(query);
      statement.setInt(1, language.getId());
      statement.setString(2, sentence.toString());
      resultSet = statement.executeQuery();
      while (resultSet.next()) {
        get = new DBTranslation(language, resultSet.getString("tran_sentence"), resultSet.getString("tran_content"));
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
  public List<DBTranslation> get(DBLanguage language) {
    List<DBTranslation> get = new ArrayList<>();
    String query = "SELECT * FROM " + tables[0] + " WHERE tran_language = ?";
    Connection connection = null;
    ResultSet resultSet = null;
    PreparedStatement statement = null;
    try {
      connection = daoFactory.getConnection();
      statement = connection.prepareStatement(query);
      statement.setInt(1, language.getId());
      resultSet = statement.executeQuery();
      while (resultSet.next()) {
        get.add(new DBTranslation(language, resultSet.getString("tran_sentence"), resultSet.getString("tran_content")));
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
  public DBTranslation get(DBLanguage language, String sentence) {
    DBTranslation get = null;
    String query = "SELECT tran_content FROM " + tables[0] + " INNER JOIN "  + tables[1] + " ON tran_language = lang_id WHERE "
      + "lang_code = ? and "
      + "lang_country = ? and "
      + "lang_variant = ? and "
      + "tran_sentence = ?";
    Connection connection = null;
    ResultSet resultSet = null;
    PreparedStatement statement = null;
    try {
      connection = daoFactory.getConnection();
      statement = connection.prepareStatement(query);
      statement.setString(1, language.getCode());
      statement.setString(2, language.getCountry());
      statement.setString(3, language.getVariant());
      statement.setString(4, sentence);
      resultSet = statement.executeQuery();
      if (resultSet.next()) {
        get = new DBTranslation(language, sentence, resultSet.getString("tran_content"));
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
