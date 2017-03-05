package net.sourceforge.squirrel_sql.plugins.syntax;

import net.sourceforge.squirrel_sql.client.IApplication;

/**
 * JUst needed to define an alternative key stroke
 */
public class CommentActionAltAccelerator extends CommentAction
{
   public CommentActionAltAccelerator(IApplication app, SyntaxPluginResources rsrc)
         throws IllegalArgumentException
   {
      super(app, rsrc);
   }

}
