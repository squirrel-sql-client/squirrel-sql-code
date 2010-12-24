package net.sourceforge.squirrel_sql.plugins.syntax;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;


public class ConfigureAutoCorrectAction extends SquirrelAction implements ISessionAction
{
   private SyntaxPlugin _syntaxPugin;

   public ConfigureAutoCorrectAction(IApplication app, SyntaxPluginResources resources, SyntaxPlugin syntaxPugin)
   {
      super(app, resources);
      _syntaxPugin = syntaxPugin;
   }

   public void actionPerformed(ActionEvent e)
   {
      new AutoCorrectController(_syntaxPugin);

   }

   public void setSession(ISession session)
   {
      //To change body of implemented methods use File | Settings | File Templates.
   }
}
