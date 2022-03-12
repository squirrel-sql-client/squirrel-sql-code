package net.sourceforge.squirrel_sql.client.session.action.savedsession;

public class SessionSqlJsonBean
{
   private String _internalFileName;
   private SqlPanelType _panelType;
   private String _externalFilePath;
   private boolean _activeSqlPanel = false;
   private int _caretPosition = 0;

   public String getInternalFileName()
   {
      return _internalFileName;
   }

   public void setInternalFileName(String internalFileName)
   {
      _internalFileName = internalFileName;
   }

   public void setPanelType(SqlPanelType panelType)
   {
      _panelType = panelType;
   }

   public SqlPanelType getPanelType()
   {
      return _panelType;
   }

   public void setExternalFilePath(String externalFilePath)
   {
      _externalFilePath = externalFilePath;
   }

   public String getExternalFilePath()
   {
      return _externalFilePath;
   }

   public void setActiveSqlPanel(boolean activeSqlPanel)
   {
      _activeSqlPanel = activeSqlPanel;
   }

   public boolean isActiveSqlPanel()
   {
      return _activeSqlPanel;
   }

   public void setCaretPosition(int caretPosition)
   {
      _caretPosition = caretPosition;
   }

   public int getCaretPosition()
   {
      return _caretPosition;
   }
}
