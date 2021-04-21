package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.scriptbuilder;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileScriptBuilder implements ScriptBuilder
{
   private final StringBuilder _sb;
   private Path _path;

   private boolean _firstFlush = true;

   public FileScriptBuilder(File file)
   {
      _path = file.toPath();
      _sb = new StringBuilder();
   }

   @Override
   public void append(String s)
   {
      _sb.append(s);
      checkFlush();
   }

   @Override
   public void append(StringBuilder sb)
   {
      _sb.append(sb);
      checkFlush();
   }

   private void checkFlush()
   {
      if(100000 < _sb.length())
      {
         return;
      }

      flush();
   }

   public void flush()
   {
      try
      {
         if (_firstFlush)
         {
            Files.deleteIfExists(_path);
            Files.writeString(_path, _sb, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            _firstFlush = false;
         }
         else
         {
            Files.writeString(_path, _sb, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
         }

         _sb.setLength(0);
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public String getFileName()
   {
      return _path.toFile().getAbsolutePath();
   }
}
