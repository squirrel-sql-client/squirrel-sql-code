package net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale;

/**
 * This is a marker class for comparators:
 * An object of this class represents a value itself instead of its index.
 */
public class NoIx
{
   private Object _o;

   public NoIx(Object o)
   {
      _o = o;
   }

   Object get()
   {
      return _o;
   }
}
