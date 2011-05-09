package net.sourceforge.squirrel_sql.plugins.graph.querybuilder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public class GraphDockHandleAdmin
{
   private HashMap<JToggleButton, GraphDockHandle> _handelsByButton = new HashMap<JToggleButton, GraphDockHandle>();

   private GraphDockHandleAdminListerner _listerner;

   public GraphDockHandleAdmin(GraphDockHandleAdminListerner listerner)
   {
      _listerner = listerner;
   }

   public void add(GraphDockHandle handle, final JToggleButton btn)
   {
      _handelsByButton.put(btn, handle);

      btn.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onBtn(btn);
         }
      });
   }

   private void onBtn(JToggleButton clickedBtn)
   {
      if(clickedBtn.isSelected())
      {
         for (JToggleButton btn : _handelsByButton.keySet())
         {
            if(btn != clickedBtn && btn.isSelected())
            {
               _handelsByButton.get(btn).hide();
               btn.setSelected(false);
            }
         }
         _handelsByButton.get(clickedBtn).show();
         _listerner.newDockOpened();
      }
      else
      {
         _handelsByButton.get(clickedBtn).hide();
      }
   }

   public void deselectAllButtons()
   {
      for (JToggleButton btn : _handelsByButton.keySet())
      {
         btn.setSelected(false);
      }
   }
}
