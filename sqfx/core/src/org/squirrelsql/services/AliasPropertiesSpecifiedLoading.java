package org.squirrelsql.services;

import org.squirrelsql.aliases.AliasPropertiesSchema;
import org.squirrelsql.aliases.SchemaLoadOptions;
import org.squirrelsql.table.TableLoaderRowObjectAccess;

public class AliasPropertiesSpecifiedLoading
{
   private AliasPropertiesSchema _aliasPropertiesSchema;
   private SchemaLoadOptions _tableOpt;
   private SchemaLoadOptions _viewOpt;
   private SchemaLoadOptions _procOpt;
   private SchemaLoadOptions _otherTableOpt;

   public AliasPropertiesSpecifiedLoading(AliasPropertiesSchema aliasPropertiesSchema, SchemaLoadOptions tableOpt, SchemaLoadOptions viewOpt, SchemaLoadOptions procOpt, SchemaLoadOptions otherTableOpt)
   {
      _aliasPropertiesSchema = aliasPropertiesSchema;
      _tableOpt = tableOpt;
      _viewOpt = viewOpt;
      _procOpt = procOpt;
      _otherTableOpt = otherTableOpt;
   }

   public AliasPropertiesSpecifiedLoading() // For serialization only
   {
   }

   public AliasPropertiesSpecifiedLoading(AliasPropertiesSchema aliasPropertiesSchema)
   {
      this(aliasPropertiesSchema, SchemaLoadOptions.LOAD_BUT_DONT_CACHE, SchemaLoadOptions.LOAD_BUT_DONT_CACHE, SchemaLoadOptions.LOAD_BUT_DONT_CACHE, SchemaLoadOptions.DONT_LOAD);
   }


   public AliasPropertiesSchema getAliasPropertiesSchema()
   {
      return _aliasPropertiesSchema;
   }

   public void setAliasPropertiesSchema(AliasPropertiesSchema aliasPropertiesSchema)
   {
      _aliasPropertiesSchema = aliasPropertiesSchema;
   }

   public SchemaLoadOptions getTableOpt()
   {
      return _tableOpt;
   }

   public void setTableOpt(SchemaLoadOptions tableOpt)
   {
      _tableOpt = tableOpt;
   }

   public SchemaLoadOptions getViewOpt()
   {
      return _viewOpt;
   }

   public void setViewOpt(SchemaLoadOptions viewOpt)
   {
      _viewOpt = viewOpt;
   }

   public SchemaLoadOptions getProcOpt()
   {
      return _procOpt;
   }

   public void setProcedureOpt(SchemaLoadOptions procOpt)
   {
      _procOpt = procOpt;
   }

   public SchemaLoadOptions getOtherTableOpt()
   {
      return _otherTableOpt;
   }

   public void setOtherTableOpt(SchemaLoadOptions otherTableOpt)
   {
      _otherTableOpt = otherTableOpt;
   }

   public static class TableLoaderAccess implements TableLoaderRowObjectAccess<AliasPropertiesSpecifiedLoading>
   {

      @Override
      public Object getColumn(AliasPropertiesSpecifiedLoading o, int colIx)
      {
         if(0 == colIx)
         {
            return o._aliasPropertiesSchema;
         }
         else if(1 == colIx)
         {
            return o._tableOpt;
         }
         else if(2 == colIx)
         {
            return o._viewOpt;
         }
         else if(3 == colIx)
         {
            return o._procOpt;
         }
         else if(4 == colIx)
         {
            return o._otherTableOpt;
         }
         else
         {
            throw new IllegalArgumentException("Unknown index " + colIx);
         }
      }

      @Override
      public void setColumn(AliasPropertiesSpecifiedLoading o, int colIx, Object cellValue)
      {
         if(0 == colIx)
         {
            o._aliasPropertiesSchema = (AliasPropertiesSchema) cellValue;
         }
         else if(1 == colIx)
         {
            o._tableOpt = (SchemaLoadOptions) cellValue;
         }
         else if(2 == colIx)
         {
            o._viewOpt = (SchemaLoadOptions) cellValue;
         }
         else if(3 == colIx)
         {
            o._procOpt = (SchemaLoadOptions) cellValue;
         }
         else if(4 == colIx)
         {
            o._otherTableOpt = (SchemaLoadOptions) cellValue;
         }
         else
         {
            throw new IllegalArgumentException("Unknown index " + colIx);
         }
      }
   }

}
