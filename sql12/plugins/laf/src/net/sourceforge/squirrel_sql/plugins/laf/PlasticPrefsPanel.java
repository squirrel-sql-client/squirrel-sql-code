package net.sourceforge.squirrel_sql.plugins.laf;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.plaf.metal.MetalTheme;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;

/**
 * Preferences panel. Show a dropdown of themes.
 */
final class PlasticPrefsPanel extends BaseLAFPreferencesPanelComponent
{
   private AbstractPlasticController _ctrl;
   private JComboBox _themeCmb;
   private int _origSelThemeIdx;

   PlasticPrefsPanel(AbstractPlasticController ctrl)
   {
      _ctrl = ctrl;
      createUserInterface();
   }

   private void createUserInterface()
   {
      setLayout(new GridBagLayout());
      _themeCmb = new JComboBox();

      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.anchor = GridBagConstraints.WEST;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.insets = new Insets(4, 4, 4, 4);

      gbc.gridx = 0;
      gbc.gridy = 0;
      add(new JLabel("Theme:", SwingConstants.RIGHT), gbc);

      ++gbc.gridx;
      add(_themeCmb, gbc);
   }

   /**
    * @see BaseLAFPreferencesPanelComponent#loadPreferencesPanel()
    */
   public void loadPreferencesPanel()
   {
      super.loadPreferencesPanel();
      loadThemesCombo();
   }

   /**
    * @see BaseLAFPreferencesPanelComponent#applyChanges()
    */
   public boolean applyChanges()
   {
      super.applyChanges();
      if(_origSelThemeIdx != _themeCmb.getSelectedIndex())
      {
         _ctrl.setCurrentThemeName((String) _themeCmb.getSelectedItem());
         return true;
      }
      return false;
   }

   private void loadThemesCombo()
   {
      _themeCmb.removeAllItems();

      for (Iterator<MetalTheme> it = _ctrl.themesIterator(); it.hasNext(); )
      {
         _themeCmb.addItem((it.next()).getName());
      }

      if(_themeCmb.getModel().getSize() > 0)
      {
         String selThemeName = _ctrl.getCurrentThemeName();
         if(selThemeName != null && selThemeName.length() > 0)
         {
            _themeCmb.setSelectedItem(selThemeName);
         }
         if(_themeCmb.getSelectedIndex() == -1)
         {
            _themeCmb.setSelectedIndex(0);
         }
      }
      _origSelThemeIdx = _themeCmb.getSelectedIndex();
   }
}
