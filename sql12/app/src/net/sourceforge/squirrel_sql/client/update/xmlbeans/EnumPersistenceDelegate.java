/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.beans.BeanInfo;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.IntrospectionException;
import java.beans.Introspector;

@SuppressWarnings("unchecked")
public class EnumPersistenceDelegate extends DefaultPersistenceDelegate {

   private static EnumPersistenceDelegate INSTANCE = new EnumPersistenceDelegate();

   public static void installFor(Enum<?>[] values) {
     Class<? extends Enum> declaringClass = values[0].getDeclaringClass();
     installFor(declaringClass);

     for (Enum<?> e : values)
       if (e.getClass() != declaringClass)
         installFor(e.getClass());
   }

   public static void installFor(Class<? extends Enum> enumClass) {
     try {
       BeanInfo info = Introspector.getBeanInfo( enumClass );
       info.getBeanDescriptor().setValue( "persistenceDelegate", INSTANCE );
     } catch( IntrospectionException exception ) {
       throw new RuntimeException("Unable to persist enumerated type "+enumClass, exception);
     }
   }

   protected Expression instantiate(Object oldInstance, Encoder out) {
     Enum e = (Enum)oldInstance;
     return new Expression(Enum.class,
                           "valueOf",
                           new Object[] { e.getDeclaringClass(),
                                          e.name() });
   }
}
