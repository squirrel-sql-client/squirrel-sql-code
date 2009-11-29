package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.syntax.IConstants;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;

import java.util.HashMap;

public class RSyntaxSQLEntryAreaFactory
{
	private SyntaxPugin _plugin;

	public RSyntaxSQLEntryAreaFactory(SyntaxPugin plugin)
	{
		_plugin = plugin;
	}

	public ISQLEntryPanel createSQLEntryPanel(ISession session, HashMap<String, Object> props)
	{
      SyntaxPreferences prefs = (SyntaxPreferences) session.getPluginObject(_plugin, IConstants.ISessionKeys.PREFS);
		return new RSyntaxSQLEntryPanel(session, prefs, props);
	}
}
