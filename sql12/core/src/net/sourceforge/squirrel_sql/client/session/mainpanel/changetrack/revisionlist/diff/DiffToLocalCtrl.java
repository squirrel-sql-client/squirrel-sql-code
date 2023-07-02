package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.diff;

import net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui.JMeldCore;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.RevisionListControllerChannel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.nio.file.Path;

public class DiffToLocalCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DiffToLocalCtrl.class);
   private final RevisionListControllerChannel _revisionListControllerChannel;
   private final JMeldCore _meldCore;

   private DiffPanel _diffPanel;


   public DiffToLocalCtrl(RevisionListControllerChannel revisionListControllerChannel)
   {
      _revisionListControllerChannel = revisionListControllerChannel;
      _meldCore = new JMeldCore(true);
      _diffPanel = new DiffPanel(_meldCore.getConfigurableMeldPanel());
   }

   public DiffPanel getDiffPanel()
   {
      return _diffPanel;
   }

   public void setSelectedRevision(String gitRevisionContent, String revisionDateString)
   {
      _diffPanel.pnlDiffContainer.removeAll();

      if(null == gitRevisionContent)
      {
         _diffPanel.lblLeftTitle.setText(s_stringMgr.getString("DiffToLocalCtrl.no.revision.selected.short"));

         JTextArea txt = new JTextArea();
         txt.setText(s_stringMgr.getString("DiffToLocalCtrl.no.revision.selected"));
         txt.setEditable(false);
         txt.setBorder(BorderFactory.createEtchedBorder());
         _diffPanel.pnlDiffContainer.add(new JScrollPane(txt));
         return;
      }


      _diffPanel.lblLeftTitle.setText(s_stringMgr.getString("DiffToLocalCtrl.revision.date", revisionDateString));
      _diffPanel.lblRightTitle.setText(s_stringMgr.getString("DiffToLocalCtrl.sqlEditor"));

      Path sqlEditorContentTempFile = DiffFileUtil.createSqlEditorContentTempFile(_revisionListControllerChannel.getEditorContent());
      Path gitRevisionContentTempFile = DiffFileUtil.createGitRevisionTempFile(gitRevisionContent);

      _meldCore.executeDiff(gitRevisionContentTempFile.toFile().getAbsolutePath(), sqlEditorContentTempFile.toFile().getAbsolutePath(), null, text -> _revisionListControllerChannel.replaceEditorContent(text));
      _diffPanel.pnlDiffContainer.add(_meldCore.getConfigurableMeldPanel().getMeldPanel());
   }

   public void cleanUpMelds()
   {
      _meldCore.cleanMeldPanel();
   }
}
