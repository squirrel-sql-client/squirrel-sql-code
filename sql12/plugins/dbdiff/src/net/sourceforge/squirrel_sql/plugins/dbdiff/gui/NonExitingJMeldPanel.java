package net.sourceforge.squirrel_sql.plugins.dbdiff.gui;

import org.jmeld.ui.JMeldPanel;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;

/**
 * The JMeldPanel registers a window listener that exits the JVM when it detects that the frame it resides
 * in was closed. This subclass overrides this behavior with a no-op window listener.
 */
class NonExitingJMeldPanel extends JMeldPanel
{

   /**
    * @see JMeldPanel#getWindowListener()
    */
   @Override
   public WindowListener getWindowListener()
   {
      return new WindowAdapter()
      {
      };
   }

}
