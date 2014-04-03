package org.squirrelsql.drivers;

import java.util.Enumeration;
import java.util.Iterator;
/**
 * An <TT>EnumerationIterator</TT> object will allow you to treat
 * an <TT>Enumeration</TT> as an <TT>Iterator</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class EnumerationIterator<E> implements Iterator<E>
{
   /** <TT>Enumeration</TT> that this <TT>Iterator</TT> is built over. */
   private Enumeration<E> _en;

   /**
    * Ctor.
    *
    * @param	en	<TT>Enumeration</TT> that <TT>Iterator</TT> will be built
    *				over. If <TT>null</TT> pretends it was an empty
    *				<TT>Enumeration</TT> passed.
    */
   public EnumerationIterator(Enumeration<E> en)
   {
      super();
      _en = en != null ? en : new EmptyEnumeration<E>();
   }

   /**
    * Returns <TT>true</TT> if the iteration has more elements.
    *
    * @return	<TT>true</TT> if the <TT>Iterator</TT> has more elements.
    */
   public boolean hasNext()
   {
      return _en.hasMoreElements();
   }

   /**
    * Returns the next element in the interation.
    *
    * @return	the next element in the iteration.
    *
    * @throws	<TT>NoSuchElementException</TT>
    *			iteration has no more elements.
    */
   public E next()
   {
      return _en.nextElement();
   }

   /**
    * Unsupported operation. <TT>Enumeration</TT> objects don't
    * support <TT>remove</TT>.
    *
    * @throws	<TT>UnsupportedOperationException</TT>
    *			This is an unsupported operation.
    */
   public void remove()
   {
      throw new UnsupportedOperationException("remove()");
   }
}
