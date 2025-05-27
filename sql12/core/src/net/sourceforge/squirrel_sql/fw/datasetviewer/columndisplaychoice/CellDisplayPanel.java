package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class CellDisplayPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDisplayPanel.class);
   private final JPanel _pnlContent = new JPanel(new GridLayout(1, 1));
   private DisplayPanelListener _displayPanelListener;
   private CellDetailCloseListener _cellDetailCloseListener;
   private ToggleCellDataDialogPinnedListener _toggleCellDataDialogPinnedListener;

   private ColumnDisplayDefinition _currentColumnDisplayDefinition;
   private JComboBox<DisplayMode> _cboDisplayMode = new JComboBox<>(DisplayMode.values());
   private JButton _btnClose = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.CLOSE));
   private JToggleButton _btnTogglePinned;
   private JButton _btnScale;

   public CellDisplayPanel(DisplayPanelListener displayPanelListener,
                           ToggleCellDataDialogPinnedListener toggleCellDataDialogPinnedListener,
                           boolean pinned)
   {
      _toggleCellDataDialogPinnedListener = toggleCellDataDialogPinnedListener;
      initPanel(displayPanelListener, pinned);
   }

   public CellDisplayPanel(DisplayPanelListener displayPanelListener, CellDetailCloseListener cellDetailCloseListener)
   {
      _cellDetailCloseListener = cellDetailCloseListener;
      initPanel(displayPanelListener, false);
   }

   private void initPanel(DisplayPanelListener displayPanelListener, boolean pinned)
   {
      _displayPanelListener = displayPanelListener;
      setLayout(new BorderLayout(3, 3));
      add(createDisplaySelectionPanel(pinned), BorderLayout.NORTH);
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

      _btnScale.setEnabled(DisplayMode.IMAGE == e.getItem());

      _displayPanelListener.displayModeChanged();
   }

   private JPanel createDisplaySelectionPanel(boolean pinned)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(3,3,3,0), 0,0);
      JLabel lbl = new JLabel(s_stringMgr.getString("DisplayPanel.select.display"));
      ret.add(lbl, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(3,3,3,0), 0,0);
      ret.add(GUIUtils.setPreferredWidth(_cboDisplayMode, _cboDisplayMode.getPreferredSize().width + 40), gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(3,3,3,0), 0,0);
      ret.add(new SmallToolTipInfoButton(s_stringMgr.getString("DisplayPanel.info.button")).getButton(), gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(3,3,3,0), 0,0);
      _btnScale = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SCALE));
      _btnScale.setToolTipText(s_stringMgr.getString("DisplayPanel.scale.image.to.fit"));
      _btnScale.addActionListener(e -> _displayPanelListener.scaleImageToPanelSize());
      _btnScale.setEnabled(false);
      ret.add(GUIUtils.styleAsToolbarButton(_btnScale), gbc);

      if(null != _toggleCellDataDialogPinnedListener)
      {
         gbc = new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE, new Insets(3,10,3,0), 0,0);
         _btnTogglePinned = new JToggleButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.PINNED));
         _btnTogglePinned.setToolTipText(s_stringMgr.getString("CellDisplayPanel.pinned.tooltip"));
         _btnTogglePinned.setSelected(pinned);
         ret.add(GUIUtils.styleAsToolbarButton(_btnTogglePinned), gbc);
         _btnTogglePinned.addActionListener(e -> _toggleCellDataDialogPinnedListener.onTogglePinned(_btnTogglePinned.isSelected()));
      }

      gbc = new GridBagConstraints(4,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL, new Insets(3,3,3,0), 0,0);
      ret.add(new JPanel(), gbc);

      if(null != _cellDetailCloseListener)
      {
         gbc = new GridBagConstraints(5,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
         ret.add(GUIUtils.styleAsToolbarButton(_btnClose), gbc);
         _btnClose.addActionListener(e -> _cellDetailCloseListener.close());
      }

      return ret;
   }

   public void setContentComponent(CellDisplayPanelContent<? extends JComponent> contentComponent)
   {
      _pnlContent.removeAll();
      _pnlContent.add(contentComponent.castToComponent());
   }

   public CellDisplayPanelContent<? extends JComponent> getContentComponent()
   {
      if(0 == _pnlContent.getComponents().length)
      {
         return null;
      }

      return (CellDisplayPanelContent<? extends JComponent>) _pnlContent.getComponent(0);
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

   public void dispose()
   {
      List.of(_btnTogglePinned.getListeners(ActionListener.class)).forEach(l -> _btnTogglePinned.removeActionListener(l));
      _toggleCellDataDialogPinnedListener = null;
   }

   public void switchOffPinned()
   {
      _btnTogglePinned.setSelected(false);
   }

   public void cleanUp()
   {
      getContentComponent().cleanUp();
      if( null != _displayPanelListener )
      {
         _displayPanelListener = null;
      }
   }
}
