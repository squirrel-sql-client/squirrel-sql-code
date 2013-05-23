package org.squirrelsql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SquirrelDriver implements Comparable<SquirrelDriver>
{
   private String _id = UUID.randomUUID().toString();

   private String _name;

   private String _jarFileName = null;

   private List<String> _jarFileNamesList = new ArrayList<String>();

   private String _driverClassName;

   private String _url;

   private boolean _loaded;

   private String _websiteUrl;

   public SquirrelDriver(String id, String name, String driverClassName, String url, String websiteUrl)
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

   public String getJarFileName()
   {
      return _jarFileName;
   }

   public void setJarFileName(String jarFileName)
   {
      _jarFileName = jarFileName;
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
      if(false == obj instanceof SquirrelDriver)
      {
         return false;
      }

      return ((SquirrelDriver)obj)._id.equals(_id);
   }

   @Override
   public int hashCode()
   {
      return _id.hashCode();
   }

   @Override
   public int compareTo(SquirrelDriver other)
   {
      return _name.compareTo(other._name);
   }
}
