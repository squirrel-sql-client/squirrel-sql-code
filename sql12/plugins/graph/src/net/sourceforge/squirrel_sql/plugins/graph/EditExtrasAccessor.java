package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.action.EditExtrasExternalServiceImpl;

import java.awt.*;

public class EditExtrasAccessor
{
   public static String getDateEscape(Window parentForDialog)
   {
      EditExtrasExternalServiceImpl si = new EditExtrasExternalServiceImpl();
      return si.getDateEscape(parentForDialog);
   }
}
