package net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui;

import org.jmeld.ui.JMeldPanel;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;

/**
 * The JMeldPanel registers a window listener that exits the JVM when it detects that the frame it resides
 * in was closed. This subclass overrides this behavior with a no-op window listener.
 */
class NonExitingJMeldPanel extends JMeldPanel
{

   private MeldExitListener _meldExitListener;

   public NonExitingJMeldPanel(MeldExitListener meldExitListener)
   {
      _meldExitListener = meldExitListener;
   }

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

   @Override
   public void doExit(ActionEvent ae)
   {
      super.doExit(ae);
      _meldExitListener.meldPanelExits();
   }
}
