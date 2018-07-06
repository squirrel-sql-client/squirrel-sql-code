package net.sourceforge.squirrel_sql.plugins.multisource;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.resources.Resources;

/**
 * Menu item that allows the user to add a source to the integrated, global view.
 */
public class MultiAddSourceAction extends SquirrelAction {
	private static final long serialVersionUID = 1L;

	private ISession _session;

	public MultiAddSourceAction(IApplication app, Resources rsrc, ISession session) {
		super(app, rsrc);
		_session = session;
	}

	/**
	 * The add source action retrieves a list of all existing aliases that are
	 * not virtual (have unity in the URL) then allows the user to select one of
	 * them in a dialog. If a source is selected, it is added to the virtual
	 * view and the object tree is updated.
	 */
	public void actionPerformed(ActionEvent evt) {
		try {
			// List all the aliases in the system (then have user pick one).
			Iterator<ISQLAlias> iterator = _session.getApplication().getDataCache().aliases();
			ArrayList<ISQLAlias> aliasList = new ArrayList<ISQLAlias>();
			while (iterator.hasNext()) {
				ISQLAlias alias = iterator.next();
				if (alias.getUrl().toLowerCase().indexOf("jdbc:unity") < 0) {
					aliasList.add(alias);
				}
			}

			if (aliasList.size() > 0) { 
				// Must have at least one alias source defined
				// Put up window to select an alias
				MultiAliasChooser dialog = new MultiAliasChooser(this._app, _session, aliasList);
				dialog.showDialog();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
