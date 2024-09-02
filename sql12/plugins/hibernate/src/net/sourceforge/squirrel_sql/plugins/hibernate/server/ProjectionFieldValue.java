package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;
import java.util.Objects;


public class ProjectionFieldValue implements Serializable
{
   public static final String HIBERNATE_UNINITIALIZED = "<uninitialized>";

   public static final String UNKNOWN_FIELD_NAME = null;
   public static final String UNKNOWN_TYPE_NAME = null;
   public static final String UNKNOWN_FIELD_VALUE = null;

   private final String _valueAsString;
   private final boolean _valuesIsNull;
   private final String _fieldName;
   private final String _fieldTypeName;
   private final boolean _jsonValueWithQuotes;

   private ProjectionDisplaySwitch projectionDisplaySwitch;

   public ProjectionFieldValue(String valueAsString, boolean valuesIsNull, String fieldName, String fieldTypeName, boolean jsonValueWithQuotes)
   {
      _valueAsString = valueAsString;
      _valuesIsNull = valuesIsNull;
      _fieldName = fieldName;
      _fieldTypeName = fieldTypeName;
      _jsonValueWithQuotes = jsonValueWithQuotes;
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
      if( _valuesIsNull )
      {
         return "null";
      }

      if(Objects.equals(_valueAsString, UNKNOWN_FIELD_VALUE))
      {
         return "unknown";
      }

      return HibernateServerStringUtils.escapeXmlChars(_valueAsString);
   }

   private String getXmlFieldName()
   {
      String ret = Objects.equals(_fieldName, UNKNOWN_FIELD_NAME) ? "unknownField" : _fieldName;
      return HibernateServerStringUtils.escapeXmlChars(ret);
   }

   private String getXmlTypeName()
   {
      String ret = Objects.equals(_fieldTypeName, UNKNOWN_TYPE_NAME) ? "unknownType" : _fieldTypeName;
      return HibernateServerStringUtils.escapeXmlChars(ret);
   }


   private String getJsonValue()
   {
      if( _valuesIsNull )
      {
         return "null";
      }

      if(Objects.equals(_valueAsString, UNKNOWN_FIELD_VALUE))
      {
         return "<unknown>";
      }

      return _jsonValueWithQuotes ? "\"" + HibernateServerStringUtils.escapeJsonChars(_valueAsString) + "\"" : _valueAsString;
   }

   private String getJsonFieldName()
   {
      return Objects.equals(_fieldName, UNKNOWN_FIELD_NAME) ? "\"<unknownField>\"" : "\"" + HibernateServerStringUtils.escapeJsonChars(
            _fieldName) + "\"";
   }

   private String getJsonTypeName()
   {
      return Objects.equals(_fieldTypeName, UNKNOWN_TYPE_NAME) ? "\"<unknownType>\"" : "\"" + HibernateServerStringUtils.escapeJsonChars(
            _fieldTypeName) + "\"";
   }

   private String asDefaultString()
   {
      return getDefaultFieldName() + "=" + getDefaultValue();
   }

   private String getDefaultValue()
   {
      if( _valuesIsNull )
      {
         return "null";
      }

      if(Objects.equals(_valueAsString, UNKNOWN_FIELD_VALUE))
      {
         return "<unknown>";
      }

      return _valueAsString;
   }

   private String getDefaultFieldName()
   {
      return Objects.equals(_fieldName, UNKNOWN_FIELD_NAME) ? "<unknownField>" : _fieldName;
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
