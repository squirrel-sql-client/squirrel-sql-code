package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.*;

public class CurrentSqlLabelController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CurrentSqlLabelController.class);


   private String _sql;
   private JLabel _lbl = new JLabel();

   public CurrentSqlLabelController()
   {
   }

   public void reInit(int rowCount, boolean areAllPossibleResultsOfSQLRead)
   {
      String escapedSql = Utilities.escapeHtmlChars(_sql);

      if (areAllPossibleResultsOfSQLRead)
      {
         // i18n[ResultTab.rowsMessage=Rows {0}]
         String rowsMsg = s_stringMgr.getString("ResultTab.rowsMessage", Integer.valueOf(rowCount));
         _lbl.setText("<html><pre>&nbsp;" + rowsMsg + ";&nbsp;&nbsp;" + escapedSql + "</pre></html>");
      }
      else
      {
         // i18n[ResultTab.limitMessage=Limited to <font color='red'> {0} </font> rows]
         String limitMsg = s_stringMgr.getString("ResultTab.limitMessage", Integer.valueOf(rowCount));
         _lbl.setText("<html><pre>&nbsp;" + limitMsg + ";&nbsp;&nbsp;" + escapedSql + "</pre></html>");
      }
   }

   public void clear()
   {
      _sql = "";
      _lbl.setText("");
   }

   public JLabel getLabel()
   {
      return _lbl;
   }

   public void setSql(String sql)
   {
      _sql = sql;
   }
}
