package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HQLAliasFinder
{
   private static ILogger s_log = LoggerController.createLogger(HQLAliasFinder.class);

   private MappingInfoProvider _mappingInfoProvider;
   private ISQLEntryPanel _hqlEntryPanel;
   private AliasFinderListener _aliasFinderListener;
   private Timer _timer;

   private HqlAliasParser _hqlAliasParser = new HqlAliasParser();

   public HQLAliasFinder(ISQLEntryPanel hqlEntryPanel)
   {
      _hqlEntryPanel = hqlEntryPanel;
   }


   public void start(MappingInfoProvider mappingInfoProvider, AliasFinderListener aliasFinderListener)
   {
      if (null == _timer)
      {
         _mappingInfoProvider = mappingInfoProvider;
         _aliasFinderListener = aliasFinderListener;

         // One synchron call for first completion
         findAliases();
         start();
      }
   }


   private void start()
   {
      _timer = new Timer();

      _timer.schedule(new TimerTask()
      {
         public void run()
         {
            findAliases();
         }
      }, 1000, 1000);
   }

   public void stop()
   {
      if (null != _timer)
      {
         _timer.cancel();
         _timer.purge();
         _timer = null;
      }
   }

   private void findAliases()
   {
      try
      {
         String hql = _hqlEntryPanel.getText();

         final ArrayList<AliasInfo> infos = parse(hql);

         GUIUtils.processOnSwingEventThread(new Runnable()
         {
            public void run()
            {
               _aliasFinderListener.aliasesFound(infos);
            }
         });
      }
      catch (Throwable t)
      {
         s_log.error("Error in HQLAliasFinder:", t);
      }
   }

   private ArrayList<AliasInfo> parse(String hql)
   {
      return _hqlAliasParser.parse(hql, _mappingInfoProvider);
   }
}
