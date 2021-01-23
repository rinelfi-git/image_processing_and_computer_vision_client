/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.database.models.sqlite;

import emit.ipcv.database.dao.DaoFactory;
import emit.ipcv.database.dao.ProjectHistoryDao;
import emit.ipcv.database.dao.entites.DBProjectHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rinelfi
 */
public class ProjectHistoryModel implements ProjectHistoryDao {
	
	private final DaoFactory daoFactory;
	private String[] tables;
	
	public ProjectHistoryModel(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
		tables = new String[]{"recently_project"};
	}
	
	@Override
	public List<DBProjectHistory> selectionnerTout() {
		List<DBProjectHistory> sortie = new ArrayList<>();
		String query = "SELECT * FROM "+ tables[0];
		Connection connection = null;
		ResultSet resultSet = null;
		Statement statement = null;
		try {
			connection = daoFactory.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				sortie.add(new DBProjectHistory(resultSet.getString("proj_name"), resultSet.getString("proj_location")));
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
		return sortie;
	}
	
	@Override
	public boolean deleteAll() {
		boolean sortie = false;
		String query = "DELETE FROM " + tables[0];
		Connection connection = null;
		Statement statement = null;
		try {
			connection = daoFactory.getConnection();
			statement = connection.createStatement();
			sortie = statement.executeUpdate(query) > 0;
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
		return sortie;
	}
	
	@Override
	public boolean effacer(String localisation) {
		boolean sortie = false;
		String query = "DELETE FROM " + tables[0] + " WHERE proj_location=?";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = daoFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, localisation);
			sortie = statement.executeUpdate() > 0;
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
		return sortie;
	}
	
	@Override
	public boolean addProject(DBProjectHistory projet) {
		boolean sortie = false;
		String query = "INSERT INTO " + tables[0] + "(proj_name, proj_location) VALUES(?, ?)";
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = daoFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, projet.getName());
			statement.setString(2, projet.getLocation());
			sortie = statement.executeUpdate() > 0;
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
		return sortie;
	}
	
	@Override
	public boolean saveProject(String localisation) {
		boolean sortie = false;
		String query = "SELECT count(*) AS count FROM " + tables[0] + " WHERE proj_location=?";
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = daoFactory.getConnection();
			statement = connection.prepareStatement(query);
			statement.setString(1, localisation);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				sortie = resultSet.getInt("count") > 0;
			}
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
		return sortie;
	}
	
}
