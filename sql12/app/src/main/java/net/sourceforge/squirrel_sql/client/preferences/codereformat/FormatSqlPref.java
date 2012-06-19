package net.sourceforge.squirrel_sql.client.preferences.codereformat;

public class FormatSqlPref
{
   public static final String JOIN_DISPLAY_STRING = "INNER/LEFT/RIGHT JOIN";

   public static final String INSERT = "INSERT";
   public static final String VALUES = "VALUES";


   private KeywordBehaviourPref[] _keywordBehaviourPrefs = new KeywordBehaviourPref[]
   {
      new KeywordBehaviourPref("SELECT", FormatSqlPanel.KeywordBehaviour.ALONE_IN_LINE.getID()),
      new KeywordBehaviourPref("UNION", FormatSqlPanel.KeywordBehaviour.ALONE_IN_LINE.getID()),
      new KeywordBehaviourPref("FROM", FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),

      new KeywordBehaviourPref(JOIN_DISPLAY_STRING, FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),

      new KeywordBehaviourPref("WHERE", FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),
      new KeywordBehaviourPref("AND", FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),
      new KeywordBehaviourPref("OR", FormatSqlPanel.KeywordBehaviour.NO_INFLUENCE_ON_NEW_LINE.getID()),
      new KeywordBehaviourPref("GROUP", FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),
      new KeywordBehaviourPref("ORDER", FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),

      new KeywordBehaviourPref("UPDATE", FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),
      new KeywordBehaviourPref("DELETE", FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),

      new KeywordBehaviourPref(INSERT, FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),
      new KeywordBehaviourPref(VALUES, FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID())
   };


   private int _indent = 3;
   private int _preferedLineLength = 80;
   private boolean _doInsertValuesAlign = true;

   public KeywordBehaviourPref[] getKeywordBehaviourPrefs()
   {
      return _keywordBehaviourPrefs;
   }

   public void setIndent(int indent)
   {
      _indent = indent;
   }


   public void setPreferedLineLength(int preferedLineLength)
   {
      _preferedLineLength = preferedLineLength;
   }

   public void setKeywordBehaviourPrefs(KeywordBehaviourPref[] keywordBehaviourPrefs)
   {
      _keywordBehaviourPrefs = keywordBehaviourPrefs;
   }

   public int getIndent()
   {
      return _indent;
   }

   public int getPreferedLineLength()
   {
      return _preferedLineLength;
   }

   public boolean isDoInsertValuesAlign()
   {
      return _doInsertValuesAlign;
   }

   public void setDoInsertValuesAlign(boolean doInsertValuesAlign)
   {
      _doInsertValuesAlign = doInsertValuesAlign;
   }
}
