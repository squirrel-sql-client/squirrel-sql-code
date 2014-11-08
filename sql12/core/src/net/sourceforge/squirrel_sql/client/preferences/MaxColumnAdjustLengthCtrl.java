package net.sourceforge.squirrel_sql.client.preferences;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MaxColumnAdjustLengthCtrl extends Component
{
   private static final StringManager s_stringMgr =  StringManagerFactory.getStringManager(MaxColumnAdjustLengthCtrl.class);


   private final JPanel _pnl;
   private final JCheckBox _chkSetMaxAdjustLen;
   private final JTextField _txtMaxAdjustLen;

   public MaxColumnAdjustLengthCtrl()
   {
      _pnl = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0,0,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
      JLabel lblDesc = new JLabel(s_stringMgr.getString("GeneralPreferencesPanel.maxColumnAdjustLength.description"));
      _pnl.add(lblDesc, gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
      _chkSetMaxAdjustLen = new JCheckBox(s_stringMgr.getString("GeneralPreferencesPanel.maxColumnAdjustLength.check"));
      _pnl.add(_chkSetMaxAdjustLen, gbc);

      gbc = new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
      _txtMaxAdjustLen = new JTextField();
      _txtMaxAdjustLen.setColumns(6);
      _pnl.add(_txtMaxAdjustLen, gbc);

      gbc = new GridBagConstraints(2,0,1,2,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0);
      _pnl.add(new JPanel(), gbc);

      _pnl.setBorder(BorderFactory.createEtchedBorder());

      _chkSetMaxAdjustLen.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onSetMaxAdjustLen();
         }
      });

   }

   private void onSetMaxAdjustLen()
   {
      _txtMaxAdjustLen.setEnabled(_chkSetMaxAdjustLen.isSelected());
   }

   public JPanel getPanel()
   {
      return _pnl;
   }

   public boolean isMaxColumnAdjustLengthDefined()
   {
      enforceValid();
      return _chkSetMaxAdjustLen.isSelected();
   }

   public int getMaxColumnAdjustLength()
   {
      if(StringUtilities.isEmpty(_txtMaxAdjustLen.getText(), true))
      {
         return -1;
      }

      try
      {
         int ret = Integer.parseInt(_txtMaxAdjustLen.getText());
         if (0 < ret)
         {
            return ret;
         }
         else
         {
            return -1;
         }
      }
      catch (NumberFormatException e)
      {
         return -1;
      }
   }

   private void enforceValid()
   {
      if(0 < getMaxColumnAdjustLength())
      {
         return;
      }

      _txtMaxAdjustLen.setText(null);
      _chkSetMaxAdjustLen.setSelected(false);
      onSetMaxAdjustLen();
   }

   public void init(boolean maxColumnAdjustLengthDefined, int maxColumnAdjustLength)
   {
      if( 0 < maxColumnAdjustLength)
      {
         _txtMaxAdjustLen.setText("" + maxColumnAdjustLength);
         _chkSetMaxAdjustLen.setSelected(maxColumnAdjustLengthDefined);
      }
      else
      {
         _chkSetMaxAdjustLen.setSelected(false);
      }
      onSetMaxAdjustLen();
   }
}
