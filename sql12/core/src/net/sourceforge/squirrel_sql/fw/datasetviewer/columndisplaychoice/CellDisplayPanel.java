package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CellDisplayPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDisplayPanel.class);
   private final JPanel _pnlContent = new JPanel(new GridLayout(1, 1));
   private final DisplayPanelListener _displayPanelListener;

   private List<Pair<ColumnDisplayDefinition, DisplayMode>> _columnsToNonDefaultDisplayMode = new ArrayList<>();
   private ColumnDisplayDefinition _currentColumnDisplayDefinition;
   private JComboBox<DisplayMode> _cboDisplayMode = new JComboBox<>(DisplayMode.values());

   public CellDisplayPanel(DisplayPanelListener displayPanelListener)
   {
      _displayPanelListener = displayPanelListener;
      setLayout(new BorderLayout(3, 3));
      add(createDisplaySelectionPanel(), BorderLayout.NORTH);
      add(_pnlContent, BorderLayout.CENTER);

      _cboDisplayMode.setSelectedItem(DisplayMode.DEFAULT);
      _cboDisplayMode.addActionListener(e -> onDisplayModeChanged());
   }

   private void onDisplayModeChanged()
   {
      if(null != _currentColumnDisplayDefinition)
      {
         _columnsToNonDefaultDisplayMode.removeIf(p -> p.getLeft().matchesByQualifiedName(_currentColumnDisplayDefinition));
         if(getDisplayMode() != DisplayMode.DEFAULT)
         {
            _columnsToNonDefaultDisplayMode.add(Pair.of(_currentColumnDisplayDefinition, getDisplayMode()));
         }
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

   public void setCurrentColumnDisplayDefinition(ColumnDisplayDefinition columnDisplayDefinition)
   {
      _currentColumnDisplayDefinition = columnDisplayDefinition;

      DisplayMode modeToSel = DisplayMode.DEFAULT;
      Optional<Pair<ColumnDisplayDefinition, DisplayMode>> nonDefaultDisplay =
            _columnsToNonDefaultDisplayMode.stream().filter(p -> p.getLeft().matchesByQualifiedName(columnDisplayDefinition)).findFirst();

      if(nonDefaultDisplay.isPresent())
      {
         modeToSel = nonDefaultDisplay.get().getRight();
      }

      // If changed this will fire onDisplayModeChanged()
      _cboDisplayMode.setSelectedItem(modeToSel);
   }

   public DisplayMode getDisplayMode()
   {
      return (DisplayMode) _cboDisplayMode.getSelectedItem();
   }
}
