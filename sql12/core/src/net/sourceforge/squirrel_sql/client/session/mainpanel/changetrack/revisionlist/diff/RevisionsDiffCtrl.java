package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.diff;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.nio.file.Path;

public class RevisionsDiffCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DiffToLocalCtrl.class);

   private DiffPanel _panel = new DiffPanel();
   private JMeldPanelHandler _meldPanelHandler;


   public RevisionsDiffCtrl()
   {
      _meldPanelHandler = new JMeldPanelHandler(false, null);
   }

   public DiffPanel getPanel()
   {
      return _panel;
   }

   public void setSelectedRevisions(String leftGitRevisionContent, String leftRevisionDateString,
                                    String rightGitRevisionContent, String rightRevisionDateString)
   {
      _panel.pnlDiffContainer.removeAll();

      _panel.lblLeftTitle.setText(s_stringMgr.getString("RevisionsDiffCtrl.revision.date", leftRevisionDateString));
      _panel.lblRightTitle.setText(s_stringMgr.getString("RevisionsDiffCtrl.revision.date", rightRevisionDateString));

      Path gitRevisionContentTempFileLeft = DiffFileUtil.createGitRevisionTempFile(leftGitRevisionContent);
      Path gitRevisionContentTempFileRight = DiffFileUtil.createGitRevisionTempFile(rightGitRevisionContent);;

      _meldPanelHandler.showDiff(gitRevisionContentTempFileLeft, gitRevisionContentTempFileRight);
      _panel.pnlDiffContainer.add(_meldPanelHandler.getMeldPanel());
   }

   public void close()
   {
      _meldPanelHandler.cleanMeldPanel();
   }
}
