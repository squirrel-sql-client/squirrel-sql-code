package org.squirrelsql.session;

public class TokenAtCarretInfo
{
   private String _tokenTillCarret;
   private final int _tokenBeginPos;
   private int _tokenEndPos;
   private final int _caretPosition;

   public TokenAtCarretInfo(String tokenTillCarret, int tokenBeginPos, int tokenEndPos, int caretPosition)
   {
      _tokenTillCarret = tokenTillCarret;
      _tokenBeginPos = tokenBeginPos;
      _tokenEndPos = tokenEndPos;
      _caretPosition = caretPosition;
   }

   public String getTokenTillCarret()
   {
      return _tokenTillCarret;
   }

   public int getTokenBeginPos()
   {
      return _tokenBeginPos;
   }

   public int getTokenEndPos()
   {
      return _tokenEndPos;
   }

   public int getCaretPosition()
   {
      return _caretPosition;
   }
}
