package net.sourceforge.squirrel_sql.client.session.mainpanel;

public interface SQLResultExecuterPanelFacade
{
   void closeResultTab(ResultTab resultTab);

   void returnToTabbedPane(ResultTab resultTab);

   void createSQLResultFrame(ResultTab resultTab);

   void rerunSQL(String sql, IResultTab resultTab);

   void removeErrorPanel(ErrorPanel errorPanel);
}
