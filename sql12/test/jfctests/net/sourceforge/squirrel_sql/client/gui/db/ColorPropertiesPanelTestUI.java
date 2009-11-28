package net.sourceforge.squirrel_sql.client.gui.db;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.ColorPropertiesPanel;

public class ColorPropertiesPanelTestUI {

	
	
    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationArguments.initialize(new String[] {});
        
        
        final JFrame frame = new JFrame("Test ColorPropertiesPanel");

        SQLAliasColorProperties props = new SQLAliasColorProperties();
        
        ColorPropertiesPanel panel = new ColorPropertiesPanel(props);
        
        frame.getContentPane().add(panel);
        frame.setSize(500,300);
        frame.setVisible(true);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
