package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartButtonHandler
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(StartButtonHandler.class);


   private JToggleButton _btnShowMenu;
   private Timer _popupReopenWaitTimer;

   public StartButtonHandler(final GraphControllerFacade graphControllerFacade, GraphPluginResources rsrc)
   {
      _popupReopenWaitTimer = new Timer(100, null);
      _popupReopenWaitTimer.setRepeats(false);
      _btnShowMenu = new JToggleButton(rsrc.getIcon(GraphPluginResources.IKeys.SHOW_MENU));
      _btnShowMenu.setToolTipText(s_stringMgr.getString("graph.StartButtonHandler.Kickoff"));

      _btnShowMenu.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onShowmenu(graphControllerFacade);
         }
      });


   }

   public JToggleButton getButton()
   {
      return _btnShowMenu;
   }

   private void onShowmenu(GraphControllerFacade graphControllerFacade)
   {
      if (_btnShowMenu.isSelected() && false == _popupReopenWaitTimer.isRunning())
      {
         Point loc = GUIUtils.getScreenLocationFor(_btnShowMenu);
         GraphControllerPopupListener graphControllerPopupListener = new GraphControllerPopupListener()
         {
            @Override
            public void hiding()
            {
               onPopupHide();
            }
         };

         graphControllerFacade.showPopupAbove(loc, graphControllerPopupListener);
      }
      else
      {
         graphControllerFacade.hidePopup();
         _btnShowMenu.setSelected(false);
      }
   }

   private void onPopupHide()
   {
      _btnShowMenu.setSelected(false);
      _popupReopenWaitTimer.restart();
   }



}
