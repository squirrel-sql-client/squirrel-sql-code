package net.sourceforge.squirrel_sql.plugins.hibernate.completion;


import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.ICompletorModel;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.util.CompletionParser;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.IHibernateConnectionProvider;

import java.util.ArrayList;


public class HQLCodeCompletorModel implements ICompletorModel
{

   private HQLCompletionInfoCollection _codeCompletionInfos;
   private IHibernateConnectionProvider _hibernateConnectionProvider;

   public HQLCodeCompletorModel(IHibernateConnectionProvider hibernateConnectionProvider)
   {
      _hibernateConnectionProvider = hibernateConnectionProvider;
   }


   public CompletionCandidates getCompletionCandidates(String textTillCarret)
   {
      init();


      CompletionParser parser = new CompletionParser(textTillCarret, true);
      ArrayList<CompletionInfo> cis = _codeCompletionInfos.getInfosStartingWith(parser);
      return new CompletionCandidates(cis.toArray(new CompletionInfo[cis.size()]), parser.getReplacementStart(), parser.getStringToReplace());

   }

   private void init()
   {
      if(null == _codeCompletionInfos)
      {
         _codeCompletionInfos = new HQLCompletionInfoCollection(_hibernateConnectionProvider.getHibernateConnection());
      }
   }

}
