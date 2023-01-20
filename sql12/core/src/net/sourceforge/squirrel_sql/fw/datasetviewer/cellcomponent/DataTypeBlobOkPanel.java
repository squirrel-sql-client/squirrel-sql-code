package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.gui.ReadCompleteChoiceCombo;
import net.sourceforge.squirrel_sql.fw.gui.RightLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Inner class that extends OkJPanel so that we can call the ok()
 * method to save the data when the user is happy with it.
 */
class DataTypeBlobOkPanel extends OkJPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataTypeBlobOkPanel.class);

   private JRadioButton _radReadBlobsOnTableLoading = new JRadioButton(s_stringMgr.getString("dataTypeBlob.readOnFirstLoad"));
   private JRadioButton _radReadBlobsOnCellVisible = new JRadioButton(s_stringMgr.getString("dataTypeBlob.readWhenRendered"));
   private JRadioButton _radReadBlobsOnCellFocused = new JRadioButton(s_stringMgr.getString("dataTypeBlob.readWhenFocused"));
   private JRadioButton _radReadBlobsNever = new JRadioButton(s_stringMgr.getString("dataTypeBlob.readNever"));

   // label for type combo - used to enable/disable text associated with the combo
   private RightLabel _lblReadCompleteChoice = new RightLabel(s_stringMgr.getString("dataTypeBlob.read"));

   // Combo box for read-all/read-part of blob
   private ReadCompleteChoiceCombo _cboBlobReadCompleteChoice = new ReadCompleteChoiceCombo();

   // text field for how many bytes of Blob to read
   private IntegerField _showBlobSizeField = new IntegerField(5);
   private DataTypeBlobProperties _properties;

   DataTypeBlobOkPanel(DataTypeBlobProperties properties)
   {
      _properties = properties;
      layoutPanel();

      _radReadBlobsOnTableLoading.setSelected(_properties.isReadBlobsOnTableLoading());
      _cboBlobReadCompleteChoice.setSelectedIndex(_properties.isReadCompleteBlobs() ? ReadCompleteChoiceCombo.READ_ALL_IDX : ReadCompleteChoiceCombo.READ_PARTIAL_IDX);
      _showBlobSizeField.setInt(_properties.getReadBlobsSize());

      _radReadBlobsOnCellFocused.setSelected(_properties.isReadBlobsOnCellFocused());
      _radReadBlobsOnCellVisible.setSelected(_properties.isReadBlobsOnCellVisible());
      _radReadBlobsNever.setSelected(_properties.isReadBlobsNever());


      _radReadBlobsOnTableLoading.addActionListener(e -> onUiChanged());
      _radReadBlobsOnCellVisible.addActionListener(e -> onUiChanged());
      _radReadBlobsOnCellFocused.addActionListener(e -> onUiChanged());
      _radReadBlobsNever.addActionListener(e -> onUiChanged());
      _cboBlobReadCompleteChoice.addActionListener(e -> onUiChanged());

      onUiChanged();
   }

   private void layoutPanel()
   {
      setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("dataTypeBlob.blobType")));

      setLayout(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(_radReadBlobsOnTableLoading, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      add(_lblReadCompleteChoice, gbc);

      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      add(_cboBlobReadCompleteChoice, gbc);

      gbc = new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      add(_showBlobSizeField, gbc);


      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(_radReadBlobsOnCellVisible, gbc);

      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(_radReadBlobsOnCellFocused, gbc);

      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(_radReadBlobsNever, gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(_radReadBlobsOnTableLoading);
      bg.add(_radReadBlobsOnCellVisible);
      bg.add(_radReadBlobsOnCellFocused);
      bg.add(_radReadBlobsNever);
   }

   private void onUiChanged()
   {
      _lblReadCompleteChoice.setEnabled(_radReadBlobsOnTableLoading.isSelected());
      _cboBlobReadCompleteChoice.setEnabled(_radReadBlobsOnTableLoading.isSelected());
      _showBlobSizeField.setEnabled(_radReadBlobsOnTableLoading.isSelected() && (_cboBlobReadCompleteChoice.getSelectedIndex() == ReadCompleteChoiceCombo.READ_PARTIAL_IDX));
   }

   /**
    * User has clicked OK in the surrounding JPanel,
    * so save the current state of all variables
    */
   public void ok()
   {
      _properties.setReadBlobsOnTableLoading(_radReadBlobsOnTableLoading.isSelected());
      _properties.setReadCompleteBlobs(_cboBlobReadCompleteChoice.getSelectedIndex() == ReadCompleteChoiceCombo.READ_PARTIAL_IDX ? false : true);
      _properties.setReadBlobsSize(_showBlobSizeField.getInt());

      _properties.setReadBlobsOnCellFocused(_radReadBlobsOnCellFocused.isSelected());

      _properties.setReadBlobsOnCellVisible(_radReadBlobsOnCellVisible.isSelected());

      _properties.setReadBlobsNever(_radReadBlobsNever.isSelected());

      _properties.saveProperties();
   }

} // end of inner class
