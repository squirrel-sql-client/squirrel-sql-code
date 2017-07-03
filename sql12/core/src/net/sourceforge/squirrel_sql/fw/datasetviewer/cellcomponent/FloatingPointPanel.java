package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;

/**
 * Inner class that extends OkJPanel so that we can call the ok()
 * method to save the data when the user is happy with it.
 */
class FloatingPointPanel extends OkJPanel
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FloatingPointBase.class);

   JRadioButton optUseJavaDefaultFormat = new JRadioButton(s_stringMgr.getString("floatingPointBase.useDefaultFormat", new Double(3.14159).toString()));
   JRadioButton optUseLocaleDependendFormat = new JRadioButton();
   JRadioButton optUseUserDefinedFormat = new JRadioButton(s_stringMgr.getString("floatingPointBase.optUseUserDefinedFormat"));


   IntegerField localeDependendMinimumFraction = new IntegerField(2);
   IntegerField localeDependendMaximumFraction = new IntegerField(2);


   IntegerField userDefinedMinimumFraction = new IntegerField(2);
   IntegerField userDefinedMaximumFraction = new IntegerField(2);


   JComboBox cboGroupingSeparator = new JComboBox(new String[]{".", ",", FloatingPointBase.GROUPING_SEPARATOR_NONE});
   JComboBox cboDecimalSeparator = new JComboBox(new String[]{",", "."});


   public FloatingPointPanel()
   {
      setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("floatingPointBase.typeBigDecimal")));

      setLayout(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 0, 4), 0, 0);
      add(optUseLocaleDependendFormat, gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 30, 4, 4), 0, 0);
      add(createLocalDependentConfigsPanel(), gbc);


      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 0, 4), 0, 0);
      add(optUseUserDefinedFormat, gbc);

      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 30, 4, 4), 0, 0);
      add(createUserDefinedConfigsPanel(), gbc);


      gbc = new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 0);
      add(optUseJavaDefaultFormat, gbc);


      ButtonGroup bg = new ButtonGroup();
      bg.add(optUseJavaDefaultFormat);
      bg.add(optUseUserDefinedFormat);
      bg.add(optUseLocaleDependendFormat);


      ChangeListener radioButtonListener = new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            onRadioButtonsChanged();
         }
      };

      optUseJavaDefaultFormat.addChangeListener(radioButtonListener);
      optUseLocaleDependendFormat.addChangeListener(radioButtonListener);
      optUseUserDefinedFormat.addChangeListener(radioButtonListener);

      FocusListener localeDependentConfigListener = new FocusAdapter()
      {
         @Override
         public void focusLost(FocusEvent e)
         {
            onLocaleDependentConfigChanged();
         }
      };

      optUseLocaleDependendFormat.setSelected(FloatingPointBaseDTProperties.isUseLocaleFormat());
      optUseJavaDefaultFormat.setSelected(FloatingPointBaseDTProperties.isUseJavaDefaultFormat());
      optUseUserDefinedFormat.setSelected(FloatingPointBaseDTProperties.isUseUserDefinedFormat());


      localeDependendMinimumFraction.addFocusListener(localeDependentConfigListener);
      localeDependendMaximumFraction.addFocusListener(localeDependentConfigListener);


      localeDependendMinimumFraction.setInt(FloatingPointBaseDTProperties.getMinimumFractionDigits());
      localeDependendMaximumFraction.setInt(FloatingPointBaseDTProperties.getMaximumFractionDigits());

      userDefinedMinimumFraction.setInt(FloatingPointBaseDTProperties.getUserDefinedMinimumFractionDigits());
      userDefinedMaximumFraction.setInt(FloatingPointBaseDTProperties.getUserDefinedMaximumFractionDigits());

      cboGroupingSeparator.setSelectedItem(FloatingPointBaseDTProperties.getUserDefinedGroupingSeparator());
      cboDecimalSeparator.setSelectedItem(FloatingPointBaseDTProperties.getUserDefinedDecimalSeparator());


      onLocaleDependentConfigChanged();

   }

   private void onLocaleDependentConfigChanged()
   {
      try
      {
         int minimumFractionAsInt = localeDependendMinimumFraction.getInt();
         int maximumFractionAsInt = localeDependendMaximumFraction.getInt();
         optUseLocaleDependendFormat.setText(createTextForOptUseLocaleDependendFormat(minimumFractionAsInt, maximumFractionAsInt));
         localeDependendMinimumFraction.repaint();
         localeDependendMaximumFraction.repaint();
      }
      catch (NumberFormatException nfe)
      {
      }
   }

   private void onRadioButtonsChanged()
   {
      userDefinedMinimumFraction.setEditable(optUseUserDefinedFormat.isSelected());
      userDefinedMinimumFraction.setEnabled(optUseUserDefinedFormat.isSelected());
      userDefinedMaximumFraction.setEditable(optUseUserDefinedFormat.isSelected());
      userDefinedMaximumFraction.setEnabled(optUseUserDefinedFormat.isSelected());

      localeDependendMinimumFraction.setEditable(optUseLocaleDependendFormat.isSelected());
      localeDependendMinimumFraction.setEnabled(optUseLocaleDependendFormat.isSelected());
      localeDependendMaximumFraction.setEditable(optUseLocaleDependendFormat.isSelected());
      localeDependendMaximumFraction.setEnabled(optUseLocaleDependendFormat.isSelected());

      cboGroupingSeparator.setEnabled(optUseUserDefinedFormat.isSelected());
      cboDecimalSeparator.setEnabled(optUseUserDefinedFormat.isSelected());
   }

   private JPanel createLocalDependentConfigsPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0);
      JLabel minimumFractionLabel = new JLabel(s_stringMgr.getString("floatingPointBase.uselocaleDependendFormat.minDecimalDigits"));
      ret.add(minimumFractionLabel, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0);
      ret.add(localeDependendMinimumFraction, gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0);
      JLabel maximumFractionLabel = new JLabel(s_stringMgr.getString("floatingPointBase.uselocaleDependendFormat.maxDecimalDigits"));
      ret.add(maximumFractionLabel, gbc);

      gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0);
      ret.add(localeDependendMaximumFraction, gbc);

      return ret;
   }

   private JPanel createUserDefinedConfigsPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0);
      JLabel minimumFractionLabel = new JLabel(s_stringMgr.getString("floatingPointBase.uselocaleDependendFormat.minDecimalDigits"));
      ret.add(minimumFractionLabel, gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0);
      ret.add(userDefinedMinimumFraction, gbc);


      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0);
      JLabel maximumFractionLabel = new JLabel(s_stringMgr.getString("floatingPointBase.uselocaleDependendFormat.maxDecimalDigits"));
      ret.add(maximumFractionLabel, gbc);

      gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0);
      ret.add(userDefinedMaximumFraction, gbc);


      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0);
      JLabel groupingSeparatorLabel = new JLabel(s_stringMgr.getString("floatingPointBase.uselocaleDependendFormat.groupingSeparator"));
      ret.add(groupingSeparatorLabel, gbc);

      gbc = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0);
      cboGroupingSeparator.setFont(new Font(cboGroupingSeparator.getFont().getFamily(), Font.BOLD, cboGroupingSeparator.getFont().getSize()));
      ret.add(cboGroupingSeparator, gbc);


      gbc = new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0);
      JLabel decimalSeparatorLabel = new JLabel(s_stringMgr.getString("floatingPointBase.uselocaleDependendFormat.decimalSeparator"));
      ret.add(decimalSeparatorLabel, gbc);

      gbc = new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 4, 4, 4), 0, 0);
      cboDecimalSeparator.setFont(new Font(cboDecimalSeparator.getFont().getFamily(), Font.BOLD, cboDecimalSeparator.getFont().getSize()));
      ret.add(cboDecimalSeparator, gbc);

      return ret;
   }

   /**
    * Creates the text for the {@link JRadioButton} of "use locale depended format" with respect of maxFractionDigits
    *
    * @param maxFractionDigits Number of digits after the comma to use in the example.
    */
   private String createTextForOptUseLocaleDependendFormat(int minFractionDigits, int maxFractionDigits)
   {
      NumberFormat numberFormat = NumberFormat.getInstance();
      numberFormat.setMinimumFractionDigits(minFractionDigits);
      numberFormat.setMaximumFractionDigits(maxFractionDigits);
      return s_stringMgr.getString("floatingPointBase.uselocaleDependendFormat",
            numberFormat.format(new Double(1.00000)), numberFormat.format(new Double(3.14159)));
   }

   /**
    * User has clicked OK in the surrounding JPanel,
    * so save the current state of all variables
    */
   @Override
   public void ok()
   {
      FloatingPointBaseDTProperties.setUseJavaDefaultFormat(optUseJavaDefaultFormat.isSelected());
      FloatingPointBaseDTProperties.setUseLocaleFormat(optUseLocaleDependendFormat.isSelected());
      FloatingPointBaseDTProperties.setUseUserDefinedFormat(optUseUserDefinedFormat.isSelected());

      FloatingPointBaseDTProperties.setMinimumFractionDigits(localeDependendMinimumFraction.getInt());
      FloatingPointBaseDTProperties.setMaximumFractionDigits(localeDependendMaximumFraction.getInt());

      FloatingPointBaseDTProperties.setUserDefinedMinimumFractionDigits(userDefinedMinimumFraction.getInt());
      FloatingPointBaseDTProperties.setUserDefinedMaximumFractionDigits(userDefinedMaximumFraction.getInt());

      FloatingPointBaseDTProperties.setUserDefinedDecimalSeparator((String)cboDecimalSeparator.getSelectedItem());
      FloatingPointBaseDTProperties.setUserDefinedGroupingSeparator((String)cboGroupingSeparator.getSelectedItem());
   }
}
