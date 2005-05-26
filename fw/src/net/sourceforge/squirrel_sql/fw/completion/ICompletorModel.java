package net.sourceforge.squirrel_sql.fw.completion;

public interface ICompletorModel
{
   public CompletionCandidates getCompletionCandidates(String textTillCarret);
}
