package org.squirrelsql.session.sql.syntax;

import org.fife.ui.rsyntaxtextarea.Token;
import org.squirrelsql.session.parser.kernel.ErrorInfo;

public class ErrorInfosHandler
{
   private ErrorInfo[] _errorInfos = new ErrorInfo[0];


   public String getErrorStyle()
   {
      return TokenToCssStyleMapper.getErrorStyle();
   }

   public boolean isError(int lineStart, Token token)
   {

      for (ErrorInfo errorInfo : _errorInfos)
      {
         int tokenBegin = lineStart + token.getTextOffset();
         int tokenEnd = lineStart + token.getTextOffset() + token.length() - 1;
         if(errorInfo.beginPos <= tokenBegin && tokenEnd <= errorInfo.endPos)
         {
            //System.out.println("   " + errorInfo + " ## BEGIN=" + tokenBegin + " END=" + tokenEnd + "  TOKEN="+token);

            return true;
         }
      }

      return false;
   }

   public boolean setErrorInfos(ErrorInfo[] errorInfos)
   {
      ErrorInfo[] oldErrorInfos = _errorInfos;
      _errorInfos = errorInfos;

      if(oldErrorInfos.length != _errorInfos.length)
      {
         return true;
      }

      for (int i = 0; i < _errorInfos.length; i++)
      {
         ErrorInfo errorInfo = _errorInfos[i];

         if(false == errorInfo.equals(oldErrorInfos[i]))
         {
            return true;
         }
      }

      return false;

   }
}
