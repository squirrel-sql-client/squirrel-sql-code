package net.sourceforge.squirrel_sql.client.session.messagepanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import java.awt.Color;

public class MessagePrefsCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MessagePrefsCtrl.class);

   private MessagePrefsPanel _messagePrefsPanel = new MessagePrefsPanel();

   public MessagePrefsCtrl()
   {
      _messagePrefsPanel.btnMessageForeground.addActionListener(e -> chooseColor(_messagePrefsPanel.btnMessageForeground));
      _messagePrefsPanel.btnMessageBackground.addActionListener(e -> chooseColor(_messagePrefsPanel.btnMessageBackground));
      _messagePrefsPanel.btnMessageHistoryForeground.addActionListener(e -> chooseColor(_messagePrefsPanel.btnMessageHistoryForeground));
      _messagePrefsPanel.btnMessageHistoryBackground.addActionListener(e -> chooseColor(_messagePrefsPanel.btnMessageHistoryBackground));

      _messagePrefsPanel.btnWarningForeground.addActionListener(e -> chooseColor(_messagePrefsPanel.btnWarningForeground));
      _messagePrefsPanel.btnWarningBackground.addActionListener(e -> chooseColor(_messagePrefsPanel.btnWarningBackground));
      _messagePrefsPanel.btnWarningHistoryForeground.addActionListener(e -> chooseColor(_messagePrefsPanel.btnWarningHistoryForeground));
      _messagePrefsPanel.btnWarningHistoryBackground.addActionListener(e -> chooseColor(_messagePrefsPanel.btnWarningHistoryBackground));

      _messagePrefsPanel.btnErrorForeground.addActionListener(e -> chooseColor(_messagePrefsPanel.btnErrorForeground));
      _messagePrefsPanel.btnErrorBackground.addActionListener(e -> chooseColor(_messagePrefsPanel.btnErrorBackground));
      _messagePrefsPanel.btnErrorHistoryForeground.addActionListener(e -> chooseColor(_messagePrefsPanel.btnErrorHistoryForeground));
      _messagePrefsPanel.btnErrorHistoryBackground.addActionListener(e -> chooseColor(_messagePrefsPanel.btnErrorHistoryBackground));

      _messagePrefsPanel.btnTestMessage.addActionListener(e -> onTestMessage());
      _messagePrefsPanel.btnTestWarning.addActionListener(e -> onTestWarning());
      _messagePrefsPanel.btnTestError.addActionListener(e -> onTestError());

      _messagePrefsPanel.btnRestoreDefault.addActionListener(e -> onRestoreDefault());
   }

   private void onRestoreDefault()
   {
      loadData(new SquirrelPreferences());
   }

   private void chooseColor(JButton btnColorToChoose)
   {
      Color color = JColorChooser.showDialog(_messagePrefsPanel, s_stringMgr.getString("MessagePrefsCtrl.choose.color"), btnColorToChoose.getBackground());

      if(null != color)
      {
         btnColorToChoose.setBackground(color);
      }
   }

   public MessagePrefsPanel getPanel()
   {
      return _messagePrefsPanel;
   }

   private void onTestMessage()
   {
      applyCurrentSettingsForTest();

      Main.getApplication().getMessageHandler().showMessage("Test message (previous/history)");
      Main.getApplication().getMessageHandler().showMessage("Test message (current)");
   }


   private void onTestWarning()
   {
      applyCurrentSettingsForTest();

      Main.getApplication().getMessageHandler().showWarningMessage("Test warning (previous/history)");
      Main.getApplication().getMessageHandler().showWarningMessage("Test warning (current)");
   }

   private void onTestError()
   {
      applyCurrentSettingsForTest();

      Main.getApplication().getMessageHandler().showErrorMessage("Test error (previous/history)");
      Main.getApplication().getMessageHandler().showErrorMessage("Test error (current)");
   }

   private void applyCurrentSettingsForTest()
   {
      SquirrelPreferences prefsBuf = new SquirrelPreferences();
      applyChanges(prefsBuf);

      if(prefsBuf.isMessagePanelWhiteBackgroundAsUIDefault() || prefsBuf.isMessagePanelBlackForegroundAsUIDefault())
      {
         Main.getApplication().getMainFrame().getMessagePanel().showMessage(s_stringMgr.getString("MessagePrefsCtrl.warn.cannot.set.to.Defaults"));
      }
   }


   public void loadData(SquirrelPreferences prefs)
   {
      _messagePrefsPanel.btnMessageForeground.setBackground(new Color(prefs.getMessagePanelMessageForeground()));
      _messagePrefsPanel.btnMessageBackground.setBackground(new Color(prefs.getMessagePanelMessageBackground()));
      _messagePrefsPanel.btnMessageHistoryForeground.setBackground(new Color(prefs.getMessagePanelMessageHistoryForeground()));
      _messagePrefsPanel.btnMessageHistoryBackground.setBackground(new Color(prefs.getMessagePanelMessageHistoryBackground()));

      _messagePrefsPanel.btnWarningForeground.setBackground(new Color(prefs.getMessagePanelWarningForeground()));
      _messagePrefsPanel.btnWarningBackground.setBackground(new Color(prefs.getMessagePanelWarningBackground()));
      _messagePrefsPanel.btnWarningHistoryForeground.setBackground(new Color(prefs.getMessagePanelWarningHistoryForeground()));
      _messagePrefsPanel.btnWarningHistoryBackground.setBackground(new Color(prefs.getMessagePanelWarningHistoryBackground()));

      _messagePrefsPanel.btnErrorForeground.setBackground(new Color(prefs.getMessagePanelErrorForeground()));
      _messagePrefsPanel.btnErrorBackground.setBackground(new Color(prefs.getMessagePanelErrorBackground()));
      _messagePrefsPanel.btnErrorHistoryForeground.setBackground(new Color(prefs.getMessagePanelErrorHistoryForeground()));
      _messagePrefsPanel.btnErrorHistoryBackground.setBackground(new Color(prefs.getMessagePanelErrorHistoryBackground()));

      _messagePrefsPanel.chkWhiteBackgroundAsUIDefault.setSelected(prefs.isMessagePanelWhiteBackgroundAsUIDefault());
      _messagePrefsPanel.chkBlackForegroundAsUIDefault.setSelected(prefs.isMessagePanelBlackForegroundAsUIDefault());
   }

   public void applyChanges(SquirrelPreferences prefs)
   {
      prefs.setMessagePanelMessageForeground(_messagePrefsPanel.btnMessageForeground.getBackground().getRGB());
      prefs.setMessagePanelMessageBackground(_messagePrefsPanel.btnMessageBackground.getBackground().getRGB());
      prefs.setMessagePanelMessageHistoryForeground(_messagePrefsPanel.btnMessageHistoryForeground.getBackground().getRGB());
      prefs.setMessagePanelMessageHistoryBackground(_messagePrefsPanel.btnMessageHistoryBackground.getBackground().getRGB());

      prefs.setMessagePanelWarningForeground(_messagePrefsPanel.btnWarningForeground.getBackground().getRGB());
      prefs.setMessagePanelWarningBackground(_messagePrefsPanel.btnWarningBackground.getBackground().getRGB());
      prefs.setMessagePanelWarningHistoryForeground(_messagePrefsPanel.btnWarningHistoryForeground.getBackground().getRGB());
      prefs.setMessagePanelWarningHistoryBackground(_messagePrefsPanel.btnWarningHistoryBackground.getBackground().getRGB());

      prefs.setMessagePanelErrorForeground(_messagePrefsPanel.btnErrorForeground.getBackground().getRGB());
      prefs.setMessagePanelErrorBackground(_messagePrefsPanel.btnErrorBackground.getBackground().getRGB());
      prefs.setMessagePanelErrorHistoryForeground(_messagePrefsPanel.btnErrorHistoryForeground.getBackground().getRGB());
      prefs.setMessagePanelErrorHistoryBackground(_messagePrefsPanel.btnErrorHistoryBackground.getBackground().getRGB());

      prefs.setMessagePanelWhiteBackgroundAsUIDefault(_messagePrefsPanel.chkWhiteBackgroundAsUIDefault.isSelected());
      prefs.setMessagePanelBlackForegroundAsUIDefault(_messagePrefsPanel.chkBlackForegroundAsUIDefault.isSelected());

      // Apply to message panel.
      Main.getApplication().getMainFrame().getMessagePanel().applyMessagePanelStyle(prefs);
   }

   //public void switchToLight()
   //{
   //   loadData(new SquirrelPreferences()); // Setting to default
   //   applyChanges(Main.getApplication().getSquirrelPreferences());
   //}
   //
   //public void switchToDark()
   //{
   //   final SquirrelPreferences darkThemePrefs = new SquirrelPreferences(); // Start with default
   //
   //    darkThemePrefs.setMessagePanelMessageForeground(Color.green.getRGB());
   //    darkThemePrefs.setMessagePanelMessageBackground(Color.white.getRGB());
   //    darkThemePrefs.setMessagePanelMessageHistoryForeground(Color.black.getRGB());
   //    darkThemePrefs.setMessagePanelMessageHistoryBackground(Color.white.getRGB());
   //
   //    darkThemePrefs.setMessagePanelWarningForeground(Color.yellow.getRGB());
   //    darkThemePrefs.setMessagePanelWarningBackground(Color.white.getRGB());
   //    darkThemePrefs.setMessagePanelWarningHistoryForeground(Color.yellow.darker().darker().getRGB());
   //    darkThemePrefs.setMessagePanelWarningHistoryBackground(Color.white.getRGB());
   //
   //    loadData(darkThemePrefs);
   //    applyChanges(Main.getApplication().getSquirrelPreferences());
   //}
}
