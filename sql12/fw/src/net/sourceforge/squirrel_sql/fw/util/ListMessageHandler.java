package net.sourceforge.squirrel_sql.fw.util;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.util.List;
import java.util.Vector;

/**
 * This message handler stores msgs in 2 <TT>java.util.List</TT> objects. One
 * for exceptions and the other for strings.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ListMessageHandler implements IMessageHandler
{
   ///////////////////////////////////////////////////////////
   // We use Vectors because a MessageHandler might be called from different threads.


   /** Stores msgs. */
   private List<String> _msgs = new Vector<String>();

   /** Stores exceptions. */
   private List<Throwable> _throwables = new Vector<Throwable>();

   /** Stores error msgs. */
   private List<String> _errMsgs = new Vector<String>();

   /** Stores exceptions. */
   private List<Throwable> _errThrowables = new Vector<Throwable>();

   private List<String> _warningMsgs = new Vector<String>();

   //
   ///////////////////////////////////////////////////////////////////////////////////

   /**
    * Ctor.
    */
   public ListMessageHandler()
   {
      super();
   }

   /**
    * Store this exception.
    *
    * @param	th	Exception to be stored.
    */
   public void showMessage(Throwable th, ExceptionFormatter formatter)
   {
      _throwables.add(th);
   }

   /**
    * Store this msg.
    *
    * @param	msg	Message to be stored.
    */
   public void showMessage(String msg)
   {
      _msgs.add(msg);
   }

   /**
    * Store this msg.
    *
    * @param	th		Exception.
    */
   public void showErrorMessage(Throwable th, ExceptionFormatter formatter)
   {
      _errThrowables.add(th);
   }

   /**
    * Store this exception.
 * @param	th		Exception.
    */
   public void showErrorMessage(String msg)
   {
      _errMsgs.add(msg);
   }

   public void showWarningMessage(String msg)
   {
      _warningMsgs.add(msg);
   }

   /**
    * Return array of stored exceptions.
    *
    * @return	array of stored exceptions.
    */
   public Throwable[] getExceptions()
   {
      return _throwables.toArray(new Throwable[_throwables.size()]);
   }

   /**
    * Return array of stored exceptionsfrom <TT>showErrorMessage(Throwable)</TT>..
    *
    * @return	array of stored exceptions.
    */
   public Throwable[] getErrorExceptions()
   {
      return _errThrowables.toArray(new Throwable[_errThrowables.size()]);
   }

   /**
    * Return array of stored messages.
    *
    * @return	array of stored messages.
    */
   public String[] getMessages()
   {
      return _msgs.toArray(new String[_msgs.size()]);
   }

   /**
    * Return array of stored messages from <TT>showErrorMessage(String)</TT>.
    *
    * @return	array of stored messages.
    */
   public String[] getErrorMessages()
   {
      return _errMsgs.toArray(new String[_errMsgs.size()]);
   }

   public String[] getWarningMessages()
   {
      return _warningMsgs.toArray(new String[_warningMsgs.size()]);
   }   
}
