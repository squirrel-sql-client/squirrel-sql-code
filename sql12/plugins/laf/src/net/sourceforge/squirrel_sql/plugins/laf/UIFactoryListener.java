package net.sourceforge.squirrel_sql.plugins.laf;

import com.jgoodies.looks.Options;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactoryAdapter;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactoryComponentCreatedEvent;

import javax.swing.JTabbedPane;

class UIFactoryListener extends UIFactoryAdapter
{
   /**
    * A tabbed panel object has been created.
    *
    * @param evt event object.
    */
   public void tabbedPaneCreated(UIFactoryComponentCreatedEvent evt)
   {
      final JTabbedPane pnl = (JTabbedPane) evt.getComponent();
      pnl.putClientProperty(Options.NO_CONTENT_BORDER_KEY, Boolean.TRUE);
   }
}
