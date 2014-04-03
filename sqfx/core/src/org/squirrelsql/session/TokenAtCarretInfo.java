package org.squirrelsql.session;

public class TokenAtCarretInfo
{
   private String _token;
   private final int _tokenBeginPos;
   private final int _caretPosition;

   public TokenAtCarretInfo(String token, int tokenBeginPos, int caretPosition)
   {
      _token = token;
      _tokenBeginPos = tokenBeginPos;
      _caretPosition = caretPosition;
   }

   public String getToken()
   {
      return _token;
   }

   public int getTokenBeginPos()
   {
      return _tokenBeginPos;
   }

   public int getCaretPosition()
   {
      return _caretPosition;
   }
}
