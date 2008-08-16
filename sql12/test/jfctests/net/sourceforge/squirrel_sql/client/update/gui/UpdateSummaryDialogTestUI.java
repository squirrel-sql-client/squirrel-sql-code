package net.sourceforge.squirrel_sql.client.update.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;

public class UpdateSummaryDialogTestUI {

   /**
    * @param args
    */
   public static void main(String[] args) {
      ApplicationArguments.initialize(new String[] {});

      final JFrame frame = new JFrame("Test UpdateManagerDialog");
      JButton showDialogButton = new JButton("Show UpdateManager Dialog");
      JLabel installedVersionLabel = new JLabel("InstalledVersion: ");
      JLabel availableVersionLabel = new JLabel("AvailableVersion: ");
      final JTextField installedVersionTF = new JTextField("installedVersion", 30);
      final JTextField availableVersionTF = new JTextField("availableVersion", 30);
      showDialogButton.addActionListener(new ActionListener() {
         UpdateSummaryDialog dialog = null;

         public void actionPerformed(ActionEvent e) {
            dialog = new UpdateSummaryDialog(frame, getArtifacts(),null);
            dialog.setInstalledVersion(installedVersionTF.getText());
            dialog.setAvailableVersion(availableVersionTF.getText());
            dialog.setVisible(true);
         }
      });
      JPanel panel = new JPanel();
      
      panel.add(installedVersionLabel);
      panel.add(installedVersionTF);
      panel.add(availableVersionLabel);
      panel.add(availableVersionTF);
      panel.add(showDialogButton);
      
      frame.getContentPane().add(panel);
      frame.setSize(400, 200);
      frame.setVisible(true);

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

   }

   private static List<ArtifactStatus> getArtifacts() {
      ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
      ArtifactStatus status = new ArtifactStatus();
      status.setName("fw.jar");
      status.setType("core");
      status.setInstalled(true);
      result.add(status);

      status = new ArtifactStatus();
      status.setName("squirrel-sql.jar");
      status.setType("core");
      status.setInstalled(true);
      result.add(status);
      
      
      status = new ArtifactStatus();
      status.setName("dbcopy.jar");
      status.setType("plugin");
      status.setInstalled(true);
      result.add(status);

      status = new ArtifactStatus();
      status.setName("h2.jar");
      status.setType("plugin");
      status.setInstalled(false);
      result.add(status);      
      
      status = new ArtifactStatus();
      status.setName("squirrel-sql_zh_CN.jar");
      status.setType("i18n");
      status.setInstalled(true);
      result.add(status);

      status = new ArtifactStatus();
      status.setName("squirrel-sql_bg_BG.jar");
      status.setType("i18n");
      status.setInstalled(false);
      result.add(status);
      
      
      return result;
   }
}
