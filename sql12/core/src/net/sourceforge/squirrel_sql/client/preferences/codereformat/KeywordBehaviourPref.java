package net.sourceforge.squirrel_sql.client.preferences.codereformat;

public class KeywordBehaviourPref
{
   private String _keyWord;
   private int _keywordBehaviourId;

   public KeywordBehaviourPref()
   {
   }

   public KeywordBehaviourPref(String keyWord, int keywordBehaviourId)
   {
      _keyWord = keyWord;
      _keywordBehaviourId = keywordBehaviourId;
   }

   public String getKeyWord()
   {
      return _keyWord;
   }

   public void setKeywordBehaviourId(int keywordBehaviourId)
   {
      _keywordBehaviourId = keywordBehaviourId;
   }

   public int getKeywordBehaviourId()
   {
      return _keywordBehaviourId;
   }

   public void setKeyWord(String keyWord)
   {
      _keyWord = keyWord;
   }
}
