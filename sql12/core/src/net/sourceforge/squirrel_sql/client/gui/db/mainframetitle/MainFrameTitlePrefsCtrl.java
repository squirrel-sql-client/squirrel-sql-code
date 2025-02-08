package net.sourceforge.squirrel_sql.client.gui.db.mainframetitle;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;

public class MainFrameTitlePrefsCtrl
{
   private final MainFrameTitlePrefsPanel _panel;

   public MainFrameTitlePrefsCtrl()
   {
      _panel = new MainFrameTitlePrefsPanel();

      _panel.cboPosApplicationName.addActionListener(e -> onComboChanged(e));
      _panel.cboPosVersion.addActionListener(e -> onComboChanged(e));
      _panel.cboPosUserDir.addActionListener(e -> onComboChanged(e));
      _panel.cboPosHomeDir.addActionListener(e -> onComboChanged(e));
      _panel.cboPosSessionName.addActionListener(e -> onComboChanged(e));
      _panel.cboPosSavedSessionOrGroupName.addActionListener(e -> onComboChanged(e));
   }

   private void onComboChanged(ActionEvent e)
   {
      JComboBox<PositionInMainFrameTitle>[] comboBoxes = getComboBoxesArray();

      for(JComboBox<PositionInMainFrameTitle> comboBox : comboBoxes)
      {
         if(comboBox == e.getSource() || comboBox.getSelectedItem() == PositionInMainFrameTitle.POS_NONE)
         {
            continue;
         }

         if(comboBox.getSelectedItem() == ((JComboBox<PositionInMainFrameTitle>)e.getSource()).getSelectedItem())
         {
            comboBox.setSelectedItem(PositionInMainFrameTitle.POS_NONE);
         }
      }
   }

   private JComboBox<PositionInMainFrameTitle>[] getComboBoxesArray()
   {
      JComboBox<PositionInMainFrameTitle>[] comboBoxes = new JComboBox[]
            {
                  _panel.cboPosApplicationName,
                  _panel.cboPosVersion,
                  _panel.cboPosUserDir,
                  _panel.cboPosHomeDir,
                  _panel.cboPosSessionName,
                  _panel.cboPosSavedSessionOrGroupName
            };
      return comboBoxes;
   }

   public MainFrameTitlePrefsPanel getPanel()
   {
      return _panel;
   }

   public void loadData(SquirrelPreferences prefs)
   {
      _panel.cboPosApplicationName.setSelectedItem(PositionInMainFrameTitle.valueOf(prefs.getMainFrameTitlePosApplicationName()));
      _panel.cboPosVersion.setSelectedItem(PositionInMainFrameTitle.valueOf(prefs.getMainFrameTitlePosVersion()));
      _panel.cboPosUserDir.setSelectedItem(PositionInMainFrameTitle.valueOf(prefs.getMainFrameTitlePosUserDir()));
      _panel.cboPosHomeDir.setSelectedItem(PositionInMainFrameTitle.valueOf(prefs.getMainFrameTitlePosHomeDir()));
      _panel.cboPosSessionName.setSelectedItem(PositionInMainFrameTitle.valueOf(prefs.getMainFrameTitlePosSessionName()));
      _panel.cboPosSavedSessionOrGroupName.setSelectedItem(PositionInMainFrameTitle.valueOf(prefs.getMainFrameTitlePosSavedSessionOrGroupName()));
   }

   public void applyChanges(SquirrelPreferences prefs)
   {
      prefs.setMainFrameTitlePosApplicationName(((PositionInMainFrameTitle)_panel.cboPosApplicationName.getSelectedItem()).name());
      prefs.setMainFrameTitlePosVersion(((PositionInMainFrameTitle)_panel.cboPosVersion.getSelectedItem()).name());
      prefs.setMainFrameTitlePosUserDir(((PositionInMainFrameTitle)_panel.cboPosUserDir.getSelectedItem()).name());
      prefs.setMainFrameTitlePosHomeDir(((PositionInMainFrameTitle)_panel.cboPosHomeDir.getSelectedItem()).name());
      prefs.setMainFrameTitlePosSessionName(((PositionInMainFrameTitle)_panel.cboPosSessionName.getSelectedItem()).name());
      prefs.setMainFrameTitlePosSavedSessionOrGroupName(((PositionInMainFrameTitle)_panel.cboPosSavedSessionOrGroupName.getSelectedItem()).name());
      Main.getApplication().getMainFrame().getMainFrameTitleHandler().updateMainFrameTitle();
   }
}
