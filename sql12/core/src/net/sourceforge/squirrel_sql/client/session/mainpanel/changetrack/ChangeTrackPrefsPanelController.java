package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;


import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.JColorChooser;
import javax.swing.SwingUtilities;
import java.awt.Color;

public class ChangeTrackPrefsPanelController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChangeTrackPrefsPanelController.class);


   private ChangeTrackPrefsPanel _panel;

   public ChangeTrackPrefsPanelController()
   {
      _panel = new ChangeTrackPrefsPanel();
   }

   public ChangeTrackPrefsPanel getPanel()
   {
      return _panel;
   }

   public void loadData(SquirrelPreferences prefs)
   {
      _panel.chkEnableChangeTracking.setSelected(prefs.isEnableChangeTracking());
      _panel.chkEnableChangeTracking.addActionListener(e -> adjustUi());

      _panel.chkGitCommitMsgManually.setSelected(prefs.isGitCommitMsgManually());
      _panel.chkGitCommitMsgManually.addActionListener(e -> adjustUi());
      _panel.txtGitCommitMsgDefault.setText(prefs.getGitCommitMsgDefault());

      _panel.btnDeletedBold.setSelected(prefs.isDeletedBold());
      _panel.btnDeletedItalics.setSelected(prefs.isDeletedItalics());

      _panel.setDeletedForeground(prefs.getDeltedForegroundRGB());
      _panel.btnDeletedForeground.addActionListener(e -> _panel.setDeletedForeground(chooseColorRgb(_panel.getDeletedForeground())));

      _panel.setInsertBeginBackground(prefs.getInsertBeginBackgroundRGB());
      _panel.btnInsertBeginBackground.addActionListener(e -> _panel.setInsertBeginBackground(chooseColorRgb(_panel.getInsertBeginBackground())));

      _panel.setInsertEndBackground(prefs.getInsertEndBackgroundRGB());
      _panel.btnInsertEndBackground.addActionListener(e -> _panel.setInsertEndBackground(chooseColorRgb(_panel.getInsertEndBackground())));

      adjustUi();
   }

   private Integer chooseColorRgb(int curRgb)
   {
      Color color = JColorChooser.showDialog(_panel, s_stringMgr.getString("ChangeTrackPrefsPanel.choose.color"), new Color(curRgb));

      if(null == color)
      {
         return null;
      }
      return color.getRGB();
   }

   private void adjustUi()
   {
      boolean ctEnabled = _panel.chkEnableChangeTracking.isSelected();

      _panel.chkGitCommitMsgManually.setEnabled(ctEnabled);
      _panel.txtGitCommitMsgDefault.setEnabled(ctEnabled && false == _panel.chkGitCommitMsgManually.isSelected());

      _panel.btnDeletedBold.setEnabled(ctEnabled);
      _panel.btnDeletedItalics.setEnabled(ctEnabled);

      _panel.btnDeletedForeground.setEnabled(ctEnabled);

      _panel.btnInsertBeginBackground.setEnabled(ctEnabled);

      _panel.btnInsertEndBackground.setEnabled(ctEnabled);

      _panel.txtExampleChangeTrackBase.setEnabled(ctEnabled);
      _panel.txtExampleEditorText.setEnabled(ctEnabled);
      _panel.txtExamplePopup.setEnabled(ctEnabled);
   }

   public void applyChanges(SquirrelPreferences prefs)
   {
      prefs.setEnableChangeTracking(_panel.chkEnableChangeTracking.isSelected());

      prefs.setGitCommitMsgManually(_panel.chkGitCommitMsgManually.isSelected());

      if (false == StringUtilities.isEmpty(_panel.txtGitCommitMsgDefault.getText(), true))
      {
         prefs.setGitCommitMsgDefault(_panel.txtGitCommitMsgDefault.getText());
      }

      prefs.setDeletedBold(_panel.btnDeletedBold.isSelected());
      prefs.setDeletedItalics(_panel.btnDeletedItalics.isSelected());

      prefs.setDeltedForegroundRGB(_panel.getDeletedForeground());

      prefs.setInsertBeginBackgroundRGB(_panel.getInsertBeginBackground());
      prefs.setInsertEndBackgroundRGB(_panel.getInsertEndBackground());
   }
}
