package net.sourceforge.squirrel_sql.plugins.hibernate.completion;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;
import net.sourceforge.squirrel_sql.fw.completion.ICompletorModel;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernatePlugin;

public class HQLCodeCompletorModel implements ICompletorModel
{
   public HQLCodeCompletorModel(ISession session, HibernatePlugin plugin, HQLCompletionInfos codeCompletionInfos, IIdentifier sqlEntryIdentifier)
   {
      //To change body of created methods use File | Settings | File Templates.
   }


   public CompletionCandidates getCompletionCandidates(String textTillCarret)
   {
      return new CompletionCandidates(
         new CompletionInfo[]
            {
               new HQLCompletionInfo("Gerd"),
               new HQLCompletionInfo("Sara"),
               new HQLCompletionInfo("Anna"),
            },

         textTillCarret.length(),
         ""
      );
   }
}
