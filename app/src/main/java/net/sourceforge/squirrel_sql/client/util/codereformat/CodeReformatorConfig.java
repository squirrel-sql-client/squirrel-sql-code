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

   /**
    * Use CodeReformatorConfigFactory to create instances of this class
    */
   CodeReformatorConfig(String statementSeparator, CommentSpec[] commentSpecs, String indent, int trySplitLineLen, boolean doInsertValuesAlign, ArrayList<PieceMarkerSpec> specs)
   {
      _statementSeparator = statementSeparator;
      _commentSpecs = commentSpecs;
      _indent = indent;
      _trySplitLineLen = trySplitLineLen;
      _doInsertValuesAlign = doInsertValuesAlign;
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
}
