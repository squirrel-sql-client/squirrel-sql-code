package net.sourceforge.squirrel_sql.client.session.action.dbdiff;

import net.sourceforge.squirrel_sql.client.gui.jmeld.JMeldUtil;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.AbstractContentPanel;
import org.jmeld.ui.BufferDiffPanel;
import org.jmeld.ui.FilePanel;
import org.jmeld.ui.JMeldPanel;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

public class JMeldPanelHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(JMeldPanelHandler.class);

   private final JMeldPanel _meldPanel;
   private final boolean _allowSaveToSqlEditor;
   private final JMeldPanelHandlerSaveCallback _meldPanelHandlerSaveCallback;

   public JMeldPanelHandler()
   {
      this(false, t->{/* not saved */});
   }

   public JMeldPanelHandler(boolean allowSaveToSqlEditor, JMeldPanelHandlerSaveCallback meldPanelHandlerSaveCallback)
   {
      _allowSaveToSqlEditor = allowSaveToSqlEditor;
      _meldPanelHandlerSaveCallback = meldPanelHandlerSaveCallback;
      _meldPanel = new JMeldPanel();

      _meldPanel.SHOW_TOOLBAR_OPTION.disable();
      _meldPanel.SHOW_TABBEDPANE_OPTION.disable();
      _meldPanel.SHOW_FILE_LABEL_OPTION.disable();

      JMeldSettings.getInstance().getEditor().enableCustomFont(true);
      JMeldSettings.getInstance().getEditor().setFont(new Font(Font.MONOSPACED, Font.PLAIN, new JLabel().getFont().getSize()));
      JMeldSettings.getInstance().getEditor().setLeftsideReadonly(true);
      JMeldSettings.getInstance().setDrawCurves(true);

   }

   public void showDiff(Path leftPath, Path rightPath)
   {
      JMeldSettings.getInstance().getEditor().setRightsideReadonly(false == _allowSaveToSqlEditor);


      JMeldUtil.cleanMeldPanel(_meldPanel);
      _meldPanel.openComparison(leftPath.toFile().getAbsolutePath(), rightPath.toFile().getAbsolutePath());

      if(_allowSaveToSqlEditor)
      {
         GUIUtils.forceProperty(() -> 1 < JMeldPanel.getContentPanelList(_meldPanel.getTabbedPane()).size(), () -> prepareSaveButton(_meldPanel));
      }
   }

   private void prepareSaveButton(JMeldPanel meldPanel)
   {
      JButton saveButton = getRightFilePanel(meldPanel).getSaveButton();
      saveButton.addActionListener(e -> onSaveToEditor(meldPanel));
      saveButton.setToolTipText(s_stringMgr.getString("JMeldPanelHandler.write.changes.to.sql.editor"));
   }

   private void onSaveToEditor(JMeldPanel meldPanel)
   {
      String savedText = getRightFilePanel(meldPanel).getEditor().getText();
      _meldPanelHandlerSaveCallback.rightSideSaved(savedText);
   }

   private FilePanel getRightFilePanel(JMeldPanel meldPanel)
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



   public JMeldPanel getMeldPanel()
   {
      return _meldPanel;
   }

   public void cleanMeldPanel()
   {
      JMeldUtil.cleanMeldPanel(_meldPanel);
   }
}
