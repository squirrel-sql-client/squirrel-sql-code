package net.sourceforge.squirrel_sql.plugins.hibernate.completion;


import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.ICompletorModel;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.IHibernateConnectionProvider;
import net.sourceforge.squirrel_sql.plugins.hibernate.ConnectionListener;

import java.util.ArrayList;


public class HQLCodeCompletorModel implements ICompletorModel
{

   private HQLCompletionInfoCollection _codeCompletionInfos;
   private IHibernateConnectionProvider _hibernateConnectionProvider;

   public HQLCodeCompletorModel(IHibernateConnectionProvider hibernateConnectionProvider)
   {
      _hibernateConnectionProvider = hibernateConnectionProvider;

      _hibernateConnectionProvider.addConnectionListener(new ConnectionListener()
      {
         public void connectionClosed()
         {
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
      }
   }

}
