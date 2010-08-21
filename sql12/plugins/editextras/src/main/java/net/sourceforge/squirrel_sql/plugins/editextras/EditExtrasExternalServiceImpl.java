package net.sourceforge.squirrel_sql.plugins.editextras;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;

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
}
