package net.sourceforge.squirrel_sql.client.session.action.syntax.rsyntax.rtffix;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;


final class SquirrelRtfToText
{
   private Reader r;
   private StringBuilder sb;
   private StringBuilder controlWord;
   private int blockCount;
   private boolean inControlWord;

   private SquirrelRtfToText(Reader r)
   {
      this.r = r;
      this.sb = new StringBuilder();
      this.controlWord = new StringBuilder();
      this.blockCount = 0;
      this.inControlWord = false;
   }

   private String convert() throws IOException
   {
      int i = this.r.read();
      if (i != 123)
      {
         throw new IOException("Invalid RTF file");
      }
      else
      {
         while (true)
         {
            while (true)
            {
               while ((i = this.r.read()) != -1)
               {
                  char ch = (char) i;
                  switch (ch)
                  {
                     case '\n':
                     case '\r':
                        if (this.blockCount == 0 && this.inControlWord)
                        {
                           this.endControlWord();
                        }
                        break;
                     case ' ':
                        if (this.blockCount == 0)
                        {
                           if (this.inControlWord)
                           {
                              this.endControlWord();
                           }
                           else
                           {
                              this.sb.append(' ');
                           }
                        }
                        break;
                     case '\\':
                        if (this.blockCount == 0)
                        {
                           if (this.inControlWord)
                           {
                              if (this.controlWord.length() == 0)
                              {
                                 this.sb.append('\\');
                                 this.controlWord.setLength(0);
                                 this.inControlWord = false;
                              }
                              else
                              {
                                 this.endControlWord();
                                 this.inControlWord = true;
                              }
                           }
                           else
                           {
                              this.inControlWord = true;
                           }
                        }
                        break;
                     case '{':
                        if (this.inControlWord && this.controlWord.length() == 0)
                        {
                           this.sb.append('{');
                           this.controlWord.setLength(0);
                           this.inControlWord = false;
                           break;
                        }

                        ++this.blockCount;
                        break;
                     case '}':
                        if (this.inControlWord && this.controlWord.length() == 0)
                        {
                           this.sb.append('}');
                           this.controlWord.setLength(0);
                           this.inControlWord = false;
                           break;
                        }

                        --this.blockCount;
                        break;
                     default:
                        if (this.blockCount == 0)
                        {
                           if (this.inControlWord)
                           {
                              this.controlWord.append(ch);
                           }
                           else
                           {
                              this.sb.append(ch);
                           }
                        }
                  }
               }

               return this.sb.toString();
            }
         }
      }
   }

   private void endControlWord()
   {
      String word = this.controlWord.toString();
      if ("par".equals(word) || "line".equals(word))  // BUG FIX FOR SQUIRREL. THE FIX IS: || "line".equals(word)
      {
         this.sb.append('\n');
      }
      else if ("tab".equals(word))
      {
         this.sb.append('\t');
      }

      this.controlWord.setLength(0);
      this.inControlWord = false;
   }

   static String getPlainText(byte[] rtf) throws IOException
   {
      return getPlainText((InputStream) (new ByteArrayInputStream(rtf)));
   }

   private static String getPlainText(File file) throws IOException
   {
      return getPlainText((Reader) (new BufferedReader(new FileReader(file))));
   }

   static String getPlainText(InputStream in) throws IOException
   {
      return getPlainText((Reader) (new InputStreamReader(in, "US-ASCII")));
   }

   private static String getPlainText(Reader r) throws IOException
   {
      String var2;
      try
      {
         SquirrelRtfToText converter = new SquirrelRtfToText(r);
         var2 = converter.convert();
      }
      finally
      {
         r.close();
      }

      return var2;
   }

   private static String getPlainText(String rtf) throws IOException
   {
      return getPlainText((Reader) (new StringReader(rtf)));
   }
}
