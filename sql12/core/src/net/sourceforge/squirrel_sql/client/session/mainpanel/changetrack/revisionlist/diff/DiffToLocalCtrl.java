package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.diff;

import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.RevisionListControllerChannel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.AbstractContentPanel;
import org.jmeld.ui.BufferDiffPanel;
import org.jmeld.ui.FilePanel;
import org.jmeld.ui.JMeldPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Font;
import java.nio.file.Path;

public class DiffToLocalCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DiffToLocalCtrl.class);
   private final RevisionListControllerChannel _revisionListControllerChannel;

   private DiffToLocalPanel _panel = new DiffToLocalPanel();
   private JMeldPanel _meldPanel;


   public DiffToLocalCtrl(RevisionListControllerChannel revisionListControllerChannel)
   {
      _revisionListControllerChannel = revisionListControllerChannel;
      _meldPanel = new JMeldPanel();

      _meldPanel.SHOW_TOOLBAR_OPTION.disable();
      _meldPanel.SHOW_TABBEDPANE_OPTION.disable();
      _meldPanel.SHOW_FILE_LABEL_OPTION.disable();

      JMeldSettings.getInstance().getEditor().enableCustomFont(true);
      JMeldSettings.getInstance().getEditor().setFont(new Font(Font.MONOSPACED, Font.PLAIN, new JLabel().getFont().getSize()));
      JMeldSettings.getInstance().getEditor().setLeftsideReadonly(true);
      JMeldSettings.getInstance().getEditor().setRightsideReadonly(false);
      JMeldSettings.getInstance().setDrawCurves(true);
   }

   public DiffToLocalPanel getPanel()
   {
      return _panel;
   }

   public void setSelectedRevision(String gitRevisionContent, String revisionDateString)
   {
      _panel.pnlDiffContainer.removeAll();

      if(null == gitRevisionContent)
      {
         _panel.lblGitRevision.setText(s_stringMgr.getString("DiffToLocalCtrl.no.revision.selected.short"));

         JTextArea txt = new JTextArea();
         txt.setText(s_stringMgr.getString("DiffToLocalCtrl.no.revision.selected"));
         txt.setEditable(false);
         txt.setBorder(BorderFactory.createEtchedBorder());
         _panel.pnlDiffContainer.add(new JScrollPane(txt));
         return;
      }

      JMeldUtil.cleanMeldPanel(_meldPanel);

      _panel.lblGitRevision.setText(s_stringMgr.getString("DiffToLocalCtrl.revision.date", revisionDateString));

      Path sqlEditorContentTempFile = DiffFileUtil.createSqlEditorContentTempFile(_revisionListControllerChannel.getEditorContent());
      Path gitRevisionContentTempFile = DiffFileUtil.createGitRevisionTempFile(gitRevisionContent);

      _meldPanel.openComparison(gitRevisionContentTempFile.toFile().getAbsolutePath(), sqlEditorContentTempFile.toFile().getAbsolutePath());
      _panel.pnlDiffContainer.add(_meldPanel);

      GUIUtils.forceProperty(() -> 1 < JMeldPanel.getContentPanelList(_meldPanel.getTabbedPane()).size(), () -> prepareSaveButton(_meldPanel));
   }

   private void prepareSaveButton(JMeldPanel meldPanel)
   {
      JButton saveButton = getRightFilePanel(meldPanel).getSaveButton();
      saveButton.addActionListener(e -> onSaveToEditor(meldPanel));
      saveButton.setToolTipText(s_stringMgr.getString("DiffToLocalCtrl.write.changes.to.sql.editor"));
   }

   private void onSaveToEditor(JMeldPanel meldPanel)
   {
      String savedText = getRightFilePanel(meldPanel).getEditor().getText();
      _revisionListControllerChannel.replaceEditorContent(savedText);
   }

   private static FilePanel getRightFilePanel(JMeldPanel meldPanel)
   {
      for( AbstractContentPanel abstractContentPanel : JMeldPanel.getContentPanelList(meldPanel.getTabbedPane()) )
      {
         if(abstractContentPanel instanceof BufferDiffPanel)
         {
            return ((BufferDiffPanel) abstractContentPanel).getFilePanel(BufferDiffPanel.RIGHT);
         }
      }
      throw new IllegalStateException("Failed to return org.jmeld.ui.FilePanel");
   }

   public void close()
   {
      JMeldUtil.cleanMeldPanel(_meldPanel);
   }
}
