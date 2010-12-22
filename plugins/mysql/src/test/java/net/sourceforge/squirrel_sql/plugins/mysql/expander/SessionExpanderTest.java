/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.mysql.expander;


import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.AbstractINodeExpanderTest;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlResources;
import net.sourceforge.squirrel_sql.plugins.mysql.ObjectTypes;
import org.junit.Before;

import javax.swing.*;

public class SessionExpanderTest extends AbstractINodeExpanderTest
{


	@Before
	public void setUp() throws Exception
	{
      classUnderTest = new SessionExpander(new ObjectTypesMock());
	}

   private static class ObjectTypesMock extends ObjectTypes
   {
      private ObjectTypesMock()
      {
         super(new MysqlResourcesMock());
      }

      @Override
      public DatabaseObjectType getUserParent()
      {
         return DatabaseObjectType.createNewDatabaseObjectType("USERS", new ImageIcon());
      }

   }

   private static class MysqlResourcesMock extends MysqlResources
   {
      MysqlResourcesMock()
      {
         super(MysqlPlugin.class.getName(), new MysqlPlugin());
      }

      @Override
      public ImageIcon getIcon(String keyName)
      {
         return new ImageIcon();
      }
   }

}
