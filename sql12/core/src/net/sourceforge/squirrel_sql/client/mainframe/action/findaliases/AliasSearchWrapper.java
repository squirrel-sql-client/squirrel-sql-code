package net.sourceforge.squirrel_sql.client.mainframe.action.findaliases;

import net.sourceforge.squirrel_sql.client.gui.db.AliasFolder;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class AliasSearchWrapper
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasSearchWrapper.class);

   private ISQLAlias _alias;
   private AliasFolder _aliasFolder;

   public AliasSearchWrapper(ISQLAlias alias)
   {
      _alias = alias;
   }

   public AliasSearchWrapper(AliasFolder aliasFolder)
   {
      _aliasFolder = aliasFolder;
   }

   public ISQLAlias getAlias()
   {
      return _alias;
   }

   public AliasFolder getAliasFolder()
   {
      return _aliasFolder;
   }

   public String getName()
   {
      if(null != _alias)
      {
         return _alias.getName();
      }
      else
      {
         return _aliasFolder.getFolderName();
      }
   }

   public static List<AliasSearchWrapper> wrapAliases(List<? extends ISQLAlias> aliasList)
   {
      return aliasList.stream().map( a -> new AliasSearchWrapper(a)).collect(Collectors.toList());
   }

   public static List<AliasSearchWrapper> wrapAliasFolders(List<AliasFolder> aliasFolders)
   {
      return aliasFolders.stream().map( f -> new AliasSearchWrapper(f)).collect(Collectors.toList());
   }

   public String getSearchListDisplayString()
   {
      if (null != _alias)
      {
         return _alias.getName() + "\n  URL: " + _alias.getUrl() + "\n  User: " + _alias.getUserName() + "\n";
      }
      else
      {
         return s_stringMgr.getString("AliasSearchWrapper.aliasFolder",_aliasFolder.getFolderName());
      }
   }

   public int getColorRGB()
   {
      if (null != _alias)
      {
         final SQLAlias sqlAlias = (SQLAlias) _alias;
         if (sqlAlias.getColorProperties().isOverrideAliasBackgroundColor())
         {
            return sqlAlias.getColorProperties().getAliasBackgroundColorRgbValue();
         }

         return AliasFolder.NO_COLOR_RGB;
      }
      else
      {
         return _aliasFolder.getColorRGB();
      }
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      AliasSearchWrapper other = (AliasSearchWrapper) o;

      if(other._alias != null && _alias != null)
      {
         return other._alias.equals(_alias);
      }
      else if(other._aliasFolder != null && _aliasFolder != null)
      {
         return other._aliasFolder.equals(_aliasFolder);
      }

      return false;

   }

   @Override
   public int hashCode()
   {
      if(null != _alias)
      {
         return _alias.hashCode();
      }
      else
      {
         return _aliasFolder.hashCode();
      }

//      int result = _alias != null ? _alias.hashCode() : 0;
//      result = 31 * result + (_aliasFolder != null ? _aliasFolder.hashCode() : 0);
//      return result;
   }
}
