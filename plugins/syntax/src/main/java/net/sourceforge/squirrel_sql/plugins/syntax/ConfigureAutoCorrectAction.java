package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

import javax.swing.*;
import java.awt.event.ActionEvent;


public class ConfigureAutoCorrectAction extends SquirrelAction implements ISessionAction
{
   private SyntaxPugin _syntaxPugin;

   public ConfigureAutoCorrectAction(IApplication app, SyntaxPluginResources resources, SyntaxPugin syntaxPugin)
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
