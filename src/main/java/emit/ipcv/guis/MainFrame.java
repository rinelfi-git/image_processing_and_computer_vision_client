/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.guis;

import emit.ipcv.dataFormat.DataPacket;
import emit.ipcv.database.dao.*;
import emit.ipcv.database.dao.entites.DBLanguage;
import emit.ipcv.database.dao.entites.DBProjectHistory;
import emit.ipcv.database.dao.entites.DBSetting;
import emit.ipcv.engines.formRecognition.PapertTurtle;
import emit.ipcv.engines.interfaces.Operations;
import emit.ipcv.engines.localProcessing.linear.GaussianFilter;
import emit.ipcv.engines.localProcessing.linear.MiddleFilter;
import emit.ipcv.engines.localProcessing.linear.discreteCovolution.*;
import emit.ipcv.engines.localProcessing.nonLinear.AverageFilter;
import emit.ipcv.engines.localProcessing.nonLinear.ConservativeFilter;
import emit.ipcv.engines.localProcessing.nonLinear.MedianFilter;
import emit.ipcv.engines.operations.AllOrNothing;
import emit.ipcv.engines.operations.Dilation;
import emit.ipcv.engines.operations.Erosion;
import emit.ipcv.engines.pointProcessing.DynamicDisplay;
import emit.ipcv.engines.pointProcessing.HistogramEqualization;
import emit.ipcv.engines.pointProcessing.InvertGrayscale;
import emit.ipcv.engines.pointProcessing.ThresholdingBinarization;
import emit.ipcv.engines.transformation2D.*;
import emit.ipcv.guis.customComponents.ImageExplorer;
import emit.ipcv.guis.customComponents.ImageViewer;
import emit.ipcv.guis.customComponents.ProjectExplorer;
import emit.ipcv.utils.ApplicationHistory;
import emit.ipcv.utils.Const;
import emit.ipcv.utils.ImageLoader;
import emit.ipcv.utils.ImageProcessingProjectFile;
import emit.ipcv.utils.colors.RGBA;
import emit.ipcv.utils.imageHelper.GrayscaleImageHelper;
import emit.ipcv.utils.imageHelper.RgbImageHelper;
import emit.ipcv.utils.thresholding.Otsu;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rinelfi
 */
public class MainFrame extends javax.swing.JFrame {
	
	private String lastLocation;
	private String projectNameLocation;
	private ImageViewer imageViewer;
	private boolean hasChanges, newProject;
	
	private String defaultTitle, currentTitle;
	
	private ApplicationHistory<RGBA[][]> applicationHistory;
	
	private ProjectHistoryDao projectHistoryModel;
	private TranslationDao translationModel;
	private LanguageDao languageModel;
	private SettingDao settingModel;
	private DBLanguage language;
	private DBSetting setting;
	
	/**
	 * Creates new form MainWindow
	 *
	 * @param args arguments of application
	 */
	public MainFrame(String[] args) {
		System.out.println("[INFO] Window components initializations");
		initComponents();
		System.out.println("[INFO] Database connection");
		initConnection();
		System.out.println("[INFO] Loading settings");
		loadSettings();
		initCustomComponents();
		initProjectHistory();
		if (args.length > 0) {
			if (new File(args[0]).isFile()) {
				try {
					openProject(new File(args[0]));
				} catch (IOException ex) {
					openNewImage(new File(args[0]));
				}
			}
		}
		initLanguages();
	}
	
	private void initCustomComponents() {
		disableMenus();
		String defaultFontName = coordinateField.getFont().getName();
		lastLocation = System.getProperty("user.home");
		imageViewer = new ImageViewer();
		imageScreen.add(BorderLayout.CENTER, imageViewer);
		applicationHistory = new ApplicationHistory<>();
		processingBar.setStringPainted(true);
		processingBar.setIndeterminate(true);
		processingBar.setVisible(false);
		processingBar.setFont(new Font(defaultFontName, Font.PLAIN, 13));
		hasChanges = false;
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				closureRequestPerformed();
			}
		});
		List<DBLanguage> languages = languageModel.get();
		languageMenu.removeAll();
		for (DBLanguage oneLanguage : languages) {
			JRadioButtonMenuItem languageRadio = new JRadioButtonMenuItem(oneLanguage.toString());
			languageButtonGroup.add(languageRadio);
			languageRadio.setSelected(oneLanguage.getId() == this.language.getId());
			languageRadio.addActionListener(e -> {
				language = oneLanguage;
				System.out.printf("[INFO] Change the application language to \"%s\"%n", language.toString());
				settingModel.setLanguage(oneLanguage.getId());
				initLanguages();
			});
			languageMenu.add(languageRadio);
		}
		imageViewer.addZoomListener(value -> curretnZoomLab.setText(value + "%"));
	}
	
	private void initConnection() {
		DaoFactory daoFactory = DaoFactory.getInstance();
		assert daoFactory != null;
		projectHistoryModel = Objects.requireNonNull(daoFactory).getProjectHistory();
		translationModel = daoFactory.getTranslationModel();
		languageModel = daoFactory.getLanguageModel();
		settingModel = daoFactory.getSettingModel();
	}
	
	private void newProjectMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectMenuActionPerformed
		newProjectPerformed();
	}//GEN-LAST:event_newProjectMenuActionPerformed
	
	private void exitMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuActionPerformed
		closureRequestPerformed();
	}//GEN-LAST:event_exitMenuActionPerformed
	
	private void verticalSymmetryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verticalSymmetryActionPerformed
		verticalSymmetryPerformed();
	}//GEN-LAST:event_verticalSymmetryActionPerformed
	
	private void horizontalSymmetryMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_horizontalSymmetryMenuActionPerformed
		horizontalSymmetryPerformed();
	}//GEN-LAST:event_horizontalSymmetryMenuActionPerformed
	
	private void invertGrayscaleMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertGrayscaleMenuActionPerformed
		invertColorPerformed();
	}//GEN-LAST:event_invertGrayscaleMenuActionPerformed
	
	private void histogramEqualizationMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_histogramEqualizationMenuActionPerformed
		histogramEqualizationPerformed();
	}//GEN-LAST:event_histogramEqualizationMenuActionPerformed
	
	private void symmetryInTheCenterMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_symmetryInTheCenterMenuActionPerformed
		symmetryInCenterPerformed();
	}//GEN-LAST:event_symmetryInTheCenterMenuActionPerformed
	
	private void homothetyMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homothetyMenuActionPerformed
		openHomothetyPerformed();
	}//GEN-LAST:event_homothetyMenuActionPerformed
	
	private void rotationMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotationMenuActionPerformed
		openRotationPerformed();
	}//GEN-LAST:event_rotationMenuActionPerformed
	
	private void shearMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shearMenuActionPerformed
		openShearPerformed();
	}//GEN-LAST:event_shearMenuActionPerformed
	
	private void dynamicDisplayMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dynamicDisplayMenuActionPerformed
		openDynamicDisplayPerformed();
	}//GEN-LAST:event_dynamicDisplayMenuActionPerformed
	
	private void histogramMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_histogramMenuActionPerformed
		HistogramDialog histogramDialog = new HistogramDialog(this, true).setTranslation(translationModel).setLanguage(language);
		histogramDialog.setHistogram(new GrayscaleImageHelper(new RgbImageHelper(imageViewer.getImageLoader().getOriginalColor()).getGrayscale()).getHistogram());
		histogramDialog.loadPartialChart();
		histogramDialog.loadFullChart();
		System.out.println("[INFO] Open the histogram");
		histogramDialog.setVisible(true);
	}//GEN-LAST:event_histogramMenuActionPerformed
	
	private void undoMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoMenuActionPerformed
		undoPerformed();
	}//GEN-LAST:event_undoMenuActionPerformed
	
	private void redoMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoMenuActionPerformed
		redoPerformed();
	}//GEN-LAST:event_redoMenuActionPerformed
	
	private void cumulativeHistogramMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cumulativeHistogramMenuActionPerformed
		CumulativeHistogramDialog cumulativeHistogramDialog = new CumulativeHistogramDialog(this, true).setTranslation(translationModel).setLanguage(language);
		cumulativeHistogramDialog.setHistogram(new GrayscaleImageHelper(new RgbImageHelper(imageViewer.getImageLoader().getOriginalColor()).getGrayscale()).getCumulativeHistogram());
		cumulativeHistogramDialog.loadPartialChart();
		cumulativeHistogramDialog.loadFullChart();
		System.out.println("[INFO] Open the cumulative histogram");
		cumulativeHistogramDialog.setVisible(true);
	}//GEN-LAST:event_cumulativeHistogramMenuActionPerformed
	
	private void mediumFilteringMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediumFilteringMenuActionPerformed
		mediumFilteringPerformed();
	}//GEN-LAST:event_mediumFilteringMenuActionPerformed
	
	private void gaussianFilteringMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gaussianFilteringMenuActionPerformed
		gaussianFilteringPerformed();
	}//GEN-LAST:event_gaussianFilteringMenuActionPerformed
	
	private void conservativeSmoothingMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conservativeSmoothingMenuActionPerformed
		conservativeSmoothingPerformed();
	}//GEN-LAST:event_conservativeSmoothingMenuActionPerformed
	
	private void medianFilteringMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_medianFilteringMenuActionPerformed
		medianFilteringPerformed();
	}//GEN-LAST:event_medianFilteringMenuActionPerformed
	
	private void fixedThresholdMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedThresholdMenuActionPerformed
		openFixedThresholdPerformed();
	}//GEN-LAST:event_fixedThresholdMenuActionPerformed
	
	private void otsuThresholdMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_otsuThresholdMenuActionPerformed
		otsuThresholding();
	}//GEN-LAST:event_otsuThresholdMenuActionPerformed
	
	private void averageFilteringMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_averageFilteringMenuActionPerformed
		averageFilteringPerformed();
	}//GEN-LAST:event_averageFilteringMenuActionPerformed
	
	private void saveAsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuActionPerformed
		saveAsPerformed();
	}//GEN-LAST:event_saveAsMenuActionPerformed
	
	private void exportImageMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportImageMenuActionPerformed
		exportImagePerformed();
	}//GEN-LAST:event_exportImageMenuActionPerformed
	
	private void saveMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuActionPerformed
		saveOrSaveAsPerformed();
	}//GEN-LAST:event_saveMenuActionPerformed
	
	private void openProjectMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openProjectMenuActionPerformed
		openProjectPerformed();
	}//GEN-LAST:event_openProjectMenuActionPerformed
	
	private void openProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openProjectButtonActionPerformed
		openProjectPerformed();
	}//GEN-LAST:event_openProjectButtonActionPerformed
	
	private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
		saveOrSaveAsPerformed();
	}//GEN-LAST:event_saveButtonActionPerformed
	
	private void newProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectButtonActionPerformed
		newProjectPerformed();
	}//GEN-LAST:event_newProjectButtonActionPerformed
	
	private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
		undoPerformed();
	}//GEN-LAST:event_undoButtonActionPerformed
	
	private void redoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoButtonActionPerformed
		redoPerformed();
	}//GEN-LAST:event_redoButtonActionPerformed
	
	private void zoomInBoutonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInBoutonActionPerformed
		imageViewer.zoomIn();
	}//GEN-LAST:event_zoomInBoutonActionPerformed
	
	private void zoomOutBoutonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutBoutonActionPerformed
		imageViewer.zoomOut();
	}//GEN-LAST:event_zoomOutBoutonActionPerformed
	
	private void fixedZoomComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedZoomComboActionPerformed
		changeUserScale();
	}//GEN-LAST:event_fixedZoomComboActionPerformed
	
	private void erosionMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_erosionMenuActionPerformed
		erosion();
	}//GEN-LAST:event_erosionMenuActionPerformed
	
	private void dilationMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dilationMenuActionPerformed
		dilation();
	}//GEN-LAST:event_dilationMenuActionPerformed
	
	private void openingMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openingMenuActionPerformed
		opening();
	}//GEN-LAST:event_openingMenuActionPerformed
	
	private void topHatOpeningMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topHatOpeningMenuActionPerformed
		thickeningOpeningPerformed();
	}//GEN-LAST:event_topHatOpeningMenuActionPerformed
	
	private void topHatClosureMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_topHatClosureMenuActionPerformed
		thickeningClosurePerformed();
	}//GEN-LAST:event_topHatClosureMenuActionPerformed
	
	private void allOrNothingMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allOrNothingMenuActionPerformed
		allOrNothing();
	}//GEN-LAST:event_allOrNothingMenuActionPerformed
	
	private void papertTurtleMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_papertTurtleMenuActionPerformed
		papertTurtlePerformed();
	}//GEN-LAST:event_papertTurtleMenuActionPerformed
	
	private void thickeningMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thickeningMenuActionPerformed
		thickeningPerformed();
	}//GEN-LAST:event_thickeningMenuActionPerformed
	
	private javax.swing.JMenuItem laplacianFilterMenu;
	private javax.swing.JMenuItem rehausseurMenu;
	private javax.swing.JMenuItem sobelFilter1Menu;
	private javax.swing.JMenuItem sobelFilter2Menu;
	private javax.swing.JMenuItem discreteCovolutionMenu3;
	
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		
		languageButtonGroup = new javax.swing.ButtonGroup();
		rootPanel = new javax.swing.JPanel();
		imageScreen = new javax.swing.JPanel();
		bottomPanel = new javax.swing.JPanel();
		processingBar = new javax.swing.JProgressBar();
		coordinateLab = new javax.swing.JLabel();
		coordinateField = new javax.swing.JTextField();
		fixedZoomLab = new javax.swing.JLabel();
		fixedZoomCombo = new javax.swing.JComboBox<>();
		curretnZoomLab = new javax.swing.JLabel();
		imageSizeLab = new javax.swing.JLabel();
		topPanel = new javax.swing.JPanel();
		jToolBar1 = new javax.swing.JToolBar();
		newProjectButton = new javax.swing.JButton();
		openProjectButton = new javax.swing.JButton();
		saveButton = new javax.swing.JButton();
		jToolBar2 = new javax.swing.JToolBar();
		undoButton = new javax.swing.JButton();
		redoButton = new javax.swing.JButton();
		jToolBar3 = new javax.swing.JToolBar();
		zoomInBouton = new javax.swing.JButton();
		zoomOutBouton = new javax.swing.JButton();
		menuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		newProjectMenu = new javax.swing.JMenuItem();
		openProjectMenu = new javax.swing.JMenuItem();
		recentsProjectsMenu = new javax.swing.JMenu();
		jSeparator2 = new javax.swing.JPopupMenu.Separator();
		saveMenu = new javax.swing.JMenuItem();
		saveAsMenu = new javax.swing.JMenuItem();
		exportImageMenu = new javax.swing.JMenuItem();
		jSeparator3 = new javax.swing.JPopupMenu.Separator();
		exitMenu = new javax.swing.JMenuItem();
		editionMenu = new javax.swing.JMenu();
		undoMenu = new javax.swing.JMenuItem();
		redoMenu = new javax.swing.JMenuItem();
		planTransformationMenu = new javax.swing.JMenu();
		shearMenu = new javax.swing.JMenuItem();
		homothetyMenu = new javax.swing.JMenuItem();
		rotationMenu = new javax.swing.JMenuItem();
		verticalSymmetry = new javax.swing.JMenuItem();
		horizontalSymmetryMenu = new javax.swing.JMenuItem();
		symmetryInTheCenterMenu = new javax.swing.JMenuItem();
		pointTransformationMenu = new javax.swing.JMenu();
		invertGrayscaleMenu = new javax.swing.JMenuItem();
		binarizationMenu = new javax.swing.JMenu();
		fixedThresholdMenu = new javax.swing.JMenuItem();
		otsuThresholdMenu = new javax.swing.JMenuItem();
		histogramEqualizationMenu = new javax.swing.JMenuItem();
		dynamicDisplayMenu = new javax.swing.JMenuItem();
		localProcessingMenu = new javax.swing.JMenu();
		discreteCovolutionMenu = new javax.swing.JMenu();
		sobelFilter1Menu = new javax.swing.JMenuItem();
		sobelFilter2Menu = new javax.swing.JMenuItem();
		laplacianFilterMenu = new javax.swing.JMenuItem();
		rehausseurMenu = new javax.swing.JMenuItem();
		discreteCovolutionMenu1 = new javax.swing.JMenuItem();
		discreteCovolutionMenu2 = new javax.swing.JMenuItem();
		discreteCovolutionMenu3 = new javax.swing.JMenuItem();
		smoothingMenu = new javax.swing.JMenu();
		mediumFilteringMenu = new javax.swing.JMenuItem();
		gaussianFilteringMenu = new javax.swing.JMenuItem();
		jSeparator1 = new javax.swing.JPopupMenu.Separator();
		conservativeSmoothingMenu = new javax.swing.JMenuItem();
		medianFilteringMenu = new javax.swing.JMenuItem();
		averageFilteringMenu = new javax.swing.JMenuItem();
		operationMenu = new javax.swing.JMenu();
		erosionMenu = new javax.swing.JMenuItem();
		dilationMenu = new javax.swing.JMenuItem();
		openingMenu = new javax.swing.JMenuItem();
		allOrNothingMenu = new javax.swing.JMenuItem();
		thickeningMenu = new javax.swing.JMenuItem();
		topHatOpeningMenu = new javax.swing.JMenuItem();
		topHatClosureMenu = new javax.swing.JMenuItem();
		formRecognitionMenu = new javax.swing.JMenu();
		papertTurtleMenu = new javax.swing.JMenuItem();
		labelingMenu = new javax.swing.JMenuItem();
		graphicsMenu = new javax.swing.JMenu();
		histogramMenu = new javax.swing.JMenuItem();
		cumulativeHistogramMenu = new javax.swing.JMenuItem();
		settingMenu = new javax.swing.JMenu();
		languageMenu = new javax.swing.JMenu();
		decoderMenu = new JMenuItem("Processing engine");
		frenchMenu = new javax.swing.JRadioButtonMenuItem();
		
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("Analyse d'image et vision par ordinateur");
		setMinimumSize(new java.awt.Dimension(1024, 600));
		
		rootPanel.setBackground(new java.awt.Color(255, 255, 255));
		rootPanel.setLayout(new java.awt.BorderLayout());
		
		imageScreen.setBackground(new java.awt.Color(255, 255, 255));
		imageScreen.setLayout(new java.awt.BorderLayout());
		
		bottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
		
		processingBar.setFont(new java.awt.Font("DejaVu Sans", 0, 13)); // NOI18N
		bottomPanel.add(processingBar);
		
		coordinateLab.setText("Coordonnées");
		bottomPanel.add(coordinateLab);
		
		coordinateField.setText("0,0");
		coordinateField.setMinimumSize(new java.awt.Dimension(100, 27));
		coordinateField.setPreferredSize(new java.awt.Dimension(100, 27));
		bottomPanel.add(coordinateField);
		
		fixedZoomLab.setText("zoom fixe");
		bottomPanel.add(fixedZoomLab);
		
		fixedZoomCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"200%", "175%", "150%", "125%", "100%", "75%", "50%", "25%"}));
		fixedZoomCombo.setSelectedIndex(4);
		fixedZoomCombo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fixedZoomComboActionPerformed(evt);
			}
		});
		bottomPanel.add(fixedZoomCombo);
		
		curretnZoomLab.setText("100%");
		curretnZoomLab.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
		bottomPanel.add(curretnZoomLab);
		
		imageSizeLab.setText("Taille de l'image");
		bottomPanel.add(imageSizeLab);
		
		imageScreen.add(bottomPanel, java.awt.BorderLayout.PAGE_END);
		
		topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
		
		jToolBar1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		jToolBar1.setRollover(true);
		
		newProjectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/createNewProject.png"))); // NOI18N
		newProjectButton.setToolTipText("Nouveau projet");
		newProjectButton.setFocusable(false);
		newProjectButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		newProjectButton.setMaximumSize(new java.awt.Dimension(35, 35));
		newProjectButton.setMinimumSize(new java.awt.Dimension(35, 35));
		newProjectButton.setPreferredSize(new java.awt.Dimension(35, 35));
		newProjectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		newProjectButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				newProjectButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(newProjectButton);
		
		openProjectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/menu-open.png"))); // NOI18N
		openProjectButton.setToolTipText("Ouvrir un projet");
		openProjectButton.setFocusable(false);
		openProjectButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		openProjectButton.setMaximumSize(new java.awt.Dimension(35, 35));
		openProjectButton.setMinimumSize(new java.awt.Dimension(35, 35));
		openProjectButton.setPreferredSize(new java.awt.Dimension(35, 35));
		openProjectButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		openProjectButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openProjectButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(openProjectButton);
		
		saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/menu-saveall.png"))); // NOI18N
		saveButton.setToolTipText("Sauvegarder le projet");
		saveButton.setFocusable(false);
		saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		saveButton.setMaximumSize(new java.awt.Dimension(35, 35));
		saveButton.setMinimumSize(new java.awt.Dimension(35, 35));
		saveButton.setPreferredSize(new java.awt.Dimension(35, 35));
		saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(saveButton);
		
		topPanel.add(jToolBar1);
		
		jToolBar2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		jToolBar2.setRollover(true);
		
		undoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/undo.png"))); // NOI18N
		undoButton.setToolTipText("Annuler");
		undoButton.setFocusable(false);
		undoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		undoButton.setMaximumSize(new java.awt.Dimension(35, 35));
		undoButton.setMinimumSize(new java.awt.Dimension(35, 35));
		undoButton.setPreferredSize(new java.awt.Dimension(35, 35));
		undoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		undoButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				undoButtonActionPerformed(evt);
			}
		});
		jToolBar2.add(undoButton);
		
		redoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/redo.png"))); // NOI18N
		redoButton.setToolTipText("Retablir");
		redoButton.setFocusable(false);
		redoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		redoButton.setMaximumSize(new java.awt.Dimension(35, 35));
		redoButton.setMinimumSize(new java.awt.Dimension(35, 35));
		redoButton.setPreferredSize(new java.awt.Dimension(35, 35));
		redoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		redoButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				redoButtonActionPerformed(evt);
			}
		});
		jToolBar2.add(redoButton);
		
		topPanel.add(jToolBar2);
		
		jToolBar3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
		jToolBar3.setRollover(true);
		
		zoomInBouton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/zoomIn.png"))); // NOI18N
		zoomInBouton.setToolTipText("Élargir l'image");
		zoomInBouton.setFocusable(false);
		zoomInBouton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		zoomInBouton.setMaximumSize(new java.awt.Dimension(35, 35));
		zoomInBouton.setMinimumSize(new java.awt.Dimension(35, 35));
		zoomInBouton.setPreferredSize(new java.awt.Dimension(35, 35));
		zoomInBouton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		zoomInBouton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				zoomInBoutonActionPerformed(evt);
			}
		});
		jToolBar3.add(zoomInBouton);
		
		zoomOutBouton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/zoomOut.png"))); // NOI18N
		zoomOutBouton.setToolTipText("Rapetisser l'image");
		zoomOutBouton.setFocusable(false);
		zoomOutBouton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		zoomOutBouton.setMaximumSize(new java.awt.Dimension(35, 35));
		zoomOutBouton.setMinimumSize(new java.awt.Dimension(35, 35));
		zoomOutBouton.setPreferredSize(new java.awt.Dimension(35, 35));
		zoomOutBouton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		zoomOutBouton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				zoomOutBoutonActionPerformed(evt);
			}
		});
		jToolBar3.add(zoomOutBouton);
		
		topPanel.add(jToolBar3);
		
		imageScreen.add(topPanel, java.awt.BorderLayout.NORTH);
		
		rootPanel.add(imageScreen, java.awt.BorderLayout.CENTER);
		
		getContentPane().add(rootPanel, java.awt.BorderLayout.CENTER);
		
		fileMenu.setText("Fichier");
		
		newProjectMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
		newProjectMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/createNewProject.png"))); // NOI18N
		newProjectMenu.setText("Nouveau projet");
		newProjectMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				newProjectMenuActionPerformed(evt);
			}
		});
		fileMenu.add(newProjectMenu);
		
		openProjectMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
		openProjectMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/menu-open.png"))); // NOI18N
		openProjectMenu.setText("Ouvrir un projet");
		openProjectMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openProjectMenuActionPerformed(evt);
			}
		});
		fileMenu.add(openProjectMenu);
		
		recentsProjectsMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/flattenPackages.png"))); // NOI18N
		recentsProjectsMenu.setText("Projets récents");
		fileMenu.add(recentsProjectsMenu);
		fileMenu.add(jSeparator2);
		
		saveMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
		saveMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/menu-saveall.png"))); // NOI18N
		saveMenu.setText("Enregistrer");
		saveMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveMenuActionPerformed(evt);
			}
		});
		fileMenu.add(saveMenu);
		
		saveAsMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		saveAsMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/menu-saveall.png"))); // NOI18N
		saveAsMenu.setText("Enregistrer sous");
		saveAsMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveAsMenuActionPerformed(evt);
			}
		});
		fileMenu.add(saveAsMenu);
		
		exportImageMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
		exportImageMenu.setText("Exporter l'image");
		exportImageMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exportImageMenuActionPerformed(evt);
			}
		});
		fileMenu.add(exportImageMenu);
		fileMenu.add(jSeparator3);
		
		exitMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
		exitMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/exit.png"))); // NOI18N
		exitMenu.setText("Quitter");
		exitMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exitMenuActionPerformed(evt);
			}
		});
		fileMenu.add(exitMenu);
		
		menuBar.add(fileMenu);
		
		editionMenu.setText("Édition");
		
		undoMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
		undoMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/undo.png"))); // NOI18N
		undoMenu.setText("Annuler");
		undoMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				undoMenuActionPerformed(evt);
			}
		});
		editionMenu.add(undoMenu);
		
		redoMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
		redoMenu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/emit/ipcv/images/redo.png"))); // NOI18N
		redoMenu.setText("Retablir");
		redoMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				redoMenuActionPerformed(evt);
			}
		});
		editionMenu.add(redoMenu);
		
		menuBar.add(editionMenu);
		
		planTransformationMenu.setText("Transformation plane");
		
		shearMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		shearMenu.setText("Cisaillemet");
		shearMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				shearMenuActionPerformed(evt);
			}
		});
		planTransformationMenu.add(shearMenu);
		
		homothetyMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		homothetyMenu.setText("Homothétie");
		homothetyMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				homothetyMenuActionPerformed(evt);
			}
		});
		planTransformationMenu.add(homothetyMenu);
		
		rotationMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		rotationMenu.setText("Rotation");
		rotationMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				rotationMenuActionPerformed(evt);
			}
		});
		planTransformationMenu.add(rotationMenu);
		
		verticalSymmetry.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_UP, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		verticalSymmetry.setText("Symétrie verticale");
		verticalSymmetry.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				verticalSymmetryActionPerformed(evt);
			}
		});
		planTransformationMenu.add(verticalSymmetry);
		
		horizontalSymmetryMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_LEFT, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		horizontalSymmetryMenu.setText("Symétrie horizontale");
		horizontalSymmetryMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				horizontalSymmetryMenuActionPerformed(evt);
			}
		});
		planTransformationMenu.add(horizontalSymmetryMenu);
		
		symmetryInTheCenterMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		symmetryInTheCenterMenu.setText("Symétrie au centre");
		symmetryInTheCenterMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				symmetryInTheCenterMenuActionPerformed(evt);
			}
		});
		planTransformationMenu.add(symmetryInTheCenterMenu);
		
		menuBar.add(planTransformationMenu);
		
		pointTransformationMenu.setText("Transformation ponctuel");
		
		invertGrayscaleMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
		invertGrayscaleMenu.setText("Inverser le niveau de gris");
		invertGrayscaleMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				invertGrayscaleMenuActionPerformed(evt);
			}
		});
		pointTransformationMenu.add(invertGrayscaleMenu);
		
		binarizationMenu.setText("Binarisation");
		
		fixedThresholdMenu.setText("Seuillage fixe");
		fixedThresholdMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				fixedThresholdMenuActionPerformed(evt);
			}
		});
		binarizationMenu.add(fixedThresholdMenu);
		
		otsuThresholdMenu.setText("Méthode d'Otsu");
		otsuThresholdMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				otsuThresholdMenuActionPerformed(evt);
			}
		});
		binarizationMenu.add(otsuThresholdMenu);
		
		pointTransformationMenu.add(binarizationMenu);
		
		histogramEqualizationMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
		histogramEqualizationMenu.setText("Egalisation d'histogramme");
		histogramEqualizationMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				histogramEqualizationMenuActionPerformed(evt);
			}
		});
		pointTransformationMenu.add(histogramEqualizationMenu);
		
		dynamicDisplayMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
		dynamicDisplayMenu.setText("Expansion dynamique");
		dynamicDisplayMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				dynamicDisplayMenuActionPerformed(evt);
			}
		});
		pointTransformationMenu.add(dynamicDisplayMenu);
		
		menuBar.add(pointTransformationMenu);
		
		localProcessingMenu.setText("Transformation locale");
		
		discreteCovolutionMenu.setText("Convolution discrete");
		
		sobelFilter1Menu.setText("Sobel1");
		sobelFilter1Menu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sobelFilter1MenuActionPerformed(evt);
			}
		});
		discreteCovolutionMenu.add(sobelFilter1Menu);
		
		sobelFilter2Menu.setText("Sobel2");
		sobelFilter2Menu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sobelFilter2MenuActionPerformed(evt);
			}
		});
		discreteCovolutionMenu.add(sobelFilter2Menu);
		
		laplacianFilterMenu.setText("Laplacien");
		laplacianFilterMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				laplacianFilterMenuActionPerformed(evt);
			}
		});
		discreteCovolutionMenu.add(laplacianFilterMenu);
		
		rehausseurMenu.setText("Rehausseur");
		rehausseurMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				rehausseurMenuActionPerformed(evt);
			}
		});
		discreteCovolutionMenu.add(rehausseurMenu);
		
		discreteCovolutionMenu1.setText("Filtre personnel 1");
		discreteCovolutionMenu1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				discreteCovolutionMenu1ActionPerformed(evt);
			}
		});
		discreteCovolutionMenu.add(discreteCovolutionMenu1);
		
		discreteCovolutionMenu2.setText("Filtre personnel 2");
		discreteCovolutionMenu2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				discreteCovolutionMenu2ActionPerformed(evt);
			}
		});
		discreteCovolutionMenu.add(discreteCovolutionMenu2);
		
		discreteCovolutionMenu3.setText("Filtre personnel 3");
		discreteCovolutionMenu3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				discreteCovolutionMenu3ActionPerformed(evt);
			}
		});
		discreteCovolutionMenu.add(discreteCovolutionMenu3);
		
		localProcessingMenu.add(discreteCovolutionMenu);
		
		smoothingMenu.setText("Lissage");
		
		mediumFilteringMenu.setText("Filtre moyen");
		mediumFilteringMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				mediumFilteringMenuActionPerformed(evt);
			}
		});
		smoothingMenu.add(mediumFilteringMenu);
		
		gaussianFilteringMenu.setText("Filtre gaussien");
		gaussianFilteringMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				gaussianFilteringMenuActionPerformed(evt);
			}
		});
		smoothingMenu.add(gaussianFilteringMenu);
		
		localProcessingMenu.add(smoothingMenu);
		localProcessingMenu.add(jSeparator1);
		
		conservativeSmoothingMenu.setText("Lissage conservateur");
		conservativeSmoothingMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				conservativeSmoothingMenuActionPerformed(evt);
			}
		});
		localProcessingMenu.add(conservativeSmoothingMenu);
		
		medianFilteringMenu.setText("Filtrage median");
		medianFilteringMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				medianFilteringMenuActionPerformed(evt);
			}
		});
		localProcessingMenu.add(medianFilteringMenu);
		
		averageFilteringMenu.setText("Filtrage moyenne");
		averageFilteringMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				averageFilteringMenuActionPerformed(evt);
			}
		});
		localProcessingMenu.add(averageFilteringMenu);
		
		menuBar.add(localProcessingMenu);
		
		operationMenu.setText("Opération");
		
		erosionMenu.setText("Erosion");
		erosionMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				erosionMenuActionPerformed(evt);
			}
		});
		operationMenu.add(erosionMenu);
		
		dilationMenu.setText("Dilatation");
		dilationMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				dilationMenuActionPerformed(evt);
			}
		});
		operationMenu.add(dilationMenu);
		
		openingMenu.setText("Ouverture");
		openingMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openingMenuActionPerformed(evt);
			}
		});
		operationMenu.add(openingMenu);
		
		allOrNothingMenu.setText("Tout ou rien");
		allOrNothingMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				allOrNothingMenuActionPerformed(evt);
			}
		});
		operationMenu.add(allOrNothingMenu);
		
		thickeningMenu.setText("Epaississement");
		thickeningMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				thickeningMenuActionPerformed(evt);
			}
		});
		operationMenu.add(thickeningMenu);
		
		topHatOpeningMenu.setText("Top haut (ouverture)");
		topHatOpeningMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				topHatOpeningMenuActionPerformed(evt);
			}
		});
		operationMenu.add(topHatOpeningMenu);
		
		topHatClosureMenu.setText("Top haut (fermeture)");
		topHatClosureMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				topHatClosureMenuActionPerformed(evt);
			}
		});
		operationMenu.add(topHatClosureMenu);
		
		menuBar.add(operationMenu);
		
		formRecognitionMenu.setText("Reconaissance de forme");
		
		papertTurtleMenu.setText("Tortue de papert");
		papertTurtleMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				papertTurtleMenuActionPerformed(evt);
			}
		});
		formRecognitionMenu.add(papertTurtleMenu);
		
		labelingMenu.setText("Etiquetage");
		labelingMenu.setEnabled(false);
		formRecognitionMenu.add(labelingMenu);
		
		menuBar.add(formRecognitionMenu);
		
		graphicsMenu.setText("Graphes statistique");
		
		histogramMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
		histogramMenu.setText("Histogramme");
		histogramMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				histogramMenuActionPerformed(evt);
			}
		});
		graphicsMenu.add(histogramMenu);
		
		cumulativeHistogramMenu.setText("Histogramme cumulé");
		cumulativeHistogramMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cumulativeHistogramMenuActionPerformed(evt);
			}
		});
		graphicsMenu.add(cumulativeHistogramMenu);
		
		menuBar.add(graphicsMenu);
		
		settingMenu.setText("Paramètre");
		
		languageMenu.setText("Langage");
		decoderMenu.addActionListener(this::changeDecoderMode);
		settingMenu.add(languageMenu);
		settingMenu.add(decoderMenu);
		
		menuBar.add(settingMenu);
		
		setJMenuBar(menuBar);
		
		pack();
		setLocationRelativeTo(null);
	}// </editor-fold>//GEN-END:initComponents
	
	private void changeDecoderMode(ActionEvent actionEvent) {
		new Thread(() -> {
			RemoteServerConfigurationDialog remoteServerConfigurationDialog = new RemoteServerConfigurationDialog(this, true);
			remoteServerConfigurationDialog.setIpAddressField(setting.getRemoteIpAddress());
			remoteServerConfigurationDialog.setPortField(setting.getRemotePortAddress());
			remoteServerConfigurationDialog.setDecoder(setting.isUseLocalHardware());
			remoteServerConfigurationDialog.addObserver(address -> {
				this.settingModel.changeEngineDecoder(Boolean.valueOf(address.get("use-local-hardware").toString()), (String)address.get("ip-address"), (Integer)address.get("port"));
				this.setting = this.settingModel.loadSetting();
			});
			remoteServerConfigurationDialog.setVisible(true);
		}).start();
	}
	
	private void sobelFilter1MenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sobelFilter1MenuActionPerformed
		sobel1CovolutionPerformed();
	}//GEN-LAST:event_sobelFilter1MenuActionPerformed
	
	private void discreteCovolutionMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discreteCovolutionMenu1ActionPerformed
		customCovolution1Performed();
	}//GEN-LAST:event_discreteCovolutionMenu1ActionPerformed
	
	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JMenuItem allOrNothingMenu;
	private javax.swing.JMenuItem averageFilteringMenu;
	private javax.swing.JMenu binarizationMenu;
	private javax.swing.JPanel bottomPanel;
	private javax.swing.JMenuItem conservativeSmoothingMenu;
	private javax.swing.JTextField coordinateField;
	private javax.swing.JLabel coordinateLab;
	private javax.swing.JMenuItem cumulativeHistogramMenu;
	private javax.swing.JLabel curretnZoomLab;
	private javax.swing.JMenuItem dilationMenu;
	private javax.swing.JMenu discreteCovolutionMenu;
	private javax.swing.JMenuItem discreteCovolutionMenu1;
	private javax.swing.JMenuItem discreteCovolutionMenu2;
	
	private void sobelFilter2MenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sobelFilter2MenuActionPerformed
		sobel2CovolutionPerformed();
	}//GEN-LAST:event_sobelFilter2MenuActionPerformed
	
	private javax.swing.JMenuItem dynamicDisplayMenu;
	private javax.swing.JMenu editionMenu;
	private javax.swing.JMenuItem erosionMenu;
	private javax.swing.JMenuItem exitMenu;
	private javax.swing.JMenuItem exportImageMenu;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JMenuItem fixedThresholdMenu;
	private javax.swing.JComboBox<String> fixedZoomCombo;
	private javax.swing.JLabel fixedZoomLab;
	private javax.swing.JMenu formRecognitionMenu;
	private javax.swing.JRadioButtonMenuItem frenchMenu;
	private javax.swing.JMenuItem gaussianFilteringMenu;
	private javax.swing.JMenu graphicsMenu;
	private javax.swing.JMenuItem histogramEqualizationMenu;
	private javax.swing.JMenuItem histogramMenu;
	private javax.swing.JMenuItem homothetyMenu;
	private javax.swing.JMenuItem horizontalSymmetryMenu;
	private javax.swing.JPanel imageScreen;
	private javax.swing.JLabel imageSizeLab;
	private javax.swing.JMenuItem invertGrayscaleMenu;
	private javax.swing.JPopupMenu.Separator jSeparator1;
	private javax.swing.JPopupMenu.Separator jSeparator2;
	private javax.swing.JPopupMenu.Separator jSeparator3;
	private javax.swing.JToolBar jToolBar1;
	private javax.swing.JToolBar jToolBar2;
	private javax.swing.JToolBar jToolBar3;
	private javax.swing.JMenuItem labelingMenu;
	private javax.swing.ButtonGroup languageButtonGroup, decoderMenuGroup;
	private javax.swing.JMenu languageMenu;
	private JMenuItem decoderMenu;
	
	private void discreteCovolutionMenu2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discreteCovolutionMenu2ActionPerformed
		customCovolution2Performed();
	}//GEN-LAST:event_discreteCovolutionMenu2ActionPerformed
	
	private javax.swing.JMenu localProcessingMenu;
	private javax.swing.JMenuItem medianFilteringMenu;
	private javax.swing.JMenuItem mediumFilteringMenu;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JButton newProjectButton;
	private javax.swing.JMenuItem newProjectMenu;
	private javax.swing.JButton openProjectButton;
	private javax.swing.JMenuItem openProjectMenu;
	private javax.swing.JMenuItem openingMenu;
	private javax.swing.JMenu operationMenu;
	private javax.swing.JMenuItem otsuThresholdMenu;
	private javax.swing.JMenuItem papertTurtleMenu;
	private javax.swing.JMenu planTransformationMenu;
	private javax.swing.JMenu pointTransformationMenu;
	private javax.swing.JProgressBar processingBar;
	private javax.swing.JMenu recentsProjectsMenu;
	private javax.swing.JButton redoButton;
	private javax.swing.JMenuItem redoMenu;
	
	private void discreteCovolutionMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discreteCovolutionMenu3ActionPerformed
		customCovolution3Performed();
	}//GEN-LAST:event_discreteCovolutionMenu3ActionPerformed
	
	private javax.swing.JPanel rootPanel;
	private javax.swing.JMenuItem rotationMenu;
	private javax.swing.JMenuItem saveAsMenu;
	private javax.swing.JButton saveButton;
	private javax.swing.JMenuItem saveMenu;
	private javax.swing.JMenu settingMenu;
	private javax.swing.JMenuItem shearMenu;
	private javax.swing.JMenu smoothingMenu;
	
	private void laplacianFilterMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_laplacianFilterMenuActionPerformed
		laplacianCovolutionPerformed();
	}//GEN-LAST:event_laplacianFilterMenuActionPerformed
	
	private void rehausseurMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rehausseurMenuActionPerformed
		rehausseurCovolutionPerformed();
	}//GEN-LAST:event_rehausseurMenuActionPerformed
	
	private javax.swing.JMenuItem symmetryInTheCenterMenu;
	private javax.swing.JMenuItem thickeningMenu;
	private javax.swing.JMenuItem topHatClosureMenu;
	private javax.swing.JMenuItem topHatOpeningMenu;
	private javax.swing.JPanel topPanel;
	private javax.swing.JButton undoButton;
	private javax.swing.JMenuItem undoMenu;
	private javax.swing.JMenuItem verticalSymmetry;
	private javax.swing.JButton zoomInBouton;
	private javax.swing.JButton zoomOutBouton;
	// End of variables declaration//GEN-END:variables
	
	private void applyImageChange(RGBA[][] imageColor, BufferedImage bufferedImage) {
		final int ligne = bufferedImage.getWidth(), colonne = bufferedImage.getHeight();
		for (int x = 0; x < ligne; x++) {
			for (int y = 0; y < colonne; y++) {
				bufferedImage.setRGB(x, y, new Color(imageColor[x][y].getRed(), imageColor[x][y].getGreen(), imageColor[x][y].getBlue(), imageColor[x][y].getAlpha()).getRGB());
			}
		}
	}
	
	private void saveOrSaveAsPerformed() {
		if (newProject) {
			saveAsPerformed();
		} else {
			saveProject();
		}
	}
	
	private void saveAndClose() {
		if (newProject) {
			saveAsProjectAndClose();
		} else {
			saveAndCloseProject();
		}
	}
	
	private void saveAsPerformed() {
		ProjectExplorer projectExplorer = new ProjectExplorer(lastLocation + File.separatorChar + translationModel.get(language, "new_project") + ".imgproc").setTranslation(translationModel).setLanguage(language);
		int acceptation = projectExplorer.saveAs(rootPanel);
		if (acceptation == JOptionPane.OK_OPTION) {
			final JFrame that = this;
			new Thread(() -> {
				SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
				ImageLoader imageLoader = imageViewer.getImageLoader();
				File image = projectExplorer.getSelectedFile();
				
				// Check if location is writable
				if (Files.isWritable(image.getParentFile().toPath())) {
					String absolutePath = image.getAbsolutePath();
					// checking file extension
					String selectedExtension = projectExplorer.getFileFilter().getDescription();
					selectedExtension = selectedExtension.substring(selectedExtension.lastIndexOf("*.") + 2, selectedExtension.length() - 1);
					if (image.getName().contains(".")) {
						boolean extensionExacte = false;
						for (String extension : projectExplorer.getExtensionEnregistrement()) {
							String filename = image.getName();
							if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase(extension)) {
								extensionExacte = true;
								selectedExtension = extension;
							}
						}
						if (!extensionExacte) {
							absolutePath += "." + selectedExtension;
						}
					} // checking file extension
					else {
						absolutePath += "." + selectedExtension;
					}
					File sortie = new File(absolutePath);
					ImageProcessingProjectFile project = new ImageProcessingProjectFile();
					project.setImage(imageLoader.getOriginalColor());
					project.setImageFileExtension(imageLoader.getImageOriginalExtension());
					BufferedImage bufferedImage = imageLoader.getBufferedImage();
					project.setImageWidth(bufferedImage.getWidth());
					project.setImageHeight(bufferedImage.getHeight());
					project.setImageType(bufferedImage.getType());
					try {
						ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(sortie));
						outputStream.writeObject(project);
						outputStream.flush();
						outputStream.close();
					} catch (IOException ex) {
						Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
					}
					if (!projectHistoryModel.saveProject(sortie.getAbsolutePath()) && projectHistoryModel.addProject(new DBProjectHistory(sortie.getName(), sortie.getAbsolutePath()))) {
						initProjectHistory();
					}
					SwingUtilities.invokeLater(() -> {
						currentTitle = "(" + translationModel.get(language, "registered") + ") - " + sortie.getName();
						setTitle(currentTitle);
					});
					newProject = false;
					projectNameLocation = sortie.getAbsolutePath();
					hasChanges = false;
				} else {
					JOptionPane.showMessageDialog(that, translationModel.get(language, "write_permission_error_message").toString().replaceAll("\\[br]", "\n"), translationModel.get(language, "write_permission_error_title").toString(), JOptionPane.ERROR_MESSAGE);
				}
				SwingUtilities.invokeLater(() -> processingBar.setVisible(false));
			}).start();
		}
	}
	
	private void saveProject() {
		final JFrame that = this;
		new Thread(() -> {
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			ImageLoader imageLoader = imageViewer.getImageLoader();
			File output = new File(projectNameLocation);
			if (Files.isWritable(output.getParentFile().toPath())) {
				ImageProcessingProjectFile project = new ImageProcessingProjectFile();
				project.setImage(imageLoader.getOriginalColor());
				BufferedImage bufferedImage = imageLoader.getBufferedImage();
				project.setImageWidth(bufferedImage.getWidth());
				project.setImageHeight(bufferedImage.getHeight());
				project.setImageType(bufferedImage.getType());
				project.setImageFileExtension(imageLoader.getImageOriginalExtension());
				try {
					ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(output));
					outputStream.writeObject(project);
					outputStream.flush();
					outputStream.close();
				} catch (IOException ex) {
					Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
				}
				if (!projectHistoryModel.saveProject(output.getAbsolutePath()) && projectHistoryModel.addProject(new DBProjectHistory(output.getName(), output.getAbsolutePath()))) {
					initProjectHistory();
				}
				SwingUtilities.invokeLater(() -> {
					currentTitle = "(" + translationModel.get(language, "registered") + ") - " + output.getName();
					setTitle(currentTitle);
				});
				System.out.println("[INFO] Saving project");
				hasChanges = false;
			} else {
				JOptionPane.showMessageDialog(that, translationModel.get(language, "write_permission_error_message").toString().replaceAll("\\[br]", "\n"), translationModel.get(language, "write_permission_error_title").toString(), JOptionPane.ERROR_MESSAGE);
			}
			SwingUtilities.invokeLater(() -> processingBar.setVisible(false));
		}).start();
	}
	
	protected void closureRequestPerformed() {
		if (hasChanges) {
			String[] boutons = new String[]{translationModel.get(language, "save").toString(), translationModel.get(language, "not_save").toString(), translationModel.get(language, "cancel").toString()};
			int confirmationEnregistrement = JOptionPane.showOptionDialog(rootPanel, translationModel.get(language, "save_changes_request_message").toString().replaceAll("\\[br]", "\n"), translationModel.get(language, "save_changes_request_title").toString(), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, boutons, boutons[0]);
			if (confirmationEnregistrement == JOptionPane.OK_OPTION) {
				saveAndClose();
			} else if (confirmationEnregistrement == JOptionPane.NO_OPTION) {
				boutons = new String[]{translationModel.get(language, "close").toString(), translationModel.get(language, "cancel").toString()};
				confirmationEnregistrement = JOptionPane.showOptionDialog(rootPanel, translationModel.get(language, "quit_application_request_message").toString(), translationModel.get(language, "quit_application_request_title").toString(), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, boutons, boutons[0]);
				if (confirmationEnregistrement == JOptionPane.OK_OPTION) {
					System.out.println("[INFO] Close the application");
					System.exit(0);
				}
			}
		} else {
			String[] boutons = new String[]{translationModel.get(language, "close").toString(), translationModel.get(language, "cancel").toString()};
			int confirmationEnregistrement = JOptionPane.showOptionDialog(rootPanel, translationModel.get(language, "quit_application_request_message").toString(), translationModel.get(language, "quit_application_request_title").toString(), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, boutons, boutons[0]);
			if (confirmationEnregistrement == JOptionPane.OK_OPTION) {
				System.out.println("[INFO] Close the application");
				System.exit(0);
			}
		}
	}
	
	private void openNewImage(File newImage) {
		new Thread(() -> {
			SwingUtilities.invokeLater(() -> {
				processingBar.setVisible(true);
			});
			projectNameLocation = null;
			newProject = true;
			hasChanges = true;
			imageViewer.clearObserver();
			imageViewer.addObserver((coordonnees) -> {
				SwingUtilities.invokeLater(() -> {
					coordinateField.setText(coordonnees[0] + "," + coordonnees[1]);
				});
			});
			lastLocation = newImage.getParent();
			BufferedImage image = null;
			ImageLoader imageLoader = null;
			try {
				imageLoader = new ImageLoader(newImage);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String imageName = newImage.getName();
				imageLoader.setImageOriginalExtension(imageName.substring(imageName.lastIndexOf('.') + 1, imageName.length()));
				System.out.printf("[INFO] Image file extension \"*.%s\"\n", imageLoader.getImageOriginalExtension());
				image = imageLoader.getBufferedImage();
			RGBA[][] colorImage = imageLoader.getOriginalColor();
			
			applyImageChange(colorImage, image);
			
			currentTitle = translationModel.get(language, "new_project") + ".imgproc";
			projectNameLocation = lastLocation + File.separatorChar + currentTitle;
			setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
			
			applicationHistory = new ApplicationHistory<>();
			applicationHistory.append(colorImage);
			final ImageLoader imageHelperCopie = imageLoader;
			SwingUtilities.invokeLater(() -> {
				imageViewer.setImageLoader(imageHelperCopie);
				imageViewer.repaint();
				imageSizeLab.setText(colorImage.length + " x " + colorImage[0].length);
				enableMenus();
				processingBar.setVisible(false);
			});
		}).start();
	}
	
	private void disableMenus() {
		final boolean activation = false;
		topHatClosureMenu.setEnabled(activation);
		topHatOpeningMenu.setEnabled(activation);
		allOrNothingMenu.setEnabled(activation);
		openingMenu.setEnabled(activation);
		erosionMenu.setEnabled(activation);
		dilationMenu.setEnabled(activation);
		saveMenu.setEnabled(activation);
		saveAsMenu.setEnabled(activation);
		exportImageMenu.setEnabled(activation);
		undoMenu.setEnabled(activation);
		redoMenu.setEnabled(activation);
		shearMenu.setEnabled(activation);
		homothetyMenu.setEnabled(activation);
		rotationMenu.setEnabled(activation);
		verticalSymmetry.setEnabled(activation);
		horizontalSymmetryMenu.setEnabled(activation);
		symmetryInTheCenterMenu.setEnabled(activation);
		invertGrayscaleMenu.setEnabled(activation);
		binarizationMenu.setEnabled(activation);
		cumulativeHistogramMenu.setEnabled(activation);
		dynamicDisplayMenu.setEnabled(activation);
		histogramEqualizationMenu.setEnabled(activation);
		discreteCovolutionMenu.setEnabled(activation);
		sobelFilter1Menu.setEnabled(activation);
		discreteCovolutionMenu1.setEnabled(activation);
		sobelFilter2Menu.setEnabled(activation);
		smoothingMenu.setEnabled(activation);
		conservativeSmoothingMenu.setEnabled(activation);
		medianFilteringMenu.setEnabled(activation);
		averageFilteringMenu.setEnabled(activation);
		histogramMenu.setEnabled(activation);
		cumulativeHistogramMenu.setEnabled(activation);
		coordinateField.setEnabled(activation);
		fixedZoomCombo.setEnabled(activation);
		undoButton.setEnabled(activation);
		redoButton.setEnabled(activation);
		zoomInBouton.setEnabled(activation);
		zoomOutBouton.setEnabled(activation);
		saveButton.setEnabled(activation);
		papertTurtleMenu.setEnabled(activation);
		thickeningMenu.setEnabled(activation);
		//labelingMenu.setEnabled(activation);
	}
	
	private void enableMenus() {
		final boolean activation = true;
		topHatClosureMenu.setEnabled(activation);
		topHatOpeningMenu.setEnabled(activation);
		allOrNothingMenu.setEnabled(activation);
		openingMenu.setEnabled(activation);
		erosionMenu.setEnabled(activation);
		dilationMenu.setEnabled(activation);
		openProjectMenu.setEnabled(activation);
		saveMenu.setEnabled(activation);
		exportImageMenu.setEnabled(activation);
		saveAsMenu.setEnabled(activation);
		recentsProjectsMenu.setEnabled(activation);
		undoMenu.setEnabled(activation);
		redoMenu.setEnabled(activation);
		shearMenu.setEnabled(activation);
		homothetyMenu.setEnabled(activation);
		rotationMenu.setEnabled(activation);
		verticalSymmetry.setEnabled(activation);
		horizontalSymmetryMenu.setEnabled(activation);
		symmetryInTheCenterMenu.setEnabled(activation);
		invertGrayscaleMenu.setEnabled(activation);
		binarizationMenu.setEnabled(activation);
		cumulativeHistogramMenu.setEnabled(activation);
		dynamicDisplayMenu.setEnabled(activation);
		histogramEqualizationMenu.setEnabled(activation);
		discreteCovolutionMenu.setEnabled(activation);
		sobelFilter1Menu.setEnabled(activation);
		discreteCovolutionMenu1.setEnabled(activation);
		sobelFilter2Menu.setEnabled(activation);
		smoothingMenu.setEnabled(activation);
		conservativeSmoothingMenu.setEnabled(activation);
		medianFilteringMenu.setEnabled(activation);
		averageFilteringMenu.setEnabled(activation);
		histogramMenu.setEnabled(activation);
		cumulativeHistogramMenu.setEnabled(activation);
		coordinateField.setEnabled(activation);
		fixedZoomCombo.setEnabled(activation);
		undoButton.setEnabled(activation);
		redoButton.setEnabled(activation);
		zoomInBouton.setEnabled(activation);
		zoomOutBouton.setEnabled(activation);
		saveButton.setEnabled(activation);
		papertTurtleMenu.setEnabled(activation);
		thickeningMenu.setEnabled(activation);
		// labelingMenu.setEnabled(activation);
	}
	
	private void openProject(File file) throws IOException {
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
			ImageProcessingProjectFile project = (ImageProcessingProjectFile) inputStream.readObject();
			System.out.println("[INFO] Open an existing project");
			new Thread(() -> {
				SwingUtilities.invokeLater(() -> {
					processingBar.setVisible(true);
				});
				imageViewer.clearObserver();
				imageViewer.addObserver((coordinates) -> {
					SwingUtilities.invokeLater(() -> {
						coordinateField.setText(coordinates[0] + "," + coordinates[1]);
					});
				});
				lastLocation = file.getParent();
				BufferedImage image = new BufferedImage(project.getImageWidth(), project.getImageHeight(), project.getImageType());
				ImageLoader imageLoader = new ImageLoader(image);
				imageLoader.setImageOriginalExtension(project.getImageFileExtension());
				RGBA[][] imageColor = project.getImage();
				
				applyImageChange(imageColor, image);
				
				applicationHistory = new ApplicationHistory<>();
				applicationHistory.append(imageColor);
				final ImageLoader imageHelperCopy = imageLoader;
				imageLoader.setOriginalColor(imageColor);
				if (!projectHistoryModel.saveProject(file.getAbsolutePath()) && projectHistoryModel.addProject(new DBProjectHistory(file.getName(), file.getAbsolutePath()))) {
					initProjectHistory();
				}
				projectNameLocation = file.getAbsolutePath();
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageHelperCopy);
					imageViewer.repaint();
					imageSizeLab.setText(imageColor.length + " x " + imageColor[0].length);
					enableMenus();
					currentTitle = file.getName();
					setTitle("(" + translationModel.get(language, "registered") + ") - " + currentTitle);
					processingBar.setVisible(false);
				});
			}).start();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ClassNotFoundException ex) {
			System.out.println("[WARNING] I/O stream exception");
			Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private void initProjectHistory() {
		List<DBProjectHistory> recentProjects = projectHistoryModel.selectionnerTout();
		recentsProjectsMenu.removeAll();
		recentProjects.stream().map((recentProject) -> {
			JMenuItem projectMenu = new JMenuItem(recentProject.getName());
			projectMenu.setToolTipText(recentProject.getLocation());
			projectMenu.setIcon(new ImageIcon(getClass().getResource("/emit/ipcv/images/history.png")));
			projectMenu.addActionListener((e) -> {
				new Thread(() -> {
					try {
						openProject(new File(recentProject.getLocation()));
					} catch (IOException ex) {
						Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
					}
				}).start();
			});
			return projectMenu;
		}).forEachOrdered((cheminImageMenu) -> {
			recentsProjectsMenu.add(cheminImageMenu);
		});
		if (recentProjects.size() > 0) {
			JMenuItem deleteProjectHistory = new JMenuItem(translationModel.get(language, "clear_project_history").toString());
			deleteProjectHistory.setIcon(new ImageIcon(getClass().getResource("/emit/ipcv/images/delete.png")));
			deleteProjectHistory.addActionListener((ActionEvent e) -> {
				String[] buttons = new String[]{translationModel.get(language, "registrered").toString(), translationModel.get(language, "cancel").toString()};
				int confirmation = JOptionPane.showOptionDialog(rootPanel, translationModel.get(language, "confirm_project_history_deletion_message").toString(), translationModel.get(language, "confirm_project_history_deletion_message").toString(), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[0]);
				if (confirmation == JOptionPane.OK_OPTION) {
					projectHistoryModel.deleteAll();
					initProjectHistory();
				}
			});
			recentsProjectsMenu.add(new JSeparator());
			recentsProjectsMenu.add(deleteProjectHistory);
		} else {
			JMenuItem noProject = new JMenuItem(translationModel.get(language, "recent_project_empty").toString());
			noProject.setEnabled(false);
			recentsProjectsMenu.add(noProject);
		}
	}
	
	private void saveAsProjectAndClose() {
		ProjectExplorer projectExplorer = new ProjectExplorer(lastLocation).setTranslation(translationModel).setLanguage(language);
		int acceptation = projectExplorer.saveAs(rootPanel);
		if (acceptation == JOptionPane.OK_OPTION) {
			new Thread(() -> {
				SwingUtilities.invokeLater(() -> {
					processingBar.setVisible(true);
				});
				ImageLoader imageLoader = imageViewer.getImageLoader();
				File image = projectExplorer.getSelectedFile();
				String absolutePath = image.getAbsolutePath();
				// checking file extension
				String selectedExtension = projectExplorer.getFileFilter().getDescription();
				selectedExtension = selectedExtension.substring(selectedExtension.lastIndexOf("*.") + 2, selectedExtension.length() - 1);
				if (image.getName().contains(".")) {
					boolean isExtensionExact = false;
					for (String extension : projectExplorer.getExtensionEnregistrement()) {
						String filename = image.getName();
						if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase(extension)) {
							isExtensionExact = true;
							selectedExtension = extension;
						}
					}
					if (!isExtensionExact) {
						absolutePath += "." + selectedExtension;
					}
				} // checking file extension
				else {
					absolutePath += "." + selectedExtension;
				}
				File output = new File(absolutePath);
				ImageProcessingProjectFile project = new ImageProcessingProjectFile();
				project.setImage(imageLoader.getOriginalColor());
				project.setImageFileExtension(imageLoader.getImageOriginalExtension());
				BufferedImage bufferedImage = imageLoader.getBufferedImage();
				project.setImageWidth(bufferedImage.getWidth());
				project.setImageHeight(bufferedImage.getHeight());
				project.setImageType(bufferedImage.getType());
				try {
					ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(output));
					outputStream.writeObject(project);
					outputStream.flush();
					outputStream.close();
				} catch (IOException ex) {
					Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
				}
				if (!projectHistoryModel.saveProject(output.getAbsolutePath()) && projectHistoryModel.addProject(new DBProjectHistory(output.getName(), output.getAbsolutePath()))) {
					initProjectHistory();
				}
				SwingUtilities.invokeLater(() -> {
					processingBar.setVisible(false);
					currentTitle = "(" + translationModel.get(language, "registered") + ") - " + output.getName();
					setTitle(currentTitle);
				});
				projectNameLocation = output.getAbsolutePath();
				hasChanges = false;
				newProject = false;
				String[] buttons = new String[]{translationModel.get(language, "close").toString(), translationModel.get(language, "cancel").toString()};
				int quitConfirmation = JOptionPane.showOptionDialog(rootPanel, translationModel.get(language, "quit_application_request_message").toString(), translationModel.get(language, "quit_application_request_quit_application_request_title").toString(), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, buttons, buttons[0]);
				if (quitConfirmation == JOptionPane.OK_OPTION) {
					System.out.println("[INFO] Close the application");
					System.exit(0);
				}
			}).start();
		}
	}
	
	private void saveAndCloseProject() {
		new Thread(() -> {
			SwingUtilities.invokeLater(() -> {
				processingBar.setVisible(true);
			});
			ImageLoader imageLoader = imageViewer.getImageLoader();
			File output = new File(projectNameLocation);
			ImageProcessingProjectFile project = new ImageProcessingProjectFile();
			project.setImage(imageLoader.getOriginalColor());
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			project.setImageWidth(bufferedImage.getWidth());
			project.setImageHeight(bufferedImage.getHeight());
			project.setImageType(bufferedImage.getType());
			project.setImageFileExtension(imageLoader.getImageOriginalExtension());
			try {
				ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(output));
				outputStream.writeObject(project);
				outputStream.flush();
				outputStream.close();
			} catch (IOException ex) {
				Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
			}
			if (!projectHistoryModel.saveProject(output.getAbsolutePath()) && projectHistoryModel.addProject(new DBProjectHistory(output.getName(), output.getAbsolutePath()))) {
				initProjectHistory();
			}
			SwingUtilities.invokeLater(() -> {
				processingBar.setVisible(false);
				currentTitle = "(" + translationModel.get(language, "registered") + ") - " + output.getName();
				setTitle(currentTitle);
			});
			hasChanges = false;
			String[] boutons = new String[]{translationModel.get(language, "close").toString(), translationModel.get(language, "cancel").toString()};
			int quitConfirmation = JOptionPane.showOptionDialog(rootPanel, translationModel.get(language, "quit_application_request_message").toString(), translationModel.get(language, "quit_application_request_title").toString(), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, boutons, boutons[0]);
			if (quitConfirmation == JOptionPane.OK_OPTION) {
				System.out.println("[INFO] Close the application");
				System.exit(0);
			}
		}).start();
	}
	
	private void newProjectPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Browsing new image file");
			ImageExplorer imageChooser = new ImageExplorer(lastLocation).setTranslation(translationModel).setLanguage(language);
			int selectFile = imageChooser.open(rootPanel);
			if (selectFile == JFileChooser.APPROVE_OPTION) {
				File selectedFile = imageChooser.getSelectedFile();
				System.out.println("[INFO] Selected new file : \"" + selectedFile.getAbsolutePath() + "\"");
				openNewImage(selectedFile);
			} else {
				System.out.println("[INFO] Browsing new image file canceled");
			}
		}).start();
	}
	
	private void openProjectPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Browsing project file");
			ProjectExplorer projectExplorer = new ProjectExplorer(lastLocation).setTranslation(translationModel).setLanguage(language);
			int approbation = projectExplorer.open(rootPanel);
			if (approbation == JFileChooser.APPROVE_OPTION) {
				SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
				File selectedFile = projectExplorer.getSelectedFile();
				System.out.println("[INFO] Selected project file : \"" + selectedFile.getAbsolutePath() + "\"");
				try {
					openProject(selectedFile);
				} catch (IOException ex) {
					Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
				}
				SwingUtilities.invokeLater(() -> processingBar.setVisible(false));
			} else {
				System.out.println("[INFO] Browsing project file canceled");
			}
		}).start();
	}
	
	private void undoPerformed() {
		if (applicationHistory.hasPreviousElement()) {
			new Thread(() -> {
				SwingUtilities.invokeLater(() -> {
					processingBar.setVisible(true);
				});
				final ImageLoader imageLoader = imageViewer.getImageLoader();
				final RGBA[][] imageColor = applicationHistory.undo();
				BufferedImage bufferedImage = imageLoader.getBufferedImage();
				applyImageChange(imageColor, bufferedImage);
				imageLoader.setOriginalColor(imageColor);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
				});
			}).start();
		}
	}
	
	private void redoPerformed() {
		if (applicationHistory.hasMoreElement()) {
			new Thread(() -> {
				SwingUtilities.invokeLater(() -> {
					processingBar.setVisible(true);
				});
				final ImageLoader imageHelper = imageViewer.getImageLoader();
				final RGBA[][] imageColor = applicationHistory.redo();
				BufferedImage bufferedImage = imageHelper.getBufferedImage();
				applyImageChange(imageColor, bufferedImage);
				imageHelper.setOriginalColor(imageColor);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageHelper);
					imageViewer.repaint();
					processingBar.setVisible(false);
				});
			}).start();
		}
	}
	
	private void changeUserScale() {
		final String selectedEchelle = fixedZoomCombo.getSelectedItem().toString();
		final int scale = Integer.valueOf(selectedEchelle.substring(0, selectedEchelle.indexOf('%')));
		imageViewer.setUserScale(scale);
	}
	
	private void erosion() {
		new Thread(() -> {
			StructuringElementDialog structuringElementDialog = new StructuringElementDialog(this, true).setTranslation(translationModel).setLanguage(language).initLanguage();
			structuringElementDialog.addOrigineListener((values) -> {
				new Thread(() -> {
					System.out.println(String.format("[INFO] Erosion"));
					SwingUtilities.invokeLater(() -> {
						processingBar.setVisible(true);
					});
					ImageLoader imageLoader = imageViewer.getImageLoader();
					BufferedImage bufferedImage = null;
					bufferedImage = imageLoader.getBufferedImage();
					if(setting.isUseLocalHardware()) {
						int x = (int) values[0];
						int y = (int) values[1];
						int[][] structuringElement = (int[][]) values[2];
						Operations operation = new Erosion(structuringElement, x, y, new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale());
						int[][] newGrayscale = operation.execute();
						
						RgbImageHelper rgbImageHelper = new RgbImageHelper(imageLoader.getOriginalColor());
						RGBA[][] fullColorImage = new RgbImageHelper(newGrayscale).setAlphas(rgbImageHelper.getAlphas()).getImage();
						applyImageChange(fullColorImage, bufferedImage);
						imageLoader.setOriginalColor(fullColorImage);
						
						applicationHistory.append(fullColorImage);
						SwingUtilities.invokeLater(() -> {
							imageViewer.setImageLoader(imageLoader);
							imageViewer.repaint();
							processingBar.setVisible(false);
							currentTitle = newProject ? defaultTitle : currentTitle;
							setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
						});
						hasChanges = true;
					} else {
						
					}
				}).start();
			});
			structuringElementDialog.setVisible(true);
		}).start();
	}
	
	private void otsuThresholding() {
		new Thread(() -> {
			SwingUtilities.invokeLater(() -> {
				processingBar.setVisible(true);
			});
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = null;
			bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				final int threshold = new Otsu(new RgbImageHelper(image).getGrayscale()).execute();
				System.out.println(String.format("[INFO] Otsu binarisation using \"%d\" as threshold", threshold));
				ThresholdingBinarization transformation = new ThresholdingBinarization(new RgbImageHelper(image).getGrayscale(), threshold);
				int[][] newGrayscale = transformation.execute();
				
				RgbImageHelper rgbImageHelper = new RgbImageHelper(imageLoader.getOriginalColor());
				RGBA[][] fullColorImage = new RgbImageHelper(newGrayscale).setAlphas(rgbImageHelper.getAlphas()).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					Map<String, Object> outputData = new HashMap<>();
					outputData.put("image", imageLoader.getOriginalColor());
					objectOutputStream.writeObject(outputPacket.setHeader(Const.BINARIZATION).setData(outputData));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void dilation() {
		new Thread(() -> {
			StructuringElementDialog structuringElementDialog = new StructuringElementDialog(this, true).setTranslation(translationModel).setLanguage(language).initLanguage();
			structuringElementDialog.addOrigineListener((values) -> {
				new Thread(() -> {
					System.out.println("[INFO] Dilation");
					SwingUtilities.invokeLater(() -> {
						processingBar.setVisible(true);
					});
					int x = (int) values[0];
					int y = (int) values[1];
					int[][] structuringElement = (int[][]) values[2];
					ImageLoader imageLoader = imageViewer.getImageLoader();
					BufferedImage bufferedImage = null;
					bufferedImage = imageLoader.getBufferedImage();
					if(setting.isUseLocalHardware()) {
						Operations operation = new Dilation(structuringElement, x, y, new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale());
						int[][] newGrayscale = operation.execute();
						
						RgbImageHelper rgbImageHelper = new RgbImageHelper(imageLoader.getOriginalColor());
						RGBA[][] fullColorImage = new RgbImageHelper(newGrayscale).setAlphas(rgbImageHelper.getAlphas()).getImage();
						applyImageChange(fullColorImage, bufferedImage);
						imageLoader.setOriginalColor(fullColorImage);
						
						applicationHistory.append(fullColorImage);
						SwingUtilities.invokeLater(() -> {
							imageViewer.setImageLoader(imageLoader);
							imageViewer.repaint();
							processingBar.setVisible(false);
							currentTitle = newProject ? defaultTitle : currentTitle;
							setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
						});
						hasChanges = true;
					} else {
						
					}
				}).start();
			});
			structuringElementDialog.setVisible(true);
		}).start();
	}
	
	private void allOrNothing() {
		new Thread(() -> {
			AllOrNothingDialog allOrNothingDialog = new AllOrNothingDialog(this, true).setTranslation(translationModel).setLanguage(language).initLanguage();
			allOrNothingDialog.addOrigineListener((values) -> {
				new Thread(() -> {
					System.out.println("[INFO] All or nothing");
					SwingUtilities.invokeLater(() -> {
						processingBar.setVisible(true);
					});
					ImageLoader imageLoader = imageViewer.getImageLoader();
					BufferedImage bufferedImage = null;
					bufferedImage = imageLoader.getBufferedImage();
					if(setting.isUseLocalHardware()) {
						int[][] structuringElement = values;
						Operations operation = new AllOrNothing(structuringElement, new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale());
						int[][] newGrayscale = operation.execute();
						RGBA[][] fullColorImage = new RgbImageHelper(newGrayscale).getImage();
						applyImageChange(fullColorImage, bufferedImage);
						imageLoader.setOriginalColor(fullColorImage);
						applicationHistory.append(fullColorImage);
						SwingUtilities.invokeLater(() -> {
							imageViewer.setImageLoader(imageLoader);
							imageViewer.repaint();
							processingBar.setVisible(false);
							currentTitle = newProject ? defaultTitle : currentTitle;
							setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
						});
						hasChanges = true;
					} else {
						
					}
				}).start();
			});
			allOrNothingDialog.setVisible(true);
		}).start();
	}
	
	private void opening() {
		new Thread(() -> {
			StructuringElementDialog openingDialog = new StructuringElementDialog(this, true).setTranslation(translationModel).setLanguage(language).initLanguage();
			openingDialog.addOrigineListener(values -> {
				new Thread(() -> {
					System.out.println("[INFO] Opening");
					SwingUtilities.invokeLater(() -> {
						processingBar.setVisible(true);
					});
					ImageLoader imageLoader = imageViewer.getImageLoader();
					BufferedImage bufferedImage = null;
					bufferedImage = imageLoader.getBufferedImage();
					if(setting.isUseLocalHardware()) {
						Operations erosion, dilation;
						int x = (int) values[0];
						int y = (int) values[1];
						int[][] structuringElement = (int[][]) values[2];
						erosion = new Erosion(structuringElement, x, y, new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale());
						int[][] operationErosion = erosion.execute();
						dilation = new Dilation(structuringElement, x, y, operationErosion);
						int[][] newGrayscale = dilation.execute();
						
						RgbImageHelper rgbImageHelper = new RgbImageHelper(imageLoader.getOriginalColor());
						RGBA[][] fullColorImage = new RgbImageHelper(newGrayscale).setAlphas(rgbImageHelper.getAlphas()).getImage();
						applyImageChange(fullColorImage, bufferedImage);
						imageLoader.setOriginalColor(fullColorImage);
						
						applicationHistory.append(fullColorImage);
						SwingUtilities.invokeLater(() -> {
							imageViewer.setImageLoader(imageLoader);
							imageViewer.repaint();
							processingBar.setVisible(false);
							currentTitle = newProject ? defaultTitle : currentTitle;
							setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
						});
						hasChanges = true;
					} else {
					
					}
				}).start();
			});
			openingDialog.setVisible(true);
		}).start();
	}
	
	private void initLanguages() {
		System.out.println("[INFO] Loading application language");
		UIManager.put("FileChooser.cancelButtonText", translationModel.get(language, "cancel").toString());
		
		if (projectNameLocation == null) {
			defaultTitle = translationModel.get(language, "new_project") + ".imgproc";
			setTitle(translationModel.get(language, "application_title").toString());
		} else {
			currentTitle = newProject ? defaultTitle : currentTitle;
			setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
		}
		
		fileMenu.setText(translationModel.get(language, "file").toString());
		newProjectMenu.setText(translationModel.get(language, "new_project").toString());
		openProjectMenu.setText(translationModel.get(language, "open_project").toString());
		recentsProjectsMenu.setText(translationModel.get(language, "recents_projects").toString());
		saveMenu.setText(translationModel.get(language, "save").toString());
		saveAsMenu.setText(translationModel.get(language, "save_as").toString());
		exportImageMenu.setText(translationModel.get(language, "export_image").toString());
		settingMenu.setText(translationModel.get(language, "setting").toString());
		exitMenu.setText(translationModel.get(language, "exit").toString());
		editionMenu.setText(translationModel.get(language, "edition").toString());
		undoMenu.setText(translationModel.get(language, "undo").toString());
		redoMenu.setText(translationModel.get(language, "redo").toString());
		planTransformationMenu.setText(translationModel.get(language, "plane_transformation").toString());
		shearMenu.setText(translationModel.get(language, "shear").toString());
		homothetyMenu.setText(translationModel.get(language, "homothety").toString());
		rotationMenu.setText(translationModel.get(language, "rotation").toString());
		verticalSymmetry.setText(translationModel.get(language, "vertical_symmetry").toString());
		horizontalSymmetryMenu.setText(translationModel.get(language, "horizontal_symmetry").toString());
		symmetryInTheCenterMenu.setText(translationModel.get(language, "symmetry_in_the_center").toString());
		pointTransformationMenu.setText(translationModel.get(language, "point_transformation").toString());
		invertGrayscaleMenu.setText(translationModel.get(language, "invert_grayscale").toString());
		binarizationMenu.setText(translationModel.get(language, "binarization").toString());
		fixedThresholdMenu.setText(translationModel.get(language, "fixed_threshold").toString());
		otsuThresholdMenu.setText(translationModel.get(language, "otsu_threshold").toString());
		histogramEqualizationMenu.setText(translationModel.get(language, "histogram_equalization").toString());
		dynamicDisplayMenu.setText(translationModel.get(language, "dynamic_display").toString());
		localProcessingMenu.setText(translationModel.get(language, "local_processing").toString());
		discreteCovolutionMenu.setText(translationModel.get(language, "discrete_covolution").toString());
		smoothingMenu.setText(translationModel.get(language, "smoothing").toString());
		mediumFilteringMenu.setText(translationModel.get(language, "medium_filtering").toString());
		gaussianFilteringMenu.setText(translationModel.get(language, "gaussian_filtering").toString());
		conservativeSmoothingMenu.setText(translationModel.get(language, "conservative_smoothing").toString());
		medianFilteringMenu.setText(translationModel.get(language, "median_filtering").toString());
		averageFilteringMenu.setText(translationModel.get(language, "average_filtering").toString());
		operationMenu.setText(translationModel.get(language, "operation").toString());
		erosionMenu.setText(translationModel.get(language, "erosion").toString());
		dilationMenu.setText(translationModel.get(language, "dilation").toString());
		openingMenu.setText(translationModel.get(language, "opening").toString());
		allOrNothingMenu.setText(translationModel.get(language, "all_or_nothing").toString());
		topHatOpeningMenu.setText(translationModel.get(language, "top_hat_opening").toString());
		topHatClosureMenu.setText(translationModel.get(language, "top_hat_closure").toString());
		graphicsMenu.setText(translationModel.get(language, "graphics").toString());
		histogramMenu.setText(translationModel.get(language, "histogram").toString());
		cumulativeHistogramMenu.setText(translationModel.get(language, "cumulative_histogram").toString());
		formRecognitionMenu.setText(translationModel.get(language, "form_recognition").toString());
		papertTurtleMenu.setText(translationModel.get(language, "papert_turtle").toString());
		labelingMenu.setText(translationModel.get(language, "labeling").toString());
		processingBar.setString(translationModel.get(language, "processing") + "...");
		coordinateLab.setText(translationModel.get(language, "coordinate").toString());
		fixedZoomLab.setText(translationModel.get(language, "fixed_zoom").toString());
		thickeningMenu.setText(translationModel.get(language, "thickening").toString());
		
		if (imageViewer.getImageLoader() == null) {
			imageSizeLab.setText(translationModel.get(language, "image_size").toString());
		}
		
		newProjectButton.setToolTipText(translationModel.get(language, "new_project").toString());
		openProjectButton.setToolTipText(translationModel.get(language, "open_project").toString());
		saveButton.setToolTipText(translationModel.get(language, "save").toString());
		undoButton.setToolTipText(translationModel.get(language, "undo").toString());
		redoButton.setToolTipText(translationModel.get(language, "redo").toString());
		zoomInBouton.setToolTipText(translationModel.get(language, "zoom_in").toString());
		zoomOutBouton.setToolTipText(translationModel.get(language, "zoom_out").toString());
		
		imageViewer.setImagePreviewMessage(translationModel.get(language, "image_preview").toString());
		initProjectHistory();
	}
	
	private void loadSettings() {
		if (!this.settingModel.languageDefined()) {
			this.settingModel.setLanguage(1);
		}
		this.language = this.settingModel.getLanguage();
		this.setting = this.settingModel.loadSetting();
	}
	
	private void exportImagePerformed() {
		ImageExplorer imageExplorer = new ImageExplorer(lastLocation).setTranslation(translationModel).setLanguage(language);
		ImageLoader imageLoader = imageViewer.getImageLoader();
		imageExplorer.setOutputExtension(imageLoader.getImageOriginalExtension());
		int acceptation = imageExplorer.saveAs(rootPanel);
		if (acceptation == JOptionPane.OK_OPTION) {
			final JFrame that = this;
			new Thread(() -> {
				SwingUtilities.invokeLater(() -> {
					processingBar.setVisible(true);
				});
				File image = imageExplorer.getSelectedFile();
				if (Files.isWritable(image.getParentFile().toPath())) {
					String absolutePath = image.getAbsolutePath();
					String selectedExtension = imageExplorer.getFileFilter().getDescription();
					selectedExtension = selectedExtension.substring(selectedExtension.lastIndexOf("*.") + 2, selectedExtension.length() - 1);
					if (image.getName().contains(".")) {
						boolean isExtensionExact = false;
						for (String extension : imageExplorer.getSaveExtensions()) {
							String filename = image.getName();
							if (filename.substring(filename.lastIndexOf(".") + 1).equalsIgnoreCase(extension)) {
								isExtensionExact = true;
								selectedExtension = extension;
							}
						}
						if (!isExtensionExact) {
							absolutePath += "." + selectedExtension;
						}
					} else {
						absolutePath += "." + selectedExtension;
					}
					File output = new File(absolutePath);
					try {
						ImageIO.write(imageLoader.getBufferedImage(), selectedExtension, output);
					} catch (IOException ex) {
						Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
					}
				} else {
					JOptionPane.showMessageDialog(that, translationModel.get(language, "write_permission_error_message").toString().replaceAll("\\[br\\]", "\n"), translationModel.get(language, "write_permission_error_title").toString(), JOptionPane.ERROR_MESSAGE);
				}
				SwingUtilities.invokeLater(() -> {
					processingBar.setVisible(false);
				});
			}).start();
		}
	}
	
	private void openShearPerformed() {
		new Thread(() -> {
			ShearDialog shearDialog = new ShearDialog(this, true).setTranslation(translationModel).setLanguage(language).initLanguage();
			shearDialog.addObserver(constants -> {
				new Thread(() -> {
					System.out.println("[INFO] Shear transformation");
					SwingUtilities.invokeLater(() -> {
						processingBar.setVisible(true);
					});
					ImageLoader imageLoader = imageViewer.getImageLoader();
					BufferedImage bufferedImage = null;
					bufferedImage = imageLoader.getBufferedImage();
					RGBA[][] image = imageLoader.getOriginalColor();
          if(setting.isUseLocalHardware()) {
	          RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
	          int[][] alphaOperation = new Shear2D(rgbImageHelper.getAlphas(), constants).execute(),
		          redOperation = new Shear2D(rgbImageHelper.getReds(), constants).execute(),
		          greenOperation = new Shear2D(rgbImageHelper.getGreens(), constants).execute(),
		          blueOperation = new Shear2D(rgbImageHelper.getBlues(), constants).execute();
	
	          RGBA[][] fullColorImage = new RgbImageHelper(alphaOperation, redOperation, greenOperation, blueOperation).getImage();
	          applyImageChange(fullColorImage, bufferedImage);
	          SwingUtilities.invokeLater(() -> {
		          imageViewer.setImageLoader(imageLoader);
		          imageViewer.repaint();
		          processingBar.setVisible(false);
		          currentTitle = newProject ? defaultTitle : currentTitle;
		          setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
	          });
	          hasChanges = true;
          } else {
	          try {
	          	Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
		          ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		          DataPacket outputPacket = new DataPacket();
		          Map<String, Object> outputData = new HashMap<>();
		          outputData.put("constants", constants);
		          outputData.put("image", image);
		          objectOutputStream.writeObject(outputPacket.setHeader(Const.SHEAR).setData(outputData));
		          objectOutputStream.flush();
		          
		          ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
		          DataPacket result = (DataPacket) objectInputStream.readObject();
		          RGBA[][] fullColorImage = (RGBA[][]) result.getData();
		          applyImageChange(fullColorImage, bufferedImage);
		          imageLoader.setOriginalColor(fullColorImage);
		          applicationHistory.append(fullColorImage);
		          
		          objectOutputStream.close();
		          objectInputStream.close();
		          socket.close();
	          } catch (IOException | ClassNotFoundException e) {
		          e.printStackTrace();
	          } finally {
		          SwingUtilities.invokeLater(() -> {
			          imageViewer.setImageLoader(imageLoader);
			          imageViewer.repaint();
			          processingBar.setVisible(false);
			          currentTitle = newProject ? defaultTitle : currentTitle;
			          setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
		          });
		          hasChanges = true;
	          }
          }
				}).start();
			});
			shearDialog.setVisible(true);
		}).start();
	}
	
	private void openHomothetyPerformed() {
		new Thread(() -> {
			HomothetyDialog homothetyDialog = new HomothetyDialog(this, true).setTranslation(translationModel).setLanguage(language).initLanguage();
			homothetyDialog.addObserver(constants -> {
				new Thread(() -> {
					System.out.println("[INFO] Homothety transformation");
					SwingUtilities.invokeLater(() -> {
						processingBar.setVisible(true);
					});
					ImageLoader imageLoader = imageViewer.getImageLoader();
					BufferedImage bufferedImage = null;
					bufferedImage = imageLoader.getBufferedImage();
					RGBA[][] image = imageLoader.getOriginalColor();
					if(setting.isUseLocalHardware()) {
						RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
						
						int[][] alphaOperation = new Homothety2D(rgbImageHelper.getAlphas(), constants).execute(),
							redOperation = new Homothety2D(rgbImageHelper.getReds(), constants).execute(),
							greenOperation = new Homothety2D(rgbImageHelper.getGreens(), constants).execute(),
							blueOperation = new Homothety2D(rgbImageHelper.getBlues(), constants).execute();
						
						RGBA[][] fullColorImage = new RgbImageHelper(alphaOperation, redOperation, greenOperation, blueOperation).getImage();
						applyImageChange(fullColorImage, bufferedImage);
						imageLoader.setOriginalColor(fullColorImage);
						applicationHistory.append(fullColorImage);
						SwingUtilities.invokeLater(() -> {
							imageViewer.setImageLoader(imageLoader);
							imageViewer.repaint();
							processingBar.setVisible(false);
							currentTitle = newProject ? defaultTitle : currentTitle;
							setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
						});
						hasChanges = true;
					} else {
						try {
							Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
							ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
							DataPacket outputPacket = new DataPacket();
							Map<String, Object> outputData = new HashMap<>();
							outputData.put("constants", constants);
							outputData.put("image", image);
							objectOutputStream.writeObject(outputPacket.setHeader(Const.HOMOTHETY).setData(outputData));
							objectOutputStream.flush();
							ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
							DataPacket result = (DataPacket) objectInputStream.readObject();
							RGBA[][] fullColorImage = (RGBA[][]) result.getData();
							applyImageChange(fullColorImage, bufferedImage);
							imageLoader.setOriginalColor(fullColorImage);
							applicationHistory.append(fullColorImage);
							
							objectOutputStream.close();
							objectInputStream.close();
							socket.close();
						} catch (IOException | ClassNotFoundException e) {
							e.printStackTrace();
						} finally {
							SwingUtilities.invokeLater(() -> {
								imageViewer.setImageLoader(imageLoader);
								imageViewer.repaint();
								processingBar.setVisible(false);
								currentTitle = newProject ? defaultTitle : currentTitle;
								setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
							});
							hasChanges = true;
						}
					}
				}).start();
			});
			homothetyDialog.setVisible(true);
		}).start();
	}
	
	private void openRotationPerformed() {
		new Thread(() -> {
			RotationDialog rotationDialog = new RotationDialog(this, true).setTranslation(translationModel).setLanguage(language).initLanguage();
			rotationDialog.addObserver(angle -> {
				new Thread(() -> {
					System.out.println(String.format("[INFO] Rotation transformation to %d°", angle));
					SwingUtilities.invokeLater(() -> {
						processingBar.setVisible(true);
					});
					final ImageLoader imageLoader = imageViewer.getImageLoader();
					BufferedImage bufferedImage = null;
					bufferedImage = imageLoader.getBufferedImage();
					RGBA[][] image = imageLoader.getOriginalColor();
					if(setting.isUseLocalHardware()) {
						RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
						
						int[][] alphaRotation = new Rotation2D(rgbImageHelper.getAlphas(), angle).execute(),
							redRotation = new Rotation2D(rgbImageHelper.getReds(), angle).execute(),
							greenRotation = new Rotation2D(rgbImageHelper.getGreens(), angle).execute(),
							blueRotation = new Rotation2D(rgbImageHelper.getBlues(), angle).execute();
						
						RGBA[][] fullColorImage = new RgbImageHelper(alphaRotation, redRotation, greenRotation, blueRotation).getImage();
						applyImageChange(fullColorImage, bufferedImage);
						imageLoader.setOriginalColor(fullColorImage);
						
						applicationHistory.append(fullColorImage);
						SwingUtilities.invokeLater(() -> {
							imageViewer.setImageLoader(imageLoader);
							imageViewer.repaint();
							processingBar.setVisible(false);
							currentTitle = newProject ? defaultTitle : currentTitle;
							setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
						});
						hasChanges = true;
					} else {
						try {
							Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
							ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
							DataPacket outputPacket = new DataPacket();
							Map<String, Object> outputData = new HashMap<>();
							outputData.put("angle", angle);
							outputData.put("image", image);
							objectOutputStream.writeObject(outputPacket.setHeader(Const.ROTATION).setData(outputData));
							objectOutputStream.flush();
							ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
							DataPacket result = (DataPacket) objectInputStream.readObject();
							RGBA[][] fullColorImage = (RGBA[][]) result.getData();
							applyImageChange(fullColorImage, bufferedImage);
							imageLoader.setOriginalColor(fullColorImage);
							applicationHistory.append(fullColorImage);
							
							objectOutputStream.close();
							objectInputStream.close();
							socket.close();
						} catch (IOException | ClassNotFoundException e) {
							e.printStackTrace();
						} finally {
							SwingUtilities.invokeLater(() -> {
								imageViewer.setImageLoader(imageLoader);
								imageViewer.repaint();
								processingBar.setVisible(false);
								currentTitle = newProject ? defaultTitle : currentTitle;
								setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
							});
							hasChanges = true;
						}
					}
				}).start();
			});
			rotationDialog.setVisible(true);
		}).start();
	}
	
	private void verticalSymmetryPerformed() {
		new Thread(() -> {
			System.out.println(String.format("[INFO] Vertical symmetry"));
			SwingUtilities.invokeLater(() -> {
				processingBar.setVisible(true);
			});
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = null;
			bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] alphaOperation = new SymmetryX2D(rgbImageHelper.getAlphas()).execute(),
					redOperation = new SymmetryX2D(rgbImageHelper.getReds()).execute(),
					greenOperation = new SymmetryX2D(rgbImageHelper.getGreens()).execute(),
					blueOperation = new SymmetryX2D(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(alphaOperation, redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
				});
				hasChanges = true;
				currentTitle = newProject ? defaultTitle : currentTitle;
				setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.VERTICAL_SYMMETRY).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void horizontalSymmetryPerformed() {
		new Thread(() -> {
			System.out.println(String.format("[INFO] Horizontal symmetry"));
			SwingUtilities.invokeLater(() -> {
				processingBar.setVisible(true);
			});
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] alphaOperation = new SymmetryY2D(rgbImageHelper.getAlphas()).execute(),
					redOperation = new SymmetryY2D(rgbImageHelper.getReds()).execute(),
					greenOperation = new SymmetryY2D(rgbImageHelper.getGreens()).execute(),
					blueOperation = new SymmetryY2D(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(alphaOperation, redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
				});
				hasChanges = true;
				currentTitle = newProject ? defaultTitle : currentTitle;
				setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.HORIZONTAL_SYMMETRY).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void symmetryInCenterPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Symmetry in center");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] alphaOperation = new SymmetryO2D(rgbImageHelper.getAlphas()).execute(),
					redOperation = new SymmetryO2D(rgbImageHelper.getReds()).execute(),
					greenOperation = new SymmetryO2D(rgbImageHelper.getGreens()).execute(),
					blueOperation = new SymmetryO2D(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(alphaOperation, redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.SYMMETRY_IN_THE_CENTER).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void openFixedThresholdPerformed() {
		new Thread(() -> {
			ThresholdingDialog thresholdingDialog = new ThresholdingDialog(this, true).setTranslation(translationModel).setLanguage(language).initLanguage();
			thresholdingDialog.addObserver(threshold -> {
				System.out.printf("[INFO] Binarisation using \"%d\" as threshold%n", threshold);
				new Thread(() -> {
					SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
					final ImageLoader imageLoader = imageViewer.getImageLoader();
					BufferedImage bufferedImage = null;
					bufferedImage = imageLoader.getBufferedImage();
					if(setting.isUseLocalHardware()) {
						int[][] grayscale = new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale();
						ThresholdingBinarization transformation = new ThresholdingBinarization(grayscale, threshold);
						int[][] newGrayscale = transformation.execute();
						
						RgbImageHelper rgbImageHelper = new RgbImageHelper(imageLoader.getOriginalColor());
						RGBA[][] fullColorImage = new RgbImageHelper(newGrayscale).setAlphas(rgbImageHelper.getAlphas()).getImage();
						applyImageChange(fullColorImage, bufferedImage);
						imageLoader.setOriginalColor(fullColorImage);
						
						applicationHistory.append(fullColorImage);
						SwingUtilities.invokeLater(() -> {
							imageViewer.setImageLoader(imageLoader);
							imageViewer.repaint();
							processingBar.setVisible(false);
							currentTitle = newProject ? defaultTitle : currentTitle;
							setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
						});
						hasChanges = true;
					} else {
						try {
							Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
							ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
							DataPacket outputPacket = new DataPacket();
							Map<String, Object> outputData = new HashMap<>();
							outputData.put("threshold", threshold);
							outputData.put("image-loader", imageLoader);
							objectOutputStream.writeObject(outputPacket.setHeader(Const.BINARIZATION).setData(outputData));
							objectOutputStream.flush();
							ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
							DataPacket result = (DataPacket) objectInputStream.readObject();
							RGBA[][] fullColorImage = (RGBA[][]) result.getData();
							applyImageChange(fullColorImage, bufferedImage);
							imageLoader.setOriginalColor(fullColorImage);
							applicationHistory.append(fullColorImage);
							
							objectOutputStream.close();
							objectInputStream.close();
							socket.close();
						} catch (IOException | ClassNotFoundException e) {
							e.printStackTrace();
						} finally {
							SwingUtilities.invokeLater(() -> {
								imageViewer.setImageLoader(imageLoader);
								imageViewer.repaint();
								processingBar.setVisible(false);
								currentTitle = newProject ? defaultTitle : currentTitle;
								setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
							});
							hasChanges = true;
						}
					}
				}).start();
			});
			thresholdingDialog.setVisible(true);
		}).start();
	}
	
	private void invertColorPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Color inversion");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new InvertGrayscale(rgbImageHelper.getReds()).execute(),
					greenOperation = new InvertGrayscale(rgbImageHelper.getGreens()).execute(),
					blueOperation = new InvertGrayscale(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.COLOR_INVERSION).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void papertTurtlePerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Shape outline from papert turtle");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			if(setting.isUseLocalHardware()){
				PapertTurtle papertTurtle = new PapertTurtle(new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale());
				int[][] newGrayscale = papertTurtle.execute();
				
				RgbImageHelper rgbImageHelper = new RgbImageHelper(imageLoader.getOriginalColor());
				RGBA[][] fullColorImage = new RgbImageHelper(newGrayscale).setAlphas(rgbImageHelper.getAlphas()).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
				});
				hasChanges = true;
				currentTitle = newProject ? defaultTitle : currentTitle;
				setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.PAPERT_TURTLE).setData(imageLoader));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void histogramEqualizationPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Histogram equalization");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[] redHistogram = new HistogramEqualization(rgbImageHelper.getReds()).execute(),
					greenHistogram = new HistogramEqualization(rgbImageHelper.getGreens()).execute(),
					blueHistogram = new HistogramEqualization(rgbImageHelper.getBlues()).execute();
				
				final int lineLength = rgbImageHelper.lineLength(), columnLength = rgbImageHelper.columnLength();
				int[][] redOperation = new int[lineLength][columnLength], greenOperation = new int[lineLength][columnLength], blueOperation = new int[lineLength][columnLength];
				
				// red
				for (int line = 0; line < lineLength; line++) {
					for (int column = 0; column < columnLength; column++) {
						redOperation[line][column] = redHistogram[image[line][column].getRed()];
					}
				}
				
				// green
				for (int line = 0; line < lineLength; line++) {
					for (int column = 0; column < columnLength; column++) {
						greenOperation[line][column] = greenHistogram[image[line][column].getGreen()];
					}
				}
				
				// blue
				for (int line = 0; line < lineLength; line++) {
					for (int column = 0; column < columnLength; column++) {
						blueOperation[line][column] = blueHistogram[image[line][column].getBlue()];
					}
				}
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.HISTOGRAM_EQUALIZATION).setData(imageLoader));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void openDynamicDisplayPerformed() {
		new Thread(() -> {
			DynamicDisplayDialog dynamicDisplayDialog = new DynamicDisplayDialog(this, true).setTranslation(translationModel).setLanguage(language).initLanguage();
			dynamicDisplayDialog.addObserver(minMax -> new Thread(() -> {
				System.out.printf("[INFO] Dynamic display : {min: %d; max: %d}%n", minMax[0], minMax[1]);
				SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
				final ImageLoader imageLoader = imageViewer.getImageLoader();
				BufferedImage bufferedImage = null;
				bufferedImage = imageLoader.getBufferedImage();
				RGBA[][] image = imageLoader.getOriginalColor();
				if(setting.isUseLocalHardware()) {
					RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
					
					int[][] redOperation = new DynamicDisplay(rgbImageHelper.getReds(), minMax[0], minMax[1]).execute(),
						greenOperation = new DynamicDisplay(rgbImageHelper.getGreens(), minMax[0], minMax[1]).execute(),
						blueOperation = new DynamicDisplay(rgbImageHelper.getBlues(), minMax[0], minMax[1]).execute();
					
					RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					
					applicationHistory.append(fullColorImage);
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				} else {
					try {
						Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
						DataPacket outputPacket = new DataPacket();
						Map<String, Object> outputData = new HashMap<>();
						outputData.put("min-max", minMax);
						outputData.put("image", image);
						objectOutputStream.writeObject(outputPacket.setHeader(Const.DYNAMIC_DISPLAY).setData(outputData));
						objectOutputStream.flush();
						ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
						DataPacket result = (DataPacket) objectInputStream.readObject();
						RGBA[][] fullColorImage = (RGBA[][]) result.getData();
						applyImageChange(fullColorImage, bufferedImage);
						imageLoader.setOriginalColor(fullColorImage);
						applicationHistory.append(fullColorImage);
						
						objectOutputStream.close();
						objectInputStream.close();
						socket.close();
					} catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					} finally {
						SwingUtilities.invokeLater(() -> {
							imageViewer.setImageLoader(imageLoader);
							imageViewer.repaint();
							processingBar.setVisible(false);
							currentTitle = newProject ? defaultTitle : currentTitle;
							setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
						});
						hasChanges = true;
					}
				}
			}).start());
			dynamicDisplayDialog.setVisible(true);
		}).start();
	}
	
	private void sobel1CovolutionPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Sobel1 discrete convolution");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(this.setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new SobelFilter1(rgbImageHelper.getReds()).execute(),
					greenOperation = new SobelFilter1(rgbImageHelper.getGreens()).execute(),
					blueOperation = new SobelFilter1(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.SOBEL_FILTER1).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void customCovolution1Performed() {
		new Thread(() -> {
			System.out.println("[INFO] Discrete convolution 1");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new CustomFilter1(rgbImageHelper.getReds()).execute(),
					greenOperation = new CustomFilter1(rgbImageHelper.getGreens()).execute(),
					blueOperation = new CustomFilter1(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.PERSONAL_FILTER1).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void sobel2CovolutionPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Sobel2 discrete convolution");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new SobelFilter2(rgbImageHelper.getReds()).execute(),
					greenOperation = new SobelFilter2(rgbImageHelper.getGreens()).execute(),
					blueOperation = new SobelFilter2(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.SOBEL_FILTER2).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void averageFilteringPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Average filtering");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new AverageFilter(rgbImageHelper.getReds()).execute(),
					greenOperation = new AverageFilter(rgbImageHelper.getGreens()).execute(),
					blueOperation = new AverageFilter(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
			
			}
		}).start();
	}
	
	private void medianFilteringPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Median filtering");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new MedianFilter(rgbImageHelper.getReds()).execute(),
					greenOperation = new MedianFilter(rgbImageHelper.getGreens()).execute(),
					blueOperation = new MedianFilter(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			}else {
			
			}
		}).start();
	}
	
	private void conservativeSmoothingPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Conservative smoothing");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new ConservativeFilter(rgbImageHelper.getReds()).execute(),
					greenOperation = new ConservativeFilter(rgbImageHelper.getGreens()).execute(),
					blueOperation = new ConservativeFilter(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.CONSERVATIVE_SMOOTHING).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void gaussianFilteringPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Gaussian filtering");
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new GaussianFilter(rgbImageHelper.getReds()).execute(),
					greenOperation = new GaussianFilter(rgbImageHelper.getGreens()).execute(),
					blueOperation = new GaussianFilter(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.GAUSSIAN_FILTERING).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void mediumFilteringPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Middle filtering");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new MiddleFilter(rgbImageHelper.getReds()).execute(),
					greenOperation = new MiddleFilter(rgbImageHelper.getGreens()).execute(),
					blueOperation = new MiddleFilter(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			}else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.MEDIUM_FILTERING).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void thickeningOpeningPerformed() {
		new Thread(() -> {
			StructuringElementDialog thickeningOpeningDialog = new StructuringElementDialog(this, true).setTranslation(translationModel).setLanguage(language).initLanguage();
			thickeningOpeningDialog.addOrigineListener(values -> new Thread(() -> {
				System.out.println("[INFO] Top hat opening");
				SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
				ImageLoader imageLoader = imageViewer.getImageLoader();
				BufferedImage bufferedImage = null;
				bufferedImage = imageLoader.getBufferedImage();
				if(setting.isUseLocalHardware()) {
					int x = (int) values[0];
					int y = (int) values[1];
					int[][] structuringElement = (int[][]) values[2];
					int[][] binaryGrayscale = new ThresholdingBinarization(new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale(), new Otsu(new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale()).execute()).execute();
					int[][] openingGrayscale = new Dilation(structuringElement, x, y, new Erosion(structuringElement, x, y, binaryGrayscale).execute()).execute();
					GrayscaleImageHelper openingGrayscaleHelper = new GrayscaleImageHelper(openingGrayscale);
					int[][] newGrayscale = new int[openingGrayscaleHelper.lineLength()][openingGrayscaleHelper.columnLength()];
					
					for (int line = 0; line < openingGrayscaleHelper.lineLength(); line++) {
						for (int column = 0; column < openingGrayscaleHelper.columnLength(); column++) {
							newGrayscale[line][column] = Math.abs(binaryGrayscale[line][column] - openingGrayscale[line][column]);
						}
					}
					newGrayscale = new InvertGrayscale(newGrayscale).execute();
					
					RgbImageHelper rgbImageHelper = new RgbImageHelper(imageLoader.getOriginalColor());
					RGBA[][] fullColorImage = new RgbImageHelper(newGrayscale).setAlphas(rgbImageHelper.getAlphas()).getImage();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					
					applicationHistory.append(fullColorImage);
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}else {
					try {
						Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
						DataPacket outputPacket = new DataPacket();
						Map<String, Object> outputData = new HashMap<>();
						outputData.put("values", values);
						outputData.put("image-loader", imageLoader);
						objectOutputStream.writeObject(outputPacket.setHeader(Const.TOP_HAT_OPENING).setData(outputData));
						objectOutputStream.flush();
						ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
						DataPacket result = (DataPacket) objectInputStream.readObject();
						RGBA[][] fullColorImage = (RGBA[][]) result.getData();
						applyImageChange(fullColorImage, bufferedImage);
						imageLoader.setOriginalColor(fullColorImage);
						applicationHistory.append(fullColorImage);
						
						objectOutputStream.close();
						objectInputStream.close();
						socket.close();
					} catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					} finally {
						SwingUtilities.invokeLater(() -> {
							imageViewer.setImageLoader(imageLoader);
							imageViewer.repaint();
							processingBar.setVisible(false);
							currentTitle = newProject ? defaultTitle : currentTitle;
							setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
						});
						hasChanges = true;
					}
				}
			}).start());
			thickeningOpeningDialog.setVisible(true);
		}).start();
	}
	
	private void thickeningClosurePerformed() {
		new Thread(() -> {
			StructuringElementDialog thickeningOpeningDialog = new StructuringElementDialog(this, true).setTranslation(translationModel).setLanguage(language).initLanguage();
			thickeningOpeningDialog.addOrigineListener(values -> new Thread(() -> {
				System.out.println("[INFO] Top hat closure");
				SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
				ImageLoader imageLoader = imageViewer.getImageLoader();
				BufferedImage bufferedImage = null;
				bufferedImage = imageLoader.getBufferedImage();
				if(setting.isUseLocalHardware()) {
					int x = (int) values[0];
					int y = (int) values[1];
					int[][] structuringElement = (int[][]) values[2];
					int[][] binaryGrayscale = new ThresholdingBinarization(new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale(), new Otsu(new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale()).execute()).execute();
					int[][] closingGrayscale = new Erosion(structuringElement, x, y, new Dilation(structuringElement, x, y, binaryGrayscale).execute()).execute();
					GrayscaleImageHelper openingGrayscaleHelper = new GrayscaleImageHelper(closingGrayscale);
					int[][] newGrayscale = new int[openingGrayscaleHelper.lineLength()][openingGrayscaleHelper.columnLength()];
					
					for (int line = 0; line < openingGrayscaleHelper.lineLength(); line++) {
						for (int column = 0; column < openingGrayscaleHelper.columnLength(); column++) {
							newGrayscale[line][column] = Math.abs(closingGrayscale[line][column] - binaryGrayscale[line][column]);
						}
					}
					newGrayscale = new InvertGrayscale(newGrayscale).execute();
					
					RgbImageHelper rgbImageHelper = new RgbImageHelper(imageLoader.getOriginalColor());
					RGBA[][] fullColorImage = new RgbImageHelper(newGrayscale).setAlphas(rgbImageHelper.getAlphas()).getImage();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					
					applicationHistory.append(fullColorImage);
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				} else {
					try {
						Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
						DataPacket outputPacket = new DataPacket();
						Map<String, Object> outputData = new HashMap<>();
						outputData.put("values", values);
						outputData.put("image-loader", imageLoader);
						objectOutputStream.writeObject(outputPacket.setHeader(Const.TOP_HAT_CLOSURE).setData(outputData));
						objectOutputStream.flush();
						ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
						DataPacket result = (DataPacket) objectInputStream.readObject();
						RGBA[][] fullColorImage = (RGBA[][]) result.getData();
						applyImageChange(fullColorImage, bufferedImage);
						imageLoader.setOriginalColor(fullColorImage);
						applicationHistory.append(fullColorImage);
						
						objectOutputStream.close();
						objectInputStream.close();
						socket.close();
					} catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					} finally {
						SwingUtilities.invokeLater(() -> {
							imageViewer.setImageLoader(imageLoader);
							imageViewer.repaint();
							processingBar.setVisible(false);
							currentTitle = newProject ? defaultTitle : currentTitle;
							setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
						});
						hasChanges = true;
					}
				}
			}).start());
			thickeningOpeningDialog.setVisible(true);
		}).start();
	}
	
	private void thickeningPerformed() {
		new Thread(() -> {
			AllOrNothingDialog allOrNothingDialog = new AllOrNothingDialog(this, true).setTranslation(translationModel).setLanguage(language).initLanguage();
			allOrNothingDialog.addOrigineListener((structuringElement) -> new Thread(() -> {
				System.out.println("[INFO] Thickening");
				SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
				ImageLoader imageLoader = imageViewer.getImageLoader();
				BufferedImage bufferedImage = imageLoader.getBufferedImage();
				if(setting.isUseLocalHardware()) {
					int[][] binaryGrayscale = new ThresholdingBinarization(new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale(), new Otsu(new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale()).execute()).execute();
					int[][] allOrNothingGrayscale = new AllOrNothing(structuringElement, new RgbImageHelper(imageLoader.getOriginalColor()).getGrayscale()).execute();
					GrayscaleImageHelper grayscaleHelper = new GrayscaleImageHelper(binaryGrayscale);
					int[][] newGrayscale = new int[grayscaleHelper.lineLength()][grayscaleHelper.columnLength()];
					
					for (int line = 0; line < grayscaleHelper.lineLength(); line++) {
						for (int column = 0; column < grayscaleHelper.columnLength(); column++) {
							newGrayscale[line][column] = Math.abs(binaryGrayscale[line][column] - allOrNothingGrayscale[line][column]);
						}
					}
					newGrayscale = new InvertGrayscale(newGrayscale).execute();
					
					RgbImageHelper rgbImageHelper = new RgbImageHelper(imageLoader.getOriginalColor());
					RGBA[][] fullColorImage = new RgbImageHelper(newGrayscale).setAlphas(rgbImageHelper.getAlphas()).getImage();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					
					applicationHistory.append(fullColorImage);
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				} else {
				
				}
			}).start());
			allOrNothingDialog.setVisible(true);
		}).start();
	}
	
	private void laplacianCovolutionPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Laplacian discrete convolution");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new LaplacianFilter(rgbImageHelper.getReds()).execute(),
					greenOperation = new LaplacianFilter(rgbImageHelper.getGreens()).execute(),
					blueOperation = new LaplacianFilter(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.LAPLACIAN_FILTER).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void rehausseurCovolutionPerformed() {
		new Thread(() -> {
			System.out.println("[INFO] Rehausseur discrete convolution");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = null;
			bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()){
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new RehausseurFilter(rgbImageHelper.getReds()).execute(),
					greenOperation = new RehausseurFilter(rgbImageHelper.getGreens()).execute(),
					blueOperation = new RehausseurFilter(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.REHAUSSEUR_FILTER).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void customCovolution2Performed() {
		new Thread(() -> {
			System.out.println("[INFO] Discrete convolution 2");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = null;
			bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new CustomFilter2(rgbImageHelper.getReds()).execute(),
					greenOperation = new CustomFilter2(rgbImageHelper.getGreens()).execute(),
					blueOperation = new CustomFilter2(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.PERSONAL_FILTER2).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
	
	private void customCovolution3Performed() {
		new Thread(() -> {
			System.out.println("[INFO] Discrete convolution 3");
			SwingUtilities.invokeLater(() -> processingBar.setVisible(true));
			final ImageLoader imageLoader = imageViewer.getImageLoader();
			BufferedImage bufferedImage = null;
			bufferedImage = imageLoader.getBufferedImage();
			RGBA[][] image = imageLoader.getOriginalColor();
			if(setting.isUseLocalHardware()) {
				RgbImageHelper rgbImageHelper = new RgbImageHelper(image);
				
				int[][] redOperation = new CustomFilter3(rgbImageHelper.getReds()).execute(),
					greenOperation = new CustomFilter3(rgbImageHelper.getGreens()).execute(),
					blueOperation = new CustomFilter3(rgbImageHelper.getBlues()).execute();
				
				RGBA[][] fullColorImage = new RgbImageHelper(rgbImageHelper.getAlphas(), redOperation, greenOperation, blueOperation).getImage();
				applyImageChange(fullColorImage, bufferedImage);
				imageLoader.setOriginalColor(fullColorImage);
				applicationHistory.append(fullColorImage);
				SwingUtilities.invokeLater(() -> {
					imageViewer.setImageLoader(imageLoader);
					imageViewer.repaint();
					processingBar.setVisible(false);
					currentTitle = newProject ? defaultTitle : currentTitle;
					setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
				});
				hasChanges = true;
			} else {
				try {
					Socket socket = new Socket(setting.getRemoteIpAddress(), setting.getRemotePortAddress());
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
					DataPacket outputPacket = new DataPacket();
					objectOutputStream.writeObject(outputPacket.setHeader(Const.PERSONAL_FILTER3).setData(image));
					objectOutputStream.flush();
					ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
					DataPacket result = (DataPacket) objectInputStream.readObject();
					RGBA[][] fullColorImage = (RGBA[][]) result.getData();
					applyImageChange(fullColorImage, bufferedImage);
					imageLoader.setOriginalColor(fullColorImage);
					applicationHistory.append(fullColorImage);
					
					objectOutputStream.close();
					objectInputStream.close();
					socket.close();
				} catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					SwingUtilities.invokeLater(() -> {
						imageViewer.setImageLoader(imageLoader);
						imageViewer.repaint();
						processingBar.setVisible(false);
						currentTitle = newProject ? defaultTitle : currentTitle;
						setTitle("[(" + translationModel.get(language, "not_registered") + ")] - " + currentTitle);
					});
					hasChanges = true;
				}
			}
		}).start();
	}
}
