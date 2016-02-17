package org.squirrelsql.settings;

import java.util.HashMap;

public class SQLFormatSettings
{
   private int _indentSpaceCount = 3;
   private int _preferedLineLength = 80;
   private HashMap<SQLKeyWord, KeyWordBehavior> _behaviorsByKeyWord = new HashMap<>();
   private boolean _alignmInsertValues = true;

   private boolean _indentSections;
   private boolean _commaAtLineBegin;
   private boolean _lineBreakFor_AND_OR_in_FROM_clause = true;


   public SQLFormatSettings()
   {
      _behaviorsByKeyWord.put(SQLKeyWord.SELECT, KeyWordBehavior.ALONE_IN_LINE);
      _behaviorsByKeyWord.put(SQLKeyWord.UNION, KeyWordBehavior.ALONE_IN_LINE);
      _behaviorsByKeyWord.put(SQLKeyWord.FROM, KeyWordBehavior.START_NEW_LINE);
      _behaviorsByKeyWord.put(SQLKeyWord.INNER_LEFT_RIGHT_JOIN, KeyWordBehavior.START_NEW_LINE);
      _behaviorsByKeyWord.put(SQLKeyWord.WHERE, KeyWordBehavior.START_NEW_LINE);
      _behaviorsByKeyWord.put(SQLKeyWord.AND, KeyWordBehavior.START_NEW_LINE);
      _behaviorsByKeyWord.put(SQLKeyWord.OR, KeyWordBehavior.NO_INFLUENCE_ON_LINEBREAK);
      _behaviorsByKeyWord.put(SQLKeyWord.GROUP, KeyWordBehavior.START_NEW_LINE);
      _behaviorsByKeyWord.put(SQLKeyWord.ORDER, KeyWordBehavior.START_NEW_LINE);
      _behaviorsByKeyWord.put(SQLKeyWord.UPDATE, KeyWordBehavior.START_NEW_LINE);
      _behaviorsByKeyWord.put(SQLKeyWord.DELETE, KeyWordBehavior.START_NEW_LINE);
      _behaviorsByKeyWord.put(SQLKeyWord.INSERT, KeyWordBehavior.START_NEW_LINE);
      _behaviorsByKeyWord.put(SQLKeyWord.VALUES, KeyWordBehavior.START_NEW_LINE);
      
   }

   public int getIndentSpaceCount()
   {
      return _indentSpaceCount;
   }

   public void setIndentSpaceCount(int indentSpaceCount)
   {
      _indentSpaceCount = indentSpaceCount;
   }

   public int getPreferedLineLength()
   {
      return _preferedLineLength;
   }

   public void setPreferedLineLength(int preferedLineLength)
   {
      _preferedLineLength = preferedLineLength;
   }

   public HashMap<SQLKeyWord, KeyWordBehavior> getBehaviorsByKeyWord()
   {
      return _behaviorsByKeyWord;
   }

   public void setBehaviorsByKeyWord(HashMap<SQLKeyWord, KeyWordBehavior> behaviorsByKeyWord)
   {
      _behaviorsByKeyWord = behaviorsByKeyWord;
   }

   public boolean isAlignmInsertValues()
   {
      return _alignmInsertValues;
   }

   public void setAlignmInsertValues(boolean alignmInsertValues)
   {
      _alignmInsertValues = alignmInsertValues;
   }

   public boolean isCommaAtLineBegin()
   {
      return _commaAtLineBegin;
   }

   public void setCommaAtLineBegin(boolean commaAtLineBegin)
   {
      _commaAtLineBegin = commaAtLineBegin;
   }

   public boolean isLineBreakFor_AND_OR_in_FROM_clause()
   {
      return _lineBreakFor_AND_OR_in_FROM_clause;
   }

   public void setLineBreakFor_AND_OR_in_FROM_clause(boolean lineBreakFor_AND_OR_in_FROM_clause)
   {
      _lineBreakFor_AND_OR_in_FROM_clause = lineBreakFor_AND_OR_in_FROM_clause;
   }

   public boolean isIndentSections()
   {
      return _indentSections;
   }

   public void setIndentSections(boolean indentSections)
   {
      _indentSections = indentSections;
   }
}
