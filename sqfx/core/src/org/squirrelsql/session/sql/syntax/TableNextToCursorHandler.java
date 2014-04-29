package org.squirrelsql.session.sql.syntax;

import org.fife.ui.rsyntaxtextarea.Token;
import org.squirrelsql.session.TableInfo;
import org.squirrelsql.session.schemainfo.SchemaCache;

import java.util.ArrayList;
import java.util.List;

public class TableNextToCursorHandler
{
   private TableNextToCursorListener _tableNextToCursorListener;
   private final int _caretPosition;
   private final SchemaCache _schemaCache;
   private List<TableInfo> _tablesNextToCursor = new ArrayList<>();

   private int _minDistToCursor = Integer.MAX_VALUE;

   public TableNextToCursorHandler(TableNextToCursorListener tableNextToCursorListener, int caretPosition, SchemaCache schemaCache)
   {
      _tableNextToCursorListener = tableNextToCursorListener;
      _caretPosition = caretPosition;
      _schemaCache = schemaCache;
   }

   public void checkToken(int lineStart, Token token)
   {
      if(null == _tableNextToCursorListener)
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
      _tablesNextToCursor = _schemaCache.getTables(textArray, token.getOffset(), token.length());

   }

   public void fireTableNextToCursor()
   {
      if(null == _tableNextToCursorListener)
      {
         return;
      }

      _tableNextToCursorListener.currentTableInfosNextToCursor(_tablesNextToCursor);

   }
}
