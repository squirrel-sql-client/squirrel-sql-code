package net.sourceforge.squirrel_sql.client.session.properties;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SQLResultConfigCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLResultConfigCtrl.class);


   private JCheckBox _sqlLimitRowsChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.limitrows"));
   private IntegerField _sqlNbrRowsToShowField = new IntegerField(10);


   private JCheckBox _sqlReadOnChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.readOn"));
   private IntegerField _sqlReadOnBlockSize = new IntegerField(10);

   private MultipleLineLabel _lblReadOnWarning = new MultipleLineLabel(s_stringMgr.getString("SessionSQLPropertiesPanel.readOnWarning"));


   private JCheckBox _sqlUseFetchSizeChk = new JCheckBox(s_stringMgr.getString("SessionSQLPropertiesPanel.fetchSize"));
   private IntegerField _sqlFetchSizeField = new IntegerField(10);

   private boolean _inUpdateControlStatus;

   public SQLResultConfigCtrl()
   {

      ActionListener actionListener = new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent evt)
         {
            updateCheckBoxStatus(evt);
         }
      };

      _sqlLimitRowsChk.addActionListener(actionListener);
      _sqlReadOnChk.addActionListener(actionListener);
      _sqlUseFetchSizeChk.addActionListener(actionListener);
   }


   public void loadData(SessionProperties props)
   {

      _sqlLimitRowsChk.setSelected(props.getSQLLimitRows());
      _sqlNbrRowsToShowField.setInt(props.getSQLNbrRowsToShow());

      _sqlUseFetchSizeChk.setSelected(props.getSQLUseFetchSize());
      _sqlFetchSizeField.setInt(props.getSQLFetchSize());

      _sqlReadOnChk.setSelected(props.getSQLReadOn());
      _sqlReadOnBlockSize.setInt(props.getSQLReadOnBlockSize());

      updateCheckBoxStatus(null);
   }

   public boolean isLimitRows()
   {
      return _sqlLimitRowsChk.isSelected();
   }

   public int getNbrRowsToShow()
   {
      return _sqlNbrRowsToShowField.getInt();
   }

   public boolean isUseFetchSize()
   {
      return _sqlUseFetchSizeChk.isSelected();
   }

   public int getFetchSize()
   {
      return _sqlFetchSizeField.getInt();
   }

   public boolean isReadOn()
   {
      return _sqlReadOnChk.isSelected();
   }

   public int getReadOnBlockSize()
   {
      return _sqlReadOnBlockSize.getInt();
   }



   private void updateCheckBoxStatus(ActionEvent evt)
   {
      if(_inUpdateControlStatus)
      {
         return;
      }

      try
      {
         _inUpdateControlStatus = true;


         if(_sqlReadOnChk.isSelected())
         {
            _sqlLimitRowsChk.setSelected(false);
            _sqlLimitRowsChk.setEnabled(false);
            _sqlNbrRowsToShowField.setEnabled(false);

            _sqlReadOnBlockSize.setEnabled(true);
            _lblReadOnWarning.setEnabled(true);
         }
         else
         {

            _sqlReadOnBlockSize.setEnabled(false);
            _lblReadOnWarning.setEnabled(false);


            if(null != evt && evt.getSource() == _sqlReadOnChk)
            {
               if(0 < _sqlNbrRowsToShowField.getInt())
               {
                  _sqlLimitRowsChk.setSelected(true);
               }
            }

            _sqlLimitRowsChk.setEnabled(true);
            _sqlNbrRowsToShowField.setEnabled(_sqlLimitRowsChk.isSelected());

         }
         _sqlFetchSizeField.setEnabled(_sqlUseFetchSizeChk.isSelected());

      }
      finally
      {
         _inUpdateControlStatus = false;
      }

   }


   public JPanel createResultLimitAndReadOnPanel()
   {

      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;


      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5),0,0);
      ret.add(_sqlLimitRowsChk, gbc);

      gbc = new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(_sqlNbrRowsToShowField, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("SessionSQLPropertiesPanel.rows")), gbc);



      gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,3,5),0,0);
      ret.add(_sqlReadOnChk, gbc);

      gbc = new GridBagConstraints(1,1,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(_sqlReadOnBlockSize, gbc);

      gbc = new GridBagConstraints(2,1,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("SessionSQLPropertiesPanel.rowsPerBlock")), gbc);



      gbc = new GridBagConstraints(0,2,3,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5),0,0);
      ret.add(_lblReadOnWarning, gbc);


      gbc = new GridBagConstraints(0,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20,5,5,5),0,0);
      ret.add(_sqlUseFetchSizeChk, gbc);

      gbc = new GridBagConstraints(1,3,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(20,5,5,5),0,0);
      ret.add(_sqlFetchSizeField, gbc);

      gbc = new GridBagConstraints(2,3,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(20,5,5,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("SessionSQLPropertiesPanel.rows")), gbc);


      ret.setBorder(BorderFactory.createTitledBorder(s_stringMgr.getString("SessionSQLPropertiesPanel.sqlResultLoading")));
      return ret;
   }
}
