package net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import java.util.ArrayList;
import java.util.HashMap;

public class RSyntaxSQLEntryAreaFactory
{
   private HashMap<IIdentifier, ArrayList<RSyntaxSQLEntryPanel>> _rSyntaxSQLEntryPanelsBySessionID = new HashMap<>();

   public RSyntaxSQLEntryAreaFactory()
	{
	}

	public ISQLEntryPanel createSQLEntryPanel(ISession session, HashMap<String, Object> props)
	{
      SyntaxPreferences prefs = Main.getApplication().getSyntaxManager().getSyntaxPreferences();

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
