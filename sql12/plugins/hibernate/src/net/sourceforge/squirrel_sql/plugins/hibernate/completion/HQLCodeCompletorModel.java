package net.sourceforge.squirrel_sql.plugins.hibernate.completion;


import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.ICompletorModel;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateChannel;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.ConnectionListener;
import net.sourceforge.squirrel_sql.plugins.hibernate.HqlSyntaxHighlightTokenMatcherProxy;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.HibernateConfiguration;

import java.util.ArrayList;


public class HQLCodeCompletorModel implements ICompletorModel
{

   private HQLCompletionInfoCollection _codeCompletionInfos;
   private HibernateChannel _hibernateChannel;
   private HQLAliasFinder _hqlAliasFinder;
   private HqlSyntaxHighlightTokenMatcherProxy _hqlSyntaxHighlightTokenMatcherProxy;

   public HQLCodeCompletorModel(HibernateChannel hibernateChannel, HQLAliasFinder hqlAliasFinder, HqlSyntaxHighlightTokenMatcherProxy hqlSyntaxHighlightTokenMatcherProxy)
   {
      _hibernateChannel = hibernateChannel;
      _hqlAliasFinder = hqlAliasFinder;
      _hqlSyntaxHighlightTokenMatcherProxy = hqlSyntaxHighlightTokenMatcherProxy;

      _hibernateChannel.addConnectionListener(new ConnectionListener()
      {
         public void connectionOpened(HibernateConnection con, HibernateConfiguration cfg)
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
         _codeCompletionInfos = new HQLCompletionInfoCollection(_hibernateChannel.getHibernateConnection());



         AliasFinderListener aliasFinderListener = new AliasFinderListener()
         {

            public void aliasesFound(ArrayList<AliasInfo> aliasInfos)
            {
               onAliasesFound(aliasInfos);
            }
         };
         
         _hqlAliasFinder.start(_codeCompletionInfos, aliasFinderListener);

         _hqlSyntaxHighlightTokenMatcherProxy.setDelegate(_codeCompletionInfos.getHqlSyntaxHighlightTokenMatcher());
      }
   }

   private void onAliasesFound(ArrayList<AliasInfo> aliasInfos)
   {
      if (null != _codeCompletionInfos)
      {
         _codeCompletionInfos.setCurrentAliasInfos(aliasInfos);
      }
   }

}
