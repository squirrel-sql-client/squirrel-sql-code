package net.sourceforge.squirrel_sql.client.session.messagepanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class MessagePrefsPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MessagePrefsPanel.class);

   JButton btnMessageForeground = new JButton();
   JButton btnMessageBackground = new JButton();
   JButton btnMessageHistoryForeground = new JButton();
   JButton btnMessageHistoryBackground = new JButton();
   JButton btnTestMessage = new JButton();

   JButton btnWarningForeground = new JButton();
   JButton btnWarningBackground = new JButton();
   JButton btnWarningHistoryForeground = new JButton();
   JButton btnWarningHistoryBackground = new JButton();
   JButton btnTestWarning = new JButton();

   JButton btnErrorForeground = new JButton();
   JButton btnErrorBackground = new JButton();
   JButton btnErrorHistoryForeground = new JButton();
   JButton btnErrorHistoryBackground = new JButton();
   JButton btnTestError = new JButton();

   JCheckBox chkWhiteBackgroundAsUIDefault;
   JCheckBox chkBlackForegroundAsUIDefault;

   JButton btnRestoreDefault;

   public MessagePrefsPanel()
   {
      super(new GridBagLayout());
      setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("MessagePrefsPanel.title")));


      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(7, 5, 0, 5), 0, 0);
      final JPanel messages = createRowPanel("MessagePrefsPanel.messages", btnMessageForeground, btnMessageBackground, btnMessageHistoryForeground, btnMessageHistoryBackground, btnTestMessage);
      btnMessageForeground.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.last.message.foreground.color"));
      btnMessageBackground.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.last.message.background.color"));
      btnMessageHistoryForeground.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.message.history.foreground.color"));
      btnMessageHistoryBackground.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.message.history.background.color"));
      add(messages, gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(7, 5, 0, 5), 0, 0);
      final JPanel warnings = createRowPanel("MessagePrefsPanel.warnings", btnWarningForeground, btnWarningBackground, btnWarningHistoryForeground, btnWarningHistoryBackground, btnTestWarning);
      btnWarningForeground.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.last.warning.foreground.color"));
      btnWarningBackground.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.last.warning.background.color"));
      btnWarningHistoryForeground.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.warning.history.foreground.color"));
      btnWarningHistoryBackground.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.warning.history.background.color"));
      add(warnings, gbc);

      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(7, 5, 3, 5), 0, 0);
      final JPanel errors = createRowPanel("MessagePrefsPanel.errors", btnErrorForeground, btnErrorBackground, btnErrorHistoryForeground, btnErrorHistoryBackground, btnTestError);
      btnErrorForeground.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.last.error.foreground.color"));
      btnErrorBackground.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.last.error.background.color"));
      btnErrorHistoryForeground.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.error.history.foreground.color"));
      btnErrorHistoryBackground.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.error.history.background.color"));
      add(errors, gbc);

      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(7, 5, 3, 5), 0, 0);
      chkWhiteBackgroundAsUIDefault = new JCheckBox(s_stringMgr.getString("MessagePrefsPanel.whiteBackgroundLeavesUIBackground"));
      chkWhiteBackgroundAsUIDefault.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.WhiteBackgroundAsUIDefault.tooltip"));
      add(chkWhiteBackgroundAsUIDefault, gbc);

      gbc = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(3, 5, 3, 5), 0, 0);
      chkBlackForegroundAsUIDefault = new JCheckBox(s_stringMgr.getString("MessagePrefsPanel.blackForegroundLeavesUIBackground"));
      chkBlackForegroundAsUIDefault.setToolTipText(s_stringMgr.getString("MessagePrefsPanel.BlackForegroundAsUIDefault.tooltip"));
      add(chkBlackForegroundAsUIDefault, gbc);

      gbc = new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 3, 5), 0, 0);
      btnRestoreDefault = new JButton(s_stringMgr.getString("MessagePrefsPanel.restoreDefault"));
      add(btnRestoreDefault, gbc);
   }

   private int getMaxTitleWidth()
   {
      return
         GUIUtils.getMaxStringWidth(new JLabel(),
                                    s_stringMgr.getString("MessagePrefsPanel.messages"),
                                    s_stringMgr.getString("MessagePrefsPanel.warnings"),
                                    s_stringMgr.getString("MessagePrefsPanel.errors"));
   }

   private JPanel createRowPanel(String titleKey, JButton btnForeground, JButton btnBackground, JButton btnHistoryForeground, JButton btnMessageHistoryBackground, JButton btnTest)
   {
      SquirrelResources rsrc = Main.getApplication().getResources();
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      final JLabel lblTitle = new JLabel(s_stringMgr.getString(titleKey));
      GUIUtils.setPreferredWidth(lblTitle, getMaxTitleWidth());
      ret.add(lblTitle, gbc);

      gbc = new GridBagConstraints(1,0,1,1, 0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnForeground.setIcon(rsrc.getIcon(SquirrelResources.IImageNames.PEN));
      ret.add(styleAsColorChooseButton(btnForeground), gbc);

      gbc = new GridBagConstraints(2,0,1,1, 0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnBackground.setIcon(rsrc.getIcon(SquirrelResources.IImageNames.FILL));
      ret.add(styleAsColorChooseButton(btnBackground), gbc);


      gbc = new GridBagConstraints(3,0,1,1, 0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      ret.add(new JLabel(s_stringMgr.getString("MessagePrefsPanel.history")), gbc);

      gbc = new GridBagConstraints(4,0,1,1, 0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnHistoryForeground.setIcon(rsrc.getIcon(SquirrelResources.IImageNames.PEN));
      ret.add(styleAsColorChooseButton(btnHistoryForeground), gbc);

      gbc = new GridBagConstraints(5,0,1,1, 0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnMessageHistoryBackground.setIcon(rsrc.getIcon(SquirrelResources.IImageNames.FILL));
      ret.add(styleAsColorChooseButton(btnMessageHistoryBackground), gbc);


      gbc = new GridBagConstraints(6,0,1,1, 0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnTest.setText(s_stringMgr.getString("MessagePrefsPanel.test"));
      ret.add(btnTest, gbc);

      return ret;
   }

   private JButton styleAsColorChooseButton(JButton btn)
   {
      btn.setBorder(BorderFactory.createEtchedBorder());
      return btn;
   }

}
