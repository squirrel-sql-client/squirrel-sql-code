package net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.jmeld.JMeldConfigCtrl;
import net.sourceforge.squirrel_sql.client.gui.jmeld.JMeldUtil;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.JMeldPanelHandlerSaveCallback;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.jmeld.settings.EditorSettings;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.AbstractContentPanel;
import org.jmeld.ui.BufferDiffPanel;
import org.jmeld.ui.FilePanel;
import org.jmeld.ui.JMeldPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JMeldCore
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(JMeldDiffPresentation.class);
   private boolean _useEmbedded;
   private JMeldPanel _meldPanel;
   private ConfigurableMeldPanel _configurableMeldPanel;

   public JMeldCore()
   {
   }

   public JMeldCore(boolean useEmbedded)
   {
      _useEmbedded = useEmbedded;

      if(false == _useEmbedded)
      {
         return;
      }

      _configurableMeldPanel = createPanel(null);

   }

   public void executeDiff(String leftFilename, String rightFilename, String diffDialogTitle)
   {
      executeDiff(leftFilename, rightFilename, diffDialogTitle, null);
   }

   public void executeDiff(String leftFilename, String rightFilename, String diffDialogTitle, JMeldPanelHandlerSaveCallback saveCallback)
   {
      if (false == _useEmbedded && null != diffDialogTitle)
      {
         JDialog diffDialog = new JDialog(Main.getApplication().getMainFrame(), diffDialogTitle);

         _configurableMeldPanel = createPanel(diffDialog);
         diffDialog.getContentPane().setLayout(new GridLayout(1,1));
         diffDialog.add(_configurableMeldPanel);

         JMeldSettings.getInstance().setDrawCurves(true);


         GUIUtils.enableCloseByEscape(diffDialog, w -> cleanMeldPanel());
         diffDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
               cleanMeldPanel();
            }

            @Override
            public void windowClosed(WindowEvent e)
            {
               cleanMeldPanel();
            }
         });

         GUIUtils.initLocation(diffDialog, 500, 400, JMeldDiffPresentation.class.getName());

         diffDialog.setVisible(true);
      }

      if (_useEmbedded)
      {
         cleanMeldPanel();
      }

      final EditorSettings editorSettings = JMeldSettings.getInstance().getEditor();
      JMeldSettings.getInstance().getEditor().enableCustomFont(true);
      JMeldSettings.getInstance().getEditor().setFont(new Font(Font.MONOSPACED, Font.PLAIN, new JLabel().getFont().getSize()));
      editorSettings.setRightsideReadonly(null == saveCallback);
      editorSettings.setLeftsideReadonly(true);

      if(null != saveCallback)
      {
         GUIUtils.forceProperty(() -> 1 < JMeldPanel.getContentPanelList(_meldPanel.getTabbedPane()).size(), () -> prepareSaveButton(_meldPanel, saveCallback));
      }

      _meldPanel.openComparison(leftFilename, rightFilename);
   }

   private void prepareSaveButton(JMeldPanel meldPanel, JMeldPanelHandlerSaveCallback saveCallback)
   {
      JButton saveButton = getRightFilePanel(meldPanel).getSaveButton();
      saveButton.addActionListener(e -> onSaveToEditor(meldPanel, saveCallback));
      saveButton.setToolTipText(s_stringMgr.getString("JMeldCore.write.changes.to.sql.editor"));
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

   private void onSaveToEditor(JMeldPanel meldPanel, JMeldPanelHandlerSaveCallback saveCallback)
   {
      String savedText = getRightFilePanel(meldPanel).getEditor().getText();
      saveCallback.rightSideSaved(savedText);
   }


   private ConfigurableMeldPanel createPanel(JDialog diffDialog)
   {
      _meldPanel = new NonExitingJMeldPanel(() -> close(diffDialog));
      _meldPanel.SHOW_TABBEDPANE_OPTION.disable();
      _meldPanel.SHOW_TOOLBAR_OPTION.disable();
      _meldPanel.SHOW_FILE_LABEL_OPTION.disable();

      return new ConfigurableMeldPanel(_meldPanel, new JMeldConfigCtrl(_meldPanel));
   }

   public ConfigurableMeldPanel getConfigurableMeldPanel()
   {
      return _configurableMeldPanel;
   }

   private void close(JDialog diffDialog)
   {
      cleanMeldPanel();

      if (null != diffDialog)
      {
         diffDialog.setVisible(false);
         diffDialog.dispose();
      }
   }

   public void cleanMeldPanel()
   {
      JMeldUtil.cleanMeldPanel(_meldPanel);
   }

}
