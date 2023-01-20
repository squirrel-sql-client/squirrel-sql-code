package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.gui.ReadCompleteChoiceCombo;
import net.sourceforge.squirrel_sql.fw.gui.RightLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Inner class that extends OkJPanel so that we can call the ok()
 * method to save the data when the user is happy with it.
 */
class DataTypeClobOkJPanel extends OkJPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataTypeClobOkJPanel.class);

   private JRadioButton _radReadClobsOnTableLoading = new JRadioButton(s_stringMgr.getString("dataTypeClob.readOnFirstLoad"));

   private JRadioButton _radReadClobsOnCellVisible = new JRadioButton(s_stringMgr.getString("dataTypeClob.readWhenRendered"));
   private JRadioButton _radReadClobsOnCellFocused = new JRadioButton(s_stringMgr.getString("dataTypeClob.readWhenFocused"));
   private JRadioButton _radReadClobsNever = new JRadioButton(s_stringMgr.getString("dataTypeClob.readNever"));


   private RightLabel _lblReadCompleteChoice = new RightLabel(s_stringMgr.getString("dataTypeClob.read"));

   // Combo box for read-all/read-part of blob
   private ReadCompleteChoiceCombo _cboClobReadCompleteChoice = new ReadCompleteChoiceCombo();

   // text field for how many bytes of Blob to read
   private IntegerField _showClobSizeField = new IntegerField(5);

   // check box for whether to show newlines as "\n" for in-cell display
   private JCheckBox _chkMakeNewlinesVisibleInCell = new JCheckBox(s_stringMgr.getString("dataTypeClob.newlinesAsbackslashN"));
   private DataTypeClobProperties _properties;


   public DataTypeClobOkJPanel(DataTypeClobProperties properties)
   {
      layoutPanel();

      _properties = properties;

      _radReadClobsOnTableLoading.setSelected(_properties.isReadClobsOnTableLoading());
      _cboClobReadCompleteChoice.setSelectedIndex((_properties.isReadCompleteClobs()) ? ReadCompleteChoiceCombo.READ_ALL_IDX : ReadCompleteChoiceCombo.READ_PARTIAL_IDX);
      _showClobSizeField.setInt(_properties.getReadClobsSize());

      _radReadClobsOnCellFocused.setSelected(_properties.isReadClobsOnCellFocused());
      _radReadClobsOnCellVisible.setSelected(_properties.isReadClobsOnCellVisible());
      _radReadClobsNever.setSelected(_properties.isReadClobsNever());

      _chkMakeNewlinesVisibleInCell.setSelected(_properties.isMakeNewlinesVisibleInCell());


      _radReadClobsOnTableLoading.addActionListener(e -> onUiChanged());
      _radReadClobsOnCellVisible.addActionListener(e -> onUiChanged());
      _radReadClobsOnCellFocused.addActionListener(e -> onUiChanged());
      _radReadClobsNever.addActionListener(e -> onUiChanged());
      _cboClobReadCompleteChoice.addActionListener(e -> onUiChanged());

      onUiChanged();
   }

   private void layoutPanel()
   {
      // i18n[dataTypeClob.typeClob=CLOB   (SQL type 2005)]
      setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("dataTypeClob.typeClob_NClob")));

      setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(_radReadClobsOnTableLoading, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      add(_lblReadCompleteChoice, gbc);

      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      add(_cboClobReadCompleteChoice, gbc);

      gbc = new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      add(_showClobSizeField, gbc);


      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(_radReadClobsOnCellVisible, gbc);

      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(_radReadClobsOnCellFocused, gbc);

      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(_radReadClobsNever, gbc);


      gbc = new GridBagConstraints(0, 4, 3, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0);
      add(_chkMakeNewlinesVisibleInCell, gbc);


      ButtonGroup bg = new ButtonGroup();
      bg.add(_radReadClobsOnTableLoading);
      bg.add(_radReadClobsOnCellVisible);
      bg.add(_radReadClobsOnCellFocused);
      bg.add(_radReadClobsNever);

   }

   private void onUiChanged()
   {
      _lblReadCompleteChoice.setEnabled(_radReadClobsOnTableLoading.isSelected());
      _cboClobReadCompleteChoice.setEnabled(_radReadClobsOnTableLoading.isSelected());
      _showClobSizeField.setEnabled(_radReadClobsOnTableLoading.isSelected() && (_cboClobReadCompleteChoice.getSelectedIndex() == ReadCompleteChoiceCombo.READ_PARTIAL_IDX));

      _chkMakeNewlinesVisibleInCell.setEnabled(false == _radReadClobsNever.isSelected());
   }


   /**
    * User has clicked OK in the surrounding JPanel,
    * so save the current state of all variables
    */
   public void ok()
   {
      _properties.setReadClobsOnTableLoading(_radReadClobsOnTableLoading.isSelected());
      _properties.setReadCompleteClobs((_cboClobReadCompleteChoice.getSelectedIndex() == ReadCompleteChoiceCombo.READ_PARTIAL_IDX) ? false : true);
      _properties.setReadClobsSize(_showClobSizeField.getInt());

      _properties.setReadClobsOnCellFocused(_radReadClobsOnCellFocused.isSelected());

      _properties.setReadClobsOnCellVisible(_radReadClobsOnCellVisible.isSelected());

      _properties.setReadClobsNever(_radReadClobsNever.isSelected());


      _properties.setMakeNewlinesVisibleInCell(_chkMakeNewlinesVisibleInCell.isSelected());

      _properties.saveProperties();
   }

} // end of inner class
