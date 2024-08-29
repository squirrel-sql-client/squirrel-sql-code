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
         case JSON_MODE:
            return asJsonString();
      }

      return asDefaultString();
   }

   private String asJsonString()
   {
      return getJsonFieldName()  + " : " + getJsonValue();
   }

   private String getJsonValue()
   {
      if(valuesIsNull)
      {
         return "null";
      }

      if(Objects.equals(valueAsString,  UNKNOWN_FIELD_VALUE))
      {
         return "null";
      }

      return jsonValueWithQuotes ? "\"" + valueAsString + "\"" : valueAsString;
   }

   private String getJsonFieldName()
   {
      return Objects.equals(fieldName,  UNKNOWN_TYPE_NAME) ? "\"<unknownField>\"" : "\"" + fieldName + "\"";
   }

   private String asDefaultString()
   {
      if( false == Objects.equals(fieldName,  UNKNOWN_TYPE_NAME) )
      {
         return fieldName + "="  + (Objects.equals(valueAsString,  UNKNOWN_FIELD_NAME) ? "<unknown>" : valueAsString);
      }
      else
      {
         return (Objects.equals(valueAsString,  UNKNOWN_FIELD_VALUE) ? "<unknown>" : valueAsString);
      }
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

      return projectionDisplaySwitch.getTypedValuesDisplayMode();
   }
}
