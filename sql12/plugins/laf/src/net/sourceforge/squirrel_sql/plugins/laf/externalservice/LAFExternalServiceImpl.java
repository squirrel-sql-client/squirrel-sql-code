package net.sourceforge.squirrel_sql.plugins.laf.externalservice;

import net.sourceforge.squirrel_sql.plugins.laf.LAFPlugin;

public class LAFExternalServiceImpl implements LAFExternalService
{
   private LAFPlugin _lafPlugin;

   public LAFExternalServiceImpl(LAFPlugin lafPlugin)
   {
      _lafPlugin = lafPlugin;
   }

   @Override
   public void applyMetalOcean()
   {
      _lafPlugin.applyMetalOcean();
   }

   @Override
   public void applyMetalCharCoal()
   {
      _lafPlugin.applyMetalCharCoal();
   }
}
