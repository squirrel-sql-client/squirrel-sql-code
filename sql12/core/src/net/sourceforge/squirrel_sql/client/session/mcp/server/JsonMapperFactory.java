package net.sourceforge.squirrel_sql.client.session.mcp.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMapperFactory
{
   public static ObjectMapper createJsonMapper()
   {
      return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL)
                               .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
   }
}
