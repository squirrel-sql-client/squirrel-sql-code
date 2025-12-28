package net.sourceforge.squirrel_sql.client.session.mainpanel;

public interface SQLResultExecuterPanelFacade
{
   void closeResultTab(IResultTab resultTab);

   void returnToTabbedPane(ResultTab resultTab);

   void createSQLResultFrame(IResultTab resultTab);

   void rerunSQL(String sql, IResultTab resultTabToReplace);

   void removeErrorPanel(ErrorPanel errorPanel);
}
