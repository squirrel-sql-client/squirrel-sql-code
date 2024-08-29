package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;
import java.util.Objects;


public class ProjectionFieldValue implements Serializable
{
   public static final String HIBERNATE_UNINITIALIZED = "<uninitialized>";

   public static final String UNKNOWN_FIELD_NAME = null;
   public static final String UNKNOWN_TYPE_NAME = null;
   public static final String UNKNOWN_FIELD_VALUE = null;

   private final String valueAsString;
   private final boolean valuesIsNull;
   private final String fieldName;
   private final String fieldTypeName;
   private final boolean jsonValueWithQuotes;

   private ProjectionDisplaySwitch projectionDisplaySwitch;

   public ProjectionFieldValue(String valueAsString, boolean valuesIsNull, String fieldName, String fieldTypeName, boolean jsonValueWithQuotes)
   {
      this.valueAsString = valueAsString;
      this.valuesIsNull = valuesIsNull;
      this.fieldName = fieldName;
      this.fieldTypeName = fieldTypeName;
      this.jsonValueWithQuotes = jsonValueWithQuotes;
   }

   public String toUiRepresentationString()
   {
      switch( getProjectionDisplayMode() )
      {
         case DEFAULT_MODE:
            return asDefaultString();
         case JSON_MODE:
            return asJsonString();
         case JSON_MODE_INC_TYPES:
            return asJsonStringIncType();
         case XML_MODE:
            return asXmlString();
         case XML_MODE_INC_TYPES:
            return asXmlStringIncTypes();
         default:
            throw new IllegalStateException("Unknown ProjectionDisplayMode: " + getProjectionDisplayMode());
      }

   }

   private String asJsonStringIncType()
   {
      return "{\"type\" : " + getJsonTypeName() + ", \"field\" : " + getJsonFieldName() + ", \"value\" : " + getJsonValue() + "}";
   }

   private String asJsonString()
   {
      return getJsonFieldName()  + " : " + getJsonValue();
   }

   private String asXmlString()
   {
      return "<field name=\"" + getXmlFieldName() + "\">" + getXmlValue() + "</field>";
   }

   private String asXmlStringIncTypes()
   {
      return "<field name=\"" + getXmlFieldName() + "\" type=\"" + getXmlTypeName()  + "\">" + getXmlValue() + "</field>";
   }

   private String getXmlValue()
   {
      if(valuesIsNull)
      {
         return "null";
      }

      if(Objects.equals(valueAsString,  UNKNOWN_FIELD_VALUE))
      {
         return "unknown";
      }

      return HibernateServerStringUtils.escapeXmlChars(valueAsString);
   }

   private String getXmlFieldName()
   {
      String ret = Objects.equals(fieldName, UNKNOWN_FIELD_NAME) ? "unknownField" : fieldName;
      return HibernateServerStringUtils.escapeXmlChars(ret);
   }

   private String getXmlTypeName()
   {
      String ret = Objects.equals(fieldTypeName, UNKNOWN_TYPE_NAME) ? "unknownType" : fieldTypeName;
      return HibernateServerStringUtils.escapeXmlChars(ret);
   }


   private String getJsonValue()
   {
      if(valuesIsNull)
      {
         return "null";
      }

      if(Objects.equals(valueAsString,  UNKNOWN_FIELD_VALUE))
      {
         return "<unknown>";
      }

      return jsonValueWithQuotes ? "\"" + HibernateServerStringUtils.escapeJsonChars(valueAsString) + "\"" : valueAsString;
   }

   private String getJsonFieldName()
   {
      return Objects.equals(fieldName,  UNKNOWN_FIELD_NAME) ? "\"<unknownField>\"" : "\"" + HibernateServerStringUtils.escapeJsonChars(fieldName) + "\"";
   }

   private String getJsonTypeName()
   {
      return Objects.equals(fieldTypeName,  UNKNOWN_TYPE_NAME) ? "\"<unknownType>\"" : "\"" + HibernateServerStringUtils.escapeJsonChars(fieldTypeName) + "\"";
   }

   private String asDefaultString()
   {
      return getDefaultFieldName() + "=" + getDefaultValue();
   }

   private String getDefaultValue()
   {
      if(valuesIsNull)
      {
         return "null";
      }

      if(Objects.equals(valueAsString,  UNKNOWN_FIELD_VALUE))
      {
         return "<unknown>";
      }

      return valueAsString;
   }

   private String getDefaultFieldName()
   {
      return Objects.equals(fieldName,  UNKNOWN_FIELD_NAME) ? "<unknownField>" : fieldName;
   }



   public void setProjectionDisplaySwitch(ProjectionDisplaySwitch projectionDisplaySwitch)
   {
      this.projectionDisplaySwitch = projectionDisplaySwitch;
   }

   public ProjectionDisplayMode getProjectionDisplayMode()
   {
      if( null == projectionDisplaySwitch )
      {
         return ProjectionDisplayMode.DEFAULT_MODE;
      }

      return projectionDisplaySwitch.getProjectionDisplayMode();
   }
}
