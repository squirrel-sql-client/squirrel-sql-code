package net.sourceforge.squirrel_sql.plugins.multisource;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 * Menu item that allows user to remove a source from the virtual view. 
 */
public class MultiRemoveSourceAction extends SquirrelAction {
	private static final long serialVersionUID = 1L;

	private ISession _session;

	public MultiRemoveSourceAction(IApplication app, Resources rsrc, ISession session) {
		super(app, rsrc);
		_session = session;
	}

	/**
	 * Removes a source from the virtual view.
	 */
	public void actionPerformed(ActionEvent evt) {
		try {
			SessionInternalFrame sessMainFrm = _session.getSessionInternalFrame();
			IObjectTreeAPI otree =  sessMainFrm.getObjectTreeAPI();
			IDatabaseObjectInfo[] dbObjs = otree.getSelectedDatabaseObjects();

			// Only allow delete of one source regardless how many are selected
			if (dbObjs.length > 0)
	        {
				DatabaseObjectInfo di = (DatabaseObjectInfo) dbObjs[0];
				String sourceName = di.getSimpleName();				

	            Connection con = _session.getSQLConnection().getConnection();		// Retrieve connection
	            Object gs = MultiSourcePlugin.getSchema(con);	            
	            removeSource(sourceName, gs);
	            otree.removeNodes(otree.getSelectedNodes());
	            MultiSourcePlugin.updateSession(_session);	            
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
	public void removeSource(String sourceName, Object gs)
	{
		Class<? extends Object> cls = gs.getClass();
              
		try {
			Method meth = cls.getMethod("removeDatabase", (Class[]) new Class[]{java.lang.String.class});
			meth.invoke(gs, new Object[]{sourceName});			
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);			
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);			
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);			
		} catch (SecurityException e) {
			throw new RuntimeException(e);			
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);			
		}
	}
}
