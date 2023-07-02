package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.diff;

import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.JMeldCore;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.nio.file.Path;

public class RevisionsDiffCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DiffToLocalCtrl.class);

   private DiffPanel _diffPanel;
   private JMeldCore _meldCore;

   public RevisionsDiffCtrl()
   {
      _meldCore = new JMeldCore(true);
      _diffPanel = new DiffPanel(_meldCore.getConfigurableMeldPanel());
   }

   public DiffPanel getDiffPanel()
   {
      return _diffPanel;
   }

   public void setSelectedRevisions(String leftGitRevisionContent, String leftRevisionDateString,
                                    String rightGitRevisionContent, String rightRevisionDateString)
   {
      _diffPanel.pnlDiffContainer.removeAll();

      _diffPanel.lblLeftTitle.setText(s_stringMgr.getString("RevisionsDiffCtrl.revision.date", leftRevisionDateString));
      _diffPanel.lblRightTitle.setText(s_stringMgr.getString("RevisionsDiffCtrl.revision.date", rightRevisionDateString));

      Path gitRevisionContentTempFileLeft = DiffFileUtil.createGitRevisionTempFile(leftGitRevisionContent);
      Path gitRevisionContentTempFileRight = DiffFileUtil.createGitRevisionTempFile(rightGitRevisionContent);;

      _meldCore.executeDiff(gitRevisionContentTempFileLeft.toFile().getAbsolutePath(), gitRevisionContentTempFileRight.toFile().getAbsolutePath(), null, null);
      _diffPanel.pnlDiffContainer.add(_meldCore.getConfigurableMeldPanel().getMeldPanel());
   }

   public void cleanUpMelds()
   {
      _meldCore.cleanMeldPanel();
   }
}
