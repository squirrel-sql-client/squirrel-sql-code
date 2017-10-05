package org.squirrelsql.session.sql;

import javafx.scene.control.Tab;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.SessionTabContext;

public class SqlTabCtrl
{
   private I18n _i18n = new I18n(getClass());
   private final Tab _sqlTab;
   private final SqlPaneCtrl _sqlPaneCtrl;

   public SqlTabCtrl(SessionTabContext sessionTabContext)
   {
      _sqlTab = new Tab(_i18n.t("session.tab.sql"));
      _sqlTab.setClosable(false);

      _sqlPaneCtrl = new SqlPaneCtrl(sessionTabContext);

      _sqlTab.setContent(_sqlPaneCtrl.getSqlPane());
   }

   public Tab getSqlTab()
   {
      return _sqlTab;
   }

   public void close()
   {
      _sqlPaneCtrl.close();
   }

   public void requestFocus()
   {
      _sqlPaneCtrl.requestFocus();
   }

   public SQLTextAreaServices getSQLTextAreaServices()
   {
      return _sqlPaneCtrl.getSQLTextAreaServices();
   }
}

