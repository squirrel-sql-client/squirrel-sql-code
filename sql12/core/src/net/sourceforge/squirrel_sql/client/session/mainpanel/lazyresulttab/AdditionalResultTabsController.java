package net.sourceforge.squirrel_sql.client.session.mainpanel.lazyresulttab;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.OverviewCtrl;
import net.sourceforge.squirrel_sql.client.session.mainpanel.rotatedtable.RotatedTableCtrl;
import net.sourceforge.squirrel_sql.client.session.mainpanel.textresult.TextResultCtrl;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.DataSetViewerFindHandler;

import javax.swing.*;
import java.awt.*;

public class AdditionalResultTabsController
{
   private final ISession _session;
   private final JTabbedPane _tabResultTabs;
   private final int _additionalResultTabsStartTabIndex;
   private boolean _isOutputAsTable;


   private LazyResultTabInitializer<OverviewCtrl> _overviewInitializer;
   private LazyResultTabInitializer<RotatedTableCtrl> _rotatedTableInitializer;
   private LazyResultTabInitializer<TextResultCtrl> _textResultController;

   public AdditionalResultTabsController(ISession session, JTabbedPane tabResultTabs, boolean isOutputAsTable)
   {
      _session = session;
      _tabResultTabs = tabResultTabs;
      _isOutputAsTable = isOutputAsTable;

      _additionalResultTabsStartTabIndex = tabResultTabs.getTabCount();

      _overviewInitializer = new LazyResultTabInitializer<>(_tabResultTabs, new LazyResultTabControllerFactory<OverviewCtrl>()
      {
         @Override
         public OverviewCtrl create()
         {
            return new OverviewCtrl(_session);
         }

         @Override
         public boolean isMatchingPanel(Component comp)
         {
            return OverviewCtrl.isOverviewPanel(comp);
         }
      });
      _overviewInitializer.initTab();



      _rotatedTableInitializer = new LazyResultTabInitializer<>(_tabResultTabs, new LazyResultTabControllerFactory<RotatedTableCtrl>()
      {
         @Override
         public RotatedTableCtrl create()
         {
            return new RotatedTableCtrl(_session);
         }

         @Override
         public boolean isMatchingPanel(Component comp)
         {
            return RotatedTableCtrl.isRotatedTablePanel(comp);
         }
      });
      _rotatedTableInitializer.initTab();

      if (_isOutputAsTable)
      {
         _textResultController = new LazyResultTabInitializer<>(_tabResultTabs, new LazyResultTabControllerFactory<TextResultCtrl>()
         {
            @Override
            public TextResultCtrl create()
            {
               return new TextResultCtrl();
            }

            @Override
            public boolean isMatchingPanel(Component comp)
            {
               return TextResultCtrl.isTextResultPanel(comp);
            }
         });
         _textResultController.initTab();
      }


   }

   public void setCurrentResult(ResultSetDataSet rsds)
   {
      _overviewInitializer.setCurrentResult(rsds);
      _rotatedTableInitializer.setCurrentResult(rsds);

      if (_isOutputAsTable)
      {
         _textResultController.setCurrentResult(rsds);
      }
   }

   public void moreResultsHaveBeenRead()
   {
      _overviewInitializer.moreResultsHaveBeenRead();
      _rotatedTableInitializer.moreResultsHaveBeenRead();

      if (_isOutputAsTable)
      {
         _textResultController.moreResultsHaveBeenRead();
      }
   }

   public DataSetViewerFindHandler getDataSetViewerFindHandlerOfSelectedTabOrNull()
   {
      // _additionalResultTabsStartTabIndex --> Overview

      // _additionalResultTabsStartTabIndex + 1 --> Rotated table
      if(_additionalResultTabsStartTabIndex + 1 == _tabResultTabs.getSelectedIndex())
      {
         return _rotatedTableInitializer.getController().getDataSetViewerFindHandler();
      }

      return null;
   }
}
