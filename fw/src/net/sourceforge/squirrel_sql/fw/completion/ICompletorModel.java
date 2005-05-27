package net.sourceforge.squirrel_sql.fw.completion;

public interface ICompletorModel
{
   /**
    *
    * @param textTillCarret Is only the text till carret if editor and filter are the same.
    * If there is an extra filter text field the complete text in this text field is passed.
    *
    * @return
    */
   public CompletionCandidates getCompletionCandidates(String textTillCarret);
}
