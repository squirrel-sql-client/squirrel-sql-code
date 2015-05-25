package org.squirrelsql.settings;

import org.squirrelsql.services.I18n;
import org.squirrelsql.sqlreformat.PieceMarkerSpec;

public enum KeyWordBehavior
{
   ALONE_IN_LINE(new I18n(KeyWordBehavior.class).t("keyword.behavior.alone.in.line"), PieceMarkerSpec.TYPE_PIECE_MARKER_IN_OWN_PIECE),
   START_NEW_LINE(new I18n(KeyWordBehavior.class).t("keyword.behavior.start.new.line"), PieceMarkerSpec.TYPE_PIECE_MARKER_AT_BEGIN),
   NO_INFLUENCE_ON_LINEBREAK(new I18n(KeyWordBehavior.class).t("keyword.behavior.no.influence.on.line.break"), PieceMarkerSpec.TYPE_PIECE_MARKER_AT_END);

   private String _title;
   private int _pieceMarkerType;

   KeyWordBehavior(String title, int pieceMarkerType)
   {
      _title = title;
      _pieceMarkerType = pieceMarkerType;
   }


   @Override
   public String toString()
   {
      return _title;
   }

   public int getPieceMarkerType()
   {
      return _pieceMarkerType;
   }
}
