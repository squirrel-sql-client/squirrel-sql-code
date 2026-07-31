package net.sourceforge.squirrel_sql.client.session.action.dbdiff.gui;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

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

public class JMeldCore
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(JMeldDiffPresentation.class);
   private boolean _useEmbedded;
   private JMeldPanel _meldPanel;
   private ConfigurableMeldPanel _configurableMeldPanel;

   public JMeldCore()
   {
      this(false);
   }

   public JMeldCore(boolean useEmbedded)
   {
      _useEmbedded = useEmbedded;

      if(false == _useEmbedded)
      {
         return;
      }

      // Callers of embedded usage need the ConfigurableMeldPanel to place the meld panel itself
      // es well as the configuration panel in their own dialog.
      _configurableMeldPanel = createPanel(null);
   }

   public void executeDiff(String leftFilename, String rightFilename, String diffDialogTitle)
   {
      executeDiff(leftFilename, rightFilename, diffDialogTitle, null);
   }

   public void executeDiff(String leftFilename, String rightFilename, String diffDialogTitle, JMeldPanelHandlerSaveCallback saveCallback)
   {
      executeDiff(leftFilename, rightFilename, diffDialogTitle, saveCallback, Main.getApplication().getMainFrame());
   }
   
   public void executeDiff(String leftFilename, String rightFilename, String diffDialogTitle, JMeldPanelHandlerSaveCallback saveCallback, Window owningWindow)
   {
      if (_useEmbedded)
      {
         cleanMeldPanel();
         doCompare(leftFilename, rightFilename, saveCallback);
      }
      else
      {
         JDialog diffDialog = new JDialog(owningWindow, diffDialogTitle);

         // When saving is allowed the dialog is made modal
         // to prevent the editor contents from being changed concurrently.
         // diffDialog.setModal(null != saveCallback);

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

         doCompare(leftFilename, rightFilename, saveCallback);
         diffDialog.setVisible(true);
      }
   }

   private void doCompare(String leftFilename, String rightFilename, JMeldPanelHandlerSaveCallback saveCallback)
   {
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
      fixJideLookAndFeelFactoryIllegalAccessError();

      _meldPanel = new NonExitingJMeldPanel(() -> close(diffDialog));
      _meldPanel.SHOW_TABBEDPANE_OPTION.disable();
      _meldPanel.SHOW_TOOLBAR_OPTION.disable();
      _meldPanel.SHOW_FILE_LABEL_OPTION.disable();

      return new ConfigurableMeldPanel(_meldPanel, new JMeldConfigCtrl(_meldPanel));
   }

   /**
    * Fixes the following exception at least on Windows:
    * java.lang.IllegalAccessError: class com.jidesoft.plaf.LookAndFeelFactory (in unnamed module @0x69f77c76) cannot access class com.sun.java.swing.plaf.windows.WindowsLookAndFeel (in module java.desktop)
    *        because module java.desktop does not export com.sun.java.swing.plaf.windows to unnamed module @0x69f77c76
    * 	at com.jidesoft.plaf.LookAndFeelFactory.getDefaultStyle(LookAndFeelFactory.java:539)
    * 	at com.jidesoft.plaf.LookAndFeelFactory.installJideExtension(LookAndFeelFactory.java:598)
    * 	at com.jidesoft.swing.JideTabbedPane.updateUI(JideTabbedPane.java:329)
    * 	at java.desktop/javax.swing.JTabbedPane.<init>(JTabbedPane.java:227)
    * 	at com.jidesoft.swing.JideTabbedPane.<init>(JideTabbedPane.java:292)
    * 	at com.jidesoft.swing.JideTabbedPane.<init>(JideTabbedPane.java:264)
    * 	at org.jmeld.ui.JMeldPanel.<init>(JMeldPanel.java:81)
    */
   private static void fixJideLookAndFeelFactoryIllegalAccessError()
   {
      System.setProperty("jide.defaultStyle", "2"); // 2 = com.jidesoft.plaf.LookAndFeelFactory.ECLIPSE_STYLE
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
