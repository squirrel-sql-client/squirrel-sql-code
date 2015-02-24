package org.squirrelsql.session.sql.syntax;

import javafx.beans.value.ObservableObjectValue;
import org.fife.ui.rsyntaxtextarea.Token;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.schemainfo.SchemaCache;

import java.util.ArrayList;
import java.util.List;

public class TableNextToCursorHandler
{
   private LexAndParseResultListener _lexAndParseResultListener;
   private final int _caretPosition;
   private final ObservableObjectValue<SchemaCache> _schemaCacheValue;
   private List<TableInfo> _tablesNextToCursor = new ArrayList<>();

   private int _minDistToCursor = Integer.MAX_VALUE;

   public TableNextToCursorHandler(LexAndParseResultListener lexAndParseResultListener, int caretPosition, ObservableObjectValue<SchemaCache> schemaCacheValue)
   {
      _lexAndParseResultListener = lexAndParseResultListener;
      _caretPosition = caretPosition;
      _schemaCacheValue = schemaCacheValue;
   }

   public void checkToken(int lineStart, Token token)
   {
      if(null == _lexAndParseResultListener)
      {
         return;
      }

      if(token.getType() != SquirrelTokenMarker.TOKEN_IDENTIFIER_TABLE)
      {
         return;
      }

      int distToCursor = Math.abs(lineStart + token.getOffset() - _caretPosition);

      if(_minDistToCursor <= distToCursor)
      {
         return;
      }

      char[] textArray = token.getTextArray();
      _tablesNextToCursor = _schemaCacheValue.get().getTables(textArray, token.getOffset(), token.length());

   }

   public void fireTableNextToCursor()
   {
      if(null == _lexAndParseResultListener)
      {
         return;
      }

      _lexAndParseResultListener.currentTableInfosNextToCursor(_tablesNextToCursor);

   }
}
