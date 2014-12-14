package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax.search;

import java.awt.event.ActionListener;

/**
 * Created by gerd on 14.12.14.
 */
public interface ISquirrelReplaceListener
{
   void addReplaceActionListener(ActionListener actionListener);

   void removeReplaceActionListener(ActionListener actionListener);

   void addReplaceAllActionListener(ActionListener actionListener);

   void removeReplaceAllActionListener(ActionListener actionListener);
}
