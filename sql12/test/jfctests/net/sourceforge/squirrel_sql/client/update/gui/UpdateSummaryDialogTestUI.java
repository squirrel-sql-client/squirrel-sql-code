package net.sourceforge.squirrel_sql.client.update.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.update.UpdateController;
import net.sourceforge.squirrel_sql.client.update.UpdateControllerImpl;

public class UpdateSummaryDialogTestUI {

   /**
    * @param args
    */
   public static void main(String[] args) {
      ApplicationArguments.initialize(new String[] {});

      final JFrame frame = new JFrame("Test UpdateManagerDialog");
      JButton showDialogButton = new JButton("Show UpdateManager Dialog");
      showDialogButton.addActionListener(new ActionListener() {
         UpdateSummaryDialog dialog = null;

         public void actionPerformed(ActionEvent e) {
            if (dialog == null) {
               dialog = new UpdateSummaryDialog(frame, getArtifacts(),null);
               dialog.setInstalledVersion("Installed version");
               dialog.setAvailableVersion("Available Version");
            }
            dialog.setVisible(true);
         }
      });
      JPanel panel = new JPanel();
      panel.add(showDialogButton);
      frame.getContentPane().add(panel);
      frame.setSize(400, 200);
      frame.setVisible(true);

      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

   }

   private static List<ArtifactStatus> getArtifacts() {
      ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
      ArtifactStatus status = new ArtifactStatus("fw.jar", "app", true);
      result.add(status);
      return result;
   }
}
