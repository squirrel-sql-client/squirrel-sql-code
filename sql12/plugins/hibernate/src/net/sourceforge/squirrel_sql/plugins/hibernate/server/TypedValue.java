package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;
import java.util.Objects;

public class TypedValue implements Serializable
{
   public static final String HIBERNATE_UNINITIALIZED = "<uninitialized>";

   public static final String UNKNOWN_FIELD_NAME = null;
   public static final String UNKNOWN_TYPE_NAME = null;
   public static final String UNKNOWN_FIELD_VALUE = null;

   private final String valueAsString;
   private final boolean valuesIsNull;
   private final String fieldName;
   private final String fieldTypeName;
   private TypedValuesDisplaySwitch typedValuesDisplaySwitch;

   public TypedValue(String valueAsString, boolean valuesIsNull, String fieldName, String fieldTypeName)
   {
      this.valueAsString = valueAsString;
      this.valuesIsNull = valuesIsNull;
      this.fieldName = fieldName;
      this.fieldTypeName = fieldTypeName;
   }

   public String getValueAsString()
   {
      return valueAsString;
   }

   public boolean isValuesIsNull()
   {
      return valuesIsNull;
   }

   public String getFieldName()
   {
      return fieldName;
   }

   public String getFieldTypeName()
   {
      return fieldTypeName;
   }

   public String asString()
   {
      switch( getTypedValuesDisplayMode() )
      {
         case JSON_MODE:
            return asJsonString();
      }

      return asDefaultString();
   }

   private String asJsonString()
   {

      if( false == Objects.equals(fieldName,  UNKNOWN_TYPE_NAME) )
      {
         return "\"" + fieldName + "\" : \""  + (Objects.equals(valueAsString,  UNKNOWN_FIELD_NAME) ? "<unknown>" : valueAsString) + "\"";
      }
      else
      {
         return "\"<unknownField>\" : \""  + (Objects.equals(valueAsString,  UNKNOWN_FIELD_NAME) ? "<unknown>" : valueAsString) + "\"";
      }
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

   public void setTypedValuesDisplaySwitch(TypedValuesDisplaySwitch typedValuesDisplaySwitch)
   {
      this.typedValuesDisplaySwitch = typedValuesDisplaySwitch;
   }

   public TypedValuesDisplayMode getTypedValuesDisplayMode()
   {
      if(null == typedValuesDisplaySwitch)
      {
         return TypedValuesDisplayMode.DEFAULT_MODE;
      }

      return typedValuesDisplaySwitch.getTypedValuesDisplayMode();
   }
}
