package net.sourceforge.squirrel_sql.plugins.oracle.tab;
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

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourcePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;

/**
 * This class is only responsible for formatting source statements for Oracle
 * views.
 *
 * @author manningr
 */
public abstract class OracleSourceTab extends BaseSourceTab
{

   public static final int VIEW_TYPE = 0;
   public static final int STORED_PROC_TYPE = 1;
   public static final int TRIGGER_TYPE = 2;
   public static final int TABLE_TYPE = 3;

   private int _sourceType;

   public OracleSourceTab(String hint)
   {
      this(hint, VIEW_TYPE);
   }

   public OracleSourceTab(String hint, int sourceType)
   {
      super(hint);
      _sourceType = sourceType;
   }

   /**
    * @see net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab#createSourcePanel()
    */
   @Override
   protected BaseSourcePanel createSourcePanel()
   {
      // () -> getDatabaseObjectInfo() because DatabaseObjectInfo is initialized too late.
      return new OracleSourcePanel(getSession(), _sourceType, () -> getDatabaseObjectInfo());
   }

   public int getSourceType()
   {
      return _sourceType;
   }
}
