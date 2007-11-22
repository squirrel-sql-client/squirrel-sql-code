package net.sourceforge.squirrel_sql.client.update.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;

public class UpdateManagerDialogTestUI {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationArguments.initialize(new String[] {});
        
        
        final JFrame frame = new JFrame("Test UpdateManagerDialog");
        JButton showDialogButton = new JButton("Show UpdateManager Dialog");
        showDialogButton.addActionListener(new ActionListener() {
            UpdateManagerDialog dialog = null;
            
            public void actionPerformed(ActionEvent e) {
                if (dialog == null) {
                    dialog = new UpdateManagerDialog(frame);
                }
                dialog.setVisible(true);
            }
        });
        JPanel panel = new JPanel();
        panel.add(showDialogButton);
        frame.getContentPane().add(panel);
        frame.setSize(400,200);
        frame.setVisible(true);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
