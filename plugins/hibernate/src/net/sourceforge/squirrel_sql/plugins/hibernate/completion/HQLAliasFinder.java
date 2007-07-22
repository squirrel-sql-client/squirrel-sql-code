package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

public class HQLAliasFinder
{
   private MappingInfoProvider _mappingInfoProvider;
   private ISQLEntryPanel _hqlEntryPanel;
   private AliasFinderListener _aliasFinderListener;
   private Timer _timer;

   public HQLAliasFinder(ISQLEntryPanel hqlEntryPanel)
   {
      _hqlEntryPanel = hqlEntryPanel;
   }


   public void setMappingInfoProvider(MappingInfoProvider mappingInfoProvider)
   {
      _mappingInfoProvider = mappingInfoProvider;
      init();
   }


   public void setAliasFinderListener(AliasFinderListener aliasFinderListener)
   {
      _aliasFinderListener = aliasFinderListener;
      init();
   }

   private void init()
   {
      if(null == _mappingInfoProvider || null == _aliasFinderListener)
      {
         return;
      }


      // One synchron call for first completion
      findAliases();
      start();
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
      _timer.cancel();
      _timer.purge();
   }

   private void findAliases()
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

   private ArrayList<AliasInfo> parse(String hql)
   {
      ArrayList<AliasInfo> ret = new ArrayList<AliasInfo>();

      // Just a dummy
      if(-1 < hql.indexOf("Kv auftrag"))
      {
         MappedClassInfo mci = _mappingInfoProvider.getMappedClassInfoFor("Kv");
         ret.add(new AliasInfo(mci, "auftrag"));
      }

      return ret;
   }

}
