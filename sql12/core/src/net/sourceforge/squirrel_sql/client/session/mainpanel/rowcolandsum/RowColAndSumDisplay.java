package net.sourceforge.squirrel_sql.client.session.mainpanel.rowcolandsum;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum RowColAndSumDisplay
{
   ROW_COLS(I18n.s_stringMgr.getString("RowColAndSumDisplay.rows.and.colums")),
   SUM_FUNCTIONS(I18n.s_stringMgr.getString("RowColAndSumDisplay.sum.and.functions")),
   BOTH(I18n.s_stringMgr.getString("RowColAndSumDisplay.both"));

   private String _title;

   RowColAndSumDisplay(String title)
   {
      _title = title;
   }

   @Override
   public String toString()
   {
      return _title;
   }

   interface I18n
   {
      StringManager s_stringMgr = StringManagerFactory.getStringManager(RowColAndSumDisplay.class);
   }
}
