package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.sql.Blob;

/**
 * Was introduced for SourceForge bug #1464
 * and
 * https://github.com/xerial/sqlite-jdbc/issues/589
 */
public class BlobResult
{
   private Blob _blob;
   private Throwable _eBlob;

   private byte[] _bytesResult;
   private Throwable _eBytes;

   private String _stringResult;

   public BlobResult(Blob blob)
   {
      _blob = blob;
   }

   public BlobResult(byte[] bytesResult, Throwable eBlob)
   {
      _bytesResult = bytesResult;
      _eBlob = eBlob;
   }

   public BlobResult(String stringResult, Throwable eBytes)
   {

      _stringResult = stringResult;
      _eBytes = eBytes;
   }

   public boolean isBlob()
   {
      return null != _blob;
   }

   public String getAsString()
   {
      if(null != _bytesResult)
      {
         return new String(_bytesResult);
      }
      else if(null != _stringResult)
      {
         return _stringResult;
      }

      return null;
   }

   public Blob getBlob()
   {
      return _blob;
   }

   public byte[] getBytes()
   {
      return _bytesResult;
   }
}
