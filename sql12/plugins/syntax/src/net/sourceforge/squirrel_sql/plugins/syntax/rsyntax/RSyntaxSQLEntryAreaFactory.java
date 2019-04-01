package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.plugins.syntax.IConstants;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPlugin;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;

public class RSyntaxSQLEntryAreaFactory
{
	private SyntaxPlugin _plugin;
   private HashMap<IIdentifier, ArrayList<RSyntaxSQLEntryPanel>> _rSyntaxSQLEntryPanelsBySessionID = new HashMap<IIdentifier, ArrayList<RSyntaxSQLEntryPanel>>();

   public RSyntaxSQLEntryAreaFactory(SyntaxPlugin plugin)
	{
		_plugin = plugin;
	}

	public ISQLEntryPanel createSQLEntryPanel(ISession session, HashMap<String, Object> props)
	{
      SyntaxPreferences prefs = (SyntaxPreferences) session.getPluginObject(_plugin, IConstants.ISessionKeys.PREFS);

      ArrayList<RSyntaxSQLEntryPanel> sqlEntryPanels = _rSyntaxSQLEntryPanelsBySessionID.get(session.getIdentifier());

      if(null == sqlEntryPanels)
      {
         sqlEntryPanels = new ArrayList<>();
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
