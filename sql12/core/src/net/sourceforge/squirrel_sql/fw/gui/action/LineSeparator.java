package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public enum LineSeparator
{
   DEFAULT,
   CRLF,
   LF;


   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(LineSeparator.class);

   public String toString()
   {
      if (this == DEFAULT)
      {
         return s_stringMgr.getString("TableExportCsvDlg.defaultLabel");
      }
      if (this == CRLF)
      {
         return "CRLF (\\r\\n)";
      }
      return "LF (\\n)";
   }

   public String getSeparator()
   {
      String result = null;
      switch (this)
      {
         case DEFAULT:
            result = System.getProperty("line.separator");
            break;
         case LF:
            result = "\n";
            break;
         case CRLF:
            result = "\r\n";
            break;
      }
      return result;
   }
}
