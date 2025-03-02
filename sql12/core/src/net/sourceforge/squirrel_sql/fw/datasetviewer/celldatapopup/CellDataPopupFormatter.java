package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class CellDataPopupFormatter
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDataPopupFormatter.class);
   private static final ILogger s_log = LoggerController.createLogger(CellDataPopupFormatter.class);


   public static FormattingResult format(String toFormat, boolean silent)
   {
      Exception jsonFormatException = null;

      FormatResult formatResult = tryJson(toFormat);
      jsonFormatException = formatResult.error;
      if (null == jsonFormatException)
      {
         return new FormattingResult(formatResult.result, true, FormattingResultType.JSON);
      }

      formatResult = tryXml(toFormat);
      if (null == formatResult.error)
      {
         return new FormattingResult(formatResult.result, true, FormattingResultType.XML);
      }

      if(false == silent)
      {
         String msg = s_stringMgr.getString(
               "CellDataPopupFormatter.failed.reformat",
               StringUtilities.removeNewLine("" + jsonFormatException),
               StringUtilities.removeNewLine("" + formatResult.error));

         Main.getApplication().getMessageHandler().showErrorMessage(msg);

         s_log.error("Failed to reformat cell data as JSON and as XML.\n" +
               "JSON error:\n" + Utilities.getStackTrace(jsonFormatException) +
               "XML error:\n" + Utilities.getStackTrace(formatResult.error));
      }


      return new FormattingResult(toFormat, false, FormattingResultType.NONE);
   }

   private static FormatResult tryXml(String toFormat)
   {
      try
      {
         Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(toFormat)));
         TransformerFactory transformerFactory = TransformerFactory.newInstance();
         transformerFactory.setAttribute("indent-number", 3);
         Transformer transformer = transformerFactory.newTransformer();
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");

         if (null == document.getXmlEncoding() && false == document.getXmlStandalone())
         {
            // Here we assume the input XML comes without declaration, so formatting should leave it out, too.
            // See https://www.w3resource.com/xml/declarations.php
            // or https://www.w3.org/TR/xml/#sec-prolog-dtd
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
         }

         //transformer.setOutputProperty(OutputKeys.METHOD, "text");
         StringWriter writer = new StringWriter();
         transformer.transform(new DOMSource(document), new StreamResult(writer));
         String formatted = writer.toString();

         formatted = StringUtilities.removeEmptyLines(formatted, previousLine -> false == previousLine.trim().endsWith(">"));

         return new FormatResult(formatted);
      }
      catch (Exception e)
      {
         return new FormatResult(e);
      }
   }

   private static FormatResult tryJson(String toFormat)
   {
      try
      {
         ObjectMapper mapper = new ObjectMapper();
         JsonNode jsonNode = mapper.readTree(toFormat);

         StringWriter sw = new StringWriter();
         mapper.writerWithDefaultPrettyPrinter().writeValue(sw, jsonNode);

         return new FormatResult(sw.toString());
      }
      catch (IOException e)
      {
         return new FormatResult(e);
      }
   }

   private static class FormatResult
   {
      private String result;
      private Exception error;

      public FormatResult(String result)
      {
         this.result = result;
      }

      public FormatResult(Exception error)
      {
         this.error = error;
      }
   }


   public static void main(String[] args)
   {
      String json =
            "[             { " +
                  "                                    \"url\" : \"jdbc:derby://<server>[:<port>]/<databaseName>[;<URL attribute>=<value>]\", " +
                  "  \"squirrelPredefinedDriver\" :                       true, " +
                  "  \"websiteUrl\" : \"http://db.apache.org/derby\", " +
                  "  \"jarFileNamesList\" : [ ], " +
                  "  \"driverClassName\" :                                \"org.apache.derby.jdbc.ClientDriver\", " +
                  "  \"name\" : \"Apache Derby Client\", " +
                  "  \"id\" : \"PRE_DEF_0042\" " +
                  "}, { " +
                  "  \"url\" : \"jdbc:derby:<database>[;create=true]\", " +
                  "  \"squirrelPredefinedDriver\" : true, " +
                  "  \"websiteUrl\" : \"http://db.apache.org/derby\", " +
                  "  \"jarFileNamesList\" : [ ], " +
                  "  \"driverClassName\" : \"org.apache.derby.jdbc.EmbeddedDriver\", " +
                  "  \"name\" : \"Apache Derby Embedded\", " +
                  "  \"id\" : \"PRE_DEF_0041\" " +
                  "}]";

      System.out.println("1: " + tryJson(json).result);

      System.out.println("2: " + tryJson(" ").result);

      System.out.println("3: " + tryJson(" ffg").result);
      System.out.println("4: " + tryJson("122").result);




      String jsonCut =
            "[             { " +
                  "                                    \"url\" : \"jdbc:derby://<server>[:<port>]/<databaseName>[;<URL attribute>=<value>]\", " +
                  "  \"squirrelPredefinedDriver\" :                       true, " +
                  "  \"websiteUrl\" : \"http://db.apache.org/derby\", " +
                  "  \"jarFileNamesList\" : [ ], " +
                  "  \"driverClassName\" :                                \"org.apache.derby.jdbc.ClientDriver\", " +
                  "  \"name\" : \"Apache Derby Client\", " +
                  "  \"id\" : \"PRE_DEF_0042\" " +
                  "}, { " +
                  "  \"url\" : \"jdbc:derby:<database>[;create=true]\", " +
                  "  \"squirrelPredefinedDriver\" : true, ";

      System.out.println("4: " + tryJson(jsonCut).result);


      System.out.println("5: " + tryXml("<AU> <someTag>bla</someTag> <otherTag>blub</otherTag> </AU>").result);

      String xml = "<emails> <email> <from>Kai</from> <to>Amanda</to> <time>2018-03-05</time>\n" +
            "<subject>I am flying to you</subject></email> <email>\n" +
            "<from>Jerry</from> <to>Tom</to> <time>1992-08-08</time> <subject>Hey Tom, catch me if you can!</subject>\n" +
            "</email> </emails>";

      System.out.println("6: " + tryXml(xml).result);

      xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<users>\n" +
            "<user>\n" +
            "<name>test one</name>\n" +
            "<last-name>test two</last-name>\n" +
            "<username>test three</username>\n" +
            "</user>\n" +
            "</users>\n";

      System.out.println("7: " + tryXml(xml).result);

      System.out.println("8: " + tryXml("<AU> <someTag>bla</someTag> dgfdg\nsdsdfds <otherTag>blub</otherTag> </AU>").result);


   }
}
