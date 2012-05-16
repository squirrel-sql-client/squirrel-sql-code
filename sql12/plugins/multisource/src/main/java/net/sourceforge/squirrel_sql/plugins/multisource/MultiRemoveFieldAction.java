package net.sourceforge.squirrel_sql.plugins.multisource;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.Resources;

/**
 * Menu item that allows user to add source to integrated, virtual view. 
 */
public class MultiRemoveFieldAction extends SquirrelAction {
	private static final long serialVersionUID = 1L;

	private ISession _session;

	public MultiRemoveFieldAction(IApplication app, Resources rsrc, ISession session) {
		super(app, rsrc);
		_session = session;
	}

	public void actionPerformed(ActionEvent evt) {
		try {
			// TODO: Not yet implemented
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
}
