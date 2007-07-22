package net.sourceforge.squirrel_sql.plugins.hibernate.completion;


import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.ICompletorModel;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.plugins.hibernate.IHibernateConnectionProvider;
import net.sourceforge.squirrel_sql.plugins.hibernate.ConnectionListener;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

import java.util.ArrayList;


public class HQLCodeCompletorModel implements ICompletorModel
{

   private HQLCompletionInfoCollection _codeCompletionInfos;
   private IHibernateConnectionProvider _hibernateConnectionProvider;
   private HQLAliasFinder _hqlAliasFinder;

   public HQLCodeCompletorModel(IHibernateConnectionProvider hibernateConnectionProvider, HQLAliasFinder hqlAliasFinder)
   {
      _hibernateConnectionProvider = hibernateConnectionProvider;
      _hqlAliasFinder = hqlAliasFinder;

      _hibernateConnectionProvider.addConnectionListener(new ConnectionListener()
      {
         public void connectionClosed()
         {
            _hqlAliasFinder.stop();
            _codeCompletionInfos = null;
         }
      });
   }


   public CompletionCandidates getCompletionCandidates(String textTillCarret)
   {
      init();

      CompletionParser parser = new CompletionParser(textTillCarret, true);
      return _codeCompletionInfos.getInfosStartingWith(parser);

   }

   private void init()
   {
      if(null == _codeCompletionInfos)
      {
         _codeCompletionInfos = new HQLCompletionInfoCollection(_hibernateConnectionProvider.getHibernateConnection());

         _hqlAliasFinder.setMappingInfoProvider(new MappingInfoProvider(){
            public MappedClassInfo getMappedClassInfoFor(String token)
            {
               return onGetMappedClassInfoFor(token);
            }
         });

         _hqlAliasFinder.setAliasFinderListener(new AliasFinderListener(){

            public void aliasesFound(ArrayList<AliasInfo> aliasInfos)
            {
               onAliasesFound(aliasInfos);
            }
         });
      }
   }

   private void onAliasesFound(ArrayList<AliasInfo> aliasInfos)
   {
      _codeCompletionInfos.setCurrentAliasInfos(aliasInfos);
   }

   private MappedClassInfo onGetMappedClassInfoFor(String className)
   {
      return _codeCompletionInfos.getMappedClassInfo(className);
   }

}
