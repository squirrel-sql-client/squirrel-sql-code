package net.sourceforge.squirrel_sql.fw.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class TableExportCsvDlgTestUI {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        ApplicationArguments.initialize(new String[] {});
        SquirrelResources _resources = 
            new SquirrelResources("net.sourceforge.squirrel_sql.client.resources.squirrel");
        SquirrelPreferences _prefs = SquirrelPreferences.load();

		  TableExportCsvDlg dialog = new TableExportCsvDlg();
		  dialog.setSize(500,500);
		  GUIUtils.centerWithinScreen(dialog);
		  
        dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
      	  
        });
        
        dialog.setVisible(true);

    }

}
