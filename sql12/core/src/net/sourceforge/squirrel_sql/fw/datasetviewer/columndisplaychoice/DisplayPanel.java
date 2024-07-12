package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DisplayPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DisplayPanel.class);
   private final JPanel _pnlContent = new JPanel(new GridLayout(1, 1));
   private final DisplayPanelListener _displayPanelListener;

   private List<Pair<ColumnDisplayDefinition, DisplayMode>> _columnsToNonDefaultDisplayMode = new ArrayList<>();
   private ExtTableColumn _currentColumn;
   private JComboBox<DisplayMode> _cboDisplayMode = new JComboBox<>(DisplayMode.values());

   public DisplayPanel(DisplayPanelListener displayPanelListener)
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
      _columnsToNonDefaultDisplayMode.removeIf(p -> p.getLeft().matchesByQualifiedName(_currentColumn.getColumnDisplayDefinition()));
      if(getDisplayMode() != DisplayMode.DEFAULT)
      {
         _columnsToNonDefaultDisplayMode.add(Pair.of(_currentColumn.getColumnDisplayDefinition(), getDisplayMode()));
      }

      _displayPanelListener.displayModeChanged();
   }

   private JPanel createDisplaySelectionPanel()
   {
      JPanel ret = new JPanel(new BorderLayout(5,5));
      JLabel lbl = new JLabel(s_stringMgr.getString("DisplayPanel.select.display"));
      ret.add(lbl, BorderLayout.WEST);
      ret.add(_cboDisplayMode, BorderLayout.CENTER);
      return ret;
   }

   public void setContentComponent(JComponent contentComponent)
   {
      _pnlContent.removeAll();
      _pnlContent.add(contentComponent);
   }

   public void setCurrentColumn(ExtTableColumn column)
   {
      _currentColumn = column;

      DisplayMode modeToSel = DisplayMode.DEFAULT;
      Optional<Pair<ColumnDisplayDefinition, DisplayMode>> nonDefaultDisplay =
            _columnsToNonDefaultDisplayMode.stream().filter(p -> p.getLeft().matchesByQualifiedName(_currentColumn.getColumnDisplayDefinition())).findFirst();

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
