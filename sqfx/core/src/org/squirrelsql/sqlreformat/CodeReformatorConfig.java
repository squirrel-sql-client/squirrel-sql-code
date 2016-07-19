package org.squirrelsql.sqlreformat;

import org.squirrelsql.settings.SQLFormatSettings;
import org.squirrelsql.settings.SQLKeyWord;

import java.util.ArrayList;

public class CodeReformatorConfig
{
   private String _statementSeparator;
   private CommentSpec[] _commentSpecs;
   private SQLFormatSettings _sqlFormatSettings;

   private PieceMarkerSpec[] _keywordPieceMarkerSpec = new PieceMarkerSpec[0];
   private String _indent;

   /**
    * Use CodeReformatorConfigFactory to create instances of this class
    */
   public CodeReformatorConfig(String statementSeparator, CommentSpec[] commentSpecs, SQLFormatSettings sqlFormatSettings)
   {
      _statementSeparator = statementSeparator;

      _commentSpecs = commentSpecs;
      _sqlFormatSettings = sqlFormatSettings;

      _indent = "";
      for (int i = 0; i < _sqlFormatSettings.getIndentSpaceCount(); i++)
      {
         _indent += " ";
      }

      ArrayList<PieceMarkerSpec> buf = new ArrayList<>();

      for (SQLKeyWord sqlKeyWord : sqlFormatSettings.getBehaviorsByKeyWord().keySet())
      {
         buf.addAll(sqlKeyWord.createPieceMarkerSpec(sqlFormatSettings.getBehaviorsByKeyWord().get(sqlKeyWord)));
      }

      _keywordPieceMarkerSpec = buf.toArray(new PieceMarkerSpec[buf.size()]);

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
      return _sqlFormatSettings.getPreferedLineLength();
   }

   public PieceMarkerSpec[] getKeywordPieceMarkerSpecs()
   {
      return _keywordPieceMarkerSpec;
   }

   public boolean isDoInsertValuesAlign()
   {
      return _sqlFormatSettings.isAlignmInsertValues();
   }

   public boolean isIndentSections()
   {
      return _sqlFormatSettings.isIndentSections();
   }

   public boolean isLineBreakFor_AND_OR_in_FROM_clause()
   {
      return _sqlFormatSettings.isLineBreakFor_AND_OR_in_FROM_clause();
   }

   public boolean isCommasAtLineBegin()
   {
      return _sqlFormatSettings.isCommaAtLineBegin();
   }

   public ColumnListSpiltMode getSelectListSpiltMode()
   {
      return ColumnListSpiltMode.valueOf(_sqlFormatSettings.getColumnListSpiltMode());
   }
}
