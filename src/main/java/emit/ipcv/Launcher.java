/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv;

import com.formdev.flatlaf.FlatIntelliJLaf;
import emit.ipcv.guis.MainFrame;
import emit.ipcv.utils.colors.RGBA;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author rinelfi
 */
public class Launcher {

  /**
   * @param args the command line arguments
   * @throws javax.swing.UnsupportedLookAndFeelException
   */
  public static void main(String args[]) throws UnsupportedLookAndFeelException, IOException {
    System.out.println("[INFO] Checking arguments");
    if (args.length > 0) {
      if (!Files.exists(Paths.get(args[0]))) {
        System.out.println("[ERR] Invalid file format or it doesn't exist");
        JOptionPane.showMessageDialog(null, "Le fichier que vous avez sélectionné n'existe pas ou invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
      }
    }
    
    // Socket socket = new Socket(InetAddress.getLocalHost(), 2046);
    System.out.println("[INFO] Enable openGL engine");
    System.setProperty("sun.java2d.opengl", "true");
  
    System.out.println("[INFO] Starting the app");
    UIManager.setLookAndFeel(new FlatIntelliJLaf());
    java.awt.EventQueue.invokeLater(() -> {
      new MainFrame(args).setVisible(true);
    });

  }
}
