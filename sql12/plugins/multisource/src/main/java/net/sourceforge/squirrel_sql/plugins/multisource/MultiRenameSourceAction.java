package net.sourceforge.squirrel_sql.plugins.multisource;

import java.awt.event.ActionEvent;
import java.sql.Connection;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;

/**
 * Menu item that allows user to rename a source in the virtual view. 
 */
public class MultiRenameSourceAction extends SquirrelAction {
	private static final long serialVersionUID = 1L;

	private ISession _session;

	public MultiRenameSourceAction(IApplication app, Resources rsrc, ISession session) {
		super(app, rsrc);
		_session = session;
	}

	public void actionPerformed(ActionEvent evt) {
		try {
			SessionInternalFrame sessMainFrm = _session.getSessionInternalFrame();
			IObjectTreeAPI otree =  sessMainFrm.getObjectTreeAPI();
			IDatabaseObjectInfo[] dbObjs = otree.getSelectedDatabaseObjects();

			// Only allow renaming of one source regardless how many are selected
			if (dbObjs.length > 0) {
				DatabaseObjectInfo di = (DatabaseObjectInfo) dbObjs[0];
				String sourceName = di.getSimpleName();				

	            Connection con = _session.getSQLConnection().getConnection();		// Retrieve connection
	            Object gs = MultiSourcePlugin.getSchema(con);						// Invoke Get Global Schema Method using Reflection	            
	            renameSource(sourceName, gs);	            	            
	        }						
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Removes a source from the global schema using reflection.
	 * @param gs
	 * @return
	 */
	public void renameSource(String sourceName, Object gs)
	{
		// TODO: Not implemented yet.		
	}	
}
