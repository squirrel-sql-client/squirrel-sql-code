package net.sourceforge.squirrel_sql.plugins.graph.xmlbeans;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Needed as with the former NanoXml library it was possible
 * to create XML tags with leading numbers.
 */
public class LegacyNanoxmlConverter
{
   public static InputStream convertXml(String graphFilePath)
   {
      try
      {
         final List<String> lines = Files.readAllLines(Path.of(graphFilePath));

         StringBuilder sb = new StringBuilder();
         for (String line : lines)
         {
            final String buf = line.replaceAll("<32Converted>", "<converted32>").replaceAll("</32Converted>", "</converted32>");
            sb.append(buf).append('\n');
         }

         final ByteArrayInputStream bis = new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));

         return bis;
      }
      catch (IOException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
