package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import java.awt.*;

public class EditExtrasExternalServiceImpl
{
   private IApplication _app;
   private boolean _copyQuotedSqlsToClip;

   public EditExtrasExternalServiceImpl()
   {
      this(false);
   }

   public EditExtrasExternalServiceImpl(boolean copyQuotedSqlsToClip)
   {
      _copyQuotedSqlsToClip = copyQuotedSqlsToClip;
   }

   public void quoteSQL(ISQLEntryPanel entryPanel)
   {
      InQuotesCommand.quoteSQL(entryPanel, false, _copyQuotedSqlsToClip);
   }

   public void quoteSQLSb(ISQLEntryPanel entryPanel)
   {
      InQuotesCommand.quoteSQL(entryPanel, true, _copyQuotedSqlsToClip);
   }


   public void unquoteSQL(ISQLEntryPanel entryPanel)
   {
      RemoveQuotesCommand.unquoteSQL(entryPanel);
   }


   public String getDateEscape(Window parentForDialog)
   {
      final String[] ret = new String[1];

      EscapeDateListener escapeDateListener = new EscapeDateListener()
      {
         @Override
         public void setDateString(String escapedString)
         {
            ret[0] = escapedString;
         }
      };

      new EscapeDateController(parentForDialog, escapeDateListener, true);

      return ret[0];
   }
}
