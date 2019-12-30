package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist;

import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.GitHandler;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.JComponent;
import java.io.File;
import java.util.List;

public class RevisionListController
{

   private final RevisionListDialog _dlg;

   public RevisionListController(File file, JComponent parentComp)
   {
      _dlg = new RevisionListDialog(parentComp, file.getName());

      _dlg.lstRevisions.setCellRenderer(new RevisionListCellRenderer());

      List<RevisionWrapper> revisions = GitHandler.getRevisions(file);

      _dlg.lstRevisions.setListData(revisions.toArray(new RevisionWrapper[0]));

      if(0 < revisions.size())
      {
         _dlg.lstRevisions.setSelectedIndex(0);
         _dlg.lstRevisions.ensureIndexIsVisible(0);
      }


      GUIUtils.initLocation(_dlg, 500, 500);
      GUIUtils.enableCloseByEscape(_dlg);

      _dlg.setVisible(true);

   }
}
