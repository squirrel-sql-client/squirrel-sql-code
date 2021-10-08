package net.sourceforge.squirrel_sql.client.session.mainpanel.custompanel;

import java.awt.LayoutManager;
import javax.swing.JPanel;

public class CustomResultPanel extends JPanel
{
   private CustomResultPanelDisposeListener _disposeListener;

   public CustomResultPanel(LayoutManager layout)
   {
      super(layout);
   }

   public void dispose()
   {
      if(null != _disposeListener)
      {
         _disposeListener.onDispose();
      }
   }

   public void setDisposeListener(CustomResultPanelDisposeListener disposeListener)
   {
      _disposeListener = disposeListener;
   }
}
