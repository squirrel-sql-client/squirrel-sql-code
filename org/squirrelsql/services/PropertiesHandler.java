package org.squirrelsql.services;

import javafx.application.Application;

public class PropertiesHandler
{
   public PropertiesHandler(Application.Parameters parameters)
   {
      //To change body of created methods use File | Settings | File Templates.
   }

   public String getProperty(SquirrelProperty squirrelProperty)
   {
      return squirrelProperty.getDefaultValue();
   }
}
