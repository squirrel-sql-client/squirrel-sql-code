package net.sourceforge.squirrel_sql.client.session.properties;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ResultLimitAndReadOnPanelSmallPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLResultConfigCtrl.class);
   public static final String CBO_LIMIT_ROWS = s_stringMgr.getString("ResultLimitAndReadOnPanelSmallPanel.limitRows");
   public static final String CBO_READ_ON_BLOCK_SIZE = s_stringMgr.getString("ResultLimitAndReadOnPanelSmallPanel.readOnWithBlockSize");


   private JCheckBox _chkLimitRowsEnabled;
   private JComboBox _cboLimitOrReadOn;
   private IntegerField _txtLimitOrBlockSize;
   private SessionProperties _props;
   private boolean _inLoadData = false;
   private boolean _inEvent;

   public ResultLimitAndReadOnPanelSmallPanel()
   {
      createUI();

      _chkLimitRowsEnabled.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onChkLimitRowsEnabled();
         }
      });


      _cboLimitOrReadOn.addItemListener(new ItemListener()
      {
         @Override
         public void itemStateChanged(ItemEvent e)
         {
            onCboLimitOrReadOnChanged(e);
         }
      });

      _txtLimitOrBlockSize.getDocument().addDocumentListener(new DocumentListener()
      {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            onLimitOrBlockSizeChanged();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            onLimitOrBlockSizeChanged();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            onLimitOrBlockSizeChanged();
         }
      });
   }

   private void onLimitOrBlockSizeChanged()
   {
      if(_inLoadData || _inEvent || 0 >= _txtLimitOrBlockSize.getInt())
      {
         return;
      }

      try
      {
         _inEvent = true;

         if(CBO_LIMIT_ROWS == _cboLimitOrReadOn.getSelectedItem())
         {
            _props.setSQLNbrRowsToShow(_txtLimitOrBlockSize.getInt());
         }
         else
         {
            _props.setSQLReadOnBlockSize(_txtLimitOrBlockSize.getInt());
         }
      }
      finally
      {
         _inEvent = false;
      }


   }

   private void onCboLimitOrReadOnChanged(ItemEvent e)
   {
      try
      {
         if(_inLoadData || _inEvent || ItemEvent.SELECTED != e.getStateChange())
         {
            return;
         }

         _inEvent = true;

         if(CBO_READ_ON_BLOCK_SIZE == _cboLimitOrReadOn.getSelectedItem())
         {
            _props.setSQLReadOn(true);
            _txtLimitOrBlockSize.setInt(_props.getSQLReadOnBlockSize());
         }
         else
         {
            _props.setSQLReadOn(false);
            _txtLimitOrBlockSize.setInt(_props.getSQLNbrRowsToShow());

            if(0 < _props.getSQLNbrRowsToShow())
            {
               // If we switch the combo box back to limit rows, make sure the check box is selected.
               _props.setSQLLimitRows(true);
            }
         }

      }
      finally
      {
         _inEvent = false;
      }
      loadData(_props);

   }

   private void onChkLimitRowsEnabled()
   {
      if(_inLoadData || _inEvent)
      {
         return;
      }

      _props.setSQLLimitRows(_chkLimitRowsEnabled.isSelected());
      loadData(_props);
   }

   private void createUI()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,2),0,0);
      _chkLimitRowsEnabled = new JCheckBox();
      add(_chkLimitRowsEnabled, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      _cboLimitOrReadOn = new JComboBox(new Object[]{CBO_LIMIT_ROWS, CBO_READ_ON_BLOCK_SIZE});
      add(_cboLimitOrReadOn, gbc);

      gbc = new GridBagConstraints(2,0,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,5),0,0);
      _txtLimitOrBlockSize = new IntegerField(10);
      add(_txtLimitOrBlockSize, gbc);
   }


   public void loadData(SessionProperties props)
   {
      if(_inLoadData || _inEvent)
      {
         return;
      }

      try
      {
         _inLoadData = true;
         _props = props;

         if(_props.getSQLReadOn())
         {
            _cboLimitOrReadOn.setSelectedItem(CBO_READ_ON_BLOCK_SIZE);
            _chkLimitRowsEnabled.setEnabled(false);
            _txtLimitOrBlockSize.setEnabled(true);
            _txtLimitOrBlockSize.setInt(_props.getSQLReadOnBlockSize());

            _cboLimitOrReadOn.setToolTipText(s_stringMgr.getString("ResultLimitAndReadOnPanelSmallPanel.readOnWarningHtml"));

         }
         else
         {
            _cboLimitOrReadOn.setSelectedItem(CBO_LIMIT_ROWS);
            _chkLimitRowsEnabled.setEnabled(true);
            _chkLimitRowsEnabled.setSelected(_props.getSQLLimitRows());
            _txtLimitOrBlockSize.setInt(_props.getSQLNbrRowsToShow());
            _txtLimitOrBlockSize.setEnabled(_props.getSQLLimitRows());
            _cboLimitOrReadOn.setToolTipText(null);
         }
      }
      finally
      {
         _inLoadData = false;
      }
   }

   public void propsChanged(SessionProperties props)
   {
      loadData(props);
   }
}
