package net.sourceforge.squirrel_sql.plugins.multisource;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.resources.Resources;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Menu item that allows the user to add a source to the integrated, global view.
 */
public class MultiAddSourceAction extends SquirrelAction {

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
			Iterator<? extends SQLAlias> iterator = _session.getApplication().getAliasesAndDriversManager().aliases();
			ArrayList<SQLAlias> aliasList = new ArrayList<SQLAlias>();
			while (iterator.hasNext()) {
				SQLAlias alias = iterator.next();
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
