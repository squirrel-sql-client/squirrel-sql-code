package net.sourceforge.squirrel_sql.client.util.codereformat;

import net.sourceforge.squirrel_sql.client.preferences.codereformat.FormatSqlPanel;
import net.sourceforge.squirrel_sql.client.preferences.codereformat.FormatSqlPref;
import net.sourceforge.squirrel_sql.client.preferences.codereformat.FormatSqlPrefReader;
import net.sourceforge.squirrel_sql.client.preferences.codereformat.KeywordBehaviourPref;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.util.ArrayList;

public class CodeReformatorConfigFactory
{

   public static final CommentSpec[] DEFAULT_COMMENT_SPECS = new CommentSpec[]
         {
               new CommentSpec("/*", "*/"),
               new CommentSpec("--", StringUtilities.getEolStr())
         };

   public static final String DEFAULT_STATEMENT_SEPARATOR = ";";

   public static CodeReformatorConfig createConfig(ISession sess)
   {
      String statementSep = sess.getQueryTokenizer().getSQLStatementSeparator();
      return createConfig(statementSep);
   }

   public static CodeReformatorConfig createConfig(String statementSep)
   {
      return createConfig(statementSep, DEFAULT_COMMENT_SPECS);
   }

   public static CodeReformatorConfig createConfig(String statementSeparator, CommentSpec[] commentSpecs)
   {
      FormatSqlPref formatSqlPref = FormatSqlPrefReader.loadPref();
      return createConfig(statementSeparator, commentSpecs, formatSqlPref);
   }

   public static CodeReformatorConfig createConfig(FormatSqlPref formatSqlPref)
   {
      return createConfig(DEFAULT_STATEMENT_SEPARATOR, DEFAULT_COMMENT_SPECS, formatSqlPref);
   }

   public static CodeReformatorConfig createConfig(String statementSeparator, CommentSpec[] commentSpecs, FormatSqlPref formatSqlPref)
   {
      String indent = "";
      for (int i = 0; i < formatSqlPref.getIndent(); i++)
      {
         indent += " ";
      }

      int trySplitLineLen = formatSqlPref.getPreferedLineLength();


      ArrayList<PieceMarkerSpec> specs = new ArrayList<PieceMarkerSpec>();

      for (KeywordBehaviourPref keywordBehaviourPref : formatSqlPref.getKeywordBehaviourPrefs())
      {
         specs.addAll(createPieceMarkerSpecs(keywordBehaviourPref));
      }

      return new CodeReformatorConfig(statementSeparator, commentSpecs, indent, trySplitLineLen, formatSqlPref.isDoInsertValuesAlign(), specs);
   }

   private static ArrayList<PieceMarkerSpec> createPieceMarkerSpecs(KeywordBehaviourPref keywordBehaviourPref)
   {
      ArrayList<PieceMarkerSpec> ret = new ArrayList<PieceMarkerSpec>();

      String keyWord = keywordBehaviourPref.getKeyWord();
      Integer pieceMarkerSpecType = FormatSqlPanel.KeywordBehaviour.forId(keywordBehaviourPref.getKeywordBehaviourId()).getPieceMarkerSpecType();

      if (FormatSqlPref.JOIN_DISPLAY_STRING.equals(keyWord))
      {
         ret.add(new PieceMarkerSpec("JOIN", PieceMarkerSpec.TYPE_PIECE_MARKER_AT_BEGIN));
         ret.add(new PieceMarkerSpec("INNER JOIN", PieceMarkerSpec.TYPE_PIECE_MARKER_AT_BEGIN));
         ret.add(new PieceMarkerSpec("LEFT JOIN", PieceMarkerSpec.TYPE_PIECE_MARKER_AT_BEGIN));
         ret.add(new PieceMarkerSpec("RIGHT JOIN", PieceMarkerSpec.TYPE_PIECE_MARKER_AT_BEGIN));

      }
      else
      {
         if (null != pieceMarkerSpecType)
         {
            ret.add(new PieceMarkerSpec(keyWord, pieceMarkerSpecType));
         }
      }
      return ret;
   }
}
