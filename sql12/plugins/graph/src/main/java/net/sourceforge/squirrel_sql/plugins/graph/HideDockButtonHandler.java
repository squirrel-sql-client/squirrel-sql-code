package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HideDockButtonHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(HideDockButtonHandler.class);

   private JToggleButton _btnToClickOnHide;
   private JButton _bntHide;


   public HideDockButtonHandler(JToggleButton btnToClickOnHide, GraphPluginResources rsrc)
   {
      _btnToClickOnHide = btnToClickOnHide;

      _bntHide = new JButton(rsrc.getIcon(GraphPluginResources.IKeys.HIDE_DOCK));
      _bntHide.setPressedIcon(rsrc.getIcon(GraphPluginResources.IKeys.HIDE_DOCK_SEL));
      _bntHide.setToolTipText(s_stringMgr.getString("graph.GraphQuerySQLPanel.hide"));
      _bntHide.setBorder(BorderFactory.createEmptyBorder());

      _bntHide.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _btnToClickOnHide.doClick(0);
         }
      });
   }

   public JButton getHideButton()
   {
      return _bntHide;
   }
}
