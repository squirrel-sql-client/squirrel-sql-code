package net.sourceforge.squirrel_sql.client.gui.db;

import java.sql.DriverPropertyInfo;

import javax.swing.JFrame;

import utils.EasyMockHelper;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.DriverPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;

public class DriverPropertiesPanelTestUI {

	
	
    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationArguments.initialize(new String[] {});
        
        
        final JFrame frame = new JFrame("Test DriverPropertiesPanel");
        SQLDriverPropertyCollection propertyCollection = 
      	  new SQLDriverPropertyCollection();
        
        DriverPropertyInfo mockDriverPropertyInfo = new DriverPropertyInfo("propName", "propValue");
        mockDriverPropertyInfo.description = "Test prop description";
        
        SQLDriverProperty prop = new SQLDriverProperty(mockDriverPropertyInfo);
        
        propertyCollection.setDriverProperties(new SQLDriverProperty[] { prop });
        DriverPropertiesPanel panel = new DriverPropertiesPanel(propertyCollection);
        
        frame.getContentPane().add(panel);
        frame.setSize(500,300);
        frame.setVisible(true);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
