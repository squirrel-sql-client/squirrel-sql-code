package net.sourceforge.squirrel_sql.plugins.hibernate.completion;


import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.ICompletorModel;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.plugins.hibernate.IHibernateConnectionProvider;
import net.sourceforge.squirrel_sql.plugins.hibernate.ConnectionListener;
import net.sourceforge.squirrel_sql.plugins.hibernate.HqlSyntaxHighlightTokenMatcherProxy;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.mapping.MappedClassInfo;

import java.util.ArrayList;


public class HQLCodeCompletorModel implements ICompletorModel
{

   private HQLCompletionInfoCollection _codeCompletionInfos;
   private IHibernateConnectionProvider _hibernateConnectionProvider;
   private HQLAliasFinder _hqlAliasFinder;
   private HqlSyntaxHighlightTokenMatcherProxy _hqlSyntaxHighlightTokenMatcherProxy;

   public HQLCodeCompletorModel(IHibernateConnectionProvider hibernateConnectionProvider, HQLAliasFinder hqlAliasFinder, HqlSyntaxHighlightTokenMatcherProxy hqlSyntaxHighlightTokenMatcherProxy)
   {
      _hibernateConnectionProvider = hibernateConnectionProvider;
      _hqlAliasFinder = hqlAliasFinder;
      _hqlSyntaxHighlightTokenMatcherProxy = hqlSyntaxHighlightTokenMatcherProxy;

      _hibernateConnectionProvider.addConnectionListener(new ConnectionListener()
      {
         public void connectionOpened(HibernateConnection con)
         {
            init();
         }

         public void connectionClosed()
         {
            _hqlAliasFinder.stop();
            _codeCompletionInfos = null;
            _hqlSyntaxHighlightTokenMatcherProxy.setDelegate(null);

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

         MappingInfoProvider mappingInfoProvider = new MappingInfoProvider()
         {
            public MappedClassInfo getMappedClassInfoFor(String token)
            {
               return onGetMappedClassInfoFor(token);
            }

            public boolean mayBeClassOrAliasName(String token)
            {
               return onMayBeClassOrAliasName(token);
            }
         };


         AliasFinderListener aliasFinderListener = new AliasFinderListener()
         {

            public void aliasesFound(ArrayList<AliasInfo> aliasInfos)
            {
               onAliasesFound(aliasInfos);
            }
         };
         
         _hqlAliasFinder.start(mappingInfoProvider, aliasFinderListener);

         _hqlSyntaxHighlightTokenMatcherProxy.setDelegate(_codeCompletionInfos.getHqlSyntaxHighlightTokenMatcher());
      }
   }

   private boolean onMayBeClassOrAliasName(String token)
   {
      return _codeCompletionInfos.mayBeClassOrAliasName(token);
   }

   private void onAliasesFound(ArrayList<AliasInfo> aliasInfos)
   {
      _codeCompletionInfos.setCurrentAliasInfos(aliasInfos);
   }

   private MappedClassInfo onGetMappedClassInfoFor(String token)
   {
      return _codeCompletionInfos.getMappedClassInfo(token);
   }

}
