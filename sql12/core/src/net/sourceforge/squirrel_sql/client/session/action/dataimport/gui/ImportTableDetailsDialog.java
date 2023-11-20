package net.sourceforge.squirrel_sql.client.session.action.dataimport.gui;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class ImportTableDetailsDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ImportTableDetailsDialog.class);

   JTextField txtTableNamePattern;
   JCheckBox chkSuggestColumnTypes;
   IntegerField txtVarcharLength;
   IntegerField txtNumericPrecision;
   IntegerField txtNumericScale;


   JTextArea txtCreateTableSQL;
   JButton btnCreateTable;
   JButton btnClose;


   public ImportTableDetailsDialog(Window owningWindow)
   {
      super(owningWindow, s_stringMgr.getString("ImportTableDetailsDialog.table.details"), DEFAULT_MODALITY_TYPE);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("ImportTableDetailsDialog.table.name.gen.explain")), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0);
      txtTableNamePattern = new JTextField();
      getContentPane().add(txtTableNamePattern, gbc);

      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0);
      chkSuggestColumnTypes = new JCheckBox(s_stringMgr.getString("ImportTableDetailsDialog.suggest.column.types.by.preview.csv.lines"));
      getContentPane().add(chkSuggestColumnTypes, gbc);


      gbc = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 5), 0, 0);
      getContentPane().add(createVarcharLengthPanel(), gbc);

      gbc = new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 5), 0, 0);
      getContentPane().add(createNumericPanel(), gbc);


      gbc = new GridBagConstraints(0, 6, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 5), 0, 0);
      txtCreateTableSQL = new JTextArea();
      JScrollPane scrollPane = new JScrollPane(txtCreateTableSQL);
      getContentPane().add(scrollPane, gbc);

      gbc = new GridBagConstraints(0, 7, 1, 0, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 5, 0), 0, 0);
      getContentPane().add(createButtonPanel(), gbc);



   }

   private JPanel createNumericPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(new JLabel(s_stringMgr.getString("ImportTableDetailsDialog.numeric.and.precision")), gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      txtNumericPrecision = new IntegerField(5, 1);
      txtNumericPrecision.setPreferredSize(new Dimension(200, txtVarcharLength.getPreferredSize().height));
      txtNumericPrecision.setMinimumSize(new Dimension(200, txtVarcharLength.getMinimumSize().height));
      txtNumericPrecision.setMaximumSize(new Dimension(200, txtVarcharLength.getMaximumSize().height));
      ret.add(txtNumericPrecision, gbc);

      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      ret.add(new JLabel(s_stringMgr.getString("ImportTableDetailsDialog.numeric.scale")), gbc);

      gbc = new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      txtNumericScale = new IntegerField(5, 1);
      txtNumericScale.setPreferredSize(new Dimension(200, txtVarcharLength.getPreferredSize().height));
      txtNumericScale.setMinimumSize(new Dimension(200, txtVarcharLength.getMinimumSize().height));
      txtNumericScale.setMaximumSize(new Dimension(200, txtVarcharLength.getMaximumSize().height));
      ret.add(txtNumericScale, gbc);

      return ret;
   }

   private JPanel createVarcharLengthPanel()
   {

      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(new JLabel(s_stringMgr.getString("ImportTableDetailsDialog.varchar.length")), gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0);
      txtVarcharLength = new IntegerField(5, 1);
      txtVarcharLength.setPreferredSize(new Dimension(200, txtVarcharLength.getPreferredSize().height));
      txtVarcharLength.setMinimumSize(new Dimension(200, txtVarcharLength.getMinimumSize().height));
      txtVarcharLength.setMaximumSize(new Dimension(200, txtVarcharLength.getMaximumSize().height));
      ret.add(txtVarcharLength, gbc);

      return ret;
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridLayout(1,2, 10,10));

      btnCreateTable = new JButton(s_stringMgr.getString("ImportTableDetailsDialog.create.table"));
      btnCreateTable.setToolTipText(s_stringMgr.getString("ImportTableDetailsDialog.create.table.tooltip"));
      ret.add(btnCreateTable);

      btnClose = new JButton(s_stringMgr.getString("ImportTableDetailsDialog.close"));
      btnClose.setToolTipText(s_stringMgr.getString("ImportTableDetailsDialog.close.tooltip"));
      ret.add(btnClose);

      return ret;
   }
}
