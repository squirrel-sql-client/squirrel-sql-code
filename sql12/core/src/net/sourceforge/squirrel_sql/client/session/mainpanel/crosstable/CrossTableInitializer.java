package net.sourceforge.squirrel_sql.client.session.mainpanel.crosstable;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CrossTableInitializer
{
   private final ISession _session;
   private final JTabbedPane _tabResultTabs;
   private ResultSetDataSet _rsds;

   private Integer _crossTableTabIndex = null;
   private CrossTableCtrl _crossTableCtrl;
   private boolean _isInitialized = false;


   public CrossTableInitializer(ISession session, JTabbedPane tabResultTabs)
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
      if(_isInitialized || null == _crossTableTabIndex || null == _tabResultTabs || null == _rsds)
      {
         return;
      }

      if (_crossTableTabIndex == _tabResultTabs.getSelectedIndex())
      {
         _crossTableCtrl.init(_rsds);
         _isInitialized = true;
      }
   }

   public void initCrossTable()
   {
      if(null != _crossTableTabIndex && _crossTableTabIndex < _tabResultTabs.getTabCount() && CrossTableCtrl.isCrossTablePanel(_tabResultTabs.getComponentAt(_crossTableTabIndex)))
      {
         _tabResultTabs.removeTabAt(_crossTableTabIndex);
      }

      if (null == _crossTableTabIndex)
      {
         _crossTableTabIndex = _tabResultTabs.getTabCount();
      }


      _crossTableCtrl = new CrossTableCtrl(_session);
      _tabResultTabs.insertTab(_crossTableCtrl.getTitle(), null, _crossTableCtrl.getPanel(), null, _crossTableTabIndex);
   }

   public void setCurrentResult(ResultSetDataSet rsds)
   {
      _rsds = rsds;
      _isInitialized = false;
   }
}
