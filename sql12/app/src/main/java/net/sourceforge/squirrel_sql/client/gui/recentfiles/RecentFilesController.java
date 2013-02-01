package net.sourceforge.squirrel_sql.client.gui.recentfiles;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RecentFilesController
{

   private RecentFilesDialog _dialog;

   public RecentFilesController(Frame parent, ISQLAlias selectedAlias)
   {
      init(parent, selectedAlias, false);
   }


   public RecentFilesController(ISQLPanelAPI panel)
   {
      init(GUIUtils.getOwningFrame(panel.getSQLEntryPanel().getTextComponent()), panel.getSession().getAlias(), true);
   }


   private void init(Frame parent, ISQLAlias selectedAlias, boolean showAppendOption)
   {
      _dialog = new RecentFilesDialog(parent, showAppendOption);

      _dialog.btnClose.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            _dialog.dispose();
         }
      });



      _dialog.setVisible(true);
   }

}
