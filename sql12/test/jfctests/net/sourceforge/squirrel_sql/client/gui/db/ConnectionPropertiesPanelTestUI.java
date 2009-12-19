package net.sourceforge.squirrel_sql.client.gui.db;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.ConnectionPropertiesPanel;

public class ConnectionPropertiesPanelTestUI {

	
	
    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationArguments.initialize(new String[] {});
        
        
        final JFrame frame = new JFrame("Test ConnectionPropertiesPanel");

        SQLAliasConnectionProperties props = new SQLAliasConnectionProperties();
        
        ConnectionPropertiesPanel panel = new ConnectionPropertiesPanel(props);
        
        frame.getContentPane().add(panel);
        frame.setSize(500,300);
        frame.setVisible(true);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
