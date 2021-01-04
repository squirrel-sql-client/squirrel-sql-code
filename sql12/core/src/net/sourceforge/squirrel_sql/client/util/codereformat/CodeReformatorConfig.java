package net.sourceforge.squirrel_sql.client.util.codereformat;

import java.util.ArrayList;

public class CodeReformatorConfig
{
   private String _statementSeparator;
   private CommentSpec[] _commentSpecs;
   private String _indent;
   private int _trySplitLineLen;
   private boolean _doInsertValuesAlign;

   private PieceMarkerSpec[] _keywordPieceMarkerSpec = new PieceMarkerSpec[0];

   private boolean _indentSections;
   private boolean _lineBreakFor_AND_OR_in_FROM_clause;
   private boolean _commasAtLineBegin;

   private final ColumnListSpiltMode _selectListSplitMode;
   private boolean _useVerticalBlankFormatter;


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
                        boolean commasAtLineBegin,
                        ColumnListSpiltMode selectListSplitMode,
                        boolean useVerticalBlankFormatter)
   {
      _statementSeparator = statementSeparator;
      _commentSpecs = commentSpecs;
      _indent = indent;
      _trySplitLineLen = trySplitLineLen;
      _doInsertValuesAlign = doInsertValuesAlign;
      _indentSections = indentSections;
      _lineBreakFor_AND_OR_in_FROM_clause = lineBreakFor_AND_OR_in_FROM_clause;
      _commasAtLineBegin = commasAtLineBegin;
      _keywordPieceMarkerSpec = specs.toArray(new PieceMarkerSpec[specs.size()]);
      _selectListSplitMode = selectListSplitMode;

      _useVerticalBlankFormatter = useVerticalBlankFormatter;
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
      return _keywordPieceMarkerSpec;
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

   public ColumnListSpiltMode getSelectListSpiltMode()
   {
      return _selectListSplitMode;
   }

   public boolean isUseVerticalBlankFormatter()
   {
      return _useVerticalBlankFormatter;
   }
}
