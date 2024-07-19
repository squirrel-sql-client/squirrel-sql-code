package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;

public class CellDisplayPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDisplayPanel.class);
   private final JPanel _pnlContent = new JPanel(new GridLayout(1, 1));
   private final DisplayPanelListener _displayPanelListener;

   private ColumnDisplayDefinition _currentColumnDisplayDefinition;
   private JComboBox<DisplayMode> _cboDisplayMode = new JComboBox<>(DisplayMode.values());

   public CellDisplayPanel(DisplayPanelListener displayPanelListener)
   {
      _displayPanelListener = displayPanelListener;
      setLayout(new BorderLayout(3, 3));
      add(createDisplaySelectionPanel(), BorderLayout.NORTH);
      add(_pnlContent, BorderLayout.CENTER);

      _cboDisplayMode.setSelectedItem(DisplayMode.DEFAULT);
      _cboDisplayMode.addItemListener(e -> onDisplayModeChanged(e));
   }

   private void onDisplayModeChanged(ItemEvent e)
   {
      if(e.getStateChange() == ItemEvent.DESELECTED)
      {
         return;
      }

      if(null != _currentColumnDisplayDefinition)
      {
         Main.getApplication().getCellDetailDisplayModeManager().putDisplayMode(_currentColumnDisplayDefinition, getDisplayMode());
      }

      _displayPanelListener.displayModeChanged();
   }

   private JPanel createDisplaySelectionPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(3,3,3,0), 0,0);
      JLabel lbl = new JLabel(s_stringMgr.getString("DisplayPanel.select.display"));
      ret.add(lbl, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(3,3,3,0), 0,0);
      ret.add(GUIUtils.setPreferredWidth(_cboDisplayMode, _cboDisplayMode.getPreferredSize().width + 40), gbc);

      gbc = new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(3,3,3,0), 0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }

   public void setContentComponent(JComponent contentComponent)
   {
      _pnlContent.removeAll();
      _pnlContent.add(contentComponent);
   }

   public void setCurrentColumnDisplayDefinition(ColumnDisplayDefinition cdd)
   {
      _currentColumnDisplayDefinition = cdd;

      DisplayMode modeToSel = Main.getApplication().getCellDetailDisplayModeManager().getNonDefaultDisplayMode(cdd);
      if(null == modeToSel)
      {
         modeToSel = DisplayMode.DEFAULT;
      }

      // If changed this will fire onDisplayModeChanged()
      _cboDisplayMode.setSelectedItem(modeToSel);
   }

   public DisplayMode getDisplayMode()
   {
      return (DisplayMode) _cboDisplayMode.getSelectedItem();
   }
}
