package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.OverviewCtrl;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OverviewInitializer
{
   private final ISession _session;
   private final JTabbedPane _tabResultTabs;
   private Integer _overviewTabIndex = null;
   private ResultSetDataSet _rsds;
   private OverviewCtrl _overviewCtrl;
   private boolean _isInitialized = false;

   public OverviewInitializer(ISession session, JTabbedPane tabResultTabs)
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
      if(_isInitialized || null == _overviewTabIndex || null == _tabResultTabs || null == _rsds)
      {
         return;
      }

      if (_overviewTabIndex == _tabResultTabs.getSelectedIndex())
      {
         _overviewCtrl.init(_rsds);
         _isInitialized = true;
      }
   }

   public void setCurrentResult(ResultSetDataSet rsds)
   {
      _rsds = rsds;
      _isInitialized = false;
   }


   public void initOverview()
   {
      if(null != _overviewTabIndex && _overviewTabIndex < _tabResultTabs.getTabCount() && OverviewCtrl.isOverviewPanel(_tabResultTabs.getComponentAt(_overviewTabIndex)))
      {
         _tabResultTabs.removeTabAt(_overviewTabIndex);
      }

      if (null == _overviewTabIndex)
      {
         _overviewTabIndex = _tabResultTabs.getTabCount();
      }
      _overviewCtrl = new OverviewCtrl(_session);
      _tabResultTabs.addTab(_overviewCtrl.getTitle(), _overviewCtrl.getPanel());
      _tabResultTabs.insertTab(_overviewCtrl.getTitle(), null, _overviewCtrl.getPanel(), null, _overviewTabIndex);

   }

   public void moreResultsHaveBeenRead()
   {
      initOverview();
   }
}
