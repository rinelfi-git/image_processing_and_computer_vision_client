/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.guis;

import emit.ipcv.database.dao.TranslationDao;
import emit.ipcv.database.dao.entites.DBLanguage;
import emit.ipcv.engines.interfaces.observerPatterns.ObservableClass;
import emit.ipcv.engines.interfaces.observerPatterns.ObserverClass;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rinelfi
 */
public class RotationDialog extends javax.swing.JDialog implements ObservableClass<Integer> {

  private List<ObserverClass<Integer>> observers;
  private DBLanguage language;
  private TranslationDao translation;

  /**
   * Creates new form HomotetieDialog
   *
   * @param parent
   * @param modal
   */
  public RotationDialog(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
    observers = new ArrayList<>();
  }
  
  public RotationDialog setTranslation(TranslationDao translation) {
    this.translation = translation;
    return this;
  }
  
  public RotationDialog setLanguage(DBLanguage language) {
    this.language = language;
    return this;
  }
  
  public RotationDialog initLanguage() {
    setTitle(translation.get(language, "rotation").toString());
    rotationAngleLab.setText(translation.get(language, "rotation_angle").toString() + ":");
    confirmButton.setText(translation.get(language, "confirm").toString());
    return this;
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    rootPanel = new javax.swing.JPanel();
    rotationAngleLab = new javax.swing.JLabel();
    rotationAngleSpin = new javax.swing.JSpinner();
    confirmButton = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Rotation");
    setResizable(false);

    rootPanel.setBackground(new java.awt.Color(255, 255, 255));
    rootPanel.setLayout(new java.awt.GridBagLayout());

    rotationAngleLab.setText("Angle de rotation :");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
    rootPanel.add(rotationAngleLab, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
    rootPanel.add(rotationAngleSpin, gridBagConstraints);

    confirmButton.setText("Confirmer");
    confirmButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        confirmButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
    rootPanel.add(confirmButton, gridBagConstraints);

    getContentPane().add(rootPanel, java.awt.BorderLayout.CENTER);

    pack();
    setLocationRelativeTo(null);
  }// </editor-fold>//GEN-END:initComponents

  private void confirmButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmButtonActionPerformed
    dispose();
    update(Integer.valueOf(rotationAngleSpin.getValue().toString()));
  }//GEN-LAST:event_confirmButtonActionPerformed

  @Override
  public void addObserver(ObserverClass<Integer> observer) {
    observers.add(observer);
  }

  @Override
  public void clearObserver() {
    observers = new ArrayList<>();
  }

  @Override
  public void update(Integer data) {
    observers.forEach(observer -> observer.callback(data));
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton confirmButton;
  private javax.swing.JPanel rootPanel;
  private javax.swing.JLabel rotationAngleLab;
  private javax.swing.JSpinner rotationAngleSpin;
  // End of variables declaration//GEN-END:variables
}
