package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

public class HibernateConfiguration
{
   private String _provider;
   private String _name;
   private String[] _classpathEntries = new String[0];

   /** the root directory to recurse to find *.cfg.xml files */
   private String _mappingFileRootDir = null;

   public String getProvider()
   {
      return _provider;
   }

   public void setProvider(String provider)
   {
      this._provider = provider;
   }

   public String getName()
   {
      return _name;
   }

   public void setName(String name)
   {
      this._name = name;
   }

   public String[] getClassPathEntries()
   {
      return _classpathEntries;
   }

   public void setClassPathEntries(String[] classPathEntries)
   {
      _classpathEntries = classPathEntries;
   }


   public String toString()
   {
      return _name;
   }

   /**
    * @return the _mappingFileRootDir
    */
   public String getMappingFileRootDir() {
       return _mappingFileRootDir;
   }

   /**
    * @param fileRootDir the _mappingFileRootDir to set
    */
   public void setMappingFileRootDir(String fileRootDir) {
       _mappingFileRootDir = fileRootDir;
   }
}
