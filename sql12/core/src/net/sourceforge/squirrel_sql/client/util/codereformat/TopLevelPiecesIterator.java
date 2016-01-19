package net.sourceforge.squirrel_sql.client.util.codereformat;

public class TopLevelPiecesIterator
{
   private final PieceMarkerSpec[] _pieceSpecs;
   private StateOfPosition[] _statesOfPosition;
   private final boolean _lineBreakFor_AND_OR_in_FROM_clause;
   private boolean _in_FROM_Clause;

   public TopLevelPiecesIterator(PieceMarkerSpec[] pieceSpecs, StateOfPosition[] statesOfPosition, boolean lineBreakFor_AND_OR_in_FROM_clause)
   {
      _pieceSpecs = pieceSpecs;
      _statesOfPosition = statesOfPosition;
      _lineBreakFor_AND_OR_in_FROM_clause = lineBreakFor_AND_OR_in_FROM_clause;
   }

   public Piece getNextToplevelPiece(int startAt, String in)
   {
      Piece ret = new Piece();
      ret.beginsAt = in.length();

      for(int i=0; i < _pieceSpecs.length; ++i)
      {
         if(hasFromClauseLineBreakVeto(_pieceSpecs[i]))
         {
            continue;
         }

         int buf = getTopLevelIndex(startAt, in, _pieceSpecs[i]);
         if(-1 < buf && buf < ret.beginsAt)
         {
            ret.spec = _pieceSpecs[i];
            ret.beginsAt = buf;
         }
      }

      if(null == ret.spec)
      {
         ret.beginsAt = startAt;
      }
      else
      {
         if(ret.spec.is_FROM_begin())
         {
            _in_FROM_Clause = true;
         }

         if(ret.spec.is_FROM_end())
         {
            _in_FROM_Clause = false;
         }
      }

      return ret;
   }

   private int getTopLevelIndex(int startAt, String in, PieceMarkerSpec pieceSpec)
   {
      int ix = in.indexOf(pieceSpec.getPieceMarker(), startAt);

      while(-1 != ix)
      {
         if(_statesOfPosition[ix].isTopLevel)
         {
            if(pieceSpec.needsSuroundingWhiteSpaces())
            {
               char before = (0 == ix ? ' ': in.charAt(ix-1) );

               int pieceMArkerEnd = ix + pieceSpec.getPieceMarker().length() - 1;
               char after = (pieceMArkerEnd == in.length() - 1 ? ' ': in.charAt(pieceMArkerEnd+1) );

               if(Character.isWhitespace(before) && Character.isWhitespace(after))
               {
                  return ix;
               }
            }
            else
            {
               return ix;
            }
         }
         ix = in.indexOf(pieceSpec.getPieceMarker(), ix + 1);
      }
      return -1;
   }


   private boolean hasFromClauseLineBreakVeto(PieceMarkerSpec pieceMarkerSpec)
   {
      if(false == _in_FROM_Clause || false == _lineBreakFor_AND_OR_in_FROM_clause)
      {
         return false;
      }

      return pieceMarkerSpec.is_AND_or_OR() && PieceMarkerSpec.TYPE_PIECE_MARKER_AT_BEGIN == pieceMarkerSpec.getType();
   }

}
