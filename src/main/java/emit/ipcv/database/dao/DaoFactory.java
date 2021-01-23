/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.database.dao;

import emit.ipcv.database.models.sqlite.ProjectHistoryModel;
import emit.ipcv.database.models.sqlite.LanguageModel;
import emit.ipcv.database.models.sqlite.SettingModel;
import emit.ipcv.database.models.sqlite.TranslationModel;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rinelfi
 */
public class DaoFactory {
	
	String database;
	
	private DaoFactory(String database) throws URISyntaxException, UnsupportedEncodingException {
		File destinationBaseDeDonnee = null;
		File dossier = new File(URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath(), "UTF-8"));
		if (dossier.isDirectory()) {
			destinationBaseDeDonnee = new File(dossier.getAbsolutePath() + File.separatorChar + database);
		} else {
			destinationBaseDeDonnee = new File(dossier.getParent() + File.separatorChar + database);
		}
		if (!destinationBaseDeDonnee.exists()) {
			try {
				Files.copy(DaoFactory.class.getResourceAsStream("/emit/ipcv/database/" + database), destinationBaseDeDonnee.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ex) {
				Logger.getLogger(DaoFactory.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		this.database = destinationBaseDeDonnee.getAbsolutePath();
	}
	
	public static DaoFactory getInstance() {
		try {
			Class.forName("org.sqlite.JDBC");
			return new DaoFactory("aivo.sqlite");
		} catch (ClassNotFoundException | URISyntaxException | UnsupportedEncodingException ex) {
			Logger.getLogger(DaoFactory.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:sqlite:" + database);
	}
	
	public ProjectHistoryDao getProjectHistory() {return new ProjectHistoryModel(this);}
	
	public TranslationDao getTranslationModel() {
		return new TranslationModel(this);
	}
	
	public LanguageDao getLanguageModel() {
		return new LanguageModel(this);
	}
	
	public SettingDao getSettingModel() {
		return new SettingModel(this);
	}
}
