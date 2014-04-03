package org.squirrelsql.drivers;

import java.util.Enumeration;
import java.util.NoSuchElementException;
/**
 * This represents an enumeration that is over an empty container.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class EmptyEnumeration<E> implements Enumeration<E>
{
   /**
    * Returns <CODE>false</CODE> as container is empty.
    */
   public boolean hasMoreElements()
   {
      return false;
   }

   /**
    * Throws <CODE>NoSuchElementException</CODE> as container is empty.
    */
   public E nextElement()
   {
      throw new NoSuchElementException();
   }
}
