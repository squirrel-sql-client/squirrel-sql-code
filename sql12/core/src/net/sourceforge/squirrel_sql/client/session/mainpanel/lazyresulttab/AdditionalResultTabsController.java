package net.sourceforge.squirrel_sql.client.session.mainpanel.lazyresulttab;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.OverviewCtrl;
import net.sourceforge.squirrel_sql.client.session.mainpanel.rotatedtable.RotatedTableCtrl;
import net.sourceforge.squirrel_sql.client.session.mainpanel.textresult.TextResultCtrl;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;

import javax.swing.*;
import java.awt.*;

public class AdditionalResultTabsController
{
   private final ISession _session;
   private final JTabbedPane _tabResultTabs;


   private LazyResultTabInitializer<OverviewCtrl> _overviewInitializer;
   private LazyResultTabInitializer<RotatedTableCtrl> _rotatedTableInitializer;
   private LazyResultTabInitializer<TextResultCtrl> _textResultController;

   public AdditionalResultTabsController(ISession session, JTabbedPane tabResultTabs)
   {
      _session = session;
      _tabResultTabs = tabResultTabs;

      _overviewInitializer = new LazyResultTabInitializer<OverviewCtrl>(_session, _tabResultTabs, new LazyResultTabControllerFactory<OverviewCtrl>()
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



      _rotatedTableInitializer = new LazyResultTabInitializer<RotatedTableCtrl>(_session, _tabResultTabs, new LazyResultTabControllerFactory<RotatedTableCtrl>()
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

      _textResultController = new LazyResultTabInitializer<TextResultCtrl>(_session, _tabResultTabs, new LazyResultTabControllerFactory<TextResultCtrl>()
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

   public void setCurrentResult(ResultSetDataSet rsds)
   {
      _overviewInitializer.setCurrentResult(rsds);
      _rotatedTableInitializer.setCurrentResult(rsds);
      _textResultController.setCurrentResult(rsds);
   }

   public void moreResultsHaveBeenRead()
   {
      _overviewInitializer.moreResultsHaveBeenRead();
      _rotatedTableInitializer.moreResultsHaveBeenRead();
      _textResultController.moreResultsHaveBeenRead();
   }
}
