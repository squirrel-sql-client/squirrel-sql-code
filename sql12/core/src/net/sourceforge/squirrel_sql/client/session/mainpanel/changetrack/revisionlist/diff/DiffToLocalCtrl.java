package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.diff;

import net.sourceforge.squirrel_sql.client.gui.jmeld.JMeldConfigCtrl;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.RevisionListControllerChannel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.nio.file.Path;

public class DiffToLocalCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DiffToLocalCtrl.class);
   private final RevisionListControllerChannel _revisionListControllerChannel;

   private DiffPanel _panel;
   private JMeldPanelHandler _meldPanelHandler;


   public DiffToLocalCtrl(RevisionListControllerChannel revisionListControllerChannel)
   {
      _revisionListControllerChannel = revisionListControllerChannel;
      _meldPanelHandler = new JMeldPanelHandler(true, revisionListControllerChannel);

      _panel = new DiffPanel(new JMeldConfigCtrl(_meldPanelHandler.getMeldPanel()).getPanel());
   }

   public DiffPanel getPanel()
   {
      return _panel;
   }

   public void setSelectedRevision(String gitRevisionContent, String revisionDateString)
   {
      _panel.pnlDiffContainer.removeAll();

      if(null == gitRevisionContent)
      {
         _panel.lblLeftTitle.setText(s_stringMgr.getString("DiffToLocalCtrl.no.revision.selected.short"));

         JTextArea txt = new JTextArea();
         txt.setText(s_stringMgr.getString("DiffToLocalCtrl.no.revision.selected"));
         txt.setEditable(false);
         txt.setBorder(BorderFactory.createEtchedBorder());
         _panel.pnlDiffContainer.add(new JScrollPane(txt));
         return;
      }


      _panel.lblLeftTitle.setText(s_stringMgr.getString("DiffToLocalCtrl.revision.date", revisionDateString));
      _panel.lblRightTitle.setText(s_stringMgr.getString("DiffToLocalCtrl.sqlEditor"));

      Path sqlEditorContentTempFile = DiffFileUtil.createSqlEditorContentTempFile(_revisionListControllerChannel.getEditorContent());
      Path gitRevisionContentTempFile = DiffFileUtil.createGitRevisionTempFile(gitRevisionContent);

      _meldPanelHandler.showDiff(gitRevisionContentTempFile, sqlEditorContentTempFile);
      _panel.pnlDiffContainer.add(_meldPanelHandler.getMeldPanel());
   }

   public void cleanUpMelds()
   {
      _meldPanelHandler.cleanMeldPanel();
   }
}
