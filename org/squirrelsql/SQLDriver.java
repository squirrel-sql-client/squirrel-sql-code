package org.squirrelsql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLDriver implements Comparable<SQLDriver>
{
   private String _id = UUID.randomUUID().toString();

   private String _name;

   private List<String> _jarFileNamesList = new ArrayList<String>();

   private String _driverClassName;

   private String _url;

   private boolean _loaded;

   private String _websiteUrl;

   public SQLDriver(String id, String name, String driverClassName, String url, String websiteUrl)
   {
      _id = id;
      _name = name;
      _driverClassName = driverClassName;
      _url = url;
      _websiteUrl = websiteUrl;
   }

   public String getId()
   {
      return _id;
   }

   public void setId(String id)
   {
      _id = id;
   }

   public String getName()
   {
      return _name;
   }

   public void setName(String name)
   {
      _name = name;
   }

   public List<String> getJarFileNamesList()
   {
      return _jarFileNamesList;
   }

   public void setJarFileNamesList(List<String> jarFileNamesList)
   {
      _jarFileNamesList = jarFileNamesList;
   }

   public String getDriverClassName()
   {
      return _driverClassName;
   }

   public void setDriverClassName(String driverClassName)
   {
      _driverClassName = driverClassName;
   }

   public String getUrl()
   {
      return _url;
   }

   public void setUrl(String url)
   {
      _url = url;
   }

   public boolean isLoaded()
   {
      return _loaded;
   }

   public void setLoaded(boolean loaded)
   {
      _loaded = loaded;
   }

   public String getWebsiteUrl()
   {
      return _websiteUrl;
   }

   public void setWebsiteUrl(String websiteUrl)
   {
      _websiteUrl = websiteUrl;
   }

   @Override
   public boolean equals(Object obj)
   {
      if(false == obj instanceof SQLDriver)
      {
         return false;
      }

      return ((SQLDriver)obj)._id.equals(_id);
   }

   @Override
   public int hashCode()
   {
      return _id.hashCode();
   }

   @Override
   public int compareTo(SQLDriver other)
   {
      return _name.compareTo(other._name);
   }
}
