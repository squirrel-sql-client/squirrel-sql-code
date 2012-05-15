package net.sourceforge.squirrel_sql.plugins.multisource;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;

/**
 * Menu item that allows user to remove a table from the integrated view. 
 */
public class MultiRemoveTableAction extends SquirrelAction {
	private static final long serialVersionUID = 1L;

	private ISession _session;

	public MultiRemoveTableAction(IApplication app, Resources rsrc, ISession session) {
		super(app, rsrc);
		_session = session;
	}

	public void actionPerformed(ActionEvent evt) {
		try {
			SessionInternalFrame sessMainFrm = _session.getSessionInternalFrame();
			IObjectTreeAPI otree =  sessMainFrm.getObjectTreeAPI();
			List<ITableInfo> tables =  otree.getSelectedTables();

			// Only allow delete of one table regardless of how many are selected
			if (tables.size() > 0)
	        {
				ITableInfo ti = (ITableInfo) tables.get(0);
				String sourceName = ti.getSchemaName();
				String tableName = ti.getSimpleName();				

	            Connection con = _session.getSQLConnection().getConnection();		// Retrieve connection	            
	            Object gs = MultiSourcePlugin.getSchema(con);						// Invoke Get Global Schema Method using Reflection	            
	            removeTable(sourceName, tableName, gs);
	            otree.removeNodes(otree.getSelectedNodes());
	            MultiSourcePlugin.updateSession(_session);
	            
	        }						
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Removes a table from the global schema using reflection.
	 * @param gs
	 * @return
	 */
	public void removeTable(String sourceName, String tableName, Object gs)
	{
		Class<? extends Object> cls = gs.getClass();
              
		try {
			Method meth = cls.getMethod("removeTable", (Class[]) new Class[]{java.lang.String.class,java.lang.String.class});
			meth.invoke(gs, new Object[]{sourceName, tableName});			
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
