package net.sourceforge.squirrel_sql.client.session.mainpanel.lazyresulttab;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class LazyResultTabInitializer <T extends LazyTabControllerCtrl>
{
   private final ISession _session;
   private final JTabbedPane _tabResultTabs;
   private Integer tabIndex = null;
   private ResultSetDataSet _rsds;
   private T _lazyTabController;
   private boolean _isInitialized = false;


   private LazyResultTabControllerFactory _lazyResultTabControllerFactory;


   public LazyResultTabInitializer(ISession session, JTabbedPane tabResultTabs, LazyResultTabControllerFactory<T> lazyResultTabControllerFactory)
   {
      _session = session;
      _tabResultTabs = tabResultTabs;
      _lazyResultTabControllerFactory = lazyResultTabControllerFactory;

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
      if(_isInitialized || null == tabIndex || null == _tabResultTabs || null == _rsds)
      {
         return;
      }

      if (tabIndex == _tabResultTabs.getSelectedIndex())
      {
         _lazyTabController.init(_rsds);
         _isInitialized = true;
      }
   }

   public void initTab()
   {
      if(null != tabIndex && tabIndex < _tabResultTabs.getTabCount() && _lazyResultTabControllerFactory.isMatchingPanel(_tabResultTabs.getComponentAt(tabIndex)))
      {
         _tabResultTabs.removeTabAt(tabIndex);
      }

      if (null == tabIndex)
      {
         tabIndex = _tabResultTabs.getTabCount();
      }
      _lazyTabController = (T) _lazyResultTabControllerFactory.create();
      _tabResultTabs.insertTab(_lazyTabController.getTitle(), null, _lazyTabController.getPanel(), null, tabIndex);

   }

   public void setCurrentResult(ResultSetDataSet rsds)
   {
      _rsds = rsds;
      _isInitialized = false;
   }

   public void moreResultsHaveBeenRead()
   {
      initTab();
      setCurrentResult(_rsds);
   }
}
