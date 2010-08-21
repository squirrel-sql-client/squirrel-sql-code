package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.syntax.IConstants;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.NetbeansSQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import java.util.HashMap;
import java.util.ArrayList;

public class RSyntaxSQLEntryAreaFactory
{
	private SyntaxPugin _plugin;
   private HashMap<IIdentifier, ArrayList<RSyntaxSQLEntryPanel>> _rSyntaxSQLEntryPanelsBySessionID = new HashMap<IIdentifier, ArrayList<RSyntaxSQLEntryPanel>>();

   public RSyntaxSQLEntryAreaFactory(SyntaxPugin plugin)
	{
		_plugin = plugin;
	}

	public ISQLEntryPanel createSQLEntryPanel(ISession session, HashMap<String, Object> props)
	{
      SyntaxPreferences prefs = (SyntaxPreferences) session.getPluginObject(_plugin, IConstants.ISessionKeys.PREFS);

      ArrayList<RSyntaxSQLEntryPanel> sqlEntryPanels = _rSyntaxSQLEntryPanelsBySessionID.get(session.getIdentifier());

      if(null == sqlEntryPanels)
      {
         sqlEntryPanels = new ArrayList<RSyntaxSQLEntryPanel>();
         _rSyntaxSQLEntryPanelsBySessionID.put(session.getIdentifier(), sqlEntryPanels);
      }

      RSyntaxSQLEntryPanel ret = new RSyntaxSQLEntryPanel(session, prefs, props);
      sqlEntryPanels.add(ret);

      return ret;
	}

   public void sessionEnding(ISession session)
   {
      ArrayList<RSyntaxSQLEntryPanel> rSyntaxSQLEntryPanels = _rSyntaxSQLEntryPanelsBySessionID.remove(session.getIdentifier());

      if(null == rSyntaxSQLEntryPanels)
      {
         return;
      }

      for (RSyntaxSQLEntryPanel rSyntaxSQLEntryPanel : rSyntaxSQLEntryPanels)
      {
         rSyntaxSQLEntryPanel.sessionEnding();
      }

   }
}
