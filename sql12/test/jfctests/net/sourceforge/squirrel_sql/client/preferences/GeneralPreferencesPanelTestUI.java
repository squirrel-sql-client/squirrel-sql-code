package net.sourceforge.squirrel_sql.client.preferences;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;

public class GeneralPreferencesPanelTestUI {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationArguments.initialize(new String[] {});
        
        
        final JFrame frame = new JFrame("Test UpdatePreferencesPanel");
        GeneralPreferencesPanel panel = new GeneralPreferencesPanel();
        
        frame.getContentPane().add(panel.getPanelComponent());
        frame.setSize(600,400);
        frame.setVisible(true);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
