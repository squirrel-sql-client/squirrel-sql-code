package net.sourceforge.squirrel_sql.plugins.multisource;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.resources.Resources;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;

/**
 * Menu item that allows user to export the source information into XML files.
 */
public class MultiExportAction extends SquirrelAction {

	private ISession _session;

	public MultiExportAction(IApplication app, Resources rsrc, ISession session) {
		super(app, rsrc);
		_session = session;
	}

	public void actionPerformed(ActionEvent evt) {
		try {
			// Show a file chooser to select a file name
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Configuration Files", "xml");
		    chooser.setFileFilter(filter);
		    chooser.setDialogTitle("Select location to save virtualization configuration file");
		    int returnVal = chooser.showSaveDialog(_session.getApplication().getMainFrame());
		    if (returnVal == JFileChooser.APPROVE_OPTION) {		       		      
		       String sourcesFileName = chooser.getSelectedFile().getPath();		       
		       MultiSourcePlugin.export(sourcesFileName, _session);		
		    }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
}
