package net.sourceforge.squirrel_sql.plugins.multisource;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.Resources;

/**
 * Menu item that allows user to add source to integrated, global view.
 * @author rlawrenc
 *
 */
public class MultiRenameFieldAction extends SquirrelAction {
	private static final long serialVersionUID = 1L;

	private ISession _session;

	public MultiRenameFieldAction(IApplication app, Resources rsrc, ISession session) {
		super(app, rsrc);
		_session = session;
	}

	public void actionPerformed(ActionEvent evt) {
		try {
			// TODO: Not implemented yet.

			SessionInternalFrame sessMainFrm = _session.getSessionInternalFrame();
			
			sessMainFrm.getSQLPanelAPI().appendSQLScript("RENAME FIELD!");
			sessMainFrm.getSessionPanel().selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
