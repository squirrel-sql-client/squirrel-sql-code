package net.sourceforge.squirrel_sql.plugins.syntax.rsyntax;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.TokenMaker;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;


public class SquirrelTokenMarkerFactory extends AbstractTokenMakerFactory implements SyntaxConstants
{
   private SquirrelTokenMarker _squirrelTokenMarker;

   public SquirrelTokenMarkerFactory(SquirrelRSyntaxTextArea squirrelRSyntaxTextArea, ISyntaxHighlightTokenMatcher syntaxHighlightTokenMatcher)
   {
      _squirrelTokenMarker = new SquirrelTokenMarker(squirrelRSyntaxTextArea, syntaxHighlightTokenMatcher); 
   }

   protected Map createTokenMakerKeyToClassNameMap()
   {
      HashMap map = new HashMap();
      map.put(SYNTAX_STYLE_SQL, "fw.SquirrelTokenMarker");
      return map;

   }

   @Override
   protected TokenMaker getTokenMakerImpl(String key)
   {
      return _squirrelTokenMarker;
   }

}