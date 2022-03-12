package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;

public class SQLPanelSaveInfo
{
   private final SQLPanel _sqlPanel;
   private final SqlPanelType _sqlPanelType;
   private boolean _activeSqlPanel = false;
   private int _caretPosition = 0;

   public SQLPanelSaveInfo(SQLPanel sqlPanel, SqlPanelType sqlPanelType)
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

   public void setActiveSqlPanel(int caretPosition)
   {
      _activeSqlPanel = true;
      _caretPosition = caretPosition;
   }

   public boolean isActiveSqlPanel()
   {
      return _activeSqlPanel;
   }

   public int getCaretPosition()
   {
      return _caretPosition;
   }
}
