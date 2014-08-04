package net.sourceforge.squirrel_sql.client.session.mainpanel;

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

   public OverviewInitializer(ISession session, JTabbedPane tabResultTabs)
   {
      _session = session;
      _tabResultTabs = tabResultTabs;
   }

   public void setCurrentResult(ResultSetDataSet rsds)
   {
      _rsds = rsds;
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
      final OverviewCtrl ctrl = new OverviewCtrl(_session);
      _tabResultTabs.addTab(ctrl.getTitle(), ctrl.getPanel());

      _tabResultTabs.addChangeListener(new ChangeListener()
      {
         @Override
         public void stateChanged(ChangeEvent e)
         {
            if (_overviewTabIndex == _tabResultTabs.getSelectedIndex())
            {
               ctrl.init(_rsds);
            }
         }
      });
   }

}
