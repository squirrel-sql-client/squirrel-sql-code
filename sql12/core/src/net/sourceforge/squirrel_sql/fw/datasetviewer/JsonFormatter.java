package net.sourceforge.squirrel_sql.fw.datasetviewer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

public class JsonFormatter
{
   private String _toFormat;
   private String _formattedJson;

   public JsonFormatter(String toFormat)
   {
      try
      {
         if(null == toFormat)
         {
            return;
         }


         _toFormat = toFormat;

         ObjectMapper mapper = new ObjectMapper();
         JsonNode jsonNode = mapper.readTree(_toFormat);

         StringWriter sw = new StringWriter();
         mapper.writerWithDefaultPrettyPrinter().writeValue(sw, jsonNode);

         _formattedJson = sw.toString();
      }
      catch (JsonParseException e)
      {
         // It's just not a json
      }
      catch (JsonMappingException e)
      {
         // It's just not a json
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public boolean success()
   {
      return null != _formattedJson;
   }

   public String getFormattedJson()
   {
      return _formattedJson;
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

      System.out.println(new JsonFormatter(json).getFormattedJson());

      System.out.println(new JsonFormatter(" ").getFormattedJson());

      System.out.println(new JsonFormatter(" ffg").getFormattedJson());
      System.out.println(new JsonFormatter("122").getFormattedJson());




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

      System.out.println(new JsonFormatter(jsonCut).getFormattedJson());


   }
}
