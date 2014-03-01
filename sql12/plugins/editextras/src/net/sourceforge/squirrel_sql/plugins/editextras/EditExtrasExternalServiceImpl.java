package net.sourceforge.squirrel_sql.plugins.editextras;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

import java.awt.*;

public class EditExtrasExternalServiceImpl implements EditExtrasExternalService
{
   public void quoteSQL(ISQLEntryPanel entryPanel)
   {
      InQuotesCommand.quoteSQL(entryPanel, false);
   }

   public void quoteSQLSb(ISQLEntryPanel entryPanel)
   {
      InQuotesCommand.quoteSQL(entryPanel, true);
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
