package net.sourceforge.squirrel_sql.client.util.codereformat;

import net.sourceforge.squirrel_sql.client.preferences.codereformat.FormatSqlPanel;
import net.sourceforge.squirrel_sql.client.preferences.codereformat.FormatSqlPref;
import net.sourceforge.squirrel_sql.client.preferences.codereformat.FormatSqlPrefReader;
import net.sourceforge.squirrel_sql.client.preferences.codereformat.KeywordBehaviourPref;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.util.ArrayList;
import java.util.Collection;

public class CodeReformatorConfig
{
   private String _statementSeparator;
   private CommentSpec[] _commentSpecs;
   private String _indent;
   private int _trySplitLineLen;
   private boolean _doInsertValuesAlign;

   private PieceMarkerSpec[] keywordPieceMarkerSpec = new PieceMarkerSpec[0];

   private boolean _indentSections;
   private boolean _lineBreakFor_AND_OR_in_FROM_clause;
   private boolean _commasAtLineBegin;


   /**
    * Use CodeReformatorConfigFactory to create instances of this class
    */
   CodeReformatorConfig(String statementSeparator,
                        CommentSpec[] commentSpecs,
                        String indent,
                        int trySplitLineLen,
                        boolean doInsertValuesAlign,
                        ArrayList<PieceMarkerSpec> specs,
                        boolean indentSections,
                        boolean lineBreakFor_AND_OR_in_FROM_clause,
                        boolean commasAtLineBegin)
   {
      _statementSeparator = statementSeparator;
      _commentSpecs = commentSpecs;
      _indent = indent;
      _trySplitLineLen = trySplitLineLen;
      _doInsertValuesAlign = doInsertValuesAlign;
      _indentSections = indentSections;
      _lineBreakFor_AND_OR_in_FROM_clause = lineBreakFor_AND_OR_in_FROM_clause;
      _commasAtLineBegin = commasAtLineBegin;
      keywordPieceMarkerSpec = specs.toArray(new PieceMarkerSpec[specs.size()]);
   }

   public String getStatementSeparator()
   {
      return _statementSeparator;
   }

   public CommentSpec[] getCommentSpecs()
   {
      return _commentSpecs;
   }

   public String getIndent()
   {
      return _indent;
   }

   public int getTrySplitLineLen()
   {
      return _trySplitLineLen;
   }

   public PieceMarkerSpec[] getKeywordPieceMarkerSpecs()
   {
      return keywordPieceMarkerSpec;
   }

   public boolean isDoInsertValuesAlign()
   {
      return _doInsertValuesAlign;
   }

   public boolean isIndentSections()
   {
      return _indentSections;
   }

   public boolean isLineBreakFor_AND_OR_in_FROM_clause()
   {
      return _lineBreakFor_AND_OR_in_FROM_clause;
   }

   public boolean isCommasAtLineBegin()
   {
      return _commasAtLineBegin;
   }
}
