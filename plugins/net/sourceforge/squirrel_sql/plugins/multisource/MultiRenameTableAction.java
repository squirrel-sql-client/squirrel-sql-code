package net.sourceforge.squirrel_sql.plugins.multisource;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;

/**
 * Menu item that allows user to rename a table in the integrated view. 
 */
public class MultiRenameTableAction extends SquirrelAction {
	private static final long serialVersionUID = 1L;

	private ISession _session;

	public MultiRenameTableAction(IApplication app, Resources rsrc, ISession session) {
		super(app, rsrc);
		_session = session;
	}

	public void actionPerformed(ActionEvent evt) {
		try {
			
			SessionInternalFrame sessMainFrm = _session.getSessionInternalFrame();
			IObjectTreeAPI otree =  sessMainFrm.getObjectTreeAPI();
			IDatabaseObjectInfo[] dbObjs = otree.getSelectedDatabaseObjects();
			
			// Only allow one rename regardless of how many are selected
			if (dbObjs.length > 0)
	        {
				// DatabaseObjectInfo di = (DatabaseObjectInfo) dbObjs[0];
				// String sourceName = di.getSimpleName();				

				// TODO: Not implemented yet.	            	         	            
	        }						
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
}
