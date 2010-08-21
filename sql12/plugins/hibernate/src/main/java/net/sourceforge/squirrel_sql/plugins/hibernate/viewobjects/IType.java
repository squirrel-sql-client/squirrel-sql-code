package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import java.util.ArrayList;

public interface IType
{
   ArrayList<? extends IType> getKidTypes();

   /**
    *
    * @return null if this Type does not have results
    */
   ArrayList<? extends IResult> getResults();

}
