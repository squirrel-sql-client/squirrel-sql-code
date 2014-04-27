package org.squirrelsql.session.schemainfo;

import org.squirrelsql.services.CaseInsensitiveString;

import java.io.Serializable;
import java.util.HashSet;

public class CaseInsensitiveCache implements Serializable
{
   private HashSet<CaseInsensitiveString> _ciTableNames = new HashSet<>();
   private HashSet<CaseInsensitiveString> _ciProcedureNames = new HashSet<>();
   private HashSet<CaseInsensitiveString> _ciKeywords = new HashSet<>();

   private CaseInsensitiveString _buf = new CaseInsensitiveString();

   public void addProc(String s)
   {
      _ciProcedureNames.add(new CaseInsensitiveString(s));
   }

   public void addKeyword(String s)
   {
      _ciKeywords.add(new CaseInsensitiveString(s));
   }

   public void addTable(String s)
   {
      _ciTableNames.add(new CaseInsensitiveString(s));
   }


   public boolean isTable(char[] buffer, int offset, int len)
   {
      return _ciTableNames.contains(_buf.setCharBuffer(buffer, offset, len));
   }

   public boolean isProcedure(char[] buffer, int offset, int len)
   {
      return _ciProcedureNames.contains(_buf.setCharBuffer(buffer, offset, len));
   }

   public boolean isKeyword(char[] buffer, int offset, int len)
   {
      return _ciKeywords.contains(_buf.setCharBuffer(buffer, offset, len));
   }
}
