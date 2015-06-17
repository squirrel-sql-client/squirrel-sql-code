package net.sourceforge.squirrel_sql.client.session.mainpanel.rotatedtable;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RotatedTableInitializer
{
   private final ISession _session;
   private final JTabbedPane _tabResultTabs;
   private ResultSetDataSet _rsds;

   private Integer _rotatedTableTabIndex = null;
   private RotatedTableCtrl _rotatedTableCtrl;
   private boolean _isInitialized = false;


   public RotatedTableInitializer(ISession session, JTabbedPane tabResultTabs)
   {
      _session = session;
      _tabResultTabs = tabResultTabs;

      _tabResultTabs.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            onStateChanged();
         }
      });

   }

   private void onStateChanged()
   {
      if(_isInitialized || null == _rotatedTableTabIndex || null == _tabResultTabs || null == _rsds)
      {
         return;
      }

      if (_rotatedTableTabIndex == _tabResultTabs.getSelectedIndex())
      {
         _rotatedTableCtrl.init(_rsds);
         _isInitialized = true;
      }
   }

   public void initRotatedTable()
   {
      if(null != _rotatedTableTabIndex && _rotatedTableTabIndex < _tabResultTabs.getTabCount() && RotatedTableCtrl.isRotatedTablePanel(_tabResultTabs.getComponentAt(_rotatedTableTabIndex)))
      {
         _tabResultTabs.removeTabAt(_rotatedTableTabIndex);
      }

      if (null == _rotatedTableTabIndex)
      {
         _rotatedTableTabIndex = _tabResultTabs.getTabCount();
      }


      _rotatedTableCtrl = new RotatedTableCtrl(_session);
      _tabResultTabs.insertTab(_rotatedTableCtrl.getTitle(), null, _rotatedTableCtrl.getPanel(), null, _rotatedTableTabIndex);
   }

   public void setCurrentResult(ResultSetDataSet rsds)
   {
      _rsds = rsds;
      _isInitialized = false;
   }

   public void moreResultsHaveBeenRead()
   {
      initRotatedTable();
      setCurrentResult(_rsds);
   }
}
