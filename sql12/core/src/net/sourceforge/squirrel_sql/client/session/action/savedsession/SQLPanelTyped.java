package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;

public class SQLPanelTyped
{
   private final SQLPanel _sqlPanel;
   private final SqlPanelType _sqlPanelType;

   public SQLPanelTyped(SQLPanel sqlPanel, SqlPanelType sqlPanelType)
   {
      _sqlPanel = sqlPanel;
      _sqlPanelType = sqlPanelType;
   }

   public SQLPanel getSqlPanel()
   {
      return _sqlPanel;
   }

   public SqlPanelType getSqlPanelType()
   {
      return _sqlPanelType;
   }
}
