package net.sourceforge.squirrel_sql.client.preferences.codereformat;

import net.sourceforge.squirrel_sql.client.util.codereformat.ColumnListSpiltMode;

public class FormatSqlPref
{
   public static final String JOIN_DISPLAY_STRING = "INNER/LEFT/RIGHT JOIN";

   public static final String INSERT = "INSERT";
   public static final String VALUES = "VALUES";
   public static final String FROM = "FROM";
   public static final String WHERE = "WHERE";
   public static final String GROUP = "GROUP";
   public static final String ORDER = "ORDER";
   public static final String UNION = "UNION";
   public static final String AND = "AND";
   public static final String OR = "OR";
   public static final String SELECT = "SELECT";


   private KeywordBehaviourPref[] _keywordBehaviourPrefs = new KeywordBehaviourPref[]
   {
      new KeywordBehaviourPref(SELECT, FormatSqlPanel.KeywordBehaviour.ALONE_IN_LINE.getID()),
      new KeywordBehaviourPref(UNION, FormatSqlPanel.KeywordBehaviour.ALONE_IN_LINE.getID()),
      new KeywordBehaviourPref(FROM, FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),

      new KeywordBehaviourPref(JOIN_DISPLAY_STRING, FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),

      new KeywordBehaviourPref(WHERE, FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),
      new KeywordBehaviourPref(AND, FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),
      new KeywordBehaviourPref(OR, FormatSqlPanel.KeywordBehaviour.NO_INFLUENCE_ON_NEW_LINE.getID()),
      new KeywordBehaviourPref(GROUP, FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),
      new KeywordBehaviourPref(ORDER, FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),

      new KeywordBehaviourPref("UPDATE", FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),
      new KeywordBehaviourPref("DELETE", FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),

      new KeywordBehaviourPref(INSERT, FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID()),
      new KeywordBehaviourPref(VALUES, FormatSqlPanel.KeywordBehaviour.START_NEW_LINE.getID())
   };

   private boolean _useVerticalBlankFormatter = false;

   private int _indent = 3;
   private int _preferedLineLength = 80;
   private boolean _doInsertValuesAlign = true;

   private boolean _indentSections = false;
   private boolean _lineBreakFor_AND_OR_in_FROM_clause = false;
   private boolean _commasAtLineBegin = false;
   private String _columnListSplitMode = ColumnListSpiltMode.ALLOW_SPLIT.name();


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

   public boolean isIndentSections()
   {
      return _indentSections;
   }

   public void setIndentSections(boolean indentSections)
   {
      _indentSections = indentSections;
   }

   public boolean isLineBreakFor_AND_OR_in_FROM_clause()
   {
      return _lineBreakFor_AND_OR_in_FROM_clause;
   }

   public void setLineBreakFor_AND_OR_in_FROM_clause(boolean lineBreakFor_AND_OR_in_FROM_clause)
   {
      _lineBreakFor_AND_OR_in_FROM_clause = lineBreakFor_AND_OR_in_FROM_clause;
   }

   public boolean isCommasAtLineBegin()
   {
      return _commasAtLineBegin;
   }

   public void setCommasAtLineBegin(boolean commasAtLineBegin)
   {
      _commasAtLineBegin = commasAtLineBegin;
   }

   public String getColumnListSplitMode()
   {
      return _columnListSplitMode;
   }

   public void setColumnListSplitMode(String columnListSplitMode)
   {
      _columnListSplitMode = columnListSplitMode;
   }

   public boolean isUseVerticalBlankFormatter()
   {
      return _useVerticalBlankFormatter;
   }

   public void setUseVerticalBlankFormatter(boolean useVerticalBlankFormatter)
   {
      _useVerticalBlankFormatter = useVerticalBlankFormatter;
   }
}
