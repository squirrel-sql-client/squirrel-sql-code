package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.util.concurrent.Callable;

public enum CompletionCaseSpelling
{

   UNCHANGED(() -> StringManagerFactory.getStringManager(CompletionCaseSpelling.class).getString("CompletionCaseSpelling.unchanged")),
   UPPER_CASE(() -> StringManagerFactory.getStringManager(CompletionCaseSpelling.class).getString("CompletionCaseSpelling.uppercase")),
   LOWER_CASE(() -> StringManagerFactory.getStringManager(CompletionCaseSpelling.class).getString("CompletionCaseSpelling.lowercase"));


   private Callable<String> _i18nStringProvider;

   CompletionCaseSpelling(Callable<String> i18nStringProvider)
   {
      _i18nStringProvider = i18nStringProvider;
   }

   public String adjustCaseSpelling(String completionString)
   {
      if(null == completionString)
      {
         return null;
      }

      switch (this)
      {
         case UPPER_CASE:
            return completionString.toUpperCase();
         case LOWER_CASE:
            return completionString.toLowerCase();
         default:
            return completionString;
      }
   }

   @Override
   public String toString()
   {
      try
      {
         return _i18nStringProvider.call();
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
