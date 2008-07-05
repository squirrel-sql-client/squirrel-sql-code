package net.sourceforge.squirrel_sql.client.preferences;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.MockApplication;

public class UpdatePreferencesPanelTestUI {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationArguments.initialize(new String[] {});
        
        
        final JFrame frame = new JFrame("Test UpdatePreferencesPanel");
        UpdatePreferencesTab tab = new UpdatePreferencesTab();
        tab.initialize(new MockApplication());
        
        frame.getContentPane().add(tab.getPanelComponent());
        frame.setSize(600,600);
        frame.setVisible(true);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
