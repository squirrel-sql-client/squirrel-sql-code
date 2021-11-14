package net.sourceforge.squirrel_sql.plugins.syntax.externalservice;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPlugin;
import net.sourceforge.squirrel_sql.plugins.syntax.theme.SyntaxThemeFactory;

import java.util.Hashtable;

public class SyntaxExternalServiceImpl implements SyntaxExternalService
{
   private SyntaxPlugin _syntaxPlugin;

   public SyntaxExternalServiceImpl(SyntaxPlugin syntaxPlugin)
   {
      _syntaxPlugin = syntaxPlugin;
   }

   @Override
   public Hashtable<String, String> getAutoCorrects()
   {
      return _syntaxPlugin.getAutoCorrectProviderImpl().getAutoCorrects();
   }

   @Override
   public void applyDarkTheme()
   {
      _syntaxPlugin.getSyntaxPreferences().initSyntaxTheme(SyntaxThemeFactory.createDarkTheme());
   }

   @Override
   public void applyDefaultTheme()
   {
      _syntaxPlugin.getSyntaxPreferences().initSyntaxTheme(SyntaxThemeFactory.createDefaultLightTheme());
   }
}
